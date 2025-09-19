package com.example.arcane_summoner;

import com.example.arcane_summoner.config.ModConfig;
import com.example.arcane_summoner.config.SlotRulesConfig;
import com.example.arcane_summoner.content.entity.HumanKnight;
import com.example.arcane_summoner.client.render.HumanKnightRenderer;
import com.example.arcane_summoner.config.ConfigGenerator;
import com.example.arcane_summoner.registry.ModBlocks;
import com.example.arcane_summoner.registry.ModEntities;
import com.example.arcane_summoner.registry.ModItems;
import com.example.arcane_summoner.registry.ModBlockEntities;
import com.example.arcane_summoner.registry.ModMenus;
import com.example.arcane_summoner.registry.ModNetworking;
import com.example.arcane_summoner.summon.PersistentHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(ArcaneSummoner.MODID)
public class ArcaneSummoner {
    public static final String MODID = "arcane_summoner";

    public ArcaneSummoner() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // registrar coisas que dependem do registrador (DeferredRegister)
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);

        // listener de setup comum (lado servidor + cliente compartilhado)
        modEventBus.addListener(this::commonSetup);

        // listener de setup do cliente
        modEventBus.addListener(ClientSetup::init);

        // gerar configs
        Path configDir = FMLPaths.CONFIGDIR.get();
        ConfigGenerator.generateDefaults(configDir);
        ModConfig.load(configDir);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetworking.register(); // inicializa o canal de rede
        });
    }

}
