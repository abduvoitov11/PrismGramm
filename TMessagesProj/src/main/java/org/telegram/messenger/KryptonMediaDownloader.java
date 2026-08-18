package org.telegram.messenger;

import android.app.DownloadManager;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.ui.ActionBar.AlertDialog;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Krypton Professional Media Downloader v2.0
 * Multi-Engine Architecture for Instagram, TikTok, YouTube/Shorts, Twitter/X, Pinterest, Facebook.
 *
 * Includes:
 *   - Engine 1: Cobalt v10 API (tunnel, redirect, picker, stream)
 *   - Engine 2: TikWM API (TikTok HD No-Watermark Fallback)
 *   - Engine 3: Invidious / Piped API (YouTube & Shorts Fallback)
 *   - Automatic Gallery / MediaStore Registration
 *   - Multi-file (Carousel/Picker) Support
 */
public class KryptonMediaDownloader {

    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://(?:www\\.|m\\.|vm\\.|vt\\.)?(instagram\\.com|tiktok\\.com|youtube\\.com|youtu\\.be|twitter\\.com|x\\.com|pinterest\\.com|pin\\.it|facebook\\.com|fb\\.watch)/[^\\s]+",
        Pattern.CASE_INSENSITIVE
    );

    private static final String COBALT_API = "https://api.cobalt.tools/";

    /** Xabar matnida qo'llab-quvvatlanadigan media havolasini qidiradi. */
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

    /** Platforma nomini qaytaradi (Menyu va bildirishnomalar uchun). */
    public static String platformName(String url) {
        if (url == null) return "Media";
        String lower = url.toLowerCase();
        if (lower.contains("instagram.com")) return "Instagram";
        if (lower.contains("tiktok.com")) return "TikTok";
        if (lower.contains("youtube.com") || lower.contains("youtu.be")) return "YouTube";
        if (lower.contains("twitter.com") || lower.contains("x.com")) return "Twitter/X";
        if (lower.contains("pinterest.com") || lower.contains("pin.it")) return "Pinterest";
        if (lower.contains("facebook.com") || lower.contains("fb.watch")) return "Facebook";
        return "Media";
    }

    public static class DownloadItem {
        public String url;
        public boolean isVideo;

        public DownloadItem(String url, boolean isVideo) {
            this.url = url;
            this.isVideo = isVideo;
        }
    }

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
            List<DownloadItem> items = new ArrayList<>();
            String errorMessage = null;

            try {
                items = resolveMediaPipeline(sourceUrl);
            } catch (Exception e) {
                FileLog.e(e);
                errorMessage = e.getMessage();
            }

            final List<DownloadItem> finalItems = items;
            final String finalError = errorMessage;

            AndroidUtilities.runOnUIThread(() -> {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ignore) {
                }

                if (finalItems == null || finalItems.isEmpty()) {
                    Toast.makeText(
                        context,
                        !TextUtils.isEmpty(finalError) ? finalError : "Videoni yuklab bo'lmadi. Havolani qayta tekshiring.",
                        Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                for (DownloadItem item : finalItems) {
                    enqueueSystemDownload(context, item.url, sourceUrl, item.isVideo);
                }
            });
        });
    }

    /** Multi-Engine Fallback Pipeline: Cobalt -> TikWM -> Invidious */
    private static List<DownloadItem> resolveMediaPipeline(String sourceUrl) throws Exception {
        List<DownloadItem> results = new ArrayList<>();

        // Engine 1: Cobalt v10 API
        try {
            results = resolveViaCobalt(sourceUrl);
            if (results != null && !results.isEmpty()) {
                return results;
            }
        } catch (Exception e) {
            FileLog.e("KryptonEngine: Cobalt failed: " + e.getMessage());
        }

        String lowerUrl = sourceUrl.toLowerCase();

        // Engine 2: TikTok Special Engine (TikWM)
        if (lowerUrl.contains("tiktok.com")) {
            try {
                results = resolveViaTikWM(sourceUrl);
                if (results != null && !results.isEmpty()) {
                    return results;
                }
            } catch (Exception e) {
                FileLog.e("KryptonEngine: TikWM failed: " + e.getMessage());
            }
        }

        // Engine 3: YouTube Special Engine (Invidious API)
        if (lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be")) {
            try {
                results = resolveViaInvidious(sourceUrl);
                if (results != null && !results.isEmpty()) {
                    return results;
                }
            } catch (Exception e) {
                FileLog.e("KryptonEngine: Invidious failed: " + e.getMessage());
            }
        }

        if (results == null || results.isEmpty()) {
            throw new Exception("Media manbasi aniqlanmadi. Xizmat vaqtincha band bo'lishi mumkin.");
        }

        return results;
    }

    /** Engine 1: Cobalt v10 API Driver */
    private static List<DownloadItem> resolveViaCobalt(String sourceUrl) throws Exception {
        List<DownloadItem> list = new ArrayList<>();
        URL url = new URL(COBALT_API);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(12000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");

            JSONObject body = new JSONObject();
            body.put("url", sourceUrl);
            body.put("videoQuality", "1080");
            body.put("downloadMode", "auto");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            String responseStr = readStreamToString(is);

            if (TextUtils.isEmpty(responseStr)) {
                return list;
            }

            JSONObject json = new JSONObject(responseStr);
            String status = json.optString("status", "");

            if ("error".equals(status)) {
                JSONObject err = json.optJSONObject("error");
                String msg = err != null ? err.optString("code", "") : json.optString("text", "");
                throw new Exception(!TextUtils.isEmpty(msg) ? msg : "Cobalt error");
            }

            if ("tunnel".equals(status) || "redirect".equals(status) || "stream".equals(status)) {
                String mediaUrl = json.optString("url", null);
                if (!TextUtils.isEmpty(mediaUrl)) {
                    list.add(new DownloadItem(mediaUrl, true));
                }
            } else if ("picker".equals(status)) {
                JSONArray picker = json.optJSONArray("picker");
                if (picker != null) {
                    for (int i = 0; i < picker.length(); i++) {
                        JSONObject pObj = picker.optJSONObject(i);
                        if (pObj != null) {
                            String pUrl = pObj.optString("url", "");
                            String type = pObj.optString("type", "video");
                            if (!TextUtils.isEmpty(pUrl)) {
                                list.add(new DownloadItem(pUrl, !"photo".equals(type)));
                            }
                        }
                    }
                }
            } else {
                String fallbackUrl = json.optString("url", "");
                if (!TextUtils.isEmpty(fallbackUrl)) {
                    list.add(new DownloadItem(fallbackUrl, true));
                }
            }
        } finally {
            conn.disconnect();
        }
        return list;
    }

    /** Engine 2: TikWM (TikTok HD No-Watermark API) */
    private static List<DownloadItem> resolveViaTikWM(String sourceUrl) throws Exception {
        List<DownloadItem> list = new ArrayList<>();
        URL url = new URL("https://www.tikwm.com/api/?url=" + Uri.encode(sourceUrl) + "&count=12&cursor=0&web=1&hd=1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(12000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android Mobile)");

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                String res = readStreamToString(conn.getInputStream());
                JSONObject json = new JSONObject(res);
                if (json.optInt("code", -1) == 0) {
                    JSONObject data = json.optJSONObject("data");
                    if (data != null) {
                        String play = data.optString("play", "");
                        String hdplay = data.optString("hdplay", "");
                        String target = !TextUtils.isEmpty(hdplay) ? hdplay : play;
                        if (!TextUtils.isEmpty(target)) {
                            if (!target.startsWith("http")) {
                                target = "https://www.tikwm.com" + target;
                            }
                            list.add(new DownloadItem(target, true));
                        } else {
                            JSONArray images = data.optJSONArray("images");
                            if (images != null) {
                                for (int i = 0; i < images.length(); i++) {
                                    String imgUrl = images.optString(i);
                                    if (!TextUtils.isEmpty(imgUrl)) {
                                        list.add(new DownloadItem(imgUrl, false));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            conn.disconnect();
        }
        return list;
    }

    /** Engine 3: Invidious Public Instances (YouTube & Shorts Fallback) */
    private static List<DownloadItem> resolveViaInvidious(String sourceUrl) throws Exception {
        List<DownloadItem> list = new ArrayList<>();
        String videoId = extractYouTubeId(sourceUrl);
        if (TextUtils.isEmpty(videoId)) return list;

        String[] instances = new String[]{
            "https://invidious.privacydev.net",
            "https://inv.tux.pizza",
            "https://invidious.nerdvpn.de"
        };

        for (String inst : instances) {
            try {
                URL url = new URL(inst + "/api/v1/videos/" + videoId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() == 200) {
                    String res = readStreamToString(conn.getInputStream());
                    JSONObject json = new JSONObject(res);
                    JSONArray formats = json.optJSONArray("formatStreams");
                    if (formats != null && formats.length() > 0) {
                        JSONObject bestFormat = formats.getJSONObject(formats.length() - 1);
                        String downloadUrl = bestFormat.optString("url", "");
                        if (!TextUtils.isEmpty(downloadUrl)) {
                            list.add(new DownloadItem(downloadUrl, true));
                            conn.disconnect();
                            break;
                        }
                    }
                }
                conn.disconnect();
            } catch (Exception ignore) {
            }
        }
        return list;
    }

    private static String extractYouTubeId(String url) {
        if (TextUtils.isEmpty(url)) return null;
        Pattern p = Pattern.compile("(?:v=|/shorts/|youtu\\.be/)([a-zA-Z0-9_-]{11})");
        Matcher m = p.matcher(url);
        return m.find() ? m.group(1) : null;
    }

    private static String readStreamToString(InputStream is) throws Exception {
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

    private static void enqueueSystemDownload(Context context, String directUrl, String sourceUrl, boolean isVideo) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager == null) {
                Toast.makeText(context, "Yuklab olib bo'lmadi", Toast.LENGTH_SHORT).show();
                return;
            }

            String ext = isVideo ? ".mp4" : ".jpg";
            String platform = platformName(sourceUrl).toLowerCase().replaceAll("[^a-z0-9]", "");
            String fileName = "prismgramm_" + platform + "_" + System.currentTimeMillis() + ext;

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(directUrl));
            request.setTitle(platformName(sourceUrl) + (isVideo ? " Video" : " Rasm"));
            request.setDescription("Prisma Downloader orqali yuklanmoqda...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);

            long downloadId = downloadManager.enqueue(request);

            // Android Galereyasiga (MediaStore) darhol ko'rinishi uchun scan qilish
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
            MediaScannerConnection.scanFile(context, new String[]{filePath}, new String[]{isVideo ? "video/mp4" : "image/jpeg"}, null);

            Toast.makeText(context, "Yuklab olish boshlandi! Downloads papkasiga saqlanmoqda.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            FileLog.e(e);
            Toast.makeText(context, "Yuklab olib bo'lmadi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

