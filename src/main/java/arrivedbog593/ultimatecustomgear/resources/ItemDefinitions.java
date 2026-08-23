package arrivedbog593.ultimatecustomgear.resources;

import java.nio.charset.StandardCharsets;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.itemDefinitionLoc;

/**
 * Writes the ITEM DEFINITIONS — {@code assets/customgear/items/<id>.json}.
 * <p>
 * WHY THIS CLASS EXISTS AT ALL. Up to 1.21.1 one file said everything: a model
 * under {@code models/item/} carried the geometry, the textures AND an
 * {@code overrides} array deciding which variant to draw. That array is gone.
 * Choosing a model is now a separate file, in a separate folder, with a
 * different grammar — and an item with no file here renders as MISSING however
 * correct its model is.
 * <p>
 * The grammar is a small tree of typed nodes rather than a flat list of
 * predicates:
 * <ul>
 *   <li>{@code minecraft:model} — draw this model, the leaf of every branch</li>
 *   <li>{@code minecraft:condition} — a boolean, e.g. is the holder using it</li>
 *   <li>{@code minecraft:range_dispatch} — a number with thresholds, e.g. draw
 *       progress</li>
 *   <li>{@code minecraft:select} — a value with cases, e.g. what a crossbow is
 *       loaded with</li>
 *   <li>{@code minecraft:special} — hand over to a built-in renderer, which is
 *       how a shield, a chest or a shulker box get a shape no model can hold</li>
 * </ul>
 * That tree is what replaced the predicates this mod used to register from Java
 * (see ClientSetup): the animation is data now, and nothing has to run on client
 * start to make a bow bend.
 */
public final class ItemDefinitions {

    private ItemDefinitions() {}

    private static void write(PackSink pack, String itemId, String json) {
        pack.addRaw(itemDefinitionLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /** One model, no conditions — the common case. */
    public static void plain(PackSink pack, String itemId, String modelRef) {
        write(pack, itemId, """
        {
          "model": {
            "type": "minecraft:model",
            "model": "%s"
          }
        }
        """.formatted(modelRef));
    }

    /**
     * A bow: not drawing at all, or drawing at one of three stages.
     * <p>
     * The thresholds are vanilla's, and the SCALE is what makes them mean
     * anything: {@code use_duration} counts ticks, so 0.05 turns the first
     * twenty into 0..1. The old {@code pull} predicate did that division in
     * Java, which is why ClientSetup had to compute it per bow.
     */
    public static void bow(PackSink pack, String itemId,
                           String base, String p0, String p1, String p2) {
        write(pack, itemId, """
        {
          "model": {
            "type": "minecraft:condition",
            "property": "minecraft:using_item",
            "on_false": { "type": "minecraft:model", "model": "%s" },
            "on_true": {
              "type": "minecraft:range_dispatch",
              "property": "minecraft:use_duration",
              "scale": 0.05,
              "fallback": { "type": "minecraft:model", "model": "%s" },
              "entries": [
                { "threshold": 0.65, "model": { "type": "minecraft:model", "model": "%s" } },
                { "threshold": 0.9,  "model": { "type": "minecraft:model", "model": "%s" } }
              ]
            }
          }
        }
        """.formatted(base, p0, p1, p2));
    }

    /**
     * A crossbow: what it is loaded with wins over how far it is drawn, which is
     * why the select sits OUTSIDE the condition. The old override list said the
     * same thing by ordering — charged after pulling — and got it wrong the
     * moment anyone reordered the array.
     * <p>
     * {@code minecraft:crossbow/pull} is already 0..1, so unlike the bow there
     * is no scale to apply.
     */
    public static void crossbow(PackSink pack, String itemId, String base,
                                String p0, String p1, String p2,
                                String arrow, String firework) {
        write(pack, itemId, """
        {
          "model": {
            "type": "minecraft:select",
            "property": "minecraft:charge_type",
            "cases": [
              { "when": "arrow",  "model": { "type": "minecraft:model", "model": "%s" } },
              { "when": "rocket", "model": { "type": "minecraft:model", "model": "%s" } }
            ],
            "fallback": {
              "type": "minecraft:condition",
              "property": "minecraft:using_item",
              "on_false": { "type": "minecraft:model", "model": "%s" },
              "on_true": {
                "type": "minecraft:range_dispatch",
                "property": "minecraft:crossbow/pull",
                "fallback": { "type": "minecraft:model", "model": "%s" },
                "entries": [
                  { "threshold": 0.58, "model": { "type": "minecraft:model", "model": "%s" } },
                  { "threshold": 1.0,  "model": { "type": "minecraft:model", "model": "%s" } }
                ]
              }
            }
          }
        }
        """.formatted(arrow, firework, base, p0, p1, p2));
    }

    /**
     * A shield: a mesh no baked model can express, so the drawing is handed to
     * vanilla's shield renderer. The base model still matters — it supplies the
     * display transforms and the particle texture.
     * <p>
     * The scale of -1 on Y and Z is vanilla's, and it is not decoration: the
     * shield mesh comes from entity rendering, where those axes run the other
     * way.
     */
    public static void shield(PackSink pack, String itemId, String base, String blocking) {
        write(pack, itemId, """
        {
          "model": {
            "type": "minecraft:condition",
            "property": "minecraft:using_item",
            "on_false": {
              "type": "minecraft:special",
              "base": "%s",
              "model": { "type": "minecraft:shield" }
            },
            "on_true": {
              "type": "minecraft:special",
              "base": "%s",
              "model": { "type": "minecraft:shield" }
            },
            "transformation": {
              "translation": [0.0, 0.0, 0.0],
              "left_rotation": [0.0, 0.0, 0.0, 1.0],
              "scale": [1.0, -1.0, -1.0],
              "right_rotation": [0.0, 0.0, 0.0, 1.0]
            }
          }
        }
        """.formatted(base, blocking));
    }

    /**
     * A chest in the hand, the inventory, on the ground or in an item frame.
     * <p>
     * This is what the deleted BEWLR used to do. The sprite is a NAME in the
     * chest atlas, not a path — see ContainerTextures — and the lid is drawn
     * closed, because an item has no opener count.
     */
    public static void chest(PackSink pack, String itemId, String base, String sprite) {
        write(pack, itemId, """
        {
          "model": {
            "type": "minecraft:special",
            "base": "%s",
            "model": {
              "type": "minecraft:chest",
              "texture": "%s"
            }
          }
        }
        """.formatted(base, sprite));
    }

    /**
     * A shulker box item. Same idea as the chest, with vanilla's transform: the
     * model is authored around the block's bottom centre, so it has to be lifted
     * and shrunk a hair to sit inside the item's own box.
     */
    public static void shulkerBox(PackSink pack, String itemId, String base, String sprite) {
        write(pack, itemId, """
        {
          "model": {
            "type": "minecraft:special",
            "base": "%s",
            "model": {
              "type": "minecraft:shulker_box",
              "texture": "%s"
            },
            "transformation": {
              "translation": [0.5, 1.4995, 0.5],
              "left_rotation": [1.0, 0.0, 0.0, 0.0],
              "scale": [0.9995, 0.9995, 0.9995],
              "right_rotation": [0.0, 0.0, 0.0, 1.0]
            }
          }
        }
        """.formatted(base, sprite));
    }
}
