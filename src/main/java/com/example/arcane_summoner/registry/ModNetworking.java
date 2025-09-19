package com.example.arcane_summoner.registry;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.network.InvokeAltarC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ArcaneSummoner.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++,
                InvokeAltarC2SPacket.class,
                InvokeAltarC2SPacket::encode,
                InvokeAltarC2SPacket::decode,
                InvokeAltarC2SPacket::handle);
    }
}

