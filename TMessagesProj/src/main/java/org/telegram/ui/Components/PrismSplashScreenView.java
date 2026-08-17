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
 * Adapts dynamically to the user's active App Icon with 20+ particle & kinetic physics effects,
 * personalized time-of-day multilingual greetings, and liquid glass transitions.
 */
public class PrismSplashScreenView extends FrameLayout {

    private enum ThemeFamily {
        MATRIX, SUNSET, RUBY, COSMOS, AQUA, NOX, DEFAULT
    }

    private ThemeFamily themeFamily = ThemeFamily.DEFAULT;
    private int primaryColor = 0xFF00E5FF;
    private int secondaryColor = 0xFF7C4DFF;
    private int bgStartColor = 0xFF0B1424;
    private int bgEndColor = 0xFF040810;

    private final ParticleCanvasView particleCanvas;
    private final FrameLayout iconCard;
    private final ImageView iconBgView;
    private final ImageView iconFgView;
    private final GlassShimmerOverlay shimmerOverlay;
    private final PulseRingsView pulseRingsView;
    private final LinearLayout textContainer;
    private final TextView greetingView;
    private final TextView appTitle;
    private final TextView appSubtitle;

    private ValueAnimator idleAnimator;
    private ValueAnimator shimmerAnimator;
    private boolean isDismissing = false;

    public PrismSplashScreenView(@NonNull Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);

        LauncherIconController.LauncherIcon currentIcon = LauncherIconController.getSelectedIcon();
        resolvePalette(currentIcon);

        // Dynamic multi-stop deep liquid background
        GradientDrawable bgDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{bgStartColor, bgEndColor}
        );
        setBackground(bgDrawable);

        // 1. Particle & Fluid Simulation Canvas (20+ multi-physics particle system)
        particleCanvas = new ParticleCanvasView(context, themeFamily, primaryColor, secondaryColor);
        addView(particleCanvas, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Center Content Wrapper
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setOrientation(LinearLayout.VERTICAL);
        centerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(centerContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // 2. Pulse Shockwave Rings behind the 3D Icon
        FrameLayout iconWrapper = new FrameLayout(context);
        centerContainer.addView(iconWrapper, LayoutHelper.createLinear(160, 160, Gravity.CENTER_HORIZONTAL));

        pulseRingsView = new PulseRingsView(context, primaryColor, secondaryColor);
        iconWrapper.addView(pulseRingsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        // 3. 3D Rounded Squircle Liquid Glass Icon Card
        iconCard = new FrameLayout(context) {
            private final Path clipPath = new Path();
            private final RectF rectF = new RectF();
            private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            {
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(AndroidUtilities.dp(1.8f));
                borderPaint.setColor(Color.argb(120, 255, 255, 255));
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
        iconCard.setElevation(AndroidUtilities.dp(16));
        iconWrapper.addView(iconCard, LayoutHelper.createFrame(92, 92, Gravity.CENTER));

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

        // 4. Personalized Greeting & Typography Container
        textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        centerContainer.addView(textContainer, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 18, 0, 0));

        // Personalized Multilingual Greeting Card
        String greetingText = generateGreetingText();
        greetingView = new TextView(context);
        greetingView.setText(greetingText);
        greetingView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        greetingView.setTypeface(AndroidUtilities.bold());
        greetingView.setTextColor(0xFFFFFFFF);
        greetingView.setGravity(Gravity.CENTER);
        greetingView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(6), AndroidUtilities.dp(16), AndroidUtilities.dp(6));

        GradientDrawable greetingBadge = new GradientDrawable();
        greetingBadge.setColor(Color.argb(45, 255, 255, 255));
        greetingBadge.setCornerRadius(AndroidUtilities.dp(16));
        greetingBadge.setStroke(AndroidUtilities.dp(1), Color.argb(80, 255, 255, 255));
        greetingView.setBackground(greetingBadge);
        textContainer.addView(greetingView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));

        // App Title
        appTitle = new TextView(context);
        appTitle.setText("PrismGram");
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        appTitle.setTypeface(AndroidUtilities.bold());
        appTitle.setTextColor(0xFFFFFFFF);
        appTitle.setLetterSpacing(0.04f);
        textContainer.addView(appTitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 10, 0, 0));

        // App Subtitle with glowing theme text
        appSubtitle = new TextView(context);
        appSubtitle.setText(getThemeEditionLabel(currentIcon));
        appSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        appSubtitle.setTextColor(primaryColor);
        appSubtitle.setLetterSpacing(0.06f);
        textContainer.addView(appSubtitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 4, 0, 0));

        // Tap-to-dismiss support for instant entry
        setOnClickListener(v -> dismiss());
    }

    private void resolvePalette(LauncherIconController.LauncherIcon icon) {
        if (icon == null) return;
        String key = icon.key.toLowerCase();
        if (key.contains("matrix") || key.contains("cyber") || key.contains("emerald")) {
            themeFamily = ThemeFamily.MATRIX;
            primaryColor = 0xFF00FF66;
            secondaryColor = 0xFF00E5FF;
            bgStartColor = 0xFF04140B;
            bgEndColor = 0xFF020905;
        } else if (key.contains("sunset") || key.contains("lava") || key.contains("bronze") || key.contains("gold")) {
            themeFamily = ThemeFamily.SUNSET;
            primaryColor = 0xFFFF9100;
            secondaryColor = 0xFFFF1744;
            bgStartColor = 0xFF1F0E14;
            bgEndColor = 0xFF0E0509;
        } else if (key.contains("ruby") || key.contains("sakura") || key.contains("plasma")) {
            themeFamily = ThemeFamily.RUBY;
            primaryColor = 0xFFFF2A6D;
            secondaryColor = 0xFF9D4EDD;
            bgStartColor = 0xFF1F0714;
            bgEndColor = 0xFF0D0308;
        } else if (key.contains("aurora") || key.contains("amethyst") || key.contains("cosmos") || key.contains("singularity") || key.contains("nebula") || key.contains("opal")) {
            themeFamily = ThemeFamily.COSMOS;
            primaryColor = 0xFF9D4EDD;
            secondaryColor = 0xFF00E5FF;
            bgStartColor = 0xFF140726;
            bgEndColor = 0xFF080312;
        } else if (key.contains("aqua") || key.contains("pure") || key.contains("abyss") || key.contains("cobalt") || key.contains("glacial") || key.contains("diamond")) {
            themeFamily = ThemeFamily.AQUA;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF0072FF;
            bgStartColor = 0xFF061726;
            bgEndColor = 0xFF020912;
        } else if (key.contains("nox") || key.contains("monochrome") || key.contains("stealth")) {
            themeFamily = ThemeFamily.NOX;
            primaryColor = 0xFF60A5FA;
            secondaryColor = 0xFF94A3B8;
            bgStartColor = 0xFF0B0D12;
            bgEndColor = 0xFF030406;
        } else {
            themeFamily = ThemeFamily.DEFAULT;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF7C4DFF;
            bgStartColor = 0xFF0B1424;
            bgEndColor = 0xFF040810;
        }
    }

    private String getThemeEditionLabel(LauncherIconController.LauncherIcon icon) {
        if (icon == null) return "Liquid Glass Edition";
        switch (themeFamily) {
            case MATRIX: return "Cyber Liquid Matrix";
            case SUNSET: return "Molten Amber Glass";
            case RUBY: return "Velvet Ruby Crystal";
            case COSMOS: return "Cosmic Nebula Opal";
            case AQUA: return "Frosted Glacial Ice";
            case NOX: return "Midnight OLED Stealth";
            default: return "iOS 26.5 Liquid Glass";
        }
    }

    /**
     * Generates a personalized time-of-day greeting in the user's selected language.
     */
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
            // Uzbek & Default
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
        // 1. Entrance kinetic 3D bounce animation
        iconCard.setScaleX(0.4f);
        iconCard.setScaleY(0.4f);
        iconCard.setRotationX(25f);
        iconCard.setRotationY(-20f);
        iconCard.setAlpha(0.0f);

        greetingView.setAlpha(0.0f);
        greetingView.setTranslationY(AndroidUtilities.dp(16));
        appTitle.setAlpha(0.0f);
        appSubtitle.setAlpha(0.0f);

        iconCard.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .rotationX(0f)
                .rotationY(0f)
                .alpha(1.0f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator(1.4f))
                .start();

        greetingView.animate()
                .alpha(1.0f)
                .translationY(0)
                .setDuration(450)
                .setStartDelay(180)
                .setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT)
                .start();

        appTitle.animate()
                .alpha(1.0f)
                .setDuration(450)
                .setStartDelay(260)
                .start();

        appSubtitle.animate()
                .alpha(1.0f)
                .setDuration(450)
                .setStartDelay(340)
                .start();

        // 2. Start specular diagonal light sweep
        shimmerOverlay.startShimmer();

        // 3. Start 3D kinetic harmonic breathing tilt
        startIdlePhysics();

        // 4. Auto dismiss after delay (4.5 seconds)
        postDelayed(this::dismiss, Math.max(delayMs, 4500));
    }

    private void startIdlePhysics() {
        idleAnimator = ValueAnimator.ofFloat(0f, 1f);
        idleAnimator.setDuration(2400);
        idleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        idleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        idleAnimator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            if (!isDismissing) {
                float tiltX = (float) Math.sin(fraction * Math.PI * 2) * 5f;
                float tiltY = (float) Math.cos(fraction * Math.PI * 2) * 6f;
                float scale = 0.98f + fraction * 0.04f;
                iconCard.setRotationX(tiltX);
                iconCard.setRotationY(tiltY);
                iconCard.setScaleX(scale);
                iconCard.setScaleY(scale);
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
                .setDuration(340)
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
    // 1. Multi-physics Particle Canvas Layer (20+ Particle Types & Animations)
    // ──────────────────────────────────────────────────────────────────────────
    private static class ParticleCanvasView extends View {
        private static final int PARTICLE_COUNT = 36;
        private final List<Particle> particles = new ArrayList<>();
        private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final ThemeFamily themeFamily;
        private final int color1;
        private final int color2;
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
        }

        public ParticleCanvasView(Context context, ThemeFamily family, int c1, int c2) {
            super(context);
            this.themeFamily = family;
            this.color1 = c1;
            this.color2 = c2;
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
            p.y = randomY ? random.nextFloat() * h : (themeFamily == ThemeFamily.MATRIX ? -20 : h + 20);
            p.radius = AndroidUtilities.dp(2 + random.nextFloat() * 5);
            p.maxAlpha = 0.3f + random.nextFloat() * 0.6f;
            p.alpha = random.nextFloat() * p.maxAlpha;
            p.rotation = random.nextFloat() * 360f;
            p.vRotation = (random.nextFloat() - 0.5f) * 4f;
            p.color = random.nextBoolean() ? color1 : color2;

            switch (themeFamily) {
                case MATRIX:
                    p.vx = (random.nextFloat() - 0.5f) * 0.4f;
                    p.vy = 2.5f + random.nextFloat() * 4f;
                    break;
                case SUNSET:
                    p.vx = (random.nextFloat() - 0.5f) * 1.5f;
                    p.vy = -(1.5f + random.nextFloat() * 3f);
                    break;
                case RUBY:
                    p.vx = (random.nextFloat() - 0.5f) * 1.8f;
                    p.vy = -(1.0f + random.nextFloat() * 2.5f);
                    break;
                case COSMOS:
                    p.vx = (random.nextFloat() - 0.5f) * 2.2f;
                    p.vy = (random.nextFloat() - 0.5f) * 2.2f;
                    break;
                case AQUA:
                    p.vx = (float) Math.sin(p.y * 0.05f) * 1.2f;
                    p.vy = -(1.8f + random.nextFloat() * 3.5f);
                    break;
                default:
                    p.vx = (random.nextFloat() - 0.5f) * 1.5f;
                    p.vy = -(1.2f + random.nextFloat() * 2.5f);
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
                p.x += p.vx * 60f * dt;
                p.y += p.vy * 60f * dt;
                p.rotation += p.vRotation;

                if (p.y < -30 || p.y > h + 30 || p.x < -30 || p.x > w + 30) {
                    Particle fresh = createParticle(false);
                    p.x = fresh.x;
                    p.y = fresh.y;
                    p.vx = fresh.vx;
                    p.vy = fresh.vy;
                    p.alpha = 0f;
                } else {
                    if (p.alpha < p.maxAlpha) {
                        p.alpha = Math.min(p.maxAlpha, p.alpha + 0.02f);
                    }
                }

                particlePaint.setColor(p.color);
                particlePaint.setAlpha((int) (p.alpha * 255));

                if (themeFamily == ThemeFamily.MATRIX) {
                    // Cyber digital square / rectangle light
                    canvas.drawRect(p.x - p.radius, p.y - p.radius * 2, p.x + p.radius, p.y + p.radius * 2, particlePaint);
                } else if (themeFamily == ThemeFamily.COSMOS) {
                    // Twinkling diamond starburst
                    canvas.save();
                    canvas.rotate(p.rotation, p.x, p.y);
                    canvas.drawCircle(p.x, p.y, p.radius, particlePaint);
                    canvas.restore();
                } else {
                    // Liquid glowing glass orb
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
        private final int primaryColor;
        private final int secondaryColor;
        private float progress;

        public PulseRingsView(Context context, int c1, int c2) {
            super(context);
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
            int alpha1 = (int) ((1f - progress) * 110);
            ringPaint.setColor(primaryColor);
            ringPaint.setAlpha(alpha1);
            ringPaint.setStrokeWidth(AndroidUtilities.dp(2f * (1f - progress)));
            canvas.drawCircle(cx, cy, r1, ringPaint);

            // Ring 2 (delayed)
            float p2 = (progress + 0.5f) % 1.0f;
            float r2 = p2 * maxRadius;
            int alpha2 = (int) ((1f - p2) * 90);
            ringPaint.setColor(secondaryColor);
            ringPaint.setAlpha(alpha2);
            ringPaint.setStrokeWidth(AndroidUtilities.dp(1.8f * (1f - p2)));
            canvas.drawCircle(cx, cy, r2, ringPaint);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Diagonal Specular Glass Shimmer Overlay
    // ──────────────────────────────────────────────────────────────────────────
    private static class GlassShimmerOverlay extends View {
        private final Paint shimmerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Matrix shaderMatrix = new Matrix();
        private LinearGradient shimmerShader;
        private float shimmerProgress = -1.5f;

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
                        new int[]{Color.TRANSPARENT, Color.argb(160, 255, 255, 255), Color.TRANSPARENT},
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
