package com.example.arcane_summoner.procedural_difficulty;

import com.example.arcane_summoner.config.ModConfig;
import com.example.arcane_summoner.ArcaneSummoner;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID)
public class ProceduralDifficultyEvents {

    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob))
            return;
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        if (!ModConfig.isProceduralEnabled())
            return;

        if (mob instanceof Monster || mob.getType().getCategory() == MobCategory.MONSTER) {
            // Se ainda não recebeu multiplicador
            if (!mob.getPersistentData().getBoolean("ArcaneProceduralApplied")) {
                ProceduralDifficultyData data = ProceduralDifficultyData.get(level.getServer());
                double oldMult = data.getMultiplier();

                // incrementa
                data.add(ModConfig.getMultiplierPerHostile());

                double newMult = data.getMultiplier();

                // dispara anúncio se cruzou marco
                ProceduralDifficultyAnnouncer.onMultiplierIncreased(level, oldMult, newMult);

                applyMultiplierRebased(mob, newMult);
                mob.getPersistentData().putBoolean("ArcaneProceduralApplied", true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        ServerLevel level = (ServerLevel) player.level();
        if (!ModConfig.isProceduralEnabled())
            return;

        ProceduralDifficultyData data = ProceduralDifficultyData.get(level.getServer());
        data.subtract(ModConfig.getMultiplierLostPerDeath());
    }

    private static void applyMultiplierRebased(Mob mob, double mult) {
        if (mult <= 1.0)
            return;

        CompoundTag data = mob.getPersistentData();

        // HP
        if (mob.getAttribute(Attributes.MAX_HEALTH) != null) {
            double base = data.contains("ProcBaseHP")
                    ? data.getDouble("ProcBaseHP")
                    : mob.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
            data.putDouble("ProcBaseHP", base);

            mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(base * mult);
            mob.setHealth(mob.getMaxHealth());
        }

        // Dano
        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            double base = data.contains("ProcBaseAD")
                    ? data.getDouble("ProcBaseAD")
                    : mob.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
            data.putDouble("ProcBaseAD", base);

            mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(base * mult);
        }

        // Armadura
        if (mob.getAttribute(Attributes.ARMOR) != null) {
            double base = data.contains("ProcBaseAR")
                    ? data.getDouble("ProcBaseAR")
                    : mob.getAttribute(Attributes.ARMOR).getBaseValue();
            data.putDouble("ProcBaseAR", base);

            mob.getAttribute(Attributes.ARMOR).setBaseValue(base * mult);
        }
    }

}
