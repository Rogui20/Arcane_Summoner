package com.example.arcane_summoner.summon;

import java.util.List;

import com.example.arcane_summoner.config.ModConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

public class SummonRulesHelper {
    public static boolean canSummonHere(ServerLevel level, BlockPos altarPos) {
        int max = ModConfig.getSummonMaxMobs();
        int radius = ModConfig.getSummonCheckRadius();

        AABB box = new AABB(
                altarPos.getX() - radius, altarPos.getY() - radius, altarPos.getZ() - radius,
                altarPos.getX() + radius, altarPos.getY() + radius, altarPos.getZ() + radius);

        List<Mob> summoned = level.getEntitiesOfClass(Mob.class, box,
                mob -> PersistentHelper.isSummonedMob(mob));

        return summoned.size() < max;
    }

}
