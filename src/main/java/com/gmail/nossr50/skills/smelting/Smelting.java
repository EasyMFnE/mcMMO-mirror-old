package com.gmail.nossr50.skills.smelting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.ModUtils;

public class Smelting {
    // The order of the values is extremely important, a few methods depend on it to work properly
    public enum Tier {
        EIGHT(8),
        SEVEN(7),
        SIX(6),
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2),
        ONE(1);

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        protected int getLevel() {
            return AdvancedConfig.getInstance().getSmeltingRankLevel(this);
        }

        protected int getVanillaXPBoostModifier() {
            return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostMultiplier(this);
        }
    }

    public static int    burnModifierMaxLevel = AdvancedConfig.getInstance().getBurnModifierMaxLevel();
    public static double burnTimeMultiplier   = AdvancedConfig.getInstance().getBurnTimeMultiplier();

    public static int    secondSmeltMaxLevel  = AdvancedConfig.getInstance().getSecondSmeltMaxLevel();
    public static double secondSmeltMaxChance = AdvancedConfig.getInstance().getSecondSmeltMaxChance();

    public static int    fluxMiningUnlockLevel = AdvancedConfig.getInstance().getFluxMiningUnlockLevel();
    public static double fluxMiningChance      = AdvancedConfig.getInstance().getFluxMiningChance();

    protected static int getResourceXp(ItemStack smelting) {
        MaterialData data = smelting.getData();
        Material resourceType = smelting.getType();

        return ModUtils.isCustomOre(data) ? ModUtils.getCustomBlock(data).getSmeltingXpGain() : ExperienceConfig.getInstance().getXp(SkillType.SMELTING, resourceType != Material.GLOWING_REDSTONE_ORE ? resourceType : Material.REDSTONE_ORE);
    }
}
