package com.example.arcane_summoner.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.EnumSet;

public class TeleportToTargetGoal extends Goal {
    private final Mob mob;
    private final int cooldownTicks;
    private int tickCounter = 0;
    private int tries = 0;

    public TeleportToTargetGoal(Mob mob, int cooldownTicks) {
        this.mob = mob;
        this.cooldownTicks = cooldownTicks;
        this.tickCounter = mob.getRandom().nextInt(cooldownTicks);
        this.setFlags(EnumSet.noneOf(Goal.Flag.class)); // não rouba MOVE/LOOK
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true; // garante que o tick rode sempre
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel level))
            return;

        LivingEntity target = mob.getTarget();
        if (target == null)
            return;

        if (++tickCounter >= cooldownTicks) {

            double tx = target.getX() + (level.random.nextDouble() - 0.5) * 10;
            double ty = target.getY();
            double tz = target.getZ() + (level.random.nextDouble() - 0.5) * 10;

            BlockPos newPos = new BlockPos((int) tx, (int) ty, (int) tz);
            if (level.getBlockState(newPos).isAir() && level.getBlockState(newPos.above()).isAir()) {
                // partículas + som
                level.sendParticles(ParticleTypes.PORTAL,
                        mob.getX(), mob.getY() + mob.getBbHeight() / 2.0, mob.getZ(),
                        32, 0.5, 0.5, 0.5, 0.2);
                level.playSound(null, mob.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                        SoundSource.HOSTILE, 1.0F, 1.0F);

                // teleporta
                mob.teleportTo(tx, ty, tz);

                level.sendParticles(ParticleTypes.PORTAL,
                        tx, ty + mob.getBbHeight() / 2.0, tz,
                        32, 0.5, 0.5, 0.5, 0.2);
                level.playSound(null, newPos, SoundEvents.ENDERMAN_TELEPORT,
                        SoundSource.HOSTILE, 1.0F, 1.0F);

                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, true, false));
                tries = 0;
                tickCounter = 0;
            } else {
                tries++;
                if (tries > 3) {
                    tries = 0;
                    tickCounter = 0; // só depois de várias falhas reseta o cooldown
                }
            }
        }
    }
}
