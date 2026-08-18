package org.telegram.messenger;

import android.graphics.Color;
import org.telegram.ui.LauncherIconController;

/**
 * PrismThemeController — Ilova ikonkasi (App Icon) tanlanganda butun ilova
 * dizayni (accent ranglar, shisha hoshiyalar, xabarlar akslanishi, menyu auralari)ni
 * avtomatik dinamik ravishda to'liq moslashtiruvchi boshqaruvchi klass.
 */
public class PrismThemeController {

    public static class IconPalette {
        public final int primary;
        public final int secondary;
        public final int accentGlow;
        public final String name;

        public IconPalette(int primary, int secondary, int accentGlow, String name) {
            this.primary = primary;
            this.secondary = secondary;
            this.accentGlow = accentGlow;
            this.name = name;
        }
    }

    public static IconPalette getPaletteForIcon(LauncherIconController.LauncherIcon icon) {
        if (icon == null) {
            return new IconPalette(0xFF00E5FF, 0xFF007AFF, 0xFF00F0FF, "Default");
        }
        String key = icon.key.toLowerCase();
        if (key.contains("matrix")) {
            return new IconPalette(0xFF00FF66, 0xFF00E5FF, 0xFF39FF14, "Matrix");
        } else if (key.contains("sunset")) {
            return new IconPalette(0xFFFF9100, 0xFFFF1744, 0xFFFFD700, "Sunset");
        } else if (key.contains("ruby")) {
            return new IconPalette(0xFFE11D48, 0xFFBE123C, 0xFFFB7185, "Ruby");
        } else if (key.contains("cosmos")) {
            return new IconPalette(0xFF8B5CF6, 0xFF00E5FF, 0xFFD946EF, "Cosmos");
        } else if (key.contains("lava")) {
            return new IconPalette(0xFFFF3D00, 0xFFFFD600, 0xFFFF0055, "Lava");
        } else if (key.contains("chrome")) {
            return new IconPalette(0xFF00F0FF, 0xFFE0E7FF, 0xFFFFFFFF, "Chrome");
        } else if (key.contains("sakura")) {
            return new IconPalette(0xFFF472B6, 0xFFC084FC, 0xFFFFD1DC, "Sakura");
        } else if (key.contains("singularity")) {
            return new IconPalette(0xFF7C4DFF, 0xFFFF007F, 0xFF00F5FF, "Singularity");
        } else if (key.contains("plasma")) {
            return new IconPalette(0xFFD946EF, 0xFF38BDF8, 0xFFF43F5E, "Plasma");
        } else if (key.contains("amethyst")) {
            return new IconPalette(0xFFA855F7, 0xFFEC4899, 0xFFE879F9, "Amethyst");
        } else if (key.contains("cyber")) {
            return new IconPalette(0xFF00E5FF, 0xFFFF007F, 0xFFFFEA00, "Cyber");
        } else if (key.contains("abyss")) {
            return new IconPalette(0xFF0052D4, 0xFF4364F7, 0xFF6FB1FC, "Abyss");
        } else if (key.contains("bronze")) {
            return new IconPalette(0xFFD97706, 0xFFB45309, 0xFFF59E0B, "Bronze");
        } else if (key.contains("monochrome")) {
            return new IconPalette(0xFFFFFFFF, 0xFF9CA3AF, 0xFFE5E7EB, "Monochrome");
        } else if (key.contains("spectrum")) {
            return new IconPalette(0xFFFF0055, 0xFF00F0FF, 0xFFFFD700, "Spectrum");
        } else if (key.contains("glitch")) {
            return new IconPalette(0xFF00FFCC, 0xFFFF0055, 0xFFFFFFFF, "Glitch");
        } else if (key.contains("aurora")) {
            return new IconPalette(0xFF00FFA3, 0xFF00E5FF, 0xFF7000FF, "Aurora");
        } else if (key.contains("pure")) {
            return new IconPalette(0xFF00E5FF, 0xFF7000FF, 0xFFFFFFFF, "Pure");
        } else if (key.contains("cobalt")) {
            return new IconPalette(0xFF2979FF, 0xFF00E5FF, 0xFF448AFF, "Cobalt");
        } else if (key.contains("aqua")) {
            return new IconPalette(0xFF00E5FF, 0xFF00B0FF, 0xFF80D8FF, "Aqua");
        } else if (key.contains("turbo")) {
            return new IconPalette(0xFFFF5252, 0xFFFF7A00, 0xFFFFAB40, "Turbo");
        } else if (key.contains("vintage")) {
            return new IconPalette(0xFFE0A96D, 0xFF7D5A50, 0xFFF7D1BA, "Vintage");
        } else if (key.contains("nox")) {
            return new IconPalette(0xFF6366F1, 0xFF8B5CF6, 0xFFA855F7, "Nox");
        } else {
            return new IconPalette(0xFF00E5FF, 0xFF007AFF, 0xFF00F0FF, "Default");
        }
    }

    public static IconPalette getCurrentPalette() {
        return getPaletteForIcon(LauncherIconController.getSelectedIcon());
    }

    public static void onIconChanged(LauncherIconController.LauncherIcon newIcon) {
        AndroidUtilities.runOnUIThread(() -> {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme);
        });
    }
}
