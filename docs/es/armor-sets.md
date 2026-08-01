# Sets de armadura

## Set de Armadura — Ejemplo completo

```json
{
  "id": "mi_armadura",
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
    "helmet":     { "type": "shaped", "pattern": ["GGG","G G","   "], "key": {"G": "mimod:mi_gema"} },
    "chestplate": { "type": "shaped", "pattern": ["G G","GGG","GGG"], "key": {"G": "mimod:mi_gema"} },
    "leggings":   { "type": "shaped", "pattern": ["GGG","G G","G G"], "key": {"G": "mimod:mi_gema"} },
    "boots":      { "type": "shaped", "pattern": ["   ","G G","G G"], "key": {"G": "mimod:mi_gema"} }
  }
}
```

## Conjunto de Armadura con Modelo 3D y Resistencias — Ejemplo Completo

```json
{
  "id": "dragonslayer_armor",
  "type": "armor_set",
  "piece_names": {
    "es_mx": {
      "helmet":     "Yelmo Matadragones",
      "chestplate": "Coraza Matadragones",
      "leggings":   "Grebas Matadragones",
      "boots":      "Escarpes Matadragones"
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

Lo que muestra este ejemplo:

- **`armor_3d`** convierte las cuatro piezas en un modelo de GeckoLib. `refs`
  sigue apuntando a PNG planos — esos son los íconos del inventario, que
  siempre son 2D.
- **`armor_layers: "transparent"`** es el respaldo deliberado en este caso: en
  una instancia sin GeckoLib la armadura no dibuja nada, en lugar de caer a las
  capas de hierro vanilla, que se verían fuera de lugar en este conjunto.
- **Las resistencias del conjunto son por pieza**: `0.125` de fuego en cuatro
  piezas es 50% con el set completo, y `0.0625` de proyectiles es 25%.
- **Las botas agregan** su propio `damage_resistances`. Las entradas de pieza se
  fusionan con las del conjunto: las botas siguen recibiendo las reducciones de
  fuego y proyectiles, más su propia entrada de caída. Una pieza solo sobrescribe
  las claves específicas que declara.
- **La regla condicional** le gana a la entrada general de fuego: el ataque
  ígneo de un blaze se reduce 20% por pieza (80% con el set completo) en vez de
  12.5%, porque `conditional` tiene prioridad sobre `damage`.
- El bono de conjunto y las resistencias se aplican de forma independiente: el
  efecto de Resistencia al Fuego actúa primero, y el daño que sobreviva a eso lo
  reduce después la armadura.

> Todo excepto las stats base es recargable en caliente. Agregar o quitar el
> bloque `armor_3d` requiere reiniciar.

## Campos de armadura

| Campo                            | Tipo    | Descripción                                                                                                                                           |
|----------------------------------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `pieces`                         | Map     | Define cada pieza. Claves: `helmet`, `chestplate`, `leggings`, `boots`                                                                                |
| `pieces.durability`              | Int     | Durabilidad de esta pieza                                                                                                                             |
| `pieces.defense`                 | Int     | Puntos de armadura que provee esta pieza                                                                                                              |
| `pieces.toughness`               | Float   | Resistencia de armadura por pieza. Netherite = 3.0                                                                                                    |
| `pieces.knockback_resistance`    | Float   | Resistencia al retroceso. Máximo 1.0. Valores mayores causan glitches de física                                                                       |
| `piece_names`                    | Map     | Nombre completo de cada pieza por idioma. Cada idioma define las cuatro piezas de forma independiente.                                                |
| `piece_effects`                  | Map     | Efectos aplicados al portar una pieza específica individualmente                                                                                      |
| `set_bonus`                      | Object  | Efectos aplicados al tener el número requerido de piezas equipadas                                                                                    |
| `set_bonus.required_pieces`      | Int     | Número de piezas necesarias para activar el bonus                                                                                                     |
| `set_bonus.effects`              | List    | Lista de efectos a aplicar cuando el set está completo                                                                                                |
| `damage_resistances`             | Map     | Reducción de daño por tipo de daño. Ver [Resistencias de Daño](damage-resistances.md)                                                                 |
| `attacker_resistances`           | Map     | Reducción de daño por atacante. Ver [Resistencias de Daño](damage-resistances.md)                                                                     |
| `conditional_resistances`        | List    | Reducción para una combinación de atacante + tipo de daño                                                                                             |
| `show_player_resistances`        | Bool    | Mostrar las entradas `player:` en el tooltip. Por defecto `false` (quedan ocultas)                                                                    |
| `pieces.damage_resistances`      | Map     | Entradas por pieza — se fusionan con el mapa del conjunto y ganan solo en las claves declaradas                                                       |
| `pieces.attacker_resistances`    | Map     | Entradas por pieza — se fusionan con el mapa del conjunto                                                                                             |
| `pieces.conditional_resistances` | List    | Reglas por pieza — se fusionan por el par `attacker` + `damage`                                                                                       |
| `pieces.inherit_set_resistances` | Bool    | `false` hace que la pieza ignore todas las resistencias del conjunto. Por defecto `true`                                                              |
| `trimmable`                      | Boolean | Si esta armadura acepta adornos de herrería. Por defecto: `false`. Ver [Tags automáticos del equipamiento](tags.md#tags-automáticos-del-equipamiento) |
