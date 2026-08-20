package arrivedbog593.ultimatecustomgear.items.fluids;

import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * LiquidBlock that applies the fluid's contact behavior (fire and effects)
 * to EVERY entity touching it — players, mobs, dropped items, projectiles —
 * the same way vanilla lava and fire work.
 * <p>
 * This replaces the old FluidContactHandler, which ran on PlayerTickEvent
 * and therefore only ever affected players. Using {@code entityInside} means:
 * <ul>
 *   <li>Vanilla calls us automatically for each entity whose bounding box
 *       intersects the block — no per-tick scanning, no per-player state,
 *       no logout cleanup. Completely stateless.</li>
 *   <li>Dropped items in a burning fluid catch fire and are destroyed,
 *       like in lava. Mobs receive fire and contact effects.</li>
 *   <li>Both source and flowing blocks trigger it (same block instance).</li>
 * </ul>
 * FluidData is looked up per call through FLUID_MAP (never cached here), so
 * /customgear reload changes to contact effects apply immediately.
 * <p>
 * DoT note: effects are only (re)applied when the current instance is close
 * to expiring. Re-adding every tick would reset the duration and prevent
 * poison/wither from ever crossing their internal damage thresholds
 * (multiples of 25/40 remaining ticks) — the bug we already fixed once in
 * the old handler; the fix carries over here.
 */
public class CustomLiquidBlock extends LiquidBlock {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    /** FLUID_MAP key for this fluid ("customgear:<id>"), fixed at registration. */
    private final ResourceLocation fluidId;

    public CustomLiquidBlock(FlowingFluid fluid, Properties properties, String dataId) {
        super(fluid, properties);
        this.fluidId = ResourceLocation.fromNamespaceAndPath("customgear", dataId);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level,
                             @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!level.isClientSide()) {
            FluidData data = FluidRegistry.FLUID_MAP.get(fluidId);
            if (data != null) {
                applyContactBehavior(data, entity);
            }
        }
        super.entityInside(state, level, pos, entity);
    }

    private static void applyContactBehavior(FluidData data, Entity entity) {
        // ── Fire: affects EVERY entity type, like lava ──────────────────────
        if (data.burnsEntities) {
            if (!entity.fireImmune()) {
                // max(): don't shorten an existing longer burn
                entity.setRemainingFireTicks(
                        Math.max(entity.getRemainingFireTicks(), data.burnDuration * 20));
            }
        } else {
            // Non-burning fluids extinguish, like water (feature since 1.2.5)
            entity.clearFire();
        }

        // ── Contact effects: living entities only (items can't have effects) ─
        if (data.contactEffects == null || data.contactEffects.isEmpty()) return;
        if (!(entity instanceof LivingEntity living)) return;

        int intervalTicks = Math.max(1, (int) (data.contactEffectInterval * 20));
        for (FluidData.ContactEffectData effectData : data.contactEffects) {
            applyEffect(living, effectData, intervalTicks);
        }
    }

    private static void applyEffect(LivingEntity entity, FluidData.ContactEffectData effectData,
                                    int intervalTicks) {
        if (effectData.effect == null || effectData.effect.isBlank()) return;
        try {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Optional<Holder.Reference<MobEffect>> holder =
                    BuiltInRegistries.MOB_EFFECT.getHolder(rl);
            if (holder.isEmpty()) {
                LOGGER.warn("[CustomGear] Contact effect not found: '{}'", effectData.effect);
                return;
            }

            // Only refresh when close to expiring — lets the duration decay
            // through poison/wither damage thresholds in between (see class doc)
            MobEffectInstance existing = entity.getEffect(holder.get());
            if (existing != null
                    && existing.getAmplifier() == effectData.amplifier
                    && existing.getDuration() > intervalTicks + 10) {
                return;
            }

            int durationTicks = effectData.duration * 20;
            entity.addEffect(new MobEffectInstance(
                    holder.get(), durationTicks, effectData.amplifier, false, true));
        } catch (Exception e) {
            LOGGER.warn("[CustomGear] Invalid contact effect ID '{}': {}",
                    effectData.effect, e.getMessage());
        }
    }
}