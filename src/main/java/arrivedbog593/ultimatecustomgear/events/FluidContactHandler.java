package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.loader.FluidRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Applies contact effects and fire when a player is submerged
 * in a custom fluid defined via JSON.
 * <p>
 * Uses PlayerTickEvent.Post which fires every tick for every player.
 * Effects are only applied every contact_effect_interval ticks to avoid spam.
 * The tick counter is tracked per-player per-fluid and cleared when leaving.
 */
public class FluidContactHandler {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    /**
     * Tick counter per player per fluid.
     * Key: "entityId:fluidId", Value: tick count
     */
    private static final Map<String, Integer> TICK_COUNTERS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // Only process on server side
        if (player.level().isClientSide()) return;

        for (Map.Entry<ResourceLocation, FluidData> entry : FluidRegistry.FLUID_MAP.entrySet()) {
            ResourceLocation fluidLoc = entry.getKey();
            FluidData data = entry.getValue();

            // Skip fluids with no contact behavior
            if ((data.contactEffects == null || data.contactEffects.isEmpty())
                    && !data.burnsEntities) {
                TICK_COUNTERS.remove(key(player, fluidLoc));
                continue;
            }

            // Get the registered source fluid
            Optional<Fluid> fluidOpt = BuiltInRegistries.FLUID.getOptional(fluidLoc);
            if (fluidOpt.isEmpty()) continue;

            Fluid fluid = fluidOpt.get();
            String key = key(player, fluidLoc);

            if (player.isInFluidType(fluid.getFluidType())) {
                int ticks = TICK_COUNTERS.getOrDefault(key, 0) + 1;
                TICK_COUNTERS.put(key, ticks);

                // Apply fire damage if configured
                if (data.burnsEntities) {
                    player.setRemainingFireTicks(data.burnDuration * 20);
                }

                // Apply contact effects at the configured interval
                int interval = Math.max(1, (int)(data.contactEffectInterval * 20));
                if (ticks % interval == 0 && data.contactEffects != null) {
                    for (FluidData.ContactEffectData effectData : data.contactEffects) {
                        applyEffect(player, effectData);
                    }
                }

            } else {
                // Player left the fluid — clear counter
                TICK_COUNTERS.remove(key);
            }
        }
    }

    private static void applyEffect(Player player, FluidData.ContactEffectData effectData) {
        if (effectData.effect == null || effectData.effect.isBlank()) return;
        try {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Optional<Holder.Reference<MobEffect>> holder =
                    BuiltInRegistries.MOB_EFFECT.getHolder(rl);
            if (holder.isEmpty()) {
                LOGGER.warn("[CustomGear] Contact effect not found: '{}'", effectData.effect);
                return;
            }
            int durationTicks = effectData.duration * 20;
            player.addEffect(new MobEffectInstance(
                    holder.get(), durationTicks, effectData.amplifier, false, true));
        } catch (Exception e) {
            LOGGER.warn("[CustomGear] Invalid contact effect ID '{}': {}",
                    effectData.effect, e.getMessage());
        }
    }

    private static String key(Player player, ResourceLocation fluidLoc) {
        return player.getId() + ":" + fluidLoc;
    }
}