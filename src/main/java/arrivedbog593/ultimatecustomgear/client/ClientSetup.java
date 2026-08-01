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
                    int elapsed = stack.getUseDuration(entity) - entity.getUseItemRemainingTicks();
                    // CustomBowItem.getUseDuration returns 72000 / chargeSpeed, so a
                    // SLOWER bow has a LONGER duration. The draw window has to scale
                    // the same way, hence multiply.
                    //
                    // This was a division. At chargeSpeed 1.0 both forms give 20, so
                    // the bug was invisible at the default and only showed up at any
                    // other value — chargeSpeed 0.25 drew 16x too fast instead of 4x
                    // too slow, skipping bow_pulling_0 entirely.
                    float fullDrawTicks = 20.0f * (duration / 72000.0f);
                    if (fullDrawTicks <= 0.0f) return 1.0F;
                    return Math.min(elapsed / 20.0f, 1.0f);
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
                    if (entity == null || entity.getUseItem() != stack) return 0.0F;
                    if (CrossbowItem.isCharged(stack)) return 0.0F;
                    int elapsed = stack.getUseDuration(entity) - entity.getUseItemRemainingTicks();
                    // Divide by the CHARGE duration, not the USE duration. Vanilla
                    // decides "is it loaded" via getPowerForTime, which divides by
                    // getChargeDuration (25 ticks). getUseDuration is 25/chargeSpeed+3,
                    // which at chargeSpeed 0.25 is 103 — so the animation ran ~4x
                    // behind the real charge and the crossbow could be fired before
                    // crossbow_pulling_1 had ever appeared.
                    int chargeTicks = CrossbowItem.getChargeDuration(stack, entity);
                    if (chargeTicks <= 0) return 1.0F;
                    return Math.min((float) elapsed / (float) chargeTicks, 1.0f);
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