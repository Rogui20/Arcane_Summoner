package com.example.arcane_summoner.summon;

import java.util.EnumSet;
import java.util.jar.Attributes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;

public class ShootArrowGoal extends Goal {
    private final Mob mob;
    private final int cooldownTicks;
    private int tickCounter;

    public ShootArrowGoal(Mob mob, int cooldownTicks) {
        this.mob = mob;
        this.cooldownTicks = cooldownTicks;
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
        if (!(mob.level() instanceof ServerLevel level)) return;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return;

        tickCounter++;
        if (tickCounter < cooldownTicks) return;

        tickCounter = 0;

        // Direção até o alvo
        double dx = target.getX() - mob.getX();
        double dy = target.getY(0.3333D) - mob.getEyeY(); // mira ~ no peito
        double dz = target.getZ() - mob.getZ();

        double dist = Math.sqrt(dx * dx + dz * dz);

        // Cria a flecha
        AbstractArrow arrow = new Arrow(level, mob);
        arrow.setPos(mob.getX(), mob.getEyeY() - 0.1, mob.getZ());
        arrow.shoot(dx, dy + dist * 0.2, dz, 1.6F, 14 - level.getDifficulty().getId() * 4);

        // Opcional: dano base extra
        //if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
        //    arrow.setBaseDamage(mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        //}

        level.addFreshEntity(arrow);

        mob.playSound(SoundEvents.SKELETON_SHOOT, 1.0F,
                1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
