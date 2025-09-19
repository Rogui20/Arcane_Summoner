package com.example.arcane_summoner.registry;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.config.ModConfig;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        Path configDir = FMLPaths.CONFIGDIR.get();
        ModConfig.load(configDir);
        System.out.println("[ArcaneSummoner] Configs recarregadas ao iniciar o servidor/mundo");
    }
}