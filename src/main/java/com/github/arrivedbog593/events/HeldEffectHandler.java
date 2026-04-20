package com.github.arrivedbog593.events;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.CustomArmorItem;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.world.item.Item;
import net.minecraft.core.Holder;

import java.util.*;

@EventBusSubscriber(modid = "customgear")
public class HeldEffectHandler {

    // Tracks which items are currently held per player (main/off hand)
    private static final Map<UUID, ResourceLocation> lastMainHand = new HashMap<>();
    private static final Map<UUID, ResourceLocation> lastOffHand = new HashMap<>();

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
        ItemStack offStack = player.getOffhandItem();

        ResourceLocation currentMain = getItemId(mainStack);
        ResourceLocation currentOff = getItemId(offStack);

        ResourceLocation prevMain = lastMainHand.get(id);
        ResourceLocation prevOff = lastOffHand.get(id);

        // If main hand item changed, remove effects from previous item
        if (prevMain != null && !prevMain.equals(currentMain)) {
            GearData prevData = GearRegistry.GEAR_MAP.get(prevMain);
            if (prevData != null && prevData.heldEffects != null) {
                removeEffects(player, prevData.heldEffects);
            }
        }

        // If off hand item changed, remove effects from previous item
        if (prevOff != null && !prevOff.equals(currentOff)) {
            GearData prevData = GearRegistry.GEAR_MAP.get(prevOff);
            if (prevData != null && prevData.heldEffects != null) {
                removeEffects(player, prevData.heldEffects);
            }
        }

        // Apply effects from current main hand item
        checkAndApply(player, mainStack);
        checkAndApply(player, offStack);

        // Update tracking
        lastMainHand.put(id, currentMain);
        lastOffHand.put(id, currentOff);
    }

    private static void checkAndApply(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;

        List<GearData.EffectData> effects = getHeldEffects(stack);
        if (effects == null || effects.isEmpty()) return;

        for (GearData.EffectData effectData : effects) {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT
                    .getHolder(rl)
                    .orElse(null);

            if (effectHolder == null) {
                System.err.println("[CustomGear] Effect not found: " + effectData.effect);
                continue;
            }

            player.addEffect(new MobEffectInstance(
                    effectHolder,
                    -1,    // infinite duration
                    effectData.amplifier,
                    true,  // ambient
                    false  // no particles
            ));
        }
    }

    private static void removeEffects(Player player, List<GearData.EffectData> effects) {
        for (GearData.EffectData effectData : effects) {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT
                    .getHolder(rl)
                    .orElse(null);
            if (effectHolder == null) continue;
            player.removeEffect(effectHolder);
        }
    }

    private static ResourceLocation getItemId(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    private static List<GearData.EffectData> getHeldEffects(ItemStack stack) {
        if (stack.isEmpty()) return null;

        Item item = stack.getItem();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        GearData data = GearRegistry.GEAR_MAP.get(id);

        if (data != null) {
            if (item instanceof CustomArmorItem) {
                return null;
            }
            return data.heldEffects;
        }

        return null;
    }
}