package arrivedbog593.customgear.client;

import arrivedbog593.customgear.items.weapons.CustomBowItem;
import arrivedbog593.customgear.items.weapons.CustomCrossbowItem;
import arrivedbog593.customgear.items.weapons.CustomShieldItem;
import arrivedbog593.customgear.platform.Services;
import arrivedbog593.customgear.registry.GearRegistry;
import arrivedbog593.customgear.network.CustomGearNetworking;
import arrivedbog593.customgear.network.OpenBackpackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Client behavior shared by every loader: item properties for bow/crossbow
 * animations and shield blocking, the open-backpack key and the WARN-mode chat
 * warning. What each loader registers in its own way (screens, renderers,
 * model layers, tooltips) lives in its client entry point.
 * <p>
 * Vanilla only registers these properties for Items.BOW, Items.CROSSBOW and
 * Items.SHIELD — mod items are distinct instances and need explicit registration.
 */
public class ClientSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger("CustomGear");

    /**
     * Registers the item properties. Called by each loader during client setup,
     * on the render thread: ItemProperties is not thread-safe.
     */
    public static void registerItemProperties() {
        int total = GearRegistry.ITEMS.getEntries().size();
        LOGGER.info("[CustomGear] ClientSetup: scanning {} registered items for bow/crossbow/shield properties", total);

        GearRegistry.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();

            switch (item) {
                case CustomBowItem ignored -> {
                    LOGGER.debug("[CustomGear] ClientSetup: registering bow properties for {}", holder.getId());
                    registerBowProperties(item);
                }
                case CustomCrossbowItem ignored -> {
                    LOGGER.debug("[CustomGear] ClientSetup: registering crossbow properties for {}", holder.getId());
                    registerCrossbowProperties(item);
                }
                case CustomShieldItem ignored -> {
                    LOGGER.debug("[CustomGear] ClientSetup: registering shield properties for {}", holder.getId());
                    registerShieldProperties(item);
                }
                default -> {
                }
            }
        });
    }

    /** Called by each loader once the local player has joined a world. */
    public static void onClientJoin(LocalPlayer player) {
        Component warning = CustomGearNetworking.takeMismatchWarning();
        if (warning != null) player.displayClientMessage(warning, false);
    }

    // ── Bow ──────────────────────────────────────────────────────────────────

    private static void registerBowProperties(Item item) {
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null || entity.getUseItem() != stack) return 0.0F;
                    int duration = stack.getUseDuration(entity);
                    int elapsed  = duration - entity.getUseItemRemainingTicks();
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

    // ── Keys ──────────────────────────────────────────────────────────────────

    /**
     * Called by each loader at the end of every client tick. Acts outside any
     * screen only: inside one the container screen handles its own bindings,
     * and opening a backpack over an open chest would be confusing.
     */
    public static void onClientTick(Minecraft mc) {
        if (mc.screen != null || mc.player == null) return;
        while (CustomGearKeys.OPEN_BACKPACK.consumeClick()) {
            Services.NETWORK.sendToServer(new OpenBackpackPayload());
        }
    }
}
