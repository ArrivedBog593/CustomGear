package arrivedbog593.ultimatecustomgear.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Shared entity-key matching for every feature that filters by entity:
 * mob drops ("which mobs drop this") and attacker resistances ("which
 * attackers this armor resists").
 * <p>
 * Key grammar — the same one used across the mod:
 * <ul>
 *   <li>{@code "all"} — any entity</li>
 *   <li>{@code "minecraft:zombie"} — exact entity type id (any mod)</li>
 *   <li>{@code "#minecraft:undead"} — entity type tag</li>
 *   <li>{@code "mekanism:*"} — every entity from that mod</li>
 *   <li>{@code "player:Steve"} — a specific player by name (case-insensitive)</li>
 * </ul>
 * Unknown or malformed keys simply never match; they are validated at parse
 * time instead of failing at runtime.
 */
public final class EntityMatcher {

    private static final String PLAYER_PREFIX = "player:";

    private EntityMatcher() {}

    /** True if the entity matches the given key. */
    public static boolean matches(String key, Entity entity) {
        if (key == null || key.isBlank() || entity == null) return false;

        if (key.equals("all")) return true;

        if (key.startsWith(PLAYER_PREFIX)) {
            if (!(entity instanceof Player player)) return false;
            String name = key.substring(PLAYER_PREFIX.length());
            if (name.equals("*")) return true;              // ← any player
            return player.getGameProfile().getName().equalsIgnoreCase(name);
        }

        if (key.startsWith("#")) {
            ResourceLocation tagRl = ResourceLocation.tryParse(key.substring(1));
            return tagRl != null
                    && entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, tagRl));
        }

        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());

        if (key.endsWith(":*")) {
            return entityId.getNamespace().equals(key.substring(0, key.length() - 2));
        }
        return entityId.toString().equals(key);
    }

    /** True if the entity matches ANY key in the list (empty list = no match). */
    public static boolean matchesAny(List<String> keys, Entity entity) {
        if (keys == null || keys.isEmpty() || entity == null) return false;
        for (String key : keys) {
            if (matches(key, entity)) return true;
        }
        return false;
    }
}