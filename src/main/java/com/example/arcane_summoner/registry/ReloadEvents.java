package com.example.arcane_summoner.registry;

import java.nio.file.Path;

import com.example.arcane_summoner.config.ModConfig;
import com.example.arcane_summoner.ArcaneSummoner;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReloadEvents {
    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        Path configDir = FMLPaths.CONFIGDIR.get();
        ModConfig.load(configDir);
        System.out.println("[ArcaneSummoner] Configs recarregadas via /reload");
    }
}
