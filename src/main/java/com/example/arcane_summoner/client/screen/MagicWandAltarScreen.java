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
import net.minecraft.world.inventory.Slot;

public class MagicWandAltarScreen extends AbstractContainerScreen<MagicWandAltarMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("arcane_summoner",
            "textures/gui/magic_wand_altar.png");

    private static final ResourceLocation ICON_HELM = new ResourceLocation("minecraft",
            "textures/item/empty_armor_slot_helmet.png");
    private static final ResourceLocation ICON_CHEST = new ResourceLocation("minecraft",
            "textures/item/empty_armor_slot_chestplate.png");
    private static final ResourceLocation ICON_LEGS = new ResourceLocation("minecraft",
            "textures/item/empty_armor_slot_leggings.png");
    private static final ResourceLocation ICON_BOOTS = new ResourceLocation("minecraft",
            "textures/item/empty_armor_slot_boots.png");
    private static final ResourceLocation ICON_SWORD = new ResourceLocation("minecraft",
            "textures/item/empty_slot_sword.png");
    private static final ResourceLocation ICON_INGOT = new ResourceLocation("minecraft",
            "textures/item/empty_slot_ingot.png");
    private static final ResourceLocation ICON_POTION = new ResourceLocation("minecraft",
            "textures/item/potion.png");
    private static final ResourceLocation ICON_BOOK = new ResourceLocation("arcane_summoner",
            "textures/item/empty_book_slot.png");
    private static final ResourceLocation ICON_BEEF = new ResourceLocation("arcane_summoner",
            "textures/item/empty_cooked_beef_slot.png");
    private static final ResourceLocation ICON_ELYTRA = new ResourceLocation("arcane_summoner",
            "textures/item/empty_elytra_slot.png");
    private static final ResourceLocation ICON_NETHER_STAR = new ResourceLocation("arcane_summoner",
            "textures/item/empty_nether_star_slot.png");

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

        Slot oreIngot = this.menu.slots.get(0);
        if (!oreIngot.hasItem()) {
            gfx.blit(ICON_INGOT, this.leftPos + oreIngot.x, this.topPos + oreIngot.y, 0, 0, 16, 16, 16, 16);
        }

        Slot oreIngot2 = this.menu.slots.get(1);
        if (!oreIngot2.hasItem()) {
            gfx.blit(ICON_INGOT, this.leftPos + oreIngot2.x, this.topPos + oreIngot2.y, 0, 0, 16, 16, 16, 16);
        }
        Slot food = this.menu.slots.get(2);
        if (!food.hasItem()) {
            gfx.blit(ICON_BEEF, this.leftPos + food.x, this.topPos + food.y, 0, 0, 16, 16, 16, 16);
        }
        // Slot 3 = capacete
        Slot helm = this.menu.slots.get(3);
        if (!helm.hasItem()) {
            gfx.blit(ICON_HELM, this.leftPos + helm.x, this.topPos + helm.y, 0, 0, 16, 16, 16, 16);
        }

        // Slot 4 = peitoral
        Slot chest = this.menu.slots.get(4);
        if (!chest.hasItem()) {
            gfx.blit(ICON_CHEST, this.leftPos + chest.x, this.topPos + chest.y, 0, 0, 16, 16, 16, 16);
        }

        // Slot 5 = pernas
        Slot legs = this.menu.slots.get(5);
        if (!legs.hasItem()) {
            gfx.blit(ICON_LEGS, this.leftPos + legs.x, this.topPos + legs.y, 0, 0, 16, 16, 16, 16);
        }

        // Slot 6 = botas
        Slot boots = this.menu.slots.get(6);
        if (!boots.hasItem()) {
            gfx.blit(ICON_BOOTS, this.leftPos + boots.x, this.topPos + boots.y, 0, 0, 16, 16, 16, 16);
        }
        Slot swordSlot = this.menu.slots.get(7);
        if (!swordSlot.hasItem()) {
            gfx.blit(ICON_SWORD, this.leftPos + swordSlot.x, this.topPos + swordSlot.y, 0, 0, 16, 16, 16, 16);
        }
        Slot swordSlot2 = this.menu.slots.get(8);
        if (!swordSlot2.hasItem()) {
            gfx.blit(ICON_SWORD, this.leftPos + swordSlot2.x, this.topPos + swordSlot2.y, 0, 0, 16, 16, 16, 16);
        }
        Slot bookSlot = this.menu.slots.get(9);
        if (!bookSlot.hasItem()) {
            gfx.blit(ICON_BOOK, this.leftPos + bookSlot.x, this.topPos + bookSlot.y, 0, 0, 16, 16, 16, 16);
        }
        Slot elytraSlot = this.menu.slots.get(10);
        if (!elytraSlot.hasItem()) {
            gfx.blit(ICON_ELYTRA, this.leftPos + elytraSlot.x, this.topPos + elytraSlot.y, 0, 0, 16, 16, 16, 16);
        }
        Slot powerSlot = this.menu.slots.get(11);
        if (!powerSlot.hasItem()) {
            gfx.blit(ICON_NETHER_STAR, this.leftPos + powerSlot.x, this.topPos + powerSlot.y, 0, 0, 16, 16, 16, 16);
        }
        Slot potionSlot = this.menu.slots.get(12);
        if (!potionSlot.hasItem()) {
            gfx.blit(ICON_POTION, this.leftPos + potionSlot.x, this.topPos + potionSlot.y, 0, 0, 16, 16, 16, 16);
        }
        Slot potionSlot2 = this.menu.slots.get(13);
        if (!potionSlot2.hasItem()) {
            gfx.blit(ICON_POTION, this.leftPos + potionSlot2.x, this.topPos + potionSlot2.y, 0, 0, 16, 16, 16, 16);
        }
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
