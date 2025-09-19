package com.example.arcane_summoner.client.screen;

import com.example.arcane_summoner.content.block.menu.MagicWandAltarMenu;
import com.example.arcane_summoner.network.InvokeAltarC2SPacket;
import com.example.arcane_summoner.registry.ModNetworking;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MagicWandAltarScreen extends AbstractContainerScreen<MagicWandAltarMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("arcane_summoner",
            "textures/gui/magic_wand_altar.png");

    public MagicWandAltarScreen(MagicWandAltarMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176; // largura padrão
        this.imageHeight = 166; // altura padrão
    }

    @Override
    protected void init() {
        super.init();

        // Centralizar botão "Summon" abaixo dos slots do altar
        int centerX = this.leftPos + this.imageWidth / 2;
        int buttonX = centerX - 40; // 80 px de largura → centralizado
        int buttonY = this.topPos + 58; // fica entre altar e inventário

        this.addRenderableWidget(
                Button.builder(Component.literal("Summon"), btn -> {
                    var be = this.menu.getAltar();
                    if (be != null) {
                        ModNetworking.CHANNEL.sendToServer(new InvokeAltarC2SPacket(be.getBlockPos()));
                    }
                }).pos(buttonX, buttonY).size(80, 20).build());

    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        gfx.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTicks);
        this.renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, 8, 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 8, 72, 4210752, false);
    }
}
