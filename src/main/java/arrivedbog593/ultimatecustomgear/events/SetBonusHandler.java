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

/**
 * Applies armor set bonuses and per-piece effects using the refreshed
 * short-duration scheme from {@link EffectUtils}.
 * <p>
 * Each second this handler:
 * <ol>
 *   <li>Computes which sets/pieces are currently active from worn armor.</li>
 *   <li>APPLIES their effects — for effects already active this is just a
 *       duration refresh (no visual churn, potions never downgraded).</li>
 *   <li>Removes (potion-safely) only the effects of sets/pieces that were
 *       active last cycle but are no longer — a diff, not the previous
 *       remove-everything-reapply-everything churn.</li>
 * </ol>
 * If tracking is ever lost (logout, crash), effects self-expire in ≤12s.
 */
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

        // ── 1. Compute what is active NOW from worn armor ────────────────────
        Map<String, Integer> piecesWorn = new HashMap<>();
        Map<String, GearData> setDataMap = new HashMap<>();
        List<PieceInfo> equippedPieces = new ArrayList<>();

        for (ItemStack stack : arrivedbog593.ultimatecustomgear.util.ArmorSlots.of(player)) {
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

        Map<String, GearData> newActiveSetData = new HashMap<>();
        Map<String, List<GearData.EffectData>> newActivePieceEffectsData = new HashMap<>();

        // ── 2. Apply (= refresh) everything currently active ─────────────────
        for (Map.Entry<String, Integer> entry : piecesWorn.entrySet()) {
            String setId = entry.getKey();
            GearData data = setDataMap.get(setId);
            if (data.setBonus != null && entry.getValue() >= data.setBonus.requiredPieces) {
                EffectUtils.applyEffects(player, data.setBonus.effects);
                newActiveSetData.put(setId, data);
            }
        }

        for (PieceInfo info : equippedPieces) {
            List<GearData.EffectData> effects = info.data.pieceEffects.get(info.piece());
            if (effects != null && !effects.isEmpty()) {
                EffectUtils.applyEffects(player, effects);
                newActivePieceEffectsData.put(info.pieceId, effects);
            }
        }

        // ── 3. Remove only what STOPPED being active (diff vs. last cycle) ────
        Map<String, GearData> prevSets =
                activeSetData.getOrDefault(playerId, Map.of());
        Map<String, List<GearData.EffectData>> prevPieces =
                activePieceEffectsData.getOrDefault(playerId, Map.of());

        for (Map.Entry<String, GearData> entry : prevSets.entrySet()) {
            if (!newActiveSetData.containsKey(entry.getKey())
                    && entry.getValue().setBonus != null) {
                EffectUtils.removeEffects(player, entry.getValue().setBonus.effects);
            }
        }

        for (Map.Entry<String, List<GearData.EffectData>> entry : prevPieces.entrySet()) {
            if (!newActivePieceEffectsData.containsKey(entry.getKey())) {
                EffectUtils.removeEffects(player, entry.getValue());
            }
        }

        // ── 4. Store the new state ────────────────────────────────────────────
        activeSetData.put(playerId, newActiveSetData);
        activePieceEffectsData.put(playerId, newActivePieceEffectsData);
    }

    private record PieceInfo(String pieceId, GearData data, String piece) {
    }
}