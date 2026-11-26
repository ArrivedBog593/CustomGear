# Tools

## Tool Set — Full Example

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
      "harvest_level": 4
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
      "till_radius": 3
    }
  },
  "enchantable": true,
  "enchantability": 22,
  "texture": {
    "refs": {
      "pickaxe": "minecraft:item/netherite_pickaxe",
      "axe":     "minecraft:item/netherite_axe",
      "shovel":  "minecraft:item/netherite_shovel",
      "hoe":     "minecraft:item/netherite_hoe"
    }
  },
  "recipes": {
    "pickaxe": { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_pickaxe",  "addition": "minecraft:netherite_ingot" },
    "axe":     { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_axe",     "addition": "minecraft:netherite_ingot" },
    "shovel":  { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_shovel",  "addition": "minecraft:netherite_ingot" },
    "hoe":     { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mymod:my_diamond_hoe",     "addition": "minecraft:netherite_ingot" }
  }
}
```

## Tool Fields

| Field                       | Type  | Description                                                                          |
|-----------------------------|-------|--------------------------------------------------------------------------------------|
| `tools`                     | Map   | Defines each tool. Keys: `pickaxe`, `axe`, `shovel`, `hoe`                           |
| `tools.durability`          | Int   | Durability of this tool                                                              |
| `tools.attack_damage`       | Float | Base attack damage                                                                   |
| `tools.attack_damage_bonus` | Float | Additional damage added on top of `attack_damage`                                    |
| `tools.attack_speed`        | Float | Attack speed. Sword=1.6, Axe=0.9, Shovel=1.0                                         |
| `tools.mining_speed`        | Float | Mining speed. Netherite=9.0, Diamond=8.0, Iron=6.0                                   |
| `tools.harvest_level`       | Int   | 0=Wood, 1=Stone, 2=Iron, 3=Diamond, 4=Netherite                                      |
| `tools.held_effects`        | List  | Effects applied when this tool is held in hand                                       |
| `tools.till_radius`         | Int   | (Hoe only) Radius of blocks to till around the target. 0 = no area tilling           |
| `tool_names`                | Map   | Full name for each tool per language. Each language defines all tools independently. |
