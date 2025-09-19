package com.example.arcane_summoner.summon;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

public class WeaponGoalHelper {

    /** Remove todos os goals de melee existentes (para evitar conflitos com o de arco). */
    public static void removeMeleeGoals(PathfinderMob pf) {
        pf.goalSelector.removeAllGoals(g -> g instanceof MeleeAttackGoal);
    }

    /**
     * Se o mob tiver arco na main hand e suportar ataques à distância (RangedAttackMob),
     * adiciona o goal de arco. Caso contrário, garante o melee.
     */
    public static void applyWeaponGoals(Mob mob, double speed, int bowAttackIntervalTicks, float bowRange) {
        if (!(mob instanceof PathfinderMob pf)) return;

        ItemStack main = mob.getMainHandItem();
        boolean hasBowEquipped = main.getItem() instanceof BowItem;

        if (hasBowEquipped && mob instanceof RangedAttackMob) {
            // Evita ficar com melee + ranged ao mesmo tempo na mesma prioridade
            removeMeleeGoals(pf);

            // ⭐ Truque: usar cast de interseção para satisfazer T extends Mob & RangedAttackMob
            @SuppressWarnings("unchecked")
            RangedBowAttackGoal<?> bowGoal = new RangedBowAttackGoal<>(
                    (Mob & RangedAttackMob) mob,
                    speed,
                    bowAttackIntervalTicks,
                    bowRange
            );
            pf.goalSelector.addGoal(2, bowGoal);

        } else {
            // Fallback: melee padrão
            pf.goalSelector.addGoal(2, new MeleeAttackGoal(pf, speed, false));
        }
    }
}
