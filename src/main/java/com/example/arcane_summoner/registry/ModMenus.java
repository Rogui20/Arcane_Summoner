package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.content.block.entity.MagicWandAltarBlockEntity;
import com.example.arcane_summoner.content.block.menu.MagicWandAltarMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "arcane_summoner");

    public static final RegistryObject<MenuType<MagicWandAltarMenu>> MAGIC_WAND_ALTAR =
            MENUS.register("magic_wand_altar", () ->
                    IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        BlockEntity be = inv.player.level().getBlockEntity(pos);
                        if (be instanceof MagicWandAltarBlockEntity altar) {
                            ContainerData containerData = new SimpleContainerData(1);
                            return new MagicWandAltarMenu(windowId, inv, altar, containerData);
                        }
                        return null;
                    }));
}
