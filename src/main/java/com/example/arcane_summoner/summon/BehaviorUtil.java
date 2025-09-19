package com.example.arcane_summoner.summon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class BehaviorUtil {
    public static String getBehaviorBase(LivingEntity e) {
        if (!(e instanceof Mob mob)) return "";
        return mob.getPersistentData().getString("ArcaneSummonerBehavior");
    }

    public static boolean isFriendly(LivingEntity e) {
        return "friendly".equalsIgnoreCase(getBehaviorBase(e));
    }

    public static boolean isHostile(LivingEntity e) {
        return "hostile".equalsIgnoreCase(getBehaviorBase(e));
    }

    public static boolean isNeutral(LivingEntity e) {
        return "neutral".equalsIgnoreCase(getBehaviorBase(e));
    }
}
