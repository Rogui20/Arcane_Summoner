package com.example.arcane_summoner.content.block.entity;

import com.example.arcane_summoner.registry.ModBlockEntities;
import com.example.arcane_summoner.content.block.menu.MagicWandAltarMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;

public class MagicWandAltarBlockEntity extends BlockEntity implements Container, MenuProvider {
    private final SimpleContainer items = new SimpleContainer(14); // 14 slots

    public MagicWandAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGIC_WAND_ALTAR.get(), pos, state);
    }

    // -------------------------------
    // Container methods
    // -------------------------------
    @Override
    public int getContainerSize() {
        return items.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return items.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return items.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        // Agora sem cortar stacks — aceita até 64 normal
        items.setItem(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clearContent();
    }

    // -------------------------------
    // MenuProvider
    // -------------------------------
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcane_summoner.magic_wand_altar");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        ContainerData data = new SimpleContainerData(1);
        return new MagicWandAltarMenu(id, playerInv, this, data);
    }
}
