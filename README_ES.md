# CustomGear

**CustomGear** es un mod de NeoForge para Minecraft 1.21.1 basado en datos, que permite a administradores de servidores, creadores de modpacks y jugadores agregar sets de armadura, armas y herramientas completamente personalizados — todo mediante simples archivos JSON. No se requiere programar.

---

## Características

- Agrega **sets de armadura** personalizados con defensa, durabilidad, toughness y resistencia al retroceso por pieza individual
- Agrega **sets de herramientas** (pico, hacha, pala, azadón) e herramientas individuales con daño, velocidad y velocidad de minado personalizados
- Agrega **sets de armas** (espada, arco, ballesta, escudo) y armas individuales
- Agrega **arcos** personalizados con daño de flecha y velocidad de carga configurables, con animación de tensado completa
- Agrega **ballestas** personalizadas con daño de flecha y velocidad de carga configurables, con animación de carga completa
- Agrega **escudos** personalizados con durabilidad configurable y renderizado 3D completo en mano e inventario
- Efectos por pieza de armadura al portarla individualmente (ej. el casco da Visión Nocturna)
- Efectos de bonus de set al tener el número requerido de piezas equipadas
- Efectos al sostener por herramienta/arma (ej. el pico da Prisa, la espada da Fuerza)
- **Sistema de recetas nativo** — define recetas de crafteo directamente en los archivos JSON, sin mods externos
- Soporte completo para nombres en múltiples idiomas — define el nombre completo por idioma sin restricciones de formato
- Texturas personalizadas con sistema de rutas flexible, o reutiliza modelos de otros mods
- Los archivos JSON pueden organizarse en cualquier estructura de subcarpetas dentro de `.minecraft/customgear/`
- Compatible con JEI — las recetas son completamente visibles
- Todos los ítems son encantables con encantamientos de vanilla y de otros mods
- Comando `/customgear reload` para recargar nombres, texturas y efectos sin reiniciar el juego

---

## Instalación

1. Descarga e instala [NeoForge 1.21.1](https://neoforged.net/)
2. Coloca `customgear-1.1.0.jar` en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `customgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON a `.minecraft/customgear/`
5. Reinicia el juego

> **Nota de sintaxis JSON:** El JSON estándar no permite comas al final del último elemento. Una coma sobrante al final de un objeto o lista hará que el archivo sea ignorado silenciosamente al cargar.

---

## Estructura de archivos JSON

Todos los archivos JSON van dentro de `.minecraft/customgear/`. Cada archivo define un ítem, set, bloque o fluido. Los archivos pueden organizarse en cualquier estructura de subcarpetas.

### Set de Armadura — Ejemplo completo

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

### Set de Herramientas — Ejemplo completo

> **Nota:** `tool_set` solo soporta `pickaxe`, `axe`, `shovel` y `hoe`. Para agregar una espada junto a las herramientas, usa un archivo separado de tipo `weapon_set` o `sword`.

```json
{
  "id": "mis_herramientas",
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
    "mode": "reference",
    "refs": {
      "pickaxe": "minecraft:item/netherite_pickaxe",
      "axe":     "minecraft:item/netherite_axe",
      "shovel":  "minecraft:item/netherite_shovel",
      "hoe":     "minecraft:item/netherite_hoe"
    }
  },
  "recipes": {
    "pickaxe": { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mimod:mi_pico_diamante",  "addition": "minecraft:netherite_ingot" },
    "axe":     { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mimod:mi_hacha_diamante", "addition": "minecraft:netherite_ingot" },
    "shovel":  { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mimod:mi_pala_diamante",  "addition": "minecraft:netherite_ingot" },
    "hoe":     { "type": "smithing_transform", "template": "minecraft:netherite_upgrade_smithing_template", "base": "mimod:mi_azadon_diamante","addition": "minecraft:netherite_ingot" }
  }
}
```

### Set de Armas — Ejemplo completo

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
    "mode": "reference",
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

### Espada individual — Ejemplo completo

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
    "mode": "reference",
    "refs": { "sword": "minecraft:item/diamond_sword" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": [" G ", " G ", " S "],
    "key": { "G": "mimod:mi_gema", "S": "minecraft:stick" }
  }
}
```

### Arco individual — Ejemplo completo

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
    "mode": "reference",
    "refs": { "bow": "minecraft:item/bow" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": [" GT", "G T", " GT"],
    "key": { "G": "mimod:mi_gema", "T": "minecraft:string" }
  }
}
```

### Ballesta individual — Ejemplo completo

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
    "mode": "reference",
    "refs": { "crossbow": "minecraft:item/crossbow" }
  }
}
```

---

## Referencia de campos

### Campos comunes

| Campo            | Tipo    | Descripción                                                                                                            |
|------------------|---------|------------------------------------------------------------------------------------------------------------------------|
| `id`             | String  | Identificador único. Solo letras minúsculas, números y guiones bajos. 2–64 caracteres.                                 |
| `type`           | String  | `armor_set`, `tool_set`, `weapon_set`, `sword`, `bow`, `crossbow`, `shield`, `pickaxe`, `axe`, `shovel`, o `hoe`       |
| `names`          | Map     | Nombre completo por idioma (solo para ítems individuales — los sets usan `piece_names`, `tool_names` o `weapon_names`) |
| `enchantable`    | Boolean | Si el ítem puede ser encantado                                                                                         |
| `enchantability` | Int     | Mayor = mejores encantamientos. Hierro = 9, Oro = 25, Diamante = 10                                                    |

### Campos de armadura

| Campo                         | Tipo   | Descripción                                                                                            |
|-------------------------------|--------|--------------------------------------------------------------------------------------------------------|
| `pieces`                      | Map    | Define cada pieza. Claves: `helmet`, `chestplate`, `leggings`, `boots`                                 |
| `pieces.durability`           | Int    | Durabilidad de esta pieza                                                                              |
| `pieces.defense`              | Int    | Puntos de armadura que provee esta pieza                                                               |
| `pieces.toughness`            | Float  | Resistencia de armadura por pieza. Netherite = 3.0                                                     |
| `pieces.knockback_resistance` | Float  | Resistencia al retroceso. Máximo 1.0 (resistencia total)                                               |
| `piece_names`                 | Map    | Nombre completo de cada pieza por idioma. Cada idioma define las cuatro piezas de forma independiente. |
| `piece_effects`               | Map    | Efectos aplicados al portar una pieza específica individualmente                                       |
| `set_bonus`                   | Object | Efectos aplicados al tener el número requerido de piezas equipadas                                     |
| `set_bonus.required_pieces`   | Int    | Número de piezas necesarias para activar el bonus                                                      |
| `set_bonus.effects`           | List   | Lista de efectos a aplicar cuando el set está completo                                                 |

### Campos de herramientas

| Campo                       | Tipo  | Descripción                                                                                                       |
|-----------------------------|-------|-------------------------------------------------------------------------------------------------------------------|
| `tools`                     | Map   | Define cada herramienta. Claves: `pickaxe`, `axe`, `shovel`, `hoe`                                                |
| `tools.durability`          | Int   | Durabilidad de esta herramienta                                                                                   |
| `tools.attack_damage`       | Float | Daño de ataque base                                                                                               |
| `tools.attack_damage_bonus` | Float | Daño adicional sumado a `attack_damage`                                                                           |
| `tools.attack_speed`        | Float | Velocidad de ataque. Valores referencia: espada = 1.6, hacha = 0.9, pala = 1.0                                    |
| `tools.mining_speed`        | Float | Velocidad de minado. Netherite = 9.0, Diamante = 8.0, Hierro = 6.0                                                |
| `tools.harvest_level`       | Int   | 0=Madera, 1=Piedra, 2=Hierro, 3=Diamante, 4=Netherite                                                             |
| `tools.held_effects`        | List  | Efectos aplicados al sostener esta herramienta en la mano                                                         |
| `tools.till_radius`         | Int   | (Solo azadón) Radio de bloques a arar alrededor del bloque objetivo. 0 = sin arado en área                        |
| `tool_names`                | Map   | Nombre completo de cada herramienta por idioma. Cada idioma define todas las herramientas de forma independiente. |

### Campos de armas

| Campo                             | Tipo  | Descripción                                                                                      |
|-----------------------------------|-------|--------------------------------------------------------------------------------------------------|
| `weapons`                         | Map   | Define cada arma. Claves: `sword`, `bow`, `crossbow`, `shield`                                   |
| `weapons.durability`              | Int   | Durabilidad de esta arma                                                                         |
| `weapons.attack_damage`           | Float | Daño de ataque base (solo espada)                                                                |
| `weapons.attack_damage_bonus`     | Float | Daño adicional (solo espada)                                                                     |
| `weapons.attack_speed`            | Float | Velocidad de ataque (solo espada)                                                                |
| `weapons.damage_multiplier`       | Float | Multiplicador del daño de ataque final. Por defecto: 1.0                                         |
| `weapons.arrow_damage`            | Float | Daño base de flecha (arco/ballesta). Si es 0, usa el cálculo vanilla                             |
| `weapons.arrow_damage_bonus`      | Float | Bonus plano sumado al daño de flecha (arco/ballesta)                                             |
| `weapons.arrow_damage_multiplier` | Float | Multiplicador del daño de flecha (arco/ballesta). Por defecto: 1.0                               |
| `weapons.charge_speed`            | Float | Multiplicador de velocidad de carga (arco/ballesta). Valores < 1.0 = más lento. Por defecto: 1.0 |
| `weapons.held_effects`            | List  | Efectos aplicados al sostener esta arma en la mano                                               |
| `weapon_names`                    | Map   | Nombre completo de cada arma por idioma.                                                         |

### Campos para ítems individuales

Los ítems individuales (`type: "sword"`, `type: "bow"`, etc.) usan los mismos campos que los anteriores pero al nivel raíz del JSON en lugar de anidados dentro de `weapons` o `tools`.

### Referencia de daño de ataque

| Arma vanilla        | attack_damage |
|---------------------|---------------|
| Espada de madera    | 4.0           |
| Espada de piedra    | 5.0           |
| Espada de hierro    | 6.0           |
| Espada de diamante  | 7.0           |
| Espada de netherita | 8.0           |

### Objeto de efecto

| Campo       | Tipo   | Descripción                                                                   |
|-------------|--------|-------------------------------------------------------------------------------|
| `effect`    | String | ID del efecto en formato `namespace:nombre_efecto` (ej. `minecraft:strength`) |
| `amplifier` | Int    | Nivel del efecto menos 1. `0` = Nivel I, `1` = Nivel II, etc.                 |

### Campos de textura

| Campo          | Tipo   | Descripción                                                                                                                                   |
|----------------|--------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode` | String | `default`, `custom`, o `reference`                                                                                                            |
| `texture.refs` | Map    | Para `custom`: ruta relativa a un PNG dentro de `.minecraft/customgear/`. Para `reference`: resource location completo del modelo de otro mod |
| `armor_layers` | Map    | (Solo sets de armadura) `layer_1` y `layer_2` para la textura de armadura en el mundo                                                         |

**Ejemplo de estructura de archivos:**
```
.minecraft/customgear/
├── models/
│   └── mi_armadura/
│       ├── layer_1.png
│       └── layer_2.png
├── item/
│   ├── armor/
│   │   ├── casco.png
│   │   ├── pechera.png
│   │   ├── pantalones.png
│   │   └── botas.png
│   ├── herramientas/
│   │   ├── pico.png
│   │   └── ...
│   └── armas/
│       ├── espada.png
│       └── ...
├── armadura1.json
├── herramientas1.json
└── armas/
    └── armas1.json
```


### Campos de receta

| Campo          | Tipo         | Descripción                                                                           |
|----------------|--------------|---------------------------------------------------------------------------------------|
| `recipe`       | Objeto/Array | Receta para ítems individuales. Puede ser un objeto o un array para múltiples recetas |
| `recipes`      | Map          | Recetas para sets. Una entrada por pieza/herramienta/arma                             |
| `type`         | String       | `shaped`, `shapeless`, `smelting`, `blasting`, o `smithing_transform`                 |
| `pattern`      | String[]     | (shaped) 1–3 filas de hasta 3 caracteres cada una                                     |
| `key`          | Map          | (shaped) Mapea cada carácter del patrón a un ID de ítem                               |
| `ingredients`  | String[]     | (shapeless) Lista de IDs de ítems                                                     |
| `ingredient`   | String       | (smelting/blasting) ID del ítem de entrada                                            |
| `experience`   | Float        | (smelting/blasting) XP otorgado al completar. Por defecto: 0.1                        |
| `cooking_time` | Int          | (smelting/blasting) Ticks de cocción. Por defecto: 200 (smelting), 100 (blasting)     |
| `template`     | String       | (smithing_transform) ID del ítem template                                             |
| `base`         | String       | (smithing_transform) ID del ítem base a mejorar                                       |
| `addition`     | String       | (smithing_transform) ID del material de mejora                                        |
| `result_count` | Int          | Cantidad de ítems producidos. Por defecto: 1. Solo aplica a shaped/shapeless          |

---

## Comandos

| Comando              | Permiso    | Descripción                                              |
|----------------------|------------|----------------------------------------------------------|
| `/customgear reload` | OP nivel 2 | Recarga todos los archivos JSON y texturas sin reiniciar |

### Qué actualiza el comando reload
- Nombres de ítems
- Texturas y modelos
- Efectos al sostener (armas y herramientas)
- Efectos por pieza y bonificaciones de set (armadura)
- Visualización de durabilidad

### Qué requiere reinicio completo del juego
- Daño de ataque y velocidad de ataque
- Defensa, toughness y resistencia al retroceso de armadura
- Velocidad de minado y nivel de cosecha
- Agregar o eliminar ítems (archivos JSON nuevos o eliminados)
- Cambiar IDs de ítems

---

## Referencia de IDs

| Tipo                      | Patrón de ID                        | Ejemplo                               |
|---------------------------|-------------------------------------|---------------------------------------|
| Piezas de set de armadura | `customgear:<id_set>_<pieza>`       | `customgear:mi_armadura_helmet`       |
| Herramientas de set       | `customgear:<id_set>_<herramienta>` | `customgear:mis_herramientas_pickaxe` |
| Armas de set              | `customgear:<id_set>_<arma>`        | `customgear:mis_armas_sword`          |
| Ítems individuales        | `customgear:<id>`                   | `customgear:mi_espada`                |
| Bloques                   | `customgear:<id>`                   | `customgear:mi_mineral`               |
| Cubetas de fluido         | `customgear:<id>_bucket`            | `customgear:mi_fluido_bucket`         |

---

## Compatibilidad

- Minecraft 1.21.1
- NeoForge 21.1.x
- JEI (opcional, recomendado) — las recetas son completamente visibles
- Los encantamientos de otros mods funcionan automáticamente en ítems encantables
- Los modelos de cualquier mod instalado pueden referenciarse con el modo `reference`
- Los ingredientes de cualquier mod instalado pueden usarse en recetas

---

## Licencia

Licencia MIT — ver el archivo LICENSE para más detalles.