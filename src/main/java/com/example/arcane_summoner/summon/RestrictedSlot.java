package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.config.ModConfig;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RestrictedSlot extends Slot {
    private final int slotIndex;

    public RestrictedSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
        this.slotIndex = index;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return ModConfig.isItemAllowedForSlot(slotIndex, stack.getItem());
    }
}
