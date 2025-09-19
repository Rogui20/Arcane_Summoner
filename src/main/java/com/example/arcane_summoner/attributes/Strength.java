package com.example.arcane_summoner.attributes;

import com.example.arcane_summoner.config.ModConfig;
import net.minecraft.world.item.ItemStack;

public class Strength {
    public static double calculateStrength(ItemStack stack) {
        if (stack.isEmpty()) return 0.0;
        // Aqui usamos o stackLimit como "peso de força"
        return ModConfig.getStackLimit(stack.getItem());
    }
}
