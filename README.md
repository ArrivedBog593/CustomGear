# CustomGear

**CustomGear** is a data-driven NeoForge mod for Minecraft 1.21.1 that allows server owners, modpack creators, and players to add fully custom armor sets, weapons, and tools — all through simple JSON files. No coding required.

---

## Features

- Add custom **armor sets** with per-piece defense, durability, toughness, and knockback resistance
- Add custom **tool sets** (pickaxe, axe, shovel, hoe) and individual tools with custom damage, speed, and mining speed
- Add custom **weapon sets** (sword, bow, crossbow, shield) and individual weapons
- Add custom **bows** with configurable arrow damage and charge speed
- Add custom **crossbows** with configurable arrow damage and charge speed
- Add custom **shields** with configurable durability
- Per-piece armor effects (e.g., helmet gives Night Vision when worn individually)
- Set bonus effects when wearing the required number of armor pieces
- Held effects per tool/weapon (e.g., pickaxe gives Haste, sword gives Strength)
- Full multi-language support — define the full item name per language with no format restrictions
- Custom textures with a flexible path system, or reuse models from other mods
- JSON files can be organized in any subfolder structure inside `.minecraft/customgear/`
- Compatible with JEI
- All items are enchantable with vanilla and modded enchantments
- `/customgear reload` command to reload names, textures, and effects without restarting

---

## Installation

1. Download and install [NeoForge 1.21.1](https://neoforged.net/)
2. Place `customgear-1.0.jar` in your `mods/` folder
3. Launch the game once to generate the `customgear/` folder inside `.minecraft/`
4. Add your JSON files to `.minecraft/customgear/`
5. Restart the game

> **JSON syntax note:** Standard JSON does not allow trailing commas. A trailing comma after the last element in an object or array will cause the file to be silently skipped on load.

---

## JSON File Structure

All JSON files go inside `.minecraft/customgear/`. Each file defines one item set or weapon. Files can be organized in any subfolder structure you prefer.

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

> **Note:** `tool_set` supports `pickaxe`, `axe`, `shovel`, and `hoe` only. To add a sword alongside tools, use a separate `weapon_set` or individual `sword` file.

```json
{
  "id": "my_tools",
  "type": "tool_set",
  "tool_names": {
    "en_us": {
      "pickaxe": "My Pickaxe",
      "axe": "My Axe",
      "shovel": "My Shovel",
      "hoe": "My Hoe"
    },
    "es_mx": {
      "pickaxe": "Mi Pico",
      "axe": "Mi Hacha",
      "shovel": "Mi Pala",
      "hoe": "Mi Azada"
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
      "hoe":     "item/tools/hoe.png"
    }
  }
}
```

### Weapon Set — Full Example

```json
{
  "id": "my_weapons",
  "type": "weapon_set",
  "weapon_names": {
    "en_us": {
      "sword":    "My Sword",
      "bow":      "My Bow",
      "crossbow": "My Crossbow",
      "shield":   "My Shield"
    },
    "es_mx": {
      "sword":    "Mi Espada",
      "bow":      "Mi Arco",
      "crossbow": "Mi Ballesta",
      "shield":   "Mi Escudo"
    }
  },
  "weapons": {
    "sword": {
      "durability": 2031,
      "attack_damage": 8.0,
      "attack_speed": 1.6,
      "held_effects": [
        { "effect": "minecraft:strength", "amplifier": 1 }
      ]
    },
    "bow": {
      "durability": 384,
      "arrow_damage": 8.0,
      "arrow_damage_bonus": 2.0,
      "arrow_damage_multiplier": 1.3,
      "charge_speed": 1.0
    },
    "crossbow": {
      "durability": 465,
      "arrow_damage": 11.0,
      "arrow_damage_multiplier": 1.5,
      "charge_speed": 1.0
    },
    "shield": {
      "durability": 336
    }
  },
  "enchantable": true,
  "enchantability": 15,
  "texture": {
    "mode": "reference",
    "refs": {
      "sword":    "minecraft:item/netherite_sword",
      "bow":      "minecraft:item/bow",
      "crossbow": "minecraft:item/crossbow",
      "shield":   "minecraft:item/shield"
    }
  }
}
```

### Individual Sword — Full Example

```json
{
  "id": "my_sword",
  "type": "sword",
  "names": {
    "en_us": "My Custom Sword",
    "es_mx": "Mi Espada Personalizada"
  },
  "durability": 1561,
  "attack_damage": 8.0,
  "attack_damage_bonus": 0.0,
  "attack_speed": 1.6,
  "enchantable": true,
  "enchantability": 15,
  "held_effects": [
    { "effect": "minecraft:strength", "amplifier": 1 }
  ],
  "texture": {
    "mode": "reference",
    "refs": {
      "sword": "minecraft:item/netherite_sword"
    }
  }
}
```

### Individual Bow — Full Example

```json
{
  "id": "my_bow",
  "type": "bow",
  "names": {
    "en_us": "My Custom Bow",
    "es_mx": "Mi Arco Personalizado"
  },
  "durability": 600,
  "arrow_damage": 10.0,
  "arrow_damage_bonus": 1.0,
  "arrow_damage_multiplier": 1.2,
  "charge_speed": 1.0,
  "enchantable": true,
  "enchantability": 15,
  "texture": {
    "mode": "reference",
    "refs": {
      "bow": "minecraft:item/bow"
    }
  }
}
```

### Individual Crossbow — Full Example

```json
{
  "id": "my_crossbow",
  "type": "crossbow",
  "names": {
    "en_us": "My Custom Crossbow",
    "es_mx": "Mi Ballesta Personalizada"
  },
  "durability": 465,
  "arrow_damage": 12.0,
  "arrow_damage_multiplier": 1.5,
  "charge_speed": 1.0,
  "enchantable": true,
  "enchantability": 15,
  "texture": {
    "mode": "reference",
    "refs": {
      "crossbow": "minecraft:item/crossbow"
    }
  }
}
```

---

## Field Reference

### Common Fields

| Field            | Type    | Description                                                                                                       |
|------------------|---------|-------------------------------------------------------------------------------------------------------------------|
| `id`             | String  | Unique identifier. Lowercase letters, numbers, and underscores only.                                              |
| `type`           | String  | `armor_set`, `tool_set`, `weapon_set`, `sword`, `bow`, `crossbow`, `shield`, `pickaxe`, `axe`, `shovel`, or `hoe` |
| `names`          | Map     | Full item name per language (for individual items only — sets use `piece_names`, `tool_names`, or `weapon_names`) |
| `enchantable`    | Boolean | Whether the item can be enchanted                                                                                 |
| `enchantability` | Int     | Higher = better enchantments. Iron = 9, Gold = 25, Diamond = 10                                                   |

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

| Field                       | Type  | Description                                                                               |
|-----------------------------|-------|-------------------------------------------------------------------------------------------|
| `tools`                     | Map   | Defines each tool. Keys: `pickaxe`, `axe`, `shovel`, `hoe`                                |
| `tools.durability`          | Int   | Durability of this tool                                                                   |
| `tools.attack_damage`       | Float | Base attack damage                                                                        |
| `tools.attack_damage_bonus` | Float | Additional attack damage added on top of `attack_damage`                                  |
| `tools.attack_speed`        | Float | Attack speed. Standard values: sword = 1.6, axe = 0.9, shovel = 1.0                       |
| `tools.mining_speed`        | Float | Mining speed. Netherite = 9.0, Diamond = 8.0, Iron = 6.0                                  |
| `tools.harvest_level`       | Int   | 0=Wood, 1=Stone, 2=Iron, 3=Diamond, 4=Netherite                                           |
| `tools.held_effects`        | List  | Effects applied when this tool is held in hand                                            |
| `tools.till_radius`         | Int   | (Hoe only) Radius of blocks to till around the target block. 0 = no area tilling          |
| `tool_names`                | Map   | Full name for each tool type per language. Each language defines all tools independently. |

### Weapon Fields

| Field                             | Type  | Description                                                                  |
|-----------------------------------|-------|------------------------------------------------------------------------------|
| `weapons`                         | Map   | Defines each weapon. Keys: `sword`, `bow`, `crossbow`, `shield`              |
| `weapons.durability`              | Int   | Durability of this weapon                                                    |
| `weapons.attack_damage`           | Float | Base attack damage (sword only)                                              |
| `weapons.attack_damage_bonus`     | Float | Additional attack damage (sword only)                                        |
| `weapons.attack_speed`            | Float | Attack speed (sword only)                                                    |
| `weapons.damage_multiplier`       | Float | Multiplier applied to final attack damage. Default: 1.0                      |
| `weapons.arrow_damage`            | Float | Base arrow damage (bow/crossbow). If 0, uses vanilla calculation             |
| `weapons.arrow_damage_bonus`      | Float | Flat bonus added to arrow damage (bow/crossbow)                              |
| `weapons.arrow_damage_multiplier` | Float | Multiplier applied to arrow damage (bow/crossbow). Default: 1.0              |
| `weapons.charge_speed`            | Float | Charge speed multiplier (bow/crossbow). Values < 1.0 = slower. Default: 1.0  |
| `weapons.held_effects`            | List  | Effects applied when this weapon is held in hand (sword/bow/crossbow/shield) |
| `weapon_names`                    | Map   | Full name for each weapon type per language.                                 |

> **charge_speed note:** Values greater than 1.0 are not currently supported for crossbows and will be clamped to vanilla speed. Only values ≤ 1.0 (slower than vanilla) take effect.

### Fields for individual weapons (not in a weapon_set)

Individual weapons (`type: "sword"`, `type: "bow"`, etc.) use the same fields as above but at the top level instead of nested under `weapons`:

```json
{
  "id": "my_bow",
  "type": "bow",
  "durability": 384,
  "arrow_damage": 8.0,
  "charge_speed": 1.0,
  ...
}
```

### Attack Damage Reference

| Vanilla Weapon    | attack_damage |
|-------------------|---------------|
| Wooden Sword      | 4.0           |
| Stone Sword       | 5.0           |
| Iron Sword        | 6.0           |
| Diamond Sword     | 7.0           |
| Netherite Sword   | 8.0           |

### Effect Object

| Field       | Type   | Description                                                             |
|-------------|--------|-------------------------------------------------------------------------|
| `effect`    | String | Effect ID in `namespace:effect_name` format (e.g. `minecraft:strength`) |
| `amplifier` | Int    | Effect level minus 1. `0` = Level I, `1` = Level II, etc.               |

### Texture Fields

| Field                  | Type   | Description                                                                                                                                                                                                                                                               |
|------------------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode`         | String | `default`, `custom`, or `reference`                                                                                                                                                                                                                                       |
| `texture.refs`         | Map    | For `custom`: relative path to a PNG file inside `.minecraft/customgear/`. For `reference`: full model resource location from another mod (e.g. `minecraft:item/netherite_sword`). **For tools and weapons in reference mode, this is a model path, not a texture path.** |
| `texture.armor_layers` | Map    | Layer textures for the armor model (`layer_1`, `layer_2`). Required for `custom` mode armors. In `reference` mode, use the armor material resource location.                                                                                                              |

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

**For armor sets:**
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

**For tool sets:**
```json
"texture": {
  "mode": "custom",
  "refs": {
    "pickaxe": "item/tools/pickaxe.png",
    "axe":     "item/tools/axe.png",
    "shovel":  "item/tools/shovel.png",
    "hoe":     "item/tools/hoe.png"
  }
}
```

**For weapon sets (bow requires pulling animation frames):**
```json
"texture": {
  "mode": "custom",
  "refs": {
    "sword":         "item/weapons/sword.png",
    "bow":           "item/weapons/bow.png",
    "bow_pulling_0": "item/weapons/bow_pulling_0.png",
    "bow_pulling_1": "item/weapons/bow_pulling_1.png",
    "bow_pulling_2": "item/weapons/bow_pulling_2.png"
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
│   ├── tools/
│   │   ├── pickaxe.png
│   │   └── ...
│   └── weapons/
│       ├── sword.png
│       └── ...
├── armor1.json
├── tools1.json
└── weapons/
    └── weapons1.json
```

### `reference`
Reuses models from another installed mod. For tools and weapons, `refs` must point to an **item model** resource location (not a texture). For armor `refs`, point to the item inventory texture.

**For tool sets:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "pickaxe": "minecraft:item/netherite_pickaxe",
    "axe":     "minecraft:item/netherite_axe",
    "shovel":  "minecraft:item/netherite_shovel",
    "hoe":     "minecraft:item/netherite_hoe"
  }
}
```

**For weapon sets:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "sword":    "minecraft:item/netherite_sword",
    "bow":      "minecraft:item/bow",
    "crossbow": "minecraft:item/crossbow",
    "shield":   "minecraft:item/shield"
  }
}
```

**For armor sets:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "helmet":     "minecraft:item/netherite_helmet",
    "chestplate": "minecraft:item/netherite_chestplate",
    "leggings":   "minecraft:item/netherite_leggings",
    "boots":      "minecraft:item/netherite_boots"
  },
  "armor_layers": {
    "layer_1": "minecraft:models/armor/netherite_layer_1",
    "layer_2": "minecraft:models/armor/netherite_layer_2"
  }
}
```

> **Finding resource locations:** Open the mod's `.jar` file as a ZIP and navigate to `assets/<modid>/models/item/`. The resource location follows the pattern `modid:item/filename` without the `.json` extension.

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
- Textures and models
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
- Tools: `customgear:my_tools_pickaxe`, `customgear:my_tools_axe`, etc.
- Weapons: `customgear:my_weapons_sword`, `customgear:my_weapons_bow`, etc.
- Individual items: `customgear:my_sword`, `customgear:my_bow`, etc.

---

## Compatibility

- Minecraft 1.21.1
- NeoForge 21.1.x
- JEI (optional, recommended)
- Compatible with KubeJS for recipes
- Modded enchantments work automatically on enchantable items
- Models from any installed mod can be referenced with `reference` mode

---

## License

MIT License — see LICENSE file for details.