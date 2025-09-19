package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.content.block.MagicWandAltarBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ArcaneSummoner.MODID);

    public static final RegistryObject<Block> MAGIC_WAND_ALTAR = BLOCKS.register("magic_wand_altar",
            () -> new MagicWandAltarBlock(BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.COLOR_BLUE) // cor do mapa
                    .strength(1.0F, 1200.0F)       // resistência/explosão
                    .sound(SoundType.AMETHYST)     // som ao colocar/quebrar
                    .lightLevel(s -> 12)           // emite luz leve
                    .noOcclusion()                 // não bloqueia visão total
                    .pushReaction(PushReaction.DESTROY) // pistões destroem
            ));
}
