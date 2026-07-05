package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.data.RecipeData;
import arrivedbog593.ultimatecustomgear.data.RecipeListDeserializer;
import arrivedbog593.ultimatecustomgear.util.ParserUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GearParser {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(
                    new com.google.gson.reflect.TypeToken<List<RecipeData>>(){}.getType(),
                    new RecipeListDeserializer()
            )
            .create();

    private static final Set<String> GEAR_TYPES = Set.of(
            "armor_set", "tool_set", "weapon_set",
            "sword", "bow", "crossbow", "shield",
            "pickaxe", "axe", "shovel", "hoe"
    );

    /**
     * Loads gear from every content root: the loose folder plus every zip
     * mounted from packs/. Cache keys are prefixed with the root label
     * ("packs/foo.zip!inner/path.json") so zip entries and loose files never
     * collide in the cache. Zip entries are cached like loose files — their
     * lastModified comes from the entry inside the zip.
     */
    public static List<GearData> loadAll(ContentRoots contentRoots) {
        List<GearData> result  = new ArrayList<>();
        Set<String> seenIds    = new HashSet<>();

        Path configFolder = contentRoots.configFolder();
        if (!ParserUtils.ensureFolderExists(configFolder)) return result;

        GenericCache<GearData> cache = GenericCache.load(
                configFolder,
                "gear_cache.json",
                new TypeToken<Map<String, GenericCache.CacheEntry<GearData>>>() {}.getType()
        );
        Set<String>    currentKeys   = new HashSet<>();
        AtomicBoolean  cacheModified = new AtomicBoolean(false);

        for (ContentRoots.Root root : contentRoots.roots()) {
            // Symlink escapes are only possible on the real filesystem;
            // zip virtual filesystems cannot reference paths outside the zip.
            Path canonicalRoot = root.isLoose() ? ParserUtils.resolveCanonical(root.path()) : null;
            if (root.isLoose() && canonicalRoot == null) continue;

            try {
                Files.walk(root.path())
                        .filter(p -> p.toString().endsWith(".json")
                                && !root.path().relativize(p).startsWith(".cache")
                                && !root.path().relativize(p).startsWith("packs"))
                        .forEach(path -> {
                            if (root.isLoose() && !ParserUtils.isSafeChild(path, canonicalRoot)) {
                                LOGGER.warn("[CustomGear] Skipping file outside customgear/ dir (possible symlink attack): {}", path);
                                return;
                            }

                            String relKey = root.label() + root.path().relativize(path);
                            currentKeys.add(relKey);

                            try {
                                String json = Files.readString(path);
                                String type = ParserUtils.extractType(json);
                                if (type == null || !GEAR_TYPES.contains(type)) return;

                                FileTime ft           = Files.getLastModifiedTime(path);
                                long     lastModified = ft.toMillis();
                                GenericCache.CacheEntry<GearData> cached = cache.get(relKey);

                                if (cached != null && cached.lastModified == lastModified && cached.data != null) {
                                    if (!seenIds.add(cached.data.id)) {
                                        LOGGER.error("[CustomGear] Duplicate ID '{}' found (cache) — skipping: {}",
                                                cached.data.id, relKey);
                                        return;
                                    }
                                    result.add(cached.data);
                                    LOGGER.debug("[CustomGear] Cache hit: {}", relKey);
                                } else {
                                    GearData data = GSON.fromJson(json, GearData.class);
                                    if (validate(data, path)) {
                                        if (!seenIds.add(data.id)) {
                                            LOGGER.error("[CustomGear] Duplicate ID '{}' — file '{}' will be ignored. "
                                                    + "Each gear ID must be unique across all JSON files and packs.", data.id, relKey);
                                            return;
                                        }
                                        result.add(data);
                                        cache.put(relKey, lastModified, data);
                                        cacheModified.set(true);
                                        LOGGER.info("[CustomGear] Loaded: {} ({})", data.id, relKey);
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("[CustomGear] Error reading {}: {}",
                                        path.getFileName(), e.getMessage());
                            }
                        });
            } catch (IOException e) {
                LOGGER.error("[CustomGear] Error scanning {}: {}",
                        root.isLoose() ? "folder" : root.label(), e.getMessage());
            }
        }

        if (cache.removeStale(currentKeys)) {
            cacheModified.set(true);
        }
        if (cacheModified.get()) {
            cache.save();
        }

        return result;
    }

    private static boolean validate(GearData data, Path path) {
        if (data.id == null || data.id.isBlank()) {
            LOGGER.warn("[CustomGear] JSON missing 'id': {}", path.getFileName());
            return false;
        }
        if (!data.id.matches("^[a-z][a-z0-9_]{1,63}$")) {
            LOGGER.warn("[CustomGear] ID does not match format (lowercase, 2-64 chars): {}", data.id);
            return false;
        }
        if (data.type == null || data.type.isBlank()) {
            LOGGER.warn("[CustomGear] JSON missing 'type': {}", path.getFileName());
            return false;
        }

        if (!validateNumericRanges(data, path)) return false;

        if (data.pieceEffects != null) {
            for (Map.Entry<String, List<GearData.EffectData>> entry : data.pieceEffects.entrySet()) {
                if (entry.getValue() != null) {
                    for (GearData.EffectData effectData : entry.getValue()) {
                        if (!validateEffect(effectData, data.id)) return false;
                    }
                }
            }
        }

        if (data.heldEffects != null) {
            for (GearData.EffectData effectData : data.heldEffects) {
                if (!validateEffect(effectData, data.id)) return false;
            }
        }

        if (data.setBonus != null && data.setBonus.effects != null) {
            for (GearData.EffectData effectData : data.setBonus.effects) {
                if (!validateEffect(effectData, data.id)) return false;
            }
        }

        return true;
    }

    /**
     * Validates numeric ranges to avoid values that cause undefined behavior
     * in Minecraft (negative durability, negative damage, etc.).
     * <p>
     * Hard limits (real Minecraft constraints):
     *   - knockback_resistance: 0.0-1.0 (probabilistic, >1.0 causes physics glitches)
     *   - amplifier: 0-255 (stored as byte internally by MobEffectInstance)
     * <p>
     * Soft lower bounds (>= 0 only, no upper cap):
     *   - durability, defense, toughness, miningSpeed, damage values, multipliers, tillRadius
     *     These are left uncapped so admins can freely configure powerful gear.
     */
    private static boolean validateNumericRanges(GearData data, Path path) {
        String name = path.getFileName().toString();

        if (data.durability < 0) {
            LOGGER.warn("[CustomGear] '{}': durability must be >= 0 (got {})", name, data.durability);
            return false;
        }

        if (data.pieces != null) {
            for (Map.Entry<String, GearData.PieceData> e : data.pieces.entrySet()) {
                GearData.PieceData p = e.getValue();
                if (p.durability < 0) {
                    LOGGER.warn("[CustomGear] '{}': piece '{}' durability must be >= 0", name, e.getKey());
                    return false;
                }
                if (p.defense < 0) {
                    LOGGER.warn("[CustomGear] '{}': piece '{}' defense must be >= 0 (got {})", name, e.getKey(), p.defense);
                    return false;
                }
                if (p.toughness < 0) {
                    LOGGER.warn("[CustomGear] '{}': piece '{}' toughness must be >= 0 (got {})", name, e.getKey(), p.toughness);
                    return false;
                }
                if (p.knockback_resistance < 0 || p.knockback_resistance > 1) {
                    LOGGER.warn("[CustomGear] '{}': piece '{}' knockback_resistance must be 0.0-1.0", name, e.getKey());
                    return false;
                }
            }
        }

        if (data.tools != null) {
            for (Map.Entry<String, GearData.ToolData> e : data.tools.entrySet()) {
                GearData.ToolData t = e.getValue();
                if (t.durability < 0) {
                    LOGGER.warn("[CustomGear] '{}': tool '{}' durability must be >= 0", name, e.getKey());
                    return false;
                }
                if (t.miningSpeed < 0) {
                    LOGGER.warn("[CustomGear] '{}': tool '{}' miningSpeed must be >= 0", name, e.getKey());
                    return false;
                }
            }
        }

        if (data.attackDamage < 0) {
            LOGGER.warn("[CustomGear] '{}': attackDamage must be >= 0 (got {})", name, data.attackDamage);
            return false;
        }
        if (data.arrowDamage < 0) {
            LOGGER.warn("[CustomGear] '{}': arrowDamage must be >= 0 (got {})", name, data.arrowDamage);
            return false;
        }
        if (data.damageMultiplier < 0) {
            LOGGER.warn("[CustomGear] '{}': damageMultiplier must be >= 0 (got {})", name, data.damageMultiplier);
            return false;
        }
        if (data.arrowDamageMultiplier < 0) {
            LOGGER.warn("[CustomGear] '{}': arrowDamageMultiplier must be >= 0 (got {})", name, data.arrowDamageMultiplier);
            return false;
        }
        if (data.tillRadius < 0) {
            LOGGER.warn("[CustomGear] '{}': tillRadius must be >= 0 (got {})", name, data.tillRadius);
            return false;
        }

        if (data.weapons != null) {
            for (Map.Entry<String, GearData.WeaponData> e : data.weapons.entrySet()) {
                GearData.WeaponData w = e.getValue();
                String wKey = e.getKey();
                if (w.durability < 0) {
                    LOGGER.warn("[CustomGear] '{}': weapon '{}' durability must be >= 0", name, wKey);
                    return false;
                }
                if (w.attackDamage < 0) {
                    LOGGER.warn("[CustomGear] '{}': weapon '{}' attackDamage must be >= 0", name, wKey);
                    return false;
                }
                if (w.arrowDamage < 0) {
                    LOGGER.warn("[CustomGear] '{}': weapon '{}' arrowDamage must be >= 0", name, wKey);
                    return false;
                }
                if (w.damageMultiplier < 0) {
                    LOGGER.warn("[CustomGear] '{}': weapon '{}' damageMultiplier must be >= 0", name, wKey);
                    return false;
                }
                if (w.arrowDamageMultiplier < 0) {
                    LOGGER.warn("[CustomGear] '{}': weapon '{}' arrowDamageMultiplier must be >= 0", name, wKey);
                    return false;
                }
                if (w.chargeSpeed < 0) {
                    LOGGER.warn("[CustomGear] '{}': weapon '{}' chargeSpeed must be >= 0", name, wKey);
                    return false;
                }
            }
        }

        if (data.heldEffects != null) {
            for (GearData.EffectData ed : data.heldEffects) {
                if (ed.amplifier < 0 || ed.amplifier > 255) {
                    LOGGER.warn("[CustomGear] '{}': heldEffect amplifier must be 0-255 (got {})", name, ed.amplifier);
                    return false;
                }
            }
        }
        if (data.setBonus != null && data.setBonus.effects != null) {
            for (GearData.EffectData ed : data.setBonus.effects) {
                if (ed.amplifier < 0 || ed.amplifier > 255) {
                    LOGGER.warn("[CustomGear] '{}': setBonus amplifier must be 0-255 (got {})", name, ed.amplifier);
                    return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean validateEffect(GearData.EffectData effectData, String gearId) {
        if (effectData.effect == null || effectData.effect.isBlank()) {
            LOGGER.warn("[CustomGear] Empty effect ID in gear '{}'", gearId);
            return false;
        }
        try {
            ResourceLocation rl = ResourceLocation.parse(effectData.effect);
            if (BuiltInRegistries.MOB_EFFECT.getHolder(rl).isEmpty()) {
                LOGGER.warn("[CustomGear] Effect not found in registry: '{}' (gear: '{}')",
                        effectData.effect, gearId);
                return false;
            }
        } catch (Exception e) {
            LOGGER.warn("[CustomGear] Invalid effect ID format: '{}' (gear: '{}')",
                    effectData.effect, gearId);
            return false;
        }
        return true;
    }
}