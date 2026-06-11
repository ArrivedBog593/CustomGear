package arrivedbog593.ultimatecustomgear.util;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.Holder;
import net.minecraft.ChatFormatting;

import java.util.List;

public class TooltipHelper {

    // Tooltip for held effects (weapons and tools)
    public static void addHeldEffectsTooltip(List<Component> tooltipComponents,
                                             GearData data) {
        if (data.heldEffects == null || data.heldEffects.isEmpty()) return;

        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("tooltip.customgear.held_effects")
                .withStyle(ChatFormatting.GOLD));

        for (GearData.EffectData effectData : data.heldEffects) {
            String effectName = getEffectName(effectData.effect);
            String amplifier = toRoman(effectData.amplifier + 1);
            tooltipComponents.add(Component.literal("• " + effectName + " " + amplifier)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // Tooltip for individual piece effects (armor)
    public static void addPieceEffectsTooltip(List<Component> tooltipComponents,
                                              GearData data, String piece) {
        if (data.pieceEffects == null) return;
        List<GearData.EffectData> effects = data.pieceEffects.get(piece);
        if (effects == null || effects.isEmpty()) return;

        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("tooltip.customgear.piece_effects")
                .withStyle(ChatFormatting.GOLD));

        for (GearData.EffectData effectData : effects) {
            String effectName = getEffectName(effectData.effect);
            String amplifier = toRoman(effectData.amplifier + 1);
            tooltipComponents.add(Component.literal("• " + effectName + " " + amplifier)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // Tooltip for set bonus (armor)
    public static void addSetBonusTooltip(List<Component> tooltipComponents,
                                          GearData data) {
        if (data.setBonus == null || data.setBonus.effects == null) return;

        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable(
                        "tooltip.customgear.set_bonus",
                        data.setBonus.requiredPieces)
                .withStyle(ChatFormatting.GOLD));

        for (GearData.EffectData effectData : data.setBonus.effects) {
            String effectName = getEffectName(effectData.effect);
            String amplifier = toRoman(effectData.amplifier + 1);
            tooltipComponents.add(Component.literal("• " + effectName + " " + amplifier)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // Obtains the effect name from the Minecraft registry
    private static String getEffectName(String effectId) {
        try {
            ResourceLocation rl = ResourceLocation.parse(effectId);
            Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT
                    .getHolder(rl).orElse(null);
            if (holder != null) {
                return Component.translatable(
                        holder.value().getDescriptionId()).getString();
            }
        } catch (Exception ignored) {}
        return effectId;
    }

    // Converts a number to Roman numerals (I, II, III, IV, V...)
    public static String toRoman(int number) {
        return switch (number) {
            case 1  -> "I";
            case 2  -> "II";
            case 3  -> "III";
            case 4  -> "IV";
            case 5  -> "V";
            case 6  -> "VI";
            case 7  -> "VII";
            case 8  -> "VIII";
            case 9  -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }

    // Tooltip for till radius (hoes)
    public static void addTillRadiusTooltip(List<Component> tooltipComponents,
                                            GearData data) {
        if (data.tillRadius <= 0) return;

        int diameter = 2 * data.tillRadius + 1;

        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("tooltip.customgear.till_radius")
                .withStyle(ChatFormatting.GOLD));

        tooltipComponents.add(Component.literal("• " + data.tillRadius + " (")
                .append(Component.translatable("tooltip.customgear.till_area", diameter, diameter))
                .append(Component.literal(")"))
                .withStyle(ChatFormatting.GRAY));
    }

    // Tooltip for food on-eat effects
    public static void addFoodEffectsTooltip(List<Component> tooltipComponents,
                                             arrivedbog593.ultimatecustomgear.data.ItemData data) {
        // Eat duration line
        if (data.eatDuration == 0) {
            tooltipComponents.add(Component.translatable("tooltip.ultimatecustomgear.instant")
                    .withStyle(ChatFormatting.GRAY));
        } else if (data.eatDuration > 0) {
            float seconds = data.eatDuration;
            tooltipComponents.add(Component.translatable("tooltip.ultimatecustomgear.eat_duration",
                            String.format("%.1f", seconds))
                    .withStyle(ChatFormatting.GRAY));
        }

        // On eat effects
        if (data.onEatEffects == null || data.onEatEffects.isEmpty()) return;

        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("tooltip.ultimatecustomgear.on_eat")
                .withStyle(ChatFormatting.GOLD));

        for (arrivedbog593.ultimatecustomgear.data.ItemData.FoodEffectData ed : data.onEatEffects) {
            String effectName = getEffectName(ed.effect);
            String level      = ed.amplifier > 0 ? " " + toRoman(ed.amplifier + 1) : "";
            String duration   = ed.duration >= 60
                    ? (ed.duration / 60) + "m " + (ed.duration % 60) + "s"
                    : ed.duration + "s";
            tooltipComponents.add(Component.literal("• " + effectName + level + " (" + duration + ")")
                    .withStyle(ChatFormatting.BLUE));
        }
    }
}