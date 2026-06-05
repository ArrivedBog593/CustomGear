# CustomGear

**CustomGear** is a data-driven NeoForge mod for Minecraft 1.21.1 that allows server owners, modpack creators, and players to add fully custom armor sets, weapons, and tools — all through simple JSON files. No coding required.

---

## Features

- Add custom **armor sets** with per-piece defense, durability, toughness, and knockback resistance
- Add custom **tool sets** (pickaxe, axe, shovel, hoe) and individual tools with custom damage, speed, and mining speed
- Add custom **weapon sets** (sword, bow, crossbow, shield) and individual weapons
- Add custom **bows** with configurable arrow damage and charge speed, with full drawing animation
- Add custom **crossbows** with configurable arrow damage and charge speed, with full loading animation
- Add custom **shields** with configurable durability and full 3D rendering in hand and inventory
- Per-piece armor effects (e.g., helmet gives Night Vision when worn individually)
- Set bonus effects when wearing the required number of armor pieces
- Held effects per tool/weapon (e.g., pickaxe gives Haste, sword gives Strength)
- **Native recipe system** — define crafting recipes directly in JSON files, no external mods needed
- Full multi-language support — define the full item name per language with no format restrictions
- Custom textures with a flexible path system, or reuse models from other mods
- JSON files can be organized in any subfolder structure inside `.minecraft/customgear/`
- Compatible with JEI — recipes are fully visible
- All items are enchantable with vanilla and modded enchantments
- `/customgear reload` command to reload names, textures, and effects without restarting

---

## Installation

1. Download and install [NeoForge 1.21.1](https://neoforged.net/)
2. Place `customgear-1.1.0.jar` in your `mods/` folder
3. Launch the game once to generate the `customgear/` folder inside `.minecraft/`
4. Add your JSON files to `.minecraft/customgear/`
5. Restart the game

> **JSON syntax note:** Standard JSON does not allow trailing commas. A trailing comma after the last element in an object or array will cause the file to be silently skipped on load.

---

## JSON File Structure

All JSON files go inside `.minecraft/customgear/`. Each file defines one item, set, block, or fluid. Files can be organized in any subfolder structure you prefer.

### Armor Set — Full Example

```json
{
  "id": "my_armor",
  "type": "armor_set",
  "piece_names": {
    "en_us": {
      "helmet":     "My Armor Helmet",
      "chestplate": "My Armor Chestplate",
      "leggings":   "My Armor Leggings",
      "boots":      "My Armor Boots"
    },
    "es_mx": {
      "helmet":     "Casco de Mi Armadura",
      "chestplate": "Pechera de Mi Armadura",
      "leggings":   "Pantalones de Mi Armadura",
      "boots":      "Botas de Mi Armadura"
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
    "helmet": [ { "effect": "minecraft:night_vision", "amplifier": 0 } ],
    "boots":  [ { "effect": "minecraft:jump_boost",   "amplifier": 0 } ]
  },
  "set_bonus": {
    "required_pieces": 4,
    "effects": [
      { "effect": "minecraft:strength",   "amplifier": 1 },
      { "effect": "minecraft:resistance", "amplifier": 0 }
    ]
  },
  "texture": {
    "mode": "reference",
    "refs": {
      "helmet":     "minecraft:item/diamond_helmet",
      "chestplate": "minecraft:item/diamond_chestplate",
      "leggings":   "minecraft:item/diamond_leggings",
      "boots":      "minecraft:item/diamond_boots"
    },
    "armor_layers": {
      "layer_1": "minecraft:models/armor/diamond_layer_1",
      "layer_2": "minecraft:models/armor/diamond_layer_2"
    }
  },
  "recipes": {
    "helmet":     { "type": "shaped", "pattern": ["GGG","G G","   "], "key": {"G": "mymod:my_gem"} },
    "chestplate": { "type": "shaped", "pattern": ["G G","GGG","GGG"], "key": {"G": "mymod:my_gem"} },
    "leggings":   { "type": "shaped", "pattern": ["GGG","G G","G G"], "key": {"G": "mymod:my_gem"} },
    "boots":      { "type": "shaped", "pattern": ["   ","G G","G G"], "key": {"G": "mymod:my_gem"} }
  }
}
```

### Tool Set — Full Example

> **Note:** `tool_set` only supports `pickaxe`, `axe`, `shovel`, and `hoe`. To add a sword alongside tools, use a separate `weapon_set` or individual `sword` file.

```json
{
  "id": "my_tools",
  "type": "tool_set",
  "tool_names": {
    "en_us": {
      "pickaxe": "My Pickaxe",
      "axe":     "My Axe",
      "shovel":  "My Shovel",
      "hoe":     "My Hoe"
    },
    "es_mx": {
      "pickaxe": "Mi Pico",
      "axe":     "Mi Hacha",
      "shovel":  "Mi Pala",
      "hoe":     "Mi Azada"
    }
  },
  "tools": {
    "pickaxe": {
      "durability": 8000,
      "attack_damage": 5.0,
      "attack_speed": 1.2,
      "mining_speed": 20.0,
      "harvest_level": 4,
      "held_effects": [ { "effect": "minecraft:haste", "amplifier": 1 } ]
    },
    "axe": {
      "durability": 7000,
      "attack_damage": 8.0,
      "attack_speed": 0.9,
      "mining_speed": 15.0,
      "harvest_level": 4,
      "held_effects": [ { "effect": "minecraft:strength", "amplifier": 1 } ]
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
      "held_effects": [ { "effect": "minecraft:regeneration", "amplifier": 0 } ]
    }
  },
  "enchantable": true,
  "enchantability": 22,
  "texture": {
    "mode": "reference",
    "refs": {
      "pickaxe": "minecraft:item/netherite_pickaxe",
      "axe":     "minecraft:item/netherite_axe",
      "shovel":  "minecraft:item/netherite_shovel",
      "hoe":     "minecraft:item/netherite_hoe"
    }
  },
  "recipes": {
    "pickaxe": { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_pickaxe", "addition": "minecraft:netherite_ingot" },
    "axe":     { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_axe",     "addition": "minecraft:netherite_ingot" },
    "shovel":  { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_shovel",  "addition": "minecraft:netherite_ingot" },
    "hoe":     { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_hoe",     "addition": "minecraft:netherite_ingot" }
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
      "held_effects": [ { "effect": "minecraft:strength", "amplifier": 1 } ]
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
    "shield": { "durability": 336 }
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
  },
  "recipes": {
    "sword": { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_sword", "addition": "minecraft:netherite_ingot" }
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
  "held_effects": [ { "effect": "minecraft:strength", "amplifier": 1 } ],
  "texture": {
    "mode": "reference",
    "refs": { "sword": "minecraft:item/diamond_sword" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": [" G ", " G ", " S "],
    "key": { "G": "mymod:my_gem", "S": "minecraft:stick" }
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
    "refs": { "bow": "minecraft:item/bow" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": [" GT", "G T", " GT"],
    "key": { "G": "mymod:my_gem", "T": "minecraft:string" }
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
    "refs": { "crossbow": "minecraft:item/crossbow" }
  }
}
```

---

## Field Reference

### Common Fields

| Field            | Type    | Description                                                                                                       |
|------------------|---------|-------------------------------------------------------------------------------------------------------------------|
| `id`             | String  | Unique identifier. Lowercase letters, numbers, and underscores only. 2–64 characters.                             |
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
| `tools.attack_damage_bonus` | Float | Additional damage added on top of `attack_damage`                                         |
| `tools.attack_speed`        | Float | Attack speed. Standard values: sword = 1.6, axe = 0.9, shovel = 1.0                       |
| `tools.mining_speed`        | Float | Mining speed. Netherite = 9.0, Diamond = 8.0, Iron = 6.0                                  |
| `tools.harvest_level`       | Int   | 0=Wood, 1=Stone, 2=Iron, 3=Diamond, 4=Netherite                                           |
| `tools.held_effects`        | List  | Effects applied when this tool is held in hand                                            |
| `tools.till_radius`         | Int   | (Hoe only) Radius of blocks to till around the target block. 0 = no area tilling          |
| `tool_names`                | Map   | Full name for each tool per language. Each language defines all tools independently.      |

### Weapon Fields

| Field                             | Type  | Description                                                                                      |
|-----------------------------------|-------|--------------------------------------------------------------------------------------------------|
| `weapons`                         | Map   | Defines each weapon. Keys: `sword`, `bow`, `crossbow`, `shield`                                  |
| `weapons.durability`              | Int   | Durability of this weapon                                                                        |
| `weapons.attack_damage`           | Float | Base attack damage (sword only)                                                                  |
| `weapons.attack_damage_bonus`     | Float | Additional damage (sword only)                                                                   |
| `weapons.attack_speed`            | Float | Attack speed (sword only)                                                                        |
| `weapons.damage_multiplier`       | Float | Final attack damage multiplier. Default: 1.0                                                     |
| `weapons.arrow_damage`            | Float | Base arrow damage (bow/crossbow). If 0, uses vanilla calculation                                 |
| `weapons.arrow_damage_bonus`      | Float | Flat bonus added to arrow damage (bow/crossbow)                                                  |
| `weapons.arrow_damage_multiplier` | Float | Arrow damage multiplier (bow/crossbow). Default: 1.0                                             |
| `weapons.charge_speed`            | Float | Charge speed multiplier (bow/crossbow). Values < 1.0 = slower. Default: 1.0                      |
| `weapons.held_effects`            | List  | Effects applied when this weapon is held in hand                                                 |
| `weapon_names`                    | Map   | Full name for each weapon per language.                                                          |

### Individual Weapon/Tool Fields

Individual items (`type: "sword"`, `type: "bow"`, etc.) use the same fields as above but at the root level instead of nested inside `weapons` or `tools`.

### Attack Damage Reference

| Vanilla weapon      | attack_damage |
|---------------------|---------------|
| Wood sword          | 4.0           |
| Stone sword         | 5.0           |
| Iron sword          | 6.0           |
| Diamond sword       | 7.0           |
| Netherite sword     | 8.0           |

### Effect Object

| Field       | Type   | Description                                                             |
|-------------|--------|-------------------------------------------------------------------------|
| `effect`    | String | Effect ID in `namespace:effect_name` format (e.g. `minecraft:strength`) |
| `amplifier` | Int    | Effect level minus 1. `0` = Level I, `1` = Level II, etc.               |

### Texture Fields

| Field          | Type   | Description                                                                                                                          |
|----------------|--------|--------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode` | String | `default`, `custom`, or `reference`                                                                                                  |
| `texture.refs` | Map    | For `custom`: relative path to a PNG inside `.minecraft/customgear/`. For `reference`: full resource location of another mod's model |
| `armor_layers` | Map    | (Armor sets only) `layer_1` and `layer_2` paths for the in-world armor texture                                                       |

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


### Recipe Fields

| Field          | Type         | Description                                                                          |
|----------------|--------------|--------------------------------------------------------------------------------------|
| `recipe`       | Object/Array | Recipe for individual items. Can be a single object or an array for multiple recipes |
| `recipes`      | Map          | Recipes for sets. One entry per piece/tool/weapon key                                |
| `type`         | String       | `shaped`, `shapeless`, `smelting`, `blasting`, or `smithing_transform`               |
| `pattern`      | String[]     | (shaped) 1–3 rows of up to 3 characters each                                         |
| `key`          | Map          | (shaped) Maps each pattern character to an item ID                                   |
| `ingredients`  | String[]     | (shapeless) List of item IDs                                                         |
| `ingredient`   | String       | (smelting/blasting) Single input item ID                                             |
| `experience`   | Float        | (smelting/blasting) XP granted on completion. Default: 0.1                           |
| `cooking_time` | Int          | (smelting/blasting) Ticks to cook. Default: 200 (smelting), 100 (blasting)           |
| `template`     | String       | (smithing_transform) Template item ID                                                |
| `base`         | String       | (smithing_transform) Base item ID to upgrade                                         |
| `addition`     | String       | (smithing_transform) Upgrade material item ID                                        |
| `result_count` | Int          | Number of items produced. Default: 1. Only applies to shaped/shapeless               |

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
- Adding or removing items (new or deleted JSON files)
- Changing item IDs

---

## Item ID Reference

| Type               | ID pattern                     | Example                       |
|--------------------|--------------------------------|-------------------------------|
| Armor set pieces   | `customgear:<set_id>_<piece>`  | `customgear:my_armor_helmet`  |
| Tool set tools     | `customgear:<set_id>_<tool>`   | `customgear:my_tools_pickaxe` |
| Weapon set weapons | `customgear:<set_id>_<weapon>` | `customgear:my_weapons_sword` |
| Individual items   | `customgear:<id>`              | `customgear:my_sword`         |
| Blocks             | `customgear:<id>`              | `customgear:my_ore`           |
| Fluid buckets      | `customgear:<id>_bucket`       | `customgear:my_fluid_bucket`  |

---

## Compatibility

- Minecraft 1.21.1
- NeoForge 21.1.x
- JEI (optional, recommended) — recipes are fully visible
- Modded enchantments work automatically on enchantable items
- Models from any installed mod can be referenced with `reference` mode
- Ingredients from any installed mod can be used in recipes

---

## License

MIT License — see LICENSE file for details.