package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Emits the vanilla tags every piece of gear needs to behave like real gear.
 * <p>
 * WHY THIS EXISTS: gear never went through the tag system at all — ItemTagLoader
 * only takes items, blocks, and fluids, so nothing registered from GearData got a
 * single tag. That is not cosmetic. In 1.21 the enchanting table decides what it
 * offers through #minecraft:enchantable/* tags, so a custom sword was offered
 * nothing while a diamond sword was offered everything. Same for #minecraft:
 * swords (breaks decorated pots), #minecraft:head_armor (what counts as a
 * helmet), and so on.
 * <p>
 * These tags are DERIVED, not declared: registering a chestplate is already
 * enough to know it belongs in #minecraft:chest_armor. The user never asks for
 * them. That is the difference from ItemTagLoader, which only emits what the
 * "tags" field spells out.
 * <p>
 * Two layers, kept apart on purpose:
 * <ul>
 *   <li><b>Behavior</b> (#minecraft:swords, #minecraft:head_armor...) — always
 *       emitted. They say what the item IS.</li>
 *   <li><b>Enchantability</b> (#minecraft:enchantable/*) — only when the gear
 *       declares enchantable: true. Emitting them regardless would make an
 *       explicitly non-enchantable weapon enchantable anyway, silently
 *       overriding what the JSON asked for.</li>
 * </ul>
 * Convention (c:) tags are emitted too — c:armors, c:tools, c:tools/melee_weapon
 * and friends. They are a community namespace rather than a vanilla one, so no
 * mod owns them; other mods read them to recognize gear generically, and
 * without them this mod's content is invisible to that whole category of
 * integration.
 * <p>
 * Smithing trims are opt-in with "trimmable": true. Off by default because
 * trims draw over the armor layers, so a set with transparent layers or a
 * GeckoLib 3D model applies the trim but never shows it. Opting out needs an
 * explicit removal, not just silence: vanilla's trimmable_armor is the union of
 * the four slot tags, so armor inherits it from the slot tag this class adds.
 * <p>
 * Like every tag source, run these BOTH at startup and in /customgear reload,
 * before TagFileBuilder.emit.
 */
public class GearTagLoader {

    public static void loadAll(TagFileBuilder tags, List<GearData> gearList) {
        for (GearData data : gearList) {
            switch (data.type) {
                case "armor_set" -> {
                    if (data.pieces == null) continue;
                    for (String piece : new String[]{"helmet", "chestplate", "leggings", "boots"}) {
                        if (!data.pieces.containsKey(piece)) continue;
                        armorPiece(tags, data.id + "_" + piece, piece, data);
                    }
                }
                case "tool_set" -> {
                    if (data.tools == null) continue;
                    for (String tool : new String[]{"pickaxe", "axe", "shovel", "hoe"}) {
                        if (!data.tools.containsKey(tool)) continue;
                        tool(tags, data.id + "_" + tool, tool, data);
                    }
                }
                case "weapon_set" -> {
                    if (data.weapons == null) continue;
                    for (String weapon : new String[]{"sword", "bow", "crossbow", "shield"}) {
                        if (!data.weapons.containsKey(weapon)) continue;
                        weapon(tags, data.id + "_" + weapon, weapon, data);
                    }
                }
                case "sword", "bow", "crossbow", "shield" -> weapon(tags, data.id, data.type, data);
                case "pickaxe", "axe", "shovel", "hoe"    -> tool(tags, data.id, data.type, data);
                default -> { /* blocks, items, fluids, advancements: not gear */ }
            }
        }
    }

    // ── Armor ────────────────────────────────────────────────────────────────

    private static void armorPiece(TagFileBuilder tags, String itemId, String piece, GearData data) {
        String id = "customgear:" + itemId;

        String slotTag = switch (piece) {
            case "helmet"     -> "head_armor";
            case "chestplate" -> "chest_armor";
            case "leggings"   -> "leg_armor";
            default           -> "foot_armor";
        };

        add(tags, slotTag, id);

        // Trims are opt-in. Not adding the tag is not enough: vanilla's
        // trimmable_armor is the union of the four slot tags, so the piece is
        // already in it via the slot tag added just above. It has to be removed
        // explicitly. (Mekanism does the same for its MekaSuit.)
        if (data.trimmable) {
            add(tags, "trimmable_armor", id);
        } else {
            remove(tags, "trimmable_armor", id);
        }

        if (data.enchantable) {
            add(tags, "enchantable/" + slotTag, id);
            add(tags, "enchantable/armor", id);
            add(tags, "enchantable/equippable", id);
            add(tags, "enchantable/durability", id);
            add(tags, "enchantable/vanishing", id);
        }

        addC(tags, "armors", id);
        if (data.enchantable) addC(tags, "enchantables", id);
    }

    // ── Tools ────────────────────────────────────────────────────────────────

    private static void tool(TagFileBuilder tags, String itemId, String type, GearData data) {
        String id = "customgear:" + itemId;

        add(tags, switch (type) {
            case "pickaxe" -> "pickaxes";
            case "axe"     -> "axes";
            case "shovel"  -> "shovels";
            default        -> "hoes";
        }, id);
        add(tags, "breaks_decorated_pots", id);

        if (data.enchantable) {
            add(tags, "enchantable/mining", id);
            add(tags, "enchantable/mining_loot", id);
            add(tags, "enchantable/durability", id);
            add(tags, "enchantable/vanishing", id);

            // Vanilla axes take Sharpness, Smite and Bane of Arthropods, so they
            // sit in the weapon tags too. The other three mining tools do not.
            if (type.equals("axe")) {
                add(tags, "enchantable/weapon", id);
                add(tags, "enchantable/sharp_weapon", id);
                add(tags, "enchantable/fire_aspect", id);
            }
        }

        addC(tags, "tools", id);
        addC(tags, "tools/mining_tool", id);
        if (type.equals("axe")) addC(tags, "tools/melee_weapon", id);
        if (data.enchantable) addC(tags, "enchantables", id);
    }

    // ── Weapons ──────────────────────────────────────────────────────────────

    private static void weapon(TagFileBuilder tags, String itemId, String type, GearData data) {
        String id = "customgear:" + itemId;

        switch (type) {
            case "sword" -> {
                add(tags, "swords", id);
                add(tags, "breaks_decorated_pots", id);
                if (data.enchantable) {
                    add(tags, "enchantable/sword", id);
                    add(tags, "enchantable/sharp_weapon", id);
                    add(tags, "enchantable/weapon", id);
                    add(tags, "enchantable/fire_aspect", id);
                    add(tags, "enchantable/durability", id);
                    add(tags, "enchantable/vanishing", id);
                }
                addC(tags, "tools", id);
                addC(tags, "tools/melee_weapon", id);
            }
            case "bow", "crossbow" -> {
                if (data.enchantable) {
                    add(tags, "enchantable/" + type, id);
                    add(tags, "enchantable/durability", id);
                    add(tags, "enchantable/vanishing", id);
                }
                addC(tags, "tools", id);
                addC(tags, "tools/ranged_weapon", id);
                addC(tags, "tools/" + type, id);
            }
            case "shield" -> {
                // Shields take almost nothing: Unbreaking, Mending, the curses.
                if (data.enchantable) {
                    add(tags, "enchantable/durability", id);
                    add(tags, "enchantable/vanishing", id);
                }
                addC(tags, "tools", id);
                addC(tags, "tools/shield", id);
            }
            default -> { }
        }

        if (data.enchantable) addC(tags, "enchantables", id);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Vanilla tag: required:true, this is our own content and always exists. */
    private static void add(TagFileBuilder tags, String path, String contentId) {
        tags.add("item", ResourceLocation.fromNamespaceAndPath("minecraft", path), contentId, true);
    }

    /** Removes from a vanilla tag — see TagFileBuilder.remove. */
    @SuppressWarnings("SameParameterValue")
    private static void remove(TagFileBuilder tags, String path, String contentId) {
        tags.remove("item", ResourceLocation.fromNamespaceAndPath("minecraft", path), contentId);
    }

    /** Convention (c:) tag — community namespace, not owned by any mod. */
    private static void addC(TagFileBuilder tags, String path, String contentId) {
        tags.add("item", ResourceLocation.fromNamespaceAndPath("c", path), contentId, true);
    }
}