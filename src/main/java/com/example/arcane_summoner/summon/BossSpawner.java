package com.example.arcane_summoner.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.level.LightLayer;

import java.util.*;

import com.example.arcane_summoner.config.ModConfig;

public class BossSpawner {

    /** Spawna o boss perto do player, obedecendo configs e multiplicador. */
    public static boolean spawnBossFor(ServerPlayer target, double multiplier) {
        ServerLevel level = target.serverLevel();

        int minDist = ModConfig.getBossMinDistance();
        int maxBlockLight = ModConfig.getBossMaxBlockLight();

        // 1) Achar posição válida ≥ minDist do player, com luz de BLOCO ≤ limite
        BlockPos spawnPos = findSpawnPos(level, target.blockPosition(), minDist, maxBlockLight);
        if (spawnPos == null) {
            target.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "[ArcaneSummoner] Não achei posição adequada para o boss (luz de bloco ≤ " + maxBlockLight + ")."));
            return false;
        }

        // 2) Tipo do boss
        WitherSkeleton boss = EntityType.WITHER_SKELETON.create(level);
        if (boss == null)
            return false;

        boss.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                level.random.nextFloat() * 360F, 0);

        // 3) Copiar e/ou garantir armaduras
        copyOrGiveArmor(boss, target);

        // 4) Escolher armas
        chooseAndEquipWeapons(boss, target);

        // 5) Escalar atributos base
        BossStats stats = scaleAttributesFromPlayer(boss, target, multiplier);

        // 6) Elytra, projéteis, poções
        boolean hasElytra = maybeApplyElytraBuff(boss);
        String power = maybeApplyProjectilePower(boss);
        List<String> potions = maybeApplyRandomPotions(boss);

        // 7) IA: atacar o player (precisa ser PathfinderMob)
        PathfinderMob pm = boss; // WitherSkeleton é PathfinderMob
        pm.goalSelector.addGoal(2, new MeleeAttackGoal(pm, 1.15, false));
        pm.targetSelector.addGoal(1, new HurtByTargetGoal(pm));
        pm.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(pm, Player.class, true));

        boss.setTarget(target);

        // 8) Nome
        boss.setCustomName(
                net.minecraft.network.chat.Component.literal("Arcane Nemesis of " + target.getGameProfile().getName()));
        boss.setCustomNameVisible(true);

        // 9) Persistência
        boss.getPersistentData().putBoolean("ArcaneSummonerBoss", true);
        boss.getPersistentData().putInt("ArcaneSummonerKills", 0);
        boss.getPersistentData().putDouble("ArcaneSummonerHealth", stats.health);
        boss.getPersistentData().putDouble("ArcaneSummonerDamage", stats.damage);
        boss.getPersistentData().putDouble("ArcaneSummonerSpeed", stats.speed);
        boss.getPersistentData().putDouble("ArcaneSummonerArmor", stats.armor);
        boss.getPersistentData().putBoolean("ArcaneSummonerElytraBuff", hasElytra);
        boss.getPersistentData().putString("ArcaneSummonerPower", power);
        boss.getPersistentData().putString("ArcaneSummonerPotions", String.join(",", potions));

        boss.setPersistenceRequired();
        level.addFreshEntity(boss);

        return true;
    }

    /** Estrutura para retorno dos stats aplicados */
    private static class BossStats {
        double health;
        double damage;
        double speed;
        double armor; // novo campo
    }

    /** Procura uma posição válida para spawn */
    private static BlockPos findSpawnPos(ServerLevel level, BlockPos center, int minDist, int maxBlockLight) {
        RandomSource rnd = level.random;
        for (int tries = 0; tries < 64; tries++) {
            double angle = rnd.nextDouble() * Math.PI * 2.0;
            int dist = minDist + rnd.nextInt(12);
            int dx = center.getX() + (int) Math.round(Math.cos(angle) * dist);
            int dz = center.getZ() + (int) Math.round(Math.sin(angle) * dist);

            BlockPos pos = findGround(level, new BlockPos(dx, center.getY(), dz));
            if (pos == null)
                continue;

            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
            if (blockLight <= maxBlockLight) {
                if (level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above())) {
                    return pos;
                }
            }
        }
        return null;
    }

    private static BlockPos findGround(ServerLevel level, BlockPos start) {
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos(start.getX(), start.getY(), start.getZ());
        while (m.getY() > level.getMinBuildHeight() + 2 && level.isEmptyBlock(m)) {
            m.move(0, -1, 0);
        }
        m.move(0, 1, 0);
        if (m.getY() <= level.getMinBuildHeight() + 2)
            return null;
        return m.immutable();
    }

    private static void copyOrGiveArmor(Mob boss, ServerPlayer player) {
        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                if (stack.hasTag())
                    copy.setTag(stack.getTag().copy());
                boss.setItemSlot(slot, copy);
            } else {
                switch (slot) {
                    case HEAD -> boss.setItemSlot(slot, new ItemStack(Items.NETHERITE_HELMET));
                    case CHEST -> boss.setItemSlot(slot, new ItemStack(Items.NETHERITE_CHESTPLATE));
                    case LEGS -> boss.setItemSlot(slot, new ItemStack(Items.NETHERITE_LEGGINGS));
                    case FEET -> boss.setItemSlot(slot, new ItemStack(Items.NETHERITE_BOOTS));
                }
            }
        }
    }

    private static void chooseAndEquipWeapons(Mob boss, ServerPlayer player) {
        RandomSource random = boss.level().random;
        ItemStack mainhand = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();

        if (!mainhand.isEmpty() && isWeapon(mainhand)) {
            ItemStack copy = mainhand.copy();
            if (mainhand.hasTag())
                copy.setTag(mainhand.getTag().copy());
            boss.setItemSlot(EquipmentSlot.MAINHAND, copy);
        } else {
            boss.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }

        if (random.nextDouble() < ModConfig.getBossOffhandChance()) {
            if (!offhand.isEmpty() && isWeapon(offhand)) {
                ItemStack copy = offhand.copy();
                if (offhand.hasTag())
                    copy.setTag(offhand.getTag().copy());
                boss.setItemSlot(EquipmentSlot.OFFHAND, copy);
            } else {
                boss.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.IRON_SWORD));
            }
        }
    }

    private static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem
                || stack.getItem() instanceof AxeItem;
    }

    private static BossStats scaleAttributesFromPlayer(Mob boss, ServerPlayer player, double mult) {
        BossStats stats = new BossStats();

        double playerMaxHp = player.getMaxHealth();
        if (boss.getAttribute(Attributes.MAX_HEALTH) != null) {
            stats.health = Math.max(20.0, playerMaxHp * mult);
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(stats.health);
            boss.setHealth(boss.getMaxHealth());
        }
        if (boss.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            stats.damage = Math.max(2.0 * mult, boss.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(stats.damage);
        }
        if (boss.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            stats.speed = boss.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue()
                    * Mth.clamp(mult * 0.9, 1.0, 1.6);
            boss.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(stats.speed);
        }
        if (boss.getAttribute(Attributes.ARMOR) != null) {
            double playerArmor = player.getArmorValue(); // valor total da armadura atual do player
            boss.getAttribute(Attributes.ARMOR).setBaseValue(
                    Math.max(10.0, playerArmor * mult) // escala pela config (multiplier)
            );
            stats.armor = boss.getAttribute(Attributes.ARMOR).getBaseValue();
        }

        return stats;
    }

    private static boolean maybeApplyElytraBuff(Mob boss) {
        if (boss.level().random.nextDouble() < ModConfig.getBossElytraChance()) {
            boss.addEffect(new MobEffectInstance(MobEffects.JUMP, 999999, 1, true, false));
            boss.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 999999, 0, true, false));
            boss.goalSelector.addGoal(3, new TeleportToTargetGoal(boss, 100));
            return true;
        }
        return false;
    }

    private static String maybeApplyProjectilePower(Mob boss) {
        if (boss.level().random.nextDouble() < ModConfig.getBossProjectileChance()) {
            boolean wither = boss.level().random.nextDouble() < ModConfig.getBossWitherProjectileChance();
            boss.goalSelector.addGoal(4, new ShootProjectileGoal(boss, wither ? 100 : 60, wither));
            return wither ? "wither" : "fireball";
        }
        return "";
    }

    private static List<String> maybeApplyRandomPotions(Mob boss) {
        List<String> applied = new ArrayList<>();
        if (boss.level().random.nextDouble() < ModConfig.getBossPotion1Chance()) {
            MobEffect eff = ModConfig.applyRandomConfiguredEffect(boss);
            if (eff != null)
                applied.add(eff.getDescriptionId());
        }
        if (boss.level().random.nextDouble() < ModConfig.getBossPotion2Chance()) {
            MobEffect eff = ModConfig.applyRandomConfiguredEffect(boss);
            if (eff != null)
                applied.add(eff.getDescriptionId());
        }
        return applied;
    }

    public static void loadBossData(Mob boss, ServerLevel level) {
        CompoundTag tag = boss.getPersistentData();
        if (!tag.getBoolean("ArcaneSummonerBoss"))
            return;
        // 🔹 Limpa IA antiga se for um PathfinderMob
        if (boss instanceof PathfinderMob pathfinderMob) {
            pathfinderMob.goalSelector.removeAllGoals(g -> true);
            pathfinderMob.targetSelector.removeAllGoals(g -> true);
        }

        // Limpa os goals existentes

        // Restaurar atributos
        if (boss.getAttribute(Attributes.MAX_HEALTH) != null) {
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(tag.getDouble("ArcaneSummonerHealth"));
            boss.setHealth((float) boss.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        }
        if (boss.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(tag.getDouble("ArcaneSummonerDamage"));
        }
        if (boss.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            boss.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(tag.getDouble("ArcaneSummonerSpeed"));
        }
        if (boss.getAttribute(Attributes.ARMOR) != null) {
            boss.getAttribute(Attributes.ARMOR).setBaseValue(tag.getDouble("ArcaneSummonerArmor"));
        }

        // Elytra buff
        if (tag.getBoolean("ArcaneSummonerElytraBuff")) {
            boss.addEffect(new MobEffectInstance(MobEffects.JUMP, 999999, 1, true, false));
            boss.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 999999, 0, true, false));
            boss.goalSelector.addGoal(3, new TeleportToTargetGoal(boss, 100));
        }

        // Poder especial
        String power = tag.getString("ArcaneSummonerPower");
        if (power.equals("fireball")) {
            boss.goalSelector.addGoal(4, new ShootProjectileGoal(boss, 60, false));
        } else if (power.equals("wither")) {
            boss.goalSelector.addGoal(4, new ShootProjectileGoal(boss, 100, true));
        }

        // Poções
        String potionsStr = tag.getString("ArcaneSummonerPotions");
        if (!potionsStr.isEmpty()) {
            String[] potionIds = potionsStr.split(",");
            for (String id : potionIds) {
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(id));
                if (effect != null) {
                    boss.addEffect(new MobEffectInstance(effect, 999999, 0, true, true));
                }
            }
        }

        // IA hostil padrão (só se for PathfinderMob)
        if (boss instanceof PathfinderMob pm) {
            pm.goalSelector.addGoal(2, new MeleeAttackGoal(pm, 1.15, false));
            pm.targetSelector.addGoal(1, new HurtByTargetGoal(pm));
            pm.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(pm, Player.class, true));
        }

        boss.setPersistenceRequired();
    }

}
