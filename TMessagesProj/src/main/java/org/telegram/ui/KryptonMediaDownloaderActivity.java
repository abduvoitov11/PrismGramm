package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KryptonMediaExtractor;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.io.File;

/**
 * Krypton Media Downloader — TikTok, YouTube va Instagram'dan
 * video, rasm va audio yuklab olish uchun professional, minimalistik ekran.
 */
public class KryptonMediaDownloaderActivity extends BaseFragment {

    private EditTextBoldCursor urlInput;
    private TextView downloadButton;
    private LinearLayout progressContainer;
    private TextView progressText;
    private ProgressBar progressBar;
    private TextView progressPercent;
    private LinearLayout resultContainer;
    private TextView resultTitle;
    private TextView resultInfo;
    private TextView openGalleryButton;
    private TextView platformLabel;

    private boolean isDownloading = false;
    private Uri lastSavedUri;
    private String lastMimeType;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Media Downloader");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) finishFragment();
            }
        });

        FrameLayout rootFrame = new FrameLayout(context);
        rootFrame.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView = rootFrame;

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        rootFrame.addView(scrollView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(24),
                AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        scrollView.addView(container, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ─── Platform Label ───
        platformLabel = new TextView(context);
        platformLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        platformLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        platformLabel.setText("TikTok  •  YouTube  •  Instagram");
        platformLabel.setGravity(Gravity.CENTER);
        container.addView(platformLabel, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 14));

        // ─── URL Input Card ───
        FrameLayout inputCard = new FrameLayout(context);
        GradientDrawable inputBg = new GradientDrawable();
        inputBg.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        inputBg.setCornerRadius(AndroidUtilities.dp(12));
        inputCard.setBackground(inputBg);
        inputCard.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4),
                AndroidUtilities.dp(48), AndroidUtilities.dp(4));
        inputCard.setElevation(AndroidUtilities.dp(1));
        container.addView(inputCard, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        urlInput = new EditTextBoldCursor(context);
        urlInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        urlInput.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        urlInput.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        urlInput.setHint("\uD83D\uDD17  Havolani kiriting...");
        urlInput.setBackground(null);
        urlInput.setSingleLine(true);
        urlInput.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        urlInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        urlInput.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor));
        urlInput.setCursorSize(AndroidUtilities.dp(20));
        urlInput.setCursorWidth(1.5f);
        inputCard.addView(urlInput, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 48, Gravity.CENTER_VERTICAL));

        // Paste tugmasi
        ImageView pasteBtn = new ImageView(context);
        pasteBtn.setImageResource(R.drawable.msg_copy);
        pasteBtn.setColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons));
        pasteBtn.setScaleType(ImageView.ScaleType.CENTER);
        pasteBtn.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8),
                AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        pasteBtn.setOnClickListener(v -> pasteFromClipboard(context));
        inputCard.addView(pasteBtn, LayoutHelper.createFrame(
                40, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL));

        urlInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updatePlatformLabel(s.toString());
            }
        });

        // ─── Download Button ───
        downloadButton = new TextView(context);
        downloadButton.setText("\uD83D\uDCE5  Yuklab olish");
        downloadButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        downloadButton.setTypeface(AndroidUtilities.bold());
        downloadButton.setTextColor(Color.WHITE);
        downloadButton.setGravity(Gravity.CENTER);
        downloadButton.setPadding(0, AndroidUtilities.dp(14), 0, AndroidUtilities.dp(14));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        btnBg.setCornerRadius(AndroidUtilities.dp(12));
        downloadButton.setBackground(btnBg);
        downloadButton.setOnClickListener(v -> startDownload(context));
        container.addView(downloadButton, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 16, 0, 0));

        // ─── Progress Container (yashirin) ───
        progressContainer = createCard(context);
        progressContainer.setVisibility(View.GONE);
        container.addView(progressContainer, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 16, 0, 0));

        progressText = new TextView(context);
        progressText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        progressText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        progressText.setText("Yuklanmoqda...");
        progressContainer.addView(progressText, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressContainer.addView(progressBar, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, 6, 0, 10, 0, 4));

        progressPercent = new TextView(context);
        progressPercent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        progressPercent.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        progressPercent.setText("0%");
        progressPercent.setGravity(Gravity.RIGHT);
        progressContainer.addView(progressPercent, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        // ─── Result Container (yashirin) ───
        resultContainer = createCard(context);
        resultContainer.setVisibility(View.GONE);
        container.addView(resultContainer, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 16, 0, 0));

        resultTitle = new TextView(context);
        resultTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        resultTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        resultTitle.setTypeface(AndroidUtilities.bold());
        resultContainer.addView(resultTitle, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        resultInfo = new TextView(context);
        resultInfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        resultInfo.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        resultContainer.addView(resultInfo, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 4, 0, 0));

        openGalleryButton = new TextView(context);
        openGalleryButton.setText("\uD83D\uDCC2  Galereyada ochish");
        openGalleryButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        openGalleryButton.setTypeface(AndroidUtilities.bold());
        openGalleryButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        openGalleryButton.setGravity(Gravity.CENTER);
        openGalleryButton.setPadding(0, AndroidUtilities.dp(12), 0, AndroidUtilities.dp(4));
        openGalleryButton.setOnClickListener(v -> openLastSaved(context));
        resultContainer.addView(openGalleryButton, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 4, 0, 0));

        // ─── Info Text ───
        TextView infoText = new TextView(context);
        infoText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        infoText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        infoText.setText("Video va rasmlar qurilmangizning galereyasiga\n(Krypton papkasiga) saqlanadi.");
        infoText.setGravity(Gravity.CENTER);
        infoText.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);
        container.addView(infoText, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 24, 0, 0));

        return fragmentView;
    }

    private LinearLayout createCard(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        bg.setCornerRadius(AndroidUtilities.dp(12));
        card.setBackground(bg);
        card.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(14),
                AndroidUtilities.dp(16), AndroidUtilities.dp(14));
        card.setElevation(AndroidUtilities.dp(1));
        return card;
    }

    private void pasteFromClipboard(Context context) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData clip = clipboard.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    CharSequence text = clip.getItemAt(0).getText();
                    if (text != null) {
                        urlInput.setText(text);
                        urlInput.setSelection(text.length());
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void updatePlatformLabel(String url) {
        KryptonMediaExtractor.Platform platform = KryptonMediaExtractor.detectPlatform(url);
        switch (platform) {
            case TIKTOK:
                platformLabel.setText("\uD83D\uDCF1 TikTok aniqlandi");
                platformLabel.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                break;
            case YOUTUBE:
                platformLabel.setText("▶️ YouTube aniqlandi");
                platformLabel.setTextColor(0xFFFF0000);
                break;
            case INSTAGRAM:
                platformLabel.setText("\uD83D\uDCF8 Instagram aniqlandi");
                platformLabel.setTextColor(0xFFE1306C);
                break;
            default:
                platformLabel.setText("TikTok  •  YouTube  •  Instagram");
                platformLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                break;
        }
    }

    private void startDownload(Context context) {
        String url = urlInput.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            AndroidUtilities.shakeView(urlInput);
            return;
        }

        KryptonMediaExtractor.Platform platform = KryptonMediaExtractor.detectPlatform(url);
        if (platform == KryptonMediaExtractor.Platform.UNKNOWN) {
            AndroidUtilities.shakeView(urlInput);
            Toast.makeText(context, "Faqat TikTok, YouTube yoki Instagram havolalari qo‘llab-quvvatlanadi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDownloading) return;
        isDownloading = true;

        // UI yangilash
        downloadButton.setText("⏳  Havola tahlil qilinmoqda...");
        downloadButton.setEnabled(false);
        downloadButton.setAlpha(0.6f);
        progressContainer.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressText.setText("Media havola aniqlanmoqda...");
        progressPercent.setText("0%");
        resultContainer.setVisibility(View.GONE);

        AndroidUtilities.hideKeyboard(urlInput);

        KryptonMediaExtractor.extractMedia(url, false, new KryptonMediaExtractor.ExtractionCallback() {
            @Override
            public void onSuccess(KryptonMediaExtractor.MediaResult result) {
                if (result.directUrl == null || result.directUrl.isEmpty()) {
                    onError("Media havolasi topilmadi");
                    return;
                }

                progressText.setText("Yuklanmoqda: " + (result.filename != null ? result.filename : "media"));

                String ext = result.isAudio ? ".mp3" : ".mp4";
                if (result.filename != null && result.filename.contains(".")) {
                    ext = result.filename.substring(result.filename.lastIndexOf("."));
                }

                String fileName = "Krypton_" + System.currentTimeMillis() + ext;
                File tempFile = new File(context.getCacheDir(), fileName);

                final String displayName = result.filename != null ? result.filename : fileName;

                KryptonMediaExtractor.downloadFile(result.directUrl, tempFile,
                        new KryptonMediaExtractor.DownloadProgressCallback() {
                    @Override
                    public void onProgress(int percent, long downloadedBytes, long totalBytes) {
                        progressBar.setProgress(percent);
                        String dl = AndroidUtilities.formatFileSize(downloadedBytes);
                        String total = totalBytes > 0 ? AndroidUtilities.formatFileSize(totalBytes) : "?";
                        progressPercent.setText(percent + "%  •  " + dl + " / " + total);
                    }

                    @Override
                    public void onComplete(File file, String mimeType) {
                        progressText.setText("Galereyaga saqlanmoqda...");
                        progressBar.setProgress(100);

                        long fileSize = file.length();
                        Uri uri = KryptonMediaExtractor.saveToGallery(context, file, mimeType, fileName);

                        resetDownloadState();
                        progressContainer.setVisibility(View.GONE);

                        if (uri != null) {
                            lastSavedUri = uri;
                            lastMimeType = mimeType;
                            resultContainer.setVisibility(View.VISIBLE);

                            String icon = mimeType.startsWith("video") ? "🎬"
                                    : mimeType.startsWith("audio") ? "🎵" : "🖼️";
                            String type = mimeType.startsWith("video") ? "Video"
                                    : mimeType.startsWith("audio") ? "Audio" : "Rasm";

                            resultTitle.setText(icon + "  " + displayName);
                            resultInfo.setText(type + "  •  " + AndroidUtilities.formatFileSize(fileSize)
                                    + "  •  Galereyaga saqlandi ✓");

                            if (mimeType.startsWith("audio")) {
                                openGalleryButton.setText("🎵  Tinglash");
                            } else if (mimeType.startsWith("image")) {
                                openGalleryButton.setText("🖼️  Ko‘rish");
                            } else {
                                openGalleryButton.setText("📂  Galereyada ochish");
                            }
                        } else {
                            Toast.makeText(context, "Galereyaga saqlashda xatolik", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        resetDownloadState();
                        progressContainer.setVisibility(View.GONE);
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                resetDownloadState();
                progressContainer.setVisibility(View.GONE);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetDownloadState() {
        isDownloading = false;
        downloadButton.setText("📥  Yuklab olish");
        downloadButton.setEnabled(true);
        downloadButton.setAlpha(1.0f);
    }

    private void openLastSaved(Context context) {
        if (lastSavedUri != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(lastSavedUri, lastMimeType != null ? lastMimeType : "*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
                Toast.makeText(context, "Faylni ochishda xatolik", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
