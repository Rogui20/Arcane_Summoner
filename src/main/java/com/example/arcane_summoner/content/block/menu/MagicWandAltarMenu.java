package com.example.arcane_summoner.content.block.menu;

import com.example.arcane_summoner.config.ModConfig;
import com.example.arcane_summoner.content.block.entity.MagicWandAltarBlockEntity;
import com.example.arcane_summoner.registry.ModMenus;
import com.example.arcane_summoner.summon.RestrictedSlot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MagicWandAltarMenu extends AbstractContainerMenu {
    private final MagicWandAltarBlockEntity altar;

    public MagicWandAltarMenu(int id, Inventory playerInv, MagicWandAltarBlockEntity altar, ContainerData data) {
        super(ModMenus.MAGIC_WAND_ALTAR.get(), id);
        this.altar = altar;

        // Slots do altar (14 slots → 7x2)
        for (int i = 0; i < 14; i++) {
            int col = i % 7;
            int row = i / 7;
            int x = 26 + col * 18;
            int y = 18 + row * 18;
            this.addSlot(new RestrictedSlot(this.altar, i, x, y));

        }

        // Inventário do player (3 linhas de 9 + hotbar)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public MagicWandAltarBlockEntity getAltar() {
        return this.altar;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            int altarSlots = this.altar.getContainerSize();

            // Move do altar para o inventário
            if (index < altarSlots) {
                if (!this.moveItemStackTo(stack, altarSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Move do inventário para o altar
            else if (!this.moveItemStackTo(stack, 0, altarSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // devolve todos os itens do altar para o player
        if (!player.level().isClientSide) {
            for (int i = 0; i < this.altar.getContainerSize(); i++) {
                ItemStack stack = this.altar.getItem(i);
                if (!stack.isEmpty()) {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false); // dropa no chão se não couber
                    }
                    this.altar.setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }
}
