package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.TagPatchData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

/**
 * Feeds the TagFileBuilder from tag_patch files — the "foreign content belongs
 * to this tag" direction. See TagPatchData for why the "tags" field cannot
 * express this.
 * <p>
 * Every entry is emitted with required:false, unconditionally. The whole point
 * is tagging content from mods that may not be installed, and a required entry
 * pointing at a missing id makes vanilla drop the ENTIRE tag at datapack load —
 * so one uninstalled mod would silently disable a tag that other mods still
 * populate correctly.
 * <p>
 * That safety has a cost: a typo becomes invisible. "iceandfire:dragon_fier"
 * fails exactly like a legitimately absent mod. So this class warns when the
 * namespace IS loaded but the id is not in the registry — a loaded mod that
 * does not have the id means a typo, not an absence.
 * <p>
 * The check only reaches registries that exist at construction time
 * (BuiltInRegistries). damage_type, enchantment and other DATAPACK registries
 * cannot be checked here — and damage_type is the flagship use case, so typos
 * in exactly the most common patch go undetected. Documented, not solvable at
 * this point in the lifecycle.
 * <p>
 * Like every other tag source, this must run BOTH at startup and in
 * /customgear reload, before TagFileBuilder.emit.
 * <p>
 * A patch can also REMOVE ids, using NeoForge's "remove" extension. That is the
 * only way out of a tag filled by inheritance rather than by entries. Removals
 * carry no required:false equivalent — the array is plain ids — and they are
 * warned about when the tag belongs to minecraft or c, because taking content
 * out of a shared tag breaks other mods in ways nobody will trace back here.
 */
public class TagPatchLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    /**
     * Registries known to work. Anything else is accepted WITH a warning rather
     * than rejected: tag directories are just paths, so a registry this list
     * has not heard of still produces a valid file. Rejecting would mean
     * shipping a new mod version for every registry someone finds a use for.
     */
    private static final Set<String> KNOWN_REGISTRIES = Set.of(
            "item", "block", "fluid", "entity_type", "damage_type",
            "enchantment", "biome", "game_event", "banner_pattern",
            "instrument", "painting_variant", "point_of_interest_type"
    );

    /** Registries that can be verified at this point in the load cycle. */
    private static final Set<String> VERIFIABLE = Set.of(
            "item", "block", "fluid", "entity_type");

    public static void loadAll(TagFileBuilder tags, List<TagPatchData> patches) {
        for (TagPatchData patch : patches) {
            String registry = patch.normalizedRegistry();
            if (registry == null) {
                LOGGER.error("[CustomGear] tag_patch for '{}': invalid 'registry' — expected a "
                        + "plain registry name like 'damage_type' or 'item'", patch.tag);
                continue;
            }

            if (!KNOWN_REGISTRIES.contains(registry)) {
                LOGGER.warn("[CustomGear] tag_patch for '{}': registry '{}' is not one this "
                                + "version knows about. Writing the file anyway — if the registry name "
                                + "is right it will work, if not the tag is simply ignored by the game.",
                        patch.tag, registry);
            }

            ResourceLocation tagRl = parseTag(patch.tag);
            if (tagRl == null) continue;

            int added = 0;
            if (patch.values != null) {
                for (String raw : patch.values) {
                    ResourceLocation valueRl = parseValue(raw, patch.tag);
                    if (valueRl == null) continue;

                    warnIfLikelyTypo(registry, valueRl, patch.tag);

                    // required:false always — see class doc
                    tags.add(registry, tagRl, valueRl.toString(), false);
                    added++;
                }
            }

            int removed = 0;
            if (patch.remove != null) {
                warnIfRiskyRemoval(tagRl);
                for (String raw : patch.remove) {
                    ResourceLocation valueRl = parseValue(raw, patch.tag);
                    if (valueRl == null) continue;

                    // No typo check here: removing an id that was never in the
                    // tag is harmless and silent by design, so a warning would
                    // fire on every legitimately-absent mod.
                    tags.remove(registry, tagRl, valueRl.toString());
                    removed++;
                }
            }

            LOGGER.info("[CustomGear] tag_patch on {} tag '{}': {} added, {} removed",
                    registry, tagRl, added, removed);
        }
    }

    private static ResourceLocation parseTag(String raw) {
        // Tolerate a stray '#' the same way the tags field does
        String cleaned = raw.startsWith("#") ? raw.substring(1) : raw;
        ResourceLocation rl = ResourceLocation.tryParse(cleaned);
        if (rl == null) {
            LOGGER.error("[CustomGear] tag_patch: malformed 'tag' value '{}' — the whole patch "
                    + "is skipped. Expected 'namespace:path' with no '#'.", raw);
        }
        return rl;
    }

    /**
     * Warns when the id's namespace belongs to a LOADED mod but the id is not
     * in the registry. A loaded mod that does not have the id means a typo;
     * an unloaded mod means legitimate absence, which is the supported case
     * and stays silent.
     * <p>
     * Namespace and mod id are not guaranteed to match, but they do for the
     * overwhelming majority of mods, and a false silence is cheap here — the
     * worst case is that a typo goes unwarned, which is the status quo.
     */
    private static void warnIfLikelyTypo(String registry, ResourceLocation id, String tag) {
        if (!VERIFIABLE.contains(registry)) return;

        String namespace = id.getNamespace();
        boolean nsPresent = namespace.equals("minecraft")
                || ModList.get().isLoaded(namespace);
        if (!nsPresent) return; // mod not installed — expected, this is the whole feature

        boolean exists = switch (registry) {
            case "item"        -> BuiltInRegistries.ITEM.containsKey(id);
            case "block"       -> BuiltInRegistries.BLOCK.containsKey(id);
            case "fluid"       -> BuiltInRegistries.FLUID.containsKey(id);
            case "entity_type" -> BuiltInRegistries.ENTITY_TYPE.containsKey(id);
            default            -> true;
        };

        if (!exists) {
            LOGGER.warn("[CustomGear] tag_patch '{}': '{}' is not a registered {} even though "
                            + "'{}' IS loaded — this looks like a typo. The entry is written as "
                            + "optional, so it will be silently ignored in game.",
                    tag, id, registry, namespace);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Parses one values/remove entry, or null when malformed. */
    private static ResourceLocation parseValue(String raw, String tag) {
        if (raw == null || raw.isBlank()) return null;
        ResourceLocation rl = ResourceLocation.tryParse(raw.trim());
        if (rl == null) {
            LOGGER.warn("[CustomGear] tag_patch '{}': malformed value '{}' — skipped. "
                    + "Expected 'namespace:path'.", tag, raw);
        }
        return rl;
    }

    /**
     * Warns when removing from a tag this mod does not own.
     * <p>
     * Adding to a foreign tag is additive and safe. Removing is not: taking an
     * item out of #minecraft:planks or #c:ingots breaks recipes across every
     * mod that consumes them, and the person who sees the breakage has no
     * reason to connect it to a tag_patch file they wrote weeks ago.
     */
    private static void warnIfRiskyRemoval(ResourceLocation tag) {
        String ns = tag.getNamespace();
        if (ns.equals("minecraft") || ns.equals("c")) {
            LOGGER.warn("[CustomGear] tag_patch removes entries from '{}', a tag this mod does "
                    + "not own. Removals affect every mod that reads it — if a recipe or "
                    + "mechanic stops working elsewhere, check this patch first.", tag);
        }
    }
}