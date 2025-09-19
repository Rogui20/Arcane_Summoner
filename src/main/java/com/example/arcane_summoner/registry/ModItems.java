package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.ArcaneSummoner;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneSummoner.MODID);

    public static final RegistryObject<Item> MAGIC_WAND_ALTAR =
            ITEMS.register("magic_wand_altar", () -> new BlockItem(
                    ModBlocks.MAGIC_WAND_ALTAR.get(),
                    new Item.Properties()
            ));
}
