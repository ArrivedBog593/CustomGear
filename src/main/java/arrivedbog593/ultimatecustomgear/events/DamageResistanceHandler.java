package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomArmorItem;
import arrivedbog593.ultimatecustomgear.util.EntityMatcher;
import arrivedbog593.ultimatecustomgear.util.ResistanceResolver;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Map;

/**
 * Applies the three resistance systems of custom armor, all baked into the
 * armor itself (no enchantments involved):
 * <ul>
 *   <li><b>damage_resistances</b> — "against WHAT": damage type tags
 *       ("#minecraft:is_fire") or exact ids ("iceandfire:dragon_fire")</li>
 *   <li><b>attacker_resistances</b> — "against WHOM": entity ids, entity tags
 *       ("#minecraft:undead"), mod wildcards ("mekanism:*") or a specific
 *       player ("player:Steve")</li>
 *   <li><b>conditional_resistances</b> — the combination: reduce damage only
 *       when BOTH the damage type and the attacker match (either key may be
 *       omitted, in which case only the other one is checked)</li>
 * </ul>
 * Values are the reduction PER EQUIPPED PIECE (0.0-1.0).
 * <p>
 * <b>Specificity, not accumulation:</b> the three systems are evaluated from
 * most to least specific — conditional (damage AND attacker) &gt; attacker &gt;
 * damage type — and the first level with any match REPLACES the more general
 * ones FOR THAT PIECE, even if its value is lower. Within a level, matching
 * entries add up; each equipped piece resolves on its own and the pieces then
 * add up. The total is clamped to 1.0 (100% = the hit deals zero damage);
 * there is deliberately no balance cap below that, so server owners may design
 * immunity armor on purpose.
 * <p>
 * Scope is a separate axis from specificity: a piece's maps MERGE with the
 * set-level ones, winning only on the keys they declare, unless the piece sets
 * inherit_set_resistances to false. See {@link ResistanceResolver}.
 * <p>
 * All data is resolved through the live gear lookup, so changes apply with
 * /customgear reload — no restart needed.
 */
public class DamageResistanceHandler {

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        DamageSource source = event.getSource();
        // The "owner" of the damage: an arrow reports the skeleton that shot
        // it, so resisting an attacker covers its projectiles too.
        Entity attacker = source.getEntity();

        // Each equipped piece resolves independently: it picks the most
        // specific level that matched and contributes that value. The pieces
        // then add up. Evaluating per piece (instead of globally) is what
        // makes partial per-piece overrides compose — otherwise a conditional
        // on one piece would silence the other three.
        float totalReduction = 0f;

        for (ItemStack stack : entity.getArmorSlots()) {
            if (!(stack.getItem() instanceof CustomArmorItem armor)) continue;

            GearData data = armor.getGearDataDirect();
            if (data == null) continue;
            String piece = armor.getPiece();

            float conditional = sumConditional(
                    ResistanceResolver.conditionalList(data, piece), source, attacker);
            float attackerLvl = sumAttackerResistances(
                    ResistanceResolver.attackerMap(data, piece), attacker);
            float damageLvl = sumDamageResistances(
                    ResistanceResolver.damageMap(data, piece), source);

            // Specificity within this piece: conditional > attacker > damage.
            totalReduction += conditional > 0f ? conditional
                    : attackerLvl > 0f ? attackerLvl
                    : damageLvl;
        }

        if (totalReduction <= 0f) return;

        // Mathematical clamp: more than 100% reduction has no meaning.
        totalReduction = Math.min(totalReduction, 1.0f);
        event.setAmount(event.getAmount() * (1.0f - totalReduction));
    }

    // ── Summing ──────────────────────────────────────────────────────────────

    private static float sumDamageResistances(Map<String, Double> map, DamageSource source) {
        if (map == null || map.isEmpty()) return 0f;
        float sum = 0f;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (entry.getValue() == null || entry.getValue() <= 0) continue;
            if (matchesDamage(source, entry.getKey())) sum += entry.getValue().floatValue();
        }
        return sum;
    }

    private static float sumAttackerResistances(Map<String, Double> map, Entity attacker) {
        if (map == null || map.isEmpty() || attacker == null) return 0f;
        float sum = 0f;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (entry.getValue() == null || entry.getValue() <= 0) continue;
            if (EntityMatcher.matches(entry.getKey(), attacker)) sum += entry.getValue().floatValue();
        }
        return sum;
    }

    private static float sumConditional(List<GearData.ConditionalResistance> list,
                                        DamageSource source, Entity attacker) {
        if (list == null || list.isEmpty()) return 0f;
        float sum = 0f;
        for (GearData.ConditionalResistance cond : list) {
            if (cond == null || cond.amount <= 0) continue;

            boolean hasDamage   = cond.damage != null && !cond.damage.isBlank();
            boolean hasAttacker = cond.attacker != null && !cond.attacker.isBlank();
            if (!hasDamage && !hasAttacker) continue; // validated at parse time

            // AND semantics: every declared condition must match
            if (hasDamage && !matchesDamage(source, cond.damage)) continue;
            if (hasAttacker && (attacker == null
                    || !EntityMatcher.matches(cond.attacker, attacker))) continue;

            sum += (float) cond.amount;
        }
        return sum;
    }

    // ── Damage type matching ─────────────────────────────────────────────────

    /** "#ns:tag" → damage type tag membership; "ns:id" → exact damage type. */
    private static boolean matchesDamage(DamageSource source, String key) {
        if (key == null || key.isBlank()) return false;

        if (key.startsWith("#")) {
            ResourceLocation tagRl = ResourceLocation.tryParse(key.substring(1));
            if (tagRl == null) return false;
            return source.is(TagKey.create(Registries.DAMAGE_TYPE, tagRl));
        }

        ResourceLocation rl = ResourceLocation.tryParse(key);
        if (rl == null) return false;
        return source.typeHolder().unwrapKey()
                .map(ResourceKey::location)
                .map(rl::equals)
                .orElse(false);
    }
}