package com.example.arcane_summoner.procedural_difficulty;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class ProceduralDifficultyAnnouncer {

    private ProceduralDifficultyAnnouncer() {}

    /** Chame isto sempre que o multiplicador AUMENTAR. */
    public static void onMultiplierIncreased(ServerLevel level, double oldVal, double newVal) {
        if (newVal <= oldVal) return;

        Double crossed = highestMilestoneCrossed(oldVal, newVal);
        if (crossed == null) return; // não cruzou nenhum marco

        broadcast(level, newVal, crossed >= 50.0);
    }

    /** Retorna o MAIOR marco cruzado entre oldVal (exclusivo) e newVal (inclusivo). */
    private static Double highestMilestoneCrossed(double oldVal, double newVal) {
        double cur = nextMilestoneStrict(oldVal);
        Double last = null;

        while (cur <= newVal) {
            last = cur;
            cur = nextMilestoneStrict(cur);
        }
        return last;
    }

    /** Próximo marco ESTRITAMENTE maior que x, conforme regras. */
    private static double nextMilestoneStrict(double x) {
        if (x < 5.0)   return 5.0;
        if (x < 10.0)  return 10.0;
        if (x < 50.0)  return (Math.floor(x / 10.0) + 1) * 10.0;
        // x >= 50.0 → 70, 90, 110...
        return 50.0 + (Math.floor((x - 50.0) / 20.0) + 1) * 20.0;
    }

    private static void broadcast(ServerLevel level, double currentMult, boolean thunder) {
        String multStr = String.format("%.2f", currentMult);

        Component msg;
        if (currentMult > 50.0) {
            msg = Component.literal("§4☠ O apocalipse chegou! O poder dos monstros está em "
                    + multStr + "x! CORRAM!");
        } else if (currentMult > 7.0) {
            msg = Component.literal("§c⚠ O poder dos monstros tá acima de " + multStr
                    + "x, fiquem espertos!");
        } else {
            msg = Component.literal("§eO poder dos monstros está sendo multiplicado por "
                    + multStr + " agora.");
        }

        for (ServerPlayer sp : level.players()) {
            sp.sendSystemMessage(msg);
            if (thunder) {
                // trovão local ao jogador pra reforçar o drama
                level.playSound(null, sp.blockPosition(),
                        SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER,
                        2.0F, 0.8F + level.random.nextFloat() * 0.4F);
            }
        }
    }
}
