package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomArmorItem;
import arrivedbog593.ultimatecustomgear.registry.GearRegistry;
import arrivedbog593.ultimatecustomgear.util.EffectUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Applies held effects (main hand and offhand) using the refreshed
 * short-duration scheme from {@link EffectUtils}: effects are re-applied
 * every second while the item is held, and expire on their own (≤12s) if
 * tracking is ever lost — no more infinite ghost effects after logout.
 * <p>
 * Hand-change tracking is kept for snappy removal on item swap; the removal
 * is potion-safe (see EffectUtils.removeEffects). Known minor edge: if BOTH
 * hands grant the same effect and one hand swaps away, the removal briefly
 * clears it — the next refresh cycle (≤1s) restores it from the remaining
 * hand. Self-healing by design.
 */
public class HeldEffectHandler {

    private static final Map<UUID, ResourceLocation> lastMainHand = new ConcurrentHashMap<>();
    private static final Map<UUID, ResourceLocation> lastOffHand  = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        lastMainHand.remove(id);
        lastOffHand.remove(id);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;

        UUID id = player.getUUID();

        ItemStack mainStack = player.getMainHandItem();
        ItemStack offStack  = player.getOffhandItem();

        ResourceLocation currentMain = getItemId(mainStack);
        ResourceLocation currentOff  = getItemId(offStack);

        ResourceLocation prevMain = lastMainHand.get(id);
        ResourceLocation prevOff  = lastOffHand.get(id);

        // If main hand item changed, remove effects from previous item
        // (potion-safe removal — real potions of the same effect survive)
        if (prevMain != null && !prevMain.equals(currentMain)) {
            GearData prevData = GearRegistry.lookupGear(prevMain);
            if (prevData != null && prevData.heldEffects != null) {
                EffectUtils.removeEffects(player, prevData.heldEffects);
            }
        }

        // If offhand item changed, remove effects from previous item
        if (prevOff != null && !prevOff.equals(currentOff)) {
            GearData prevData = GearRegistry.lookupGear(prevOff);
            if (prevData != null && prevData.heldEffects != null) {
                EffectUtils.removeEffects(player, prevData.heldEffects);
            }
        }

        // Apply (= refresh) effects for what is held right now. Re-applying
        // every cycle is what keeps the short duration alive; it is not churn.
        checkAndApply(player, mainStack);
        checkAndApply(player, offStack);

        if (currentMain != null) lastMainHand.put(id, currentMain);
        else lastMainHand.remove(id);

        if (currentOff != null) lastOffHand.put(id, currentOff);
        else lastOffHand.remove(id);
    }

    private static void checkAndApply(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;
        List<GearData.EffectData> effects = getHeldEffects(stack);
        if (effects == null || effects.isEmpty()) return;
        EffectUtils.applyEffects(player, effects);
    }

    private static ResourceLocation getItemId(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    private static List<GearData.EffectData> getHeldEffects(ItemStack stack) {
        if (stack.isEmpty()) return null;
        Item item = stack.getItem();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        GearData data = GearRegistry.lookupGear(id);
        if (data == null) return null;
        if (item instanceof CustomArmorItem) return null;
        return data.heldEffects;
    }
}