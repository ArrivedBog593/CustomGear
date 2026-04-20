# CustomGear

**CustomGear** is a data-driven NeoForge mod for Minecraft 1.21.1 that allows server owners, modpack creators, and players to add fully custom armor sets, weapons, and tools — all through simple JSON files. No coding required.

---

## Features

- Add custom armor sets with per-piece defense, durability, toughness, and knockback resistance
- Add custom weapons and tool sets (sword, pickaxe, axe, shovel, hoe) with custom damage, speed, and mining speed
- Per-piece armor effects (e.g., helmet gives Night Vision when worn individually)
- Set bonus effects when wearing the full armor set
- Held effects per tool (e.g., pickaxe gives Haste, sword gives Strength)
- Full multi-language support — define the full item name per language with no format restrictions
- Custom textures with a flexible path system, or reuse textures from other mods
- JSON files can be organized in any subfolder structure inside `.minecraft/customgear/`
- Compatible with JEI
- All items are enchantable with vanilla and modded enchantments
- `/customgear reload` command to reload names, textures, and effects without restarting

---

## Installation

1. Download and install [NeoForge 1.21.1](https://neoforged.net/)
2. Place `customgear-1.0.0.jar` in your `mods/` folder
3. Launch the game once to generate the `customgear/` folder inside `.minecraft/`
4. Add your JSON files to `.minecraft/customgear/`
5. Restart the game

---

## JSON File Structure

All JSON files go inside `.minecraft/customgear/`. Each file defines one armor set or one tool set. Files can be organized in any subfolder structure you prefer.

### Armor Set — Full Example

```json
{
  "id": "my_armor",
  "type": "armor_set",
  "piece_names": {
    "en_us": {
      "helmet": "My Armor Helmet",
      "chestplate": "My Armor Chestplate",
      "leggings": "My Armor Leggings",
      "boots": "My Armor Boots"
    },
    "es_mx": {
      "helmet": "Casco de Mi Armadura",
      "chestplate": "Pechera de Mi Armadura",
      "leggings": "Pantalones de Mi Armadura",
      "boots": "Botas de Mi Armadura"
    },
    "ja_jp": {
      "helmet": "マイアーマーヘルメット",
      "chestplate": "マイアーマーチェストプレート",
      "leggings": "マイアーマーレギンス",
      "boots": "マイアーマーブーツ"
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
    "mode": "custom",
    "armor_layers": {
      "layer_1": "models/my_armor/layer_1.png",
      "layer_2": "models/my_armor/layer_2.png"
    },
    "refs": {
      "helmet":     "item/armor/helmet.png",
      "chestplate": "item/armor/chestplate.png",
      "leggings":   "item/armor/leggings.png",
      "boots":      "item/armor/boots.png"
    }
  }
}
```

### Tool Set — Full Example

```json
{
  "id": "my_tools",
  "type": "tool_set",
  "tool_names": {
    "en_us": {
      "pickaxe": "My Pickaxe",
      "axe": "My Axe",
      "shovel": "My Shovel",
      "hoe": "My Hoe",
      "sword": "My Sword"
    },
    "es_mx": {
      "pickaxe": "Mi Pico",
      "axe": "Mi Hacha",
      "shovel": "Mi Pala",
      "hoe": "Mi Azada",
      "sword": "Mi Espada"
    },
    "ja_jp": {
      "pickaxe": "マイピッケル",
      "axe": "マイ斧",
      "shovel": "マイシャベル",
      "hoe": "マイ鍬",
      "sword": "マイソード"
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
      "till_radius": 3,
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
    "mode": "custom",
    "refs": {
      "pickaxe": "item/tools/pickaxe.png",
      "axe":     "item/tools/axe.png",
      "shovel":  "item/tools/shovel.png",
      "hoe":     "item/tools/hoe.png",
      "sword":   "item/tools/sword.png"
    }
  }
}
```

---

## Field Reference

### Common Fields

| Field            | Type    | Description                                                          |
|------------------|---------|----------------------------------------------------------------------|
| `id`             | String  | Unique identifier. Lowercase letters, numbers, and underscores only. |
| `type`           | String  | `armor_set` or `tool_set`                                            |
| `enchantable`    | Boolean | Whether the item can be enchanted                                    |
| `enchantability` | Int     | Higher = better enchantments. Iron = 9, Gold = 25, Diamond = 10      |

### Armor Fields

| Field                         | Type   | Description                                                                                 |
|-------------------------------|--------|---------------------------------------------------------------------------------------------|
| `pieces`                      | Map    | Defines each armor piece. Keys: `helmet`, `chestplate`, `leggings`, `boots`                 |
| `pieces.durability`           | Int    | Durability of this piece                                                                    |
| `pieces.defense`              | Int    | Armor points this piece provides                                                            |
| `pieces.toughness`            | Float  | Armor toughness per piece. Netherite = 3.0                                                  |
| `pieces.knockback_resistance` | Float  | Knockback resistance. Max is 1.0 (full resistance)                                          |
| `piece_names`                 | Map    | Full name for each piece per language. Each language defines all four pieces independently. |
| `piece_effects`               | Map    | Effects applied when a specific piece is worn individually                                  |
| `set_bonus`                   | Object | Effects applied when the required number of pieces are worn                                 |
| `set_bonus.required_pieces`   | Int    | Number of pieces needed to activate the bonus                                               |
| `set_bonus.effects`           | List   | List of effects to apply when set is complete                                               |

### Tool Fields

| Field                 | Type  | Description                                                                               |
|-----------------------|-------|-------------------------------------------------------------------------------------------|
| `tools`               | Map   | Defines each tool. Keys: `pickaxe`, `axe`, `shovel`, `hoe`, `sword`                       |
| `tools.durability`    | Int   | Durability of this tool                                                                   |
| `tools.attack_damage` | Float | Bonus attack damage                                                                       |
| `tools.attack_speed`  | Float | Attack speed. Standard sword = 1.6                                                        |
| `tools.mining_speed`  | Float | Mining speed. Netherite = 9.0, Diamond = 8.0                                              |
| `tools.harvest_level` | Int   | 0=Wood, 1=Stone, 2=Iron, 3=Diamond, 4=Netherite                                           |
| `tools.held_effects`  | List  | Effects applied when this specific tool is held in hand                                   |
| `tools.till_radius`   | Int   | (Hoe only) Radius of blocks to till around the target block. 0 = no area tilling          |
| `tool_names`          | Map   | Full name for each tool type per language. Each language defines all tools independently. |

### Effect Object

| Field       | Type   | Description                                                             |
|-------------|--------|-------------------------------------------------------------------------|
| `effect`    | String | Effect ID in `namespace:effect_name` format (e.g. `minecraft:strength`) |
| `amplifier` | Int    | Effect level minus 1. `0` = Level I, `1` = Level II, etc.               |

### Texture Fields

| Field                  | Type   | Description                                                                                                                                  |
|------------------------|--------|----------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode`         | String | `default`, `custom`, or `reference`                                                                                                          |
| `texture.refs`         | Map    | Texture path per piece/tool. For `custom`: relative path from `.minecraft/customgear/`. For `reference`: resource location from another mod. |
| `texture.armor_layers` | Map    | Layer textures for the armor model (`layer_1`, `layer_2`). Required for armor in `custom` and `reference` modes.                             |

---

## Texture Modes

### `default`
Uses iron armor/tool textures as placeholders. Good for testing.
```json
"texture": {
  "mode": "default"
}
```

### `custom`
Uses your own PNG files. All paths in `refs` and `armor_layers` are relative to `.minecraft/customgear/`.

**For armor sets, you must provide:**
- armor_layers: Layer textures for the armor model
- refs: Individual texture paths for each armor piece
```json
"texture": {
  "mode": "custom",
  "armor_layers": {
    "layer_1": "models/my_armor/layer_1.png",
    "layer_2": "models/my_armor/layer_2.png"
  },
  "refs": {
    "helmet":     "item/armor/helmet.png",
    "chestplate": "item/armor/chestplate.png",
    "leggings":   "item/armor/leggings.png",
    "boots":      "item/armor/boots.png"
  }
}
```

**For tool sets, one PNG per tool:**
- refs: Individual texture paths for each tool
```json
"texture": {
  "mode": "custom",
  "refs": {
    "pickaxe": "item/tools/pickaxe.png",
    "axe":     "item/tools/axe.png",
    "shovel":  "item/tools/shovel.png",
    "hoe":     "item/tools/hoe.png",
    "sword":   "item/tools/sword.png"
  }
}
```

**Example file structure:**
```
.minecraft/customgear/
├── models/
│   └── my_armor/
│       ├── layer_1.png
│       └── layer_2.png
├── item/
│   ├── armor/
│   │   ├── helmet.png
│   │   ├── chestplate.png
│   │   ├── leggings.png
│   │   └── boots.png
│   └── tools/
│       ├── pickaxe.png
│       ├── axe.png
│       ├── shovel.png
│       ├── hoe.png
│       └── sword.png
├── armor1.json
├── tool1.json
└── json/
    ├── armor2.json
    ├── armors/
    │   └── armor3.json
    └── tools/
        └── tool2.json
```

JSON files can be placed in any subfolder — CustomGear scans all subfolders automatically.

### `reference`
Reuses textures from another installed mod. All refs must use the full resource location (`modid:path/to/texture`).

**For tool sets:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "pickaxe": "othermod:item/mytool/pickaxe",
    "axe":     "othermod:item/mytool/axe",
    "shovel":  "othermod:item/mytool/shovel",
    "hoe":     "othermod:item/mytool/hoe",
    "sword":   "othermod:item/mytool/sword"
  }
}
```

**For armor sets:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "helmet":     "othermod:item/myarmor/helmet",
    "chestplate": "othermod:item/myarmor/chestplate",
    "leggings":   "othermod:item/myarmor/leggings",
    "boots":      "othermod:item/myarmor/boots"
  },
  "armor_layers": {
    "layer_1": "othermod:textures/models/armor/myarmor_layer_1",
    "layer_2": "othermod:textures/models/armor/myarmor_layer_2"
  }
}
```

> **Note:** To find the correct resource location for a texture from another mod, open the mod's `.jar` file (it's a ZIP) and navigate to `assets/<modid>/textures/`. The resource location follows the pattern `modid:path/within/textures/folder` without the `.png` extension for `reference` mode.

---

## Effect IDs — Common Vanilla Effects

| Effect          | ID                          |
|-----------------|-----------------------------|
| Speed           | `minecraft:speed`           |
| Haste           | `minecraft:haste`           |
| Strength        | `minecraft:strength`        |
| Jump Boost      | `minecraft:jump_boost`      |
| Regeneration    | `minecraft:regeneration`    |
| Resistance      | `minecraft:resistance`      |
| Fire Resistance | `minecraft:fire_resistance` |
| Night Vision    | `minecraft:night_vision`    |
| Water Breathing | `minecraft:water_breathing` |
| Invisibility    | `minecraft:invisibility`    |
| Slow Falling    | `minecraft:slow_falling`    |
| Health Boost    | `minecraft:health_boost`    |
| Luck            | `minecraft:luck`            |

Effects from other mods also work — use their ID in `modid:effect_name` format.

---

## Commands

| Command              | Permission | Description                                            |
|----------------------|------------|--------------------------------------------------------|
| `/customgear reload` | OP level 2 | Reloads all JSON files and textures without restarting |

### What the reload command updates
- Item names
- Textures
- Held effects (weapons and tools)
- Piece effects and set bonuses (armor)
- Durability display

### What requires a full game restart
- Attack damage and attack speed
- Armor defense, toughness, and knockback resistance
- Mining speed and harvest level
- Adding new items (new JSON files)
- Removing existing items (deleted JSON files)
- Changing item IDs

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