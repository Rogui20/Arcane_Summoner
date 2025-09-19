package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.content.block.entity.MagicWandAltarBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArcaneSummoner.MODID);

    public static final RegistryObject<BlockEntityType<MagicWandAltarBlockEntity>> MAGIC_WAND_ALTAR =
            BLOCK_ENTITIES.register("magic_wand_altar",
                    () -> BlockEntityType.Builder.of(
                            MagicWandAltarBlockEntity::new,
                            ModBlocks.MAGIC_WAND_ALTAR.get()
                    ).build(null));
}
