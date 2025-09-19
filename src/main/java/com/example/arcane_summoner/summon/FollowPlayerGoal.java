package com.example.arcane_summoner.summon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class FollowPlayerGoal extends Goal {
    private final PathfinderMob mob;
    private ServerPlayer targetPlayer;
    private final double speed;
    private final float minDist;
    private final float maxDist;

    public FollowPlayerGoal(PathfinderMob mob, double speed, float minDist, float maxDist) {
        this.mob = mob;
        this.speed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // 🔹 se já tem alvo em combate, não segue player
        if (mob.getTarget() != null && mob.getTarget().isAlive()) {
            return false;
        }

        if (!(mob.level() instanceof ServerLevel level))
            return false;

        List<ServerPlayer> players = level.players();
        if (players.isEmpty())
            return false;

        // escolhe o player mais próximo
        targetPlayer = players.stream()
                .filter(p -> !p.isSpectator() && p.isAlive())
                .min((a, b) -> Double.compare(mob.distanceToSqr(a), mob.distanceToSqr(b)))
                .orElse(null);

        if (targetPlayer == null)
            return false;

        double dist = mob.distanceToSqr(targetPlayer);
        return dist >= (minDist * minDist) && dist <= (maxDist * maxDist);
    }

    @Override
    public boolean canContinueToUse() {
        // 🔹 interrompe o follow se aparecer um alvo
        if (mob.getTarget() != null && mob.getTarget().isAlive()) {
            return false;
        }

        if (targetPlayer == null || !targetPlayer.isAlive())
            return false;
        double dist = mob.distanceToSqr(targetPlayer);
        return dist >= (minDist * minDist) && dist <= (maxDist * maxDist);
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            mob.getNavigation().moveTo(targetPlayer, speed);
            mob.getLookControl().setLookAt(targetPlayer, 30.0F, 30.0F);
        }
    }
}
