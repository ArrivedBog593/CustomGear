package com.github.arrivedbog593.client;

import com.github.arrivedbog593.items.weapons.CustomBowItem;
import com.github.arrivedbog593.items.weapons.CustomCrossbowItem;
import com.github.arrivedbog593.items.weapons.CustomShieldItem;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registers the necessary item properties so that the bow (draw) and crossbow (load) animations work on mod items.
 * <p>
 * Vanilla only registers "pull", "pulling", "charged" and "firework"
 * for Items.BOW and Items.CROSSBOW — mod items are distinct instances
 * and need to be registered explicitly.
 * <p>
 * It is intentionally kept as @EventBusSubscriber with Dist.CLIENT:
 * ItemProperties is client code and should not be loaded on the server.
 */
public class ClientSetup {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            int total = GearRegistry.ITEMS.getEntries().size();
            LOGGER.info("[CustomGear] ClientSetup: scanning {} registered items for bow/crossbow properties", total);

            GearRegistry.ITEMS.getEntries().forEach(holder -> {
                if (!holder.isBound()) {
                    LOGGER.warn("[CustomGear] ClientSetup: unbound holder {}", holder.getId());
                    return;
                }
                Item item = holder.get();
                LOGGER.debug("[CustomGear] ClientSetup: checking {} -> {}", holder.getId(), item.getClass().getSimpleName());
                if (item instanceof CustomBowItem) {
                    LOGGER.info("[CustomGear] ClientSetup: registering bow properties for {}", holder.getId());
                    registerBowProperties(item);
                } else if (item instanceof CustomCrossbowItem) {
                    LOGGER.info("[CustomGear] ClientSetup: registering crossbow properties for {}", holder.getId());
                    registerCrossbowProperties(item);
                }
            });
        });
        GearRegistry.ITEMS.getEntries().forEach(holder -> {
            if (!holder.isBound()) return;
            Item item = holder.get();
            if (item instanceof CustomShieldItem) {
                // Register the shield model so BEWLR can find it
                net.minecraft.client.renderer.item.ItemProperties.register(
                        item,
                        ResourceLocation.withDefaultNamespace("blocking"),
                        (stack, level, entity, seed) ->
                                entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                                        ? 1.0F : 0.0F
                );
            }
        });
    }

    // ── Bow ─────────────────────────────────────────────────────────────────

    private static void registerBowProperties(Item item) {
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null || entity.getUseItem() != stack) return 0.0F;
                    int duration = stack.getUseDuration(entity); // 72000 / chargeSpeed
                    int remaining = entity.getUseItemRemainingTicks();
                    int elapsed = duration - remaining;
                    float fullDrawTicks = 20.0f / (duration / 72000.0f);
                    return Math.min(elapsed / fullDrawTicks, 1.0f);
                });

        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                                ? 1.0F : 0.0F);
    }

    // ── Crossbow ─────────────────────────────────────────────────────────────

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
}
