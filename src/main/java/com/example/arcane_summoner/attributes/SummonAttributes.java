package com.example.arcane_summoner.attributes;

import net.minecraft.world.item.ItemStack;

public class SummonAttributes {
    public static double getArmor(ItemStack stack) {
        return Armor.calculateArmor(stack);
    }

    public static double getStrength(ItemStack stack) {
        return Strength.calculateStrength(stack);
    }

    public static double getHealth(ItemStack stack) {
        return Health.calculateExtraHealth(stack);
    }
}
