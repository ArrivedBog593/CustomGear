package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.registry.ItemRegistry;
import arrivedbog593.ultimatecustomgear.util.EntityMatcher;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Map;

/**
 * Adds configurable item drops to mob deaths — the "source" side of an item
 * economy (coins dropping from monsters, etc.), driven by the "mob_drops"
 * field on ItemData.
 * <p>
 * Implemented as a LivingDropsEvent handler (not a global loot modifier) on
 * purpose:
 * <ul>
 *   <li>Reads ITEM_MAP at event time → changes to chance/min/max/entities
 *       apply with /customgear reload, no datapack reload needed.</li>
 *   <li>No codecs, no serializer registries — matches the mod's existing
 *       handler pattern (SetBonusHandler, HeldEffectHandler...).</li>
 *   <li>Server-side only: the server spawns drops; clients need no
 *       extra data beyond the shared JSONs they already have.</li>
 * </ul>
 * <b>Player kill (changed in 1.6.0):</b> requires_player_kill now defaults to
 * false, matching vanilla — most vanilla drops do not care who landed the blow.
 * Servers running an economy should set it to true explicitly, otherwise a fall
 * damage farm prints currency. The parser warns when it is left undeclared.
 * <p>
 * <b>Looting (new in 1.6.0):</b> mirrors vanilla's two mechanisms via
 * looting_mode. "count" adds 0..level to the rolled amount; "chance" raises the
 * drop probability and leaves the amount alone; "none" opts out. Vanilla never
 * applies both to the same drop, and neither does this.
 */
@SuppressWarnings("deprecation") // LivingDropsEvent is deprecated in 1.20, but no replacement exists yet
public class MobDropHandler {

    @SuppressWarnings("unused") // event bus reflection
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity instanceof Player) return; // players never drop economy items
        if (entity instanceof ArmorStand) return; // crafteable → drop exploit

        // Two separate questions since 1.6.0:
        //  1. Was a player involved at all? Vanilla asks for RECENT player
        //     damage, not the killing blow, so a mob you softened up and a
        //     creeper finished still counts as yours.
        //  2. Whose weapon do we read Looting from? That one IS the killing
        //     blow — no killer, no looting.
        boolean playerInvolved = event.isRecentlyHit();
        Player killer = event.getSource().getEntity() instanceof Player p ? p : null;

        RandomSource random = entity.getRandom();

        // Resolved once per death. This is a dynamic registry lookup, and
        // ITEM_MAP can hold hundreds of entries — it must not live in the loop.
        int lootingLevel = (killer != null) ? lootingLevel(killer) : 0;

        for (Map.Entry<ResourceLocation, ItemData> entry : ItemRegistry.ITEM_MAP.entrySet()) {
            ItemData.MobDropsData drops = entry.getValue().mobDrops;
            if (drops == null) continue;

            if (drops.isPlayerKillRequired() && !playerInvolved) continue;
            if (!EntityMatcher.matchesAny(drops.entities, entity)) continue;

            // ── Gate: looting can widen it in "chance" mode ──────────────────
            double chance = drops.chance;
            if (lootingLevel > 0 && drops.isLootingChance()) {
                chance += lootingLevel * drops.lootingChanceBonus;
            }
            if (random.nextDouble() >= chance) continue;

            // ── Amount: looting can add to it in "count" mode ────────────────
            int min = Math.max(0, drops.min);
            int max = Math.max(min, drops.max);
            int count = min + (max > min ? random.nextInt(max - min + 1) : 0);

            // Applied BEFORE the empty check on purpose: vanilla lets a base
            // roll of 0 still produce items when the killer has Looting (chance
            // already acted as the gate). Also, NOT clamped to max — vanilla
            // lets looting push past the declared range.
            if (lootingLevel > 0 && drops.isLootingCount()) {
                count += random.nextInt(lootingLevel + 1);
            }

            if (count <= 0) continue; // 0 is a valid roll: this time nothing drops

            Item item = BuiltInRegistries.ITEM.get(entry.getKey());
            event.getDrops().add(new ItemEntity(
                    entity.level(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(item, count)));
        }
    }

    /**
     * Looting level on the killer's main hand.
     * <p>
     * 1.21's enchantment rework removed the static Enchantment constants, so the
     * Holder has to be pulled from the dynamic registry every time — there is no
     * cacheable constant, and caching across a datapack reload would go stale.
     */
    private static int lootingLevel(Player killer) {
        try {
            Holder<Enchantment> looting = killer.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(Enchantments.LOOTING);
            return EnchantmentHelper.getItemEnchantmentLevel(looting, killer.getMainHandItem());
        } catch (IllegalStateException e) {
            // A datapack can remove minecraft:looting from the registry.
            // Not an error worth logging every kill — just no bonus.
            return 0;
        }
    }

}