package com.example.arcane_summoner.procedural_difficulty;

import com.example.arcane_summoner.config.ModConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class ProceduralDifficultyData extends SavedData {
    private double multiplier = ModConfig.getStartingMultiplier();

    public static ProceduralDifficultyData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
                ProceduralDifficultyData::load, ProceduralDifficultyData::new, "arcane_procedural_difficulty"
        );
    }

    public ProceduralDifficultyData() {}

    public static ProceduralDifficultyData load(CompoundTag tag) {
        ProceduralDifficultyData data = new ProceduralDifficultyData();
        data.multiplier = tag.getDouble("Multiplier");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putDouble("Multiplier", multiplier);
        return tag;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double value) {
        this.multiplier = Math.max(1.0, Math.min(value, ModConfig.getMaxMultiplier())); // clamp
        this.setDirty();
    }

    public void add(double delta) {
        setMultiplier(multiplier + delta);
    }

    public void subtract(double delta) {
        setMultiplier(multiplier - delta);
    }
}
