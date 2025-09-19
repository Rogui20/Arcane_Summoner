package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.content.entity.HumanKnight;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ArcaneSummoner.MODID);

    public static final RegistryObject<EntityType<HumanKnight>> HUMAN_KNIGHT =
    ENTITIES.register("human_knight", 
        () -> EntityType.Builder.<HumanKnight>of(HumanKnight::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F) // tamanho aproximado de player
            .build("human_knight"));

}

