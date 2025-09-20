package com.example.arcane_summoner.procedural_difficulty;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DifficultyCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("difficulty")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            ServerLevel level = player.serverLevel();

                            ProceduralDifficultyData data = ProceduralDifficultyData.get(level.getServer());
                            double mult = data.getMultiplier();

                            Component msg;
                            if (mult > 50.0) {
                                msg = Component.literal(
                                        "§4☠ O apocalipse chegou! O poder dos monstros está em "
                                                + String.format("%.2f", mult) + "x! CORRAM!"
                                );
                            } else if (mult > 7.0) {
                                msg = Component.literal(
                                        "§c⚠ O poder dos monstros tá acima de " + String.format("%.2f", mult) + "x, fiquem espertos!"
                                );
                            } else {
                                msg = Component.literal(
                                        "§eO poder dos monstros está sendo multiplicado por "
                                                + String.format("%.2f", mult) + " agora."
                                );
                            }

                            for (ServerPlayer sp : level.players()) {
                                sp.sendSystemMessage(msg);
                            }

                            return 1;
                        })
        );
    }
}
