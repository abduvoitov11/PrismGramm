package org.telegram.messenger;

import android.graphics.Color;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LauncherIconController;

/**
 * PrismThemeController — Ilova ikonkasi (App Icon) tanlanganda butun ilova
 * dizayni (Kunduzgi va Tungi mavzular, ActionBar, Fonlar, FAB, Unread Badge, Switchlar, Linklar)ni
 * tanlangan ikonka uslubiga to'liq avtomatik tarzda, yengil va tezkor moslashtiruvchi markaziy tema dvigateli.
 */
public class PrismThemeController {

    public static class IconPalette {
        public final String name;
        public final int primary;
        public final int secondary;
        public final int accentGlow;

        // Dark Mode
        public final int darkActionBarBg;
        public final int darkWindowBg;
        public final int darkWindowBgGray;
        public final int darkOutBubble;
        public final int darkInBubble;
        public final int darkMessagePanelBg;

        // Light (Day) Mode
        public final int lightActionBarBg;
        public final int lightWindowBg;
        public final int lightWindowBgGray;
        public final int lightOutBubble;
        public final int lightInBubble;
        public final int lightMessagePanelBg;

        public IconPalette(
                String name,
                int primary, int secondary, int accentGlow,
                int darkActionBarBg, int darkWindowBg, int darkWindowBgGray,
                int darkOutBubble, int darkInBubble, int darkMessagePanelBg,
                int lightActionBarBg, int lightWindowBg, int lightWindowBgGray,
                int lightOutBubble, int lightInBubble, int lightMessagePanelBg
        ) {
            this.name = name;
            this.primary = primary;
            this.secondary = secondary;
            this.accentGlow = accentGlow;

            this.darkActionBarBg = darkActionBarBg;
            this.darkWindowBg = darkWindowBg;
            this.darkWindowBgGray = darkWindowBgGray;
            this.darkOutBubble = darkOutBubble;
            this.darkInBubble = darkInBubble;
            this.darkMessagePanelBg = darkMessagePanelBg;

            this.lightActionBarBg = lightActionBarBg;
            this.lightWindowBg = lightWindowBg;
            this.lightWindowBgGray = lightWindowBgGray;
            this.lightOutBubble = lightOutBubble;
            this.lightInBubble = lightInBubble;
            this.lightMessagePanelBg = lightMessagePanelBg;
        }
    }

    private static IconPalette cachedPalette = null;
    private static LauncherIconController.LauncherIcon cachedPaletteIcon = null;

    public static IconPalette getPaletteForIcon(LauncherIconController.LauncherIcon icon) {
        if (icon == null) {
            icon = LauncherIconController.LauncherIcon.DEFAULT;
        }
        if (cachedPalette != null && cachedPaletteIcon == icon) {
            return cachedPalette;
        }

        String key = icon.key.toLowerCase();
        IconPalette palette;

        if (key.contains("matrix")) {
            palette = new IconPalette(
                    "Matrix",
                    0xFF00FF66, 0xFF00E5FF, 0xFF39FF14,
                    0xFF06140B, 0xFF030A05, 0xFF07180D, 0xFF0D301C, 0xFF081C10, 0xFF06140B,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F7F2, 0xFFE3F9E8, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("sunset")) {
            palette = new IconPalette(
                    "Sunset",
                    0xFFFF9100, 0xFFFF1744, 0xFFFFD700,
                    0xFF1C0D13, 0xFF12070D, 0xFF220F17, 0xFF3D1621, 0xFF240D15, 0xFF1C0D13,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF2F4, 0xFFFFEFE8, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("ruby")) {
            palette = new IconPalette(
                    "Ruby",
                    0xFFE11D48, 0xFFBE123C, 0xFFFB7185,
                    0xFF1C0710, 0xFF100309, 0xFF240915, 0xFF420F20, 0xFF260812, 0xFF1C0710,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF0F3, 0xFFFFEBF0, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("cosmos")) {
            palette = new IconPalette(
                    "Cosmos",
                    0xFF8B5CF6, 0xFF00E5FF, 0xFFD946EF,
                    0xFF100826, 0xFF070314, 0xFF170C36, 0xFF2D1457, 0xFF1A0B33, 0xFF100826,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF3F0FA, 0xFFF1EBFE, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("lava")) {
            palette = new IconPalette(
                    "Lava",
                    0xFFFF3D00, 0xFFFFD600, 0xFFFF0055,
                    0xFF1C0803, 0xFF100301, 0xFF240B04, 0xFF421406, 0xFF260B04, 0xFF1C0803,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF2F0, 0xFFFFECE6, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("chrome")) {
            palette = new IconPalette(
                    "Chrome",
                    0xFF00B0FF, 0xFF00F0FF, 0xFFFFFFFF,
                    0xFF0A121D, 0xFF04080F, 0xFF0F1B2B, 0xFF122C42, 0xFF0A1926, 0xFF0A121D,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F4F8, 0xFFE6F5FC, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("sakura")) {
            palette = new IconPalette(
                    "Sakura",
                    0xFFF472B6, 0xFFC084FC, 0xFFFFD1DC,
                    0xFF1E0C18, 0xFF12050D, 0xFF250F1E, 0xFF3D162B, 0xFF260D1A, 0xFF1E0C18,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF0F6, 0xFFFDEAF4, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("singularity")) {
            palette = new IconPalette(
                    "Singularity",
                    0xFF7C4DFF, 0xFFFF007F, 0xFF00F5FF,
                    0xFF0C061F, 0xFF05020F, 0xFF140B30, 0xFF260F4D, 0xFF16082E, 0xFF0C061F,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF2F0FA, 0xFFEFEBFE, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("plasma")) {
            palette = new IconPalette(
                    "Plasma",
                    0xFFD946EF, 0xFF38BDF8, 0xFFF43F5E,
                    0xFF180724, 0xFF0D0314, 0xFF220B33, 0xFF3D144D, 0xFF240B2E, 0xFF180724,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF7F0FA, 0xFFFBE8FE, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("amethyst")) {
            palette = new IconPalette(
                    "Amethyst",
                    0xFFA855F7, 0xFFEC4899, 0xFFE879F9,
                    0xFF140822, 0xFF0A0314, 0xFF1D0D30, 0xFF34134D, 0xFF1E0A2E, 0xFF140822,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF5F0FA, 0xFFF6EBFE, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("cyber")) {
            palette = new IconPalette(
                    "Cyber",
                    0xFF00E5FF, 0xFFFF007F, 0xFFFFEA00,
                    0xFF080C1F, 0xFF030512, 0xFF0E1430, 0xFF12204D, 0xFF0A122E, 0xFF080C1F,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F3FA, 0xFFE6FAFD, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("abyss")) {
            palette = new IconPalette(
                    "Abyss",
                    0xFF0052D4, 0xFF4364F7, 0xFF6FB1FC,
                    0xFF060E21, 0xFF020612, 0xFF0A1633, 0xFF0F2452, 0xFF091633, 0xFF060E21,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F3F9, 0xFFE6EEFA, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("bronze")) {
            palette = new IconPalette(
                    "Bronze",
                    0xFFD97706, 0xFFB45309, 0xFFF59E0B,
                    0xFF1A1005, 0xFF0F0802, 0xFF241708, 0xFF3D250A, 0xFF241505, 0xFF1A1005,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF4F0, 0xFFFDF3E6, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("monochrome")) {
            palette = new IconPalette(
                    "Monochrome",
                    0xFF52525B, 0xFF9CA3AF, 0xFFE5E7EB,
                    0xFF141414, 0xFF0A0A0A, 0xFF1C1C1C, 0xFF303030, 0xFF1A1A1A, 0xFF141414,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF4F4F5, 0xFFE4E4E7, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("spectrum")) {
            palette = new IconPalette(
                    "Spectrum",
                    0xFFFF0055, 0xFF00F0FF, 0xFFFFD700,
                    0xFF14081C, 0xFF0C0312, 0xFF1F0D2B, 0xFF3A1245, 0xFF200926, 0xFF14081C,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF0F8, 0xFFFFEBF3, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("glitch")) {
            palette = new IconPalette(
                    "Glitch",
                    0xFF00BFA5, 0xFFFF0055, 0xFFFFFFFF,
                    0xFF061418, 0xFF02090C, 0xFF0A1E24, 0xFF0F363D, 0xFF081F24, 0xFF061418,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F8F8, 0xFFE4F9F5, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("aurora")) {
            palette = new IconPalette(
                    "Aurora",
                    0xFF00C853, 0xFF00E5FF, 0xFF7000FF,
                    0xFF051714, 0xFF020B0A, 0xFF08211D, 0xFF0F3D35, 0xFF08241F, 0xFF051714,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F8F6, 0xFFE4F8EC, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("pure")) {
            palette = new IconPalette(
                    "Pure",
                    0xFF00B0FF, 0xFF7000FF, 0xFFFFFFFF,
                    0xFF060E1F, 0xFF030712, 0xFF0A152E, 0xFF10284D, 0xFF09172E, 0xFF060E1F,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F4FA, 0xFFE6F5FC, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("cobalt")) {
            palette = new IconPalette(
                    "Cobalt",
                    0xFF2979FF, 0xFF00E5FF, 0xFF448AFF,
                    0xFF061026, 0xFF030817, 0xFF0A1838, 0xFF122C5C, 0xFF0A1B38, 0xFF061026,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F4FA, 0xFFEAF1FE, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("aqua")) {
            palette = new IconPalette(
                    "Aqua",
                    0xFF00B0FF, 0xFF00E5FF, 0xFF80D8FF,
                    0xFF06141D, 0xFF03090F, 0xFF0A1E2B, 0xFF103347, 0xFF091E2B, 0xFF06141D,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF0F5FA, 0xFFE6F5FC, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("turbo")) {
            palette = new IconPalette(
                    "Turbo",
                    0xFFFF5252, 0xFFFF7A00, 0xFFFFAB40,
                    0xFF1C0808, 0xFF100303, 0xFF240C0C, 0xFF421414, 0xFF260B0B, 0xFF1C0808,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF0F0, 0xFFFFECEC, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("vintage")) {
            palette = new IconPalette(
                    "Vintage",
                    0xFFB47A46, 0xFF7D5A50, 0xFFF7D1BA,
                    0xFF1A130F, 0xFF0F0B08, 0xFF241B15, 0xFF3D2E24, 0xFF241B15, 0xFF1A130F,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFFAF5F0, 0xFFF7EFE6, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("nox")) {
            palette = new IconPalette(
                    "Nox",
                    0xFF6366F1, 0xFF8B5CF6, 0xFFA855F7,
                    0xFF0B0B1E, 0xFF050512, 0xFF10102E, 0xFF20204D, 0xFF12122E, 0xFF0B0B1E,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF2F2FA, 0xFFECECFE, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("premium")) {
            palette = new IconPalette(
                    "Premium",
                    0xFF9C27B0, 0xFFE040FB, 0xFFFFFFFF,
                    0xFF14081D, 0xFF0B0312, 0xFF1D0D2B, 0xFF361247, 0xFF1F092B, 0xFF14081D,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF7F0FA, 0xFFF6E8FA, 0xFFFFFFFF, 0xFFFFFFFF
            );
        } else {
            palette = new IconPalette(
                    "Default",
                    0xFF007AFF, 0xFF00E5FF, 0xFF00F0FF,
                    0xFF0B141E, 0xFF050B12, 0xFF0F1D2E, 0xFF14304D, 0xFF0B1B2E, 0xFF0B141E,
                    0xFFFFFFFF, 0xFFFFFFFF, 0xFFF1F5F9, 0xFFE6F2FE, 0xFFFFFFFF, 0xFFFFFFFF
            );
        }

        cachedPalette = palette;
        cachedPaletteIcon = icon;
        return palette;
    }

    public static IconPalette getCurrentPalette() {
        return getPaletteForIcon(LauncherIconController.getSelectedIcon());
    }

    public static int getThemeColorOverride(int key) {
        // Return 0 so Telegram's native themes (Day, Night, Tinted, AMOLED) render flawlessly
        // without turning the Chats list or screen backgrounds into black voids.
        return 0;
    }

    public static void onIconChanged(LauncherIconController.LauncherIcon newIcon) {
        cachedPalette = null;
        cachedPaletteIcon = null;
        AndroidUtilities.runOnUIThread(() -> {
            Theme.reloadWallpaper(true);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme);
        });
    }
}
