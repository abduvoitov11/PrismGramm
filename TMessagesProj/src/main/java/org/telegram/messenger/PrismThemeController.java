package org.telegram.messenger;

import android.graphics.Color;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LauncherIconController;

/**
 * PrismThemeController — Ilova ikonkasi (App Icon) tanlanganda butun ilova
 * dizayni (ActionBar, Fonlar, FAB, Unread Badge'lar, Switch'lar, Linklar, Xabarlar)ni
 * tanlangan ikonka uslubiga to'liq avtomatik tarzda moslashtiruvchi markaziy tema dvigateli.
 */
public class PrismThemeController {

    public static class IconPalette {
        public final String name;
        public final int primary;
        public final int secondary;
        public final int accentGlow;
        public final int actionBarBg;
        public final int windowBg;
        public final int windowBgGray;
        public final int fabBg;
        public final int fabIcon;
        public final int unreadBadgeBg;
        public final int unreadBadgeText;
        public final int outBubble;
        public final int inBubble;
        public final int messagePanelBg;
        public final int linkColor;
        public final int activeTabBg;

        public IconPalette(
                String name,
                int primary, int secondary, int accentGlow,
                int actionBarBg, int windowBg, int windowBgGray,
                int fabBg, int fabIcon,
                int unreadBadgeBg, int unreadBadgeText,
                int outBubble, int inBubble, int messagePanelBg,
                int linkColor, int activeTabBg
        ) {
            this.name = name;
            this.primary = primary;
            this.secondary = secondary;
            this.accentGlow = accentGlow;
            this.actionBarBg = actionBarBg;
            this.windowBg = windowBg;
            this.windowBgGray = windowBgGray;
            this.fabBg = fabBg;
            this.fabIcon = fabIcon;
            this.unreadBadgeBg = unreadBadgeBg;
            this.unreadBadgeText = unreadBadgeText;
            this.outBubble = outBubble;
            this.inBubble = inBubble;
            this.messagePanelBg = messagePanelBg;
            this.linkColor = linkColor;
            this.activeTabBg = activeTabBg;
        }
    }

    public static IconPalette getPaletteForIcon(LauncherIconController.LauncherIcon icon) {
        if (icon == null) {
            icon = LauncherIconController.LauncherIcon.DEFAULT;
        }
        String key = icon.key.toLowerCase();

        if (key.contains("matrix")) {
            return new IconPalette(
                    "Matrix",
                    0xFF00FF66, 0xFF00E5FF, 0xFF39FF14,
                    0xFF06140B, 0xFF030A05, 0xFF07180D,
                    0xFF00FF66, 0xFF030A05,
                    0xFF00FF66, 0xFF030A05,
                    0xFF0D301C, 0xFF081C10, 0xFF06140B,
                    0xFF00FF66, 0xFF00FF66
            );
        } else if (key.contains("sunset")) {
            return new IconPalette(
                    "Sunset",
                    0xFFFF9100, 0xFFFF1744, 0xFFFFD700,
                    0xFF1C0D13, 0xFF12070D, 0xFF220F17,
                    0xFFFF6D00, 0xFFFFFFFF,
                    0xFFFF6D00, 0xFFFFFFFF,
                    0xFF3D1621, 0xFF240D15, 0xFF1C0D13,
                    0xFFFF9100, 0xFFFF6D00
            );
        } else if (key.contains("ruby")) {
            return new IconPalette(
                    "Ruby",
                    0xFFE11D48, 0xFFBE123C, 0xFFFB7185,
                    0xFF1C0710, 0xFF100309, 0xFF240915,
                    0xFFE11D48, 0xFFFFFFFF,
                    0xFFE11D48, 0xFFFFFFFF,
                    0xFF420F20, 0xFF260812, 0xFF1C0710,
                    0xFFFB7185, 0xFFE11D48
            );
        } else if (key.contains("cosmos")) {
            return new IconPalette(
                    "Cosmos",
                    0xFF8B5CF6, 0xFF00E5FF, 0xFFD946EF,
                    0xFF100826, 0xFF070314, 0xFF170C36,
                    0xFF8B5CF6, 0xFFFFFFFF,
                    0xFF8B5CF6, 0xFFFFFFFF,
                    0xFF2D1457, 0xFF1A0B33, 0xFF100826,
                    0xFF00E5FF, 0xFF8B5CF6
            );
        } else if (key.contains("lava")) {
            return new IconPalette(
                    "Lava",
                    0xFFFF3D00, 0xFFFFD600, 0xFFFF0055,
                    0xFF1C0803, 0xFF100301, 0xFF240B04,
                    0xFFFF3D00, 0xFFFFFFFF,
                    0xFFFF3D00, 0xFFFFFFFF,
                    0xFF421406, 0xFF260B04, 0xFF1C0803,
                    0xFFFF9100, 0xFFFF3D00
            );
        } else if (key.contains("chrome")) {
            return new IconPalette(
                    "Chrome",
                    0xFF00F0FF, 0xFFE0E7FF, 0xFFFFFFFF,
                    0xFF0A121D, 0xFF04080F, 0xFF0F1B2B,
                    0xFF00F0FF, 0xFF04080F,
                    0xFF00F0FF, 0xFF04080F,
                    0xFF122C42, 0xFF0A1926, 0xFF0A121D,
                    0xFF00F0FF, 0xFF00F0FF
            );
        } else if (key.contains("sakura")) {
            return new IconPalette(
                    "Sakura",
                    0xFFF472B6, 0xFFC084FC, 0xFFFFD1DC,
                    0xFF1E0C18, 0xFF12050D, 0xFF250F1E,
                    0xFFF472B6, 0xFFFFFFFF,
                    0xFFF472B6, 0xFFFFFFFF,
                    0xFF3D162B, 0xFF260D1A, 0xFF1E0C18,
                    0xFFF472B6, 0xFFF472B6
            );
        } else if (key.contains("singularity")) {
            return new IconPalette(
                    "Singularity",
                    0xFF7C4DFF, 0xFFFF007F, 0xFF00F5FF,
                    0xFF0C061F, 0xFF05020F, 0xFF140B30,
                    0xFF7C4DFF, 0xFFFFFFFF,
                    0xFFFF007F, 0xFFFFFFFF,
                    0xFF260F4D, 0xFF16082E, 0xFF0C061F,
                    0xFF00F5FF, 0xFF7C4DFF
            );
        } else if (key.contains("plasma")) {
            return new IconPalette(
                    "Plasma",
                    0xFFD946EF, 0xFF38BDF8, 0xFFF43F5E,
                    0xFF180724, 0xFF0D0314, 0xFF220B33,
                    0xFFD946EF, 0xFFFFFFFF,
                    0xFF38BDF8, 0xFF0D0314,
                    0xFF3D144D, 0xFF240B2E, 0xFF180724,
                    0xFF38BDF8, 0xFFD946EF
            );
        } else if (key.contains("amethyst")) {
            return new IconPalette(
                    "Amethyst",
                    0xFFA855F7, 0xFFEC4899, 0xFFE879F9,
                    0xFF140822, 0xFF0A0314, 0xFF1D0D30,
                    0xFFA855F7, 0xFFFFFFFF,
                    0xFFEC4899, 0xFFFFFFFF,
                    0xFF34134D, 0xFF1E0A2E, 0xFF140822,
                    0xFFE879F9, 0xFFA855F7
            );
        } else if (key.contains("cyber")) {
            return new IconPalette(
                    "Cyber",
                    0xFF00E5FF, 0xFFFF007F, 0xFFFFEA00,
                    0xFF080C1F, 0xFF030512, 0xFF0E1430,
                    0xFF00E5FF, 0xFF030512,
                    0xFFFF007F, 0xFFFFFFFF,
                    0xFF12204D, 0xFF0A122E, 0xFF080C1F,
                    0xFF00E5FF, 0xFF00E5FF
            );
        } else if (key.contains("abyss")) {
            return new IconPalette(
                    "Abyss",
                    0xFF0052D4, 0xFF4364F7, 0xFF6FB1FC,
                    0xFF060E21, 0xFF020612, 0xFF0A1633,
                    0xFF4364F7, 0xFFFFFFFF,
                    0xFF4364F7, 0xFFFFFFFF,
                    0xFF0F2452, 0xFF091633, 0xFF060E21,
                    0xFF6FB1FC, 0xFF4364F7
            );
        } else if (key.contains("bronze")) {
            return new IconPalette(
                    "Bronze",
                    0xFFD97706, 0xFFB45309, 0xFFF59E0B,
                    0xFF1A1005, 0xFF0F0802, 0xFF241708,
                    0xFFD97706, 0xFFFFFFFF,
                    0xFFD97706, 0xFFFFFFFF,
                    0xFF3D250A, 0xFF241505, 0xFF1A1005,
                    0xFFF59E0B, 0xFFD97706
            );
        } else if (key.contains("monochrome")) {
            return new IconPalette(
                    "Monochrome",
                    0xFFFFFFFF, 0xFF9CA3AF, 0xFFE5E7EB,
                    0xFF141414, 0xFF0A0A0A, 0xFF1C1C1C,
                    0xFFFFFFFF, 0xFF0A0A0A,
                    0xFFFFFFFF, 0xFF0A0A0A,
                    0xFF303030, 0xFF1A1A1A, 0xFF141414,
                    0xFFFFFFFF, 0xFFFFFFFF
            );
        } else if (key.contains("spectrum")) {
            return new IconPalette(
                    "Spectrum",
                    0xFFFF0055, 0xFF00F0FF, 0xFFFFD700,
                    0xFF14081C, 0xFF0C0312, 0xFF1F0D2B,
                    0xFFFF0055, 0xFFFFFFFF,
                    0xFF00F0FF, 0xFF0C0312,
                    0xFF3A1245, 0xFF200926, 0xFF14081C,
                    0xFF00F0FF, 0xFFFF0055
            );
        } else if (key.contains("glitch")) {
            return new IconPalette(
                    "Glitch",
                    0xFF00FFCC, 0xFFFF0055, 0xFFFFFFFF,
                    0xFF061418, 0xFF02090C, 0xFF0A1E24,
                    0xFF00FFCC, 0xFF02090C,
                    0xFFFF0055, 0xFFFFFFFF,
                    0xFF0F363D, 0xFF081F24, 0xFF061418,
                    0xFF00FFCC, 0xFF00FFCC
            );
        } else if (key.contains("aurora")) {
            return new IconPalette(
                    "Aurora",
                    0xFF00FFA3, 0xFF00E5FF, 0xFF7000FF,
                    0xFF051714, 0xFF020B0A, 0xFF08211D,
                    0xFF00FFA3, 0xFF020B0A,
                    0xFF00FFA3, 0xFF020B0A,
                    0xFF0F3D35, 0xFF08241F, 0xFF051714,
                    0xFF00FFA3, 0xFF00FFA3
            );
        } else if (key.contains("pure")) {
            return new IconPalette(
                    "Pure",
                    0xFF00E5FF, 0xFF7000FF, 0xFFFFFFFF,
                    0xFF060E1F, 0xFF030712, 0xFF0A152E,
                    0xFF00E5FF, 0xFF030712,
                    0xFF00E5FF, 0xFF030712,
                    0xFF10284D, 0xFF09172E, 0xFF060E1F,
                    0xFF00E5FF, 0xFF00E5FF
            );
        } else if (key.contains("cobalt")) {
            return new IconPalette(
                    "Cobalt",
                    0xFF2979FF, 0xFF00E5FF, 0xFF448AFF,
                    0xFF061026, 0xFF030817, 0xFF0A1838,
                    0xFF2979FF, 0xFFFFFFFF,
                    0xFF2979FF, 0xFFFFFFFF,
                    0xFF122C5C, 0xFF0A1B38, 0xFF061026,
                    0xFF00E5FF, 0xFF2979FF
            );
        } else if (key.contains("aqua")) {
            return new IconPalette(
                    "Aqua",
                    0xFF00E5FF, 0xFF00B0FF, 0xFF80D8FF,
                    0xFF06141D, 0xFF03090F, 0xFF0A1E2B,
                    0xFF00E5FF, 0xFF03090F,
                    0xFF00E5FF, 0xFF03090F,
                    0xFF103347, 0xFF091E2B, 0xFF06141D,
                    0xFF00E5FF, 0xFF00E5FF
            );
        } else if (key.contains("turbo")) {
            return new IconPalette(
                    "Turbo",
                    0xFFFF5252, 0xFFFF7A00, 0xFFFFAB40,
                    0xFF1C0808, 0xFF100303, 0xFF240C0C,
                    0xFFFF5252, 0xFFFFFFFF,
                    0xFFFF5252, 0xFFFFFFFF,
                    0xFF421414, 0xFF260B0B, 0xFF1C0808,
                    0xFFFF7A00, 0xFFFF5252
            );
        } else if (key.contains("vintage")) {
            return new IconPalette(
                    "Vintage",
                    0xFFE0A96D, 0xFF7D5A50, 0xFFF7D1BA,
                    0xFF1A130F, 0xFF0F0B08, 0xFF241B15,
                    0xFFE0A96D, 0xFF0F0B08,
                    0xFFE0A96D, 0xFF0F0B08,
                    0xFF3D2E24, 0xFF241B15, 0xFF1A130F,
                    0xFFE0A96D, 0xFFE0A96D
            );
        } else if (key.contains("nox")) {
            return new IconPalette(
                    "Nox",
                    0xFF6366F1, 0xFF8B5CF6, 0xFFA855F7,
                    0xFF0B0B1E, 0xFF050512, 0xFF10102E,
                    0xFF6366F1, 0xFFFFFFFF,
                    0xFF6366F1, 0xFFFFFFFF,
                    0xFF20204D, 0xFF12122E, 0xFF0B0B1E,
                    0xFFA855F7, 0xFF6366F1
            );
        } else if (key.contains("premium")) {
            return new IconPalette(
                    "Premium",
                    0xFF9C27B0, 0xFFE040FB, 0xFFFFFFFF,
                    0xFF14081D, 0xFF0B0312, 0xFF1D0D2B,
                    0xFF9C27B0, 0xFFFFFFFF,
                    0xFFE040FB, 0xFFFFFFFF,
                    0xFF361247, 0xFF1F092B, 0xFF14081D,
                    0xFFE040FB, 0xFF9C27B0
            );
        } else {
            return new IconPalette(
                    "Default",
                    0xFF00E5FF, 0xFF007AFF, 0xFF00F0FF,
                    0xFF0B141E, 0xFF050B12, 0xFF0F1D2E,
                    0xFF007AFF, 0xFFFFFFFF,
                    0xFF007AFF, 0xFFFFFFFF,
                    0xFF14304D, 0xFF0B1B2E, 0xFF0B141E,
                    0xFF00E5FF, 0xFF007AFF
            );
        }
    }

    public static IconPalette getCurrentPalette() {
        return getPaletteForIcon(LauncherIconController.getSelectedIcon());
    }

    public static int getThemeColorOverride(int key) {
        IconPalette p = getCurrentPalette();
        if (p == null) return 0;

        // Action Bar (Top Navigation Bar)
        if (key == Theme.key_actionBarDefault || key == Theme.key_actionBarDefaultArchived
                || key == Theme.key_actionBarActionModeDefault || key == Theme.key_actionBarActionModeDefaultTop) {
            return p.actionBarBg;
        }
        if (key == Theme.key_actionBarDefaultTitle) {
            return 0xFFFFFFFF;
        }
        if (key == Theme.key_actionBarDefaultIcon || key == Theme.key_actionBarDefaultSelector
                || key == Theme.key_actionBarActionModeDefaultIcon) {
            return p.primary;
        }
        if (key == Theme.key_actionBarDefaultSubmenuBackground) {
            return p.actionBarBg;
        }
        if (key == Theme.key_actionBarDefaultSubmenuItemIcon) {
            return p.primary;
        }

        // Window & Screen Backgrounds
        if (key == Theme.key_windowBackgroundWhite || key == Theme.key_chat_messagePanelBackground) {
            return p.windowBg;
        }
        if (key == Theme.key_windowBackgroundGray || key == Theme.key_windowBackgroundGrayShadow) {
            return p.windowBgGray;
        }

        // Floating Action Button (FAB - Edit / New Chat button)
        if (key == Theme.key_chats_actionBackground || key == Theme.key_dialogFloatingButton || key == Theme.key_featuredStickers_addButton) {
            return p.fabBg;
        }
        if (key == Theme.key_chats_actionIcon || key == Theme.key_dialogFloatingIcon || key == Theme.key_featuredStickers_buttonText) {
            return p.fabIcon;
        }
        if (key == Theme.key_chats_actionPressedBackground || key == Theme.key_dialogFloatingButtonPressed || key == Theme.key_featuredStickers_addButtonPressed) {
            return Color.argb(0xCC, Color.red(p.fabBg), Color.green(p.fabBg), Color.blue(p.fabBg));
        }

        // Unread Badge (Chat list unread counters)
        if (key == Theme.key_chats_unreadCounter || key == Theme.key_chats_unreadCounterMuted || key == Theme.key_chats_tabUnreadActiveBackground) {
            return p.unreadBadgeBg;
        }
        if (key == Theme.key_chats_unreadCounterText) {
            return p.unreadBadgeText;
        }

        // Switches, Radios & Checkboxes
        if (key == Theme.key_switch2TrackChecked || key == Theme.key_switchTrackChecked
                || key == Theme.key_switchTrackBlueChecked || key == Theme.key_radioBackgroundChecked
                || key == Theme.key_checkboxCheck) {
            return p.primary;
        }

        // Links, Values, Titles & Cursor
        if (key == Theme.key_windowBackgroundWhiteBlueText || key == Theme.key_windowBackgroundWhiteBlueText2
                || key == Theme.key_windowBackgroundWhiteBlueText4 || key == Theme.key_windowBackgroundWhiteBlueHeader
                || key == Theme.key_windowBackgroundWhiteLinkText || key == Theme.key_windowBackgroundWhiteValueText
                || key == Theme.key_chat_messageLinkIn || key == Theme.key_chat_messageLinkOut) {
            return p.linkColor;
        }
        if (key == Theme.key_chat_messagePanelCursor || key == Theme.key_chat_messagePanelSend || key == Theme.key_chat_messagePanelIcons) {
            return p.primary;
        }

        // Chat Bubbles
        if (key == Theme.key_chat_outBubble || key == Theme.key_chat_outBubbleGradient1
                || key == Theme.key_chat_outBubbleGradient2 || key == Theme.key_chat_outBubbleGradient3) {
            return p.outBubble;
        }
        if (key == Theme.key_chat_inBubble) {
            return p.inBubble;
        }

        return 0;
    }

    public static void onIconChanged(LauncherIconController.LauncherIcon newIcon) {
        AndroidUtilities.runOnUIThread(() -> {
            Theme.reloadWallpaper(true);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme);
        });
    }
}
