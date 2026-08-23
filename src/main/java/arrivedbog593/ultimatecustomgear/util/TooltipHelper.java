package arrivedbog593.ultimatecustomgear.util;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.data.ItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TooltipHelper {

    /** Maximum entries shown per resistance section before "…and N more". */
    private static final int MAX_RESIST_ENTRIES = 4;

    // Tooltip for held effects (weapons and tools)
    public static void addHeldEffectsTooltip(Consumer<Component> tooltipComponents,
                                             GearData data) {
        if (!detailsShown()) return;
        if (data.heldEffects == null || data.heldEffects.isEmpty()) return;

        tooltipComponents.accept(Component.literal(""));
        tooltipComponents.accept(Component.translatable("tooltip.customgear.held_effects")
                .withStyle(ChatFormatting.GOLD));

        for (GearData.EffectData effectData : data.heldEffects) {
            String effectName = getEffectName(effectData.effect);
            String amplifier = toRoman(effectData.amplifier + 1);
            tooltipComponents.accept(Component.literal("• " + effectName + " " + amplifier)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // Tooltip for individual piece effects (armor)
    public static void addPieceEffectsTooltip(Consumer<Component> tooltipComponents,
                                              GearData data, String piece) {
        if (!detailsShown()) return;
        if (data.pieceEffects == null) return;
        List<GearData.EffectData> effects = data.pieceEffects.get(piece);
        if (effects == null || effects.isEmpty()) return;

        tooltipComponents.accept(Component.literal(""));
        tooltipComponents.accept(Component.translatable("tooltip.customgear.piece_effects")
                .withStyle(ChatFormatting.GOLD));

        for (GearData.EffectData effectData : effects) {
            String effectName = getEffectName(effectData.effect);
            String amplifier = toRoman(effectData.amplifier + 1);
            tooltipComponents.accept(Component.literal("• " + effectName + " " + amplifier)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // Tooltip for set bonus (armor)
    public static void addSetBonusTooltip(Consumer<Component> tooltipComponents,
                                          GearData data) {
        if (!detailsShown()) return;
        if (data.setBonus == null || data.setBonus.effects == null) return;

        tooltipComponents.accept(Component.literal(""));
        tooltipComponents.accept(Component.translatable(
                        "tooltip.customgear.set_bonus",
                        data.setBonus.requiredPieces)
                .withStyle(ChatFormatting.GOLD));

        for (GearData.EffectData effectData : data.setBonus.effects) {
            String effectName = getEffectName(effectData.effect);
            String amplifier = toRoman(effectData.amplifier + 1);
            tooltipComponents.accept(Component.literal("• " + effectName + " " + amplifier)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // Obtains the effect name from the Minecraft registry
    private static String getEffectName(String effectId) {
        try {
            Identifier rl = Identifier.parse(effectId);
            Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT
                    .get(rl).orElse(null);
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
    public static void addTillRadiusTooltip(Consumer<Component> tooltipComponents,
                                            GearData data) {
        if (data.tillRadius <= 0) return;

        int diameter = 2 * data.tillRadius + 1;

        tooltipComponents.accept(Component.literal(""));
        tooltipComponents.accept(Component.translatable("tooltip.customgear.till_radius")
                .withStyle(ChatFormatting.GOLD));

        tooltipComponents.accept(Component.literal("• " + data.tillRadius + " (")
                .append(Component.translatable("tooltip.customgear.till_area", diameter, diameter))
                .append(Component.literal(")"))
                .withStyle(ChatFormatting.GRAY));
    }

    // Tooltip for food on-eat effects
    public static void addFoodEffectsTooltip(Consumer<Component> tooltipComponents,
                                             arrivedbog593.ultimatecustomgear.data.ItemData data) {
        // Eat duration line
        if (data.eatDuration == 0) {
            tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.instant")
                    .withStyle(ChatFormatting.GRAY));
        } else if (data.eatDuration > 0) {
            float seconds = data.eatDuration;
            tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.eat_duration",
                            String.format("%.1f", seconds))
                    .withStyle(ChatFormatting.GRAY));
        }

        // On eat effects
        if (data.onEatEffects == null || data.onEatEffects.isEmpty()) return;

        tooltipComponents.accept(Component.literal(""));
        tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.on_eat")
                .withStyle(ChatFormatting.GOLD));

        for (arrivedbog593.ultimatecustomgear.data.ItemData.FoodEffectData ed : data.onEatEffects) {
            String effectName = getEffectName(ed.effect);
            String level      = ed.amplifier > 0 ? " " + toRoman(ed.amplifier + 1) : "";
            String duration   = ed.duration >= 60
                    ? (ed.duration / 60) + "m " + (ed.duration % 60) + "s"
                    : ed.duration + "s";
            tooltipComponents.accept(Component.literal("• " + effectName + level + " (" + duration + ")")
                    .withStyle(ChatFormatting.BLUE));
        }
    }

    // Tooltip for bow/crossbow stats
    public static void addBowTooltip(Consumer<Component> tooltipComponents, GearData data) {
        tooltipComponents.accept(Component.literal(""));

        if (data.arrowDamage > 0) {
            tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.arrow_damage",
                            String.format("%.1f", data.arrowDamage))
                    .withStyle(ChatFormatting.DARK_GREEN));
        }
        if (data.arrowDamageBonus > 0) {
            tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.arrow_damage_bonus",
                            String.format("+%.1f", data.arrowDamageBonus))
                    .withStyle(ChatFormatting.DARK_GREEN));
        }
        if (data.arrowDamageMultiplier > 0 && data.arrowDamageMultiplier != 1.0f) {
            tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.arrow_damage_multiplier",
                            String.format("x%.2f", data.arrowDamageMultiplier))
                    .withStyle(ChatFormatting.DARK_GREEN));
        }
        // charge_speed is NOT shown: it does not affect the real charge time.
        // BowItem.getPowerForTime and CrossbowItem.getChargeDuration are static,
        // so the draw always completes in 20 / 25 ticks no matter what the field
        // says — it only stretches how long the click can be HELD, which is
        // already minutes either way. Showing a "Charge speed: 15.00" line
        // promised something the game never delivered. The field is kept
        // (removing it would break existing JSONs) and reserved for when the
        // real charge time is implemented.
    }

    // Tooltip for tool mining stats (harvest level and mining speed)
    public static void addToolStatsTooltip(Consumer<Component> tooltipComponents, GearData data) {
        tooltipComponents.accept(Component.literal(""));

        Component harvestValue = getHarvestValue(data);

        tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.harvest_level")
                .append(Component.literal(": "))
                .append(harvestValue)
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("tooltip.ultimatecustomgear.mining_speed",
                        String.format("%.1f", data.miningSpeed))
                .withStyle(ChatFormatting.GRAY));
    }

    private static @NotNull Component getHarvestValue(GearData data) {
        String harvestKey = switch (data.harvestLevel) {
            case 0  -> "tooltip.ultimatecustomgear.harvest_wood";
            case 1  -> "tooltip.ultimatecustomgear.harvest_stone";
            case 2  -> "tooltip.ultimatecustomgear.harvest_iron";
            case 3  -> "tooltip.ultimatecustomgear.harvest_diamond";
            case 4  -> "tooltip.ultimatecustomgear.harvest_netherite";
            default -> null;
        };

        return harvestKey != null
                ? Component.translatable(harvestKey)
                : Component.literal(String.valueOf(data.harvestLevel));
    }

    /** Appends the "Dropped by" section for items with mob_drops configured. */
    public static void appendMobDrops(ItemData data, Consumer<Component> tooltip) {
        if (!detailsShown()) return;
        if (data == null || data.mobDrops == null) return;
        ItemData.MobDropsData d = data.mobDrops;
        if (d.entities == null || d.entities.isEmpty()) return; // drop disabled

        // Filter first, so the "…and N more" count never includes entries that
        // were never candidates for display.
        List<String> visible = new ArrayList<>();
        for (String e : d.entities) {
            if (e == null || e.isBlank()) continue;
            visible.add(e);
        }
        if (visible.isEmpty()) return;

        String pct = formatChance(d.chance);
        tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.dropped_by")
                .withStyle(ChatFormatting.GOLD));

        int shown = Math.min(3, visible.size());
        for (int i = 0; i < shown; i++) {
            String e = visible.get(i);
            Component name;
            if (e.equals("all")) {
                name = Component.translatable("tooltip.ultimatecustomgear.dropped_by.any_mob");
            } else if (e.startsWith("#")) {
                name = Component.literal(e); // tag shown as-is
            } else if (e.endsWith(":*")) {
                name = Component.translatable("tooltip.ultimatecustomgear.dropped_by.mod_mobs",
                        e.substring(0, e.length() - 2));
            } else {
                Identifier rl = Identifier.tryParse(e);
                name = (rl != null)
                        ? BuiltInRegistries.ENTITY_TYPE.getOptional(rl)
                        .map(EntityType::getDescription)
                        .orElse(Component.literal(e))
                        : Component.literal(e);
            }
            tooltip.accept(Component.literal("• ").append(name)
                    .append(Component.literal(" (" + pct + ")"))
                    .withStyle(ChatFormatting.GRAY));
        }
        if (visible.size() > shown) {
            tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.dropped_by.more",
                    visible.size() - shown).withStyle(ChatFormatting.DARK_GRAY));
            if (d.isLootingCount()) {
                tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.dropped_by.looting_count")
                        .withStyle(ChatFormatting.DARK_GRAY));
            } else if (d.isLootingChance()) {
                tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.dropped_by.looting_chance")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    /** "Resistances:" section for armor with damage_resistances. */
    public static void addDamageResistancesTooltip(Consumer<Component> tooltip, GearData data, String piece) {
        if (!detailsShown()) return;
        Map<String, Double> res = ResistanceResolver.damageMap(data, piece);
        if (res == null || res.isEmpty()) return;

        List<Map.Entry<String, Double>> visible = new ArrayList<>();
        for (Map.Entry<String, Double> e : res.entrySet()) {
            if (e.getValue() == null || e.getValue() <= 0) continue;
            visible.add(e);
        }
        // Nothing displayable: return before adding the header, so no orphan
        // "Resistances:" line is left behind.
        if (visible.isEmpty()) return;

        tooltip.accept(Component.literal(""));
        tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.resistances")
                .withStyle(ChatFormatting.GOLD));

        int shown = Math.min(MAX_RESIST_ENTRIES, visible.size());
        for (int i = 0; i < shown; i++) {
            Map.Entry<String, Double> e = visible.get(i);
            tooltip.accept(Component.literal("• ")
                    .append(resistanceName(e.getKey()))
                    .append(Component.literal(" " + formatChance(e.getValue())))
                    .withStyle(ChatFormatting.GRAY));
        }
        if (visible.size() > shown) {
            tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.resistances.more",
                    visible.size() - shown).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static Component resistanceName(String key) {
        boolean isTag = key.startsWith("#");
        String clean = isTag ? key.substring(1) : key;
        Identifier rl = Identifier.tryParse(clean);
        if (rl == null) return Component.literal(key);

        // Derived lang key: tooltip.ultimatecustomgear.resist.<namespace>.<path>
        String full = "tooltip.ultimatecustomgear.resist."
                + rl.getNamespace() + "." + rl.getPath().replace('/', '.');

        // Translated if the key exists; prettified path otherwise
        return Component.translatableWithFallback(full, prettify(rl.getPath()));
    }

    private static String prettify(String path) {
        String p = path.substring(path.lastIndexOf('/') + 1);
        if (p.startsWith("is_")) p = p.substring(3);
        p = p.replace('_', ' ');
        return p.isEmpty() ? p : Character.toUpperCase(p.charAt(0)) + p.substring(1);
    }

    /** "Resists attackers:" section — player: entries are hidden on purpose. */
    public static void addAttackerResistancesTooltip(Consumer<Component> tooltip,
                                                     GearData data, String piece) {
        if (!detailsShown()) return;
        Map<String, Double> res = ResistanceResolver.attackerMap(data, piece);
        if (res == null || res.isEmpty()) return;

        // Player-specific entries stay out of the tooltip: they are usually a
        // surprise (event/troll armor) and showing them spoils it. They must not
        // reach the "…and N more" count either, or their existence leaks.
        List<Map.Entry<String, Double>> visible = new ArrayList<>();
        for (Map.Entry<String, Double> e : res.entrySet()) {
            if (e.getValue() == null || e.getValue() <= 0) continue;
            if (!data.showPlayerResistances && e.getKey().startsWith("player:")) continue;
            visible.add(e);
        }
        if (visible.isEmpty()) return;

        tooltip.accept(Component.literal(""));
        tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.attacker_resistances")
                .withStyle(ChatFormatting.GOLD));

        int shown = Math.min(MAX_RESIST_ENTRIES, visible.size());
        for (int i = 0; i < shown; i++) {
            Map.Entry<String, Double> e = visible.get(i);
            tooltip.accept(Component.literal("• ")
                    .append(attackerName(e.getKey()))
                    .append(Component.literal(" " + formatChance(e.getValue())))
                    .withStyle(ChatFormatting.GRAY));
        }
        if (visible.size() > shown) {
            tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.resistances.more",
                    visible.size() - shown).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    /** Entity names come translated from the registry; tags/wildcards as-is. */
    private static Component attackerName(String key) {
        if (key.equals("all")) {
            return Component.translatable("tooltip.ultimatecustomgear.dropped_by.any_mob");
        }
        if (key.endsWith(":*")) {
            return Component.translatable("tooltip.ultimatecustomgear.dropped_by.mod_mobs",
                    key.substring(0, key.length() - 2));
        }
        if (key.startsWith("#")) return Component.literal(key);
        if (key.startsWith("player:")) {
            return Component.literal(key.substring(7));
        }
        Identifier rl = Identifier.tryParse(key);
        return rl != null
                ? BuiltInRegistries.ENTITY_TYPE.getOptional(rl)
                .map(EntityType::getDescription)
                .orElse(Component.literal(key))
                : Component.literal(key);
    }

    /** "Conditional resistances:" section — one line per rule, capped like the rest. */
    public static void addConditionalResistancesTooltip(Consumer<Component> tooltip,
                                                        GearData data, String piece) {
        if (!detailsShown()) return;
        List<GearData.ConditionalResistance> list = ResistanceResolver.conditionalList(data, piece);
        if (list == null || list.isEmpty()) return;

        List<GearData.ConditionalResistance> visible = new ArrayList<>();
        for (GearData.ConditionalResistance c : list) {
            if (c == null || c.amount <= 0) continue;
            if (!data.showPlayerResistances
                    && c.attacker != null && c.attacker.startsWith("player:")) continue;
            boolean hasDamage   = c.damage   != null && !c.damage.isBlank();
            boolean hasAttacker = c.attacker != null && !c.attacker.isBlank();
            if (!hasDamage && !hasAttacker) continue; // malformed rule: nothing to name
            visible.add(c);
        }
        if (visible.isEmpty()) return;

        tooltip.accept(Component.literal(""));
        tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.conditional_resistances")
                .withStyle(ChatFormatting.GOLD));

        int shown = Math.min(MAX_RESIST_ENTRIES, visible.size());
        for (int i = 0; i < shown; i++) {
            GearData.ConditionalResistance c = visible.get(i);
            Component what = (c.damage != null && !c.damage.isBlank())
                    ? resistanceName(c.damage) : null;
            Component who = (c.attacker != null && !c.attacker.isBlank())
                    ? attackerName(c.attacker) : null;

            MutableComponent line = Component.literal("• ");
            if (what != null && who != null) {
                line.append(what).append(Component.literal(" ("))
                        .append(who).append(Component.literal(")"));
            } else if (what != null) {
                line.append(what);
            } else if (who != null) {
                line.append(who);
            }
            tooltip.accept(line.append(Component.literal(" " + formatChance(c.amount)))
                    .withStyle(ChatFormatting.GRAY));
        }
        if (visible.size() > shown) {
            tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.resistances.more",
                    visible.size() - shown).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static String formatChance(double chance) {
        double pct = chance * 100;
        return (pct == Math.floor(pct)) ? (int) pct + "%" : String.format("%.1f%%", pct);
    }

    /**
     * Whether the long sections should be drawn right now.
     * <p>
     * Stats stay visible because comparing two tools at a glance is the whole
     * point of a tooltip; effects, resistances and drops hide because they grow
     * with whatever the JSON declares and can fill the screen.
     * <p>
     * Screen is a client class. appendHoverText is only ever called client side
     * in practice, which is why every mod does this, but it is worth knowing.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean detailsShown() {
        return net.minecraft.client.Minecraft.getInstance().hasShiftDown();
    }

    /**
     * The line that tells the player there is more.
     * <p>
     * The key name is a parameter rather than part of the sentence so a
     * translator can put it wherever their language needs it — Sophisticated
     * does the same, and it is the reason to bother with the placeholder even
     * though the key is not configurable.
     */
    public static void addDetailsHint(Consumer<Component> tooltip) {
        tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.hold_shift",
                        Component.translatable("tooltip.ultimatecustomgear.key.shift")
                                .withStyle(ChatFormatting.AQUA))
                .withStyle(ChatFormatting.GRAY));
    }

    /** Same shape, but naming what is hidden: a container shows its contents. */
    public static void addContentsHint(Consumer<Component> tooltip) {
        tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.container.press_for_contents",
                        Component.translatable("tooltip.ultimatecustomgear.key.shift")
                                .withStyle(ChatFormatting.AQUA))
                .withStyle(ChatFormatting.GRAY));
    }

    /**
     * Whether this gear has any of the gated sections. Mirrors the early-return
     * guards of the add* methods above — a bow with no held effects should not
     * advertise a key that reveals nothing.
     */
    public static boolean hasDetails(GearData data, String piece) {
        if (data.heldEffects != null && !data.heldEffects.isEmpty()) return true;
        if (data.setBonus != null && data.setBonus.effects != null) return true;
        if (piece != null && data.pieceEffects != null) {
            List<GearData.EffectData> effects = data.pieceEffects.get(piece);
            if (effects != null && !effects.isEmpty()) return true;
        }
        return hasAnyResistances(data, piece);
    }

    public static boolean hasDetails(GearData data) {
        return hasDetails(data, null);
    }

    /**
     * Whether any resistance section would draw something.
     * <p>
     * Mirrors the three add* methods' filters, showPlayerResistances included:
     * announcing details that turn out to be hidden player-specific entries
     * would leak exactly what that flag exists to keep secret.
     */
    private static boolean hasAnyResistances(GearData data, String piece) {
        Map<String, Double> damage = ResistanceResolver.damageMap(data, piece);
        if (damage != null) {
            for (Map.Entry<String, Double> e : damage.entrySet()) {
                if (e.getValue() != null && e.getValue() > 0) return true;
            }
        }

        Map<String, Double> attacker = ResistanceResolver.attackerMap(data, piece);
        if (attacker != null) {
            for (Map.Entry<String, Double> e : attacker.entrySet()) {
                if (e.getValue() == null || e.getValue() <= 0) continue;
                if (!data.showPlayerResistances && e.getKey().startsWith("player:")) continue;
                return true;
            }
        }

        List<GearData.ConditionalResistance> conditional =
                ResistanceResolver.conditionalList(data, piece);
        if (conditional != null) {
            for (GearData.ConditionalResistance c : conditional) {
                if (c == null || c.amount <= 0) continue;
                if (!data.showPlayerResistances
                        && c.attacker != null && c.attacker.startsWith("player:")) continue;
                boolean hasDamage   = c.damage   != null && !c.damage.isBlank();
                boolean hasAttacker = c.attacker != null && !c.attacker.isBlank();
                if (!hasDamage && !hasAttacker) continue;
                return true;
            }
        }
        return false;
    }

    /**
     * Whether this item or food has any of the gated sections.
     * <p>
     * The eat-duration line does NOT count: it stays visible, so it is never
     * what the hint is announcing.
     */
    public static boolean hasDetails(ItemData data) {
        if (data == null) return false;

        if (data.onEatEffects != null && !data.onEatEffects.isEmpty()) return true;

        if (data.mobDrops != null && data.mobDrops.entities != null) {
            for (String e : data.mobDrops.entities) {
                if (e != null && !e.isBlank()) return true;
            }
        }
        return false;
    }
}