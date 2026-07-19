package arrivedbog593.ultimatecustomgear.util;

import arrivedbog593.ultimatecustomgear.data.GearData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the effective resistance maps for one armor piece.
 *
 * <p>There are two independent axes:
 *
 * <ul>
 *   <li><b>Scope</b> (this class): a piece-level map <b>merges</b> with the
 *       set-level map, and the piece wins only on the keys it declares —
 *       unless it opts out with {@code inherit_set_resistances: false}.</li>
 *   <li><b>Layer specificity</b> (the damage handler): {@code conditional} &gt;
 *       {@code attacker} &gt; {@code damage}. The most specific layer with any
 *       match <b>replaces</b> the more general ones, per piece.</li>
 * </ul>
 *
 * <pre>
 *   set:   { "#minecraft:is_fall": 0.10, "#minecraft:is_fire": 0.10 }
 *   boots: { "#minecraft:is_fall": 0.15 }
 *   -&gt; boots resist fall 0.15 AND fire 0.10; other pieces keep 0.10 / 0.10
 *
 *   set:   { "#minecraft:is_fall": 0.10, "#minecraft:is_fire": 0.10 }
 *   chest: inherit_set_resistances = false, { "#minecraft:is_projectile": 0.15 }
 *   -&gt; the chestplate resists projectiles only; nothing from the set applies
 *
 *   set:   anything
 *   helmet: inherit_set_resistances = false, no maps declared
 *   -&gt; the helmet contributes no resistance at all
 * </pre>
 *
 * <p>Both {@code TooltipHelper} and {@code DamageResistanceHandler} must resolve
 * through this class, or the tooltip and the actual damage reduction will drift
 * apart.
 */
public final class ResistanceResolver {

    private ResistanceResolver() {}

    /** Effective damage_resistances for one piece. */
    public static Map<String, Double> damageMap(GearData data, String piece) {
        GearData.PieceData p = pieceData(data, piece);
        Map<String, Double> own = (p == null) ? null : p.damageResistances;
        if (p != null && !p.inheritSetResistances) return own;
        return merge(data == null ? null : data.damageResistances, own);
    }

    /** Effective attacker_resistances for one piece. */
    public static Map<String, Double> attackerMap(GearData data, String piece) {
        GearData.PieceData p = pieceData(data, piece);
        Map<String, Double> own = (p == null) ? null : p.attackerResistances;
        if (p != null && !p.inheritSetResistances) return own;
        return merge(data == null ? null : data.attackerResistances, own);
    }

    /**
     * Effective conditional_resistances for one piece.
     * A rule's identity is its (attacker, damage) pair: a piece rule with the
     * same pair replaces the set rule, any other piece rule is added.
     */
    public static List<GearData.ConditionalResistance> conditionalList(GearData data, String piece) {
        GearData.PieceData p = pieceData(data, piece);
        List<GearData.ConditionalResistance> own = (p == null) ? null : p.conditionalResistances;
        if (p != null && !p.inheritSetResistances) return own;

        List<GearData.ConditionalResistance> base =
                (data == null) ? null : data.conditionalResistances;

        if (own == null || own.isEmpty()) return base;
        if (base == null || base.isEmpty()) return own;

        Map<String, GearData.ConditionalResistance> byKey = new LinkedHashMap<>();
        for (GearData.ConditionalResistance c : base) {
            if (c != null) byKey.put(ruleKey(c), c);
        }
        for (GearData.ConditionalResistance c : own) {
            if (c != null) byKey.put(ruleKey(c), c);
        }
        return new ArrayList<>(byKey.values());
    }

    // ------------------------------------------------------------------

    private static Map<String, Double> merge(Map<String, Double> base,
                                             Map<String, Double> override) {
        if (override == null || override.isEmpty()) return base;
        if (base == null || base.isEmpty()) return override;
        // LinkedHashMap keeps set-level order first; putAll overwrites values
        // in place, so shared keys stay where the set declared them.
        Map<String, Double> merged = new LinkedHashMap<>(base);
        merged.putAll(override);
        return merged;
    }

    private static String ruleKey(GearData.ConditionalResistance c) {
        String a = c.attacker == null ? "" : c.attacker;
        String d = c.damage   == null ? "" : c.damage;
        return a + "\u0000" + d;
    }

    private static GearData.PieceData pieceData(GearData data, String piece) {
        if (data == null || data.pieces == null || piece == null) return null;
        return data.pieces.get(piece);
    }
}