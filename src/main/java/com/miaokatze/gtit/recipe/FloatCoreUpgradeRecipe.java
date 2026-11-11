package com.miaokatze.gtit.recipe;

import java.util.Collections;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.RecipeSorter;

import com.miaokatze.gtit.common.api.enums.GTITItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.ElectricItem;

/**
 * LV 电力浮空核心 → EV 电力浮空核心 的以旧换新配方。
 * <p>
 * 无序配方，只需 1 个物品，所以在玩家背包的 2x2 合成格里即可完成。
 * 继承原版 {@link ShapelessRecipes}，NEI 的无序配方处理器能直接显示它。
 * 旧核心里剩余的电量会转入新核心（不超过新核心容量）。
 */
public class FloatCoreUpgradeRecipe extends ShapelessRecipes {

    private static final int WILDCARD = 32767;

    public FloatCoreUpgradeRecipe(ItemStack output, ItemStack oldCore) {
        super(output, Collections.singletonList(oldCore));
    }

    public static void register() {
        if (GTITItemList.ElectricFloatCore.get(1) == null || GTITItemList.ElectricFloatCoreEV.get(1) == null) {
            return;
        }
        ItemStack oldCore = GTITItemList.ElectricFloatCore.get(1);
        oldCore.setItemDamage(WILDCARD);
        RecipeSorter.register(
            "gtit:float_core_upgrade",
            FloatCoreUpgradeRecipe.class,
            RecipeSorter.Category.SHAPELESS,
            "after:minecraft:shapeless");
        GameRegistry.addRecipe(new FloatCoreUpgradeRecipe(GTITItemList.ElectricFloatCoreEV.get(1), oldCore));
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = getRecipeOutput().copy();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack in = inv.getStackInSlot(i);
            if (in == null || in.getItem() != GTITItemList.ElectricFloatCore.getItem()) continue;
            double charge = ElectricItem.manager.getCharge(in);
            if (charge > 0) {
                ElectricItem.manager.charge(result, charge, Integer.MAX_VALUE, true, false);
            }
            break;
        }
        return result;
    }
}
