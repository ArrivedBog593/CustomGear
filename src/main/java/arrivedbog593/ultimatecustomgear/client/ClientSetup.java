package arrivedbog593.ultimatecustomgear.client;

import arrivedbog593.ultimatecustomgear.items.weapons.CustomBowItem;
import arrivedbog593.ultimatecustomgear.items.weapons.CustomCrossbowItem;
import arrivedbog593.ultimatecustomgear.items.weapons.CustomShieldItem;
import arrivedbog593.ultimatecustomgear.loader.GearRegistry;
import arrivedbog593.ultimatecustomgear.network.CustomGearNetworking;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registers item properties for bow/crossbow animations and shield blocking.
 * Also registers the BEWLR for custom shields so they render their 3D model.
 * <p>
 * Vanilla only registers these properties for Items.BOW, Items.CROSSBOW and
 * Items.SHIELD — mod items are distinct instances and need explicit registration.
 */
public class ClientSetup {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    // Registered via addListener on the MOD bus — no annotation needed
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            int total = GearRegistry.ITEMS.getEntries().size();
            LOGGER.info("[CustomGear] ClientSetup: scanning {} registered items for bow/crossbow/shield properties", total);

            GearRegistry.ITEMS.getEntries().forEach(holder -> {
                if (!holder.isBound()) {
                    LOGGER.warn("[CustomGear] ClientSetup: unbound holder {}", holder.getId());
                    return;
                }
                Item item = holder.get();
                LOGGER.debug("[CustomGear] ClientSetup: checking {} -> {}", holder.getId(), item.getClass().getSimpleName());

                switch (item) {
                    case CustomBowItem ignored -> {
                        LOGGER.info("[CustomGear] ClientSetup: registering bow properties for {}", holder.getId());
                        registerBowProperties(item);
                    }
                    case CustomCrossbowItem ignored -> {
                        LOGGER.info("[CustomGear] ClientSetup: registering crossbow properties for {}", holder.getId());
                        registerCrossbowProperties(item);
                    }
                    case CustomShieldItem ignored -> {
                        LOGGER.info("[CustomGear] ClientSetup: registering shield properties for {}", holder.getId());
                        registerShieldProperties(item);
                    }
                    default -> {
                    }
                }
            });
        });
    }

    // Registered via NeoForge.EVENT_BUS.register(ClientSetup.class) — GAME bus
    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (CustomGearNetworking.pendingMismatchWarning) {
            CustomGearNetworking.pendingMismatchWarning = false;
            event.getPlayer().displayClientMessage(
                    Component.translatable("customgear.network.hash_mismatch_warn"), false);
        }
    }

    // ── Bow ──────────────────────────────────────────────────────────────────

    private static void registerBowProperties(Item item) {
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null || entity.getUseItem() != stack) return 0.0F;
                    int duration = stack.getUseDuration(entity);
                    int remaining = entity.getUseItemRemainingTicks();
                    int elapsed = duration - remaining;
                    // Vanilla bow considers 20 ticks = full draw at chargeSpeed 1.0.
                    // Scale by chargeSpeed: fullDrawTicks = 20 / (duration / 72000).
                    float fullDrawTicks = 20.0f / (duration / 72000.0f);
                    return Math.min(elapsed / fullDrawTicks, 1.0f);
                });

        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                                ? 1.0F : 0.0F);
    }

    // ── Crossbow ──────────────────────────────────────────────────────────────

    private static void registerCrossbowProperties(Item item) {
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                                && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null || CrossbowItem.isCharged(stack)) return 0.0F;
                    int duration = stack.getUseDuration(entity);
                    int remaining = entity.getUseItemRemainingTicks();
                    float elapsed = (float)(duration - remaining);
                    return Math.min(elapsed / (float) duration, 1.0f);
                });

        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("charged"),
                (stack, level, entity, seed) ->
                        CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("firework"),
                (stack, level, entity, seed) -> {
                    ChargedProjectiles charged = stack.getOrDefault(
                            DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
                    return !charged.isEmpty() && charged.contains(Items.FIREWORK_ROCKET)
                            ? 1.0F : 0.0F;
                });
    }

    // ── Shield ────────────────────────────────────────────────────────────────

    private static void registerShieldProperties(Item item) {
        // Register the blocking predicate so the model switches to shield_blocking
        // when the player right-clicks to block.
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("blocking"),
                (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                                ? 1.0F : 0.0F);
    }
}