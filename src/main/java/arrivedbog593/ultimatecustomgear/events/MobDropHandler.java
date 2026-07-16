package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.loader.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.List;
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

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity instanceof Player) return; // players never drop economy items
        if (entity instanceof ArmorStand) return; // crafteable → coin exploit

        boolean playerKill = event.getSource().getEntity() instanceof Player;
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        RandomSource random = entity.getRandom();

        for (Map.Entry<ResourceLocation, ItemData> entry : ItemRegistry.ITEM_MAP.entrySet()) {
            ItemData.MobDropsData drops = entry.getValue().mobDrops;
            if (drops == null) continue;

            if (drops.requiresPlayerKill && !playerKill) continue;
            if (!matchesEntity(drops.entities, entity, entityId)) continue;
            if (random.nextDouble() >= drops.chance) continue;

            int min = Math.max(1, drops.min);
            int max = Math.max(min, drops.max);
            int count = min + (max > min ? random.nextInt(max - min + 1) : 0);

            Item item = BuiltInRegistries.ITEM.get(entry.getKey());
            event.getDrops().add(new ItemEntity(
                    entity.level(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(item, count)));
        }
    }

    /**
     * Entity filter. null/empty = every living mob. Entries can be:
     *   - exact id:      "minecraft:zombie"
     *   - entity tag:    "#minecraft:undead" (any undead mob)
     *   - mod wildcard:  "minecraftmod:*" (every mob from that mod)
     */
    private static boolean matchesEntity(List<String> entities, LivingEntity entity,
                                         ResourceLocation entityId) {
        if (entities == null || entities.isEmpty()) return false; // must be explicit — see validator
        for (String e : entities) {
            if (e == null || e.isBlank()) continue;
            if (e.equals("all")) return true;
            if (e.startsWith("#")) {
                ResourceLocation tagRl = ResourceLocation.tryParse(e.substring(1));
                if (tagRl != null && entity.getType().is(
                        TagKey.create(Registries.ENTITY_TYPE, tagRl))) return true;
            } else if (e.endsWith(":*")) {
                if (entityId.getNamespace().equals(e.substring(0, e.length() - 2))) return true;
            } else if (entityId.toString().equals(e)) {
                return true;
            }
        }
        return false;
    }
}