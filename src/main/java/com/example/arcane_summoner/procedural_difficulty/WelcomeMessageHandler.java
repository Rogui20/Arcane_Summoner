package com.example.arcane_summoner.procedural_difficulty;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.config.ModConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID)
public class WelcomeMessageHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!ModConfig.isProceduralEnabled()) return;

        ProceduralDifficultyData data = ProceduralDifficultyData.get(level.getServer());
        double mult = data.getMultiplier();

        String multStr = String.format("%.2f", mult);

        Component msg;
        if (mult > 50.0) {
            msg = Component.literal("§4☠ O apocalipse chegou! O poder dos monstros está em "
                    + multStr + "x! CORRAM!");
            level.playSound(null, player.blockPosition(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER,
                    2.0F, 0.8F + level.random.nextFloat() * 0.4F);
        } else if (mult > 7.0) {
            msg = Component.literal("§c⚠ O poder dos monstros tá acima de "
                    + multStr + "x, fiquem espertos!");
        } else {
            msg = Component.literal("§eO poder dos monstros está sendo multiplicado por "
                    + multStr + " agora.");
        }

        player.sendSystemMessage(msg);
    }
}