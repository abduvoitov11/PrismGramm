package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class KryptonMediaDownloaderActivity extends BaseFragment {

    private EditTextBoldCursor urlInput;
    private TextView downloadButton;
    private LinearLayout progressContainer;
    private ProgressBar progressBar;
    private TextView progressPercent;
    private LinearLayout resultContainer;
    private TextView openGalleryButton;

    private boolean isDownloading = false;
    private Uri lastSavedUri;
    private String lastMimeType;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Downloader");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) finishFragment();
            }
        });

        FrameLayout rootFrame = new FrameLayout(context);
        rootFrame.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        fragmentView = rootFrame;

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(AndroidUtilities.dp(32), 0, AndroidUtilities.dp(32), AndroidUtilities.dp(64));
        rootFrame.addView(container, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));

        // URL Input Row
        LinearLayout inputRow = new LinearLayout(context);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);
        inputRow.setGravity(Gravity.CENTER_VERTICAL);
        container.addView(inputRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        urlInput = new EditTextBoldCursor(context);
        urlInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        urlInput.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        urlInput.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        urlInput.setHint("Link");
        urlInput.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        urlInput.setSingleLine(true);
        urlInput.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        urlInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        urlInput.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        urlInput.setCursorSize(AndroidUtilities.dp(20));
        urlInput.setCursorWidth(1.5f);
        inputRow.addView(urlInput, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        ImageView pasteBtn = new ImageView(context);
        pasteBtn.setImageResource(R.drawable.msg_copy);
        pasteBtn.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon));
        pasteBtn.setScaleType(ImageView.ScaleType.CENTER);
        pasteBtn.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1));
        pasteBtn.setOnClickListener(v -> pasteFromClipboard(context));
        inputRow.addView(pasteBtn, LayoutHelper.createLinear(48, 48, 8, 0, 0, 0));

        // Download Button
        downloadButton = new TextView(context);
        downloadButton.setText("Download");
        downloadButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        downloadButton.setTypeface(AndroidUtilities.bold());
        downloadButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        downloadButton.setGravity(Gravity.CENTER);
        downloadButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 0));
        downloadButton.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16));
        downloadButton.setOnClickListener(v -> startDownload(context));
        container.addView(downloadButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 24, 0, 0));

        // Progress
        progressContainer = new LinearLayout(context);
        progressContainer.setOrientation(LinearLayout.VERTICAL);
        progressContainer.setVisibility(View.GONE);
        container.addView(progressContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 16, 0, 0));

        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressContainer.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 4));

        progressPercent = new TextView(context);
        progressPercent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        progressPercent.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        progressPercent.setGravity(Gravity.CENTER);
        progressContainer.addView(progressPercent, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 12, 0, 0));

        // Result
        resultContainer = new LinearLayout(context);
        resultContainer.setOrientation(LinearLayout.VERTICAL);
        resultContainer.setVisibility(View.GONE);
        container.addView(resultContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 8, 0, 0));

        openGalleryButton = new TextView(context);
        openGalleryButton.setText("Open");
        openGalleryButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        openGalleryButton.setTypeface(AndroidUtilities.bold());
        openGalleryButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        openGalleryButton.setGravity(Gravity.CENTER);
        openGalleryButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 0));
        openGalleryButton.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16));
        openGalleryButton.setOnClickListener(v -> openLastSaved(context));
        resultContainer.addView(openGalleryButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        return fragmentView;
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

    private void startDownload(Context context) {
        String url = urlInput.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            AndroidUtilities.shakeView(urlInput);
            return;
        }

        KryptonMediaExtractor.Platform platform = KryptonMediaExtractor.detectPlatform(url);
        if (platform == KryptonMediaExtractor.Platform.UNKNOWN) {
            AndroidUtilities.shakeView(urlInput);
            Toast.makeText(context, "Unsupported link", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDownloading) return;
        isDownloading = true;

        downloadButton.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressPercent.setText("Extracting...");
        resultContainer.setVisibility(View.GONE);

        AndroidUtilities.hideKeyboard(urlInput);

        KryptonMediaExtractor.extractMedia(url, false, new KryptonMediaExtractor.ExtractionCallback() {
            @Override
            public void onSuccess(KryptonMediaExtractor.MediaResult result) {
                if (result.directUrl == null || result.directUrl.isEmpty()) {
                    onError("Not found");
                    return;
                }

                String ext = result.isAudio ? ".mp3" : ".mp4";
                if (result.filename != null && result.filename.contains(".")) {
                    ext = result.filename.substring(result.filename.lastIndexOf("."));
                }

                String fileName = "Krypton_" + System.currentTimeMillis() + ext;
                File tempFile = new File(context.getCacheDir(), fileName);

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
                        progressPercent.setText("Saving...");
                        progressBar.setProgress(100);

                        Uri uri = KryptonMediaExtractor.saveToGallery(context, file, mimeType, fileName);

                        resetDownloadState();
                        progressContainer.setVisibility(View.GONE);

                        if (uri != null) {
                            lastSavedUri = uri;
                            lastMimeType = mimeType;
                            resultContainer.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(context, "Error saving", Toast.LENGTH_SHORT).show();
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
        downloadButton.setVisibility(View.VISIBLE);
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
                Toast.makeText(context, "Cannot open file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
