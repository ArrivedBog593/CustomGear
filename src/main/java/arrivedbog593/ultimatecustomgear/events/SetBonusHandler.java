package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomArmorItem;
import arrivedbog593.ultimatecustomgear.util.EffectUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SetBonusHandler {

    private static final Map<UUID, Map<String, GearData>> activeSetData = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, List<GearData.EffectData>>> activePieceEffectsData = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        activeSetData.remove(id);
        activePieceEffectsData.remove(id);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;

        UUID playerId = player.getUUID();

        Map<String, GearData> currentActiveSetData =
                activeSetData.getOrDefault(playerId, Map.of());
        Map<String, List<GearData.EffectData>> currentActivePieceEffectsData =
                activePieceEffectsData.getOrDefault(playerId, Map.of());

        for (GearData data : currentActiveSetData.values()) {
            if (data.setBonus != null) {
                removeEffects(player, data.setBonus.effects);
            }
        }
        for (List<GearData.EffectData> effects : currentActivePieceEffectsData.values()) {
            removeEffects(player, effects);
        }

        Map<String, Integer> piecesWorn = new HashMap<>();
        Map<String, GearData> setDataMap = new HashMap<>();
        List<PieceInfo> equippedPieces = new ArrayList<>();

        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof CustomArmorItem armorItem) {
                GearData data = armorItem.getGearDataDirect();
                String piece = armorItem.getPiece();

                if (data.setBonus != null) {
                    piecesWorn.merge(data.id, 1, Integer::sum);
                    setDataMap.put(data.id, data);
                }
                if (data.pieceEffects != null && data.pieceEffects.containsKey(piece)) {
                    equippedPieces.add(new PieceInfo(data.id + "_" + piece, data, piece));
                }
            }
        }

        Set<String> newActiveSets = new HashSet<>();
        Map<String, GearData> newActiveSetData = new HashMap<>();
        Set<String> newActivePieces = new HashSet<>();
        Map<String, List<GearData.EffectData>> newActivePieceEffectsData = new HashMap<>();

        for (Map.Entry<String, Integer> entry : piecesWorn.entrySet()) {
            String setId = entry.getKey();
            GearData data = setDataMap.get(setId);
            if (data.setBonus != null && entry.getValue() >= data.setBonus.requiredPieces) {
                applyEffects(player, data.setBonus.effects);
                newActiveSets.add(setId);
                newActiveSetData.put(setId, data);
            }
        }

        for (PieceInfo info : equippedPieces) {
            List<GearData.EffectData> effects = info.data.pieceEffects.get(info.piece());
            if (effects != null && !effects.isEmpty()) {
                applyEffects(player, effects);
                newActivePieces.add(info.pieceId);
                newActivePieceEffectsData.put(info.pieceId, effects);
            }
        }

        activeSetData.put(playerId, newActiveSetData);
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