package com.example.arcane_summoner.attributes;

import com.example.arcane_summoner.config.ModConfig;
import net.minecraft.world.item.ItemStack;

public class Health {
    public static double calculateExtraHealth(ItemStack stack) {
        if (stack.isEmpty()) return 0.0;
        return ModConfig.getFoodValue(stack.getItem());
    }
}
