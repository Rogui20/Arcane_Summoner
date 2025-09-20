package com.example.arcane_summoner.summon;

import com.example.arcane_summoner.ArcaneSummoner;
import com.example.arcane_summoner.config.ModConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneSummoner.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArcaneEvents {
    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob))
            return;
        if (!(mob.level() instanceof ServerLevel level))
            return;

        // Boss
        if (mob.getPersistentData().getBoolean("ArcaneSummonerBoss")) {
            double dropChance = ModConfig.getBossEquipmentsDropChance();
            for (EquipmentSlot slot : new EquipmentSlot[] {
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET,
                    EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND }) {

                ItemStack stack = mob.getItemBySlot(slot);
                if (!stack.isEmpty() && level.random.nextDouble() < dropChance) {
                    mob.spawnAtLocation(stack.copy());
                }
            }
        }

        // Elytra Buff (só para summonados comuns, não bosses)
        //if (PersistentHelper.isSummonedMob(mob)
        //        && mob.getPersistentData().getBoolean("ArcaneSummonerElytraBuff")
        //        && !mob.getPersistentData().getBoolean("ArcaneSummonerBoss")) {
        //    mob.spawnAtLocation(new ItemStack(Items.ELYTRA));
        //}
    }
    @SubscribeEvent
    public static void onPlayerKilled(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if (!(event.getSource().getEntity() instanceof Mob mob))
            return;
        if (!mob.getPersistentData().getBoolean("ArcaneSummonerBoss"))
            return;

        int kills = mob.getPersistentData().getInt("ArcaneSummonerKills") + 1;
        mob.getPersistentData().putInt("ArcaneSummonerKills", kills);

        int maxKills = ModConfig.getBossMaxKills();
        if (kills >= maxKills) {
            // 🔹 Mensagem in-game para todos os jogadores
            mob.level().players().forEach(p -> p.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "[ArcaneSummoner] O boss desapareceu após ceifar " + kills + " vidas!")));

            mob.discard(); // despawna o boss
            System.out.println("[ArcaneSummoner] Boss despawnou após atingir " + kills + " mortes.");
        }
    }
}