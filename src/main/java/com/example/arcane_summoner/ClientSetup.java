package com.example.arcane_summoner;

import com.example.arcane_summoner.client.render.HumanKnightRenderer;
import com.example.arcane_summoner.client.screen.MagicWandAltarScreen;
import com.example.arcane_summoner.registry.ModEntities;
import com.example.arcane_summoner.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.MAGIC_WAND_ALTAR.get(), MagicWandAltarScreen::new);
        });
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.HUMAN_KNIGHT.get(), HumanKnightRenderer::new);
    }

}
