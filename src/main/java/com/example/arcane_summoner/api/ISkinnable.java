package com.example.arcane_summoner.api;

import net.minecraft.resources.ResourceLocation;

public interface ISkinnable {
    void setSkin(ResourceLocation skin);
    ResourceLocation getSkin();
}
