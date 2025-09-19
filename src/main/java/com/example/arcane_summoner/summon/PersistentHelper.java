package com.example.arcane_summoner.summon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.ItemStack;

public class PersistentHelper {

    // 🔹 Salva dados importantes no NBT do mob
    public static void saveSummonData(
            Mob mob,
            String behaviorBase,
            String subBehavior,
            boolean hasElytraBuff,
            String powerType,
            double extraArmor,
            double extraHealth,
            double extraStrength) {
        CompoundTag data = mob.getPersistentData();
        data.putBoolean("ArcaneSummonerSummoned", true);

        // comportamento
        data.putString("ArcaneSummonerBehavior", behaviorBase);
        if (subBehavior != null && !subBehavior.isEmpty()) {
            data.putString("ArcaneSummonerSubBehavior", subBehavior);
        }

        // buffs especiais
        if (hasElytraBuff) {
            data.putBoolean("ArcaneSummonerElytraBuff", true);
        }
        if (powerType != null && !powerType.isEmpty()) {
            data.putString("ArcaneSummonerPower", powerType);
        }

        // atributos extras
        data.putDouble("ArcaneSummonerExtraArmor", extraArmor);
        data.putDouble("ArcaneSummonerExtraHealth", extraHealth);
        data.putDouble("ArcaneSummonerExtraStrength", extraStrength);

        // atributos originais (antes dos buffs)
        if (mob.getAttribute(Attributes.ARMOR) != null) {
            data.putDouble("ArcaneSummonerBaseArmor",
                    mob.getAttribute(Attributes.ARMOR).getBaseValue());
        }
        if (mob.getAttribute(Attributes.MAX_HEALTH) != null) {
            data.putDouble("ArcaneSummonerBaseHealth",
                    mob.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        }
        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            data.putDouble("ArcaneSummonerBaseStrength",
                    mob.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }

        // equipamentos
        ListTag equipList = new ListTag();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                CompoundTag stackTag = new CompoundTag();
                stack.save(stackTag);
                stackTag.putString("Slot", slot.getName());
                equipList.add(stackTag);
            }
        }
        data.put("ArcaneSummonerEquipment", equipList);
    }

    public static boolean isSummonedMob(Mob mob) {
        return mob.getPersistentData().getBoolean("ArcaneSummonerSummoned");
    }

    // 🔹 Reaplica os dados quando a entidade é carregada do mundo
    public static void loadSummonData(Mob mob, ServerLevel level) {
        if (!isSummonedMob(mob))
            return;

        // 🔹 Limpa IA antiga se for um PathfinderMob
        if (mob instanceof PathfinderMob pathfinderMob) {
            pathfinderMob.goalSelector.removeAllGoals(g -> true);
            pathfinderMob.targetSelector.removeAllGoals(g -> true);
        }

        CompoundTag data = mob.getPersistentData();
        if (!data.getBoolean("ArcaneSummonerSummoned"))
            return;

        // comportamento
        if (data.contains("ArcaneSummonerBehavior")) {
            String behaviorBase = data.getString("ArcaneSummonerBehavior");
            String subBehavior = data.contains("ArcaneSummonerSubBehavior")
                    ? data.getString("ArcaneSummonerSubBehavior")
                    : "";

            MobBehaviorHelper.applyBehavior(mob, behaviorBase, subBehavior, level);
        }

        // buffs Elytra
        if (data.getBoolean("ArcaneSummonerElytraBuff")) {
            mob.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.JUMP, 999999, 1, true, false));
            mob.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.SLOW_FALLING, 999999, 0, true, false));
            mob.goalSelector.addGoal(3, new TeleportToTargetGoal(mob, 100));
        }

        // poder especial
        if (data.contains("ArcaneSummonerPower")) {
            String power = data.getString("ArcaneSummonerPower");
            if ("fireball".equals(power)) {
                mob.goalSelector.addGoal(4, new ShootProjectileGoal(mob, 60, false));
            } else if ("wither".equals(power)) {
                mob.goalSelector.addGoal(4, new ShootProjectileGoal(mob, 100, true));
            }
        }

        // atributos extras
        // Reaplica atributos
        if (data.contains("ArcaneSummonerBaseHealth") && mob.getAttribute(Attributes.MAX_HEALTH) != null) {
            double base = data.getDouble("ArcaneSummonerBaseHealth");
            double extra = data.getDouble("ArcaneSummonerExtraHealth");
            mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(base + extra);
            mob.setHealth(mob.getMaxHealth());
        }

        if (data.contains("ArcaneSummonerBaseArmor") && mob.getAttribute(Attributes.ARMOR) != null) {
            double base = data.getDouble("ArcaneSummonerBaseArmor");
            double extra = data.getDouble("ArcaneSummonerExtraArmor");
            mob.getAttribute(Attributes.ARMOR).setBaseValue(base + extra);
        }

        if (data.contains("ArcaneSummonerBaseStrength") && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            double base = data.getDouble("ArcaneSummonerBaseStrength");
            double extra = data.getDouble("ArcaneSummonerExtraStrength");
            mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(base + extra);
        }

        // equipamentos
        if (data.contains("ArcaneSummonerEquipment", Tag.TAG_LIST)) {
            ListTag equipList = data.getList("ArcaneSummonerEquipment", Tag.TAG_COMPOUND);
            for (int i = 0; i < equipList.size(); i++) {
                CompoundTag stackTag = equipList.getCompound(i);
                if (stackTag.contains("Slot") && stackTag.contains("id")) {
                    String slotName = stackTag.getString("Slot");
                    EquipmentSlot slot = EquipmentSlot.byName(slotName);
                    ItemStack stack = ItemStack.of(stackTag);
                    mob.setItemSlot(slot, stack);
                }
            }
        }
        mob.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 999999, 0, true, false));

    }

}
