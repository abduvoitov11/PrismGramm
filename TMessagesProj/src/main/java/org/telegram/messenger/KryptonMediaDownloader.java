package org.telegram.messenger;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONObject;
import org.telegram.ui.ActionBar.AlertDialog;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Krypton Media Downloader — Telegram xabarlarida uchraydigan Instagram,
 * TikTok yoki YouTube havolalarini aniqlab, videoni to'g'ridan-to'g'ri
 * qurilmaga yuklab beradi (alohida bot qidirishga hojat qoldirmaydi).
 *
 * Ishlash tamoyili:
 *   1) Xabar matnidan qo'llab-quvvatlanadigan havola aniqlanadi (regex).
 *   2) Ochiq (public) video-ajratish xizmatiga (cobalt.tools) so'rov
 *      yuboriladi — u bizga to'g'ridan-to'g'ri video fayl havolasini
 *      qaytaradi.
 *   3) Android'ning o'z DownloadManager tizimi orqali fayl "Downloads"
 *      papkasiga yuklab olinadi (bildirishnoma bilan, tizim darajasida).
 *
 * ESLATMA: bu — bizga tegishli bo'lmagan, ochiq/bepul uchinchi tomon
 * xizmatiga tayanadi. Xizmat vaqti-vaqti bilan ishlamay qolishi yoki
 * API formatini o'zgartirishi mumkin — bu bizning nazoratimizdan tashqarida.
 */
public class KryptonMediaDownloader {

    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://(?:www\\.|m\\.|vm\\.|vt\\.)?(instagram\\.com|tiktok\\.com|youtube\\.com|youtu\\.be)/[^\\s]+",
        Pattern.CASE_INSENSITIVE
    );

    private static final String EXTRACT_API = "https://api.cobalt.tools/";

    /** Xabar matnida qo'llab-quvvatlanadigan media havolasi bor-yo'qligini tekshiradi. */
    public static String findSupportedUrl(String text) {
        if (TextUtils.isEmpty(text)) return null;
        Matcher m = URL_PATTERN.matcher(text);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    public static boolean isSupportedUrl(String text) {
        return findSupportedUrl(text) != null;
    }

    /** Foydalanuvchiga ko'rinadigan platforma nomi (menyu bandi uchun). */
    public static String platformName(String url) {
        if (url == null) return "Media";
        String lower = url.toLowerCase();
        if (lower.contains("instagram.com")) return "Instagram";
        if (lower.contains("tiktok.com")) return "TikTok";
        if (lower.contains("youtube.com") || lower.contains("youtu.be")) return "YouTube";
        return "Media";
    }

    /**
     * Yuklab olishni boshlaydi: fonda API'ga so'rov yuboradi, natijani
     * DownloadManager orqali qurilmaga yuklaydi. Barcha tarmoq ishi
     * asosiy (UI) oqimidan tashqarida bajariladi.
     */
    public static void download(Context context, String sourceUrl) {
        if (context == null || TextUtils.isEmpty(sourceUrl)) return;

        final AlertDialog progressDialog = new AlertDialog(context, AlertDialog.ALERT_TYPE_SPINNER);
        try {
            progressDialog.setCanCancel(true);
            progressDialog.show();
        } catch (Exception e) {
            FileLog.e(e);
        }

        Utilities.globalQueue.postRunnable(() -> {
            String directUrl = null;
            String errorMessage = null;
            try {
                directUrl = resolveDirectUrl(sourceUrl);
            } catch (Exception e) {
                FileLog.e(e);
                errorMessage = e.getMessage();
            }

            final String finalDirectUrl = directUrl;
            final String finalError = errorMessage;
            AndroidUtilities.runOnUIThread(() -> {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ignore) {
                }

                if (TextUtils.isEmpty(finalDirectUrl)) {
                    Toast.makeText(
                        context,
                        finalError != null ? finalError : "Yuklab bo'lmadi. Xizmat vaqtincha ishlamayotgan bo'lishi mumkin.",
                        Toast.LENGTH_LONG
                    ).show();
                    return;
                }
                enqueueSystemDownload(context, finalDirectUrl, sourceUrl);
            });
        });
    }

    /** cobalt.tools ochiq API'siga murojaat qilib, to'g'ridan-to'g'ri video havolasini oladi. */
    private static String resolveDirectUrl(String sourceUrl) throws Exception {
        URL apiUrl = new URL(EXTRACT_API);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            JSONObject body = new JSONObject();
            body.put("url", sourceUrl);
            body.put("videoQuality", "1080");
            body.put("downloadMode", "auto");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            java.io.InputStream is = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
            String response = readStreamToString(is);

            JSONObject json = new JSONObject(response);
            String status = json.optString("status", "");
            if ("error".equals(status)) {
                JSONObject err = json.optJSONObject("error");
                throw new Exception(err != null ? err.optString("code", "Xatolik yuz berdi") : "Xatolik yuz berdi");
            }
            // cobalt javob formatlari: "tunnel"/"redirect" -> to'g'ridan-to'g'ri "url" maydoni bor
            String url = json.optString("url", null);
            if (TextUtils.isEmpty(url)) {
                throw new Exception("Video havolasi topilmadi");
            }
            return url;
        } finally {
            conn.disconnect();
        }
    }

    private static String readStreamToString(java.io.InputStream is) throws Exception {
        if (is == null) return "";
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int len;
        while ((len = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, len);
        }
        is.close();
        return buffer.toString("UTF-8");
    }

    private static void enqueueSystemDownload(Context context, String directUrl, String sourceUrl) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager == null) {
                Toast.makeText(context, "Yuklab olib bo'lmadi", Toast.LENGTH_SHORT).show();
                return;
            }
            String fileName = "krypton_" + platformName(sourceUrl).toLowerCase() + "_" + System.currentTimeMillis() + ".mp4";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(directUrl));
            request.setTitle(platformName(sourceUrl) + " video");
            request.setDescription("Krypton orqali yuklanmoqda...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            downloadManager.enqueue(request);
            Toast.makeText(context, "Yuklab olish boshlandi", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            FileLog.e(e);
            Toast.makeText(context, "Yuklab olib bo'lmadi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
