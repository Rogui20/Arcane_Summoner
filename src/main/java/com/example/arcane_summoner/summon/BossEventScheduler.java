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

    private static final Map<ServerLevel, Long> nextTriggerByLevel = new HashMap<>();
    private static final Random RNG = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!ModConfig.getBossRandomEventsEnabled()) return;

        var server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) {
            long now = level.getDayTime(); // ticks do mundo (conta dormir, mas usaremos gatilho por período)
            long next = nextTriggerByLevel.computeIfAbsent(level, l -> computeNextTriggerTicks(level, now));

            if (now >= next) {
                // tentar spawnar 1 boss por player online naquele level
                for (ServerPlayer sp : level.players()) {
                    double mult = ModConfig.nextBossMultiplier(); // aleatório entre [min,max]
                    BossSpawner.spawnBossFor(sp, mult);
                }
                // reprograma
                nextTriggerByLevel.put(level, computeNextTriggerTicks(level, now));
            }
        }
    }

    private static long computeNextTriggerTicks(ServerLevel level, long now) {
        int dmin = ModConfig.getBossRandomDaysMin();
        int dmax = ModConfig.getBossRandomDaysMax();
        int days = dmin + RNG.nextInt(Math.max(1, (dmax - dmin + 1)));
        return now + (days * 24000L);
    }
}
