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

    private static android.graphics.Bitmap cachedIconBitmap;
    private static LauncherIcon cachedForIcon;

    public static LauncherIcon getSelectedIcon() {
        for (LauncherIcon icon : LauncherIcon.values()) {
            if (isEnabled(icon)) {
                return icon;
            }
        }
        return LauncherIcon.DEFAULT;
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
        cachedIconBitmap = null;
        cachedForIcon = null;
        Context ctx = ApplicationLoader.applicationContext;
        PackageManager pm = ctx.getPackageManager();
        for (LauncherIcon i : LauncherIcon.values()) {
            pm.setComponentEnabledSetting(i.getComponentName(ctx), i == icon ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
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
        PURE("PureIcon", R.drawable.icon_10_background_sa, R.mipmap.icon_10_foreground_sa, R.string.AppIconPure);

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
