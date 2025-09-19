package com.example.arcane_summoner.config;

import com.example.arcane_summoner.ArcaneSummoner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitária para criar configs padrão se não existirem.
 */
public class ConfigGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void generateDefaults(Path configDir) {
        Path baseDir = configDir.resolve(ArcaneSummoner.MODID);
        try {
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
            }

            generateStackLimits(baseDir.resolve("stack_limit.json"));
            generateArmorValues(baseDir.resolve("armor_values.json"));
            generateFoodValues(baseDir.resolve("food_values.json"));
            generateSlotRules(baseDir.resolve("slot_rules.json"));
            // generateMobBehaviors(baseDir.resolve("mob_behaviors.json"));
            generateBehaviorNames(baseDir.resolve("behavior_names.json"));
            generateBehaviorMobs(baseDir.resolve("behavior_mobs.json"));
            generateBossConfig(baseDir); // <-- aqui passa o diretório
            generateSummonRules(baseDir.resolve("summon_rules.json"));
            generateMobSkins(baseDir);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateStackLimits(Path path) throws Exception {
        if (!Files.exists(path)) {
            Map<String, Integer> defaults = new HashMap<>();
            // Ores
            defaults.put("minecraft:diamond", 32);
            defaults.put("minecraft:netherite_ingot", 32);
            defaults.put("minecraft:iron_ingot", 32);
            defaults.put("minecraft:gold_ingot", 32);
            defaults.put("minecraft:copper_ingot", 32);
            defaults.put("minecraft:leather", 32);

            // Food
            defaults.put("minecraft:bread", 32);
            defaults.put("minecraft:golden_apple", 32);
            defaults.put("minecraft:golden_carrot", 32);
            defaults.put("minecraft:cooked_beef", 32);
            defaults.put("minecraft:cooked_porkchop", 32);
            defaults.put("minecraft:cooked_chicken", 32);
            defaults.put("minecraft:cooked_rabbit", 32);
            defaults.put("minecraft:cooked_cod", 32);
            defaults.put("minecraft:cooked_salmon", 32);
            defaults.put("minecraft:baked_potato", 32);
            defaults.put("minecraft:carrot", 32);
            defaults.put("minecraft:cookie", 32);
            defaults.put("minecraft:melon_slice", 32);
            defaults.put("minecraft:beetroot", 32);
            defaults.put("minecraft:glow_berries", 32);
            defaults.put("minecraft:sweet_berries", 32);
            defaults.put("minecraft:apple", 32);

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(defaults, writer);
            }
        }
    }

    private static void generateArmorValues(Path path) throws Exception {
        if (!Files.exists(path)) {
            Map<String, Float> defaults = new HashMap<>();
            defaults.put("minecraft:diamond", 3.0f);
            defaults.put("minecraft:netherite_ingot", 5.0f);
            defaults.put("minecraft:iron_ingot", 2.0f);
            defaults.put("minecraft:gold_ingot", 2.5f);
            defaults.put("minecraft:copper_ingot", 1.5f);
            defaults.put("minecraft:leather", 1.25f);

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(defaults, writer);
            }
        }
    }

    private static void generateFoodValues(Path path) throws Exception {
        if (!Files.exists(path)) {
            Map<String, Float> defaults = new HashMap<>();
            defaults.put("minecraft:bread", 2.0f);
            defaults.put("minecraft:golden_apple", 8.0f);
            defaults.put("minecraft:golden_carrot", 10.0f);
            defaults.put("minecraft:cooked_beef", 4.0f);
            defaults.put("minecraft:cooked_porkchop", 5.0f);
            defaults.put("minecraft:cooked_chicken", 4.0f);
            defaults.put("minecraft:cooked_rabbit", 3.5f);
            defaults.put("minecraft:cooked_cod", 3.5f);
            defaults.put("minecraft:cooked_salmon", 4.0f);
            defaults.put("minecraft:baked_potato", 3.5f);
            defaults.put("minecraft:carrot", 0.5f);
            defaults.put("minecraft:cookie", 0.35f);
            defaults.put("minecraft:melon_slice", 0.5f);
            defaults.put("minecraft:beetroot", 0.25f);
            defaults.put("minecraft:glow_berries", 0.5f);
            defaults.put("minecraft:sweet_berries", 0.5f);
            defaults.put("minecraft:apple", 1.5f);

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(defaults, writer);
            }
        }
    }

    private static void generateSlotRules(Path path) throws Exception {
        if (!Files.exists(path)) {
            Map<String, List<String>> defaults = new HashMap<>();
            defaults.put("0", List.of(
                    "minecraft:diamond",
                    "minecraft:netherite_ingot",
                    "minecraft:iron_ingot",
                    "minecraft:gold_ingot",
                    "minecraft:copper_ingot",
                    "minecraft:leather"));
            defaults.put("1", List.of("minecraft:diamond",
                    "minecraft:netherite_ingot",
                    "minecraft:iron_ingot",
                    "minecraft:gold_ingot",
                    "minecraft:copper_ingot",
                    "minecraft:leather"));
            defaults.put("2", List.of(
                    "minecraft:bread",
                    "minecraft:golden_apple",
                    "minecraft:golden_carrot",
                    "minecraft:cooked_beef",
                    "minecraft:cooked_porkchop",
                    "minecraft:cooked_chicken",
                    "minecraft:cooked_rabbit",
                    "minecraft:cooked_cod",
                    "minecraft:cooked_salmon",
                    "minecraft:baked_potato",
                    "minecraft:carrot",
                    "minecraft:cookie",
                    "minecraft:melon_slice",
                    "minecraft:beetroot",
                    "minecraft:glow_berries",
                    "minecraft:sweet_berries",
                    "minecraft:apple"));
            defaults.put("3", List.of("#forge:armor/helmets",
                    "minecraft:chainmail_helmet",
                    "minecraft:diamond_helmet",
                    "minecraft:golden_helmet",
                    "minecraft:iron_helmet",
                    "minecraft:leather_helmet",
                    "minecraft:netherite_helmet",
                    "minecraft:turtle_helmet"));
            defaults.put("4", List.of("#forge:armor/chestplates",
                    "minecraft:chainmail_chestplate",
                    "minecraft:diamond_chestplate",
                    "minecraft:golden_chestplate",
                    "minecraft:iron_chestplate",
                    "minecraft:leather_chestplate",
                    "minecraft:netherite_chestplate"));
            defaults.put("5", List.of("#forge:armor/leggings",
                    "minecraft:chainmail_leggings",
                    "minecraft:diamond_leggings",
                    "minecraft:golden_leggings",
                    "minecraft:iron_leggings",
                    "minecraft:leather_leggings",
                    "minecraft:netherite_leggings"));
            defaults.put("6", List.of("#forge:armor/boots",
                    "minecraft:chainmail_boots",
                    "minecraft:diamond_boots",
                    "minecraft:golden_boots",
                    "minecraft:iron_boots",
                    "minecraft:leather_boots",
                    "minecraft:netherite_boots"));
            defaults.put("7", List.of(
                    "#forge:swords",
                    "minecraft:bow",
                    "#minecraft:swords",
                    "#minecraft:axe"));
            defaults.put("8", List.of(
                    "#forge:swords",
                    "minecraft:bow",
                    "#minecraft:swords",
                    "#minecraft:axe",
                    "minecraft:shield"));
            defaults.put("9", List.of("minecraft:book", "minecraft:written_book"));
            defaults.put("10", List.of("minecraft:elytra"));
            defaults.put("11", List.of("minecraft:nether_star", "minecraft:ghast_tear"));
            defaults.put("12", List.of(
                    "minecraft:potion",
                    "minecraft:splash_potion",
                    "minecraft:lingering_potion"));
            defaults.put("13", List.of(
                    "minecraft:potion",
                    "minecraft:splash_potion",
                    "minecraft:lingering_potion"));

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(defaults, writer);
            }
        }
    }

    // private static void generateMobBehaviors(Path path) throws Exception {
    // if (!Files.exists(path)) {
    // Map<String, Map<String, Object>> defaults = new HashMap<>();
    //
    // // Exemplo de comportamento Friendly
    // Map<String, Object> friendly = new HashMap<>();
    // friendly.put("behavior", "friendly");
    // friendly.put("attack_targets", List.of("minecraft:zombie",
    // "minecraft:skeleton"));
    //
    // // Exemplo de comportamento Neutral
    // Map<String, Object> neutral = new HashMap<>();
    // neutral.put("behavior", "neutral");
    // neutral.put("attack_targets", List.of());
    //
    // // Exemplo de comportamento Hostile
    // Map<String, Object> hostile = new HashMap<>();
    // hostile.put("behavior", "hostile");
    // hostile.put("attack_targets", List.of("minecraft:player"));
    //
    // defaults.put("arcane_summoner:wither_guardian", friendly);
    // defaults.put("arcane_summoner:wither_specter", neutral);
    // defaults.put("arcane_summoner:wither_brute", hostile);
    //
    // try (FileWriter writer = new FileWriter(path.toFile())) {
    // GSON.toJson(defaults, writer);
    // }
    // }
    // }

    private static void generateBehaviorNames(Path path) throws Exception {
        if (!Files.exists(path)) {
            Map<String, Map<String, String>> defaults = new HashMap<>();

            Map<String, String> friendly = new HashMap<>();
            friendly.put("name", "Friendly Summon");
            friendly.put("color", "#00FF00");

            Map<String, String> hostile = new HashMap<>();
            hostile.put("name", "Hostile Summon");
            hostile.put("color", "#FF0000");

            Map<String, String> neutral = new HashMap<>();
            neutral.put("name", "Neutral Summon");
            neutral.put("color", "#AAAAAA");

            defaults.put("friendly", friendly);
            defaults.put("hostile", hostile);
            defaults.put("neutral", neutral);

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(defaults, writer);
            }
        }
    }

    private static void generateBehaviorMobs(Path path) throws Exception {
        if (!Files.exists(path)) {
            Map<String, String> defaults = new HashMap<>();
            defaults.put("friendly", "arcane_summoner:human_knight");
            defaults.put("hostile", "arcane_summoner:human_knight");
            defaults.put("neutral", "arcane_summoner:human_knight");

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(defaults, writer);
            }
        }
    }

    public static void generateBossConfig(Path configDir) {
        Path file = configDir.resolve("arcane_summoner_boss.json");
        if (Files.exists(file))
            return;

        try {
            String json = """
                    {
                      "enabled": true,
                      "min_distance": 30,
                      "max_block_light": 6,

                      "multiplier_min": 10.2,
                      "multiplier_max": 50.0,

                      "offhand_chance": 0.35,
                      "elytra_chance": 0.25,
                      "projectile_chance": 0.40,
                      "wither_projectile_chance": 0.50,

                      "potion1_chance": 0.50,
                      "potion2_chance": 0.25,
                      "potion_pool": [
                        "minecraft:speed",
                        "minecraft:strength",
                        "minecraft:resistance",
                        "minecraft:regeneration",
                        "minecraft:fire_resistance"
                      ],

                      "random_events_enabled": true,
                      "random_days_min": 7,
                      "random_days_max": 28,
                      "max_kills": 10,
                      "equipments_drop_chance": 0.1
                    }
                    """;
            Files.writeString(file, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateSummonRules(Path path) throws Exception {
        if (!Files.exists(path)) {
            Map<String, Object> defaults = new HashMap<>();
            defaults.put("max_mobs", 10); // limite padrão
            defaults.put("check_radius", 50); // raio quadrado padrão

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(defaults, writer);
            }
        }
    }

    private static void generateMobSkins(Path configDir) throws Exception {
        Path file = configDir.resolve("behavior_mobs_skin.json");
        if (Files.exists(file))
            return;

        try {
            String json = """
                    {
                      "friendly": [
                        "arcane_summoner:entity/human_knight/skin1.png"
                      ],
                      "neutral": [
                        "arcane_summoner:entity/human_knight/skin2.png"
                      ],
                      "hostile": [
                        "arcane_summoner:entity/human_knight/skin3.png"
                      ]
                    }
                    """;
            Files.writeString(file, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}