# CustomGear

**CustomGear** is a data-driven NeoForge mod for Minecraft 1.21.1 that allows server owners, modpack creators, and players to add fully custom armor sets, weapons, and tools — all through simple JSON files. No coding required.

---

## Features

- Add custom armor sets with per-piece defense, durability, toughness, and knockback resistance
- Add custom weapons and tool sets (sword, pickaxe, axe, shovel, hoe) with custom damage, speed, and mining speed
- Per-piece armor effects (e.g. helmet gives Night Vision when worn individually)
- Set bonus effects when wearing the full armor set
- Held effects per tool (e.g. pickaxe gives Haste, sword gives Strength)
- Full multi-language support for item names with customizable format per language
- Custom textures, or reuse textures from other mods with flexible reference system
- Compatible with JEI
- All items are enchantable with vanilla and modded enchantments
- `/customgear reload` command to reload JSONs without restarting the game

---

## Installation

1. Download and install [NeoForge 1.21.1](https://neoforged.net/)
2. Place `customgear-1.0.0.jar` in your `mods/` folder
3. Launch the game once to generate the `customgear/` folder inside `.minecraft/`
4. Add your JSON files to `.minecraft/customgear/`
5. Restart the game

---

## JSON File Structure

All JSON files go inside `.minecraft/customgear/`. Each file defines one armor set or one tool set.

### Armor Set — Full Example

```json
{
  "id": "my_armor",
  "type": "armor_set",
  "name": {
    "en_us": "My Armor",
    "es_mx": "Mi Armadura",
    "ja_jp": "マイアーマー"
  },
  "piece_name_format": {
    "en_us": "{name} {piece}",
    "es_mx": "{piece} de {name}",
    "ja_jp": "{name}の{piece}"
  },
  "piece_names": {
    "en_us": {
      "helmet": "Helmet",
      "chestplate": "Chestplate",
      "leggings": "Leggings",
      "boots": "Boots"
    },
    "es_mx": {
      "helmet": "Casco",
      "chestplate": "Pechera",
      "leggings": "Pantalones",
      "boots": "Botas"
    },
    "ja_jp": {
      "helmet": "兜",
      "chestplate": "胸当て",
      "leggings": "脚当て",
      "boots": "靴"
    }
  },
  "pieces": {
    "helmet":     { "durability": 363, "defense": 3, "toughness": 1.0, "knockback_resistance": 0.0 },
    "chestplate": { "durability": 528, "defense": 8, "toughness": 2.0, "knockback_resistance": 0.1 },
    "leggings":   { "durability": 495, "defense": 6, "toughness": 1.5, "knockback_resistance": 0.1 },
    "boots":      { "durability": 429, "defense": 3, "toughness": 1.0, "knockback_resistance": 0.0 }
  },
  "enchantable": true,
  "enchantability": 15,
  "piece_effects": {
    "helmet": [
      { "effect": "minecraft:night_vision", "amplifier": 0 }
    ],
    "chestplate": [
      { "effect": "minecraft:fire_resistance", "amplifier": 0 }
    ],
    "leggings": [
      { "effect": "minecraft:speed", "amplifier": 0 }
    ],
    "boots": [
      { "effect": "minecraft:jump_boost", "amplifier": 0 }
    ]
  },
  "set_bonus": {
    "required_pieces": 4,
    "effects": [
      { "effect": "minecraft:strength", "amplifier": 1 },
      { "effect": "minecraft:resistance", "amplifier": 0 }
    ]
  },
  "texture": {
    "mode": "default"
  }
}
```

### Tool Set — Full Example

```json
{
  "id": "my_tools",
  "type": "tool_set",
  "name": {
    "en_us": "My Tools",
    "es_mx": "Mis Herramientas"
  },
  "tool_name_format": {
    "en_us": "{name} {tool}",
    "es_mx": "{tool} de {name}"
  },
  "tool_names": {
    "en_us": {
      "pickaxe": "Pickaxe",
      "axe": "Axe",
      "shovel": "Shovel",
      "hoe": "Hoe",
      "sword": "Sword"
    },
    "es_mx": {
      "pickaxe": "Pico",
      "axe": "Hacha",
      "shovel": "Pala",
      "hoe": "Azadón",
      "sword": "Espada"
    }
  },
  "tools": {
    "pickaxe": {
      "durability": 8000,
      "attack_damage": 5.0,
      "attack_speed": 1.2,
      "mining_speed": 20.0,
      "harvest_level": 4,
      "held_effects": [
        { "effect": "minecraft:haste", "amplifier": 1 }
      ]
    },
    "axe": {
      "durability": 7000,
      "attack_damage": 8.0,
      "attack_speed": 0.9,
      "mining_speed": 15.0,
      "harvest_level": 4,
      "held_effects": [
        { "effect": "minecraft:strength", "amplifier": 1 }
      ]
    },
    "shovel": {
      "durability": 6000,
      "attack_damage": 4.0,
      "attack_speed": 1.0,
      "mining_speed": 18.0,
      "harvest_level": 4
    },
    "hoe": {
      "durability": 5000,
      "attack_damage": 3.0,
      "attack_speed": 1.0,
      "mining_speed": 16.0,
      "harvest_level": 4,
      "held_effects": [
        { "effect": "minecraft:regeneration", "amplifier": 0 }
      ]
    },
    "sword": {
      "durability": 5000,
      "attack_damage": 20.0,
      "attack_speed": 1.6,
      "held_effects": [
        { "effect": "minecraft:strength", "amplifier": 2 }
      ]
    }
  },
  "enchantable": true,
  "enchantability": 22,
  "texture": {
    "mode": "default"
  }
}
```

---

## Field Reference

### Common Fields

| Field | Type | Description |
|---|---|---|
| `id` | String | Unique identifier. Lowercase letters, numbers, and underscores only. |
| `type` | String | `armor_set` or `tool_set` |
| `name` | Map | Item name per language code (e.g. `en_us`, `es_mx`, `ja_jp`) |
| `enchantable` | Boolean | Whether the item can be enchanted |
| `enchantability` | Int | Higher = better enchantments. Iron = 9, Gold = 25, Diamond = 10 |

### Armor Fields

| Field | Type | Description |
|---|---|---|
| `pieces` | Map | Defines each armor piece. Keys: `helmet`, `chestplate`, `leggings`, `boots` |
| `pieces.durability` | Int | Durability of this piece |
| `pieces.defense` | Int | Armor points this piece provides |
| `pieces.toughness` | Float | Armor toughness per piece. Netherite = 3.0 |
| `pieces.knockback_resistance` | Float | Knockback resistance. Max is 1.0 (full resistance) |
| `piece_name_format` | Map | Format string per language. Use `{name}` and `{piece}` as placeholders |
| `piece_names` | Map | Names for each piece per language |
| `piece_effects` | Map | Effects applied when a specific piece is worn individually |
| `set_bonus` | Object | Effects applied when the required number of pieces are worn |
| `set_bonus.required_pieces` | Int | Number of pieces needed to activate the bonus |
| `set_bonus.effects` | List | List of effects to apply when set is complete |

### Tool Fields

| Field | Type | Description |
|---|---|---|
| `tools` | Map | Defines each tool. Keys: `pickaxe`, `axe`, `shovel`, `hoe`, `sword` |
| `tools.durability` | Int | Durability of this tool |
| `tools.attack_damage` | Float | Bonus attack damage |
| `tools.attack_speed` | Float | Attack speed. Standard sword = 1.6 |
| `tools.mining_speed` | Float | Mining speed. Netherite = 9.0, Diamond = 8.0 |
| `tools.harvest_level` | Int | 0=Wood, 1=Stone, 2=Iron, 3=Diamond, 4=Netherite |
| `tools.held_effects` | List | Effects applied when this specific tool is held in hand |
| `tool_name_format` | Map | Format string per language. Use `{name}` and `{tool}` as placeholders |
| `tool_names` | Map | Names for each tool type per language |

### Effect Object

| Field | Type | Description |
|---|---|---|
| `effect` | String | Effect ID in `namespace:effect_name` format (e.g. `minecraft:strength`) |
| `amplifier` | Int | Effect level minus 1. `0` = Level I, `1` = Level II, etc. |

### Texture Fields

| Field | Type | Description |
|---|---|---|
| `texture.mode` | String | `default`, `custom`, or `reference` |
| `texture.path` | String | Path relative to `.minecraft/customgear/textures/` (for `custom` mode) |
| `texture.ref` | String | Global texture fallback — appends `_tooltype` or `/tooltype` automatically (for `reference` mode) |
| `texture.refs` | Map | Individual texture path per tool/piece — takes priority over `ref` (for `reference` mode) |

---

## Texture Modes

### `default`
Uses iron armor/tool textures as placeholders. Good for testing.

### `custom`
Uses your own PNG files from `.minecraft/customgear/textures/`.

For armor sets, you need two layer files:
```
textures/my_armor_layer_1.png   ← body texture
textures/my_armor_layer_2.png   ← legs texture
```

For tool sets, one PNG per tool:
```
textures/my_tools_pickaxe.png
textures/my_tools_axe.png
textures/my_tools_sword.png
```

### `reference`
Reuses textures from another installed mod. Two options:

**Option A — Individual refs (recommended, most flexible):**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "pickaxe": "mekanismtools:item/steel/pickaxe",
    "axe":     "mekanismtools:item/steel/axe",
    "shovel":  "mekanismtools:item/steel/shovel",
    "hoe":     "mekanismtools:item/steel/hoe",
    "sword":   "mekanismtools:item/steel/sword"
  }
}
```

**Option B — Global ref with automatic suffix:**

If the ref ends with `/`, the tool type is appended directly:
```json
"texture": {
  "mode": "reference",
  "ref": "othermod:item/material/"
}
```
Generates: `othermod:item/material/pickaxe`, `othermod:item/material/sword`, etc.

If the ref does NOT end with `/`, an underscore is appended:
```json
"texture": {
  "mode": "reference",
  "ref": "othermod:item/material"
}
```
Generates: `othermod:item/material_pickaxe`, `othermod:item/material_sword`, etc.

If both `ref` and `refs` are present, `refs` takes priority per tool type.

**For armor sets in reference mode:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "helmet":     "othermod:item/myarmor/helmet",
    "chestplate": "othermod:item/myarmor/chestplate",
    "leggings":   "othermod:item/myarmor/leggings",
    "boots":      "othermod:item/myarmor/boots"
  }
}
```

---

## Effect IDs — Common Vanilla Effects

| Effect | ID |
|---|---|
| Speed | `minecraft:speed` |
| Haste | `minecraft:haste` |
| Strength | `minecraft:strength` |
| Jump Boost | `minecraft:jump_boost` |
| Regeneration | `minecraft:regeneration` |
| Resistance | `minecraft:resistance` |
| Fire Resistance | `minecraft:fire_resistance` |
| Night Vision | `minecraft:night_vision` |
| Water Breathing | `minecraft:water_breathing` |
| Invisibility | `minecraft:invisibility` |
| Slow Falling | `minecraft:slow_falling` |
| Luck | `minecraft:luck` |

Effects from other mods also work — use their ID in `modid:effect_name` format.

---

## Commands

| Command | Permission | Description |
|---|---|---|
| `/customgear reload` | OP level 2 | Reloads all JSON files and textures without restarting |

---

## Adding Recipes

CustomGear does not add crafting recipes by default. To add recipes, use [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) or a similar mod. Item IDs follow the pattern:
- Armor: `customgear:my_armor_helmet`, `customgear:my_armor_chestplate`, etc.
- Tools: `customgear:my_tools_pickaxe`, `customgear:my_tools_sword`, etc.

---

## Compatibility

- Minecraft 1.21.1
- NeoForge 21.1.x
- JEI (optional, recommended)
- Compatible with KubeJS for recipes
- Modded enchantments work automatically on enchantable items
- Textures from any installed mod can be referenced with `reference` mode

---

## License

MIT License — see LICENSE file for details.
