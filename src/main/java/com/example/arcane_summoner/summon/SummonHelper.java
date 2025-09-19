package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.api.ISkinnable;
import com.example.arcane_summoner.config.ModConfig;
import com.example.arcane_summoner.content.block.entity.MagicWandAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;

public class SummonHelper {

    public static void spawnFromAltar(ServerLevel level, BlockPos pos, MagicWandAltarBlockEntity altar) {
        // 1) Ler comportamento a partir do LIVRO (slot 9)
        // Adicionar return caso tiver 10 (padrão) mobs sumonados num radius quadrado de
        // 50 (padrão) do altar
        if (!SummonRulesHelper.canSummonHere(level, pos)) {
            System.out.println("[ArcaneSummoner] Limite de summons atingido nesse altar.");
            return;
        }

        String behavior = "neutral";
        ItemStack bookStack = altar.getItem(9);
        if (!bookStack.isEmpty() && (bookStack.getItem() instanceof WrittenBookItem
                || bookStack.getItem() instanceof WritableBookItem)) {
            behavior = BookBehaviorHelper.getBehaviorFromBook(bookStack);
        }

        // 2) Resolver entidade a partir do behavior_mobs.json
        String mobId = ModConfig.getBehaviorMobId(behavior); // ex: "minecraft:wither_skeleton"
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(mobId));
        if (type == null) {
            System.out.println("[ArcaneSummoner] EntityType inválido para behavior=" + behavior + " id=" + mobId
                    + " — fallback Zombie.");
            type = EntityType.WITHER_SKELETON;
        }

        Entity ent = type.create(level);
        if (!(ent instanceof Mob mob)) {
            System.out.println("[ArcaneSummoner] Entity não é Mob, abortando spawn.");
            return;
        }

        mob.moveTo(
                pos.getX() + 0.5,
                pos.getY() + 2,
                pos.getZ() + 0.5,
                level.random.nextFloat() * 360F,
                0);

        // 3) Atributos provenientes dos slots 0–2 (cálculo sem consumir ainda)
        double extraArmor = 0;
        double extraHealth = 0;
        double extraStrength = 0;

        // Slot 0 → resistência (armadura) por minérios
        {
            ItemStack cur = altar.getItem(0);
            if (!cur.isEmpty() && ItemConsumptionHelper.isAllowedInSlot(0, cur.getItem())) {
                int willConsume = ItemConsumptionHelper.getConsumableAmount(cur);
                extraArmor += ModConfig.getArmorValue(cur.getItem()) * willConsume;
            }
        }

        // Slot 1 → força (dano) por minérios
        {
            ItemStack cur = altar.getItem(1);
            if (!cur.isEmpty() && ItemConsumptionHelper.isAllowedInSlot(1, cur.getItem())) {
                int willConsume = ItemConsumptionHelper.getConsumableAmount(cur);
                extraStrength += ModConfig.getArmorValue(cur.getItem()) * willConsume;
            }
        }

        // Slot 2 → vida extra (comida)
        {
            ItemStack cur = altar.getItem(2);
            if (!cur.isEmpty() && ItemConsumptionHelper.isAllowedInSlot(2, cur.getItem())) {
                int willConsume = ItemConsumptionHelper.getConsumableAmount(cur);
                extraHealth += ModConfig.getFoodValue(cur.getItem()) * willConsume;
            }
        }

        // 4) Equipamentos (não consome; cópia visual no mob)
        ItemStack helm = altar.getItem(3);
        ItemStack chest = altar.getItem(4);
        ItemStack legs = altar.getItem(5);
        ItemStack boots = altar.getItem(6);

        if (!helm.isEmpty() && helm.getItem() instanceof ArmorItem)
            mob.setItemSlot(EquipmentSlot.HEAD, helm.copy());
        if (!chest.isEmpty() && chest.getItem() instanceof ArmorItem)
            mob.setItemSlot(EquipmentSlot.CHEST, chest.copy());
        if (!legs.isEmpty() && legs.getItem() instanceof ArmorItem)
            mob.setItemSlot(EquipmentSlot.LEGS, legs.copy());
        if (!boots.isEmpty() && boots.getItem() instanceof ArmorItem)
            mob.setItemSlot(EquipmentSlot.FEET, boots.copy());

        // Slots 7–8 → armas
        ItemStack mainWeapon = altar.getItem(7);
        ItemStack offWeapon = altar.getItem(8);
        if (!mainWeapon.isEmpty()
                && (mainWeapon.getItem() instanceof SwordItem || mainWeapon.getItem() instanceof BowItem)) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, mainWeapon.copy());
        }
        if (!offWeapon.isEmpty()
                && (offWeapon.getItem() instanceof SwordItem || offWeapon.getItem() instanceof BowItem)) {
            mob.setItemSlot(EquipmentSlot.OFFHAND, offWeapon.copy());
        }

        // 5) Elytra pack (slot 10)
        ItemStack elytra = altar.getItem(10);

        // Slot 11 → Poder especial
        ItemStack power = altar.getItem(11);

        // 7) Poções (slots 12–13)
        for (int slot = 12; slot <= 13; slot++) {
            ItemStack potion = altar.getItem(slot);
            if (!potion.isEmpty() && potion.getItem() instanceof PotionItem) {
                for (MobEffectInstance eff : PotionUtils.getMobEffects(potion)) {
                    if (eff != null) {
                        mob.addEffect(new MobEffectInstance(
                                eff.getEffect(),
                                999999,
                                Math.max(0, eff.getAmplifier()),
                                true,
                                true));
                    }
                }
            }
        }

        // 9) Aplicar atributos finais
        if (extraArmor > 0 && mob.getAttribute(Attributes.ARMOR) != null) {
            mob.getAttribute(Attributes.ARMOR).setBaseValue(
                    mob.getAttribute(Attributes.ARMOR).getBaseValue() + extraArmor);
        }
        if (extraHealth > 0 && mob.getAttribute(Attributes.MAX_HEALTH) != null) {
            mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                    mob.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + extraHealth);
            mob.setHealth(mob.getMaxHealth());
        }
        if (extraStrength > 0 && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                    mob.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + extraStrength);
        }

        // 10) Comportamento
        String[] parts = behavior.split(":");
        String behaviorBase = parts[0];
        String subBehavior = parts.length > 1 ? parts[1] : "";
        MobBehaviorHelper.applyBehavior(mob, behaviorBase, subBehavior, level);

        // Salvar dados persistentes no mob
        PersistentHelper.saveSummonData(
                mob,
                behaviorBase,
                subBehavior,
                !elytra.isEmpty() && elytra.getItem() == Items.ELYTRA,
                power.isEmpty() ? ""
                        : (power.getItem() == Items.FIRE_CHARGE ? "fireball"
                                : power.getItem() == Items.NETHER_STAR ? "wither" : ""),
                extraArmor,
                extraHealth,
                extraStrength);

        // 11) Spawn
        if (!elytra.isEmpty() && elytra.getItem() == Items.ELYTRA) {
            mob.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.JUMP, 999999, 1, true, false));
            mob.addEffect(
                    new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOW_FALLING, 999999, 0, true, false));
            mob.goalSelector.addGoal(3, new TeleportToTargetGoal(mob, 100)); // 5 segundos de cooldown

        }
        if (!power.isEmpty()) {
            if (power.getItem() == Items.FIRE_CHARGE) {
                mob.goalSelector.addGoal(4, new ShootProjectileGoal(mob, 60, false));
            } else if (power.getItem() == Items.NETHER_STAR) {
                mob.goalSelector.addGoal(4, new ShootProjectileGoal(mob, 100, true));
            }
        }

        // 8) Consumir todos os itens do altar, exceto o livro
        for (int slot = 0; slot < altar.getContainerSize(); slot++) {
            if (slot == 9)
                continue; // preserva o livro

            if (slot == 11) {
                // Poder especial (Ghast/Wither) → consome só 1
                ItemConsumptionHelper.consumeItems(altar, 11, 1);
            } else {
                // Demais → consome baseado no stack_limit.json
                ItemConsumptionHelper.consumeItems(altar, slot);
            }
        }

        //if (mob instanceof ISkinnable skinnable) {
        //    String skin = ModConfig.getRandomSkin(mobId, behaviorBase, level.random);
        //    if (skin != null) {
        //        skinnable.setSkin(new ResourceLocation(skin));
        //        System.out.println("Skin aplicada.");
        //    } else {
        //        System.out.println("Skin não encontrada.");
        //    }
        //}

        mob.setPersistenceRequired();
        level.addFreshEntity(mob);

        // 12) Debug chat
        for (ServerPlayer nearby : level.players()) {
            if (nearby.distanceTo(mob) < 16) {
                nearby.sendSystemMessage(Component.literal(
                        "[ArcaneSummoner] Invocado " + mobId + " (" + behavior + ")" +
                                " | +" + extraArmor + " ARM, +" + extraHealth + " HP, +" + extraStrength + " DMG"));
            }
        }

        // 13) Debug log
        System.out.println("[ArcaneSummoner] Spawned " + mobId + " as " + behavior +
                " at " + pos + " | ARM=" + extraArmor + " HP=" + extraHealth + " DMG=" + extraStrength);
    }
}
