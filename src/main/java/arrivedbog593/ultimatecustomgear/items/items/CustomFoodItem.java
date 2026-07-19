package arrivedbog593.ultimatecustomgear.items.items;

import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.loader.ItemRegistry;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
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
        super(buildProps(data));
        this.itemData = data;
    }

    private static Item.Properties buildProps(ItemData data) {
        Item.Properties p = new Item.Properties().food(buildFoodProperties(data));
        return data.fireResistant ? p.fireResistant() : p;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(CustomItem.resolveName(itemData));
    }

    // ── Food properties builder ───────────────────────────────────────────────
    @SuppressWarnings("deprecation")
    private static FoodProperties buildFoodProperties(ItemData data) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(data.nutrition)
                .saturationModifier(data.saturation);

        if (data.alwaysEdible) builder.alwaysEdible();
        if (data.fastFood)     builder.fast();

        // Effects are NOT baked here: they are applied from live data in
        // finishUsingItem so /customgear reload can change them without a restart
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
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        ItemData data = liveData();
        if (data.eatDuration >= 0) return (int)(data.eatDuration * 20);
        return data.fastFood ? 16 : 32;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        ItemData live = ItemRegistry.ITEM_MAP.get(BuiltInRegistries.ITEM.getKey(this));
        ItemData data = live != null ? live : itemData;
        TooltipHelper.addFoodEffectsTooltip(tooltipComponents, data);
        TooltipHelper.appendMobDrops(data, tooltipComponents);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack,
                                              @NotNull net.minecraft.world.level.Level level,
                                              @NotNull net.minecraft.world.entity.LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide()) {
            applyEatEffects(entity, liveData());
        }
        return result;
    }

    /** Live ItemData lookup so reload-time changes take effect immediately. */
    private ItemData liveData() {
        ItemData live = ItemRegistry.ITEM_MAP.get(BuiltInRegistries.ITEM.getKey(this));
        return live != null ? live : itemData;
    }

    private static void applyEatEffects(net.minecraft.world.entity.LivingEntity entity, ItemData data) {
        if (data.onEatEffects == null) return;
        for (ItemData.FoodEffectData ed : data.onEatEffects) {
            if (ed.probability < 1.0f && entity.getRandom().nextFloat() >= ed.probability) continue;
            MobEffectInstance instance = resolveEffect(ed);
            if (instance != null) entity.addEffect(instance);
        }
    }
}