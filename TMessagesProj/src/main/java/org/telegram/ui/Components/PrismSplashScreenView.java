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
 * 🌟 PrismSplashScreenView — Nafis Minimalizm va Har bir Ikonka uchun Alohida Ishlangan Effektlar.
 *
 * Konseptsiya:
 * 1. Sof Minimalistik Estetika: Toza, sokin, ko'zni charchatmaydigan, premium Apple/VisionOS uslubidagi nafislik.
 * 2. Har bir Ikonka uchun Maxsus Ishlangan Minimalist Effektlar:
 *    - 🌸 SAKURA: Mayin suzuvchi gilos guli yaproqlari va nafis konsentrik suv to'lqini.
 *    - 🟢 MATRIX: Yupqa kiber ma'lumot oqimlari va kvant pulsatsiyasi.
 *    - 🌌 COSMOS: Nafis elliptik orbital halqa va aylanuvchi yorug'lik yo'ldoshi.
 *    - 💎 RUBY / AMETHYST: Nozik qirrali kristall konturi va prizmatik yaltirash.
 *    - ⚡ PLASMA: Nozik elektr halqasi va energetik aura.
 *    - 🌅 SUNSET: Mayin quyosh gorizonti va iliq ko'tariluvchi zarrachalar.
 *    - ❄️ PURE: Tiniq muz kristallari va shaffof shisha kaustikasi.
 *    - ⚙️ BRONZE: Nafis xronometr siferblati va klassik oltinrang changlar.
 *    - 🏎️ TURBO: Aerodinamik tezlik chiziqlari va giper-fazoviy puls.
 *    - 🌈 SPECTRUM: Suyuq shisha jilosi va kamalak rangli nozik jilo.
 * 3. Sokin va Mayin 3D Suzuvchi Ikonka (Floating Card Physics).
 */
public class PrismSplashScreenView extends FrameLayout {

    public enum IconTheme {
        MATRIX, SAKURA, COSMOS, RUBY, PLASMA, SUNSET, PURE, BRONZE, TURBO, SPECTRUM, DEFAULT
    }

    private IconTheme iconTheme = IconTheme.DEFAULT;
    private int primaryColor = 0xFF00E5FF;
    private int secondaryColor = 0xFF7C4DFF;
    private int accentGlowColor = 0xFFFF007F;

    private int bgTop = 0xFF0B1424;
    private int bgMid = 0xFF121E36;
    private int bgBottom = 0xFF040810;
    private int ambientGlowColor = 0x3300E5FF;

    private final MinimalistBackgroundView backgroundView;
    private final BespokeMinimalistEffectView effectView;
    private final MinimalistFloatingCardView heroCardView;
    private final TextView greetingView;
    private final TextView appTitle;
    private final TextView appSubtitle;

    private boolean isDismissing = false;

    public PrismSplashScreenView(@NonNull Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);
        setElevation(AndroidUtilities.dp(300));

        LauncherIconController.LauncherIcon currentIcon = LauncherIconController.getSelectedIcon();
        resolveThemeAndPalette(currentIcon);

        // 1. Mayin va chuqur minimalist fon
        backgroundView = new MinimalistBackgroundView(context, bgTop, bgMid, bgBottom, ambientGlowColor);
        addView(backgroundView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 2. Har bir ikonka uchun maxsus yasalgan nafis minimalist effektlar
        effectView = new BespokeMinimalistEffectView(context, iconTheme, primaryColor, secondaryColor, accentGlowColor);
        addView(effectView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Markaziy asosiy konteyner
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setOrientation(LinearLayout.VERTICAL);
        centerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(centerContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // 3. Nafis suzuvchi minimalist 3D ikonka kartochkasi
        heroCardView = new MinimalistFloatingCardView(context, currentIcon, primaryColor);
        centerContainer.addView(heroCardView, LayoutHelper.createLinear(200, 200, Gravity.CENTER_HORIZONTAL));

        // 4. Tipografiya (Minimalist & Clean)
        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        centerContainer.addView(textContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 20, 0, 0));

        greetingView = new TextView(context);
        greetingView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        greetingView.setTextColor(Color.argb(200, 255, 255, 255));
        greetingView.setGravity(Gravity.CENTER);
        greetingView.setText(generateGreetingText());
        textContainer.addView(greetingView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        appTitle = new TextView(context);
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
        appTitle.setTextColor(Color.WHITE);
        appTitle.setTypeface(AndroidUtilities.bold());
        appTitle.setGravity(Gravity.CENTER);
        appTitle.setText("Prisma");
        textContainer.addView(appTitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 4));

        appSubtitle = new TextView(context);
        appSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        appSubtitle.setTextColor(Color.argb(220, Color.red(primaryColor), Color.green(primaryColor), Color.blue(primaryColor)));
        appSubtitle.setGravity(Gravity.CENTER);
        appSubtitle.setText(getThemeTitle());
        textContainer.addView(appSubtitle, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    private void resolveThemeAndPalette(LauncherIconController.LauncherIcon icon) {
        if (icon == null) icon = LauncherIconController.LauncherIcon.DEFAULT;
        String key = icon.key.toLowerCase();

        if (key.contains("matrix")) {
            iconTheme = IconTheme.MATRIX;
            primaryColor = 0xFF00FF88;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFF39FF14;
            bgTop = 0xFF04120B;
            bgMid = 0xFF082215;
            bgBottom = 0xFF020905;
            ambientGlowColor = 0x3000FF88;
        } else if (key.contains("sakura")) {
            iconTheme = IconTheme.SAKURA;
            primaryColor = 0xFFF472B6;
            secondaryColor = 0xFFC084FC;
            accentGlowColor = 0xFFFFD1DC;
            bgTop = 0xFF190C1B;
            bgMid = 0xFF2D142F;
            bgBottom = 0xFF0B030C;
            ambientGlowColor = 0x38F472B6;
        } else if (key.contains("cosmos") || key.contains("singularity")) {
            iconTheme = IconTheme.COSMOS;
            primaryColor = 0xFF8B5CF6;
            secondaryColor = 0xFF00E5FF;
            accentGlowColor = 0xFFD946EF;
            bgTop = 0xFF0B0924;
            bgMid = 0xFF171040;
            bgBottom = 0xFF03020E;
            ambientGlowColor = 0x388B5CF6;
        } else if (key.contains("ruby") || key.contains("amethyst")) {
            iconTheme = IconTheme.RUBY;
            primaryColor = 0xFFE11D48;
            secondaryColor = 0xFFA855F7;
            accentGlowColor = 0xFFFB7185;
            bgTop = 0xFF1F0511;
            bgMid = 0xFF360A1E;
            bgBottom = 0xFF0A0105;
            ambientGlowColor = 0x38E11D48;
        } else if (key.contains("plasma") || key.contains("cyber") || key.contains("glitch")) {
            iconTheme = IconTheme.PLASMA;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFFFF007F;
            accentGlowColor = 0xFFD946EF;
            bgTop = 0xFF0C0928;
            bgMid = 0xFF161044;
            bgBottom = 0xFF030210;
            ambientGlowColor = 0x3500E5FF;
        } else if (key.contains("sunset") || key.contains("lava")) {
            iconTheme = IconTheme.SUNSET;
            primaryColor = 0xFFFF9100;
            secondaryColor = 0xFFFF1744;
            accentGlowColor = 0xFFFFD700;
            bgTop = 0xFF1F0804;
            bgMid = 0xFF381007;
            bgBottom = 0xFF0A0200;
            ambientGlowColor = 0x38FF9100;
        } else if (key.contains("pure") || key.contains("aqua") || key.contains("abyss") || key.contains("aurora")) {
            iconTheme = IconTheme.PURE;
            primaryColor = 0xFF00E5FF;
            secondaryColor = 0xFF00B0FF;
            accentGlowColor = 0xFF80D8FF;
            bgTop = 0xFF041320;
            bgMid = 0xFF08243C;
            bgBottom = 0xFF01060C;
            ambientGlowColor = 0x3500E5FF;
        } else if (key.contains("bronze") || key.contains("vintage")) {
            iconTheme = IconTheme.BRONZE;
            primaryColor = 0xFFD97706;
            secondaryColor = 0xFFB45309;
            accentGlowColor = 0xFFF59E0B;
            bgTop = 0xFF1B0F04;
            bgMid = 0xFF301B08;
            bgBottom = 0xFF090401;
            ambientGlowColor = 0x35D97706;
        } else if (key.contains("turbo")) {
            iconTheme = IconTheme.TURBO;
            primaryColor = 0xFFFF5252;
            secondaryColor = 0xFFFF7A00;
            accentGlowColor = 0xFFFFAB40;
            bgTop = 0xFF1E0404;
            bgMid = 0xFF360808;
            bgBottom = 0xFF080101;
            ambientGlowColor = 0x38FF5252;
        } else {
            iconTheme = IconTheme.SPECTRUM;
            primaryColor = 0xFF00F0FF;
            secondaryColor = 0xFFFF0055;
            accentGlowColor = 0xFFFFD700;
            bgTop = 0xFF0E0822;
            bgMid = 0xFF1D1040;
            bgBottom = 0xFF04020B;
            ambientGlowColor = 0x3500F0FF;
        }
    }

    private String getThemeTitle() {
        switch (iconTheme) {
            case MATRIX: return "Cybernetic Edition";
            case SAKURA: return "Sakura Blossom Edition";
            case COSMOS: return "Cosmic Orbital Edition";
            case RUBY: return "Ruby Diamond Edition";
            case PLASMA: return "Tesla Plasma Edition";
            case SUNSET: return "Sunset Horizon Edition";
            case PURE: return "Pure Aqua Edition";
            case BRONZE: return "Vintage Bronze Edition";
            case TURBO: return "Turbo Velocity Edition";
            default: return "Prism Spectrum Edition";
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
        heroCardView.setScaleX(0.4f);
        heroCardView.setScaleY(0.4f);
        heroCardView.setAlpha(0.0f);

        greetingView.setAlpha(0.0f);
        greetingView.setTranslationY(AndroidUtilities.dp(14));
        appTitle.setAlpha(0.0f);
        appSubtitle.setAlpha(0.0f);

        heroCardView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(700)
                .setInterpolator(new OvershootInterpolator(1.2f))
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

        postDelayed(this::dismiss, Math.max(delayMs, 4200));
    }

    public void dismiss() {
        if (isDismissing) return;
        isDismissing = true;

        animate()
                .alpha(0.0f)
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(360)
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
    // 1. Sokin va Mayin Minimalist Fon (Soft Ambient Radial Bloom)
    // ──────────────────────────────────────────────────────────────────────────
    private static class MinimalistBackgroundView extends View {
        private final int c1, c2, c3, ambientColor;
        private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private LinearGradient linearGradient;
        private RadialGradient radialGradient;
        private float breath = 1.0f;
        private int lastW, lastH;

        public MinimalistBackgroundView(Context context, int c1, int c2, int c3, int ambientColor) {
            super(context);
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
            this.ambientColor = ambientColor;

            ValueAnimator breathAnim = ValueAnimator.ofFloat(0.92f, 1.08f);
            breathAnim.setDuration(3600);
            breathAnim.setRepeatCount(ValueAnimator.INFINITE);
            breathAnim.setRepeatMode(ValueAnimator.REVERSE);
            breathAnim.addUpdateListener(animation -> {
                breath = (float) animation.getAnimatedValue();
                invalidate();
            });
            breathAnim.start();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w > 0 && h > 0) {
                lastW = w;
                lastH = h;
                linearGradient = new LinearGradient(
                        0, 0, 0, h,
                        new int[]{c1, c2, c3},
                        new float[]{0.0f, 0.45f, 1.0f},
                        Shader.TileMode.CLAMP
                );
                bgPaint.setShader(linearGradient);

                float cx = w / 2f;
                float cy = h * 0.45f;
                float radius = AndroidUtilities.dp(220);
                radialGradient = new RadialGradient(
                        cx, cy, radius,
                        new int[]{ambientColor, Color.TRANSPARENT},
                        new float[]{0.0f, 1.0f},
                        Shader.TileMode.CLAMP
                );
                glowPaint.setShader(radialGradient);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (lastW == 0 || lastH == 0) return;
            canvas.drawRect(0, 0, lastW, lastH, bgPaint);

            canvas.save();
            float cx = lastW / 2f;
            float cy = lastH * 0.45f;
            canvas.scale(breath, breath, cx, cy);
            canvas.drawCircle(cx, cy, AndroidUtilities.dp(220), glowPaint);
            canvas.restore();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Har bir Ikonka uchun Alohida Ishlangan Nafis Minimalist Effektlar
    // ──────────────────────────────────────────────────────────────────────────
    private static class BespokeMinimalistEffectView extends View {
        private final IconTheme theme;
        private final int color1, color2, color3;
        private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final Matrix matrix = new Matrix();
        private final Camera camera = new Camera();
        private float progress;

        // Sakura / Water Motes
        private static final int MOTE_COUNT = 24;
        private final float[][] motes = new float[MOTE_COUNT][6]; // x, y, vx, vy, rot, size
        private final Random random = new Random();
        private long lastTime;

        public BespokeMinimalistEffectView(Context context, IconTheme theme, int c1, int c2, int c3) {
            super(context);
            this.theme = theme;
            this.color1 = c1;
            this.color2 = c2;
            this.color3 = c3;

            strokePaint.setStyle(Paint.Style.STROKE);
            fillPaint.setStyle(Paint.Style.FILL);

            for (int i = 0; i < MOTE_COUNT; i++) {
                motes[i][0] = (random.nextFloat() - 0.5f) * AndroidUtilities.dp(300);
                motes[i][1] = (random.nextFloat() - 0.5f) * AndroidUtilities.dp(500);
                motes[i][2] = (random.nextFloat() - 0.5f) * 30f;
                motes[i][3] = 40f + random.nextFloat() * 60f;
                motes[i][4] = random.nextFloat() * 360f;
                motes[i][5] = AndroidUtilities.dp(2.5f + random.nextFloat() * 4f);
            }

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

            long now = SystemClock.elapsedRealtime();
            float dt = lastTime == 0 ? 0.016f : Math.min((now - lastTime) / 1000f, 0.05f);
            lastTime = now;

            float cx = w / 2f;
            float cy = h * 0.45f;

            switch (theme) {
                case SAKURA:
                    // Mayin konsentrik suv to'lqinlari & havoda aylanuvchi nafis yaproqlar
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1.2f));
                    for (int i = 0; i < 3; i++) {
                        float ringProg = ((progress * 1.2f + i * 120) % 360f) / 360f;
                        float ringR = AndroidUtilities.dp(60) + ringProg * AndroidUtilities.dp(80);
                        strokePaint.setAlpha((int) ((1f - ringProg) * 90));
                        canvas.drawCircle(cx, cy, ringR, strokePaint);
                    }

                    fillPaint.setColor(color1);
                    for (int i = 0; i < MOTE_COUNT; i++) {
                        motes[i][0] += motes[i][2] * dt;
                        motes[i][1] += motes[i][3] * dt;
                        motes[i][4] += 2f;
                        if (motes[i][1] > AndroidUtilities.dp(300)) {
                            motes[i][1] = -AndroidUtilities.dp(300);
                            motes[i][0] = (random.nextFloat() - 0.5f) * AndroidUtilities.dp(260);
                        }
                        canvas.save();
                        canvas.translate(cx + motes[i][0], cy + motes[i][1]);
                        canvas.rotate(motes[i][4]);
                        canvas.scale(1f, (float) Math.sin(Math.toRadians(motes[i][4])));
                        fillPaint.setAlpha((int) (120 + Math.sin(Math.toRadians(motes[i][4])) * 80));
                        canvas.drawOval(-motes[i][5] * 1.5f, -motes[i][5] * 0.8f, motes[i][5] * 1.5f, motes[i][5] * 0.8f, fillPaint);
                        canvas.restore();
                    }
                    break;

                case COSMOS:
                    // Nafis 3D Elliptik Orbital Halqa va Aylanuvchi Yo'ldosh
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
                    strokePaint.setAlpha(120);

                    canvas.save();
                    camera.save();
                    camera.rotateX(66);
                    camera.rotateZ(progress * 0.7f);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(-cx, -cy);
                    matrix.postTranslate(cx, cy);
                    canvas.concat(matrix);

                    float orbitR = AndroidUtilities.dp(100);
                    canvas.drawCircle(cx, cy, orbitR, strokePaint);

                    float moonX = cx + (float) Math.cos(Math.toRadians(progress * 2)) * orbitR;
                    float moonY = cy + (float) Math.sin(Math.toRadians(progress * 2)) * orbitR;
                    fillPaint.setColor(color3);
                    fillPaint.setAlpha(240);
                    canvas.drawCircle(moonX, moonY, AndroidUtilities.dp(5), fillPaint);
                    canvas.restore();
                    break;

                case MATRIX:
                    // Yupqa kiber ma'lumot oqimlari va kvant pulsatsiyasi
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1f));
                    strokePaint.setAlpha(110);
                    for (int i = -3; i <= 3; i++) {
                        float lineX = cx + i * AndroidUtilities.dp(30);
                        float offset = ((progress * 4f + Math.abs(i) * 60) % 360f) / 360f;
                        float startY = cy - AndroidUtilities.dp(110) + offset * AndroidUtilities.dp(220);
                        canvas.drawLine(lineX, startY, lineX, startY + AndroidUtilities.dp(35), strokePaint);
                    }
                    break;

                case RUBY:
                    // Nozik qirrali kristall konturi
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1.4f));
                    strokePaint.setAlpha(140);

                    canvas.save();
                    canvas.rotate(progress * 0.4f, cx, cy);
                    path.reset();
                    float size = AndroidUtilities.dp(85);
                    for (int i = 0; i < 6; i++) {
                        float ang = (float) Math.toRadians(i * 60);
                        float px = cx + (float) Math.cos(ang) * size;
                        float py = cy + (float) Math.sin(ang) * size;
                        if (i == 0) path.moveTo(px, py);
                        else path.lineTo(px, py);
                    }
                    path.close();
                    canvas.drawPath(path, strokePaint);
                    canvas.restore();
                    break;

                case PLASMA:
                    // Nozik elektr halqasi
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
                    for (int i = 0; i < 2; i++) {
                        float rad = AndroidUtilities.dp(80 + i * 25) + (float) Math.sin(Math.toRadians(progress * 3 + i * 90)) * AndroidUtilities.dp(6);
                        strokePaint.setAlpha(100 - i * 30);
                        canvas.drawCircle(cx, cy, rad, strokePaint);
                    }
                    break;

                case SUNSET:
                    // Mayin ko'tariluvchi iliq zarrachalar
                    fillPaint.setColor(color1);
                    for (int i = 0; i < 12; i++) {
                        float prog = ((progress * 1.5f + i * 30) % 360f) / 360f;
                        float px = cx + (float) Math.sin(Math.toRadians(i * 60 + progress)) * AndroidUtilities.dp(70);
                        float py = cy + AndroidUtilities.dp(100) - prog * AndroidUtilities.dp(200);
                        fillPaint.setAlpha((int) ((1f - prog) * 160));
                        canvas.drawCircle(px, py, AndroidUtilities.dp(2f), fillPaint);
                    }
                    break;

                case BRONZE:
                    // Nafis xronometr siferblati belgilari
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1.2f));
                    strokePaint.setAlpha(120);
                    float dialR = AndroidUtilities.dp(90);
                    canvas.drawCircle(cx, cy, dialR, strokePaint);
                    for (int a = 0; a < 360; a += 30) {
                        float rad = (float) Math.toRadians(a);
                        float x1 = cx + (float) Math.cos(rad) * (dialR - AndroidUtilities.dp(6));
                        float y1 = cy + (float) Math.sin(rad) * (dialR - AndroidUtilities.dp(6));
                        float x2 = cx + (float) Math.cos(rad) * dialR;
                        float y2 = cy + (float) Math.sin(rad) * dialR;
                        canvas.drawLine(x1, y1, x2, y2, strokePaint);
                    }
                    break;

                case TURBO:
                    // Aerodinamik tezlik chiziqlari
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1.2f));
                    for (int i = 0; i < 6; i++) {
                        float prog = ((progress * 3f + i * 60) % 360f) / 360f;
                        float lineR = AndroidUtilities.dp(30) + prog * AndroidUtilities.dp(110);
                        strokePaint.setAlpha((int) ((1f - prog) * 160));
                        canvas.drawCircle(cx, cy, lineR, strokePaint);
                    }
                    break;

                default: // PURE & SPECTRUM
                    // Nafis prizmatik shisha aylanasi
                    strokePaint.setColor(color1);
                    strokePaint.setStrokeWidth(AndroidUtilities.dp(1.2f));
                    strokePaint.setAlpha(110);
                    float defR = AndroidUtilities.dp(85) + (float) Math.sin(Math.toRadians(progress * 2)) * AndroidUtilities.dp(8);
                    canvas.drawCircle(cx, cy, defR, strokePaint);
                    break;
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Nafis Suzuvchi Minimalist 3D Ikonka Kartochkasi
    // ──────────────────────────────────────────────────────────────────────────
    private static class MinimalistFloatingCardView extends FrameLayout {
        private final Camera camera = new Camera();
        private final Matrix matrix = new Matrix();
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rectF = new RectF();
        private final RectF shadowRect = new RectF();
        private final Path clipPath = new Path();

        private float tiltX, tiltY;
        private float elevationZ;

        public MinimalistFloatingCardView(Context context, LauncherIconController.LauncherIcon icon, int accentColor) {
            super(context);
            setWillNotDraw(false);

            shadowPaint.setStyle(Paint.Style.FILL);
            shadowPaint.setColor(0xFF000000);

            FrameLayout card = new FrameLayout(context) {
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
                }
            };
            card.setElevation(AndroidUtilities.dp(20));
            addView(card, LayoutHelper.createFrame(96, 96, Gravity.CENTER));

            ImageView bg = new ImageView(context);
            bg.setScaleType(ImageView.ScaleType.FIT_XY);
            bg.setImageResource(icon.background);
            card.addView(bg, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            ImageView fg = new ImageView(context);
            fg.setScaleType(ImageView.ScaleType.FIT_XY);
            fg.setImageResource(icon.foreground);
            card.addView(fg, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(AndroidUtilities.dp(1.8f));
            borderPaint.setColor(Color.argb(180, 255, 255, 255));

            // Mayin va sokin suzish fizikasi (±10° tilt, 20dp Z-float)
            ValueAnimator floatAnim = ValueAnimator.ofFloat(0f, 360f);
            floatAnim.setDuration(4000);
            floatAnim.setRepeatCount(ValueAnimator.INFINITE);
            floatAnim.addUpdateListener(animation -> {
                float val = (float) animation.getAnimatedValue();
                tiltX = (float) Math.sin(Math.toRadians(val)) * 9f;
                tiltY = (float) Math.cos(Math.toRadians(val * 0.8f)) * 12f;
                elevationZ = (float) Math.sin(Math.toRadians(val)) * 20f;
                invalidate();
            });
            floatAnim.start();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            // Yumshoq zamin soyasi
            float shadowY = cy + AndroidUtilities.dp(62);
            float shadowW = AndroidUtilities.dp(75) * (1f + elevationZ / 100f);
            float shadowH = AndroidUtilities.dp(14) * (1f - elevationZ / 120f);
            shadowRect.set(cx - shadowW / 2f, shadowY - shadowH / 2f, cx + shadowW / 2f, shadowY + shadowH / 2f);
            shadowPaint.setAlpha((int) Math.max(20, (70 - elevationZ)));
            canvas.drawOval(shadowRect, shadowPaint);

            // Sokin 3D burchak burilishi
            canvas.save();
            camera.save();
            camera.rotateX(tiltX);
            camera.rotateY(tiltY);
            camera.translate(0, 0, elevationZ);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-cx, -cy);
            matrix.postTranslate(cx, cy);
            canvas.concat(matrix);

            super.dispatchDraw(canvas);

            // Shisha qirrasi
            float cardLeft = cx - AndroidUtilities.dp(48);
            float cardTop = cy - AndroidUtilities.dp(48);
            float cardSize = AndroidUtilities.dp(96);
            rectF.set(cardLeft, cardTop, cardLeft + cardSize, cardTop + cardSize);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(24), AndroidUtilities.dp(24), borderPaint);

            canvas.restore();
        }
    }
}
