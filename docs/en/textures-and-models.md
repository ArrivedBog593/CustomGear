# Textures & Models

Three texture modes are available:

| Mode        | Description                                                        |
|-------------|--------------------------------------------------------------------|
| `default`   | Uses vanilla iron/wood textures as placeholders                    |
| `reference` | Reuses the model of another item (vanilla or modded)               |
| `custom`    | Uses your own PNG files placed in the `ultimatecustomgear/` folder |

> ⚠️ **`reference` creates a hard dependency.** The resource locations point at
> files that belong to another mod, so that mod becomes **required** for your
> content to look right. Without it the item shows the missing-texture
> checkerboard, and a 3D armor model declared this way simply won't render.
> Vanilla references (`minecraft:...`) are always safe. If you want your pack to
> stand on its own, use `custom` and ship the PNGs inside it.

## Reference mode example

```json
{
  "texture": {
    "mode": "reference",
    "refs": {
      "sword": "minecraft:item/netherite_sword"
    }
  }
}
```

For bows and crossbows, you can optionally include custom pulling/loading frame models:

```json
{
  "texture": {
    "mode": "reference", 
    "refs": {
      "bow":           "othermod:item/epic_bow", 
      "bow_pulling_0": "othermod:item/epic_bow_pulling_0", 
      "bow_pulling_1": "othermod:item/epic_bow_pulling_1", "bow_pulling_2": "othermod:item/epic_bow_pulling_2"
    }
  }
}
```

## Texture Fields

| Field                | Type   | Description                                                                                                                                                                                                         |
|----------------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode`       | String | `default`, `custom`, or `reference`                                                                                                                                                                                 |
| `texture.refs`       | Map    | For `custom`: relative path to a PNG inside `ultimatecustomgear/`. For `reference`: full resource location                                                                                                          |
| `armor_layers`       | Map    | (Armor sets only) `layer_1` and `layer_2` paths for the in-world armor texture. The special value `"transparent"` makes the armor invisible when worn (stats and effects intact); in reference mode set both layers |
| `texture.refs.block` | String | (Simple blocks) Resource location used for all 6 faces via `cube_all`                                                                                                                                               |
| `texture.faces`      | Object | (Blocks only) Per-face texture configuration. Keys: `top`, `bottom`, `north`, `south`, `east`, `west`, `side`                                                                                                       |
| `texture.faces.side` | String | Shortcut: applies to `north`, `south`, `east`, `west` if not individually defined                                                                                                                                   |

> **`refs` vs `faces`:** `refs` handles everything — a single texture with the `all` key (`"refs": { "all": "..." }`), or per-face textures with the face keys (`top`, `bottom`, `north`, `south`, `east`, `west`, `side`). `faces` is a legacy alias that only works for per-face textures. Use `refs`. (The `block` key is a legacy alias of `all`.) Note: `all`/`block` only work inside `refs`, never inside `faces`.

## 3D Armor Models (GeckoLib)

With [GeckoLib](https://www.curseforge.com/minecraft/mc-mods/geckolib) installed,
armor can render as a full 3D model when worn — horns, shoulder pads, capes,
even animations — instead of the flat vanilla layers.

Model your armor in [Blockbench](https://www.blockbench.net/) using the
**GeckoLib Animated Model** format, using the standard armor bone names
(`armorHead`, `armorBody`, `armorRightArm`, `armorLeftArm`, `armorRightLeg`,
`armorLeftLeg`, `armorRightBoot`, `armorLeftBoot`), then declare the exported
files in `texture.armor_3d`:

```json
{
  "texture": {
    "mode": "custom", 
    "refs": {
      "helmet": "textures/my_helmet_icon.png", 
      "chestplate": "textures/my_chestplate_icon.png", 
      "leggings": "textures/my_leggings_icon.png", 
      "boots": "textures/my_boots_icon.png"
    }, 
    "armor_layers": {
      "layer_1": "textures/my_armor_layer_1.png",
      "layer_2": "textures/my_armor_layer_2.png"
    }, 
    "armor_3d": {
      "model": "models/my_armor.geo.json",
      "texture": "textures/my_armor_3d.png",
      "animation": "models/my_armor.animation.json"
    }
  }
}
```

| Field       | Required | Description                                                             |
|-------------|----------|-------------------------------------------------------------------------|
| `model`     | Yes      | The `.geo.json` exported from Blockbench                                |
| `texture`   | Yes      | PNG painted for that model's UV layout (not the item icon, not a layer) |
| `animation` | No       | `.animation.json`; without it the model is static                       |

**How the three texture systems combine:**

| Declared                                   | With GeckoLib | Without GeckoLib       |
|--------------------------------------------|---------------|------------------------|
| `armor_layers` only                        | Flat layers   | Flat layers            |
| `armor_layers` + `armor_3d`                | **3D model**  | Flat layers (fallback) |
| `armor_3d` only                            | **3D model**  | Vanilla iron layers    |
| `armor_layers: "transparent"` + `armor_3d` | **3D model**  | Invisible armor        |

> Always declare `armor_layers` alongside `armor_3d` — it is your safety net
> for instances without GeckoLib. Declaring only `armor_3d` will not break
> anything, but the armor falls back to the vanilla iron layers, which is
> almost never what you want. `refs` still controls the inventory icon, which
> is always 2D. The 3D model replaces the layers when active; they are never
> drawn together.

**Modes:** in `custom` mode the three values are paths to your own files
(config folder or pack zips, like every other custom texture). In `reference`
mode they are resource locations of assets shipped by another mod
(e.g. `"othermod:geo/armor/their_armor.geo.json"`) — nothing is copied, but
**that mod becomes required** for your armor to render.

GeckoLib is an optional dependency: the mod runs fine without it.
3D rendering is baked at registration — adding or removing `armor_3d`
requires a restart.
