package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.ArcaneSummoner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID)
public class SummonEvents {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob))
            return;
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        if (PersistentHelper.isSummonedMob(mob)) {
            PersistentHelper.loadSummonData(mob, level);
        }

        if (mob.getPersistentData().getBoolean("ArcaneSummonerBoss")) {
            BossSpawner.loadBossData(mob, level);
        }
    }
}
