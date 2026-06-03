package com.github.arrivedbog593.events;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.gear.CustomArmorItem;
import com.github.arrivedbog593.util.EffectUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SetBonusHandler {

    private static final Map<UUID, Set<String>> activeSetBonuses = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, GearData>> activeSetData = new ConcurrentHashMap<>();
    private static final Map<UUID, Set<String>> activePieceEffects = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, List<GearData.EffectData>>> activePieceEffectsData = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        activeSetBonuses.remove(id);
        activeSetData.remove(id);
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

                if (data.setBonus != null) {
                    String setId = data.id;
                    piecesWorn.merge(setId, 1, Integer::sum);
                    setDataMap.put(setId, data);
                }

                if (data.pieceEffects != null && data.pieceEffects.containsKey(piece)) {
                    equippedPieces.add(new PieceInfo(data.id + "_" + piece, data, piece));
                }
            }
        }

        UUID playerId = player.getUUID();
        Set<String> currentActiveSets = activeSetBonuses.computeIfAbsent(playerId, k -> new HashSet<>());
        Map<String, GearData> currentActiveSetData = activeSetData.computeIfAbsent(playerId, k -> new HashMap<>()); // FIX
        Set<String> currentActivePieces = activePieceEffects.computeIfAbsent(playerId, k -> new HashSet<>());
        Map<String, List<GearData.EffectData>> currentActivePieceEffectsData = activePieceEffectsData.computeIfAbsent(playerId, k -> new HashMap<>());

        Set<String> newActiveSets = new HashSet<>();
        Map<String, GearData> newActiveSetData = new HashMap<>(); // FIX
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
                newActiveSetData.put(setId, data);
            }
        }

        // Apply individual piece effects
        for (PieceInfo info : equippedPieces) {
            List<GearData.EffectData> effects = info.data.pieceEffects.get(info.piece);
            if (effects != null && !effects.isEmpty()) {
                applyEffects(player, effects);
                newActivePieces.add(info.pieceId);
                newActivePieceEffectsData.put(info.pieceId, effects);
            }
        }

        // Remove effects from sets that no longer meet requirements
        for (String setId : currentActiveSets) {
            if (!newActiveSets.contains(setId)) {
                GearData data = currentActiveSetData.get(setId);
                if (data != null && data.setBonus != null) {
                    removeEffects(player, data.setBonus.effects);
                }
            }
        }

        // Remove effects from unequipped pieces
        for (String pieceId : currentActivePieces) {
            if (!newActivePieces.contains(pieceId)) {
                List<GearData.EffectData> effects = currentActivePieceEffectsData.get(pieceId);
                if (effects != null) {
                    removeEffects(player, effects);
                }
            }
        }

        activeSetBonuses.put(playerId, newActiveSets);
        activeSetData.put(playerId, newActiveSetData);
        activePieceEffects.put(playerId, newActivePieces);
        activePieceEffectsData.put(playerId, newActivePieceEffectsData);
    }

    private static void applyEffects(Player player, List<GearData.EffectData> effects) {
        EffectUtils.applyEffects(player, effects);
    }

    private static void removeEffects(Player player, List<GearData.EffectData> effects) {
        EffectUtils.removeEffects(player, effects);
    }

    private record PieceInfo(String pieceId, GearData data, String piece) {
    }
}