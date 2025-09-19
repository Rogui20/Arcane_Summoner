package com.example.arcane_summoner.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SlotRulesConfig {
    private static Map<String, List<String>> slotRules = Collections.emptyMap();

    private static final Gson GSON = new Gson();

    public static void load(Path configDir) {
        try {
            Path path = configDir.resolve("arcane_summoner/slot_rules.json");
            try (FileReader reader = new FileReader(path.toFile())) {
                Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
                slotRules = GSON.fromJson(reader, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            slotRules = Collections.emptyMap();
        }
    }

    public static List<String> getRulesForSlot(int slot) {
        return slotRules.getOrDefault(String.valueOf(slot), Collections.emptyList());
    }
}
