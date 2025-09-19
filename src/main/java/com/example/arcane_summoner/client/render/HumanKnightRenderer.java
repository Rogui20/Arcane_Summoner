package com.example.arcane_summoner.client.render;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.content.entity.HumanKnight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;

public class HumanKnightRenderer extends HumanoidMobRenderer<HumanKnight, HumanoidModel<HumanKnight>> {
    private static final ResourceLocation DEFAULT = new ResourceLocation(
            "arcane_summoner:textures/entity/human_knight.png");

    public HumanKnightRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(HumanKnight entity) {
        return DEFAULT;
    }

}
