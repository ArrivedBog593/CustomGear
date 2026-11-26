# Armas

## Set de Armas — Ejemplo completo

```json
{
  "id": "mis_armas",
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
    "sword": { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mimod:mi_espada_diamante", "addition": "minecraft:netherite_ingot" }
  }
}
```

## Espada individual — Ejemplo completo

```json
{
  "id": "mi_espada",
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
    "key": { "G": "mimod:mi_gema", "S": "minecraft:stick" }
  }
}
```

## Arco individual — Ejemplo completo

```json
{
  "id": "mi_arco",
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
    "key": { "G": "mimod:mi_gema", "T": "minecraft:string" }
  }
}
```

## Ballesta individual — Ejemplo completo

```json
{
  "id": "mi_ballesta",
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

## Campos de armas

| Campo                             | Tipo  | Descripción                                                          |
|-----------------------------------|-------|----------------------------------------------------------------------|
| `weapons`                         | Map   | Define cada arma. Claves: `sword`, `bow`, `crossbow`, `shield`       |
| `weapons.durability`              | Int   | Durabilidad de esta arma                                             |
| `weapons.attack_damage`           | Float | Daño de ataque base (solo espada)                                    |
| `weapons.attack_damage_bonus`     | Float | Daño adicional (solo espada)                                         |
| `weapons.attack_speed`            | Float | Velocidad de ataque (solo espada)                                    |
| `weapons.damage_multiplier`       | Float | Multiplicador del daño de ataque final. Por defecto: 1.0             |
| `weapons.arrow_damage`            | Float | Daño base de flecha (arco/ballesta). Si es 0, usa el cálculo vanilla |
| `weapons.arrow_damage_bonus`      | Float | Bonus plano sumado al daño de flecha (arco/ballesta)                 |
| `weapons.arrow_damage_multiplier` | Float | Multiplicador del daño de flecha (arco/ballesta). Por defecto: 1.0   |
| `weapons.charge_speed`            | Float | Reservado (arco/ballesta) — ver la nota de abajo. Por defecto: 1.0   |
| `weapons.held_effects`            | List  | Efectos aplicados al sostener esta arma en la mano                   |
| `weapon_names`                    | Map   | Nombre completo de cada arma por idioma.                             |

> ⚠️ **`charge_speed` no cambia el tiempo de carga real.** Los métodos de
> vanilla que lo deciden son estáticos y no se pueden sobrescribir, así que un
> arco siempre se tensa en 1.0s y una ballesta en 1.25s. El campo solo alarga
> cuánto tiempo se puede mantener el clic — que ya son minutos de todos modos.
> Se conserva para una versión futura que implemente el tiempo real.

## Campos para ítems individuales

Los ítems individuales (`type: "sword"`, `type: "bow"`, etc.) usan los mismos campos que los anteriores pero al nivel raíz del JSON en lugar de anidados dentro de `weapons` o `tools`.

## Referencia de daño de ataque

| Arma vanilla        | attack_damage |
|---------------------|---------------|
| Espada de madera    | 4.0           |
| Espada de piedra    | 5.0           |
| Espada de hierro    | 6.0           |
| Espada de diamante  | 7.0           |
| Espada de netherita | 8.0           |
