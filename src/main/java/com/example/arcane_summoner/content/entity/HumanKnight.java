package com.example.arcane_summoner.content.entity;

import com.example.arcane_summoner.api.ISkinnable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class HumanKnight extends Monster implements ISkinnable {

    private ResourceLocation skin;

    public HumanKnight(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    public void setSkin(ResourceLocation skin) {
        this.skin = skin;
    }

    @Override
    public ResourceLocation getSkin() {
        return skin; // pode ser null
    }

    /** Atributos base */
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, 2.0D);
    }
    
}
