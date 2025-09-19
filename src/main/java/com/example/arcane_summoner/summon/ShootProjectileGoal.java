package com.example.arcane_summoner.summon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.WitherSkull;

import java.util.EnumSet;

public class ShootProjectileGoal extends Goal {
    private final Mob mob;
    private final int cooldownTicks;
    private final boolean wither; // true = WitherSkull, false = Fireball
    private int tickCounter;

    public ShootProjectileGoal(Mob mob, int cooldownTicks, boolean wither) {
        this.mob = mob;
        this.cooldownTicks = cooldownTicks;
        this.wither = wither;
        this.tickCounter = mob.getRandom().nextInt(cooldownTicks);
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && mob.hasLineOfSight(target);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel level))
            return;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive())
            return;

        tickCounter++;
        if (tickCounter < cooldownTicks)
            return;

        double distSq = mob.distanceToSqr(target);
        if (distSq < 25)
            return; // muito perto (< 5 blocos)

        if (!mob.hasLineOfSight(target))
            return;

        tickCounter = 0;

        double dx = target.getX() - mob.getX();
        double dy = target.getY(0.5) - mob.getEyeY();
        double dz = target.getZ() - mob.getZ();

        // normalizar direção
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-6)
            return;
        dx /= len;
        dy /= len;
        dz /= len;

        // 🔹 spread aleatório (como Ghast faz: ±0.1)
        dx += level.random.triangle(0.0, 0.1);
        dy += level.random.triangle(0.0, 0.1);
        dz += level.random.triangle(0.0, 0.1);

        if (wither) {
            // Wither Skull (mesmo dano padrão do Wither)
            WitherSkull skull = new WitherSkull(level, mob, dx, dy, dz);
            skull.setPos(mob.getX(), mob.getEyeY(), mob.getZ());
            skull.setDeltaMovement(dx * 0.6, dy * 0.6, dz * 0.6);

            level.addFreshEntity(skull);
            level.playSound(null, mob.blockPosition(),
                    SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F);
        } else {
            // Fireball estilo Ghast (explosionPower = 1 → mesmo dano do Ghast)
            LargeFireball fireball = new LargeFireball(level, mob, dx, dy, dz, 1);
            fireball.setPos(mob.getX(), mob.getEyeY(), mob.getZ());
            fireball.setDeltaMovement(dx * 0.8, dy * 0.8, dz * 0.8);

            level.addFreshEntity(fireball);
            level.playSound(null, mob.blockPosition(),
                    SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

}
