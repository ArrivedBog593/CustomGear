# Weapons

## Weapon Set — Full Example

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

## Individual Sword — Full Example

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
    "refs": { "sword": "minecraft:item/diamond_sword" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": [" G ", " G ", " S "],
    "key": { "G": "mymod:my_gem", "S": "minecraft:stick" }
  }
}
```

## Individual Bow — Full Example

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
    "refs": { "bow": "minecraft:item/bow" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": [" GT", "G T", " GT"],
    "key": { "G": "mymod:my_gem", "T": "minecraft:string" }
  }
}
```

## Individual Crossbow — Full Example

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
    "refs": { "crossbow": "minecraft:item/crossbow" }
  }
}
```

## Weapon Fields

| Field                             | Type  | Description                                                      |
|-----------------------------------|-------|------------------------------------------------------------------|
| `weapons`                         | Map   | Defines each weapon. Keys: `sword`, `bow`, `crossbow`, `shield`  |
| `weapons.durability`              | Int   | Durability of this weapon                                        |
| `weapons.attack_damage`           | Float | Base attack damage (sword only)                                  |
| `weapons.attack_damage_bonus`     | Float | Additional damage (sword only)                                   |
| `weapons.attack_speed`            | Float | Attack speed (sword only)                                        |
| `weapons.damage_multiplier`       | Float | Final attack damage multiplier. Default: 1.0                     |
| `weapons.arrow_damage`            | Float | Base arrow damage (bow/crossbow). If 0, uses vanilla calculation |
| `weapons.arrow_damage_bonus`      | Float | Flat bonus added to arrow damage (bow/crossbow)                  |
| `weapons.arrow_damage_multiplier` | Float | Arrow damage multiplier (bow/crossbow). Default: 1.0             |
| `weapons.charge_speed`            | Float | Reserved (bow/crossbow) — see the note below. Default: 1.0       |
| `weapons.held_effects`            | List  | Effects applied when this weapon is held in hand                 |
| `weapon_names`                    | Map   | Full name for each weapon per language.                          |

> ⚠️ **`charge_speed` does not change the real charge time.** The vanilla
> methods that decide it are static and cannot be overridden, so a bow always
> draws in 1.0s and a crossbow in 1.25s regardless. The field only stretches
> how long the click can be held — already minutes either way. It is kept for
> a future version that implements the real timing.

## Individual Weapon/Tool Fields

Individual items (`type: "sword"`, `type: "bow"`, etc.) use the same fields as above but at the root level instead of nested inside `weapons` or `tools`.

## Attack Damage Reference

| Vanilla weapon  | attack_damage |
|-----------------|---------------|
| Wood sword      | 4.0           |
| Stone sword     | 5.0           |
| Iron sword      | 6.0           |
| Diamond sword   | 7.0           |
| Netherite sword | 8.0           |
