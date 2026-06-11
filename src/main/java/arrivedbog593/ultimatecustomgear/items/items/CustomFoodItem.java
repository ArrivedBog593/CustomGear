package arrivedbog593.ultimatecustomgear.items.items;

import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Consumable item built from an ItemData JSON with the type "food".
 * <p>
 * Supports:
 *   - nutrition and saturation
 *   - always_edible (can eat when full)
 *   - fast_food (eaten faster, like dried kelp)
 *   - on_eat_effects: list of effects applied on consumption with probability
 * <p>
 * Example JSON:
 * {
 *   "id": "magic_apple",
 *   "type": "food",
 *   "nutrition": 4,
 *   "saturation": 1.2,
 *   "always_edible": true,
 *   "on_eat_effects": [
 *     { "effect": "minecraft:regeneration", "amplifier": 1, "duration": 10, "probability": 1.0 },
 *     { "effect": "minecraft:absorption",   "amplifier": 0, "duration": 120, "probability": 1.0 }
 *   ]
 * }
 */
public class CustomFoodItem extends Item {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    private final ItemData itemData;

    public CustomFoodItem(ItemData data) {
        super(new Item.Properties().food(buildFoodProperties(data)));
        this.itemData = data;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(CustomItem.resolveName(itemData));
    }

    // ── Food properties builder ───────────────────────────────────────────────

    private static FoodProperties buildFoodProperties(ItemData data) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(data.nutrition)
                .saturationModifier(data.saturation);

        if (data.alwaysEdible) builder.alwaysEdible();
        if (data.fastFood)     builder.fast();

        if (data.onEatEffects != null) {
            for (ItemData.FoodEffectData effectData : data.onEatEffects) {
                MobEffectInstance instance = resolveEffect(effectData);
                if (instance != null) {
                    builder.effect(instance, effectData.probability);
                }
            }
        }

        return builder.build();
    }

    /**
     * Resolves a FoodEffectData to a MobEffectInstance.
     * Returns null and logs a warning if the effect ID is not found in the registry.
     */
    private static MobEffectInstance resolveEffect(ItemData.FoodEffectData effectData) {
        if (effectData.effect == null || effectData.effect.isBlank()) {
            LOGGER.warn("[CustomGear] Food effect has empty effect ID — skipping");
            return null;
        }
        try {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            Optional<Holder.Reference<MobEffect>> holder =
                    BuiltInRegistries.MOB_EFFECT.getHolder(rl);
            if (holder.isEmpty()) {
                LOGGER.warn("[CustomGear] Food effect not found in registry: '{}' — skipping",
                        effectData.effect);
                return null;
            }
            int durationTicks = effectData.duration * 20;
            return new MobEffectInstance(holder.get(), durationTicks, effectData.amplifier);
        } catch (Exception e) {
            LOGGER.warn("[CustomGear] Invalid food effect ID '{}': {} — skipping",
                    effectData.effect, e.getMessage());
            return null;
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull net.minecraft.world.entity.LivingEntity entity) {
        if (itemData.eatDuration >= 0) {
            return (int)(itemData.eatDuration * 20); // seconds to ticks
        }
        return itemData.fastFood ? 16 : 32;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        TooltipHelper.addFoodEffectsTooltip(tooltipComponents, itemData);
    }
}