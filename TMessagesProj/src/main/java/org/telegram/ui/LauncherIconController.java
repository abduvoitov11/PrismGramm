package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

public class LauncherIconController {
    public static void tryFixLauncherIconIfNeeded() {
        for (LauncherIcon icon : LauncherIcon.values()) {
            if (isEnabled(icon)) {
                return;
            }
        }

        setIcon(LauncherIcon.DEFAULT);
    }

    public static boolean isEnabled(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        int i = ctx.getPackageManager().getComponentEnabledSetting(icon.getComponentName(ctx));
        return i == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || i == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && icon == LauncherIcon.DEFAULT;
    }

    private static LauncherIcon cachedSelectedIcon = null;
    private static android.graphics.Bitmap cachedIconBitmap;
    private static LauncherIcon cachedForIcon;

    public static LauncherIcon getSelectedIcon() {
        if (cachedSelectedIcon != null) {
            return cachedSelectedIcon;
        }
        for (LauncherIcon icon : LauncherIcon.values()) {
            if (isEnabled(icon)) {
                cachedSelectedIcon = icon;
                return icon;
            }
        }
        cachedSelectedIcon = LauncherIcon.DEFAULT;
        return cachedSelectedIcon;
    }

    public static android.graphics.Bitmap getSelectedIconBitmap(Context context) {
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        if (context == null) {
            return null;
        }
        LauncherIcon current = getSelectedIcon();
        if (cachedIconBitmap != null && cachedForIcon == current && !cachedIconBitmap.isRecycled()) {
            return cachedIconBitmap;
        }
        try {
            int size = org.telegram.messenger.AndroidUtilities.dp(48);
            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);

            android.graphics.drawable.Drawable bgDrawable = androidx.core.content.ContextCompat.getDrawable(context, current.background);
            if (bgDrawable != null) {
                bgDrawable.setBounds(0, 0, size, size);
                bgDrawable.draw(canvas);
            }

            android.graphics.drawable.Drawable fgDrawable = androidx.core.content.ContextCompat.getDrawable(context, current.foreground);
            if (fgDrawable != null) {
                fgDrawable.setBounds(0, 0, size, size);
                fgDrawable.draw(canvas);
            }

            cachedIconBitmap = bitmap;
            cachedForIcon = current;
            return bitmap;
        } catch (Throwable e) {
            org.telegram.messenger.FileLog.e(e);
            return null;
        }
    }

    public static void setIcon(LauncherIcon icon) {
        cachedSelectedIcon = icon;
        cachedIconBitmap = null;
        cachedForIcon = null;
        Context ctx = ApplicationLoader.applicationContext;
        PackageManager pm = ctx.getPackageManager();
        for (LauncherIcon i : LauncherIcon.values()) {
            pm.setComponentEnabledSetting(i.getComponentName(ctx), i == icon ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        org.telegram.messenger.PrismThemeController.onIconChanged(icon);
    }

    public enum LauncherIcon {
        DEFAULT("DefaultIcon", R.drawable.icon_background_sa, R.mipmap.icon_foreground_sa, R.string.AppIconDefault),
        VINTAGE("VintageIcon", R.drawable.icon_6_background_sa, R.mipmap.icon_6_foreground_sa, R.string.AppIconVintage),
        AQUA("AquaIcon", R.drawable.icon_4_background_sa, R.mipmap.icon_4_foreground_sa, R.string.AppIconAqua),
        PREMIUM("PremiumIcon", R.drawable.icon_3_background_sa, R.mipmap.icon_3_foreground_sa, R.string.AppIconPremium),
        TURBO("TurboIcon", R.drawable.icon_5_background_sa, R.mipmap.icon_5_foreground_sa, R.string.AppIconTurbo),
        NOX("NoxIcon", R.mipmap.icon_2_background_sa, R.mipmap.icon_2_foreground_sa, R.string.AppIconNox),
        COBALT("CobaltIcon", R.drawable.icon_7_background_sa, R.mipmap.icon_7_foreground_sa, R.string.AppIconCobalt),
        RUBY("RubyIcon", R.drawable.icon_8_background_sa, R.mipmap.icon_8_foreground_sa, R.string.AppIconRuby),
        AURORA("AuroraIcon", R.drawable.icon_9_background_sa, R.mipmap.icon_9_foreground_sa, R.string.AppIconPrismAurora),
        PURE("PureIcon", R.drawable.icon_10_background_sa, R.mipmap.icon_10_foreground_sa, R.string.AppIconPure),
        MATRIX("MatrixIcon", R.drawable.icon_11_background_sa, R.mipmap.icon_11_foreground_sa, R.string.AppIconPrismMatrix),
        SUNSET("SunsetIcon", R.drawable.icon_12_background_sa, R.mipmap.icon_12_foreground_sa, R.string.AppIconPrismSunset),
        COSMOS("CosmosIcon", R.drawable.icon_13_background_sa, R.mipmap.icon_13_foreground_sa, R.string.AppIconPrismCosmos),
        LAVA("LavaIcon", R.drawable.icon_14_background_sa, R.mipmap.icon_14_foreground_sa, R.string.AppIconPrismLava),
        CHROME("ChromeIcon", R.drawable.icon_15_background_sa, R.mipmap.icon_15_foreground_sa, R.string.AppIconPrismChrome),
        SAKURA("SakuraIcon", R.drawable.icon_16_background_sa, R.mipmap.icon_16_foreground_sa, R.string.AppIconPrismSakura),
        SINGULARITY("SingularityIcon", R.drawable.icon_17_background_sa, R.mipmap.icon_17_foreground_sa, R.string.AppIconPrismSingularity),
        PLASMA("PlasmaIcon", R.drawable.icon_18_background_sa, R.mipmap.icon_18_foreground_sa, R.string.AppIconPrismPlasma),
        AMETHYST("AmethystIcon", R.drawable.icon_19_background_sa, R.mipmap.icon_19_foreground_sa, R.string.AppIconPrismAmethyst),
        CYBER("CyberIcon", R.drawable.icon_20_background_sa, R.mipmap.icon_20_foreground_sa, R.string.AppIconPrismCyber),
        ABYSS("AbyssIcon", R.drawable.icon_21_background_sa, R.mipmap.icon_21_foreground_sa, R.string.AppIconPrismAbyss),
        BRONZE("BronzeIcon", R.drawable.icon_22_background_sa, R.mipmap.icon_22_foreground_sa, R.string.AppIconPrismBronze),
        MONOCHROME("MonochromeIcon", R.drawable.icon_23_background_sa, R.mipmap.icon_23_foreground_sa, R.string.AppIconPrismMonochrome),
        SPECTRUM("SpectrumIcon", R.drawable.icon_24_background_sa, R.mipmap.icon_24_foreground_sa, R.string.AppIconPrismSpectrum),
        GLITCH("GlitchIcon", R.drawable.icon_25_background_sa, R.mipmap.icon_25_foreground_sa, R.string.AppIconPrismGlitch);

        public final String key;
        public final int background;
        public final int foreground;
        public final int title;
        public final boolean premium;

        private ComponentName componentName;

        public ComponentName getComponentName(Context ctx) {
            if (componentName == null) {
                componentName = new ComponentName(ctx.getPackageName(), "org.telegram.messenger." + key);
            }
            return componentName;
        }

        LauncherIcon(String key, int background, int foreground, int title) {
            this(key, background, foreground, title, false);
        }

        LauncherIcon(String key, int background, int foreground, int title, boolean premium) {
            this.key = key;
            this.background = background;
            this.foreground = foreground;
            this.title = title;
            this.premium = premium;
        }
    }
}
