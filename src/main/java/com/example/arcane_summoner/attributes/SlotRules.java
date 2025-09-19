package com.example.arcane_summoner.attributes;

import com.example.arcane_summoner.config.SlotRulesConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SlotRules {

    public static boolean isValidForSlot(int slot, ItemStack stack) {
        if (stack.isEmpty()) return false;

        List<String> rules = SlotRulesConfig.getRulesForSlot(slot);

        for (String rule : rules) {
            if (rule.startsWith("#")) {
                // Tag check
                String tagId = rule.substring(1); // remove "#"
                if (stack.is(ItemTags.create(new ResourceLocation(tagId)))) {
                    return true;
                }
            } else {
                // Item check
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (id != null && id.toString().equals(rule)) {
                    return true;
                }
            }
        }

        return false;
    }
}
