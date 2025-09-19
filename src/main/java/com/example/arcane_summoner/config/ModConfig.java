package com.example.arcane_summoner.config;

import com.example.arcane_summoner.ArcaneSummoner;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModConfig {
    private static final Gson GSON = new Gson();

    // Mapas em memória
    private static final Map<String, Integer> STACK_LIMITS = new HashMap<>();
    private static final Map<String, Float> ARMOR_VALUES = new HashMap<>();
    private static final Map<String, Float> FOOD_VALUES = new HashMap<>();
    private static final Map<String, List<String>> SLOT_RULES = new HashMap<>();
    private static final Map<String, com.google.gson.JsonObject> MOB_BEHAVIORS = new HashMap<>();
    private static final Map<String, Map<String, String>> BEHAVIOR_NAMES = new HashMap<>();
    private static final Map<String, String> BEHAVIOR_MOBS = new HashMap<>();
    private static final Map<String, List<String>> MOB_SKINS = new HashMap<>();
    private static JsonObject bossConfig;
    private static JsonObject summonRulesConfig;

    // Carrega todos os arquivos JSON
    public static void load(Path configDir) {
        Path baseDir = configDir.resolve(ArcaneSummoner.MODID);

        loadFile(baseDir.resolve("stack_limit.json"), STACK_LIMITS,
                new TypeToken<Map<String, Integer>>() {
                }.getType());
        loadFile(baseDir.resolve("armor_values.json"), ARMOR_VALUES,
                new TypeToken<Map<String, Float>>() {
                }.getType());
        loadFile(baseDir.resolve("food_values.json"), FOOD_VALUES,
                new TypeToken<Map<String, Float>>() {
                }.getType());
        loadFile(baseDir.resolve("slot_rules.json"), SLOT_RULES,
                new TypeToken<Map<String, List<String>>>() {
                }.getType());

        loadFile(configDir.resolve("arcane_summoner/mob_behaviors.json"), MOB_BEHAVIORS,
                new TypeToken<Map<String, com.google.gson.JsonObject>>() {
                }.getType());

        loadFile(configDir.resolve("arcane_summoner/behavior_names.json"),
                BEHAVIOR_NAMES,
                new TypeToken<Map<String, Map<String, String>>>() {
                }.getType());

        loadFile(configDir.resolve("arcane_summoner/behavior_mobs.json"),
                BEHAVIOR_MOBS,
                new TypeToken<Map<String, String>>() {
                }.getType());

        loadFile(configDir.resolve("arcane_summoner/behavior_mobs_skin.json"),
                MOB_SKINS,
                new TypeToken<Map<String, List<String>>>() {
                }.getType());

        Path bossFile = configDir.resolve("arcane_summoner/arcane_summoner_boss.json");
        try (Reader r = Files.newBufferedReader(bossFile)) {
            bossConfig = JsonParser.parseReader(r).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            bossConfig = new JsonObject();
        }
        Path summonRulesFile = configDir.resolve("arcane_summoner/summon_rules.json");
        try (Reader r = Files.newBufferedReader(summonRulesFile)) {
            summonRulesConfig = JsonParser.parseReader(r).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            summonRulesConfig = new JsonObject();
        }

        System.out.println("[ArcaneSummoner] Configs carregadas de: " + configDir.toAbsolutePath());
        System.out.println("[ArcaneSummoner] StackLimit(diamond) = " + STACK_LIMITS.get("minecraft:diamond"));
        System.out.println("[ArcaneSummoner] Regras Slot 0 = " + SLOT_RULES.get("0"));
    }

    private static <T> void loadFile(Path path, Map<String, T> target, Type type) {
        target.clear();
        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                Map<String, T> map = GSON.fromJson(reader, type);
                if (map != null) {
                    target.putAll(map);
                    System.out.println("[ArcaneSummoner][DEBUG] Carregado arquivo " + path.getFileName() + " com "
                            + map.size() + " entradas.");
                } else {
                    System.out.println("[ArcaneSummoner][DEBUG] Arquivo " + path.getFileName() + " está vazio!");
                }
            } catch (IOException e) {
                System.err.println("[ArcaneSummoner][ERRO] Falha ao ler config: " + path);
                e.printStackTrace();
            }
        } else {
            System.out.println("[ArcaneSummoner][DEBUG] Arquivo não encontrado: " + path);
        }
    }

    // Helpers
    public static int getStackLimit(Item item) {
        String id = getItemId(item);
        int limit = STACK_LIMITS.getOrDefault(id, 64);
        System.out.println("[ArcaneSummoner][DEBUG] getStackLimit(" + id + ") = " + limit);
        return limit;
    }

    public static float getArmorValue(Item item) {
        String id = getItemId(item);
        float value = ARMOR_VALUES.getOrDefault(id, 0f);
        System.out.println("[ArcaneSummoner][DEBUG] getArmorValue(" + id + ") = " + value);
        return value;
    }

    public static float getFoodValue(Item item) {
        String id = getItemId(item);
        float value = FOOD_VALUES.getOrDefault(id, 0f);
        System.out.println("[ArcaneSummoner][DEBUG] getFoodValue(" + id + ") = " + value);
        return value;
    }

    public static String getItemId(Item item) {
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
        return rl != null ? rl.toString() : "";
    }

    public static boolean isItemAllowedForSlot(int slot, Item item) {
        List<String> rules = SLOT_RULES.get(String.valueOf(slot));
        String itemId = getItemId(item);

        if (rules == null) {
            System.out.println("[ArcaneSummoner][DEBUG] Nenhuma regra para slot " + slot + " → bloqueando " + itemId);
            return false;
        }

        for (String rule : rules) {
            if (rule.startsWith("#")) {
                ResourceLocation tagId = new ResourceLocation(rule.substring(1));
                TagKey<Item> tag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, tagId);

                for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                    if (holder.value() == item) {
                        System.out.println("[ArcaneSummoner][DEBUG] Item " + itemId + " ACEITO pelo slot " + slot
                                + " via tag " + rule);
                        return true;
                    }
                }
            } else if (rule.equals(itemId)) {
                System.out.println(
                        "[ArcaneSummoner][DEBUG] Item " + itemId + " ACEITO pelo slot " + slot + " via match direto");
                return true;
            }
        }

        System.out.println("[ArcaneSummoner][DEBUG] Item " + itemId + " REJEITADO no slot " + slot);
        return false;
    }

    // Novo getter
    public static com.google.gson.JsonObject getMobBehavior(String profile) {
        return MOB_BEHAVIORS.get(profile);
    }

    public static Component getBehaviorName(String behaviorKey) {
        Map<String, String> entry = BEHAVIOR_NAMES.get(behaviorKey);
        if (entry == null) {
            return Component.literal(behaviorKey); // fallback
        }

        String name = entry.getOrDefault("name", behaviorKey);
        String colorHex = entry.getOrDefault("color", "#FFFFFF");

        int tmp;
        try {
            tmp = Integer.parseInt(colorHex.substring(1), 16);
        } catch (Exception e) {
            tmp = 0xFFFFFF;
        }
        final int rgb = tmp; // <- agora é efetivamente final

        return Component.literal(name)
                .withStyle(style -> style.withColor(TextColor.fromRgb(rgb)));

    }

    public static String getBehaviorMobId(String behavior) {
        // pega só a base antes do ':'
        String base = behavior.contains(":") ? behavior.split(":")[0] : behavior;
        return BEHAVIOR_MOBS.getOrDefault(base, "arcane_summoner:human_knight");
    }

    public static boolean getBossEnabled() {
        return bossConfig.has("enabled") && bossConfig.get("enabled").getAsBoolean();
    }

    public static int getBossMinDistance() {
        return bossConfig.has("min_distance") ? bossConfig.get("min_distance").getAsInt() : 30;
    }

    public static int getBossMaxBlockLight() {
        return bossConfig.has("max_block_light") ? bossConfig.get("max_block_light").getAsInt() : 6;
    }

    public static double getBossMultiplierMin() {
        return bossConfig.has("multiplier_min") ? bossConfig.get("multiplier_min").getAsDouble() : 1.2;
    }

    public static double getBossMultiplierMax() {
        return bossConfig.has("multiplier_max") ? bossConfig.get("multiplier_max").getAsDouble() : 2.5;
    }

    public static double getBossOffhandChance() {
        return bossConfig.has("offhand_chance") ? bossConfig.get("offhand_chance").getAsDouble() : 0.35;
    }

    public static double getBossElytraChance() {
        return bossConfig.has("elytra_chance") ? bossConfig.get("elytra_chance").getAsDouble() : 0.25;
    }

    public static double getBossProjectileChance() {
        return bossConfig.has("projectile_chance") ? bossConfig.get("projectile_chance").getAsDouble() : 0.40;
    }

    public static double getBossWitherProjectileChance() {
        return bossConfig.has("wither_projectile_chance") ? bossConfig.get("wither_projectile_chance").getAsDouble()
                : 0.50;
    }

    public static double getBossPotion1Chance() {
        return bossConfig.has("potion1_chance") ? bossConfig.get("potion1_chance").getAsDouble() : 0.50;
    }

    public static double getBossPotion2Chance() {
        return bossConfig.has("potion2_chance") ? bossConfig.get("potion2_chance").getAsDouble() : 0.25;
    }

    public static List<MobEffect> getBossPotionPool() {
        List<MobEffect> list = new ArrayList<>();
        if (bossConfig.has("potion_pool")) {
            for (JsonElement el : bossConfig.getAsJsonArray("potion_pool")) {
                ResourceLocation id = new ResourceLocation(el.getAsString());
                MobEffect eff = BuiltInRegistries.MOB_EFFECT.get(id);
                if (eff != null)
                    list.add(eff);
            }
        }
        return list;
    }

    public static boolean getBossRandomEventsEnabled() {
        return bossConfig.has("random_events_enabled") && bossConfig.get("random_events_enabled").getAsBoolean();
    }

    public static int getBossRandomDaysMin() {
        return bossConfig.has("random_days_min") ? bossConfig.get("random_days_min").getAsInt() : 7;
    }

    public static int getBossRandomDaysMax() {
        return bossConfig.has("random_days_max") ? bossConfig.get("random_days_max").getAsInt() : 28;
    }

    public static double nextBossMultiplier() {
        double min = getBossMultiplierMin();
        double max = getBossMultiplierMax();
        return min + (new Random().nextDouble() * (max - min));
    }

    public static MobEffect applyRandomConfiguredEffect(Mob mob) {
        List<MobEffect> pool = getBossPotionPool();
        if (pool.isEmpty())
            return null;

        MobEffect chosen = pool.get(mob.getRandom().nextInt(pool.size()));
        mob.addEffect(new MobEffectInstance(chosen, 999999, 0, true, false));
        return chosen; // retorna o efeito aplicado
    }

    public static int getBossMaxKills() {
        return bossConfig.has("max_kills") ? bossConfig.get("max_kills").getAsInt() : 10;
    }

    public static double getBossEquipmentsDropChance() {
        return bossConfig.has("equipments_drop_chance")
                ? bossConfig.get("equipments_drop_chance").getAsDouble()
                : 0.1;
    }

    public static int getSummonMaxMobs() {
        return summonRulesConfig.has("max_mobs") ? summonRulesConfig.get("max_mobs").getAsInt() : 10;
    }

    public static int getSummonCheckRadius() {
        return summonRulesConfig.has("check_radius") ? summonRulesConfig.get("check_radius").getAsInt() : 50;
    }

    public static List<String> getSkinsForMobAndBehavior(String behavior) {
        if (!MOB_SKINS.containsKey(behavior))
            return List.of();
        return MOB_SKINS.getOrDefault(behavior, List.of());
    }

    public static String getRandomSkin(String mobId, String behavior, RandomSource random) {
        List<String> skins = getSkinsForMobAndBehavior(behavior);
        if (skins.isEmpty())
            return null;
        return skins.get(random.nextInt(skins.size()));
    }

}
