package com.github.arrivedbog593.events;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.CustomArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.*;

@EventBusSubscriber(modid = "customgear")
public class SetBonusHandler {

    private static final Map<UUID, Set<String>> activeSetBonuses = new HashMap<>();
    private static final Map<UUID, Set<String>> activePieceEffects = new HashMap<>();
    private static final Map<UUID, Map<String, List<GearData.EffectData>>> activePieceEffectsData = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        activeSetBonuses.remove(id);
        activePieceEffects.remove(id);
        activePieceEffectsData.remove(id);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;

        Map<String, Integer> piecesWorn = new HashMap<>();
        Map<String, GearData> setDataMap = new HashMap<>();
        List<PieceInfo> equippedPieces = new ArrayList<>();

        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof CustomArmorItem armorItem) {
                GearData data = armorItem.getGearDataDirect();
                String piece = armorItem.getPiece();

                // Check for set bonus
                if (data.setBonus != null) {
                    String setId = data.id;
                    piecesWorn.merge(setId, 1, Integer::sum);
                    setDataMap.put(setId, data);
                }

                // Check for individual piece effects
                if (data.pieceEffects != null && data.pieceEffects.containsKey(piece)) {
                    equippedPieces.add(new PieceInfo(data.id + "_" + piece, data, piece));
                }
            }
        }

        UUID playerId = player.getUUID();
        Set<String> currentActiveSets = activeSetBonuses.computeIfAbsent(playerId, _ -> new HashSet<>());
        Set<String> currentActivePieces = activePieceEffects.computeIfAbsent(playerId, _ -> new HashSet<>());
        Map<String, List<GearData.EffectData>> currentActivePieceEffectsData = activePieceEffectsData.computeIfAbsent(playerId, _ -> new HashMap<>());

        Set<String> newActiveSets = new HashSet<>();
        Set<String> newActivePieces = new HashSet<>();
        Map<String, List<GearData.EffectData>> newActivePieceEffectsData = new HashMap<>();

        // Apply set bonus effects
        for (Map.Entry<String, Integer> entry : piecesWorn.entrySet()) {
            String setId = entry.getKey();
            int count = entry.getValue();
            GearData data = setDataMap.get(setId);

            if (data.setBonus != null && count >= data.setBonus.requiredPieces) {
                applyEffects(player, data.setBonus.effects);
                newActiveSets.add(setId);
            }
        }

        // Apply individual piece effects
        for (PieceInfo info : equippedPieces) {
            List<GearData.EffectData> effects = info.data.pieceEffects.get(info.piece);
            if (effects != null && !effects.isEmpty()) {
                applyEffects(player, effects);
                newActivePieces.add(info.pieceId);
                newActivePieceEffectsData.put(info.pieceId, effects); // Store effects for next iteration
            }
        }

        // Remove effects from sets that no longer meet requirements
        for (String setId : currentActiveSets) {
            if (!newActiveSets.contains(setId)) {
                GearData data = setDataMap.get(setId);
                if (data != null && data.setBonus != null) {
                    removeEffects(player, data.setBonus.effects);
                }
            }
        }

        // Remove effects from unequipped pieces
        for (String pieceId : currentActivePieces) {
            if (!newActivePieces.contains(pieceId)) {
                // Use stored data instead of searching equipped pieces
                List<GearData.EffectData> effects = currentActivePieceEffectsData.get(pieceId);
                if (effects != null) {
                    removeEffects(player, effects);
                }
            }
        }

        activeSetBonuses.put(playerId, newActiveSets);
        activePieceEffects.put(playerId, newActivePieces);
        activePieceEffectsData.put(playerId, newActivePieceEffectsData); // Store for next iteration
    }

    private static void applyEffects(Player player, List<GearData.EffectData> effects) {
        if (effects == null) return;
        for (GearData.EffectData effectData : effects) {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT
                    .getHolder(rl).orElse(null);
            if (effectHolder == null) {
                System.err.println("[CustomGear] Effect not found: " + effectData.effect);
                continue;
            }
            player.addEffect(new MobEffectInstance(
                    effectHolder, -1, effectData.amplifier, true, false));
        }
    }

    private static void removeEffects(Player player, List<GearData.EffectData> effects) {
        if (effects == null) return;
        for (GearData.EffectData effectData : effects) {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT
                    .getHolder(rl).orElse(null);
            if (effectHolder == null) continue;
            player.removeEffect(effectHolder);
        }
    }

    private record PieceInfo(String pieceId, GearData data, String piece) {
    }
}