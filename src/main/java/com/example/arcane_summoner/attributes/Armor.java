package com.example.arcane_summoner.attributes;

import com.example.arcane_summoner.config.ModConfig;
import net.minecraft.world.item.ItemStack;

public class Armor {
    public static double calculateArmor(ItemStack stack) {
        if (stack.isEmpty()) return 0.0;
        return ModConfig.getArmorValue(stack.getItem());
    }
}
