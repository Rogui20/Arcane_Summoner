package com.example.arcane_summoner.network;

import com.example.arcane_summoner.content.block.entity.MagicWandAltarBlockEntity;
import com.example.arcane_summoner.summon.SummonHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InvokeAltarC2SPacket {
    private final BlockPos pos;

    public InvokeAltarC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    // -------- Serialization --------
    public static void encode(InvokeAltarC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static InvokeAltarC2SPacket decode(FriendlyByteBuf buf) {
        return new InvokeAltarC2SPacket(buf.readBlockPos());
    }

    // -------- Handling --------
    public static void handle(InvokeAltarC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();
            if (!(level.getBlockEntity(msg.pos) instanceof MagicWandAltarBlockEntity altar)) return;

            // 🔹 Agora o SummonHelper cuida de:
            //  - Validar se os itens são compatíveis com o slot
            //  - Respeitar stack_limit.json
            //  - Consumir somente até o limite
            //  - Aplicar atributos e spawnar o mob
            SummonHelper.spawnFromAltar(level, msg.pos, altar);
        });
        ctx.get().setPacketHandled(true);
    }
}
