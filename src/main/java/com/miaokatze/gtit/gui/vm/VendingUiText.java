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

    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }
}
