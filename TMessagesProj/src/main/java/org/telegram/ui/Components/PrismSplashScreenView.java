package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.LauncherIconController;

/**
 * Dynamic iOS 26.5 Liquid Glass Splash Screen.
 * Automatically adapts its background, glow lighting, and 3D icon
 * to the user's currently active App Icon.
 */
public class PrismSplashScreenView extends FrameLayout {

    private final FrameLayout iconCard;
    private final ImageView iconBgView;
    private final ImageView iconFgView;
    private final TextView appTitle;
    private final TextView appSubtitle;
    private final View glowLightView;

    private int primaryColor = 0xFF00E5FF;
    private int secondaryColor = 0xFF7C4DFF;
    private int bgStartColor = 0xFF0A0F1A;
    private int bgEndColor = 0xFF05080E;

    public PrismSplashScreenView(@NonNull Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);

        LauncherIconController.LauncherIcon currentIcon = LauncherIconController.getSelectedIcon();
        resolvePalette(currentIcon);

        // Dynamic Background Gradient
        GradientDrawable bgDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{bgStartColor, bgEndColor}
        );
        setBackground(bgDrawable);

        // Center container
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setOrientation(LinearLayout.VERTICAL);
        centerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(centerContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // Glow lighting behind icon
        FrameLayout iconWrapper = new FrameLayout(context);
        centerContainer.addView(iconWrapper, LayoutHelper.createLinear(130, 130, Gravity.CENTER_HORIZONTAL));

        glowLightView = new View(context) {
            private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            @Override
            protected void onDraw(Canvas canvas) {
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                float radius = getWidth() / 2f;
                glowPaint.setShader(new RadialGradient(
                        cx, cy, radius,
                        new int[]{Color.argb(90, Color.red(primaryColor), Color.green(primaryColor), Color.blue(primaryColor)), Color.TRANSPARENT},
                        new float[]{0f, 1f},
                        Shader.TileMode.CLAMP
                ));
                canvas.drawCircle(cx, cy, radius, glowPaint);
            }
        };
        iconWrapper.addView(glowLightView, LayoutHelper.createFrame(130, 130, Gravity.CENTER));

        // 3D Rounded Squircle Icon Card
        iconCard = new FrameLayout(context) {
            private final Path clipPath = new Path();
            private final RectF rectF = new RectF();
            private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            {
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
                borderPaint.setColor(Color.argb(80, 255, 255, 255));
            }

            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                clipPath.reset();
                rectF.set(0, 0, w, h);
                clipPath.addRoundRect(rectF, AndroidUtilities.dp(24), AndroidUtilities.dp(24), Path.Direction.CW);
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipPath(clipPath);
                super.dispatchDraw(canvas);
                canvas.restore();
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(24), AndroidUtilities.dp(24), borderPaint);
            }
        };
        iconCard.setElevation(AndroidUtilities.dp(12));
        iconWrapper.addView(iconCard, LayoutHelper.createFrame(88, 88, Gravity.CENTER));

        iconBgView = new ImageView(context);
        iconBgView.setScaleType(ImageView.ScaleType.FIT_XY);
        iconBgView.setImageResource(currentIcon.background);
        iconCard.addView(iconBgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        iconFgView = new ImageView(context);
        iconFgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iconFgView.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10));
        iconFgView.setImageResource(currentIcon.foreground);
        iconCard.addView(iconFgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Title
        appTitle = new TextView(context);
        appTitle.setText("PrismGram");
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        appTitle.setTypeface(AndroidUtilities.bold());
        appTitle.setTextColor(0xFFFFFFFF);
        appTitle.setLetterSpacing(0.04f);
        centerContainer.addView(appTitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 16, 0, 0));

        // Subtitle
        appSubtitle = new TextView(context);
        appSubtitle.setText("Liquid Glass Edition");
        appSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        appSubtitle.setTextColor(Color.argb(170, 255, 255, 255));
        appSubtitle.setLetterSpacing(0.06f);
        centerContainer.addView(appSubtitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 4, 0, 0));
    }

    private void resolvePalette(LauncherIconController.LauncherIcon icon) {
        if (icon == null) return;
        String key = icon.key.toLowerCase();
        if (key.contains("matrix") || key.contains("cyber")) {
            primaryColor = 0xFF00FF66;
            secondaryColor = 0xFF00E5FF;
            bgStartColor = 0xFF04140B;
            bgEndColor = 0xFF020905;
        } else if (key.contains("sunset") || key.contains("lava") || key.contains("bronze")) {
            primaryColor = 0xFFFF9100;
            secondaryColor = 0xFFFF1744;
            bgStartColor = 0xFF1C0B12;
            bgEndColor = 0xFF0E0509;
        } else if (key.contains("ruby") || key.contains("sakura") || key.contains("plasma")) {
            primaryColor = 0xFFFF2A6D;
            secondaryColor = 0xFF9D4EDD;
            bgStartColor = 0xFF1C0712;
            bgEndColor = 0xFF0D0308;
        } else if (key.contains("aurora") || key.contains("amethyst") || key.contains("cosmos") || key.contains("singularity")) {
            primaryColor = 0xFF9D4EDD;
            secondaryColor = 0xFF00E5FF;
            bgStartColor = 0xFF120722;
            bgEndColor = 0xFF080310;
        } else if (key.contains("aqua") || key.contains("pure") || key.contains("abyss") || key.contains("cobalt")) {
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF0072FF;
            bgStartColor = 0xFF061524;
            bgEndColor = 0xFF020910;
        } else if (key.contains("nox") || key.contains("monochrome")) {
            primaryColor = 0xFF60A5FA;
            secondaryColor = 0xFF94A3B8;
            bgStartColor = 0xFF0B0D12;
            bgEndColor = 0xFF030406;
        } else {
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF7C4DFF;
            bgStartColor = 0xFF0B1424;
            bgEndColor = 0xFF040810;
        }
    }

    public void showAndAutoDismiss(long delayMs) {
        // Entrance spring animation
        iconCard.setScaleX(0.75f);
        iconCard.setScaleY(0.75f);
        iconCard.setAlpha(0.0f);
        appTitle.setAlpha(0.0f);
        appSubtitle.setAlpha(0.0f);

        iconCard.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(450)
                .setInterpolator(CubicBezierInterpolator.EASE_OUT_BACK)
                .start();

        appTitle.animate()
                .alpha(1.0f)
                .setDuration(400)
                .setStartDelay(100)
                .start();

        appSubtitle.animate()
                .alpha(1.0f)
                .setDuration(400)
                .setStartDelay(180)
                .start();

        postDelayed(this::dismiss, delayMs);
    }

    public void dismiss() {
        animate()
                .alpha(0.0f)
                .scaleX(1.06f)
                .scaleY(1.06f)
                .setDuration(320)
                .setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (getParent() instanceof ViewGroup) {
                            ((ViewGroup) getParent()).removeView(PrismSplashScreenView.this);
                        }
                    }
                })
                .start();
    }
}
