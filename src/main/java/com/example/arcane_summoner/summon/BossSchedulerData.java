package com.example.arcane_summoner.summon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class BossSchedulerData extends SavedData {
    private long nextTriggerTicks = -1;

    public static BossSchedulerData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                BossSchedulerData::load, BossSchedulerData::new, "arcane_boss_scheduler"
        );
    }

    public BossSchedulerData() {}

    public static BossSchedulerData load(CompoundTag tag) {
        BossSchedulerData data = new BossSchedulerData();
        data.nextTriggerTicks = tag.getLong("NextTrigger");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("NextTrigger", nextTriggerTicks);
        return tag;
    }

    public long getNextTrigger() {
        return nextTriggerTicks;
    }

    public void setNextTrigger(long ticks) {
        this.nextTriggerTicks = ticks;
        this.setDirty(); // marca como alterado → salva no disco
    }
}
