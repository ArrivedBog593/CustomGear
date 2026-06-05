package arrivedbog593.ultimatecustomgear.util;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class EffectUtils {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    /**
     * Aplica un conjunto de efectos a un jugador.
     */
    public static void applyEffects(Player player, List<GearData.EffectData> effects) {
        if (effects == null) return;

        for (GearData.EffectData effectData : effects) {
            ResourceLocation rl;
            try {
                rl = ResourceLocation.parse(effectData.effect);
            } catch (Exception e) {
                LOGGER.error("[CustomGear] Malformed effect ID skipped: {}", effectData.effect);
                continue;
            }
            Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT
                    .getHolder(rl)
                    .orElse(null);

            if (effectHolder == null) {
                LOGGER.error("[CustomGear] Effect not found: {}", effectData.effect);
                continue;
            }

            // Check if the effect is already active with the correct amplifier and infinite duration to avoid calling addEffect() every tick unnecessarily
            MobEffectInstance existing = player.getEffect(effectHolder);
            if (existing != null
                    && existing.getDuration() == -1
                    && existing.getAmplifier() == effectData.amplifier) {
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

    /**
     * Remueve un conjunto de efectos de un jugador.
     */
    public static void removeEffects(Player player, List<GearData.EffectData> effects) {
        if (effects == null) return;

        for (GearData.EffectData effectData : effects) {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT
                    .getHolder(rl)
                    .orElse(null);

            if (effectHolder == null) continue;
            player.removeEffect(effectHolder);
        }
    }
}