package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.config.ModConfig;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemConsumptionHelper {

    /**
     * Decide quantos itens podem ser consumidos de acordo com stack_limit.json.
     * NÃO consome, apenas retorna o valor máximo permitido.
     */
    public static int getConsumableAmount(ItemStack stack) {
        if (stack.isEmpty())
            return 0;

        int limit = ModConfig.getStackLimit(stack.getItem());
        return Math.min(stack.getCount(), limit);
    }

    /**
     * Verifica se o item é permitido nesse slot de acordo com slot_rules.json.
     */
    public static boolean isAllowedInSlot(int slot, Item item) {
        return ModConfig.isItemAllowedForSlot(slot, item);
    }

    /**
     * Consome os itens do slot respeitando stack_limit.json, regras do slot
     * e um limite máximo custom. Retorna a quantidade consumida.
     */
    public static int consumeItems(Container container, int slot, int maxToConsume) {
        ItemStack stack = container.getItem(slot);
        if (stack.isEmpty())
            return 0;

        Item item = stack.getItem();

        // Verifica regra de slot
        if (!isAllowedInSlot(slot, item)) {
            return 0; // não consome item inválido
        }

        // Calcula quantidade a consumir com base no limite global + limite custom
        int toConsume = Math.min(getConsumableAmount(stack), maxToConsume);

        // Consome de fato
        if (toConsume > 0) {
            stack.shrink(toConsume);
            if (stack.isEmpty()) {
                container.setItem(slot, ItemStack.EMPTY);
            }
        }

        return toConsume;
    }

    /**
     * Versão padrão, sem maxToConsume → usa limite do stack_limit.json.
     */
    public static int consumeItems(Container container, int slot) {
        return consumeItems(container, slot, Integer.MAX_VALUE);
    }
}
