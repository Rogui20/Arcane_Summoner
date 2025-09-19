package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.content.entity.HumanKnight;
import com.example.arcane_summoner.client.render.HumanKnightRenderer;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.HUMAN_KNIGHT.get(), HumanKnight.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HUMAN_KNIGHT.get(), HumanKnightRenderer::new);
    }
}
