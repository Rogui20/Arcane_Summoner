package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.config.ModConfig;
import com.example.arcane_summoner.summon.WeaponGoalHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class MobBehaviorHelper {

    /**
     * Aplica comportamento (friendly, hostile, neutral) com subcomportamentos
     * opcionais
     * e define o nome/colorização a partir da config behavior_names.json.
     *
     * @param mob          O mob invocado
     * @param behaviorBase "friendly", "hostile" ou "neutral"
     * @param subBehavior  Ex.: "follow" (apenas para friendly)
     * @param level        Mundo
     */
    public static void applyBehavior(Mob mob, String behaviorBase, String subBehavior, ServerLevel level) {
        if (!(mob instanceof PathfinderMob pathfinderMob)) {
            System.out.println("[ArcaneSummoner] Mob não é PathfinderMob, comportamento não aplicado.");
            return;
        }

        // Limpa os goals existentes
        pathfinderMob.goalSelector.removeAllGoals(goal -> true);
        pathfinderMob.targetSelector.removeAllGoals(goal -> true);

        switch (behaviorBase.toLowerCase()) {
            case "friendly" -> {
                // Ataca apenas monstros, exceto outros Friendly
                pathfinderMob.targetSelector.addGoal(1,
                        new NearestAttackableTargetGoal<>(pathfinderMob, LivingEntity.class, 10, true, false,
                                target -> {
                                    if (target instanceof Monster)
                                        return true; // vanilla hostile
                                    if (BehaviorUtil.isHostile(target))
                                        return true; // custom hostile
                                    if (BehaviorUtil.isNeutral(target))
                                        return true; // opcional, depende da regra
                                    return false;
                                }));

                pathfinderMob.targetSelector.addGoal(2, new FriendlyHurtByTargetGoal(pathfinderMob));

                pathfinderMob.goalSelector.addGoal(1, new FloatGoal(pathfinderMob));
                pathfinderMob.goalSelector.addGoal(2, new MeleeAttackGoal(pathfinderMob, 1.2, false));

                // WeaponGoalHelper.applyWeaponGoals(mob, 1.2, 20, 90f);
                pathfinderMob.goalSelector.addGoal(5, new RandomStrollGoal(pathfinderMob, 1.0)); // andar aleatório
                if (pathfinderMob.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                    pathfinderMob.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32.0D); // aumenta pra 32 blocos
                }

                // Subcomportamento opcional
                if ("follow".equalsIgnoreCase(subBehavior)) {
                    pathfinderMob.goalSelector.addGoal(3, new FollowPlayerGoal(pathfinderMob, 1.2D, 8.0f, 24.0f));
                }

                mob.setCustomName(ModConfig.getBehaviorName("friendly"));
                mob.setCustomNameVisible(true);
            }
            case "hostile" -> {
                pathfinderMob.targetSelector.addGoal(1,
                        new NearestAttackableTargetGoal<>(pathfinderMob, LivingEntity.class, 10, true, false,
                                target -> {
                                    if (target instanceof Player)
                                        return true;
                                    if (BehaviorUtil.isFriendly(target))
                                        return true;
                                    if (BehaviorUtil.isNeutral(target))
                                        return true;
                                    return false;
                                }));
                pathfinderMob.targetSelector.addGoal(2, new HurtByTargetGoal(pathfinderMob));

                pathfinderMob.goalSelector.addGoal(1, new FloatGoal(pathfinderMob));
                pathfinderMob.goalSelector.addGoal(2, new MeleeAttackGoal(pathfinderMob, 1.2, false));
                // WeaponGoalHelper.applyWeaponGoals(mob, 1.2, 20, 90f);

                pathfinderMob.goalSelector.addGoal(5, new RandomStrollGoal(pathfinderMob, 1.0)); // andar aleatório

                mob.setCustomName(ModConfig.getBehaviorName("hostile"));
                mob.setCustomNameVisible(true);
            }
            case "neutral" -> {
                pathfinderMob.goalSelector.addGoal(1, new FloatGoal(pathfinderMob));
                pathfinderMob.goalSelector.addGoal(2, new LookAtPlayerGoal(pathfinderMob, Player.class, 8.0f));
                pathfinderMob.goalSelector.addGoal(3, new RandomStrollGoal(pathfinderMob, 1.0)); // andar aleatório
                pathfinderMob.goalSelector.addGoal(4, new MeleeAttackGoal(pathfinderMob, 1.0, false)); // ataque básico

                // WeaponGoalHelper.applyWeaponGoals(mob, 1.2, 20, 90f);
                pathfinderMob.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinderMob));

                mob.setCustomName(ModConfig.getBehaviorName("neutral"));
                mob.setCustomNameVisible(true);
            }
            default -> {
                pathfinderMob.goalSelector.addGoal(1, new FloatGoal(pathfinderMob));
                pathfinderMob.goalSelector.addGoal(2, new LookAtPlayerGoal(pathfinderMob, Player.class, 8.0f));
                pathfinderMob.goalSelector.addGoal(3, new RandomStrollGoal(pathfinderMob, 1.0)); // andar aleatório
                pathfinderMob.goalSelector.addGoal(4, new MeleeAttackGoal(pathfinderMob, 1.0, false)); // ataque básico

                // WeaponGoalHelper.applyWeaponGoals(mob, 1.2, 20, 90f);
                pathfinderMob.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinderMob));

                mob.setCustomName(ModConfig.getBehaviorName("neutral"));
                mob.setCustomNameVisible(true);
            }
        }

    }
}
