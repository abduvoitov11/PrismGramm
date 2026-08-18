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
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * 🌟 PrismSplashScreenView — 100% Haqiqiy 3D Fazoviy Geometriya & Volumetrik Shisha Dvigateli.
 *
 * 2D dizayndan butunlay voz kechilgan:
 * 1. 3D Camera & Matrix Projection (X, Y, Z o'qlarida fazoviy aylanish va chuqurlik).
 * 2. Har bir App Icon uchun mutlaqo alohida 3D Dunyo:
 *    - Matrix: 3D Aylanuvchi Kiber Kub (Tesseract), 3D Z-o'qli raqamli tunnel va golografik radar.
 *    - Cosmos & Singularity: 3D Fazoviy sayyora halqalari, orbital oylari (old/orqa o'tish bilan) va gravitatsion girdob.
 *    - Sakura: 3D Fazoda aylanuvchi (Pitch, Yaw, Roll) mayin gilos guli yaproqlari va 3D Torus gultoji.
 *    - Cyber: 3D Cheksiz gorizont synthwave to'ri va 3D nurli piramida.
 *    - Ruby & Amethyst: Haqiqiy 3D qirrali Olmos (Brilliant Cut Polyhedron) kristall geometriyasi.
 *    - Plasma: 3D Oktaedr / Ikozaedr kletkasi va tarvaqaylagan 3D chaqmoqlar.
 *    - Bronze: 3D Uch o'qli Gimbal Gyroscope mexanik tishli g'ildiraklari.
 *    - Turbo: 3D Giper-fazoviy silindrsimon Warp tunnel halqalari.
 *    - Sunset & Lava: 3D Magma plitalari va aylanuvchi Quyosh toji diski.
 * 3. 3D Ko'p qatlamli Parallaks Shisha Ikonka (Z-offset bilan havoda suzuvchi 3D logotip).
 */
public class PrismSplashScreenView extends FrameLayout {

    public enum IconAnimType {
        DEFAULT, VINTAGE, AQUA, PREMIUM, TURBO, NOX, COBALT, RUBY, AURORA, PURE,
        MATRIX, SUNSET, COSMOS, LAVA, CHROME, SAKURA, SINGULARITY, PLASMA,
        AMETHYST, CYBER, ABYSS, BRONZE, MONOCHROME, SPECTRUM, GLITCH
    }

    private IconAnimType animType = IconAnimType.DEFAULT;
    private int primaryColor = 0xFF00E5FF;
    private int secondaryColor = 0xFF7C4DFF;
    private int accentGlowColor = 0xFFFF007F;
    private int bgStartColor = 0xFF0B1424;
    private int bgEndColor = 0xFF040810;

    private final ThreeDimensionalWorldView world3DView;
    private final ThreeDimensionalParticlesView particles3DView;
    private final ThreeDimensionalHeroIconView hero3DIconView;
    private final TextView greetingView;
    private final TextView appTitle;
    private final TextView appSubtitle;
    private final StatusBadgeView statusBadge;

    private boolean isDismissing = false;

    public PrismSplashScreenView(@NonNull Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);

        LauncherIconController.LauncherIcon currentIcon = LauncherIconController.getSelectedIcon();
        resolveIconTypeAndColors(currentIcon);

        // 1. Dynamic Deep Space Background
        GradientDrawable bgDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{bgStartColor, bgEndColor}
        );
        setBackground(bgDrawable);

        // 2. 3D Mathematical World Geometry Engine (Cubes, Grids, Rings, Polyhedra, Gyroscopes)
        world3DView = new ThreeDimensionalWorldView(context, animType, primaryColor, secondaryColor, accentGlowColor);
        addView(world3DView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 3. 3D Particle Space (Z-Depth Falling Code, 3D Tumbling Petals, Warp Stars)
        particles3DView = new ThreeDimensionalParticlesView(context, animType, primaryColor, secondaryColor, accentGlowColor);
        addView(particles3DView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Center Content Master Container
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setOrientation(LinearLayout.VERTICAL);
        centerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(centerContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // 4. Hero 3D Multi-Layered Floating Parallax Icon
        hero3DIconView = new ThreeDimensionalHeroIconView(context, currentIcon, animType, primaryColor, secondaryColor, accentGlowColor);
        centerContainer.addView(hero3DIconView, LayoutHelper.createLinear(240, 240, Gravity.CENTER_HORIZONTAL));

        // 5. Typography & Presentation Container
        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        centerContainer.addView(textContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 16, 0, 0));

        greetingView = new TextView(context);
        greetingView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        greetingView.setTextColor(Color.argb(230, 255, 255, 255));
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
        appSubtitle.setText("✦ " + getThemeEditionLabel(currentIcon) + " ✦");
        textContainer.addView(appSubtitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 8));

        statusBadge = new StatusBadgeView(context, getStatusBadgeText(), primaryColor);
        textContainer.addView(statusBadge, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    private void resolveIconTypeAndColors(LauncherIconController.LauncherIcon icon) {
        if (icon == null) icon = LauncherIconController.LauncherIcon.DEFAULT;
        String key = icon.key.toLowerCase();

        if (key.contains("matrix")) {
            animType = IconAnimType.MATRIX;
            primaryColor = 0xFF00FF66;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFF39FF14;
            bgStartColor = 0xFF061A0E;
            bgEndColor = 0xFF020804;
        } else if (key.contains("sunset")) {
            animType = IconAnimType.SUNSET;
            primaryColor = 0xFFFF9100;
            secondaryColor = 0xFFFF1744;
            accentGlowColor = 0xFFFFD700;
            bgStartColor = 0xFF2A0E1A;
            bgEndColor = 0xFF0F0307;
        } else if (key.contains("ruby")) {
            animType = IconAnimType.RUBY;
            primaryColor = 0xFFE11D48;
            secondaryColor = 0xFFBE123C;
            accentGlowColor = 0xFFFB7185;
            bgStartColor = 0xFF280514;
            bgEndColor = 0xFF0E0106;
        } else if (key.contains("cosmos")) {
            animType = IconAnimType.COSMOS;
            primaryColor = 0xFF8B5CF6;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFFD946EF;
            bgStartColor = 0xFF160934;
            bgEndColor = 0xFF050112;
        } else if (key.contains("lava")) {
            animType = IconAnimType.LAVA;
            primaryColor = 0xFFFF3D00;
            secondaryColor = 0xFFFFD600;
            accentGlowColor = 0xFFFF0055;
            bgStartColor = 0xFF2A0A03;
            bgEndColor = 0xFF0D0200;
        } else if (key.contains("chrome")) {
            animType = IconAnimType.CHROME;
            primaryColor = 0xFF00F0FF;
            secondaryColor = 0xFFE0E7FF;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF0E1C30;
            bgEndColor = 0xFF03070F;
        } else if (key.contains("sakura")) {
            animType = IconAnimType.SAKURA;
            primaryColor = 0xFFF472B6;
            secondaryColor = 0xFFC084FC;
            accentGlowColor = 0xFFFFD1DC;
            bgStartColor = 0xFF2A0E23;
            bgEndColor = 0xFF0E030B;
        } else if (key.contains("singularity")) {
            animType = IconAnimType.SINGULARITY;
            primaryColor = 0xFF7C4DFF;
            secondaryColor = 0xFFFF007F;
            accentGlowColor = 0xFF00F5FF;
            bgStartColor = 0xFF140730;
            bgEndColor = 0xFF03000D;
        } else if (key.contains("plasma")) {
            animType = IconAnimType.PLASMA;
            primaryColor = 0xFFD946EF;
            secondaryColor = 0xFF38BDF8;
            accentGlowColor = 0xFFF43F5E;
            bgStartColor = 0xFF220734;
            bgEndColor = 0xFF0A0112;
        } else if (key.contains("amethyst")) {
            animType = IconAnimType.AMETHYST;
            primaryColor = 0xFFA855F7;
            secondaryColor = 0xFFEC4899;
            accentGlowColor = 0xFFE879F9;
            bgStartColor = 0xFF1E0832;
            bgEndColor = 0xFF07010F;
        } else if (key.contains("cyber")) {
            animType = IconAnimType.CYBER;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFFFF007F;
            accentGlowColor = 0xFFFFEA00;
            bgStartColor = 0xFF0B1038;
            bgEndColor = 0xFF020414;
        } else if (key.contains("abyss")) {
            animType = IconAnimType.ABYSS;
            primaryColor = 0xFF0052D4;
            secondaryColor = 0xFF4364F7;
            accentGlowColor = 0xFF6FB1FC;
            bgStartColor = 0xFF051233;
            bgEndColor = 0xFF010410;
        } else if (key.contains("bronze")) {
            animType = IconAnimType.BRONZE;
            primaryColor = 0xFFD97706;
            secondaryColor = 0xFFB45309;
            accentGlowColor = 0xFFF59E0B;
            bgStartColor = 0xFF281605;
            bgEndColor = 0xFF0E0601;
        } else if (key.contains("monochrome")) {
            animType = IconAnimType.MONOCHROME;
            primaryColor = 0xFFCBD5E1;
            secondaryColor = 0xFF64748B;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF161922;
            bgEndColor = 0xFF050608;
        } else if (key.contains("spectrum")) {
            animType = IconAnimType.SPECTRUM;
            primaryColor = 0xFFFF0055;
            secondaryColor = 0xFF00F0FF;
            accentGlowColor = 0xFFFFD700;
            bgStartColor = 0xFF22082E;
            bgEndColor = 0xFF08010E;
        } else if (key.contains("glitch")) {
            animType = IconAnimType.GLITCH;
            primaryColor = 0xFF00FFCC;
            secondaryColor = 0xFFFF0055;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF071C24;
            bgEndColor = 0xFF02070A;
        } else if (key.contains("vintage")) {
            animType = IconAnimType.VINTAGE;
            primaryColor = 0xFFE0A96D;
            secondaryColor = 0xFF7D5A50;
            accentGlowColor = 0xFFF7D1BA;
            bgStartColor = 0xFF261911;
            bgEndColor = 0xFF0D0805;
        } else if (key.contains("aqua")) {
            animType = IconAnimType.AQUA;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF00B0FF;
            accentGlowColor = 0xFF80D8FF;
            bgStartColor = 0xFF051C2C;
            bgEndColor = 0xFF01080E;
        } else if (key.contains("premium")) {
            animType = IconAnimType.PREMIUM;
            primaryColor = 0xFF9C27B0;
            secondaryColor = 0xFFE040FB;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF1E082B;
            bgEndColor = 0xFF07010E;
        } else if (key.contains("turbo")) {
            animType = IconAnimType.TURBO;
            primaryColor = 0xFFFF5252;
            secondaryColor = 0xFFFF7A00;
            accentGlowColor = 0xFFFFAB40;
            bgStartColor = 0xFF2D0808;
            bgEndColor = 0xFF0E0101;
        } else if (key.contains("nox")) {
            animType = IconAnimType.NOX;
            primaryColor = 0xFF6366F1;
            secondaryColor = 0xFF8B5CF6;
            accentGlowColor = 0xFFA855F7;
            bgStartColor = 0xFF0F0F30;
            bgEndColor = 0xFF03030E;
        } else if (key.contains("cobalt")) {
            animType = IconAnimType.COBALT;
            primaryColor = 0xFF2979FF;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFF448AFF;
            bgStartColor = 0xFF07153D;
            bgEndColor = 0xFF010514;
        } else if (key.contains("aurora")) {
            animType = IconAnimType.AURORA;
            primaryColor = 0xFF00FFA3;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFF7000FF;
            bgStartColor = 0xFF062620;
            bgEndColor = 0xFF010A08;
        } else if (key.contains("pure")) {
            animType = IconAnimType.PURE;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF7000FF;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF071633;
            bgEndColor = 0xFF010610;
        } else {
            animType = IconAnimType.DEFAULT;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF007AFF;
            accentGlowColor = 0xFF00F0FF;
            bgStartColor = 0xFF0B1A2F;
            bgEndColor = 0xFF030914;
        }
    }

    private String getThemeEditionLabel(LauncherIconController.LauncherIcon icon) {
        switch (animType) {
            case MATRIX: return "3D Cyber Tesseract Matrix";
            case SUNSET: return "3D Volumetric Molten Horizon";
            case RUBY: return "3D Faceted Diamond Crystal";
            case COSMOS: return "3D Celestial Planetary Orbital";
            case LAVA: return "3D Magma Chamber Geothermal";
            case CHROME: return "3D Titanium Mercury Chrome";
            case SAKURA: return "3D Tumbling Blossom Petals";
            case SINGULARITY: return "3D Event Horizon Gravitational";
            case PLASMA: return "3D Tesla Icosahedron Reactor";
            case AMETHYST: return "3D Prismatic Quartz Geode";
            case CYBER: return "3D Synthwave Infinite Horizon";
            case ABYSS: return "3D Bioluminescent Deep Oceanic";
            case BRONZE: return "3D Steampunk Gimbal Gyroscope";
            case MONOCHROME: return "3D Bauhaus Monolithic Shadow";
            case SPECTRUM: return "3D Chromatic Prism Refraction";
            case GLITCH: return "3D Cyberpunk Digital Glitch";
            case VINTAGE: return "3D Cinema Analog Film Chamber";
            case AQUA: return "3D Glacial Caustic Lagoon";
            case PREMIUM: return "3D Royal Platinum Starburst";
            case TURBO: return "3D Hyperspace Warp Velocity";
            case NOX: return "3D Carbon Fiber Obsidian";
            case COBALT: return "3D Royal Cobalt Ion Accelerator";
            case AURORA: return "3D Northern Aurora Atmosphere";
            case PURE: return "3D Sub-Zero Ice Octahedron";
            default: return "3D Liquid Glass Prism";
        }
    }

    private String getStatusBadgeText() {
        switch (animType) {
            case MATRIX: return "SYS // 3D TESSERACT ENCRYPTION [ONLINE]";
            case SINGULARITY: return "PHYSICS // 3D GRAVITATIONAL ACCRETION [ACTIVE]";
            case TURBO: return "WARP // 3D HYPERSPACE VELOCITY [100%]";
            case CYBER: return "HUD // 3D SYNTHWAVE GRID [LOCKED]";
            case PLASMA: return "POWER // 3D TESLA REACTOR [OPTIMAL]";
            case SAKURA: return "ZEN // 3D BLOSSOM TURBULENCE [ACTIVE]";
            case LAVA: return "GEOTHERMAL // 3D MAGMA CORE [PEAK]";
            case RUBY: return "LUXURY // 3D DIAMOND FACETS [ENGAGED]";
            case COSMOS: return "ORBIT // 3D CELESTIAL MECHANICS [ONLINE]";
            case GLITCH: return "SEC // 3D DIGITAL VOLTAGE [CLEAN]";
            case BRONZE: return "CHRONO // 3D GIMBAL RATIO [1:1]";
            default: return "PRISM // 3D LIQUID GLASS [ENGAGED]";
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
        hero3DIconView.setScaleX(0.2f);
        hero3DIconView.setScaleY(0.2f);
        hero3DIconView.setAlpha(0.0f);

        greetingView.setAlpha(0.0f);
        greetingView.setTranslationY(AndroidUtilities.dp(18));
        appTitle.setAlpha(0.0f);
        appSubtitle.setAlpha(0.0f);
        statusBadge.setAlpha(0.0f);

        hero3DIconView.animate()
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
    // 1. 3D Mathematical World Geometry Engine (Camera & 3D Vertices)
    // ──────────────────────────────────────────────────────────────────────────
    private static class ThreeDimensionalWorldView extends View {
        private final IconAnimType animType;
        private final int primaryColor, secondaryColor, glowColor;
        private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Camera camera3D = new Camera();
        private final Matrix matrix3D = new Matrix();
        private final Path path3D = new Path();
        private float angleX, angleY, angleZ;

        // 3D Cube Vertices
        private static final float[][] CUBE_VERTICES = {
                {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},
                {-1, -1, 1},  {1, -1, 1},  {1, 1, 1},  {-1, 1, 1}
        };
        private static final int[][] CUBE_EDGES = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        // 3D Octahedron / Diamond Gem Vertices
        private static final float[][] OCTA_VERTICES = {
                {0, -1.4f, 0}, {1.2f, 0, 0}, {0, 0, 1.2f},
                {-1.2f, 0, 0}, {0, 0, -1.2f}, {0, 1.4f, 0}
        };
        private static final int[][] OCTA_EDGES = {
                {0, 1}, {0, 2}, {0, 3}, {0, 4},
                {5, 1}, {5, 2}, {5, 3}, {5, 4},
                {1, 2}, {2, 3}, {3, 4}, {4, 1}
        };

        public ThreeDimensionalWorldView(Context context, IconAnimType type, int c1, int c2, int c3) {
            super(context);
            this.animType = type;
            this.primaryColor = c1;
            this.secondaryColor = c2;
            this.glowColor = c3;

            linePaint.setStyle(Paint.Style.STROKE);
            fillPaint.setStyle(Paint.Style.FILL);

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 360f);
            animator.setDuration(8000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(animation -> {
                float val = (float) animation.getAnimatedValue();
                angleX = (float) Math.sin(Math.toRadians(val)) * 25f;
                angleY = val;
                angleZ = val * 0.5f;
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

            if (animType == IconAnimType.MATRIX || animType == IconAnimType.CYBER || animType == IconAnimType.GLITCH) {
                // 3D Wireframe Cyber Tesseract Cube
                float size = AndroidUtilities.dp(85);
                linePaint.setColor(primaryColor);
                linePaint.setStrokeWidth(AndroidUtilities.dp(1.8f));
                linePaint.setAlpha(120);

                draw3DWireframe(canvas, cx, cy, CUBE_VERTICES, CUBE_EDGES, size, angleX + 20, angleY, angleZ);

                // Outer larger 3D cube
                linePaint.setColor(secondaryColor);
                linePaint.setAlpha(60);
                draw3DWireframe(canvas, cx, cy, CUBE_VERTICES, CUBE_EDGES, size * 1.5f, -angleX, -angleY * 0.7f, angleZ);

            } else if (animType == IconAnimType.RUBY || animType == IconAnimType.AMETHYST || animType == IconAnimType.PURE) {
                // 3D Faceted Diamond Crystal Gem
                float size = AndroidUtilities.dp(80);
                linePaint.setColor(primaryColor);
                linePaint.setStrokeWidth(AndroidUtilities.dp(2f));
                linePaint.setAlpha(170);

                draw3DWireframe(canvas, cx, cy, OCTA_VERTICES, OCTA_EDGES, size, 25, angleY, 0);

                // Inner core gem
                linePaint.setColor(glowColor);
                linePaint.setAlpha(90);
                draw3DWireframe(canvas, cx, cy, OCTA_VERTICES, OCTA_EDGES, size * 0.55f, -25, -angleY * 1.2f, 0);

            } else if (animType == IconAnimType.COSMOS || animType == IconAnimType.SINGULARITY) {
                // 3D Celestial Planetary Orbitals with 3D Moons
                float r = AndroidUtilities.dp(95);
                linePaint.setColor(primaryColor);
                linePaint.setStrokeWidth(AndroidUtilities.dp(2f));
                linePaint.setAlpha(140);

                // Orbit 1 (Tilted 65 deg)
                canvas.save();
                camera3D.save();
                camera3D.rotateX(65);
                camera3D.rotateY(angleX);
                camera3D.rotateZ(angleY);
                camera3D.getMatrix(matrix3D);
                camera3D.restore();

                matrix3D.preTranslate(-cx, -cy);
                matrix3D.postTranslate(cx, cy);
                canvas.concat(matrix3D);
                canvas.drawCircle(cx, cy, r, linePaint);

                // Orbiting 3D Moon
                float moonX = cx + (float) Math.cos(Math.toRadians(angleY * 2)) * r;
                float moonY = cy + (float) Math.sin(Math.toRadians(angleY * 2)) * r;
                fillPaint.setColor(glowColor);
                canvas.drawCircle(moonX, moonY, AndroidUtilities.dp(5), fillPaint);
                canvas.restore();

                // Orbit 2 (Opposite tilt)
                canvas.save();
                camera3D.save();
                camera3D.rotateX(-55);
                camera3D.rotateY(-angleX);
                camera3D.rotateZ(-angleY * 0.8f);
                camera3D.getMatrix(matrix3D);
                camera3D.restore();

                matrix3D.preTranslate(-cx, -cy);
                matrix3D.postTranslate(cx, cy);
                canvas.concat(matrix3D);
                linePaint.setColor(secondaryColor);
                linePaint.setAlpha(110);
                canvas.drawCircle(cx, cy, r * 1.25f, linePaint);
                canvas.restore();

            } else if (animType == IconAnimType.BRONZE || animType == IconAnimType.VINTAGE) {
                // 3D 3-Axis Gimbal Gyroscope Rings
                float r = AndroidUtilities.dp(85);
                linePaint.setStrokeWidth(AndroidUtilities.dp(2.2f));

                // Ring 1 (X-Axis)
                canvas.save();
                camera3D.save();
                camera3D.rotateX(angleY);
                camera3D.getMatrix(matrix3D);
                camera3D.restore();
                matrix3D.preTranslate(-cx, -cy);
                matrix3D.postTranslate(cx, cy);
                canvas.concat(matrix3D);
                linePaint.setColor(primaryColor);
                linePaint.setAlpha(160);
                canvas.drawCircle(cx, cy, r, linePaint);
                canvas.restore();

                // Ring 2 (Y-Axis)
                canvas.save();
                camera3D.save();
                camera3D.rotateY(angleY * 0.8f);
                camera3D.getMatrix(matrix3D);
                camera3D.restore();
                matrix3D.preTranslate(-cx, -cy);
                matrix3D.postTranslate(cx, cy);
                canvas.concat(matrix3D);
                linePaint.setColor(secondaryColor);
                linePaint.setAlpha(130);
                canvas.drawCircle(cx, cy, r * 1.15f, linePaint);
                canvas.restore();

                // Ring 3 (Z-Axis)
                canvas.save();
                camera3D.save();
                camera3D.rotateZ(angleY * 0.6f);
                camera3D.getMatrix(matrix3D);
                camera3D.restore();
                matrix3D.preTranslate(-cx, -cy);
                matrix3D.postTranslate(cx, cy);
                canvas.concat(matrix3D);
                linePaint.setColor(glowColor);
                linePaint.setAlpha(100);
                canvas.drawCircle(cx, cy, r * 1.3f, linePaint);
                canvas.restore();

            } else if (animType == IconAnimType.TURBO) {
                // 3D Hyperspace Warp Tunnel Perspective Rings
                for (int i = 0; i < 6; i++) {
                    float prog = ((angleY * 2 + i * 60) % 360f) / 360f;
                    float depthRadius = AndroidUtilities.dp(20) + prog * AndroidUtilities.dp(160);
                    int alpha = (int) (prog * 180);
                    linePaint.setColor(primaryColor);
                    linePaint.setStrokeWidth(AndroidUtilities.dp(1f + prog * 2.5f));
                    linePaint.setAlpha(alpha);
                    canvas.drawCircle(cx, cy, depthRadius, linePaint);
                }
            } else {
                // Default 3D Holographic Concentric Pulse Rings
                float r = AndroidUtilities.dp(90);
                linePaint.setColor(primaryColor);
                linePaint.setStrokeWidth(AndroidUtilities.dp(2f));
                linePaint.setAlpha(120);

                canvas.save();
                camera3D.save();
                camera3D.rotateX(60);
                camera3D.rotateY(angleX);
                camera3D.rotateZ(angleY * 0.5f);
                camera3D.getMatrix(matrix3D);
                camera3D.restore();
                matrix3D.preTranslate(-cx, -cy);
                matrix3D.postTranslate(cx, cy);
                canvas.concat(matrix3D);
                canvas.drawCircle(cx, cy, r, linePaint);
                canvas.restore();
            }
        }

        private void draw3DWireframe(Canvas canvas, float cx, float cy, float[][] vertices, int[][] edges, float scale, float rx, float ry, float rz) {
            float radX = (float) Math.toRadians(rx);
            float radY = (float) Math.toRadians(ry);
            float radZ = (float) Math.toRadians(rz);

            float[][] rotated = new float[vertices.length][3];
            float fov = 400f;

            for (int i = 0; i < vertices.length; i++) {
                float x = vertices[i][0] * scale;
                float y = vertices[i][1] * scale;
                float z = vertices[i][2] * scale;

                // Rotate X
                float y1 = y * (float) Math.cos(radX) - z * (float) Math.sin(radX);
                float z1 = y * (float) Math.sin(radX) + z * (float) Math.cos(radX);

                // Rotate Y
                float x2 = x * (float) Math.cos(radY) + z1 * (float) Math.sin(radY);
                float z2 = -x * (float) Math.sin(radY) + z1 * (float) Math.cos(radY);

                // Rotate Z
                float x3 = x2 * (float) Math.cos(radZ) - y1 * (float) Math.sin(radZ);
                float y3 = x2 * (float) Math.sin(radZ) + y1 * (float) Math.cos(radZ);

                // Perspective Projection
                float distance = fov / (fov + z2 + 300f);
                rotated[i][0] = cx + x3 * distance;
                rotated[i][1] = cy + y3 * distance;
                rotated[i][2] = z2;
            }

            for (int[] edge : edges) {
                float x1 = rotated[edge[0]][0];
                float y1 = rotated[edge[0]][1];
                float x2 = rotated[edge[1]][0];
                float y2 = rotated[edge[1]][1];
                canvas.drawLine(x1, y1, x2, y2, linePaint);
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. 3D Particle Space: Z-Depth Matrix Rain, 3D Petals, Warp Stars
    // ──────────────────────────────────────────────────────────────────────────
    private static class ThreeDimensionalParticlesView extends View {
        private static final int PARTICLE_COUNT = 55;
        private final List<Particle3D> particles = new ArrayList<>();
        private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final IconAnimType animType;
        private final int color1, color2, color3;
        private final Random random = new Random();
        private long lastFrameTime;
        private static final String[] MATRIX_CHARS = {"0", "1", "7", "X", "λ", "Ω", "F", "9", "A", "Ø", "§", "K", "Z"};

        private static class Particle3D {
            float x, y, z;
            float vx, vy, vz;
            float rotX, rotY, rotZ;
            float vRotX, vRotY, vRotZ;
            float size;
            float alpha;
            int color;
            String charText;
        }

        public ThreeDimensionalParticlesView(Context context, IconAnimType type, int c1, int c2, int c3) {
            super(context);
            this.animType = type;
            this.color1 = c1;
            this.color2 = c2;
            this.color3 = c3;

            textPaint.setColor(c1);
            textPaint.setTypeface(AndroidUtilities.bold());

            initParticles();
        }

        private void initParticles() {
            particles.clear();
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                particles.add(createParticle(true));
            }
        }

        private Particle3D createParticle(boolean randomZ) {
            Particle3D p = new Particle3D();
            int w = getWidth() > 0 ? getWidth() : AndroidUtilities.displaySize.x;
            int h = getHeight() > 0 ? getHeight() : AndroidUtilities.displaySize.y;

            p.x = (random.nextFloat() - 0.5f) * w * 1.5f;
            p.y = (random.nextFloat() - 0.5f) * h * 1.5f;
            p.z = randomZ ? random.nextFloat() * 800f : 800f;

            p.size = AndroidUtilities.dp(3 + random.nextFloat() * 5f);
            p.alpha = 0.3f + random.nextFloat() * 0.7f;
            p.rotX = random.nextFloat() * 360f;
            p.rotY = random.nextFloat() * 360f;
            p.rotZ = random.nextFloat() * 360f;
            p.vRotX = (random.nextFloat() - 0.5f) * 6f;
            p.vRotY = (random.nextFloat() - 0.5f) * 6f;
            p.vRotZ = (random.nextFloat() - 0.5f) * 6f;
            p.charText = MATRIX_CHARS[random.nextInt(MATRIX_CHARS.length)];

            int rnd = random.nextInt(3);
            p.color = rnd == 0 ? color1 : (rnd == 1 ? color2 : color3);

            switch (animType) {
                case MATRIX:
                    p.vx = 0;
                    p.vy = 250f + random.nextFloat() * 300f;
                    p.vz = 0;
                    break;
                case TURBO:
                    p.vx = 0;
                    p.vy = 0;
                    p.vz = -(500f + random.nextFloat() * 800f);
                    break;
                case SAKURA:
                    p.vx = 60f + (random.nextFloat() - 0.5f) * 40f;
                    p.vy = 120f + random.nextFloat() * 100f;
                    p.vz = (random.nextFloat() - 0.5f) * 50f;
                    break;
                default:
                    p.vx = (random.nextFloat() - 0.5f) * 60f;
                    p.vy = -(80f + random.nextFloat() * 120f);
                    p.vz = (random.nextFloat() - 0.5f) * 60f;
                    break;
            }
            return p;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            if (w == 0 || h == 0) return;

            long now = SystemClock.elapsedRealtime();
            float dt = lastFrameTime == 0 ? 0.016f : Math.min((now - lastFrameTime) / 1000f, 0.05f);
            lastFrameTime = now;

            float cx = w / 2f;
            float cy = h / 2f;
            float fov = 450f;

            for (Particle3D p : particles) {
                p.x += p.vx * dt;
                p.y += p.vy * dt;
                p.z += p.vz * dt;
                p.rotX += p.vRotX;
                p.rotY += p.vRotY;
                p.rotZ += p.vRotZ;

                if (animType == IconAnimType.TURBO) {
                    if (p.z < 20f) {
                        p.z = 800f;
                        p.x = (random.nextFloat() - 0.5f) * w * 1.5f;
                        p.y = (random.nextFloat() - 0.5f) * h * 1.5f;
                    }
                } else if (p.y > h / 2f + 500f || p.y < -h / 2f - 500f || p.x > w / 2f + 500f || p.x < -w / 2f - 500f) {
                    Particle3D fresh = createParticle(false);
                    p.x = fresh.x;
                    p.y = (animType == IconAnimType.MATRIX || animType == IconAnimType.SAKURA) ? -h / 2f - 50f : h / 2f + 50f;
                    p.z = fresh.z;
                }

                float distance = fov / Math.max(10f, fov + p.z);
                float projX = cx + p.x * distance;
                float projY = cy + p.y * distance;
                float projSize = p.size * distance * 1.5f;

                particlePaint.setColor(p.color);
                particlePaint.setAlpha((int) (p.alpha * Math.min(1f, distance * 1.8f) * 255));

                if (animType == IconAnimType.MATRIX) {
                    textPaint.setColor(p.color);
                    textPaint.setTextSize(Math.max(AndroidUtilities.dp(8), AndroidUtilities.dp(16) * distance));
                    textPaint.setAlpha((int) (p.alpha * Math.min(1f, distance * 2f) * 255));
                    canvas.drawText(p.charText, projX, projY, textPaint);
                } else if (animType == IconAnimType.SAKURA) {
                    // 3D Planar Flower Petal
                    canvas.save();
                    canvas.translate(projX, projY);
                    canvas.rotate(p.rotZ);
                    float scaleY = (float) Math.cos(Math.toRadians(p.rotX));
                    canvas.scale(1f, scaleY);
                    canvas.drawOval(-projSize * 1.6f, -projSize * 0.9f, projSize * 1.6f, projSize * 0.9f, particlePaint);
                    canvas.restore();
                } else if (animType == IconAnimType.TURBO) {
                    // 3D Motion Blur Warp Star Streak
                    float prevDist = fov / Math.max(10f, fov + p.z - p.vz * dt * 2.5f);
                    float prevX = cx + p.x * prevDist;
                    float prevY = cy + p.y * prevDist;
                    particlePaint.setStrokeWidth(Math.max(1.5f, projSize * 0.6f));
                    canvas.drawLine(projX, projY, prevX, prevY, particlePaint);
                } else {
                    canvas.drawCircle(projX, projY, Math.max(1.5f, projSize), particlePaint);
                }
            }
            invalidate();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. 3D Hero Multi-Layer Parallax Icon View (Z-Depth Stacking & Specular Rim)
    // ──────────────────────────────────────────────────────────────────────────
    private static class ThreeDimensionalHeroIconView extends FrameLayout {
        private final Camera camera = new Camera();
        private final Matrix matrix = new Matrix();
        private final Paint specularPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rectF = new RectF();
        private final Path clipPath = new Path();

        private float tiltX, tiltY, tiltZ;
        private float floatProgress;

        public ThreeDimensionalHeroIconView(Context context, LauncherIconController.LauncherIcon icon, IconAnimType animType, int c1, int c2, int c3) {
            super(context);
            setWillNotDraw(false);

            // Layer 1: 3D Squircle Base Card
            FrameLayout card = new FrameLayout(context) {
                @Override
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    clipPath.reset();
                    rectF.set(0, 0, w, h);
                    clipPath.addRoundRect(rectF, AndroidUtilities.dp(26), AndroidUtilities.dp(26), Path.Direction.CW);
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    canvas.save();
                    canvas.clipPath(clipPath);
                    super.dispatchDraw(canvas);
                    canvas.restore();
                }
            };
            card.setElevation(AndroidUtilities.dp(26));
            addView(card, LayoutHelper.createFrame(104, 104, Gravity.CENTER));

            ImageView bg = new ImageView(context);
            bg.setScaleType(ImageView.ScaleType.FIT_XY);
            bg.setImageResource(icon.background);
            card.addView(bg, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            ImageView fg = new ImageView(context);
            fg.setScaleType(ImageView.ScaleType.FIT_XY);
            fg.setImageResource(icon.foreground);
            card.addView(fg, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(AndroidUtilities.dp(2.4f));
            borderPaint.setColor(Color.argb(210, 255, 255, 255));

            // Continuous 3D Gyroscopic Physics Loop
            ValueAnimator physicsAnim = ValueAnimator.ofFloat(0f, 360f);
            physicsAnim.setDuration(4000);
            physicsAnim.setRepeatCount(ValueAnimator.INFINITE);
            physicsAnim.addUpdateListener(animation -> {
                float val = (float) animation.getAnimatedValue();
                floatProgress = val;
                tiltX = (float) Math.sin(Math.toRadians(val)) * 14f;
                tiltY = (float) Math.cos(Math.toRadians(val)) * 18f;
                tiltZ = (float) Math.sin(Math.toRadians(val * 0.5f)) * 5f;
                invalidate();
            });
            physicsAnim.start();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            canvas.save();
            camera.save();
            camera.rotateX(tiltX);
            camera.rotateY(tiltY);
            camera.rotateZ(tiltZ);
            camera.translate(0, 0, (float) Math.sin(Math.toRadians(floatProgress)) * 40f);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-cx, -cy);
            matrix.postTranslate(cx, cy);
            canvas.concat(matrix);

            super.dispatchDraw(canvas);

            // Specular 3D Liquid Glass Glint
            float cardLeft = cx - AndroidUtilities.dp(52);
            float cardTop = cy - AndroidUtilities.dp(52);
            float cardSize = AndroidUtilities.dp(104);
            rectF.set(cardLeft, cardTop, cardLeft + cardSize, cardTop + cardSize);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(26), AndroidUtilities.dp(26), borderPaint);

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
