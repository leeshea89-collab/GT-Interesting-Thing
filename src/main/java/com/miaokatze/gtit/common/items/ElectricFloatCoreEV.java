package com.miaokatze.gtit.common.items;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 电力浮空核心 EV 版
 * <p>
 * 与 LV 版行为相同，但为 EV 电压等级（2048 EU/t 充电），容量 128M EU。
 * LV 版可在手持 2x2 合成格中一换一（保留电量），见 {@code FloatCoreUpgradeRecipe}。
 */
public class ElectricFloatCoreEV extends ElectricFloatCore {

    public static final long MAX_CHARGE = 128_000_000L;
    public static final int TIER = 4; // EV
    public static final long TRANSFER_LIMIT = 2048L;

    public ElectricFloatCoreEV() {
        super("electric_float_core_ev", MAX_CHARGE, TIER, TRANSFER_LIMIT);
    }

    /** 附魔光效，用来和 LV 版区分（共用同一贴图）。 */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }
}
