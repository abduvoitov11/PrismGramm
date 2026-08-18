package org.telegram.messenger;

import android.graphics.Color;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LauncherIconController;

/**
 * PrismThemeController — Ilova ikonkasi (App Icon) tanlanganda butun ilova
 * dizayni (ActionBar, Chatlar ro'yxati, Fonlar, Xabarlar, Tugmalar, Unread Badjlar, Switchlar, Linklar)ni
 * tanlangan ikonkaning shaxsiy rang va uslub palitrasiga to'liq avtomatik tarzda moslashtiruvchi markaziy tema dvigateli.
 *
 * Kunduzgi (Light) va Tungi (Dark) rejimlarda to'liq 100% matn kontrasti va rang uyg'unligi kafolatlangan.
 */
public class PrismThemeController {

    public static class IconPalette {
        public final String name;
        public final int primary;
        public final int secondary;
        public final int accentGlow;

        // Dark Theme Colors
        public final int darkActionBarBg;
        public final int darkActionBarTitle;
        public final int darkWindowBg;
        public final int darkWindowBgGray;
        public final int darkTextPrimary;
        public final int darkTextSecondary;
        public final int darkDivider;
        public final int darkOutBubble;
        public final int darkInBubble;
        public final int darkMessagePanelBg;

        // Light (Day) Theme Colors
        public final int lightActionBarBg;
        public final int lightActionBarTitle;
        public final int lightWindowBg;
        public final int lightWindowBgGray;
        public final int lightTextPrimary;
        public final int lightTextSecondary;
        public final int lightDivider;
        public final int lightOutBubble;
        public final int lightInBubble;
        public final int lightMessagePanelBg;

        public IconPalette(
                String name, int primary, int secondary, int accentGlow,
                int darkActionBarBg, int darkActionBarTitle, int darkWindowBg, int darkWindowBgGray,
                int darkTextPrimary, int darkTextSecondary, int darkDivider,
                int darkOutBubble, int darkInBubble, int darkMessagePanelBg,
                int lightActionBarBg, int lightActionBarTitle, int lightWindowBg, int lightWindowBgGray,
                int lightTextPrimary, int lightTextSecondary, int lightDivider,
                int lightOutBubble, int lightInBubble, int lightMessagePanelBg
        ) {
            this.name = name;
            this.primary = primary;
            this.secondary = secondary;
            this.accentGlow = accentGlow;

            this.darkActionBarBg = darkActionBarBg;
            this.darkActionBarTitle = darkActionBarTitle;
            this.darkWindowBg = darkWindowBg;
            this.darkWindowBgGray = darkWindowBgGray;
            this.darkTextPrimary = darkTextPrimary;
            this.darkTextSecondary = darkTextSecondary;
            this.darkDivider = darkDivider;
            this.darkOutBubble = darkOutBubble;
            this.darkInBubble = darkInBubble;
            this.darkMessagePanelBg = darkMessagePanelBg;

            this.lightActionBarBg = lightActionBarBg;
            this.lightActionBarTitle = lightActionBarTitle;
            this.lightWindowBg = lightWindowBg;
            this.lightWindowBgGray = lightWindowBgGray;
            this.lightTextPrimary = lightTextPrimary;
            this.lightTextSecondary = lightTextSecondary;
            this.lightDivider = lightDivider;
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
                    "Matrix", 0xFF00FF88, 0xFF00E5FF, 0xFF39FF14,
                    0xFF0A1810, 0xFFE2FBEA, 0xFF06140D, 0xFF081C12, 0xFFE2FBEA, 0xFF7EA88E, 0xFF122C1D, 0xFF0F3D24, 0xFF0B2416, 0xFF0A1810,
                    0xFFF2FAF5, 0xFF0A2616, 0xFFFFFFFF, 0xFFEDF7F1, 0xFF0C2417, 0xFF4E735F, 0xFFD8EFE2, 0xFFD4F8E0, 0xFFF0F7F2, 0xFFFFFFFF
            );
        } else if (key.contains("sakura")) {
            palette = new IconPalette(
                    "Sakura", 0xFFF472B6, 0xFFC084FC, 0xFFFFD1DC,
                    0xFF1C0D1A, 0xFFFDE8F3, 0xFF140813, 0xFF1A0A19, 0xFFFDE8F3, 0xFFA67B98, 0xFF2F142A, 0xFF4A183C, 0xFF2A0F22, 0xFF1C0D1A,
                    0xFFFAF2F6, 0xFF2B0E24, 0xFFFFFFFF, 0xFFF8EDF3, 0xFF2B0E24, 0xFF7D4E70, 0xFFF2D6E7, 0xFFFCE1F0, 0xFFF8F0F5, 0xFFFFFFFF
            );
        } else if (key.contains("cosmos") || key.contains("singularity")) {
            palette = new IconPalette(
                    "Cosmos", 0xFF8B5CF6, 0xFF00E5FF, 0xFFD946EF,
                    0xFF120C28, 0xFFEFE8FD, 0xFF0B061A, 0xFF100A24, 0xFFEFE8FD, 0xFF9080B8, 0xFF241648, 0xFF3B1E78, 0xFF1E1140, 0xFF120C28,
                    0xFFF4F0FA, 0xFF1C0D42, 0xFFFFFFFF, 0xFFEFEAF8, 0xFF1C0D42, 0xFF63508C, 0xFFDFD4F4, 0xFFECE3FC, 0xFFF3EFF9, 0xFFFFFFFF
            );
        } else if (key.contains("ruby")) {
            palette = new IconPalette(
                    "Ruby", 0xFFE11D48, 0xFFBE123C, 0xFFFB7185,
                    0xFF1E0712, 0xFFFDE8EE, 0xFF14030B, 0xFF1C0510, 0xFFFDE8EE, 0xFFA87586, 0xFF350A1E, 0xFF540E2A, 0xFF2B0715, 0xFF1E0712,
                    0xFFFAF2F4, 0xFF320819, 0xFFFFFFFF, 0xFFF7ECF0, 0xFF320819, 0xFF874B60, 0xFFF2D1DC, 0xFFFCDCE5, 0xFFF7F0F3, 0xFFFFFFFF
            );
        } else if (key.contains("amethyst")) {
            palette = new IconPalette(
                    "Amethyst", 0xFFA855F7, 0xFFEC4899, 0xFFE879F9,
                    0xFF170924, 0xFFF6E8FE, 0xFF0E0417, 0xFF150722, 0xFFF6E8FE, 0xFF9F7EBA, 0xFF2C1042, 0xFF47176E, 0xFF240C38, 0xFF170924,
                    0xFFF6F2FA, 0xFF260940, 0xFFFFFFFF, 0xFFF2EAF8, 0xFF260940, 0xFF724B91, 0xFFE6D6F4, 0xFFF1E4FC, 0xFFF4EEF8, 0xFFFFFFFF
            );
        } else if (key.contains("plasma") || key.contains("cyber") || key.contains("glitch")) {
            palette = new IconPalette(
                    "Plasma", 0xFF00E5FF, 0xFFFF007F, 0xFFD946EF,
                    0xFF0E122A, 0xFFE2F8FC, 0xFF080B1C, 0xFF0C1026, 0xFFE2F8FC, 0xFF7C8BAE, 0xFF1A224D, 0xFF18386E, 0xFF0F2042, 0xFF0E122A,
                    0xFFF0F8FA, 0xFF0A2234, 0xFFFFFFFF, 0xFFE8F4F7, 0xFF0A2234, 0xFF4A6B80, 0xFFCEE8F0, 0xFFDDF5FB, 0xFFEFF6F8, 0xFFFFFFFF
            );
        } else if (key.contains("sunset") || key.contains("lava")) {
            palette = new IconPalette(
                    "Sunset", 0xFFFF9100, 0xFFFF1744, 0xFFFFD700,
                    0xFF200B06, 0xFFFEECE6, 0xFF150603, 0xFF1E0904, 0xFFFEECE6, 0xFFA87F74, 0xFF3D1509, 0xFF571D0B, 0xFF2C0E05, 0xFF200B06,
                    0xFFFAF3F0, 0xFF3B1206, 0xFFFFFFFF, 0xFFF7ECE6, 0xFF3B1206, 0xFF8A5342, 0xFFF4D4C8, 0xFFFDE6DC, 0xFFF9F1ED, 0xFFFFFFFF
            );
        } else if (key.contains("pure") || key.contains("aqua") || key.contains("abyss") || key.contains("aurora")) {
            palette = new IconPalette(
                    "Pure", 0xFF00B0FF, 0xFF00E5FF, 0xFF80D8FF,
                    0xFF0A1826, 0xFFE5F5FD, 0xFF050F1A, 0xFF091624, 0xFFE5F5FD, 0xFF7A97AD, 0xFF132F47, 0xFF104469, 0xFF0A253B, 0xFF0A1826,
                    0xFFF0F7FA, 0xFF082236, 0xFFFFFFFF, 0xFFE8F2F7, 0xFF082236, 0xFF486E87, 0xFFCBE3F0, 0xFFDCF0FB, 0xFFEEF5F8, 0xFFFFFFFF
            );
        } else if (key.contains("bronze") || key.contains("vintage")) {
            palette = new IconPalette(
                    "Bronze", 0xFFD97706, 0xFFB45309, 0xFFF59E0B,
                    0xFF1C1106, 0xFFFDF4E7, 0xFF120A03, 0xFF1A0F05, 0xFFFDF4E7, 0xFFA88D73, 0xFF36200A, 0xFF4D300F, 0xFF281807, 0xFF1C1106,
                    0xFFFAF5EF, 0xFF361F07, 0xFFFFFFFF, 0xFFF6EFE5, 0xFF361F07, 0xFF826343, 0xFFEEDCC7, 0xFFFCECD9, 0xFFF8F2EA, 0xFFFFFFFF
            );
        } else if (key.contains("turbo")) {
            palette = new IconPalette(
                    "Turbo", 0xFFFF5252, 0xFFFF7A00, 0xFFFFAB40,
                    0xFF220707, 0xFFFEE8E8, 0xFF160303, 0xFF1F0606, 0xFFFEE8E8, 0xFFA87777, 0xFF3B0E0E, 0xFF571414, 0xFF2B0A0A, 0xFF220707,
                    0xFFFAF1F1, 0xFF380909, 0xFFFFFFFF, 0xFFF6E8E8, 0xFF380909, 0xFF854747, 0xFFF2D1D1, 0xFFFCDCDC, 0xFFF8F0F0, 0xFFFFFFFF
            );
        } else {
            palette = new IconPalette(
                    "Default", 0xFF007AFF, 0xFF00E5FF, 0xFF00F0FF,
                    0xFF0E1A29, 0xFFE8F3FD, 0xFF08111D, 0xFF0D1726, 0xFFE8F3FD, 0xFF7E97B0, 0xFF162D47, 0xFF1A4573, 0xFF0F2642, 0xFF0E1A29,
                    0xFFF1F6FA, 0xFF0A223B, 0xFFFFFFFF, 0xFFE8F0F7, 0xFF0A223B, 0xFF4E6B87, 0xFFCCE0F0, 0xFFDEF0FD, 0xFFEFF5F9, 0xFFFFFFFF
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
        IconPalette p = getCurrentPalette();
        if (p == null) return 0;

        boolean isDark = Theme.isCurrentThemeDark();

        // ─────────────────────────────────────────────
        // 1. NIGHT (DARK) THEME ADAPTATION
        // ─────────────────────────────────────────────
        if (isDark) {
            // Action Bar
            if (key == Theme.key_actionBarDefault || key == Theme.key_actionBarDefaultArchived
                    || key == Theme.key_actionBarActionModeDefault || key == Theme.key_actionBarActionModeDefaultTop) {
                return p.darkActionBarBg;
            }
            if (key == Theme.key_actionBarDefaultTitle) {
                return p.darkActionBarTitle;
            }
            if (key == Theme.key_actionBarDefaultIcon || key == Theme.key_actionBarDefaultSelector
                    || key == Theme.key_actionBarActionModeDefaultIcon) {
                return p.primary;
            }
            if (key == Theme.key_actionBarDefaultSubmenuBackground) {
                return p.darkActionBarBg;
            }
            if (key == Theme.key_actionBarDefaultSubmenuItemIcon) {
                return p.primary;
            }

            // Window & List Backgrounds
            if (key == Theme.key_windowBackgroundWhite || key == Theme.key_chat_messagePanelBackground) {
                return p.darkWindowBg;
            }
            if (key == Theme.key_windowBackgroundGray || key == Theme.key_windowBackgroundGrayShadow) {
                return p.darkWindowBgGray;
            }

            // Primary & Secondary Text Colors
            if (key == Theme.key_windowBackgroundWhiteBlackText || key == Theme.key_chats_name
                    || key == Theme.key_chats_nameMessage || key == Theme.key_chats_nameMessage_threeLines) {
                return p.darkTextPrimary;
            }
            if (key == Theme.key_windowBackgroundWhiteGrayText || key == Theme.key_windowBackgroundWhiteGrayText2
                    || key == Theme.key_chats_message || key == Theme.key_chats_message_threeLines) {
                return p.darkTextSecondary;
            }
            if (key == Theme.key_chats_date || key == Theme.key_chats_date_bold) {
                return p.darkTextSecondary;
            }

            // Dividers & Highlights
            if (key == Theme.key_divider || key == Theme.key_chats_menuTopShadow) {
                return p.darkDivider;
            }
            if (key == Theme.key_chats_pinnedOverlay) {
                return Color.argb(0x22, Color.red(p.primary), Color.green(p.primary), Color.blue(p.primary));
            }

            // Floating Action Button (FAB)
            if (key == Theme.key_chats_actionBackground || key == Theme.key_dialogFloatingButton || key == Theme.key_featuredStickers_addButton) {
                return p.primary;
            }
            if (key == Theme.key_chats_actionIcon || key == Theme.key_dialogFloatingIcon || key == Theme.key_featuredStickers_buttonText) {
                return 0xFF050505;
            }
            if (key == Theme.key_chats_actionPressedBackground || key == Theme.key_dialogFloatingButtonPressed || key == Theme.key_featuredStickers_addButtonPressed) {
                return Color.argb(0xDD, Color.red(p.primary), Color.green(p.primary), Color.blue(p.primary));
            }

            // Unread Badges
            if (key == Theme.key_chats_unreadCounter || key == Theme.key_chats_unreadCounterMuted || key == Theme.key_chats_tabUnreadActiveBackground) {
                return p.primary;
            }
            if (key == Theme.key_chats_unreadCounterText) {
                return 0xFF050505;
            }

            // Switches, Radio, Checkboxes & Links
            if (key == Theme.key_switch2TrackChecked || key == Theme.key_switchTrackChecked
                    || key == Theme.key_switchTrackBlueChecked || key == Theme.key_radioBackgroundChecked
                    || key == Theme.key_checkboxCheck) {
                return p.primary;
            }
            if (key == Theme.key_windowBackgroundWhiteBlueText || key == Theme.key_windowBackgroundWhiteBlueText2
                    || key == Theme.key_windowBackgroundWhiteBlueText4 || key == Theme.key_windowBackgroundWhiteBlueHeader
                    || key == Theme.key_windowBackgroundWhiteLinkText || key == Theme.key_windowBackgroundWhiteValueText
                    || key == Theme.key_chat_messageLinkIn || key == Theme.key_chat_messageLinkOut) {
                return p.primary;
            }
            if (key == Theme.key_chat_messagePanelCursor || key == Theme.key_chat_messagePanelSend || key == Theme.key_chat_messagePanelIcons) {
                return p.primary;
            }

            // Chat Bubbles
            if (key == Theme.key_chat_outBubble || key == Theme.key_chat_outBubbleGradient1
                    || key == Theme.key_chat_outBubbleGradient2 || key == Theme.key_chat_outBubbleGradient3) {
                return p.darkOutBubble;
            }
            if (key == Theme.key_chat_inBubble) {
                return p.darkInBubble;
            }

            return 0;
        }

        // ─────────────────────────────────────────────
        // 2. DAY (LIGHT) THEME ADAPTATION
        // ─────────────────────────────────────────────
        else {
            // Action Bar
            if (key == Theme.key_actionBarDefault || key == Theme.key_actionBarDefaultArchived
                    || key == Theme.key_actionBarActionModeDefault || key == Theme.key_actionBarActionModeDefaultTop) {
                return p.lightActionBarBg;
            }
            if (key == Theme.key_actionBarDefaultTitle) {
                return p.lightActionBarTitle;
            }
            if (key == Theme.key_actionBarDefaultIcon || key == Theme.key_actionBarDefaultSelector
                    || key == Theme.key_actionBarActionModeDefaultIcon) {
                return p.primary;
            }
            if (key == Theme.key_actionBarDefaultSubmenuBackground) {
                return p.lightActionBarBg;
            }
            if (key == Theme.key_actionBarDefaultSubmenuItemIcon) {
                return p.primary;
            }

            // Window & List Backgrounds
            if (key == Theme.key_windowBackgroundWhite || key == Theme.key_chat_messagePanelBackground) {
                return p.lightWindowBg;
            }
            if (key == Theme.key_windowBackgroundGray || key == Theme.key_windowBackgroundGrayShadow) {
                return p.lightWindowBgGray;
            }

            // Primary & Secondary Text Colors
            if (key == Theme.key_windowBackgroundWhiteBlackText || key == Theme.key_chats_name
                    || key == Theme.key_chats_nameMessage || key == Theme.key_chats_nameMessage_threeLines) {
                return p.lightTextPrimary;
            }
            if (key == Theme.key_windowBackgroundWhiteGrayText || key == Theme.key_windowBackgroundWhiteGrayText2
                    || key == Theme.key_chats_message || key == Theme.key_chats_message_threeLines) {
                return p.lightTextSecondary;
            }
            if (key == Theme.key_chats_date || key == Theme.key_chats_date_bold) {
                return p.lightTextSecondary;
            }

            // Dividers & Highlights
            if (key == Theme.key_divider || key == Theme.key_chats_menuTopShadow) {
                return p.lightDivider;
            }
            if (key == Theme.key_chats_pinnedOverlay) {
                return Color.argb(0x18, Color.red(p.primary), Color.green(p.primary), Color.blue(p.primary));
            }

            // Floating Action Button (FAB)
            if (key == Theme.key_chats_actionBackground || key == Theme.key_dialogFloatingButton || key == Theme.key_featuredStickers_addButton) {
                return p.primary;
            }
            if (key == Theme.key_chats_actionIcon || key == Theme.key_dialogFloatingIcon || key == Theme.key_featuredStickers_buttonText) {
                return 0xFFFFFFFF;
            }
            if (key == Theme.key_chats_actionPressedBackground || key == Theme.key_dialogFloatingButtonPressed || key == Theme.key_featuredStickers_addButtonPressed) {
                return Color.argb(0xDD, Color.red(p.primary), Color.green(p.primary), Color.blue(p.primary));
            }

            // Unread Badges
            if (key == Theme.key_chats_unreadCounter || key == Theme.key_chats_unreadCounterMuted || key == Theme.key_chats_tabUnreadActiveBackground) {
                return p.primary;
            }
            if (key == Theme.key_chats_unreadCounterText) {
                return 0xFFFFFFFF;
            }

            // Switches, Radio, Checkboxes & Links
            if (key == Theme.key_switch2TrackChecked || key == Theme.key_switchTrackChecked
                    || key == Theme.key_switchTrackBlueChecked || key == Theme.key_radioBackgroundChecked
                    || key == Theme.key_checkboxCheck) {
                return p.primary;
            }
            if (key == Theme.key_windowBackgroundWhiteBlueText || key == Theme.key_windowBackgroundWhiteBlueText2
                    || key == Theme.key_windowBackgroundWhiteBlueText4 || key == Theme.key_windowBackgroundWhiteBlueHeader
                    || key == Theme.key_windowBackgroundWhiteLinkText || key == Theme.key_windowBackgroundWhiteValueText
                    || key == Theme.key_chat_messageLinkIn || key == Theme.key_chat_messageLinkOut) {
                return p.primary;
            }
            if (key == Theme.key_chat_messagePanelCursor || key == Theme.key_chat_messagePanelSend || key == Theme.key_chat_messagePanelIcons) {
                return p.primary;
            }

            // Chat Bubbles
            if (key == Theme.key_chat_outBubble || key == Theme.key_chat_outBubbleGradient1
                    || key == Theme.key_chat_outBubbleGradient2 || key == Theme.key_chat_outBubbleGradient3) {
                return p.lightOutBubble;
            }
            if (key == Theme.key_chat_inBubble) {
                return p.lightInBubble;
            }

            return 0;
        }
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
