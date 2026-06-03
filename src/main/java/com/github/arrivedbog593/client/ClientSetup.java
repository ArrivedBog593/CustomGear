package com.github.arrivedbog593.client;

import com.github.arrivedbog593.items.weapons.CustomBowItem;
import com.github.arrivedbog593.items.weapons.CustomCrossbowItem;
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

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> GearRegistry.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            if (item instanceof CustomBowItem) {
                registerBowProperties(item);
            } else if (item instanceof CustomCrossbowItem) {
                registerCrossbowProperties(item);
            }
        }));
    }

    // ── Bow ─────────────────────────────────────────────────────────────────

    private static void registerBowProperties(Item item) {
        // "pull" — normalized tension progress from 0-1 in 20 ticks
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    return entity.getUseItem() != stack ? 0.0F :
                            (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
                });

        // "pulling" — 1.0 while the player is tensing
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                                ? 1.0F : 0.0F);
    }

    // ── Crossbow ─────────────────────────────────────────────────────────────

    private static void registerCrossbowProperties(Item item) {
        // "pulling" — 1.0 while loading and not ready yet
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                                && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

        // "pull" — normalized loading progress from 0-1 in 25 ticks (base time)
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null || CrossbowItem.isCharged(stack)) return 0.0F;
                    float elapsed = (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks());
                    return Math.min(elapsed / 25.0f, 1.0f);
                });

        // "charged" — 1.0 when it has a loaded projectile
        ItemProperties.register(item,
                ResourceLocation.withDefaultNamespace("charged"),
                (stack, level, entity, seed) ->
                        CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

        // "firework" — 1.0 when the loaded projectile is a rocket
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
