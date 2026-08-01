# Blocks

## Block — Simple Example

```json
{
  "id": "my_ore",
  "type": "block",
  "names": {
    "en_us": "My Ore",
    "es_mx": "Mi Mineral"
  },
  "light_level": 3,
  "destroy_time": 3.0,
  "explosion_resistance": 3.0,
  "map_color": "deepslate",
  "required_tool": "pickaxe",
  "harvest_level": 2,
  "sound": "stone",
  "texture": {
    "mode": "reference",
    "refs": {
      "block": "minecraft:block/diamond_ore"
    }
  }
}
```

## Block with Per-Face Textures — Example

```json
{
  "id": "multi_ore",
  "type": "block",
  "names": {
    "en_us": "Multi Ore",
    "es_mx": "Mineral Multi"
  },
  "texture": {
    "mode": "reference",
    "faces": {
      "top":    "minecraft:block/coal_ore",
      "bottom": "minecraft:block/iron_ore",
      "north":  "minecraft:block/gold_ore",
      "south":  "minecraft:block/redstone_ore",
      "east":   "minecraft:block/emerald_ore",
      "west":   "minecraft:block/diamond_ore"
    }
  }
}
```

Use `"side"` as a shortcut to apply the same texture to all 4 horizontal faces.

## Directional Block — Example

```json
{
  "id": "directional_block",
  "type": "block",
  "directional": true,
  "names": {
    "en_us": "My Machine",
    "es_mx": "Mi Máquina"
  },
  "texture": {
    "mode": "reference",
    "faces": {
      "top":    "minecraft:block/stone",
      "bottom": "minecraft:block/stone",
      "side":   "minecraft:block/stone",
      "north":  "minecraft:block/furnace_front_on"
    }
  }
}
```

The `"north"` face is the front face — it points toward the player when the block is placed.

## Falling Block — Example

```json
{
  "id": "my_falling_block",
  "type": "block",
  "gravity": true,
  "names": {
    "en_us": "My Falling Block",
    "es_mx": "Mi Bloque con Caída"
  },
  "sound": "sand",
  "destroy_time": 0.5,
  "explosion_resistance": 0.5,
  "texture": {
    "mode": "reference",
    "refs": { "block": "minecraft:block/sand" }
  }
}
```

## Block Fields

| Field                  | Type    | Default | Description                                                                                                                                                                            |
|------------------------|---------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `light_level`          | Int     | 0       | Light emitted by the block (0–15)                                                                                                                                                      |
| `destroy_time`         | Float   | 3.0     | Time to break with correct tool in seconds. Obsidian=9.5, bedrock=-1 (unbreakable)                                                                                                     |
| `explosion_resistance` | Float   | 3.0     | Resistance to explosions. Stone=6.0, Obsidian=1200.0                                                                                                                                   |
| `sound`                | String  | `stone` | Sound when placing/breaking/walking. See [Block sound types](block-sounds.md) for all available values. Default: `stone`                                                               |
| `map_color`            | String  | `stone` | Map color for the block. See [Map colors](map-colors.md) for all available values. Default: `stone`                                                                                    |
| `required_tool`        | String  | `none`  | Tool that mines the block efficiently: `pickaxe`, `axe`, `shovel`, `hoe`, `sword` or `none`. Grants mining speed only — use `harvest_level` to gate drops                              |
| `harvest_level`        | Int     | 0       | Tool tier that gates drops: 0=no requirement (drops with anything), 1=stone, 2=iron, 3=diamond, 4=netherite. Level 4 uses the diamond requirement (vanilla has no netherite block tag) |
| `directional`          | Boolean | false   | If true, rotates to face the player when placed. Requires `texture.faces.north` defined                                                                                                |
| `gravity`              | Boolean | false   | If true, falls when unsupported, like sand or gravel. Cannot combine with `directional`                                                                                                |
| `texture.refs.block`   | String  | —       | (Simple blocks) Resource location used for all 6 faces                                                                                                                                 |
| `texture.faces`        | Object  | —       | Per-face texture configuration. Keys: `top`, `bottom`, `north`, `south`, `east`, `west`, `side`                                                                                        |
| `texture.faces.side`   | String  | —       | Shortcut: applies to `north`, `south`, `east`, `west` if not individually defined                                                                                                      |

> With `harvest_level` ≥ 1, the block behaves like vanilla ore: the wrong tool or a lower tier is slow AND drops nothing. With level 0 (or omitted), `required_tool` only grants mining speed — the block drops with anything, like sand. Works with modded tools that follow vanilla tiers. Note: switching `harvest_level` between 0 and ≥1 requires a restart (the drop requirement is baked at startup); adjusting it between 1–4, or changing `required_tool`, applies with `/customgear reload`.
