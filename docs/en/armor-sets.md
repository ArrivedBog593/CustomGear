# Armor Sets

## Armor Set — Full Example

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

## Armor Set with 3D Model and Resistances — Full Example

```json
{
  "id": "dragonslayer_armor",
  "type": "armor_set",
  "piece_names": {
    "en_us": {
      "helmet":     "Dragonslayer Helm",
      "chestplate": "Dragonslayer Cuirass",
      "leggings":   "Dragonslayer Greaves",
      "boots":      "Dragonslayer Sabatons"
    }
  },
  "pieces": {
    "helmet":     { "durability": 407, "defense": 3, "toughness": 3.0, "knockback_resistance": 0.1 },
    "chestplate": { "durability": 592, "defense": 8, "toughness": 3.0, "knockback_resistance": 0.1 },
    "leggings":   { "durability": 555, "defense": 6, "toughness": 3.0, "knockback_resistance": 0.1 },
    "boots":      {
      "durability": 481, "defense": 3, "toughness": 3.0, "knockback_resistance": 0.1,
      "damage_resistances": { "#minecraft:is_fall": 0.25 }
    }
  },
  "damage_resistances": {
    "#minecraft:is_fire": 0.125,
    "#minecraft:is_projectile": 0.0625
  },
  "attacker_resistances": {
    "#minecraft:undead": 0.10,
    "minecraft:ender_dragon": 0.15
  },
  "conditional_resistances": [
    { "attacker": "minecraft:blaze", "damage": "#minecraft:is_fire", "amount": 0.20 }
  ],
  "show_player_resistances": false,
  "enchantable": true,
  "enchantability": 15,
  "set_bonus": {
    "required_pieces": 4,
    "effects": [ { "effect": "minecraft:fire_resistance", "amplifier": 0 } ]
  },
  "texture": {
    "mode": "custom",
    "refs": {
      "helmet":     "armor/textures/item/dragonslayer_helmet.png",
      "chestplate": "armor/textures/item/dragonslayer_chestplate.png",
      "leggings":   "armor/textures/item/dragonslayer_leggings.png",
      "boots":      "armor/textures/item/dragonslayer_boots.png"
    },
    "armor_layers": {
      "layer_1": "transparent",
      "layer_2": "transparent"
    },
    "armor_3d": {
      "model":     "armor/geo/dragonslayer.geo.json",
      "texture":   "armor/textures/armor/dragonslayer_3d.png",
      "animation": "armor/geo/dragonslayer.animation.json"
    }
  }
}
```

What this example shows:

- **`armor_3d`** turns the four pieces into a GeckoLib model. `refs` still points
  at flat PNGs — those are the inventory icons, which are always 2D.
- **`armor_layers: "transparent"`** is the deliberate fallback here: in an
  instance without GeckoLib the armor draws nothing instead of falling back to
  the vanilla iron layers, which would look wrong for this set.
- **Set-level resistances** are per piece: `0.125` fire on four pieces is 50%
  with the full set, and `0.0625` projectiles are 25%.
- **The boots add** their own `damage_resistances`. Piece entries merge with the
  set-level ones: the boots still get the fire and projectile reductions, plus
  their own fall entry. A piece only overrides the specific keys it declares.
- **The conditional rule** beats the more general fire entry: a blaze's fire
  attack is reduced by 20% per piece (80% with the full set) instead of 12.5%,
  because `conditional` outranks `damage`.
- Set bonus and resistance stack independently: the Fire Resistance effect
  applies first, and whatever damage survives it is then reduced by the armor.

> Everything except the base stats is hot-reloadable. Adding or removing the
> `armor_3d` block requires a restart.

## Armor Fields

| Field                            | Type    | Description                                                                                                         |
|----------------------------------|---------|---------------------------------------------------------------------------------------------------------------------|
| `pieces`                         | Map     | Defines each armor piece. Keys: `helmet`, `chestplate`, `leggings`, `boots`                                         |
| `pieces.durability`              | Int     | Durability of this piece                                                                                            |
| `pieces.defense`                 | Int     | Armor points this piece provides                                                                                    |
| `pieces.toughness`               | Float   | Armor toughness per piece. Netherite = 3.0                                                                          |
| `pieces.knockback_resistance`    | Float   | Knockback resistance. Max is 1.0. Values above 1.0 cause physics glitches                                           |
| `piece_names`                    | Map     | Full name for each piece per language. Each language defines all four pieces independently.                         |
| `piece_effects`                  | Map     | Effects applied when a specific piece is worn individually                                                          |
| `set_bonus`                      | Object  | Effects applied when the required number of pieces are worn                                                         |
| `set_bonus.required_pieces`      | Int     | Number of pieces needed to activate the bonus                                                                       |
| `set_bonus.effects`              | List    | List of effects to apply when set is complete                                                                       |
| `damage_resistances`             | Map     | Damage reduction by damage type. See [Damage Resistances](damage-resistances.md)                                    |
| `attacker_resistances`           | Map     | Damage reduction by attacker. See [Damage Resistances](damage-resistances.md)                                       |
| `conditional_resistances`        | List    | Damage reduction for an attacker + damage type combination                                                          |
| `show_player_resistances`        | Bool    | Show `player:` entries in the tooltip. Default `false` (they stay hidden)                                           |
| `pieces.damage_resistances`      | Map     | Per-piece entries — merge with the set-level map, winning only on declared keys                                     |
| `pieces.attacker_resistances`    | Map     | Per-piece entries — merge with the set-level map                                                                    |
| `pieces.conditional_resistances` | List    | Per-piece rules — merge by `attacker` + `damage` pair                                                               |
| `pieces.inherit_set_resistances` | Bool    | `false` makes the piece ignore all set-level resistances. Default `true`                                            |
| `trimmable`                      | Boolean | Whether this armor accepts smithing trims. Default: `false`. See [Automatic gear tags](tags.md#automatic-gear-tags) |
