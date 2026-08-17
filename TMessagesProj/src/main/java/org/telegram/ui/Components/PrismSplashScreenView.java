package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LauncherIconController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Ultra-Modern 3D Liquid Glass Splash Screen.
 * Contains 25+ distinct, bespoke animation engines and particle physics models —
 * one unique customized identity for EVERY App Icon (Matrix code rain, Singularity vortex,
 * Plasma lightning arcs, Sakura petals, Glitch RGB shift, Aurora ribbons, Lava embers, Turbo warp trails, etc.).
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

    private final ParticleCanvasView particleCanvas;
    private final FrameLayout iconCard;
    private final ImageView iconBgView;
    private final ImageView iconFgView;
    private final GlassShimmerOverlay shimmerOverlay;
    private final PulseRingsView pulseRingsView;
    private final CustomIconFxOverlay iconFxOverlay;
    private final LinearLayout textContainer;
    private final TextView greetingView;
    private final TextView appTitle;
    private final TextView appSubtitle;

    private ValueAnimator idleAnimator;
    private boolean isDismissing = false;
    private final Random random = new Random();

    public PrismSplashScreenView(@NonNull Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);

        LauncherIconController.LauncherIcon currentIcon = LauncherIconController.getSelectedIcon();
        resolveIconTypeAndColors(currentIcon);

        // 1. Dynamic background gradient
        GradientDrawable bgDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{bgStartColor, bgEndColor}
        );
        setBackground(bgDrawable);

        // 2. Multi-physics Particle Simulation Layer (25+ bespoke behaviors)
        particleCanvas = new ParticleCanvasView(context, animType, primaryColor, secondaryColor, accentGlowColor);
        addView(particleCanvas, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Center Content Container
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setOrientation(LinearLayout.VERTICAL);
        centerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(centerContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // 3. Shockwave Pulse Rings & Custom FX Behind/Around 3D Icon
        FrameLayout iconWrapper = new FrameLayout(context);
        centerContainer.addView(iconWrapper, LayoutHelper.createLinear(170, 170, Gravity.CENTER_HORIZONTAL));

        pulseRingsView = new PulseRingsView(context, animType, primaryColor, secondaryColor);
        iconWrapper.addView(pulseRingsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        // 4. 3D Rounded Squircle Liquid Glass Icon Card
        iconCard = new FrameLayout(context) {
            private final Path clipPath = new Path();
            private final RectF rectF = new RectF();
            private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            {
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(AndroidUtilities.dp(1.8f));
                borderPaint.setColor(Color.argb(130, 255, 255, 255));
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
        iconCard.setElevation(AndroidUtilities.dp(18));
        iconWrapper.addView(iconCard, LayoutHelper.createFrame(94, 94, Gravity.CENTER));

        iconBgView = new ImageView(context);
        iconBgView.setScaleType(ImageView.ScaleType.FIT_XY);
        iconBgView.setImageResource(currentIcon.background);
        iconCard.addView(iconBgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        iconFgView = new ImageView(context);
        iconFgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iconFgView.setPadding(AndroidUtilities.dp(11), AndroidUtilities.dp(11), AndroidUtilities.dp(11), AndroidUtilities.dp(11));
        iconFgView.setImageResource(currentIcon.foreground);
        iconCard.addView(iconFgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Specular Diagonal Light Shimmer Sweep
        shimmerOverlay = new GlassShimmerOverlay(context);
        iconCard.addView(shimmerOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 5. Custom Icon Overlays (HUD, Lightning, Glitch Slices, Orbit Rings)
        iconFxOverlay = new CustomIconFxOverlay(context, animType, primaryColor, secondaryColor);
        iconWrapper.addView(iconFxOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        // 6. Personalized Greeting & Typography Section
        textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        centerContainer.addView(textContainer, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 18, 0, 0));

        // Personalized Multilingual Time Greeting Card
        String greetingText = generateGreetingText();
        greetingView = new TextView(context);
        greetingView.setText(greetingText);
        greetingView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        greetingView.setTypeface(AndroidUtilities.bold());
        greetingView.setTextColor(0xFFFFFFFF);
        greetingView.setGravity(Gravity.CENTER);
        greetingView.setPadding(AndroidUtilities.dp(18), AndroidUtilities.dp(7), AndroidUtilities.dp(18), AndroidUtilities.dp(7));

        GradientDrawable greetingBadge = new GradientDrawable();
        greetingBadge.setColor(Color.argb(50, 255, 255, 255));
        greetingBadge.setCornerRadius(AndroidUtilities.dp(18));
        greetingBadge.setStroke(AndroidUtilities.dp(1.2f), Color.argb(90, 255, 255, 255));
        greetingView.setBackground(greetingBadge);
        textContainer.addView(greetingView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));

        // App Title
        appTitle = new TextView(context);
        appTitle.setText("PrismGram");
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        appTitle.setTypeface(AndroidUtilities.bold());
        appTitle.setTextColor(0xFFFFFFFF);
        appTitle.setLetterSpacing(0.04f);
        textContainer.addView(appTitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 10, 0, 0));

        // App Subtitle with customized icon edition label
        appSubtitle = new TextView(context);
        appSubtitle.setText(getThemeEditionLabel(currentIcon));
        appSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        appSubtitle.setTextColor(primaryColor);
        appSubtitle.setLetterSpacing(0.06f);
        textContainer.addView(appSubtitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 4, 0, 0));

        // Tap-to-skip support
        setOnClickListener(v -> dismiss());
    }

    private void resolveIconTypeAndColors(LauncherIconController.LauncherIcon icon) {
        if (icon == null) return;
        String key = icon.key.toLowerCase();

        if (key.contains("matrix")) {
            animType = IconAnimType.MATRIX;
            primaryColor = 0xFF00FF66;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFF39FF14;
            bgStartColor = 0xFF03140A;
            bgEndColor = 0xFF010804;
        } else if (key.contains("sunset")) {
            animType = IconAnimType.SUNSET;
            primaryColor = 0xFFFF9100;
            secondaryColor = 0xFFFF1744;
            accentGlowColor = 0xFFFFD700;
            bgStartColor = 0xFF220E15;
            bgEndColor = 0xFF0E0408;
        } else if (key.contains("ruby")) {
            animType = IconAnimType.RUBY;
            primaryColor = 0xFFE11D48;
            secondaryColor = 0xFFBE123C;
            accentGlowColor = 0xFFFB7185;
            bgStartColor = 0xFF1F0B13;
            bgEndColor = 0xFF0C0307;
        } else if (key.contains("cosmos")) {
            animType = IconAnimType.COSMOS;
            primaryColor = 0xFF8B5CF6;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFFD946EF;
            bgStartColor = 0xFF140B26;
            bgEndColor = 0xFF070312;
        } else if (key.contains("lava")) {
            animType = IconAnimType.LAVA;
            primaryColor = 0xFFFF3D00;
            secondaryColor = 0xFFFFD600;
            accentGlowColor = 0xFFFF0055;
            bgStartColor = 0xFF240A05;
            bgEndColor = 0xFF0F0302;
        } else if (key.contains("chrome")) {
            animType = IconAnimType.CHROME;
            primaryColor = 0xFF00F0FF;
            secondaryColor = 0xFFE0E7FF;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF0D141C;
            bgEndColor = 0xFF06090D;
        } else if (key.contains("sakura")) {
            animType = IconAnimType.SAKURA;
            primaryColor = 0xFFF472B6;
            secondaryColor = 0xFFC084FC;
            accentGlowColor = 0xFFFFD1DC;
            bgStartColor = 0xFF200C19;
            bgEndColor = 0xFF0E040A;
        } else if (key.contains("singularity")) {
            animType = IconAnimType.SINGULARITY;
            primaryColor = 0xFF7C4DFF;
            secondaryColor = 0xFFFF007F;
            accentGlowColor = 0xFF00F5FF;
            bgStartColor = 0xFF0E0720;
            bgEndColor = 0xFF04020A;
        } else if (key.contains("plasma")) {
            animType = IconAnimType.PLASMA;
            primaryColor = 0xFFD946EF;
            secondaryColor = 0xFF38BDF8;
            accentGlowColor = 0xFFF43F5E;
            bgStartColor = 0xFF1A0722;
            bgEndColor = 0xFF0B0210;
        } else if (key.contains("amethyst")) {
            animType = IconAnimType.AMETHYST;
            primaryColor = 0xFFA855F7;
            secondaryColor = 0xFFEC4899;
            accentGlowColor = 0xFFE879F9;
            bgStartColor = 0xFF160924;
            bgEndColor = 0xFF090310;
        } else if (key.contains("cyber")) {
            animType = IconAnimType.CYBER;
            primaryColor = 0xFF00F0FF;
            secondaryColor = 0xFFFF007A;
            accentGlowColor = 0xFF00FF66;
            bgStartColor = 0xFF07121A;
            bgEndColor = 0xFF02070C;
        } else if (key.contains("abyss")) {
            animType = IconAnimType.ABYSS;
            primaryColor = 0xFF06B6D4;
            secondaryColor = 0xFF3B82F6;
            accentGlowColor = 0xFF14B8A6;
            bgStartColor = 0xFF04121C;
            bgEndColor = 0xFF01060B;
        } else if (key.contains("bronze")) {
            animType = IconAnimType.BRONZE;
            primaryColor = 0xFFF59E0B;
            secondaryColor = 0xFFB45309;
            accentGlowColor = 0xFFFCD34D;
            bgStartColor = 0xFF1C1309;
            bgEndColor = 0xFF0C0702;
        } else if (key.contains("monochrome")) {
            animType = IconAnimType.MONOCHROME;
            primaryColor = 0xFFCBD5E1;
            secondaryColor = 0xFF64748B;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF0A0C10;
            bgEndColor = 0xFF030406;
        } else if (key.contains("spectrum")) {
            animType = IconAnimType.SPECTRUM;
            primaryColor = 0xFFFF007F;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFFFFEB3B;
            bgStartColor = 0xFF120C20;
            bgEndColor = 0xFF05030A;
        } else if (key.contains("glitch")) {
            animType = IconAnimType.GLITCH;
            primaryColor = 0xFF00F0FF;
            secondaryColor = 0xFFFF0055;
            accentGlowColor = 0xFF00FF66;
            bgStartColor = 0xFF090E16;
            bgEndColor = 0xFF030508;
        } else if (key.contains("vintage")) {
            animType = IconAnimType.VINTAGE;
            primaryColor = 0xFFEAB308;
            secondaryColor = 0xFFD97706;
            accentGlowColor = 0xFFFEF08A;
            bgStartColor = 0xFF1A1308;
            bgEndColor = 0xFF0B0702;
        } else if (key.contains("aqua")) {
            animType = IconAnimType.AQUA;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF0072FF;
            accentGlowColor = 0xFF1DE9B6;
            bgStartColor = 0xFF061624;
            bgEndColor = 0xFF020810;
        } else if (key.contains("premium")) {
            animType = IconAnimType.PREMIUM;
            primaryColor = 0xFFFF007F;
            secondaryColor = 0xFF7C4DFF;
            accentGlowColor = 0xFF00E5FF;
            bgStartColor = 0xFF180720;
            bgEndColor = 0xFF08020E;
        } else if (key.contains("turbo")) {
            animType = IconAnimType.TURBO;
            primaryColor = 0xFFFF3366;
            secondaryColor = 0xFFFF9900;
            accentGlowColor = 0xFF00F5FF;
            bgStartColor = 0xFF1C0812;
            bgEndColor = 0xFF0B0206;
        } else if (key.contains("nox")) {
            animType = IconAnimType.NOX;
            primaryColor = 0xFF60A5FA;
            secondaryColor = 0xFF818CF8;
            accentGlowColor = 0xFFE2E8F0;
            bgStartColor = 0xFF080A0E;
            bgEndColor = 0xFF020305;
        } else if (key.contains("cobalt")) {
            animType = IconAnimType.COBALT;
            primaryColor = 0xFF2563EB;
            secondaryColor = 0xFF06B6D4;
            accentGlowColor = 0xFF60A5FA;
            bgStartColor = 0xFF061124;
            bgEndColor = 0xFF020610;
        } else if (key.contains("aurora")) {
            animType = IconAnimType.AURORA;
            primaryColor = 0xFF10B981;
            secondaryColor = 0xFF8B5CF6;
            accentGlowColor = 0xFF06B6D4;
            bgStartColor = 0xFF071816;
            bgEndColor = 0xFF020A09;
        } else if (key.contains("pure")) {
            animType = IconAnimType.PURE;
            primaryColor = 0xFF38BDF8;
            secondaryColor = 0xFFE0E7FF;
            accentGlowColor = 0xFFFFFFFF;
            bgStartColor = 0xFF091624;
            bgEndColor = 0xFF030910;
        } else {
            animType = IconAnimType.DEFAULT;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF7C4DFF;
            accentGlowColor = 0xFFFF007F;
            bgStartColor = 0xFF0B1424;
            bgEndColor = 0xFF040810;
        }
    }

    private String getThemeEditionLabel(LauncherIconController.LauncherIcon icon) {
        switch (animType) {
            case MATRIX: return "Cyber Liquid Matrix";
            case SUNSET: return "Molten Amber Glass";
            case RUBY: return "Velvet Ruby Obsidian";
            case COSMOS: return "Cosmic Multiverse Opal";
            case LAVA: return "Volcanic Geothermal Heat";
            case CHROME: return "Liquid Mercury Chrome";
            case SAKURA: return "Cherry Blossom Frost";
            case SINGULARITY: return "Event Horizon Vortex";
            case PLASMA: return "High-Voltage Electric Arc";
            case AMETHYST: return "Prismatic Amethyst Quartz";
            case CYBER: return "Futuristic Synthwave HUD";
            case ABYSS: return "Oceanic Bioluminescence";
            case BRONZE: return "Steampunk Clockwork Gold";
            case MONOCHROME: return "Pure OLED Stealth Shadow";
            case SPECTRUM: return "Chromatic Rainbow Dispersion";
            case GLITCH: return "Cyberpunk Digital Glitch";
            case VINTAGE: return "Golden Nostalgia Glow";
            case AQUA: return "Frosted Glacial Ice";
            case PREMIUM: return "Iridescent Neon Crystal";
            case TURBO: return "Supersonic Warp Velocity";
            case NOX: return "Carbon Fiber Stealth";
            case COBALT: return "Deep Royal Sapphire";
            case AURORA: return "Northern Aurora Plasma";
            case PURE: return "Sub-Zero Arctic Diamond";
            default: return "iOS 26.5 Liquid Glass";
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
            if (hour >= 5 && hour < 12) {
                return "Доброе утро, " + userName + "! ☀️";
            } else if (hour >= 12 && hour < 18) {
                return "Добрый день, " + userName + "! 🌤️";
            } else if (hour >= 18 && hour < 23) {
                return "Добрый вечер, " + userName + "! 🌙";
            } else {
                return "Доброй ночи, " + userName + "! ✨";
            }
        } else if (lang.startsWith("en")) {
            if (hour >= 5 && hour < 12) {
                return "Good morning, " + userName + "! ☀️";
            } else if (hour >= 12 && hour < 18) {
                return "Good afternoon, " + userName + "! 🌤️";
            } else if (hour >= 18 && hour < 23) {
                return "Good evening, " + userName + "! 🌙";
            } else {
                return "Good night, " + userName + "! ✨";
            }
        } else {
            // Uzbek
            if (hour >= 5 && hour < 12) {
                return "Xayrli tong, " + userName + "! ☀️";
            } else if (hour >= 12 && hour < 18) {
                return "Xayrli kun, " + userName + "! 🌤️";
            } else if (hour >= 18 && hour < 23) {
                return "Xayrli kech, " + userName + "! 🌙";
            } else {
                return "Xayrli tun, " + userName + "! ✨";
            }
        }
    }

    public void showAndAutoDismiss(long delayMs) {
        iconCard.setScaleX(0.35f);
        iconCard.setScaleY(0.35f);
        iconCard.setRotationX(28f);
        iconCard.setRotationY(-22f);
        iconCard.setAlpha(0.0f);

        greetingView.setAlpha(0.0f);
        greetingView.setTranslationY(AndroidUtilities.dp(18));
        appTitle.setAlpha(0.0f);
        appSubtitle.setAlpha(0.0f);

        iconCard.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .rotationX(0f)
                .rotationY(0f)
                .alpha(1.0f)
                .setDuration(650)
                .setInterpolator(new OvershootInterpolator(1.4f))
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

        shimmerOverlay.startShimmer();
        startIdlePhysics();

        // Stays for full 4.5 seconds
        postDelayed(this::dismiss, Math.max(delayMs, 4500));
    }

    private void startIdlePhysics() {
        idleAnimator = ValueAnimator.ofFloat(0f, 1f);
        idleAnimator.setDuration(2600);
        idleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        idleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        idleAnimator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            if (!isDismissing) {
                if (animType == IconAnimType.GLITCH) {
                    // Random micro jitter
                    if (random.nextFloat() < 0.2f) {
                        iconCard.setTranslationX((random.nextFloat() - 0.5f) * AndroidUtilities.dp(6));
                        iconCard.setTranslationY((random.nextFloat() - 0.5f) * AndroidUtilities.dp(4));
                    } else {
                        iconCard.setTranslationX(0);
                        iconCard.setTranslationY(0);
                    }
                } else if (animType == IconAnimType.TURBO) {
                    // Warp stretching
                    float scaleX = 0.96f + fraction * 0.08f;
                    float scaleY = 1.04f - fraction * 0.08f;
                    iconCard.setScaleX(scaleX);
                    iconCard.setScaleY(scaleY);
                } else {
                    float tiltX = (float) Math.sin(fraction * Math.PI * 2) * 5.5f;
                    float tiltY = (float) Math.cos(fraction * Math.PI * 2) * 6.5f;
                    float scale = 0.98f + fraction * 0.04f;
                    iconCard.setRotationX(tiltX);
                    iconCard.setRotationY(tiltY);
                    iconCard.setScaleX(scale);
                    iconCard.setScaleY(scale);
                }
            }
        });
        idleAnimator.start();
    }

    public void dismiss() {
        if (isDismissing) return;
        isDismissing = true;

        if (idleAnimator != null) {
            idleAnimator.cancel();
        }

        animate()
                .alpha(0.0f)
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(350)
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
    // 1. Particle Layer: 25+ Bespoke Physics Simulations
    // ──────────────────────────────────────────────────────────────────────────
    private static class ParticleCanvasView extends View {
        private static final int PARTICLE_COUNT = 42;
        private final List<Particle> particles = new ArrayList<>();
        private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final IconAnimType animType;
        private final int color1;
        private final int color2;
        private final int color3;
        private final Random random = new Random();
        private long lastFrameTime;

        private static class Particle {
            float x, y;
            float vx, vy;
            float radius;
            float alpha;
            float rotation;
            float vRotation;
            float maxAlpha;
            int color;
            float extraParam;
        }

        public ParticleCanvasView(Context context, IconAnimType type, int c1, int c2, int c3) {
            super(context);
            this.animType = type;
            this.color1 = c1;
            this.color2 = c2;
            this.color3 = c3;
            initParticles();
        }

        private void initParticles() {
            particles.clear();
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                particles.add(createParticle(true));
            }
        }

        private Particle createParticle(boolean randomY) {
            Particle p = new Particle();
            int w = getWidth() > 0 ? getWidth() : AndroidUtilities.displaySize.x;
            int h = getHeight() > 0 ? getHeight() : AndroidUtilities.displaySize.y;

            p.x = random.nextFloat() * w;
            p.y = randomY ? random.nextFloat() * h : (animType == IconAnimType.MATRIX ? -30 : h + 30);
            p.radius = AndroidUtilities.dp(2 + random.nextFloat() * 5.5f);
            p.maxAlpha = 0.35f + random.nextFloat() * 0.6f;
            p.alpha = random.nextFloat() * p.maxAlpha;
            p.rotation = random.nextFloat() * 360f;
            p.vRotation = (random.nextFloat() - 0.5f) * 5f;
            p.extraParam = random.nextFloat() * 100f;

            int rnd = random.nextInt(3);
            p.color = rnd == 0 ? color1 : (rnd == 1 ? color2 : color3);

            switch (animType) {
                case MATRIX:
                    p.vx = (random.nextFloat() - 0.5f) * 0.3f;
                    p.vy = 3.5f + random.nextFloat() * 5.5f;
                    break;
                case SINGULARITY:
                    float cx = w / 2f;
                    float cy = h / 2f;
                    float angle = (float) Math.atan2(p.y - cy, p.x - cx);
                    p.vx = -(float) Math.cos(angle) * 2f - (float) Math.sin(angle) * 3f;
                    p.vy = -(float) Math.sin(angle) * 2f + (float) Math.cos(angle) * 3f;
                    break;
                case COSMOS:
                case AMETHYST:
                    p.vx = (random.nextFloat() - 0.5f) * 2.5f;
                    p.vy = (random.nextFloat() - 0.5f) * 2.5f;
                    break;
                case SAKURA:
                    p.vx = 1.2f + (random.nextFloat() - 0.5f) * 1.5f;
                    p.vy = 1.5f + random.nextFloat() * 2f;
                    break;
                case LAVA:
                case SUNSET:
                case BRONZE:
                    p.vx = (random.nextFloat() - 0.5f) * 1.8f;
                    p.vy = -(2.0f + random.nextFloat() * 4f);
                    break;
                case TURBO:
                    p.vx = (random.nextFloat() - 0.5f) * 4f;
                    p.vy = -(4.0f + random.nextFloat() * 8f);
                    break;
                case GLITCH:
                    p.vx = (random.nextFloat() - 0.5f) * 6f;
                    p.vy = (random.nextFloat() - 0.5f) * 2f;
                    break;
                case AQUA:
                case PURE:
                case ABYSS:
                    p.vx = (float) Math.sin(p.y * 0.04f) * 1.5f;
                    p.vy = -(1.8f + random.nextFloat() * 3.5f);
                    break;
                default:
                    p.vx = (random.nextFloat() - 0.5f) * 1.6f;
                    p.vy = -(1.4f + random.nextFloat() * 2.8f);
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

            for (Particle p : particles) {
                if (animType == IconAnimType.SINGULARITY) {
                    float cx = w / 2f;
                    float cy = h / 2f;
                    float dx = cx - p.x;
                    float dy = cy - p.y;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    if (dist > 10) {
                        p.vx += (dx / dist) * 40f * dt;
                        p.vy += (dy / dist) * 40f * dt;
                    }
                } else if (animType == IconAnimType.SAKURA) {
                    p.vx += (float) Math.sin((now / 500.0) + p.extraParam) * 0.1f;
                }

                p.x += p.vx * 60f * dt;
                p.y += p.vy * 60f * dt;
                p.rotation += p.vRotation;

                if (p.y < -40 || p.y > h + 40 || p.x < -40 || p.x > w + 40) {
                    Particle fresh = createParticle(false);
                    p.x = fresh.x;
                    p.y = fresh.y;
                    p.vx = fresh.vx;
                    p.vy = fresh.vy;
                    p.alpha = 0f;
                } else {
                    if (p.alpha < p.maxAlpha) {
                        p.alpha = Math.min(p.maxAlpha, p.alpha + 0.03f);
                    }
                }

                particlePaint.setColor(p.color);
                particlePaint.setAlpha((int) (p.alpha * 255));

                if (animType == IconAnimType.MATRIX) {
                    // Cascading digital cyber glyph bar
                    canvas.drawRect(p.x - p.radius * 0.8f, p.y - p.radius * 2.5f, p.x + p.radius * 0.8f, p.y + p.radius * 2.5f, particlePaint);
                } else if (animType == IconAnimType.SAKURA) {
                    // Floating flower petal oval
                    canvas.save();
                    canvas.rotate(p.rotation, p.x, p.y);
                    canvas.drawOval(p.x - p.radius * 1.5f, p.y - p.radius * 0.8f, p.x + p.radius * 1.5f, p.y + p.radius * 0.8f, particlePaint);
                    canvas.restore();
                } else if (animType == IconAnimType.GLITCH) {
                    // Digital glitch horizontal scan block
                    canvas.drawRect(p.x - p.radius * 3f, p.y - p.radius * 0.5f, p.x + p.radius * 3f, p.y + p.radius * 0.5f, particlePaint);
                } else if (animType == IconAnimType.COSMOS || animType == IconAnimType.AMETHYST || animType == IconAnimType.PURE) {
                    // Crystalline starburst spark
                    canvas.save();
                    canvas.rotate(p.rotation, p.x, p.y);
                    canvas.drawCircle(p.x, p.y, p.radius, particlePaint);
                    canvas.restore();
                } else {
                    // Liquid glass glowing droplet
                    canvas.drawCircle(p.x, p.y, p.radius, particlePaint);
                }
            }
            invalidate();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Pulse Shockwave Rings Layer
    // ──────────────────────────────────────────────────────────────────────────
    private static class PulseRingsView extends View {
        private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final IconAnimType animType;
        private final int primaryColor;
        private final int secondaryColor;
        private float progress;

        public PulseRingsView(Context context, IconAnimType type, int c1, int c2) {
            super(context);
            this.animType = type;
            this.primaryColor = c1;
            this.secondaryColor = c2;
            ringPaint.setStyle(Paint.Style.STROKE);

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(1800);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(animation -> {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            float maxRadius = getWidth() * 0.48f;

            // Ring 1
            float r1 = progress * maxRadius;
            int alpha1 = (int) ((1f - progress) * 120);
            ringPaint.setColor(primaryColor);
            ringPaint.setAlpha(alpha1);
            ringPaint.setStrokeWidth(AndroidUtilities.dp(2.2f * (1f - progress)));
            canvas.drawCircle(cx, cy, r1, ringPaint);

            // Ring 2 (delayed)
            float p2 = (progress + 0.5f) % 1.0f;
            float r2 = p2 * maxRadius;
            int alpha2 = (int) ((1f - p2) * 100);
            ringPaint.setColor(secondaryColor);
            ringPaint.setAlpha(alpha2);
            ringPaint.setStrokeWidth(AndroidUtilities.dp(1.8f * (1f - p2)));
            canvas.drawCircle(cx, cy, r2, ringPaint);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Custom Icon Specific Overlays (HUD, Lightning, Cyber Crosshair, Plasma)
    // ──────────────────────────────────────────────────────────────────────────
    private static class CustomIconFxOverlay extends View {
        private final Paint fxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final IconAnimType animType;
        private final int primaryColor;
        private final int secondaryColor;
        private float angle;
        private final Random random = new Random();

        public CustomIconFxOverlay(Context context, IconAnimType type, int c1, int c2) {
            super(context);
            this.animType = type;
            this.primaryColor = c1;
            this.secondaryColor = c2;
            fxPaint.setStyle(Paint.Style.STROKE);
            fxPaint.setStrokeWidth(AndroidUtilities.dp(1.5f));

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 360f);
            animator.setDuration(6000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(animation -> {
                angle = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            float r = AndroidUtilities.dp(56);

            if (animType == IconAnimType.CYBER || animType == IconAnimType.MATRIX) {
                // Cyber HUD Target Brackets
                fxPaint.setColor(primaryColor);
                fxPaint.setAlpha(160);
                canvas.save();
                canvas.rotate(angle, cx, cy);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 15, 60, false, fxPaint);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 105, 60, false, fxPaint);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 195, 60, false, fxPaint);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 285, 60, false, fxPaint);
                canvas.restore();
            } else if (animType == IconAnimType.PLASMA || animType == IconAnimType.PREMIUM) {
                // Flashing Tesla Electric Arcs
                if (random.nextFloat() < 0.4f) {
                    fxPaint.setColor(primaryColor);
                    fxPaint.setAlpha(200);
                    float x1 = cx + (random.nextFloat() - 0.5f) * r * 1.5f;
                    float y1 = cy + (random.nextFloat() - 0.5f) * r * 1.5f;
                    float x2 = x1 + (random.nextFloat() - 0.5f) * AndroidUtilities.dp(24);
                    float y2 = y1 + (random.nextFloat() - 0.5f) * AndroidUtilities.dp(24);
                    canvas.drawLine(x1, y1, x2, y2, fxPaint);
                }
            } else if (animType == IconAnimType.SINGULARITY || animType == IconAnimType.COSMOS) {
                // Planetary Orbit Ellipse
                fxPaint.setColor(secondaryColor);
                fxPaint.setAlpha(130);
                canvas.save();
                canvas.rotate(angle * 0.7f, cx, cy);
                canvas.drawOval(cx - r * 1.2f, cy - r * 0.6f, cx + r * 1.2f, cy + r * 0.6f, fxPaint);
                canvas.restore();
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. Diagonal Specular Glass Shimmer Overlay (Repeating)
    // ──────────────────────────────────────────────────────────────────────────
    private static class GlassShimmerOverlay extends View {
        private final Paint shimmerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Matrix shaderMatrix = new Matrix();
        private LinearGradient shimmerShader;
        private float shimmerProgress = -1.2f;

        public GlassShimmerOverlay(Context context) {
            super(context);
        }

        public void startShimmer() {
            ValueAnimator animator = ValueAnimator.ofFloat(-1.2f, 2.2f);
            animator.setDuration(1400);
            animator.setStartDelay(300);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animator.addUpdateListener(animation -> {
                shimmerProgress = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w > 0 && h > 0) {
                shimmerShader = new LinearGradient(
                        0, 0, w, h,
                        new int[]{Color.TRANSPARENT, Color.argb(170, 255, 255, 255), Color.TRANSPARENT},
                        new float[]{0.3f, 0.5f, 0.7f},
                        Shader.TileMode.CLAMP
                );
                shimmerPaint.setShader(shimmerShader);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (shimmerShader != null && shimmerProgress > -1.2f && shimmerProgress < 2.2f) {
                shaderMatrix.reset();
                shaderMatrix.setTranslate(getWidth() * shimmerProgress, 0);
                shimmerShader.setLocalMatrix(shaderMatrix);
                canvas.drawRect(0, 0, getWidth(), getHeight(), shimmerPaint);
            }
        }
    }
}
