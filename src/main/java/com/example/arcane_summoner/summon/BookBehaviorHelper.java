package com.example.arcane_summoner.summon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;

import java.util.Locale;

public class BookBehaviorHelper {
    public static String getBehaviorFromBook(ItemStack book) {
        if (book.isEmpty() || !(book.getItem() instanceof WrittenBookItem)) {
            return "neutral";
        }

        CompoundTag tag = book.getTag();
        if (tag == null || !tag.contains("pages")) {
            return "neutral";
        }

        ListTag pages = tag.getList("pages", 8); // 8 = String NBT
        if (pages.isEmpty()) return "neutral";

        // Linha 1 → comportamento
        String behavior = Component.Serializer.fromJson(pages.getString(0)).getString()
                .toLowerCase(Locale.ROOT).trim();

        // Linha 2 → subcomportamento (apenas usado se friendly)
        String sub = "";
        if (pages.size() > 1) {
            sub = Component.Serializer.fromJson(pages.getString(1)).getString()
                    .toLowerCase(Locale.ROOT).trim();
        }

        // Junta os dois em uma string “behavior:sub”
        if (!sub.isEmpty()) {
            return behavior + ":" + sub;
        }
        return behavior;
    }
}
