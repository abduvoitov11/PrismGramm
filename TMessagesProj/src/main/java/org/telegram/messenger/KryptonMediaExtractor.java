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

    /** cobalt.tools API orqali media havolasini chiqaradi. */
    public static void extractMedia(String url, boolean audioOnly, ExtractionCallback callback) {
        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL apiUrl = new URL(COBALT_API);
                conn = (HttpURLConnection) apiUrl.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("url", url);
                if (audioOnly) {
                    body.put("downloadMode", "audio");
                } else {
                    body.put("downloadMode", "auto");
                }

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
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
                String status = json.optString("status", "error");

                if ("redirect".equals(status) || "tunnel".equals(status) || "stream".equals(status)) {
                    MediaResult result = new MediaResult();
                    result.directUrl = json.optString("url");
                    result.filename = json.optString("filename", "krypton_media");
                    result.isVideo = !audioOnly;
                    result.isAudio = audioOnly;
                    AndroidUtilities.runOnUIThread(() -> callback.onSuccess(result));
                } else if ("picker".equals(status)) {
                    JSONArray picker = json.optJSONArray("picker");
                    if (picker != null && picker.length() > 0) {
                        JSONObject first = picker.getJSONObject(0);
                        MediaResult result = new MediaResult();
                        result.directUrl = first.optString("url");
                        result.filename = "krypton_media";
                        result.isVideo = "video".equals(first.optString("type", "video"));
                        result.isAudio = false;
                        AndroidUtilities.runOnUIThread(() -> callback.onSuccess(result));
                    } else {
                        AndroidUtilities.runOnUIThread(() -> callback.onError("Media topilmadi"));
                    }
                } else {
                    JSONObject error = json.optJSONObject("error");
                    String errorText = error != null ? error.optString("code", "Noma'lum xatolik") : json.optString("text", "Noma'lum xatolik");
                    AndroidUtilities.runOnUIThread(() -> callback.onError(errorText));
                }

            } catch (Exception e) {
                FileLog.e(e);
                AndroidUtilities.runOnUIThread(() -> callback.onError("Tarmoq xatoligi: " + e.getMessage()));
            } finally {
                if (conn != null) conn.disconnect();
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
