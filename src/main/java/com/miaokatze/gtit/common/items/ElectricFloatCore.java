package com.miaokatze.gtit.common.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.miaokatze.gtit.common.util.GTITUtils;
import com.miaokatze.gtit.register.CreativeTabManager;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

/**
 * 电力浮空核心饰品
 * <p>
 * 可以装备到任意Baubles饰品栏，提供创造飞行能力，优先消耗电力，电力耗尽后消耗饥饿值。
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class ElectricFloatCore extends Item implements IBauble, IElectricItem {

    private static final long COST_PER_TICK = 128L;

    private final String tooltipBase;
    private final double maxCharge;
    private final int tier;
    private final double transferLimit;

    /** LV 版：32M EU，32 EU/t 充电。 */
    public ElectricFloatCore() {
        this("electric_float_core", 32_000_000L, 1, 32L);
    }

    /**
     * @param name          注册/本地化名（同时作为 tooltip 键前缀）
     * @param maxCharge     电容量 (EU)
     * @param tier          IC2 电压等级（1 LV … 4 EV）
     * @param transferLimit 每 tick 最大充电量 (EU/t)
     */
    protected ElectricFloatCore(String name, long maxCharge, int tier, long transferLimit) {
        super();
        this.tooltipBase = "item." + name + ".tooltip.";
        this.maxCharge = maxCharge;
        this.tier = tier;
        this.transferLimit = transferLimit;
        setUnlocalizedName(name);
        setTextureName("gtit:electric_float_core");
        setCreativeTab(CreativeTabManager.CREATIVE_TAB);
        setMaxStackSize(1);
    }

    // ========== Baubles饰品实现 ==========

    @Override
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.UNIVERSAL;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.worldObj.isRemote) return;

        if (player instanceof EntityPlayer entityPlayer) {
            // 检查是否满足飞行条件：电力足够，或者电力不足但饥饿值>=6
            boolean hasPower = ElectricItem.manager.canUse(itemstack, COST_PER_TICK);
            boolean hasFood = entityPlayer.getFoodStats()
                .getFoodLevel() >= 6;
            boolean canFly = hasPower || hasFood;

            if (canFly) {
                // 条件恢复后重新启用飞行，需要同步到客户端
                if (!entityPlayer.capabilities.allowFlying) {
                    entityPlayer.capabilities.allowFlying = true;
                    entityPlayer.sendPlayerAbilities();
                }

                // 检测是否正在飞行
                if (entityPlayer.capabilities.isFlying) {
                    // 先尝试消耗电力
                    if (ElectricItem.manager.canUse(itemstack, COST_PER_TICK)) {
                        ElectricItem.manager.use(itemstack, COST_PER_TICK, entityPlayer);
                    } else {
                        // 电力不足，消耗饥饿值
                        entityPlayer.getFoodStats()
                            .addExhaustion(0.01f);
                    }
                }
            } else {
                // 条件不满足，禁用飞行
                // 仅在状态确实变化时发包，避免每 tick 重复发送（与上方启用分支对称）
                if (entityPlayer.capabilities.allowFlying || entityPlayer.capabilities.isFlying) {
                    entityPlayer.capabilities.allowFlying = false;
                    entityPlayer.capabilities.isFlying = false;
                    entityPlayer.sendPlayerAbilities();
                }
            }
        }
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        if (player.worldObj.isRemote) return;

        if (player instanceof EntityPlayer entityPlayer) {
            entityPlayer.capabilities.allowFlying = true;
            entityPlayer.sendPlayerAbilities();
        }
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        if (player.worldObj.isRemote) return;

        if (player instanceof EntityPlayer entityPlayer) {
            if (!entityPlayer.capabilities.isCreativeMode) {
                entityPlayer.capabilities.allowFlying = false;
                entityPlayer.capabilities.isFlying = false;
            }
            entityPlayer.sendPlayerAbilities();
        }
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean showAdvanced) {
        for (int i = 0;; i++) {
            String key = tooltipBase + i;
            String line = StatCollector.translateToLocal(key);
            if (line.equals(key)) break;
            tooltip.add(EnumChatFormatting.GOLD + line);
        }
        tooltip.add(GTITUtils.getAddedByLine());
    }

    // ========== IC2电力物品实现 ==========

    @Override
    public boolean canProvideEnergy(ItemStack aStack) {
        return true;
    }

    @Override
    public double getMaxCharge(ItemStack aStack) {
        return maxCharge;
    }

    @Override
    public int getTier(ItemStack aStack) {
        return tier;
    }

    @Override
    public double getTransferLimit(ItemStack aStack) {
        return transferLimit;
    }

    @Override
    public Item getChargedItem(ItemStack aStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack aStack) {
        return this;
    }
}
