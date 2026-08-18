package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.KryptonArchiveActivity;
import org.telegram.ui.KryptonMediaDownloaderActivity;
import org.telegram.ui.ProfileActivity;

/**
 * PrismDrawerHeaderView — Frosted Glass dizaynga ega, Neon Aura aylanuvchi avatar
 * va 4 ta 3D shisha tezkor rejimlar paneliga ega boshqaruv menyusi.
 */
public class PrismDrawerHeaderView extends LinearLayout {

    private final BaseFragment parentFragment;
    private final int currentAccount;
    private final Runnable onDismissMenu;

    private NeonAvatarContainer avatarContainer;
    private TextView nameTextView;
    private TextView subtitleTextView;

    private QuickChipView ghostChip;
    private QuickChipView downloaderChip;
    private QuickChipView archiveChip;
    private QuickChipView themeChip;

    public PrismDrawerHeaderView(@NonNull Context context, BaseFragment fragment, int account, Runnable onDismiss) {
        super(context);
        this.parentFragment = fragment;
        this.currentAccount = account;
        this.onDismissMenu = onDismiss;

        setOrientation(VERTICAL);
        setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(10), AndroidUtilities.dp(12), AndroidUtilities.dp(8));

        initView(context);
        updateUserData();
    }

    private void initView(Context context) {
        // ─── 1. Frosted Glass Profile Card ───
        GlassCardLayout profileCard = new GlassCardLayout(context);
        profileCard.setOrientation(HORIZONTAL);
        profileCard.setGravity(Gravity.CENTER_VERTICAL);
        profileCard.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(10), AndroidUtilities.dp(12), AndroidUtilities.dp(10));
        profileCard.setOnClickListener(v -> {
            if (onDismissMenu != null) onDismissMenu.run();
            TLRPC.User selfUser = UserConfig.getInstance(currentAccount).getCurrentUser();
            if (selfUser != null && parentFragment != null) {
                Bundle args = new Bundle();
                args.putLong("user_id", selfUser.id);
                parentFragment.presentFragment(new ProfileActivity(args));
            }
        });

        // Neon Aura Avatar
        avatarContainer = new NeonAvatarContainer(context);
        profileCard.addView(avatarContainer, LayoutHelper.createLinear(54, 54, Gravity.CENTER_VERTICAL, 0, 0, 12, 0));

        // User Info (Name + Badge + Status)
        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setOrientation(VERTICAL);
        infoLayout.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout nameRow = new LinearLayout(context);
        nameRow.setOrientation(HORIZONTAL);
        nameRow.setGravity(Gravity.CENTER_VERTICAL);

        nameTextView = new TextView(context);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setTypeface(AndroidUtilities.bold());
        nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameRow.addView(nameTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        // Prism PRO Neon Pill Badge
        TextView proBadge = new TextView(context);
        proBadge.setText("PRISM");
        proBadge.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9.5f);
        proBadge.setTypeface(AndroidUtilities.bold());
        proBadge.setTextColor(0xFF00E5FF);
        proBadge.setPadding(AndroidUtilities.dp(5), AndroidUtilities.dp(1.5f), AndroidUtilities.dp(5), AndroidUtilities.dp(1.5f));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(0x2200E5FF);
        badgeBg.setCornerRadius(AndroidUtilities.dp(8));
        badgeBg.setStroke(AndroidUtilities.dp(1), 0x8800E5FF);
        proBadge.setBackground(badgeBg);
        nameRow.addView(proBadge, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 8, 0, 0, 0));

        infoLayout.addView(nameRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        subtitleTextView = new TextView(context);
        subtitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.5f);
        subtitleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        subtitleTextView.setSingleLine(true);
        subtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        infoLayout.addView(subtitleTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 0));

        profileCard.addView(infoLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, Gravity.CENTER_VERTICAL));

        addView(profileCard, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        // ─── 2. Tezkor Rejimlar Paneli (4 ta 3D Glass Chips) ───
        LinearLayout chipsGrid = new LinearLayout(context);
        chipsGrid.setOrientation(HORIZONTAL);
        chipsGrid.setWeightSum(4.0f);

        // Chip 1: Ghost Mode (Ruh rejimi)
        boolean isGhost = com.radolyn.ayugram.AyuConfig.isGhostModeActive();
        ghostChip = new QuickChipView(context, R.drawable.msg_secret, "Ghost", isGhost ? 0xFF00E676 : 0xFF9E9E9E, isGhost);
        ghostChip.setOnClickListener(v -> {
            com.radolyn.ayugram.AyuConfig.toggleGhostMode();
            boolean active = com.radolyn.ayugram.AyuConfig.isGhostModeActive();
            ghostChip.setActive(active, active ? 0xFF00E676 : 0xFF9E9E9E);
        });
        chipsGrid.addView(ghostChip, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 4, 0));

        // Chip 2: Media Downloader
        downloaderChip = new QuickChipView(context, R.drawable.msg_download, "Media", 0xFF00B0FF, false);
        downloaderChip.setOnClickListener(v -> {
            if (onDismissMenu != null) onDismissMenu.run();
            if (parentFragment != null) {
                parentFragment.presentFragment(new KryptonMediaDownloaderActivity());
            }
        });
        chipsGrid.addView(downloaderChip, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 2, 0, 2, 0));

        // Chip 3: Deleted Messages Archive
        archiveChip = new QuickChipView(context, R.drawable.msg_delete, "Arxiv", 0xFFFF5252, false);
        archiveChip.setOnClickListener(v -> {
            if (onDismissMenu != null) onDismissMenu.run();
            if (parentFragment != null) {
                parentFragment.presentFragment(new KryptonArchiveActivity());
            }
        });
        chipsGrid.addView(archiveChip, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 2, 0, 2, 0));

        // Chip 4: Theme Toggle (Day / Night)
        boolean isDark = Theme.isCurrentThemeDark();
        themeChip = new QuickChipView(context, isDark ? R.drawable.menu_day_mode_24 : R.drawable.menu_night_mode_24, isDark ? "Kunduzgi" : "Tungi", 0xFFFFAB00, isDark);
        themeChip.setOnClickListener(v -> {
            if (onDismissMenu != null) onDismissMenu.run();
            toggleTheme();
        });
        chipsGrid.addView(themeChip, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 4, 0, 0, 0));

        addView(chipsGrid, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void updateUserData() {
        TLRPC.User user = UserConfig.getInstance(currentAccount).getCurrentUser();
        if (user != null) {
            String name = ContactsController.formatName(user.first_name, user.last_name);
            nameTextView.setText(name);

            if (!TextUtils.isEmpty(user.username)) {
                subtitleTextView.setText("@" + user.username);
            } else if (!TextUtils.isEmpty(user.phone)) {
                subtitleTextView.setText("+" + user.phone);
            } else {
                subtitleTextView.setText("Prism Premium");
            }

            avatarContainer.setUser(user);
        }
    }

    private void toggleTheme() {
        boolean isDark = Theme.isCurrentThemeDark();
        Theme.ThemeInfo targetTheme;
        if (isDark) {
            targetTheme = Theme.getTheme("Blue");
            if (targetTheme == null) targetTheme = Theme.getTheme("Day");
        } else {
            targetTheme = Theme.getTheme("Dark Blue");
            if (targetTheme == null) targetTheme = Theme.getTheme("Night");
        }
        if (targetTheme != null) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, targetTheme, false, null, -1, !isDark, null, null, null, true);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GlassCardLayout — Frosted Glass 3D uslubidagi orqa fon
    // ─────────────────────────────────────────────────────────────────────────────
    private static class GlassCardLayout extends LinearLayout {
        private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        public GlassCardLayout(Context context) {
            super(context);
            setWillNotDraw(false);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(AndroidUtilities.dp(1));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            rect.set(0, 0, getWidth(), getHeight());
            float r = AndroidUtilities.dp(16);

            boolean isDark = Theme.isCurrentThemeDark();
            bgPaint.setColor(isDark ? 0x22FFFFFF : 0x40F0F4F8);
            canvas.drawRoundRect(rect, r, r, bgPaint);

            // Shisha hoshiyasi (Specular edge highlight)
            borderPaint.setShader(new LinearGradient(
                    0, 0, 0, getHeight(),
                    new int[]{isDark ? 0x50FFFFFF : 0x80FFFFFF, isDark ? 0x10FFFFFF : 0x20000000},
                    null, Shader.TileMode.CLAMP
            ));
            canvas.drawRoundRect(rect, r, r, borderPaint);

            super.onDraw(canvas);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // NeonAvatarContainer — Aylanuvchi Neon Aura effektli Avatar
    // ─────────────────────────────────────────────────────────────────────────────
    private class NeonAvatarContainer extends FrameLayout {
        private final BackupImageView avatarImageView;
        private final AvatarDrawable avatarDrawable;
        private final Paint auraPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF auraRect = new RectF();
        private float rotationAngle = 0f;
        private ValueAnimator animator;

        public NeonAvatarContainer(Context context) {
            super(context);
            setWillNotDraw(false);

            avatarDrawable = new AvatarDrawable();
            avatarImageView = new BackupImageView(context);
            avatarImageView.setRoundRadius(AndroidUtilities.dp(22));

            addView(avatarImageView, LayoutHelper.createFrame(44, 44, Gravity.CENTER));

            auraPaint.setStyle(Paint.Style.STROKE);
            auraPaint.setStrokeWidth(AndroidUtilities.dp(2.5f));

            startAuraAnimation();
        }

        private void startAuraAnimation() {
            animator = ValueAnimator.ofFloat(0f, 360f);
            animator.setDuration(4000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                rotationAngle = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        public void setUser(TLRPC.User user) {
            avatarDrawable.setInfo(currentAccount, user);
            avatarImageView.setForUserOrChat(user, avatarDrawable);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float cx = getWidth() / 2.0f;
            float cy = getHeight() / 2.0f;
            float radius = AndroidUtilities.dp(24);

            auraRect.set(cx - radius, cy - radius, cx + radius, cy + radius);

            canvas.save();
            canvas.rotate(rotationAngle, cx, cy);

            int[] colors = new int[]{
                    0xFF00F0FF, // Electric Cyan
                    0xFF7000FF, // Deep Violet
                    0xFFFF007F, // Neon Pink
                    0xFF00FFA3, // Emerald Mint
                    0xFF00F0FF  // Loop back
            };
            auraPaint.setShader(new SweepGradient(cx, cy, colors, null));
            canvas.drawOval(auraRect, auraPaint);

            canvas.restore();
            super.onDraw(canvas);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // QuickChipView — 3D Shisha Tezkor Rejim Tugmachasi (Glass Chip)
    // ─────────────────────────────────────────────────────────────────────────────
    private static class QuickChipView extends LinearLayout {
        private final ImageView iconView;
        private final TextView titleView;
        private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        private int accentColor;
        private boolean isActive;

        public QuickChipView(Context context, int iconRes, String title, int accent, boolean active) {
            super(context);
            this.accentColor = accent;
            this.isActive = active;

            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8));
            setWillNotDraw(false);

            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(AndroidUtilities.dp(1));

            iconView = new ImageView(context);
            iconView.setImageResource(iconRes);
            iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iconView.setColorFilter(active ? accent : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(iconView, LayoutHelper.createLinear(22, 22, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 4));

            titleView = new TextView(context);
            titleView.setText(title);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            titleView.setTypeface(AndroidUtilities.bold());
            titleView.setTextColor(active ? accent : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            titleView.setGravity(Gravity.CENTER);
            titleView.setSingleLine(true);
            addView(titleView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
        }

        public void setActive(boolean active, int accent) {
            this.isActive = active;
            this.accentColor = accent;
            iconView.setColorFilter(active ? accent : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            titleView.setTextColor(active ? accent : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            rect.set(0, 0, getWidth(), getHeight());
            float r = AndroidUtilities.dp(12);

            boolean isDark = Theme.isCurrentThemeDark();
            if (isActive) {
                bgPaint.setColor(Color.argb(0x28, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)));
                borderPaint.setColor(Color.argb(0x80, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)));
            } else {
                bgPaint.setColor(isDark ? 0x1AFFFFFF : 0x30E6ECF2);
                borderPaint.setColor(isDark ? 0x30FFFFFF : 0x40D0D8E0);
            }

            canvas.drawRoundRect(rect, r, r, bgPaint);
            canvas.drawRoundRect(rect, r, r, borderPaint);

            super.onDraw(canvas);
        }
    }
}
