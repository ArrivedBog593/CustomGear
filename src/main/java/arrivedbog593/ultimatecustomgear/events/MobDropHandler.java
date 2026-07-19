package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.loader.ItemRegistry;
import arrivedbog593.ultimatecustomgear.util.EntityMatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
 *   <li>Server-side only: drops are spawned by the server; clients need no
 *       extra data beyond the shared JSONs they already have.</li>
 * </ul>
 * Economy safety: requires_player_kill (default true) means mobs killed by
 * environment/other mobs drop nothing — otherwise automated mob farms with
 * fall damage would print unlimited currency.
 * <p>
 * Looting enchantment does NOT increase these drops in this version
 * (1.21's enchantment rework made looting access non-trivial; planned).
 */
public class MobDropHandler {

    @SuppressWarnings("unused") // event bus reflection
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity instanceof Player) return; // players never drop economy items
        if (entity instanceof ArmorStand) return; // crafteable → drop exploit

        boolean playerKill = event.getSource().getEntity() instanceof Player;
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        RandomSource random = entity.getRandom();

        for (Map.Entry<ResourceLocation, ItemData> entry : ItemRegistry.ITEM_MAP.entrySet()) {
            ItemData.MobDropsData drops = entry.getValue().mobDrops;
            if (drops == null) continue;

            if (drops.requiresPlayerKill && !playerKill) continue;
            if (!EntityMatcher.matchesAny(drops.entities, entity)) continue;
            if (random.nextDouble() >= drops.chance) continue;

            int min = Math.max(0, drops.min);
            int max = Math.max(min, drops.max);
            int count = min + (max > min ? random.nextInt(max - min + 1) : 0);
            if (count <= 0) continue; // 0 is a valid roll: this time nothing drops

            Item item = BuiltInRegistries.ITEM.get(entry.getKey());
            event.getDrops().add(new ItemEntity(
                    entity.level(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(item, count)));
        }
    }

}