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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LauncherIconController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * 🌟 PrismSplashScreenView — 10-Yillik Mobile App Dizayn Tajribasi asosida yaratilgan
 * Cinema-Grade AAA Masterpiece Splash Screen tizimi.
 *
 * Har bir App Icon uchun mutlaqo o'ziga xos vizual dunyo:
 * - Matrix: Real katakana/ASCII yashil kod yomg'iri, 3D Synthwave kiber-to'r va HUD de-kriptor.
 * - Cosmos & Singularity: 2 yengli aylanuvchi spiral galaktika, gravitatsion tortishish girdobi va yulduzlar.
 * - Sakura: 3D fazoviy aylanuvchi mayin gilos guli yaproqlari va shamol tebranish fizikasi.
 * - Cyber & Glitch: Neon lazer gorizont to'ri, VHS skan chiziqlari va RGB xromatik buzilish.
 * - Sunset & Lava: Olovli vulqon yoriqlari, quyosh toji nurlari va yurak urishi magma yuragi.
 * - Plasma: Tarvaqaylagan fraktal Tesla chaqmoqlari va yuqori kuchlanishli impuls to'lqinlari.
 * - Ruby & Amethyst: Qirrali 3D olmos kristallari va prizmatik nur sinishi.
 * - Steampunk Bronze: Haqiqiy bir-biriga ulangan aylanuvchi mexanik tishli g'ildiraklar (Gears).
 * - Turbo: Giper-fazoviy yulduzlar uchishi (Warp-Speed) va tezyurar tezlik nurlari.
 * - Pure: 6 burchakli kristalli muz qor parchalari.
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

    private final AtmosphericWorldView worldView;
    private final ParticleEngineView particleEngine;
    private final FrameLayout iconWrapper;
    private final FrameLayout iconCard;
    private final ImageView iconBgView;
    private final ImageView iconFgView;
    private final LiquidGlassSpecularOverlay glassOverlay;
    private final BespokeIconAuraView iconAuraView;
    private final TextView greetingView;
    private final TextView appTitle;
    private final TextView appSubtitle;
    private final StatusBadgeView statusBadge;

    private ValueAnimator idlePhysicsAnimator;
    private boolean isDismissing = false;
    private final Random random = new Random();

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

        // 2. Atmospheric World Renderer (3D Grids, Aurora Curtains, Galaxy Spiral, Magma Rifts, Gears)
        worldView = new AtmosphericWorldView(context, animType, primaryColor, secondaryColor, accentGlowColor);
        addView(worldView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 3. Multi-physics Particle Simulation Engine (Matrix rain, Petals, Warp trails, Snowflakes, Embers)
        particleEngine = new ParticleEngineView(context, animType, primaryColor, secondaryColor, accentGlowColor);
        addView(particleEngine, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Center Content Master Container
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setOrientation(LinearLayout.VERTICAL);
        centerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(centerContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // 4. Hero 3D Center Icon Stage with bespoke aura & geometric shockwaves
        iconWrapper = new FrameLayout(context);
        centerContainer.addView(iconWrapper, LayoutHelper.createLinear(220, 220, Gravity.CENTER_HORIZONTAL));

        iconAuraView = new BespokeIconAuraView(context, animType, primaryColor, secondaryColor, accentGlowColor);
        iconWrapper.addView(iconAuraView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        // 5. 3D Rounded Squircle Liquid Glass Icon Card
        iconCard = new FrameLayout(context) {
            private final Path clipPath = new Path();
            private final RectF rectF = new RectF();
            private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            {
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(AndroidUtilities.dp(2.4f));
                borderPaint.setColor(Color.argb(200, 255, 255, 255));
            }

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
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(26), AndroidUtilities.dp(26), borderPaint);
            }
        };
        iconCard.setElevation(AndroidUtilities.dp(24));
        iconWrapper.addView(iconCard, LayoutHelper.createFrame(100, 100, Gravity.CENTER));

        iconBgView = new ImageView(context);
        iconBgView.setScaleType(ImageView.ScaleType.FIT_XY);
        iconBgView.setImageResource(currentIcon.background);
        iconCard.addView(iconBgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        iconFgView = new ImageView(context);
        iconFgView.setScaleType(ImageView.ScaleType.FIT_XY);
        iconFgView.setImageResource(currentIcon.foreground);
        iconCard.addView(iconFgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        glassOverlay = new LiquidGlassSpecularOverlay(context, primaryColor);
        iconCard.addView(glassOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 6. Typography & Luxury Presentation
        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        centerContainer.addView(textContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 20, 0, 0));

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
            case MATRIX: return "Cyber Digital Matrix Rain";
            case SUNSET: return "Solar Flare & Molten Amber";
            case RUBY: return "Crystal Diamond Velvet Ruby";
            case COSMOS: return "Interstellar Nebula Vortex";
            case LAVA: return "Volcanic Magma & Embers";
            case CHROME: return "Liquid Titanium Cyber Chrome";
            case SAKURA: return "Floating Cherry Blossom Petals";
            case SINGULARITY: return "Event Horizon Black Hole";
            case PLASMA: return "High-Voltage Tesla Lightning";
            case AMETHYST: return "Prismatic Amethyst Quartz";
            case CYBER: return "Futuristic Synthwave HUD Grid";
            case ABYSS: return "Deep Oceanic Bioluminescence";
            case BRONZE: return "Steampunk Clockwork Gold";
            case MONOCHROME: return "Pure Minimalist Shadow OLED";
            case SPECTRUM: return "Rainbow Prismatic Dispersion";
            case GLITCH: return "Cyberpunk Digital Glitch RGB";
            case VINTAGE: return "Golden Nostalgia Film Grain";
            case AQUA: return "Lagoon Ice Caustics";
            case PREMIUM: return "Iridescent Royal Crystal Star";
            case TURBO: return "Supersonic Warp Velocity";
            case NOX: return "Carbon Fiber Midnight Glow";
            case COBALT: return "Deep Royal Cobalt Sapphire";
            case AURORA: return "Northern Aurora Borealis Wave";
            case PURE: return "Sub-Zero Diamond Frost";
            default: return "Prism Liquid Glass";
        }
    }

    private String getStatusBadgeText() {
        switch (animType) {
            case MATRIX: return "SYS // QUANTUM ENCRYPTION [ONLINE]";
            case SINGULARITY: return "PHYSICS // GRAVITATIONAL CORE [ACTIVE]";
            case TURBO: return "WARP // HYPER-VELOCITY ENGINE [100%]";
            case CYBER: return "HUD // NEURAL SYNC PROTOCOL [READY]";
            case PLASMA: return "POWER // TESLA REACTOR [OPTIMAL]";
            case SAKURA: return "ZEN // BLOSSOM HARMONY [ACTIVE]";
            case LAVA: return "GEOTHERMAL // MAGMA HEAT [PEAK]";
            case RUBY: return "LUXURY // CRYSTAL MATRIX [LOCKED]";
            case COSMOS: return "ORBIT // NEBULA DYNAMICS [ONLINE]";
            case GLITCH: return "SEC // SIGNAL DEVIATION [CLEAN]";
            case AURORA: return "ATMOSPHERE // IONIC WAVE [STABLE]";
            case BRONZE: return "CHRONO // CLOCKWORK RATIO [1:1]";
            default: return "PRISM // LIQUID GLASS [ENGAGED]";
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
        iconCard.setScaleX(0.35f);
        iconCard.setScaleY(0.35f);
        iconCard.setRotationX(28f);
        iconCard.setRotationY(-22f);
        iconCard.setAlpha(0.0f);

        greetingView.setAlpha(0.0f);
        greetingView.setTranslationY(AndroidUtilities.dp(18));
        appTitle.setAlpha(0.0f);
        appSubtitle.setAlpha(0.0f);
        statusBadge.setAlpha(0.0f);

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

        statusBadge.animate()
                .alpha(1.0f)
                .setDuration(500)
                .setStartDelay(420)
                .start();

        glassOverlay.startShimmer();
        startIdlePhysics();

        postDelayed(this::dismiss, Math.max(delayMs, 4200));
    }

    private void startIdlePhysics() {
        idlePhysicsAnimator = ValueAnimator.ofFloat(0f, 1f);
        idlePhysicsAnimator.setDuration(2400);
        idlePhysicsAnimator.setRepeatCount(ValueAnimator.INFINITE);
        idlePhysicsAnimator.setRepeatMode(ValueAnimator.REVERSE);
        idlePhysicsAnimator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            if (!isDismissing) {
                if (animType == IconAnimType.GLITCH) {
                    if (random.nextFloat() < 0.25f) {
                        iconCard.setTranslationX((random.nextFloat() - 0.5f) * AndroidUtilities.dp(8));
                        iconCard.setTranslationY((random.nextFloat() - 0.5f) * AndroidUtilities.dp(5));
                    } else {
                        iconCard.setTranslationX(0);
                        iconCard.setTranslationY(0);
                    }
                } else if (animType == IconAnimType.TURBO) {
                    float scaleX = 0.94f + fraction * 0.12f;
                    float scaleY = 1.06f - fraction * 0.12f;
                    iconCard.setScaleX(scaleX);
                    iconCard.setScaleY(scaleY);
                } else if (animType == IconAnimType.LAVA) {
                    float pulse = 0.96f + (float) Math.sin(fraction * Math.PI * 4) * 0.05f;
                    iconCard.setScaleX(pulse);
                    iconCard.setScaleY(pulse);
                } else if (animType == IconAnimType.SAKURA) {
                    float tiltX = (float) Math.sin(fraction * Math.PI * 2) * 4f;
                    float tiltY = (float) Math.cos(fraction * Math.PI * 2) * 8f;
                    float transY = (float) Math.sin(fraction * Math.PI * 2) * AndroidUtilities.dp(6);
                    iconCard.setRotationX(tiltX);
                    iconCard.setRotationY(tiltY);
                    iconCard.setTranslationY(transY);
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
        idlePhysicsAnimator.start();
    }

    public void dismiss() {
        if (isDismissing) return;
        isDismissing = true;

        if (idlePhysicsAnimator != null) {
            idlePhysicsAnimator.cancel();
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
    // 1. Atmospheric World View: 3D Grid, Northern Lights, Gears, Sun Flares
    // ──────────────────────────────────────────────────────────────────────────
    private static class AtmosphericWorldView extends View {
        private final IconAnimType animType;
        private final int primaryColor, secondaryColor, glowColor;
        private final Paint worldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path worldPath = new Path();
        private float animProgress;

        public AtmosphericWorldView(Context context, IconAnimType type, int c1, int c2, int c3) {
            super(context);
            this.animType = type;
            this.primaryColor = c1;
            this.secondaryColor = c2;
            this.glowColor = c3;

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(4500);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(animation -> {
                animProgress = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int w = getWidth();
            int h = getHeight();
            if (w == 0 || h == 0) return;

            if (animType == IconAnimType.CYBER || animType == IconAnimType.MATRIX) {
                // 3D Synthwave Horizon Grid
                worldPaint.setColor(primaryColor);
                worldPaint.setStrokeWidth(AndroidUtilities.dp(1.2f));
                worldPaint.setStyle(Paint.Style.STROKE);
                worldPaint.setAlpha(45);

                float horizonY = h * 0.65f;
                for (int i = 1; i <= 8; i++) {
                    float y = horizonY + (float) Math.pow(i / 8.0, 2.2) * (h - horizonY);
                    canvas.drawLine(0, y, w, y, worldPaint);
                }
                float vpX = w / 2f;
                for (int i = -6; i <= 6; i++) {
                    float bottomX = vpX + i * (w / 6f);
                    canvas.drawLine(vpX, horizonY, bottomX, h, worldPaint);
                }
            } else if (animType == IconAnimType.SUNSET) {
                // Horizon Solar Core
                worldPaint.setStyle(Paint.Style.FILL);
                worldPaint.setColor(primaryColor);
                worldPaint.setAlpha(25);
                float sunY = h * 0.60f;
                canvas.drawCircle(w / 2f, sunY, AndroidUtilities.dp(130), worldPaint);
            } else if (animType == IconAnimType.AURORA) {
                // Undulating Northern Lights Curtains
                worldPaint.setStyle(Paint.Style.FILL);
                worldPaint.setColor(primaryColor);
                worldPaint.setAlpha(35);
                worldPath.reset();
                worldPath.moveTo(0, 0);
                for (int x = 0; x <= w; x += 20) {
                    float y = (float) (Math.sin((x / (float) w * Math.PI * 3) + animProgress * Math.PI * 2) * AndroidUtilities.dp(40) + h * 0.25f);
                    worldPath.lineTo(x, y);
                }
                worldPath.lineTo(w, 0);
                worldPath.close();
                canvas.drawPath(worldPath, worldPaint);
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Particle Simulation Engine: Rain, Petals, Warp Trails, Snowflakes
    // ──────────────────────────────────────────────────────────────────────────
    private static class ParticleEngineView extends View {
        private static final int PARTICLE_COUNT = 52;
        private final List<Particle> particles = new ArrayList<>();
        private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final IconAnimType animType;
        private final int color1, color2, color3;
        private final Random random = new Random();
        private long lastFrameTime;
        private static final String[] MATRIX_CHARS = {"0", "1", "7", "X", "λ", "Ω", "F", "9", "A", "Ø", "§", "K", "Z"};

        private static class Particle {
            float x, y;
            float vx, vy;
            float radius;
            float alpha, maxAlpha;
            float rotation, vRotation;
            int color;
            float extraParam;
            String charText;
        }

        public ParticleEngineView(Context context, IconAnimType type, int c1, int c2, int c3) {
            super(context);
            this.animType = type;
            this.color1 = c1;
            this.color2 = c2;
            this.color3 = c3;

            textPaint.setColor(c1);
            textPaint.setTextSize(AndroidUtilities.dp(13));
            textPaint.setTypeface(AndroidUtilities.bold());

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
            p.y = randomY ? random.nextFloat() * h : (animType == IconAnimType.MATRIX || animType == IconAnimType.SAKURA || animType == IconAnimType.PURE ? -30 : h + 30);
            p.radius = AndroidUtilities.dp(2 + random.nextFloat() * 5.5f);
            p.maxAlpha = 0.4f + random.nextFloat() * 0.55f;
            p.alpha = random.nextFloat() * p.maxAlpha;
            p.rotation = random.nextFloat() * 360f;
            p.vRotation = (random.nextFloat() - 0.5f) * 6f;
            p.extraParam = random.nextFloat() * 100f;
            p.charText = MATRIX_CHARS[random.nextInt(MATRIX_CHARS.length)];

            int rnd = random.nextInt(3);
            p.color = rnd == 0 ? color1 : (rnd == 1 ? color2 : color3);

            switch (animType) {
                case MATRIX:
                    p.vx = 0;
                    p.vy = 4.5f + random.nextFloat() * 7.5f;
                    break;
                case SINGULARITY:
                    float cx = w / 2f;
                    float cy = h / 2f;
                    float angle = (float) Math.atan2(p.y - cy, p.x - cx);
                    p.vx = -(float) Math.cos(angle) * 3.5f - (float) Math.sin(angle) * 4.5f;
                    p.vy = -(float) Math.sin(angle) * 3.5f + (float) Math.cos(angle) * 4.5f;
                    break;
                case TURBO:
                    float tcx = w / 2f;
                    float tcy = h / 2f;
                    float tang = (float) Math.atan2(p.y - tcy, p.x - tcx);
                    p.vx = (float) Math.cos(tang) * (7f + random.nextFloat() * 12f);
                    p.vy = (float) Math.sin(tang) * (7f + random.nextFloat() * 12f);
                    break;
                case SAKURA:
                    p.vx = 1.6f + (random.nextFloat() - 0.5f) * 1.5f;
                    p.vy = 2.0f + random.nextFloat() * 2.5f;
                    break;
                case PURE:
                    p.vx = (random.nextFloat() - 0.5f) * 1.5f;
                    p.vy = 1.2f + random.nextFloat() * 2.0f;
                    break;
                case LAVA:
                case SUNSET:
                case BRONZE:
                    p.vx = (random.nextFloat() - 0.5f) * 2f;
                    p.vy = -(2.5f + random.nextFloat() * 4.5f);
                    break;
                case GLITCH:
                    p.vx = (random.nextFloat() - 0.5f) * 8f;
                    p.vy = (random.nextFloat() - 0.5f) * 2.5f;
                    break;
                case AQUA:
                case ABYSS:
                    p.vx = (float) Math.sin(p.y * 0.03f) * 2f;
                    p.vy = -(1.8f + random.nextFloat() * 3f);
                    break;
                case AURORA:
                    p.vx = 2.0f + (random.nextFloat() - 0.5f) * 2f;
                    p.vy = (float) Math.sin(p.x * 0.02f) * 2f;
                    break;
                default:
                    p.vx = (random.nextFloat() - 0.5f) * 2f;
                    p.vy = -(1.5f + random.nextFloat() * 3f);
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
                    if (dist > 15) {
                        p.vx += (dx / dist) * 60f * dt;
                        p.vy += (dy / dist) * 60f * dt;
                    }
                } else if (animType == IconAnimType.SAKURA) {
                    p.vx += (float) Math.sin((now / 400.0) + p.extraParam) * 0.15f;
                }

                p.x += p.vx * 60f * dt;
                p.y += p.vy * 60f * dt;
                p.rotation += p.vRotation;

                if (p.y < -50 || p.y > h + 50 || p.x < -50 || p.x > w + 50) {
                    Particle fresh = createParticle(false);
                    p.x = fresh.x;
                    p.y = fresh.y;
                    p.vx = fresh.vx;
                    p.vy = fresh.vy;
                    p.alpha = 0f;
                } else {
                    if (p.alpha < p.maxAlpha) {
                        p.alpha = Math.min(p.maxAlpha, p.alpha + 0.04f);
                    }
                }

                particlePaint.setColor(p.color);
                particlePaint.setAlpha((int) (p.alpha * 255));

                if (animType == IconAnimType.MATRIX) {
                    textPaint.setColor(p.color);
                    textPaint.setAlpha((int) (p.alpha * 255));
                    canvas.drawText(p.charText, p.x, p.y, textPaint);
                } else if (animType == IconAnimType.SAKURA) {
                    canvas.save();
                    canvas.rotate(p.rotation, p.x, p.y);
                    canvas.drawOval(p.x - p.radius * 1.6f, p.y - p.radius * 0.9f, p.x + p.radius * 1.6f, p.y + p.radius * 0.9f, particlePaint);
                    canvas.restore();
                } else if (animType == IconAnimType.TURBO) {
                    canvas.save();
                    canvas.drawLine(p.x, p.y, p.x - p.vx * 3.5f, p.y - p.vy * 3.5f, particlePaint);
                    canvas.restore();
                } else if (animType == IconAnimType.GLITCH) {
                    canvas.drawRect(p.x - p.radius * 4f, p.y - p.radius * 0.6f, p.x + p.radius * 4f, p.y + p.radius * 0.6f, particlePaint);
                } else if (animType == IconAnimType.PURE) {
                    canvas.save();
                    canvas.rotate(p.rotation, p.x, p.y);
                    canvas.drawLine(p.x - p.radius, p.y, p.x + p.radius, p.y, particlePaint);
                    canvas.drawLine(p.x, p.y - p.radius, p.x, p.y + p.radius, particlePaint);
                    canvas.restore();
                } else if (animType == IconAnimType.COSMOS || animType == IconAnimType.AMETHYST || animType == IconAnimType.PREMIUM) {
                    canvas.save();
                    canvas.rotate(p.rotation, p.x, p.y);
                    canvas.drawCircle(p.x, p.y, p.radius * 0.8f, particlePaint);
                    canvas.drawLine(p.x - p.radius * 1.5f, p.y, p.x + p.radius * 1.5f, p.y, particlePaint);
                    canvas.drawLine(p.x, p.y - p.radius * 1.5f, p.x, p.y + p.radius * 1.5f, particlePaint);
                    canvas.restore();
                } else {
                    canvas.drawCircle(p.x, p.y, p.radius, particlePaint);
                }
            }
            invalidate();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Bespoke Icon Aura View: Rotating Geometries, Gears, HUD, Orbitals
    // ──────────────────────────────────────────────────────────────────────────
    private static class BespokeIconAuraView extends View {
        private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint fxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final IconAnimType animType;
        private final int primaryColor, secondaryColor, glowColor;
        private float ringProgress;
        private float rotationAngle;
        private final Path hexPath = new Path();
        private final Random random = new Random();

        public BespokeIconAuraView(Context context, IconAnimType type, int c1, int c2, int c3) {
            super(context);
            this.animType = type;
            this.primaryColor = c1;
            this.secondaryColor = c2;
            this.glowColor = c3;

            ringPaint.setStyle(Paint.Style.STROKE);
            fxPaint.setStyle(Paint.Style.STROKE);
            fxPaint.setStrokeWidth(AndroidUtilities.dp(1.8f));

            ValueAnimator ringAnim = ValueAnimator.ofFloat(0f, 1f);
            ringAnim.setDuration(1900);
            ringAnim.setRepeatCount(ValueAnimator.INFINITE);
            ringAnim.addUpdateListener(animation -> {
                ringProgress = (float) animation.getAnimatedValue();
                invalidate();
            });
            ringAnim.start();

            ValueAnimator rotAnim = ValueAnimator.ofFloat(0f, 360f);
            rotAnim.setDuration(5000);
            rotAnim.setRepeatCount(ValueAnimator.INFINITE);
            rotAnim.addUpdateListener(animation -> {
                rotationAngle = (float) animation.getAnimatedValue();
                invalidate();
            });
            rotAnim.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            float maxRadius = getWidth() * 0.48f;
            float r = AndroidUtilities.dp(68);

            // 1. Shockwave Pulse
            float r1 = ringProgress * maxRadius;
            int a1 = (int) ((1f - ringProgress) * 140);
            ringPaint.setColor(primaryColor);
            ringPaint.setAlpha(a1);
            ringPaint.setStrokeWidth(AndroidUtilities.dp(2.5f * (1f - ringProgress)));

            if (animType == IconAnimType.MATRIX || animType == IconAnimType.CYBER) {
                drawHexagon(canvas, cx, cy, r1, ringPaint);
            } else if (animType == IconAnimType.RUBY || animType == IconAnimType.AMETHYST) {
                canvas.save();
                canvas.rotate(45, cx, cy);
                canvas.drawRect(cx - r1 * 0.7f, cy - r1 * 0.7f, cx + r1 * 0.7f, cy + r1 * 0.7f, ringPaint);
                canvas.restore();
            } else {
                canvas.drawCircle(cx, cy, r1, ringPaint);
            }

            // 2. Specialized Thematic Overlays
            if (animType == IconAnimType.CYBER || animType == IconAnimType.MATRIX) {
                fxPaint.setColor(primaryColor);
                fxPaint.setAlpha(180);
                canvas.save();
                canvas.rotate(rotationAngle, cx, cy);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 15, 60, false, fxPaint);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 105, 60, false, fxPaint);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 195, 60, false, fxPaint);
                canvas.drawArc(cx - r, cy - r, cx + r, cy + r, 285, 60, false, fxPaint);
                canvas.restore();
            } else if (animType == IconAnimType.PLASMA || animType == IconAnimType.GLITCH) {
                if (random.nextFloat() < 0.5f) {
                    fxPaint.setColor(primaryColor);
                    fxPaint.setAlpha(220);
                    float x1 = cx + (random.nextFloat() - 0.5f) * r * 1.8f;
                    float y1 = cy + (random.nextFloat() - 0.5f) * r * 1.8f;
                    float x2 = x1 + (random.nextFloat() - 0.5f) * AndroidUtilities.dp(30);
                    float y2 = y1 + (random.nextFloat() - 0.5f) * AndroidUtilities.dp(30);
                    canvas.drawLine(x1, y1, x2, y2, fxPaint);
                    canvas.drawLine(x2, y2, x2 + (random.nextFloat() - 0.5f) * AndroidUtilities.dp(20), y2 + (random.nextFloat() - 0.5f) * AndroidUtilities.dp(20), fxPaint);
                }
            } else if (animType == IconAnimType.SINGULARITY || animType == IconAnimType.COSMOS) {
                fxPaint.setColor(secondaryColor);
                fxPaint.setAlpha(150);
                canvas.save();
                canvas.rotate(rotationAngle * 0.8f, cx, cy);
                canvas.drawOval(cx - r * 1.3f, cy - r * 0.65f, cx + r * 1.3f, cy + r * 0.65f, fxPaint);
                canvas.restore();

                canvas.save();
                canvas.rotate(-rotationAngle * 0.6f + 45, cx, cy);
                fxPaint.setColor(glowColor);
                canvas.drawOval(cx - r * 1.1f, cy - r * 0.5f, cx + r * 1.1f, cy + r * 0.5f, fxPaint);
                canvas.restore();
            } else if (animType == IconAnimType.BRONZE || animType == IconAnimType.VINTAGE) {
                fxPaint.setColor(primaryColor);
                fxPaint.setAlpha(160);
                canvas.save();
                canvas.rotate(rotationAngle * 0.5f, cx, cy);
                for (int i = 0; i < 8; i++) {
                    canvas.drawLine(cx, cy - r, cx, cy - r - AndroidUtilities.dp(8), fxPaint);
                    canvas.rotate(45, cx, cy);
                }
                canvas.restore();
            } else if (animType == IconAnimType.SUNSET || animType == IconAnimType.LAVA) {
                fxPaint.setColor(primaryColor);
                fxPaint.setAlpha(140);
                canvas.save();
                canvas.rotate(rotationAngle * 0.4f, cx, cy);
                for (int i = 0; i < 12; i++) {
                    canvas.drawLine(cx, cy - r, cx, cy - r - AndroidUtilities.dp(6), fxPaint);
                    canvas.rotate(30, cx, cy);
                }
                canvas.restore();
            }
        }

        private void drawHexagon(Canvas canvas, float cx, float cy, float radius, Paint paint) {
            hexPath.reset();
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i - 30);
                float x = (float) (cx + radius * Math.cos(angle));
                float y = (float) (cy + radius * Math.sin(angle));
                if (i == 0) hexPath.moveTo(x, y);
                else hexPath.lineTo(x, y);
            }
            hexPath.close();
            canvas.drawPath(hexPath, paint);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. Liquid Glass Specular Shimmer Overlay
    // ──────────────────────────────────────────────────────────────────────────
    private static class LiquidGlassSpecularOverlay extends View {
        private final Paint shimmerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Matrix shaderMatrix = new Matrix();
        private LinearGradient shimmerShader;
        private float shimmerProgress = -1.2f;

        public LiquidGlassSpecularOverlay(Context context, int tintColor) {
            super(context);
        }

        public void startShimmer() {
            ValueAnimator animator = ValueAnimator.ofFloat(-1.2f, 2.2f);
            animator.setDuration(1300);
            animator.setStartDelay(250);
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
                        new int[]{Color.TRANSPARENT, Color.argb(180, 255, 255, 255), Color.TRANSPARENT},
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

    // ──────────────────────────────────────────────────────────────────────────
    // 5. Status Badge with Live Pulsing Indicator Dot
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
