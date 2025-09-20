package com.example.arcane_summoner.summon;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class BossCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bossnext")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            ServerLevel level = player.serverLevel();

                            BossSchedulerData data = BossSchedulerData.get(level);
                            long next = data.getNextTrigger();
                            long now = level.getDayTime();

                            if (next < 0) {
                                ctx.getSource().sendSuccess(() ->
                                        Component.literal("Nenhum boss agendado ainda."), false);
                                return 1;
                            }

                            long remaining = Math.max(0, next - now);

                            long days = remaining / 24000L;
                            long ticks = remaining % 24000L;

                            // mensagem visível para todos
                            Component msg = Component.literal(
                                    "§ePróximo boss em " + days + " dias e " + ticks + " ticks."
                            );

                            for (ServerPlayer sp : level.players()) {
                                sp.sendSystemMessage(msg);
                            }

                            return 1;
                        })
        );
    }
}
