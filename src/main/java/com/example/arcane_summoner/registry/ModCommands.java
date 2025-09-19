package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.summon.BossSpawner;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("arcane_summoner")
                .requires(src -> src.hasPermission(2)) // OP only (ajusta se quiser)
                .then(Commands.literal("spawnboss")
                    .then(Commands.argument("multiplier", DoubleArgumentType.doubleArg(0.5, 1500.0))
                        .executes(ctx -> {
                            var src = ctx.getSource();
                            var player = src.getPlayerOrException();
                            double mult = DoubleArgumentType.getDouble(ctx, "multiplier");

                            boolean ok = BossSpawner.spawnBossFor(player, mult);
                            if (ok) {
                                src.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                                        "[ArcaneSummoner] Boss spawnado com mult=" + mult), true);
                                return 1;
                            } else {
                                src.sendFailure(net.minecraft.network.chat.Component.literal(
                                        "[ArcaneSummoner] Falha ao spawnar boss (posições inválidas?)."));
                                return 0;
                            }
                        })
                    )
                )
        );
    }
}
