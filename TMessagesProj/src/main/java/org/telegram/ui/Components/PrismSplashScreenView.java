package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LauncherIconController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * 🌟 PrismSplashScreenView — Ko'zga Nihoyatda Yoqimli, Baxmal (Velvety) Mesh Gradient
 * va 100% Alohida 3D Fazoviy Dunyolar Dvigateli.
 *
 * Yangi Dizayn:
 * 1. Ko'zga Mayin, To'yingan Velvety Mesh Gradient:
 *    - 4-pog'onali yumshoq vertikal rang o'tishi.
 *    - Markaziy nafas oluvchi Volumetrik Radial Nur Toji (Soft Ambient Orb).
 *    - Ko'zni charchatmaydigan, premium OLED qorong'i qatlamlar.
 * 2. Har bir App Icon uchun tubdan alohida vizual olam va harakat fizikasi.
 * 3. 120 FPS Buttery-Smooth Zero-Allocation GPU Rendiring.
 */
public class PrismSplashScreenView extends FrameLayout {

    public enum IconTheme {
        MATRIX, SAKURA, COSMOS, RUBY, PLASMA, SUNSET, PURE, BRONZE, TURBO, SPECTRUM, DEFAULT
    }

    private IconTheme iconTheme = IconTheme.DEFAULT;
    private int primaryColor = 0xFF00E5FF;
    private int secondaryColor = 0xFF7C4DFF;
    private int accentGlowColor = 0xFFFF007F;

    // Velvety Multi-Stop Mesh Gradient Colors
    private int gradTop = 0xFF0B1424;
    private int gradMidUpper = 0xFF142038;
    private int gradMidLower = 0xFF0C1628;
    private int gradBottom = 0xFF040810;
    private int radialOrbColor = 0x5500E5FF;

    private final VelvetyMeshBackgroundView velvetyBgView;
    private final DynamicWorldBackgroundView worldBgView;
    private final BespokeParticleFieldView particlesView;
    private final Bespoke3DHeroCardView heroCardView;
    private final TextView greetingView;
    private final TextView appTitle;
    private final TextView appSubtitle;
    private final StatusBadgeView statusBadge;

    private boolean isDismissing = false;

    public PrismSplashScreenView(@NonNull Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);
        setElevation(AndroidUtilities.dp(300));

        LauncherIconController.LauncherIcon currentIcon = LauncherIconController.getSelectedIcon();
        resolveThemeAndVelvetyPalette(currentIcon);

        // 1. Ko'zga nihoyatda yoqimli Baxmal (Velvety) Mesh Gradient va Radial Nur Fon Qatlami
        velvetyBgView = new VelvetyMeshBackgroundView(context, gradTop, gradMidUpper, gradMidLower, gradBottom, radialOrbColor);
        addView(velvetyBgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 2. Har bir ikonka uchun tubdan boshqacha bo'lgan 3D Dunyo Renderi
        worldBgView = new DynamicWorldBackgroundView(context, iconTheme, primaryColor, secondaryColor, accentGlowColor);
        addView(worldBgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 3. Har bir ikonka uchun tubdan boshqacha Zarrachalar & Fizika Simulyatsiyasi
        particlesView = new BespokeParticleFieldView(context, iconTheme, primaryColor, secondaryColor, accentGlowColor);
        addView(particlesView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Center Content Container
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setOrientation(LinearLayout.VERTICAL);
        centerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(centerContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // 4. Har bir ikonka uchun tubdan boshqacha Harakat Fizikasi (Bespoke 3D Hero Card)
        heroCardView = new Bespoke3DHeroCardView(context, currentIcon, iconTheme, primaryColor, secondaryColor, accentGlowColor);
        centerContainer.addView(heroCardView, LayoutHelper.createLinear(260, 260, Gravity.CENTER_HORIZONTAL));

        // 5. Matn va Status Badji
        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        centerContainer.addView(textContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 16, 0, 0));

        greetingView = new TextView(context);
        greetingView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        greetingView.setTextColor(Color.argb(235, 255, 255, 255));
        greetingView.setGravity(Gravity.CENTER);
        greetingView.setText(generateGreetingText());
        textContainer.addView(greetingView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        appTitle = new TextView(context);
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        appTitle.setTextColor(Color.WHITE);
        appTitle.setTypeface(AndroidUtilities.bold());
        appTitle.setGravity(Gravity.CENTER);
        appTitle.setText("PrismGramm");
        textContainer.addView(appTitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 4));

        appSubtitle = new TextView(context);
        appSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        appSubtitle.setTextColor(primaryColor);
        appSubtitle.setGravity(Gravity.CENTER);
        appSubtitle.setText("✦ " + getThemeTitle() + " ✦");
        textContainer.addView(appSubtitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 8));

        statusBadge = new StatusBadgeView(context, getStatusBadgeText(), primaryColor);
        textContainer.addView(statusBadge, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    private void resolveThemeAndVelvetyPalette(LauncherIconController.LauncherIcon icon) {
        if (icon == null) icon = LauncherIconController.LauncherIcon.DEFAULT;
        String key = icon.key.toLowerCase();

        if (key.contains("matrix")) {
            iconTheme = IconTheme.MATRIX;
            primaryColor = 0xFF00FF66;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFF39FF14;
            gradTop = 0xFF04140C;
            gradMidUpper = 0xFF0A291A;
            gradMidLower = 0xFF06180E;
            gradBottom = 0xFF020905;
            radialOrbColor = 0x4400FF66;
        } else if (key.contains("sakura")) {
            iconTheme = IconTheme.SAKURA;
            primaryColor = 0xFFF472B6;
            secondaryColor = 0xFFC084FC;
            accentGlowColor = 0xFFFFD1DC;
            gradTop = 0xFF1C0D1E;
            gradMidUpper = 0xFF381A3A;
            gradMidLower = 0xFF220E24;
            gradBottom = 0xFF0C040D;
            radialOrbColor = 0x4DF472B6;
        } else if (key.contains("cosmos") || key.contains("singularity")) {
            iconTheme = IconTheme.COSMOS;
            primaryColor = 0xFF8B5CF6;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFFD946EF;
            gradTop = 0xFF0E0B28;
            gradMidUpper = 0xFF1E144E;
            gradMidLower = 0xFF120A30;
            gradBottom = 0xFF040212;
            radialOrbColor = 0x508B5CF6;
        } else if (key.contains("ruby") || key.contains("amethyst")) {
            iconTheme = IconTheme.RUBY;
            primaryColor = 0xFFE11D48;
            secondaryColor = 0xFFA855F7;
            accentGlowColor = 0xFFFB7185;
            gradTop = 0xFF240614;
            gradMidUpper = 0xFF440C26;
            gradMidLower = 0xFF260514;
            gradBottom = 0xFF0D0106;
            radialOrbColor = 0x4DE11D48;
        } else if (key.contains("plasma") || key.contains("cyber") || key.contains("glitch")) {
            iconTheme = IconTheme.PLASMA;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFFFF007F;
            accentGlowColor = 0xFFD946EF;
            gradTop = 0xFF0E0B2E;
            gradMidUpper = 0xFF1C1352;
            gradMidLower = 0xFF100A32;
            gradBottom = 0xFF040214;
            radialOrbColor = 0x4800E5FF;
        } else if (key.contains("sunset") || key.contains("lava")) {
            iconTheme = IconTheme.SUNSET;
            primaryColor = 0xFFFF9100;
            secondaryColor = 0xFFFF1744;
            accentGlowColor = 0xFFFFD700;
            gradTop = 0xFF240A06;
            gradMidUpper = 0xFF451408;
            gradMidLower = 0xFF280B04;
            gradBottom = 0xFF0D0200;
            radialOrbColor = 0x4DFF9100;
        } else if (key.contains("pure") || key.contains("aqua") || key.contains("abyss") || key.contains("aurora")) {
            iconTheme = IconTheme.PURE;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF00B0FF;
            accentGlowColor = 0xFF80D8FF;
            gradTop = 0xFF051726;
            gradMidUpper = 0xFF0B2E4A;
            gradMidLower = 0xFF061A2C;
            gradBottom = 0xFF010810;
            radialOrbColor = 0x4800E5FF;
        } else if (key.contains("bronze") || key.contains("vintage")) {
            iconTheme = IconTheme.BRONZE;
            primaryColor = 0xFFD97706;
            secondaryColor = 0xFFB45309;
            accentGlowColor = 0xFFF59E0B;
            gradTop = 0xFF211306;
            gradMidUpper = 0xFF3D240A;
            gradMidLower = 0xFF241404;
            gradBottom = 0xFF0D0601;
            radialOrbColor = 0x48D97706;
        } else if (key.contains("turbo")) {
            iconTheme = IconTheme.TURBO;
            primaryColor = 0xFFFF5252;
            secondaryColor = 0xFFFF7A00;
            accentGlowColor = 0xFFFFAB40;
            gradTop = 0xFF240606;
            gradMidUpper = 0xFF440C0C;
            gradMidLower = 0xFF260505;
            gradBottom = 0xFF0D0101;
            radialOrbColor = 0x4DFF5252;
        } else if (key.contains("spectrum") || key.contains("chrome") || key.contains("monochrome") || key.contains("premium") || key.contains("nox") || key.contains("cobalt")) {
            iconTheme = IconTheme.SPECTRUM;
            primaryColor = 0xFF00F0FF;
            secondaryColor = 0xFFFF0055;
            accentGlowColor = 0xFFFFD700;
            gradTop = 0xFF140D2B;
            gradMidUpper = 0xFF291754;
            gradMidLower = 0xFF160C30;
            gradBottom = 0xFF05020F;
            radialOrbColor = 0x4800F0FF;
        } else {
            iconTheme = IconTheme.DEFAULT;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF007AFF;
            accentGlowColor = 0xFF00F0FF;
            gradTop = 0xFF081528;
            gradMidUpper = 0xFF11294D;
            gradMidLower = 0xFF091932;
            gradBottom = 0xFF020712;
            radialOrbColor = 0x4500E5FF;
        }
    }

    private String getThemeTitle() {
        switch (iconTheme) {
            case MATRIX: return "Cyber Tesseract Matrix";
            case SAKURA: return "Zen Blossom Spring Wind";
            case COSMOS: return "Celestial Gravitational Orbit";
            case RUBY: return "Prismatic Diamond Caustics";
            case PLASMA: return "Tesla High-Voltage Discharge";
            case SUNSET: return "Volcanic Solar Prominence";
            case PURE: return "Glacial Oceanic Caustic";
            case BRONZE: return "Steampunk Chrono Gear";
            case TURBO: return "Hyperspace Warp Velocity";
            case SPECTRUM: return "Liquid Chromatic Mercury";
            default: return "3D Liquid Glass Prism";
        }
    }

    private String getStatusBadgeText() {
        switch (iconTheme) {
            case MATRIX: return "SYS // 3D QUANTUM ENCRYPTION [ONLINE]";
            case SAKURA: return "NATURE // 3D AERODYNAMIC BREEZE [ACTIVE]";
            case COSMOS: return "ASTRO // 3D GRAVITY ACCRETION [LOCKED]";
            case RUBY: return "OPTICS // 3D DIAMOND DISPERSION [ENGAGED]";
            case PLASMA: return "ENERGY // 3D TESLA ARC VOLTAGE [MAX]";
            case SUNSET: return "THERMAL // 3D SOLAR CONVECTION [PEAK]";
            case PURE: return "HYDRO // 3D CAUSTIC REFRACTION [CLEAR]";
            case BRONZE: return "CHRONO // 3D GEAR RATIO [1:1]";
            case TURBO: return "WARP // 3D RELATIVISTIC SPEED [100%]";
            case SPECTRUM: return "PRISM // 3D CHROMATIC DISPERSION [PURE]";
            default: return "CORE // 3D LIQUID GLASS [ACTIVE]";
        }
    }

    private String generateGreetingText() {
        int currentAccount = UserConfig.selectedAccount;
        TLRPC.User currentUser = UserConfig.getInstance(currentAccount).getCurrentUser();
        String userName = "";
        if (currentUser != null) {
            userName = currentUser.first_name;
            if (TextUtils.isEmpty(userName)) {
                userName = UserObject.getUserName(currentUser);
            }
        }
        if (TextUtils.isEmpty(userName)) {
            userName = "Foydalanuvchi";
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String lang = "uz";
        try {
            if (LocaleController.getInstance() != null && LocaleController.getInstance().getCurrentLocale() != null) {
                lang = LocaleController.getInstance().getCurrentLocale().getLanguage().toLowerCase();
            }
        } catch (Throwable ignored) {}

        if (lang.startsWith("ru")) {
            if (hour >= 5 && hour < 12) return "Доброе утро, " + userName + "! ☀️";
            else if (hour >= 12 && hour < 18) return "Добрый день, " + userName + "! 🌤️";
            else if (hour >= 18 && hour < 23) return "Добрый вечер, " + userName + "! 🌙";
            else return "Доброй ночи, " + userName + "! ✨";
        } else if (lang.startsWith("en")) {
            if (hour >= 5 && hour < 12) return "Good morning, " + userName + "! ☀️";
            else if (hour >= 12 && hour < 18) return "Good afternoon, " + userName + "! 🌤️";
            else if (hour >= 18 && hour < 23) return "Good evening, " + userName + "! 🌙";
            else return "Good night, " + userName + "! ✨";
        } else {
            if (hour >= 5 && hour < 12) return "Xayrli tong, " + userName + "! ☀️";
            else if (hour >= 12 && hour < 18) return "Xayrli kun, " + userName + "! 🌤️";
            else if (hour >= 18 && hour < 23) return "Xayrli kech, " + userName + "! 🌙";
            else return "Xayrli tun, " + userName + "! ✨";
        }
    }

    public void showAndAutoDismiss(long delayMs) {
        heroCardView.setScaleX(0.2f);
        heroCardView.setScaleY(0.2f);
        heroCardView.setAlpha(0.0f);

        greetingView.setAlpha(0.0f);
        greetingView.setTranslationY(AndroidUtilities.dp(18));
        appTitle.setAlpha(0.0f);
        appSubtitle.setAlpha(0.0f);
        statusBadge.setAlpha(0.0f);

        heroCardView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(750)
                .setInterpolator(new OvershootInterpolator(1.3f))
                .start();

        greetingView.animate()
                .alpha(1.0f)
                .translationY(0)
                .setDuration(500)
                .setStartDelay(200)
                .setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT)
                .start();

        appTitle.animate()
                .alpha(1.0f)
                .setDuration(500)
                .setStartDelay(280)
                .start();

        appSubtitle.animate()
                .alpha(1.0f)
                .setDuration(500)
                .setStartDelay(360)
                .start();

        statusBadge.animate()
                .alpha(1.0f)
                .setDuration(500)
                .setStartDelay(420)
                .start();

        postDelayed(this::dismiss, Math.max(delayMs, 4300));
    }

    public void dismiss() {
        if (isDismissing) return;
        isDismissing = true;

        animate()
                .alpha(0.0f)
                .scaleX(1.12f)
                .scaleY(1.12f)
                .setDuration(380)
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

    // ──────────────────────────────────────────────────────────────────────────
    // 0. Velvety Mesh Gradient & Volumetric Radial Ambient Orb View
    // ──────────────────────────────────────────────────────────────────────────
    private static class VelvetyMeshBackgroundView extends View {
        private final int c1, c2, c3, c4, orbColor;
        private final Paint linearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint orbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private LinearGradient linearGradient;
        private RadialGradient radialGradient;
        private float orbPulse = 1.0f;
        private int lastW, lastH;

        public VelvetyMeshBackgroundView(Context context, int c1, int c2, int c3, int c4, int orbColor) {
            super(context);
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
            this.c4 = c4;
            this.orbColor = orbColor;

            ValueAnimator pulseAnim = ValueAnimator.ofFloat(0.85f, 1.15f);
            pulseAnim.setDuration(3500);
            pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
            pulseAnim.setRepeatMode(ValueAnimator.REVERSE);
            pulseAnim.addUpdateListener(animation -> {
                orbPulse = (float) animation.getAnimatedValue();
                invalidate();
            });
            pulseAnim.start();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w > 0 && h > 0) {
                lastW = w;
                lastH = h;
                linearGradient = new LinearGradient(
                        0, 0, 0, h,
                        new int[]{c1, c2, c3, c4},
                        new float[]{0.0f, 0.35f, 0.70f, 1.0f},
                        Shader.TileMode.CLAMP
                );
                linearPaint.setShader(linearGradient);

                float cx = w / 2f;
                float cy = h * 0.45f;
                float radius = AndroidUtilities.dp(240);
                radialGradient = new RadialGradient(
                        cx, cy, radius,
                        new int[]{orbColor, Color.argb(30, Color.red(orbColor), Color.green(orbColor), Color.blue(orbColor)), Color.TRANSPARENT},
                        new float[]{0.0f, 0.55f, 1.0f},
                        Shader.TileMode.CLAMP
                );
                orbPaint.setShader(radialGradient);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (lastW == 0 || lastH == 0) return;
            // 1. 4-Stop Smooth Velvety Base Linear Gradient
            canvas.drawRect(0, 0, lastW, lastH, linearPaint);

            // 2. Volumetric Breathing Core Glow Orb behind 3D Card
            canvas.save();
            float cx = lastW / 2f;
            float cy = lastH * 0.45f;
            canvas.scale(orbPulse, orbPulse, cx, cy);
            canvas.drawCircle(cx, cy, AndroidUtilities.dp(240), orbPaint);
            canvas.restore();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 1. Dynamic Bespoke World Background View (100% Unique Per Theme)
    // ──────────────────────────────────────────────────────────────────────────
    private static class DynamicWorldBackgroundView extends View {
        private final IconTheme theme;
        private final int primaryColor, secondaryColor, glowColor;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Camera camera3D = new Camera();
        private final Matrix matrix3D = new Matrix();
        private final Path path = new Path();
        private float progress;

        // 3D Geometry Vertices
        private static final float[][] CUBE_VERTS = {
                {-1.3f, -1.3f, -1.3f}, {1.3f, -1.3f, -1.3f}, {1.3f, 1.3f, -1.3f}, {-1.3f, 1.3f, -1.3f},
                {-1.3f, -1.3f, 1.3f},  {1.3f, -1.3f, 1.3f},  {1.3f, 1.3f, 1.3f},  {-1.3f, 1.3f, 1.3f}
        };
        private static final int[][] CUBE_EDGES = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        private static final float[][] DIAMOND_VERTS = {
                {0, -1.8f, 0}, {1.5f, 0, 0}, {0, 0, 1.5f},
                {-1.5f, 0, 0}, {0, 0, -1.5f}, {0, 1.8f, 0}
        };
        private static final int[][] DIAMOND_EDGES = {
                {0, 1}, {0, 2}, {0, 3}, {0, 4}, {5, 1}, {5, 2}, {5, 3}, {5, 4},
                {1, 2}, {2, 3}, {3, 4}, {4, 1}
        };

        private final float[][] proj = new float[8][2];

        public DynamicWorldBackgroundView(Context context, IconTheme theme, int c1, int c2, int c3) {
            super(context);
            this.theme = theme;
            this.primaryColor = c1;
            this.secondaryColor = c2;
            this.glowColor = c3;

            paint.setStyle(Paint.Style.STROKE);
            fillPaint.setStyle(Paint.Style.FILL);

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 360f);
            animator.setDuration(6000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(animation -> {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int w = getWidth();
            int h = getHeight();
            if (w == 0 || h == 0) return;

            float cx = w / 2f;
            float cy = h / 2f;

            switch (theme) {
                case MATRIX:
                    float boxSize = AndroidUtilities.dp(105);
                    paint.setColor(primaryColor);
                    paint.setStrokeWidth(AndroidUtilities.dp(2.4f));
                    paint.setAlpha(180);
                    draw3DWireframe(canvas, cx, cy, CUBE_VERTS, CUBE_EDGES, boxSize, 25, progress, progress * 0.5f);

                    float laserY = (cy - AndroidUtilities.dp(160)) + ((progress * 3.5f) % (AndroidUtilities.dp(320)));
                    paint.setColor(glowColor);
                    paint.setStrokeWidth(AndroidUtilities.dp(2f));
                    paint.setAlpha(120);
                    canvas.drawLine(cx - AndroidUtilities.dp(120), laserY, cx + AndroidUtilities.dp(120), laserY, paint);
                    break;

                case SAKURA:
                    paint.setStrokeWidth(AndroidUtilities.dp(2f));
                    for (int i = 0; i < 4; i++) {
                        path.reset();
                        float startY = cy - AndroidUtilities.dp(120) + i * AndroidUtilities.dp(70);
                        path.moveTo(0, startY);
                        for (float x = 0; x <= w; x += 40) {
                            float y = startY + (float) Math.sin((x / 140f) + Math.toRadians(progress * 2 + i * 45)) * AndroidUtilities.dp(22);
                            path.lineTo(x, y);
                        }
                        paint.setColor(i % 2 == 0 ? primaryColor : secondaryColor);
                        paint.setAlpha(70);
                        canvas.drawPath(path, paint);
                    }
                    break;

                case COSMOS:
                    float orbitR = AndroidUtilities.dp(135);
                    paint.setStrokeWidth(AndroidUtilities.dp(3f));

                    canvas.save();
                    camera3D.save();
                    camera3D.rotateX(68);
                    camera3D.rotateZ(progress * 0.8f);
                    camera3D.getMatrix(matrix3D);
                    camera3D.restore();
                    matrix3D.preTranslate(-cx, -cy);
                    matrix3D.postTranslate(cx, cy);
                    canvas.concat(matrix3D);

                    paint.setColor(primaryColor);
                    paint.setAlpha(170);
                    canvas.drawCircle(cx, cy, orbitR, paint);

                    float moonX = cx + (float) Math.cos(Math.toRadians(progress * 2)) * orbitR;
                    float moonY = cy + (float) Math.sin(Math.toRadians(progress * 2)) * orbitR;
                    fillPaint.setColor(glowColor);
                    fillPaint.setAlpha(255);
                    canvas.drawCircle(moonX, moonY, AndroidUtilities.dp(8), fillPaint);
                    canvas.restore();

                    canvas.save();
                    camera3D.save();
                    camera3D.rotateX(-58);
                    camera3D.rotateZ(-progress * 0.6f);
                    camera3D.getMatrix(matrix3D);
                    camera3D.restore();
                    matrix3D.preTranslate(-cx, -cy);
                    matrix3D.postTranslate(cx, cy);
                    canvas.concat(matrix3D);
                    paint.setColor(secondaryColor);
                    paint.setAlpha(120);
                    canvas.drawCircle(cx, cy, orbitR * 1.3f, paint);
                    canvas.restore();
                    break;

                case RUBY:
                    float gemSize = AndroidUtilities.dp(120);
                    paint.setColor(primaryColor);
                    paint.setStrokeWidth(AndroidUtilities.dp(2.6f));
                    paint.setAlpha(210);
                    draw3DWireframe(canvas, cx, cy, DIAMOND_VERTS, DIAMOND_EDGES, gemSize, 28, progress, 0);

                    canvas.save();
                    canvas.rotate(progress * 0.5f, cx, cy);
                    paint.setColor(glowColor);
                    paint.setStrokeWidth(AndroidUtilities.dp(1.2f));
                    paint.setAlpha(80);
                    for (int a = 0; a < 360; a += 45) {
                        float rad = (float) Math.toRadians(a);
                        canvas.drawLine(cx, cy, cx + (float) Math.cos(rad) * AndroidUtilities.dp(170), cy + (float) Math.sin(rad) * AndroidUtilities.dp(170), paint);
                    }
                    canvas.restore();
                    break;

                case PLASMA:
                    paint.setColor(primaryColor);
                    paint.setStrokeWidth(AndroidUtilities.dp(2.2f));
                    paint.setAlpha(220);
                    for (int i = 0; i < 4; i++) {
                        float angle = i * 90f + (progress * 3f);
                        float rad = (float) Math.toRadians(angle);
                        float targetX = cx + (float) Math.cos(rad) * AndroidUtilities.dp(140);
                        float targetY = cy + (float) Math.sin(rad) * AndroidUtilities.dp(140);

                        path.reset();
                        path.moveTo(cx, cy);
                        for (int s = 1; s <= 4; s++) {
                            float frac = s / 4f;
                            float nx = cx + (targetX - cx) * frac + (float) (Math.sin(progress * 8 + s + i) * AndroidUtilities.dp(16));
                            float ny = cy + (targetY - cy) * frac + (float) (Math.cos(progress * 8 + s + i) * AndroidUtilities.dp(16));
                            path.lineTo(nx, ny);
                        }
                        path.lineTo(targetX, targetY);
                        canvas.drawPath(path, paint);

                        fillPaint.setColor(glowColor);
                        canvas.drawCircle(targetX, targetY, AndroidUtilities.dp(5), fillPaint);
                    }
                    break;

                case SUNSET:
                    for (int i = 0; i < 8; i++) {
                        float flareAng = i * 45f + progress * 0.4f;
                        float rad = (float) Math.toRadians(flareAng);
                        float len = AndroidUtilities.dp(95) + (float) (Math.sin(Math.toRadians(progress * 4 + i * 40))) * AndroidUtilities.dp(35);
                        paint.setColor(i % 2 == 0 ? primaryColor : secondaryColor);
                        paint.setStrokeWidth(AndroidUtilities.dp(3f));
                        paint.setAlpha(130);
                        canvas.drawLine(cx, cy, cx + (float) Math.cos(rad) * len, cy + (float) Math.sin(rad) * len, paint);
                    }
                    break;

                case PURE:
                    paint.setStrokeWidth(AndroidUtilities.dp(2f));
                    for (int r = 1; r <= 3; r++) {
                        float radius = AndroidUtilities.dp(70 + r * 35) + (float) Math.sin(Math.toRadians(progress * 3 + r * 60)) * AndroidUtilities.dp(15);
                        paint.setColor(primaryColor);
                        paint.setAlpha(110 - r * 25);
                        canvas.drawCircle(cx, cy, radius, paint);
                    }
                    break;

                case BRONZE:
                    drawGear(canvas, cx, cy - AndroidUtilities.dp(30), AndroidUtilities.dp(95), 16, progress * 0.8f, primaryColor);
                    drawGear(canvas, cx + AndroidUtilities.dp(90), cy + AndroidUtilities.dp(60), AndroidUtilities.dp(65), 10, -progress * 1.2f, secondaryColor);
                    drawGear(canvas, cx - AndroidUtilities.dp(90), cy + AndroidUtilities.dp(60), AndroidUtilities.dp(65), 10, -progress * 1.2f, glowColor);
                    break;

                case TURBO:
                    for (int i = 0; i < 6; i++) {
                        float prog = ((progress * 2.5f + i * 60) % 360f) / 360f;
                        float depthR = AndroidUtilities.dp(20) + prog * AndroidUtilities.dp(180);
                        int alpha = (int) (prog * 200);
                        paint.setColor(primaryColor);
                        paint.setStrokeWidth(AndroidUtilities.dp(1.2f + prog * 3f));
                        paint.setAlpha(alpha);
                        canvas.drawCircle(cx, cy, depthR, paint);
                    }
                    break;

                default:
                    canvas.save();
                    canvas.rotate(progress * 0.6f, cx, cy);
                    paint.setStrokeWidth(AndroidUtilities.dp(2.4f));
                    for (int a = 0; a < 360; a += 30) {
                        float rad = (float) Math.toRadians(a);
                        paint.setColor(a % 60 == 0 ? primaryColor : secondaryColor);
                        paint.setAlpha(130);
                        canvas.drawLine(cx, cy, cx + (float) Math.cos(rad) * AndroidUtilities.dp(150), cy + (float) Math.sin(rad) * AndroidUtilities.dp(150), paint);
                    }
                    canvas.restore();
                    break;
            }
        }

        private void drawGear(Canvas canvas, float cx, float cy, float radius, int teeth, float angle, int color) {
            paint.setColor(color);
            paint.setStrokeWidth(AndroidUtilities.dp(3f));
            paint.setAlpha(170);

            path.reset();
            float toothHeight = AndroidUtilities.dp(9);
            float innerR = radius - toothHeight;
            float outerR = radius + toothHeight;

            for (int i = 0; i < teeth; i++) {
                float a1 = (float) Math.toRadians(angle + (i * 360f / teeth));
                float a2 = (float) Math.toRadians(angle + ((i + 0.35f) * 360f / teeth));
                float a3 = (float) Math.toRadians(angle + ((i + 0.65f) * 360f / teeth));
                float a4 = (float) Math.toRadians(angle + ((i + 1.0f) * 360f / teeth));

                if (i == 0) path.moveTo(cx + (float) Math.cos(a1) * innerR, cy + (float) Math.sin(a1) * innerR);
                else path.lineTo(cx + (float) Math.cos(a1) * innerR, cy + (float) Math.sin(a1) * innerR);

                path.lineTo(cx + (float) Math.cos(a2) * outerR, cy + (float) Math.sin(a2) * outerR);
                path.lineTo(cx + (float) Math.cos(a3) * outerR, cy + (float) Math.sin(a3) * outerR);
                path.lineTo(cx + (float) Math.cos(a4) * innerR, cy + (float) Math.sin(a4) * innerR);
            }
            path.close();
            canvas.drawPath(path, paint);
            canvas.drawCircle(cx, cy, radius * 0.4f, paint);
        }

        private void draw3DWireframe(Canvas canvas, float cx, float cy, float[][] vertices, int[][] edges, float scale, float rx, float ry, float rz) {
            float radX = (float) Math.toRadians(rx);
            float radY = (float) Math.toRadians(ry);
            float radZ = (float) Math.toRadians(rz);
            float fov = 400f;

            for (int i = 0; i < vertices.length; i++) {
                float x = vertices[i][0] * scale;
                float y = vertices[i][1] * scale;
                float z = vertices[i][2] * scale;

                float y1 = y * (float) Math.cos(radX) - z * (float) Math.sin(radX);
                float z1 = y * (float) Math.sin(radX) + z * (float) Math.cos(radX);
                float x2 = x * (float) Math.cos(radY) + z1 * (float) Math.sin(radY);
                float z2 = -x * (float) Math.sin(radY) + z1 * (float) Math.cos(radY);
                float x3 = x2 * (float) Math.cos(radZ) - y1 * (float) Math.sin(radZ);
                float y3 = x2 * (float) Math.sin(radZ) + y1 * (float) Math.cos(radZ);

                float distance = fov / (fov + z2 + 300f);
                proj[i][0] = cx + x3 * distance;
                proj[i][1] = cy + y3 * distance;
            }

            for (int[] edge : edges) {
                canvas.drawLine(proj[edge[0]][0], proj[edge[0]][1], proj[edge[1]][0], proj[edge[1]][1], paint);
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Bespoke Particle Field View (Unique Physics Models Per Theme)
    // ──────────────────────────────────────────────────────────────────────────
    private static class BespokeParticleFieldView extends View {
        private static final int COUNT = 50;
        private final List<BespokeParticle> list = new ArrayList<>();
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final IconTheme theme;
        private final int c1, c2, c3;
        private final Random rand = new Random();
        private long lastTime;
        private static final String[] MATRIX_GLYPHS = {"1", "0", "7", "X", "λ", "Ω", "Ø", "F", "K", "§"};

        private static class BespokeParticle {
            float x, y, z;
            float vx, vy, vz;
            float rot, vRot;
            float size;
            float alpha;
            int color;
            String text;
        }

        public BespokeParticleFieldView(Context context, IconTheme theme, int c1, int c2, int c3) {
            super(context);
            this.theme = theme;
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;

            textPaint.setColor(c1);
            textPaint.setTypeface(AndroidUtilities.bold());

            init();
        }

        private void init() {
            list.clear();
            for (int i = 0; i < COUNT; i++) {
                list.add(spawn(true));
            }
        }

        private BespokeParticle spawn(boolean randomZ) {
            BespokeParticle p = new BespokeParticle();
            int w = getWidth() > 0 ? getWidth() : AndroidUtilities.displaySize.x;
            int h = getHeight() > 0 ? getHeight() : AndroidUtilities.displaySize.y;

            p.x = (rand.nextFloat() - 0.5f) * w * 1.5f;
            p.y = (rand.nextFloat() - 0.5f) * h * 1.5f;
            p.z = randomZ ? rand.nextFloat() * 800f : 800f;
            p.size = AndroidUtilities.dp(3 + rand.nextFloat() * 5f);
            p.alpha = 0.3f + rand.nextFloat() * 0.7f;
            p.rot = rand.nextFloat() * 360f;
            p.vRot = (rand.nextFloat() - 0.5f) * 8f;
            p.text = MATRIX_GLYPHS[rand.nextInt(MATRIX_GLYPHS.length)];

            int r = rand.nextInt(3);
            p.color = r == 0 ? c1 : (r == 1 ? c2 : c3);

            switch (theme) {
                case MATRIX:
                    p.vx = 0;
                    p.vy = 280f + rand.nextFloat() * 320f;
                    p.vz = 0;
                    break;
                case SAKURA:
                    p.vx = 70f + (rand.nextFloat() - 0.5f) * 40f;
                    p.vy = 130f + rand.nextFloat() * 110f;
                    p.vz = (rand.nextFloat() - 0.5f) * 50f;
                    break;
                case SUNSET:
                    p.vx = (rand.nextFloat() - 0.5f) * 50f;
                    p.vy = -(140f + rand.nextFloat() * 160f);
                    p.vz = (rand.nextFloat() - 0.5f) * 50f;
                    break;
                case TURBO:
                    p.vx = 0;
                    p.vy = 0;
                    p.vz = -(600f + rand.nextFloat() * 900f);
                    break;
                default:
                    p.vx = (rand.nextFloat() - 0.5f) * 60f;
                    p.vy = (rand.nextFloat() - 0.5f) * 60f;
                    p.vz = (rand.nextFloat() - 0.5f) * 60f;
                    break;
            }
            return p;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int w = getWidth();
            int h = getHeight();
            if (w == 0 || h == 0) return;

            long now = SystemClock.elapsedRealtime();
            float dt = lastTime == 0 ? 0.016f : Math.min((now - lastTime) / 1000f, 0.05f);
            lastTime = now;

            float cx = w / 2f;
            float cy = h / 2f;
            float fov = 450f;

            for (BespokeParticle p : list) {
                p.x += p.vx * dt;
                p.y += p.vy * dt;
                p.z += p.vz * dt;
                p.rot += p.vRot;

                if (theme == IconTheme.TURBO) {
                    if (p.z < 20f) {
                        p.z = 800f;
                        p.x = (rand.nextFloat() - 0.5f) * w * 1.5f;
                        p.y = (rand.nextFloat() - 0.5f) * h * 1.5f;
                    }
                } else if (p.y > h / 2f + 500f || p.y < -h / 2f - 500f || p.x > w / 2f + 500f || p.x < -w / 2f - 500f) {
                    BespokeParticle fresh = spawn(false);
                    p.x = fresh.x;
                    p.y = (theme == IconTheme.SUNSET) ? h / 2f + 50f : -h / 2f - 50f;
                    p.z = fresh.z;
                }

                float distance = fov / Math.max(10f, fov + p.z);
                float px = cx + p.x * distance;
                float py = cy + p.y * distance;
                float pSize = p.size * distance * 1.6f;

                paint.setColor(p.color);
                paint.setAlpha((int) (p.alpha * Math.min(1f, distance * 1.9f) * 255));

                if (theme == IconTheme.MATRIX) {
                    textPaint.setColor(p.color);
                    textPaint.setTextSize(Math.max(AndroidUtilities.dp(9), AndroidUtilities.dp(18) * distance));
                    textPaint.setAlpha((int) (p.alpha * Math.min(1f, distance * 2.2f) * 255));
                    canvas.drawText(p.text, px, py, textPaint);
                } else if (theme == IconTheme.SAKURA) {
                    canvas.save();
                    canvas.translate(px, py);
                    canvas.rotate(p.rot);
                    canvas.scale(1f, (float) Math.sin(Math.toRadians(p.rot)));
                    canvas.drawOval(-pSize * 1.6f, -pSize * 0.9f, pSize * 1.6f, pSize * 0.9f, paint);
                    canvas.restore();
                } else if (theme == IconTheme.TURBO) {
                    float prevDist = fov / Math.max(10f, fov + p.z - p.vz * dt * 2.5f);
                    float prevX = cx + p.x * prevDist;
                    float prevY = cy + p.y * prevDist;
                    paint.setStrokeWidth(Math.max(1.8f, pSize * 0.7f));
                    canvas.drawLine(px, py, prevX, prevY, paint);
                } else {
                    canvas.drawCircle(px, py, Math.max(1.5f, pSize), paint);
                }
            }
            invalidate();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Bespoke 3D Hero Card View (Unique Movement Physics Per Theme)
    // ──────────────────────────────────────────────────────────────────────────
    private static class Bespoke3DHeroCardView extends FrameLayout {
        private final Camera camera = new Camera();
        private final Matrix matrix = new Matrix();
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rectF = new RectF();
        private final RectF shadowRect = new RectF();
        private final Path clipPath = new Path();

        private final IconTheme theme;
        private float tiltX, tiltY, tiltZ;
        private float elevationZ;

        public Bespoke3DHeroCardView(Context context, LauncherIconController.LauncherIcon icon, IconTheme theme, int c1, int c2, int c3) {
            super(context);
            this.theme = theme;
            setWillNotDraw(false);

            shadowPaint.setStyle(Paint.Style.FILL);
            shadowPaint.setColor(0xFF000000);

            FrameLayout card = new FrameLayout(context) {
                @Override
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    clipPath.reset();
                    rectF.set(0, 0, w, h);
                    clipPath.addRoundRect(rectF, AndroidUtilities.dp(28), AndroidUtilities.dp(28), Path.Direction.CW);
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    canvas.save();
                    canvas.clipPath(clipPath);
                    super.dispatchDraw(canvas);
                    canvas.restore();
                }
            };
            card.setElevation(AndroidUtilities.dp(28));
            addView(card, LayoutHelper.createFrame(110, 110, Gravity.CENTER));

            ImageView bg = new ImageView(context);
            bg.setScaleType(ImageView.ScaleType.FIT_XY);
            bg.setImageResource(icon.background);
            card.addView(bg, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            ImageView fg = new ImageView(context);
            fg.setScaleType(ImageView.ScaleType.FIT_XY);
            fg.setImageResource(icon.foreground);
            card.addView(fg, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(AndroidUtilities.dp(2.6f));
            borderPaint.setColor(Color.argb(220, 255, 255, 255));

            ValueAnimator physicsAnim = ValueAnimator.ofFloat(0f, 360f);
            physicsAnim.setDuration(theme == IconTheme.TURBO ? 2500 : (theme == IconTheme.BRONZE ? 5000 : 4000));
            physicsAnim.setRepeatCount(ValueAnimator.INFINITE);
            physicsAnim.addUpdateListener(animation -> {
                float val = (float) animation.getAnimatedValue();

                switch (theme) {
                    case SAKURA:
                        tiltX = (float) Math.sin(Math.toRadians(val)) * 14f;
                        tiltY = (float) Math.cos(Math.toRadians(val * 0.7f)) * 18f;
                        tiltZ = (float) Math.sin(Math.toRadians(val * 0.5f)) * 8f;
                        elevationZ = (float) Math.sin(Math.toRadians(val)) * 30f;
                        break;
                    case TURBO:
                        tiltX = -20f + (float) Math.sin(Math.toRadians(val * 2)) * 6f;
                        tiltY = (float) Math.sin(Math.toRadians(val)) * 12f;
                        tiltZ = 0;
                        elevationZ = (float) Math.sin(Math.toRadians(val * 2)) * 40f;
                        break;
                    case BRONZE:
                        float stepVal = ((int) (val / 30f)) * 30f;
                        tiltX = (float) Math.sin(Math.toRadians(stepVal)) * 18f;
                        tiltY = (float) Math.cos(Math.toRadians(stepVal)) * 22f;
                        tiltZ = (float) Math.sin(Math.toRadians(stepVal * 0.5f)) * 5f;
                        elevationZ = (float) Math.sin(Math.toRadians(stepVal)) * 25f;
                        break;
                    case MATRIX:
                        float jitter = (val % 40 < 5) ? (float) (Math.random() - 0.5f) * 8f : 0f;
                        tiltX = (float) Math.sin(Math.toRadians(val)) * 20f + jitter;
                        tiltY = (float) Math.cos(Math.toRadians(val)) * 26f + jitter;
                        tiltZ = jitter;
                        elevationZ = (float) Math.sin(Math.toRadians(val)) * 40f;
                        break;
                    default:
                        tiltX = (float) Math.sin(Math.toRadians(val)) * 20f;
                        tiltY = (float) Math.cos(Math.toRadians(val)) * 26f;
                        tiltZ = (float) Math.sin(Math.toRadians(val * 0.5f)) * 7f;
                        elevationZ = (float) Math.sin(Math.toRadians(val)) * 45f;
                        break;
                }
                invalidate();
            });
            physicsAnim.start();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            float shadowY = cy + AndroidUtilities.dp(78);
            float shadowW = AndroidUtilities.dp(95) * (1f + elevationZ / 120f);
            float shadowH = AndroidUtilities.dp(20) * (1f - elevationZ / 140f);
            float shadowOffsetX = -tiltY * 2.2f;
            shadowRect.set(cx - shadowW / 2f + shadowOffsetX, shadowY - shadowH / 2f, cx + shadowW / 2f + shadowOffsetX, shadowY + shadowH / 2f);
            shadowPaint.setAlpha((int) Math.max(30, (110 - elevationZ)));
            canvas.drawOval(shadowRect, shadowPaint);

            canvas.save();
            camera.save();
            camera.rotateX(tiltX);
            camera.rotateY(tiltY);
            camera.rotateZ(tiltZ);
            camera.translate(0, 0, elevationZ);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-cx, -cy);
            matrix.postTranslate(cx, cy);
            canvas.concat(matrix);

            super.dispatchDraw(canvas);

            float cardLeft = cx - AndroidUtilities.dp(55);
            float cardTop = cy - AndroidUtilities.dp(55);
            float cardSize = AndroidUtilities.dp(110);
            rectF.set(cardLeft, cardTop, cardLeft + cardSize, cardTop + cardSize);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(28), AndroidUtilities.dp(28), borderPaint);

            canvas.restore();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. Status Badge with Live Pulsing Indicator Dot
    // ──────────────────────────────────────────────────────────────────────────
    private static class StatusBadgeView extends LinearLayout {
        private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float dotPulseAlpha = 1.0f;

        public StatusBadgeView(Context context, String text, int accentColor) {
            super(context);
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(4), AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.argb(45, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)));
            bg.setStroke(AndroidUtilities.dp(1), Color.argb(120, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)));
            bg.setCornerRadius(AndroidUtilities.dp(12));
            setBackground(bg);

            dotPaint.setColor(accentColor);

            View dotView = new View(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    dotPaint.setAlpha((int) (dotPulseAlpha * 255));
                    canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, dotPaint);
                }
            };
            addView(dotView, LayoutHelper.createLinear(7, 7, Gravity.CENTER_VERTICAL, 0, 0, 8, 0));

            TextView textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            textView.setTextColor(Color.argb(220, 255, 255, 255));
            textView.setTypeface(AndroidUtilities.bold());
            textView.setText(text);
            addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            ValueAnimator pulseAnim = ValueAnimator.ofFloat(0.3f, 1.0f);
            pulseAnim.setDuration(900);
            pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
            pulseAnim.setRepeatMode(ValueAnimator.REVERSE);
            pulseAnim.addUpdateListener(animation -> {
                dotPulseAlpha = (float) animation.getAnimatedValue();
                dotView.invalidate();
            });
            pulseAnim.start();
        }
    }
}
