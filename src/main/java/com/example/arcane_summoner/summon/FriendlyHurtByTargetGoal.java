package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;

public class FriendlyHurtByTargetGoal extends HurtByTargetGoal {
    private final PathfinderMob mob;

    public FriendlyHurtByTargetGoal(PathfinderMob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) {
            return false;
        }

        LivingEntity attacker = mob.getLastHurtByMob();
        if (attacker == null) return false;

        // Ignora Players
        if (attacker instanceof Player) {
            return false;
        }

        // Ignora outros Friendly (mesmo nome/config)
        if (attacker.getCustomName() != null) {
            String attackerName = attacker.getCustomName().getString();
            String friendlyName = ModConfig.getBehaviorName("friendly").getString();
            if (attackerName.equals(friendlyName)) {
                return false;
            }
        }

        return true; // caso contrário, pode revidar
    }
}
