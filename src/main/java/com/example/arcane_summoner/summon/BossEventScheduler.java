package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID)
public class BossEventScheduler {

    private static final Random RNG = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!ModConfig.getBossRandomEventsEnabled()) return;

        var server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) {
            long now = level.getDayTime();

            BossSchedulerData data = BossSchedulerData.get(level);
            long next = data.getNextTrigger();

            // Se ainda não foi programado, calcula
            if (next < 0) {
                next = computeNextTriggerTicks(now);
                data.setNextTrigger(next);
            }

            // Se chegou a hora
            if (now >= next) {
                for (ServerPlayer sp : level.players()) {
                    double mult = ModConfig.nextBossMultiplier();
                    BossSpawner.spawnBossFor(sp, mult);
                }
                // Reagenda
                long newNext = computeNextTriggerTicks(now);
                data.setNextTrigger(newNext);
            }
        }
    }

    private static long computeNextTriggerTicks(long now) {
        int dmin = ModConfig.getBossRandomDaysMin();
        int dmax = ModConfig.getBossRandomDaysMax();
        int days = dmin + RNG.nextInt(Math.max(1, (dmax - dmin + 1)));
        return now + (days * 24000L); // 1 dia = 24000 ticks
    }
}