package com.miaokatze.gtit.gui.vm;

import net.minecraft.util.StatCollector;

/**
 * Translatable labels for the Neko Vending Machine and its related screens.
 *
 * UI strings are resolved from assets/gtit/lang according to the player's
 * selected Minecraft language. All original Chinese text is retained in
 * zh_CN.lang. No network IDs, save formats or trade logic are changed.
 */
public final class VendingUiText {

    private VendingUiText() {}

    /**
     * Translate only known built-in tab display names, never their saved IDs.
     * User-defined tab names are preserved exactly as entered.
     */
    public static String displayPageName(String original) {
        if (original == null) return null;
        return switch (original) {
            case "猫猫币" -> tr("gtit.ui.defaultpage.neko");
            case "闪烁猫猫币" -> tr("gtit.ui.defaultpage.shimmering");
            case "周期领取" -> tr("gtit.ui.defaultpage.periodic");
            case "基础" -> tr("gtit.ui.defaultpage.basic");
            case "其他" -> tr("gtit.ui.defaultpage.other");
            default -> original;
        };
    }

    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }
}
