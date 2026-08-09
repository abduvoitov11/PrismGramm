package org.telegram.messenger;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoInfo;
/**
 * Krypton Media Downloader — TikTok, YouTube va Instagram'dan
 * video/rasm/audio yuklab olish uchun yordamchi klass.
 *
 * Ishlash tartibi:
 * 1. detectPlatform() — havoladan platformani aniqlaydi
 * 2. extractMedia() — cobalt.tools API orqali to'g'ridan-to'g'ri yuklab olish havolasini oladi
 * 3. downloadFile() — faylni progress bilan yuklab oladi
 * 4. saveToGallery() — faylni qurilma galereyasiga saqlaydi
 */
public class KryptonMediaExtractor {

    private static final String COBALT_API = "https://api.cobalt.tools/";
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public enum Platform { TIKTOK, YOUTUBE, INSTAGRAM, UNKNOWN }

    public static class MediaResult {
        public String directUrl;
        public String filename;
        public boolean isVideo;
        public boolean isAudio;
    }

    public interface ExtractionCallback {
        void onSuccess(MediaResult result);
        void onError(String error);
    }

    public interface DownloadProgressCallback {
        void onProgress(int percent, long downloadedBytes, long totalBytes);
        void onComplete(File file, String mimeType);
        void onError(String error);
    }

    /** Havoladan platformani aniqlaydi. */
    public static Platform detectPlatform(String url) {
        if (url == null || url.isEmpty()) return Platform.UNKNOWN;
        String lower = url.toLowerCase();
        if (lower.contains("tiktok.com") || lower.contains("vm.tiktok")) return Platform.TIKTOK;
        if (lower.contains("youtube.com") || lower.contains("youtu.be") || lower.contains("music.youtube")) return Platform.YOUTUBE;
        if (lower.contains("instagram.com") || lower.contains("instagr.am")) return Platform.INSTAGRAM;
        return Platform.UNKNOWN;
    }

    /** cobalt.tools va boshqa API'lar orqali media havolasini chiqaradi. */
    public static void extractMedia(String url, boolean audioOnly, ExtractionCallback callback) {
        executor.execute(() -> {
            Platform platform = detectPlatform(url);
            
            // ─── TIKTOK UCHUN TIKWM API ───
            if (platform == Platform.TIKTOK) {
                HttpURLConnection conn = null;
                try {
                    URL apiUrl = new URL("https://www.tikwm.com/api/");
                    conn = (HttpURLConnection) apiUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(30000);
                    conn.setDoOutput(true);

                    String postData = "url=" + Uri.encode(url) + "&count=12&cursor=0&web=1&hd=1";
                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes("UTF-8"));
                    os.close();

                    int responseCode = conn.getResponseCode();
                    InputStream is = responseCode >= 200 && responseCode < 300
                            ? conn.getInputStream() : conn.getErrorStream();

                    StringBuilder response = new StringBuilder();
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        response.append(new String(buffer, 0, read, "UTF-8"));
                    }
                    is.close();

                    JSONObject json = new JSONObject(response.toString());
                    if (json.optInt("code", -1) == 0) {
                        JSONObject data = json.optJSONObject("data");
                        if (data != null) {
                            MediaResult result = new MediaResult();
                            
                            if (audioOnly) {
                                result.directUrl = data.optString("music");
                                result.filename = "tiktok_music.mp3";
                                result.isAudio = true;
                                result.isVideo = false;
                            } else {
                                // Rasmlar bo'lsa (slaydshou)
                                JSONArray images = data.optJSONArray("images");
                                if (images != null && images.length() > 0) {
                                    result.directUrl = images.optString(0);
                                    result.filename = "tiktok_image.jpg";
                                    result.isAudio = false;
                                    result.isVideo = false;
                                } else {
                                    // Video
                                    result.directUrl = data.optString("play");
                                    // Sifatliroq video uchun hdplay ni tekshiramiz
                                    String hdplay = data.optString("hdplay");
                                    if (hdplay != null && !hdplay.isEmpty()) {
                                        result.directUrl = hdplay;
                                    }
                                    result.filename = "tiktok_video.mp4";
                                    result.isAudio = false;
                                    result.isVideo = true;
                                }
                            }
                            
                            if (result.directUrl != null && !result.directUrl.isEmpty()) {
                                // URL agar domen bo'lmasa, tikwm manzilini qo'shamiz
                                if (result.directUrl.startsWith("/")) {
                                    result.directUrl = "https://www.tikwm.com" + result.directUrl;
                                }
                                AndroidUtilities.runOnUIThread(() -> callback.onSuccess(result));
                                return;
                            }
                        }
                    }
                    
                    AndroidUtilities.runOnUIThread(() -> callback.onError("TikTok dan yuklab bo'lmadi. Havolani tekshiring."));
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    AndroidUtilities.runOnUIThread(() -> callback.onError("Tarmoq xatoligi: " + e.getMessage()));
                    return;
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }

            // ─── YOUTUBE VA BOSHQA SAYTLAR UCHUN YOUTUBEDL-ANDROID ───
            try {
                try {
                    YoutubeDL.getInstance().init(ApplicationLoader.applicationContext);
                    // Dastur ishga tushganda yoki yangilanish talab qilinganda yt-dlp ni avtomatik eng so'nggi versiyaga ko'taradi.
                    YoutubeDL.getInstance().updateYoutubeDL(ApplicationLoader.applicationContext, YoutubeDL.UpdateChannel.STABLE.INSTANCE);
                } catch (Exception initEx) {
                    FileLog.e(initEx);
                    AndroidUtilities.runOnUIThread(() -> callback.onError("Kutubxona yuklanmadi yoki yangilanmadi: " + initEx.getMessage()));
                    return;
                }
                
                YoutubeDLRequest request = new YoutubeDLRequest(url);
                if (audioOnly) {
                    request.addOption("-f", "bestaudio");
                } else {
                    request.addOption("-f", "best[ext=mp4]/best");
                }
                
                VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(request);
                String directUrl = streamInfo.getUrl();
                
                if (directUrl != null && !directUrl.isEmpty()) {
                    MediaResult result = new MediaResult();
                    result.directUrl = directUrl;
                    result.filename = streamInfo.getTitle() != null ? streamInfo.getTitle().replaceAll("[^a-zA-Z0-9_ -]", "") : "krypton_media";
                    result.isVideo = !audioOnly;
                    result.isAudio = audioOnly;
                    AndroidUtilities.runOnUIThread(() -> callback.onSuccess(result));
                } else {
                    AndroidUtilities.runOnUIThread(() -> callback.onError("Videoni topib bo'lmadi. (YoutubeDL)"));
                }
            } catch (Exception e) {
                FileLog.e(e);
                String msg = e.getMessage();
                if (msg != null && (msg.toLowerCase().contains("login") || msg.toLowerCase().contains("cookie"))) {
                    AndroidUtilities.runOnUIThread(() -> callback.onError("Bu post yopiq profilga tegishli yoki login (cookie) talab etiladi."));
                } else {
                    AndroidUtilities.runOnUIThread(() -> callback.onError("Xatolik: " + (msg != null ? msg : "Noma'lum yt-dlp xatosi")));
                }
            }
        });
    }

    /** Faylni to'g'ridan-to'g'ri URL dan yuklab oladi, progress bilan. */
    public static void downloadFile(String downloadUrl, File destination, DownloadProgressCallback callback) {
        executor.execute(() -> {
            HttpURLConnection conn = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(downloadUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36");
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    AndroidUtilities.runOnUIThread(() -> callback.onError("Server xatoligi: " + responseCode));
                    return;
                }

                long totalBytes = conn.getContentLengthLong();
                if (totalBytes <= 0) totalBytes = conn.getContentLength();

                is = conn.getInputStream();
                fos = new FileOutputStream(destination);

                byte[] buffer = new byte[8192];
                long downloaded = 0;
                int read;
                int lastPercent = -1;
                final long total = totalBytes;

                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                    downloaded += read;
                    if (total > 0) {
                        int percent = (int) (downloaded * 100 / total);
                        if (percent != lastPercent) {
                            lastPercent = percent;
                            final int p = percent;
                            final long d = downloaded;
                            AndroidUtilities.runOnUIThread(() -> callback.onProgress(p, d, total));
                        }
                    }
                }

                fos.flush();

                // MIME turini aniqlash
                String contentType = conn.getContentType();
                String mimeType = "video/mp4";
                if (contentType != null) {
                    String ct = contentType.split(";")[0].trim().toLowerCase();
                    if (ct.startsWith("image/") || ct.startsWith("audio/") || ct.startsWith("video/")) {
                        mimeType = ct;
                    }
                }
                // Fayl nomidan ham aniqlash
                String nameLower = destination.getName().toLowerCase();
                if (nameLower.endsWith(".mp3") || nameLower.endsWith(".m4a") || nameLower.endsWith(".ogg")) {
                    mimeType = "audio/mpeg";
                } else if (nameLower.endsWith(".jpg") || nameLower.endsWith(".jpeg")) {
                    mimeType = "image/jpeg";
                } else if (nameLower.endsWith(".png")) {
                    mimeType = "image/png";
                } else if (nameLower.endsWith(".webp")) {
                    mimeType = "image/webp";
                }

                final String mime = mimeType;
                AndroidUtilities.runOnUIThread(() -> callback.onComplete(destination, mime));

            } catch (Exception e) {
                FileLog.e(e);
                AndroidUtilities.runOnUIThread(() -> callback.onError("Yuklab olishda xatolik: " + e.getMessage()));
            } finally {
                try { if (fos != null) fos.close(); } catch (Exception ignored) {}
                try { if (is != null) is.close(); } catch (Exception ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
    }

    /** Faylni qurilma galereyasiga (Krypton papkasiga) saqlaydi. */
    public static Uri saveToGallery(Context context, File file, String mimeType, String displayName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

            Uri collection;
            if (mimeType.startsWith("video")) {
                if (Build.VERSION.SDK_INT >= 29) {
                    values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Krypton");
                }
                collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else if (mimeType.startsWith("image")) {
                if (Build.VERSION.SDK_INT >= 29) {
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Krypton");
                }
                collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else if (mimeType.startsWith("audio")) {
                if (Build.VERSION.SDK_INT >= 29) {
                    values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/Krypton");
                }
                collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else {
                if (Build.VERSION.SDK_INT >= 29) {
                    values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Krypton");
                }
                collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }

            Uri uri = context.getContentResolver().insert(collection, values);
            if (uri != null) {
                OutputStream os = context.getContentResolver().openOutputStream(uri);
                FileInputStream fis = new FileInputStream(file);
                byte[] buf = new byte[8192];
                int len;
                while ((len = fis.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.close();
                fis.close();
            }

            // Vaqtincha faylni tozalash
            file.delete();

            return uri;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }
}
