package org.telegram.ui.ActionBar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.tlutils.TlUtils;
import org.telegram.messenger.wallpaper.WallpaperBitmapHolder;
import org.telegram.messenger.wallpaper.WallpaperGiftBitmapDrawable;
import org.telegram.messenger.wallpaper.WallpaperGiftPatternPosition;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.theme.ITheme;
import org.telegram.ui.ActionBar.theme.ThemeKey;
import org.telegram.ui.Components.RLottieDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EmojiThemes {

    public static final String REMOVED_EMOJI = "❌";

    public boolean showAsDefaultStub;
    public boolean showAsRemovedStub;
    public ThemeKey key;
    public TLRPC.ChatTheme chatTheme;
    public String emoji;
    public TLRPC.WallPaper wallpaper;
    int currentIndex = 0;
    public ArrayList<ThemeItem> items = new ArrayList<>();
    private final int currentAccount;

    private static final int[] previewColorKeys = new int[]{
            Theme.key_chat_inBubble,
            Theme.key_chat_outBubble,
            Theme.key_featuredStickers_addButton,
            Theme.key_chat_wallpaper,
            Theme.key_chat_wallpaper_gradient_to1,
            Theme.key_chat_wallpaper_gradient_to2,
            Theme.key_chat_wallpaper_gradient_to3,
            Theme.key_chat_wallpaper_gradient_rotation
    };

    public EmojiThemes(int currentAccount) {
        this.currentAccount = currentAccount;
    }

    public EmojiThemes(int currentAccount, TLRPC.TL_theme chatThemeObject, boolean isDefault) {
        this.currentAccount = currentAccount;
        this.showAsDefaultStub = isDefault;
        this.emoji = chatThemeObject.emoticon;
        this.key = ThemeKey.of(chatThemeObject);
        this.chatTheme = TLRPC.ChatTheme.ofEmoticon(chatThemeObject.emoticon);
        if (!isDefault) {
            ThemeItem lightTheme = new ThemeItem();
            lightTheme.tlTheme = chatThemeObject;
            lightTheme.settingsIndex = 0;
            items.add(lightTheme);

            ThemeItem darkTheme = new ThemeItem();
            darkTheme.tlTheme = chatThemeObject;
            darkTheme.settingsIndex = 1;
            items.add(darkTheme);
        }
    }

    public EmojiThemes(int currentAccount, TLRPC.TL_chatThemeUniqueGift chatThemeObject) {
        this.currentAccount = currentAccount;
        this.showAsDefaultStub = false;
        this.emoji = chatThemeObject.gift.slug;
        this.key = ThemeKey.of(chatThemeObject);
        this.chatTheme = chatThemeObject;


        ThemeItem lightTheme = new ThemeItem();
        lightTheme.tlChatThemeGift = chatThemeObject;
        lightTheme.settingsIndex = 0;
        items.add(lightTheme);

        ThemeItem darkTheme = new ThemeItem();
        darkTheme.tlChatThemeGift = chatThemeObject;
        darkTheme.settingsIndex = 1;
        items.add(darkTheme);
    }

    public boolean isAnyStub() {
        return showAsDefaultStub || showAsRemovedStub;
    }

    public boolean isGiftTheme() {
        return key != null && !TextUtils.isEmpty(key.giftSlug);
    }

    public static EmojiThemes createPreviewFullTheme(int currentAccount, TLRPC.TL_theme tl_theme) {
        EmojiThemes chatTheme = new EmojiThemes(currentAccount);
        chatTheme.emoji = tl_theme.emoticon;
        chatTheme.key = ThemeKey.of(tl_theme);
        chatTheme.chatTheme = TLRPC.ChatTheme.ofEmoticon(tl_theme.emoticon);

        for (int i = 0; i < tl_theme.settings.size(); i++) {
            ThemeItem theme = new ThemeItem();
            theme.tlTheme = tl_theme;
            theme.settingsIndex = i;
            chatTheme.items.add(theme);
        }
        return chatTheme;
    }


    public static EmojiThemes createChatThemesDefault(int currentAccount) {

        EmojiThemes themeItem = new EmojiThemes(currentAccount);
        themeItem.emoji = REMOVED_EMOJI;
        themeItem.key = ThemeKey.ofEmoticon(REMOVED_EMOJI);
        themeItem.chatTheme = TLRPC.ChatTheme.ofEmoticon(REMOVED_EMOJI);
        themeItem.showAsDefaultStub = true;

        ThemeItem lightTheme = new ThemeItem();
        lightTheme.themeInfo = getDefaultThemeInfo(true);
        themeItem.items.add(lightTheme);

        ThemeItem darkTheme = new ThemeItem();
        darkTheme.themeInfo = getDefaultThemeInfo(false);
        themeItem.items.add(darkTheme);

        return themeItem;
    }

    public static EmojiThemes createChatThemesRemoved(int currentAccount) {

        EmojiThemes themeItem = new EmojiThemes(currentAccount);
        themeItem.emoji = REMOVED_EMOJI;
        themeItem.key = ThemeKey.ofEmoticon(REMOVED_EMOJI);
        themeItem.chatTheme = TLRPC.ChatTheme.ofEmoticon(REMOVED_EMOJI);
        themeItem.showAsRemovedStub = true;

        ThemeItem lightTheme = new ThemeItem();
        lightTheme.themeInfo = getDefaultThemeInfo(true);
        themeItem.items.add(lightTheme);

        ThemeItem darkTheme = new ThemeItem();
        darkTheme.themeInfo = getDefaultThemeInfo(false);
        themeItem.items.add(darkTheme);

        return themeItem;
    }

    public static EmojiThemes createPreviewCustom(int currentAccount) {
        EmojiThemes themeItem = new EmojiThemes(currentAccount);
        themeItem.emoji = "\uD83C\uDFA8";
        themeItem.key = ThemeKey.ofEmoticon(themeItem.emoji);
        themeItem.chatTheme = TLRPC.ChatTheme.ofEmoticon(themeItem.emoji);

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
        String lastDayCustomTheme = preferences.getString("lastDayCustomTheme", null);
        int dayAccentId = preferences.getInt("lastDayCustomThemeAccentId", -1);
        if (lastDayCustomTheme == null || Theme.getTheme(lastDayCustomTheme) == null) {
            lastDayCustomTheme = preferences.getString("lastDayTheme", "Blue");
            Theme.ThemeInfo themeInfo = Theme.getTheme(lastDayCustomTheme);
            if (themeInfo == null) {
                lastDayCustomTheme = "Blue";
                dayAccentId = 99;
            } else {
                dayAccentId = themeInfo.currentAccentId;
            }
            preferences.edit().putString("lastDayCustomTheme", lastDayCustomTheme).apply();
        } else {
            if (dayAccentId == -1) {
                dayAccentId = Theme.getTheme(lastDayCustomTheme).lastAccentId;
            }
        }

        if (dayAccentId == -1) {
            lastDayCustomTheme = "Blue";
            dayAccentId = 99;
        }

        String lastDarkCustomTheme = preferences.getString("lastDarkCustomTheme", null);
        int darkAccentId = preferences.getInt("lastDarkCustomThemeAccentId", -1);
        if (lastDarkCustomTheme == null || Theme.getTheme(lastDarkCustomTheme) == null) {
            lastDarkCustomTheme = preferences.getString("lastDarkTheme", "Dark Blue");
            Theme.ThemeInfo themeInfo = Theme.getTheme(lastDarkCustomTheme);
            if (themeInfo == null) {
                lastDarkCustomTheme = "Dark Blue";
                darkAccentId = 0;
            } else {
                darkAccentId = themeInfo.currentAccentId;
            }
            preferences.edit().putString("lastDarkCustomTheme", lastDarkCustomTheme).apply();
        } else {
            if (darkAccentId == -1) {
                darkAccentId = Theme.getTheme(lastDayCustomTheme).lastAccentId;
            }
        }

        if (darkAccentId == -1) {
            lastDarkCustomTheme = "Dark Blue";
            darkAccentId = 0;
        }

        ThemeItem lightTheme = new ThemeItem();
        lightTheme.themeInfo = Theme.getTheme(lastDayCustomTheme);
        lightTheme.accentId = dayAccentId;
        themeItem.items.add(lightTheme);
        themeItem.items.add(null);

        ThemeItem darkTheme = new ThemeItem();
        darkTheme.themeInfo = Theme.getTheme(lastDarkCustomTheme);
        darkTheme.accentId = darkAccentId;
        themeItem.items.add(darkTheme);
        themeItem.items.add(null);

        return themeItem;
    }

    public static EmojiThemes createHomePreviewTheme(int currentAccount) {
        EmojiThemes themeItem = new EmojiThemes(currentAccount);
        themeItem.emoji = "\uD83C\uDFE0";
        themeItem.key = ThemeKey.ofEmoticon(themeItem.emoji);
        themeItem.chatTheme = TLRPC.ChatTheme.ofEmoticon(themeItem.emoji);

        ThemeItem blue = new ThemeItem();
        blue.themeInfo = Theme.getTheme("Blue");
        blue.accentId = 99;
        themeItem.items.add(blue);

        ThemeItem day = new ThemeItem();
        day.themeInfo = Theme.getTheme("Day");
        day.accentId = 9;
        themeItem.items.add(day);

        ThemeItem night = new ThemeItem();
        night.themeInfo = Theme.getTheme("Night");
        night.accentId = 0;
        themeItem.items.add(night);

        ThemeItem nightBlue = new ThemeItem();
        nightBlue.themeInfo = Theme.getTheme("Dark Blue");
        nightBlue.accentId = 0;
        themeItem.items.add(nightBlue);
        return themeItem;
    }

    public static EmojiThemes createLiquidGlassTheme(
            int currentAccount,
            String emoji,
            String themeName,
            int dayAccentId,
            int nightAccentId,
            int inBubbleColor,
            int outBubbleColor,
            int outLineColor,
            int bg1,
            int bg2,
            int bg3,
            int bg4,
            int rotation
    ) {
        EmojiThemes themeItem = new EmojiThemes(currentAccount);
        themeItem.emoji = emoji;
        themeItem.key = ThemeKey.ofEmoticon(emoji);
        themeItem.chatTheme = TLRPC.ChatTheme.ofEmoticon(emoji);

        Theme.ThemeInfo themeInfo = Theme.getTheme(themeName);
        if (themeInfo == null) {
            themeInfo = Theme.getTheme("Liquid Glass");
            if (themeInfo == null) themeInfo = Theme.getTheme("Night");
        }

        for (int i = 0; i < 4; i++) {
            ThemeItem item = new ThemeItem();
            item.themeInfo = themeInfo;
            item.accentId = (i >= 2) ? nightAccentId : dayAccentId;
            item.inBubbleColor = inBubbleColor;
            item.outBubbleColor = outBubbleColor;
            item.outLineColor = outLineColor;
            item.patternBgColor = bg1;
            item.patternBgGradientColor1 = bg2;
            item.patternBgGradientColor2 = bg3;
            item.patternBgGradientColor3 = bg4;
            item.patternBgRotation = rotation;
            themeItem.items.add(item);
        }

        return themeItem;
    }

    public static ArrayList<EmojiThemes> createPrismThemes(int currentAccount) {
        ArrayList<EmojiThemes> list = new ArrayList<>();
        // 1. Liquid Glass Pro (Frosted Chromatic Glass)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83E\uDEE7", "Liquid Glass", 0, 0, 0x281A2D4C, 0xFF00E5FF, 0xFF00E5FF, 0xFF0E1726, 0xFF1B2A4A, 0xFF112240, 0xFF0A1220, 45));
        // 2. Liquid Diamond (Ultra Pure Liquid Crystal)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83E\uDE9E", "Liquid Glass", 4, 4, 0x24242B3D, 0xFF818CF8, 0xFF818CF8, 0xFF12141D, 0xFF1C2030, 0xFF242A42, 0xFF141724, 135));
        // 3. Liquid Glacial (Frosted Arctic Ice)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83E\uDDCA", "Liquid Glass", 3, 3, 0x2616334D, 0xFF38BDF8, 0xFF38BDF8, 0xFF0B1B2B, 0xFF112E48, 0xFF183F60, 0xFF091724, 180));
        // 4. Liquid Nebula (Cosmic Ultra Violet Refractions)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83C\uDF0C", "Prism Nebula", 0, 0, 0x28261342, 0xFFA855F7, 0xFFA855F7, 0xFF160D2B, 0xFF251448, 0xFF381966, 0xFF120824, 45));
        // 5. Liquid Sunset Glow (Molten Amber & Crimson Glass)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83C\uDF05", "Prism Sunset", 0, 0, 0x28381520, 0xFFFB923C, 0xFFFB923C, 0xFF241018, 0xFF3B1522, 0xFF4D1D2B, 0xFF1A0A10, 135));
        // 6. Liquid Emerald (Translucent Jade & Cyber Mint)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83C\uDF3F", "Prism Matrix", 0, 0, 0x2614352A, 0xFF10B981, 0xFF10B981, 0xFF091C16, 0xFF0F2E23, 0xFF164032, 0xFF061510, 45));
        // 7. Liquid Ruby Obsidian (Velvet Wine Glass)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83C\uDF77", "Liquid Ruby", 0, 0, 0x262D0D1B, 0xFFE11D48, 0xFFE11D48, 0xFF1F0B13, 0xFF330F1F, 0xFF421429, 0xFF16060C, 45));
        // 8. Liquid Royal Gold (Molten Champagne & Amber)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83D\uDC51", "Prism Sunset", 1, 1, 0x282E210D, 0xFFF59E0B, 0xFFF59E0B, 0xFF1F170B, 0xFF33250E, 0xFF453112, 0xFF161006, 90));
        // 9. Liquid Cosmic Opal (Iridescent Multiverse Glass)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83E\uDE90", "Prism Nebula", 2, 2, 0x2822183D, 0xFF8B5CF6, 0xFF8B5CF6, 0xFF15102A, 0xFF112338, 0xFF28183E, 0xFF0B101E, 135));
        // 10. Liquid Cyber Chrome (Electric Neon & Titanium)
        list.add(createLiquidGlassTheme(currentAccount, "⚡", "Liquid Glass", 5, 5, 0x26172635, 0xFF00F0FF, 0xFF00F0FF, 0xFF0D141C, 0xFF14202C, 0xFF1C2D3E, 0xFF080D13, 45));
        // 11. Liquid Velvet Rose (Frosted Quartz & Pastel Pink)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83C\uDF3A", "Liquid Ruby", 2, 2, 0x28331326, 0xFFF472B6, 0xFFF472B6, 0xFF220E1A, 0xFF381429, 0xFF4A1A37, 0xFF180812, 135));
        // 12. Liquid Aquamarine (Deep Oceanic Glass)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83C\uDF0A", "Prism Aqua", 0, 0, 0x26102636, 0xFF06B6D4, 0xFF06B6D4, 0xFF081822, 0xFF0E2838, 0xFF14384E, 0xFF051017, 180));
        // 13. Liquid Midnight OLED (True Pitch Black Liquid Glass)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83D\uDDA4", "Night", 0, 0, 0x261B1E2B, 0xFF60A5FA, 0xFF60A5FA, 0xFF08090C, 0xFF101218, 0xFF181C26, 0xFF030406, 0));
        // 14. Liquid Matrix Core (Cyber Matrix Liquid Light)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83E\uDDEA", "Prism Matrix", 1, 1, 0x260C2618, 0xFF00FF66, 0xFF00FF66, 0xFF06140D, 0xFF0A2216, 0xFF0E3220, 0xFF030C07, 45));
        // 15. Liquid Peach Nectar (Translucent Apricot & Champagne)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83C\uDF4A", "Prism Sunset", 3, 3, 0x28361C12, 0xFFFB923C, 0xFFFB923C, 0xFF24140E, 0xFF3B1E13, 0xFF4E2819, 0xFF180C08, 135));
        // 16. Liquid Amethyst Prism (Refractive Purple Crystal)
        list.add(createLiquidGlassTheme(currentAccount, "\uD83D\uDD2E", "Liquid Glass", 1, 1, 0x28290E3C, 0xFFC084FC, 0xFFC084FC, 0xFF180A24, 0xFF2A0F3D, 0xFF3B1556, 0xFF100518, 90));
        return list;
    }

    public static EmojiThemes createHomeQrTheme(int currentAccount) {
        EmojiThemes themeItem = new EmojiThemes(currentAccount);
        themeItem.emoji = "\uD83C\uDFE0";
        themeItem.key = ThemeKey.ofEmoticon(themeItem.emoji);
        themeItem.chatTheme = TLRPC.ChatTheme.ofEmoticon(themeItem.emoji);

        ThemeItem blue = new ThemeItem();
        blue.themeInfo = Theme.getTheme("Blue");
        blue.accentId = 99;
        themeItem.items.add(blue);

        ThemeItem nightBlue = new ThemeItem();
        nightBlue.themeInfo = Theme.getTheme("Dark Blue");
        nightBlue.accentId = 0;
        themeItem.items.add(nightBlue);

        return themeItem;
    }

    public void initColors() {
        getPreviewColors(0, 0);
        getPreviewColors(0, 1);
    }

    @Deprecated
    public String getEmoticon() {
        return emoji;
    }

    public String getEmoticonOrSlug() {
        if (key == null) {
            return null;
        }

        if (key.giftSlug != null) {
            return key.giftSlug;
        }
        return key.emoticon;
    }


    public TLRPC.TL_theme getTlTheme(int index) {
        return items.get(index).tlTheme;
    }

    public ThemeKey getThemeKey() {
        return key;
    }

    public TLRPC.ChatTheme getChatTheme() {
        return chatTheme;
    }

    public ITheme getITheme(int index) {
        return items.get(index);
    }

    public long getThemeId(int index) {
        final ThemeItem item = items.get(index);
        return item.getThemeId();
    }

    public TLRPC.WallPaper getWallpaper(int index) {
        final ThemeItem item = items.get(index);
        final int settingsIndex = item.settingsIndex;
        return item.getThemeWallPaper(settingsIndex);
    }

    public String getWallpaperLink(int index) {
        return items.get(index).wallpaperLink;
    }

    public int getSettingsIndex(int index) {
        return items.get(index).settingsIndex;
    }

    public SparseIntArray getPreviewColors(int currentAccount, int index) {
        SparseIntArray currentColors = items.get(index).currentPreviewColors;
        if (currentColors != null) {
            return currentColors;
        }

        Theme.ThemeInfo themeInfo = getThemeInfo(index);
        Theme.ThemeAccent accent = null;
        if (themeInfo == null) {
            int settingsIndex = getSettingsIndex(index);
            final ITheme iTheme = getITheme(index);
            final TLRPC.TL_theme tlTheme = getTlTheme(index);
            Theme.ThemeInfo baseTheme;
            if (iTheme != null) {
                baseTheme = Theme.getTheme(Theme.getBaseThemeKey(iTheme.getThemeSettings(settingsIndex)));
            } else {
                baseTheme = Theme.getTheme("Blue");
            }
            if (baseTheme != null) {
                themeInfo = new Theme.ThemeInfo(baseTheme);
                if (iTheme != null) {
                    accent = themeInfo.createNewAccent(
                        iTheme.getThemeId(),
                        iTheme.getThemeSettings(settingsIndex),
                        tlTheme,
                        currentAccount,
                        true
                    );
                }
                if (accent != null) {
                    themeInfo.setCurrentAccentId(accent.id);
                }
            }
        } else {
            if (themeInfo.themeAccentsMap != null) {
                accent = themeInfo.themeAccentsMap.get(items.get(index).accentId);
            }
        }

        if (themeInfo == null) {
            return currentColors;
        }

        SparseIntArray currentColorsNoAccent;
        String[] wallpaperLink = new String[1];
        if (themeInfo.pathToFile != null) {
            currentColorsNoAccent = Theme.getThemeFileValues(new File(themeInfo.pathToFile), null, wallpaperLink);
        } else if (themeInfo.assetName != null) {
            currentColorsNoAccent = Theme.getThemeFileValues(null, themeInfo.assetName, wallpaperLink);
        } else {
            currentColorsNoAccent = new SparseIntArray();
        }

        items.get(index).wallpaperLink = wallpaperLink[0];

        if (accent != null) {
            currentColors = currentColorsNoAccent.clone();
            accent.fillAccentColors(currentColorsNoAccent, currentColors);
            if (isGiftTheme() && accent.parentTheme != null && accent.parentTheme.isLight()) {
                accent.resetAccentColorsForMyMessagesGiftThemeLight(currentColors);
            }
        } else {
            currentColors = currentColorsNoAccent;
        }

        SparseIntArray fallbackKeys = Theme.getFallbackKeys();
        SparseIntArray array = new SparseIntArray();
        items.get(index).currentPreviewColors = array;
        try {
            for (int i = 0; i < previewColorKeys.length; i++) {
                int key = previewColorKeys[i];
                int colorIndex = currentColors.indexOfKey(key);
                if (colorIndex >= 0) {
                    array.put(key, currentColors.valueAt(colorIndex));
                } else {
                    int fallbackKey = fallbackKeys.get(key, -1);
                    if (fallbackKey >= 0) {
                        int fallbackIndex = currentColors.indexOfKey(fallbackKey);
                        if (fallbackIndex >= 0) {
                            array.put(key, currentColors.valueAt(fallbackIndex));
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return array;
    }

    public SparseIntArray createColors(int currentAccount, int index) {
        SparseIntArray currentColors;

        Theme.ThemeInfo themeInfo = getThemeInfo(index);
        Theme.ThemeAccent accent = null;
        if (themeInfo == null) {
            int settingsIndex = getSettingsIndex(index);

            final ITheme iTheme = getITheme(index);
            final TLRPC.ThemeSettings settings = iTheme.getThemeSettings(settingsIndex);

            TLRPC.TL_theme tlTheme = getTlTheme(index);
            Theme.ThemeInfo baseTheme = Theme.getTheme(Theme.getBaseThemeKey(settings));
            themeInfo = new Theme.ThemeInfo(baseTheme);
            accent = themeInfo.createNewAccent(
                iTheme.getThemeId(),
                settings,
                tlTheme,
                currentAccount,
                true
            );
            themeInfo.setCurrentAccentId(accent.id);
        } else {
            if (themeInfo.themeAccentsMap != null) {
                accent = themeInfo.themeAccentsMap.get(items.get(index).accentId);
            }
        }

        SparseIntArray currentColorsNoAccent;
        String[] wallpaperLink = new String[1];
        if (themeInfo.pathToFile != null) {
            currentColorsNoAccent = Theme.getThemeFileValues(new File(themeInfo.pathToFile), null, wallpaperLink);
        } else if (themeInfo.assetName != null) {
            currentColorsNoAccent = Theme.getThemeFileValues(null, themeInfo.assetName, wallpaperLink);
        } else {
            currentColorsNoAccent = new SparseIntArray();
        }

        items.get(index).wallpaperLink = wallpaperLink[0];

        if (accent != null) {
            currentColors = currentColorsNoAccent.clone();
            accent.fillAccentColors(currentColorsNoAccent, currentColors);
            if (isGiftTheme() && accent.parentTheme != null && accent.parentTheme.isLight()) {
                accent.resetAccentColorsForMyMessagesGiftThemeLight(currentColors);
            }
        } else {
            currentColors = currentColorsNoAccent;
        }

        SparseIntArray fallbackKeys = Theme.getFallbackKeys();
        for (int i = 0; i < fallbackKeys.size(); i++) {
            int colorKey = fallbackKeys.keyAt(i);
            int fallbackKey = fallbackKeys.valueAt(i);
            if (currentColors.indexOfKey(colorKey) < 0) {
                int fallbackIndex = currentColors.indexOfKey(fallbackKey);
                if (fallbackIndex >= 0) {
                    currentColors.put(colorKey, currentColors.valueAt(fallbackIndex));
                }
            }
        }
        int[] defaultColors = Theme.getDefaultColors();
        for (int i = 0; i < defaultColors.length; i++) {
            if (currentColors.indexOfKey(i) < 0) {
                currentColors.put(i, defaultColors[i]);
            }
        }
        return currentColors;
    }

    public Theme.ThemeInfo getThemeInfo(int index) {
        return items.get(index).themeInfo;
    }

    public void loadWallpaper(int index, ResultCallback<Pair<Long, WallpaperBitmapHolder>> callback) {
        final TLRPC.WallPaper wallPaper = getWallpaper(index);
        if (wallPaper == null) {
            if (callback != null) {
                callback.onComplete(null);
            }
            return;
        }

        long themeId = getThemeId(index);
        loadWallpaperImage(currentAccount, wallPaper.id, wallPaper, wallpaper -> {
            if (callback != null) {
                callback.onComplete(new Pair<>(themeId, wallpaper));
            }
        });
    }

    public static void loadWallpaperImage(int currentAccount, long hash, TLRPC.WallPaper wallPaper, Utilities.Callback<WallpaperBitmapHolder> callback) {
        final int mode = wallPaper.pattern ?
            WallpaperBitmapHolder.MODE_PATTERN:
            WallpaperBitmapHolder.MODE_DEFAULT;

        ChatThemeController.getInstance(currentAccount).loadWallpaperBitmap(hash, mode, (cachedWallpaperBitmapHolder) -> {
            if (cachedWallpaperBitmapHolder != null && callback != null) {
                callback.run(cachedWallpaperBitmapHolder);
                return;
            }
            ImageLocation imageLocation = ImageLocation.getForDocument(wallPaper.document);
            ImageReceiver imageReceiver = new ImageReceiver();
            imageReceiver.setAllowLoadingOnAttachedOnly(false);

            String imageFilter;
            int w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            int h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            imageFilter = (w / AndroidUtilities.density) + "_" + (h / AndroidUtilities.density) + "_f";

            imageReceiver.setImage(imageLocation, imageFilter, null, ".jpg", wallPaper, 1);
            imageReceiver.setDelegate((receiver, set, thumb, memCache) -> {
                ImageReceiver.BitmapHolder holder = receiver.getBitmapSafe();
                ImageReceiver.BitmapHolder dHolder = receiver.getDrawableSafe();
                if (!set || holder == null) {
                    return;
                }
                List<WallpaperGiftPatternPosition> patternPositions = null;
                if (dHolder != null && dHolder.drawable instanceof WallpaperGiftBitmapDrawable) {
                    patternPositions = ((WallpaperGiftBitmapDrawable) dHolder.drawable).patternPositions;
                }

                Bitmap bitmap = holder.bitmap;
                if (bitmap == null && (holder.drawable instanceof BitmapDrawable)) {
                    bitmap = ((BitmapDrawable) holder.drawable).getBitmap();
                }

                final WallpaperBitmapHolder wallpaperBitmapHolder = new WallpaperBitmapHolder(bitmap, mode, patternPositions);
                if (callback != null) {
                    callback.run(wallpaperBitmapHolder);
                }
                ChatThemeController.getInstance(currentAccount).saveWallpaperBitmap(wallpaperBitmapHolder, hash);
            });
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
        });
    }

    public void loadWallpaperThumb(int index, ResultCallback<Pair<Long, Bitmap>> callback) {
        final TLRPC.WallPaper wallpaper = getWallpaper(index);
        if (wallpaper == null) {
            if (callback != null) {
                callback.onComplete(null);
            }
            return;
        }

        long themeId = getThemeId(index);
        if (themeId == 0) {
            if (callback != null) {
                callback.onComplete(null);
            }
            return;
        }
        Bitmap bitmap = ChatThemeController.getInstance(currentAccount).getWallpaperThumbBitmap(themeId);
        File file = getWallpaperThumbFile(themeId);
        if (bitmap == null && file.exists() && file.length() > 0) {
            try {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (bitmap != null) {
            if (callback != null) {
                callback.onComplete(new Pair<>(themeId, bitmap));
            }
            return;
        }

        if (wallpaper.document == null) {
            if (callback != null) {
                callback.onComplete(new Pair<>(themeId, null));
            }
            return;
        }
        final TLRPC.PhotoSize thumbSize = FileLoader.getClosestPhotoSizeWithSize(wallpaper.document.thumbs, 140);
        ImageLocation imageLocation = ImageLocation.getForDocument(thumbSize, wallpaper.document);
        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.setAllowLoadingOnAttachedOnly(false);
        imageReceiver.setImage(imageLocation, "120_140", null, null, null, 1);
        imageReceiver.setDelegate((receiver, set, thumb, memCache) -> {
            ImageReceiver.BitmapHolder holder = receiver.getBitmapSafe();
            if (!set || holder == null || holder.bitmap.isRecycled()) {
                return;
            }
            Bitmap resultBitmap = holder.bitmap;
            if (resultBitmap == null && (holder.drawable instanceof BitmapDrawable)) {
                resultBitmap = ((BitmapDrawable) holder.drawable).getBitmap();
            }
            if (resultBitmap != null) {
                if (callback != null) {
                    callback.onComplete(new Pair<>(themeId, resultBitmap));
                }
                final Bitmap saveBitmap = resultBitmap;
                Utilities.globalQueue.postRunnable(() -> {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        saveBitmap.compress(Bitmap.CompressFormat.PNG, 87, outputStream);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                });
            } else {
                if (callback != null) {
                    callback.onComplete(null);
                }
            }
        });
        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
    }

    public void preloadWallpaper() {
        loadWallpaperThumb(0, null);
        loadWallpaperThumb(1, null);
        loadWallpaper(0, null);
        loadWallpaper(1, null);
    }

    private File getWallpaperThumbFile(long themeId) {
        return new File(ApplicationLoader.getFilesDirFixed(), "wallpaper_thumb_" + themeId + ".png");
    }

    public static Theme.ThemeInfo getDefaultThemeInfo(boolean isDark) {
        Theme.ThemeInfo themeInfo = isDark ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        if (isDark != themeInfo.isDark()) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
            String lastThemeName = isDark
                    ? preferences.getString("lastDarkTheme", "Dark Blue")
                    : preferences.getString("lastDayTheme", "Blue");
            themeInfo = Theme.getTheme(lastThemeName);
            if (themeInfo == null) {
                themeInfo = Theme.getTheme(isDark ? "Dark Blue" : "Blue");
            }
        }
        return new Theme.ThemeInfo(themeInfo);
    }

    public static void fillTlTheme(Theme.ThemeInfo themeInfo) {
        if (themeInfo.info == null) {
            themeInfo.info = new TLRPC.TL_theme();
        }
    }

    public static SparseIntArray getPreviewColors(Theme.ThemeInfo themeInfo) {
        SparseIntArray currentColorsNoAccent;
        if (themeInfo.pathToFile != null) {
            currentColorsNoAccent = Theme.getThemeFileValues(new File(themeInfo.pathToFile), null, null);
        } else if (themeInfo.assetName != null) {
            currentColorsNoAccent = Theme.getThemeFileValues(null, themeInfo.assetName, null);
        } else {
            currentColorsNoAccent = new SparseIntArray();
        }
        SparseIntArray currentColors = currentColorsNoAccent.clone();
        Theme.ThemeAccent themeAccent = themeInfo.getAccent(false);
        if (themeAccent != null) {
            themeAccent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        return currentColors;
    }

    public int getAccentId(int themeIndex) {
        return items.get(themeIndex).accentId;
    }

    public void loadPreviewColors(int currentAccount) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                continue;
            }
            if (items.get(i).patternBgColor == 0) {
                SparseIntArray colorsMap = getPreviewColors(currentAccount, i);
                items.get(i).inBubbleColor = getOrDefault(colorsMap, Theme.key_chat_inBubble);
                items.get(i).outBubbleColor = getOrDefault(colorsMap, Theme.key_chat_outBubble);
                items.get(i).outLineColor = getOrDefault(colorsMap, Theme.key_featuredStickers_addButton);
                items.get(i).patternBgColor = colorsMap.get(Theme.key_chat_wallpaper, 0);
                items.get(i).patternBgGradientColor1 = colorsMap.get(Theme.key_chat_wallpaper_gradient_to1, 0);
                items.get(i).patternBgGradientColor2 = colorsMap.get(Theme.key_chat_wallpaper_gradient_to2, 0);
                items.get(i).patternBgGradientColor3 = colorsMap.get(Theme.key_chat_wallpaper_gradient_to3, 0);
                items.get(i).patternBgRotation = colorsMap.get(Theme.key_chat_wallpaper_gradient_rotation, 0);

                if (items.get(i).themeInfo != null && items.get(i).themeInfo.getKey().equals("Blue")) {
                    int accentId = items.get(i).accentId >= 0 ? items.get(i).accentId : items.get(i).themeInfo.currentAccentId;
                    if (accentId == 99) {
                        items.get(i).patternBgColor = 0xffdbddbb;
                        items.get(i).patternBgGradientColor1 = 0xff6ba587;
                        items.get(i).patternBgGradientColor2 = 0xffd5d88d;
                        items.get(i).patternBgGradientColor3 = 0xff88b884;
                    }
                }
            }
        }
    }

    private int getOrDefault(SparseIntArray colorsMap, int key) {
        if (colorsMap == null) return Theme.getDefaultColor(key);
        try {
            int index = colorsMap.indexOfKey(key);
            if (index >= 0) {
                return colorsMap.valueAt(index);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return Theme.getDefaultColor(key);
    }

    public ThemeItem getThemeItem(int index) {
        return items.get(index);
    }

    public static void saveCustomTheme(Theme.ThemeInfo themeInfo, int accentId) {
        if (themeInfo == null) {
            return;
        }
        if (accentId >= 0 && themeInfo.themeAccentsMap != null) {
            Theme.ThemeAccent accent = themeInfo.themeAccentsMap.get(accentId);
            if (accent == null || accent.isDefault) {
                return;
            }
        }
        if (themeInfo.getKey().equals("Blue") && accentId == 99) {
            return;
        }
        if (themeInfo.getKey().equals("Day") && accentId == 9) {
            return;
        }
        if (themeInfo.getKey().equals("Night") && accentId == 0) {
            return;
        }
        if (themeInfo.getKey().equals("Dark Blue") && accentId == 0) {
            return;
        }

        boolean dark = themeInfo.isDark();
        String key = dark ? "lastDarkCustomTheme" : "lastDayCustomTheme";
        String accentKey = dark ? "lastDarkCustomThemeAccentId" : "lastDayCustomThemeAccentId";
        ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE).edit()
                .putString(key, themeInfo.getKey())
                .putInt(accentKey, accentId)
                .apply();
    }

    public static class ThemeItem implements ITheme {

        public Theme.ThemeInfo themeInfo;
        TLRPC.TL_theme tlTheme;
        TLRPC.TL_chatThemeUniqueGift tlChatThemeGift;
        int settingsIndex;
        public int accentId = -1;
        public SparseIntArray currentPreviewColors;
        private String wallpaperLink;

        public int inBubbleColor;
        public int outBubbleColor;
        public int outLineColor;
        public int patternBgColor;
        public int patternBgGradientColor1;
        public int patternBgGradientColor2;
        public int patternBgGradientColor3;
        public int patternBgRotation;


        @Override
        public long getThemeId() {
            if (tlTheme != null) {
                return tlTheme.id;
            } else if (tlChatThemeGift != null) {
                return tlChatThemeGift.gift.gift_id;
            }
            return 0;
        }

        @Override
        public TLRPC.ThemeSettings getThemeSettings(int settingsIndex) {
            ArrayList<TLRPC.ThemeSettings> settings;
            if (tlTheme != null) {
                settings = tlTheme.settings;
            } else if (tlChatThemeGift != null) {
                settings = tlChatThemeGift.theme_settings;
            } else {
                return null;
            }

            if (settings != null && settingsIndex >= 0 && settings.size() > settingsIndex) {
                return settings.get(settingsIndex);
            }

            return null;
        }

        @Override
        public TLRPC.WallPaper getThemeWallPaper(int settingsIndex) {
            final TLRPC.ThemeSettings settings = getThemeSettings(settingsIndex);
            return settings != null ? settings.wallpaper : null;
        }
    }

    public TL_stars.TL_starGiftUnique getThemeGift() {
        if (chatTheme instanceof TLRPC.TL_chatThemeUniqueGift) {
            TL_stars.StarGift gift = ((TLRPC.TL_chatThemeUniqueGift) chatTheme).gift;
            if (gift instanceof TL_stars.TL_starGiftUnique)
            return (TL_stars.TL_starGiftUnique) gift;
        }

        return null;
    }

    public long getBusyByUserId() {
        if (chatTheme instanceof TLRPC.TL_chatThemeUniqueGift) {
            return ChatThemeController.getInstance(currentAccount)
                .getGiftThemeUser(((TLRPC.TL_chatThemeUniqueGift) chatTheme).gift.slug);
        }
        return 0;
    }

    public TLRPC.Document getEmojiAnimatedSticker() {
        if (chatTheme instanceof TLRPC.TL_chatThemeUniqueGift) {
            return TlUtils.getGiftDocument(((TLRPC.TL_chatThemeUniqueGift) chatTheme).gift);
        } else if (chatTheme instanceof TLRPC.TL_chatTheme) {
            return MediaDataController.getInstance(currentAccount)
                    .getEmojiAnimatedSticker(((TLRPC.TL_chatTheme) chatTheme).emoticon);
        }
        return null;
    }

    public void loadWallpaperGiftPattern(int index, ResultCallback<Pair<Long, Bitmap>> callback) {
        final ThemeItem item = getThemeItem(index);
        if (item != null && item.tlChatThemeGift != null) {
            long themeId = getThemeId(index);
            loadWallpaperGiftPattern(currentAccount, themeId, item.tlChatThemeGift.gift, callback);
        }
    }

    public static void loadWallpaperGiftPattern(int currentAccount, long hash, TL_stars.StarGift gift, ResultCallback<Pair<Long, Bitmap>> callback) {
        //ChatThemeController.getInstance(currentAccount).getWallpaperBitmap(hash, cachedBitmap -> {
            /*if (cachedBitmap != null && callback != null) {
                callback.onComplete(new Pair<>(hash, cachedBitmap));
                return;
            }*/

        TLRPC.Document document = TlUtils.getGiftDocumentPattern(gift);
        ImageLocation imageLocation = ImageLocation.getForDocument(document);
        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.setAllowLoadingOnAttachedOnly(false);

        imageReceiver.setImage(imageLocation, "40_40_firstframe", null, ".jpg", document, 1);
        imageReceiver.setDelegate((receiver, set, thumb, memCache) -> {
            ImageReceiver.BitmapHolder holder = receiver.getBitmapSafe();
            if (!set || holder == null) {
                return;
            }
            Bitmap bitmap = holder.bitmap;
            if (bitmap == null && (holder.drawable instanceof BitmapDrawable)) {
                bitmap = ((BitmapDrawable) holder.drawable).getBitmap();
            }
            if (callback != null) {
                callback.onComplete(new Pair<>(hash, bitmap));
            }
            // ChatThemeController.getInstance(currentAccount).saveWallpaperBitmap(bitmap, hash);
        });
        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
        //});
    }

}
