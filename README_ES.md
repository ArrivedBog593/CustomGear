# UltimateCustomGear

[![CurseForge](https://cf.way2muchnoise.eu/versions/ultimate-custom-gear.svg)](https://www.curseforge.com/minecraft/mc-mods/ultimate-custom-gear)
[![CurseForge](https://cf.way2muchnoise.eu/ultimate-custom-gear.svg)](https://www.curseforge.com/minecraft/mc-mods/ultimate-custom-gear)

**UltimateCustomGear** es un mod de NeoForge para Minecraft 1.21.1 basado en datos, que permite a administradores de servidores, creadores de modpacks y jugadores agregar sets de armadura, armas, herramientas, comida, ítems, bloques y fluidos completamente personalizados — todo mediante simples archivos JSON. No se requiere programar.

---

## Características

### Creación de Contenido
- **Sets de armadura** con stats por pieza (durabilidad, defensa, dureza, resistencia al retroceso)
- **Armas** — espadas, arcos, ballestas y escudos con daño, durabilidad y velocidad de carga personalizados
- **Herramientas** — picos, hachas, palas y azadones con velocidad de minado, niveles de cosecha y arado en área
- **Comida** con nutrición, saturación, velocidad al comer y efectos al consumir
- **Bloques** — incluyendo direccionales y con gravedad, con texturas por cara, luz, sonidos y requisitos de minado
- **Fluidos** con colores personalizados, efectos de contacto y comportamiento de fuego

### Efectos y Gameplay
- Efectos al sostener, efectos por pieza y bonos de conjunto completo
- **Drops de mobs** — cualquier ítem puede caer de los mobs con probabilidad, cantidad y filtros de entidad configurables (recargable en caliente: balancea tu economía en vivo)
- Ítems `fire_resistant` que sobreviven al fuego y la lava, como la netherita — funciona en ítems, comida, gear, bloques y cubetas
- **Armadura transparente** — armadura con stats y efectos completos que no se dibuja sobre el cuerpo

### Texturas y Modelos
- Referencia texturas de vanilla o de otros mods, o usa tus propios archivos PNG
- Texturas por cara en bloques, animaciones de tensado de arcos, capas de armadura personalizadas

### Recetas y Tags
- **Sistema nativo de recetas** — shaped, shapeless, smelting, blasting y smithing, definidas en JSON
- Los ingredientes de recetas aceptan **tags** (`"#minecraft:planks"` = cualquier tabla de madera, incluyendo ítems de otros mods)
- El contenido puede **pertenecer a tags** mediante el campo `tags`, para que tus ítems funcionen en recetas de otros mods

### Servidor y Multijugador
- **Packs de contenido** — distribuye todo como un solo `.zip` que los jugadores colocan en `packs/`
- **Verificación de contenido en multijugador** — el servidor comprueba al conectar que los clientes tengan los mismos archivos, con cumplimiento configurable
- `/customgear reload` — actualiza nombres, efectos, recetas y drops sin reiniciar

### Calidad de Vida
- Soporte completo multi-idioma por ítem
- Tooltips informativos (efectos, bonos de conjunto, niveles de cosecha, drops de mobs)
- Errores de validación claros que nombran el archivo y el problema exacto

---

## Instalación

1. Descarga e instala [NeoForge 1.21.1](https://neoforged.net/)
2. Coloca `ultimatecustomgear-1.x.x.jar` en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `ultimatecustomgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON en `.minecraft/ultimatecustomgear/` — o coloca un `.zip` de contenido en `.minecraft/ultimatecustomgear/packs/` (ver **Packs de Contenido** más abajo)
5. Reinicia el juego

> **Nota de sintaxis JSON:** El JSON estándar no permite comas al final del último elemento. Una coma sobrante al final de un objeto o lista hará que el archivo sea ignorado silenciosamente al cargar.

---

## Estructura de archivos JSON

Todos los archivos JSON van dentro de `.minecraft/ultimatecustomgear/`. Cada archivo define un ítem, set, bloque o fluido. Los archivos pueden organizarse en cualquier estructura de subcarpetas.

El contenido también puede empaquetarse como archivos `.zip` dentro de `packs/` — ver la sección **Packs de Contenido**.

### Tipos soportados

| Tipo         | Descripción                                                      |
|--------------|------------------------------------------------------------------|
| `armor_set`  | Set de armadura completo (casco, pechera, pantalones, botas)     |
| `tool_set`   | Set de herramientas completo (pico, hacha, pala, azadón)         |
| `weapon_set` | Set de armas completo (espada, arco, ballesta, escudo)           |
| `sword`      | Espada individual                                                |
| `bow`        | Arco individual                                                  |
| `crossbow`   | Ballesta individual                                              |
| `shield`     | Escudo individual                                                |
| `pickaxe`    | Pico individual                                                  |
| `axe`        | Hacha individual                                                 |
| `shovel`     | Pala individual                                                  |
| `hoe`        | Azadón individual                                                |
| `food`       | Ítem comestible                                                  |
| `item`       | Ítem simple no consumible                                        |
| `block`      | Bloque con texturas por cara opcionales y colocación direccional |
| `fluid`      | Fluido con cubeta                                                |

---

## Ejemplos de JSON

### Ítem Comestible — Ejemplo completo

```json
{
  "id": "magic_apple",
  "type": "food",
  "names": {
    "en_us": "Magic Apple",
    "es_mx": "Manzana Mágica"
  },
  "nutrition": 4,
  "saturation": 1.2,
  "always_edible": true,
  "on_eat_effects": [
    { "effect": "minecraft:regeneration",    "amplifier": 1, "duration": 10,  "probability": 1.0 },
    { "effect": "minecraft:absorption",      "amplifier": 0, "duration": 120, "probability": 1.0 },
    { "effect": "minecraft:fire_resistance", "amplifier": 0, "duration": 30,  "probability": 1.0 },
    { "effect": "minecraft:resistance",      "amplifier": 0, "duration": 300, "probability": 1.0 }
  ],
  "texture": {
    "mode": "reference",
    "refs": { "item": "minecraft:item/golden_apple" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": ["GGG","GAG","GGG"],
    "key": { "G": "minecraft:gold_block", "A": "minecraft:apple" }
  }
}
```

También se soporta comida instantánea (`eat_duration: 0`) y comida lenta (`eat_duration: 10` = 10 segundos).

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

### Fluido con Efectos al Contacto — Ejemplo completo

```json
{
  "id": "lago_venenoso",
  "type": "fluid",
  "names": {
    "en_us": "Poison Lake",
    "es_mx": "Lago Venenoso"
  },
  "bucket_names": {
    "en_us": "{fluid_name} Bucket",
    "es_mx": "Cubeta de {fluid_name}"
  },
  "light_level": 3,
  "color": "0xFF4CAF50",
  "tick_rate": 10,
  "spread_distance": 6,
  "burns_entities": false,
  "contact_effect_interval": 2.0,
  "contact_effects": [
    { "effect": "minecraft:poison",   "amplifier": 0, "duration": 3 },
    { "effect": "minecraft:slowness", "amplifier": 1, "duration": 3 }
  ],
  "texture": {
    "mode": "default"
  }
}
```

### Fluido tipo Lava — Ejemplo completo

```json
{
  "id": "fluido_de_magma",
  "type": "fluid",
  "names": {
    "en_us": "Magma Flow",
    "es_mx": "Flujo de Magma"
  },
  "bucket_names": {
    "en_us": "{fluid_name} Bucket",
    "es_mx": "Cubeta de {fluid_name}"
  },
  "light_level": 15,
  "color": "0xFFFF6600",
  "tick_rate": 30,
  "spread_distance": 4,
  "burns_entities": true,
  "burn_duration": 8,
  "contact_effect_interval": 1.0,
  "contact_effects": [
    { "effect": "minecraft:weakness",       "amplifier": 1, "duration": 5 },
    { "effect": "minecraft:mining_fatigue", "amplifier": 0, "duration": 5 }
  ],
  "texture": {
    "mode": "default"
  }
}
```

### Bloque — Ejemplo Simple

```json
{
  "id": "mi_mineral",
  "type": "block",
  "names": {
    "en_us": "My Ore",
    "es_mx": "Mi Mineral"
  },
  "light_level": 3,
  "destroy_time": 3.0,
  "explosion_resistance": 3.0,
  "map_color": "deepslate",
  "required_tool": "pickaxe",
  "harvest_level": 2,
  "sound": "stone",
  "texture": {
    "mode": "reference",
    "refs": {
      "block": "minecraft:block/diamond_ore"
    }
  }
}
```

### Bloque con Texturas por Cara — Ejemplo

```json
{
  "id": "multi_mineral",
  "type": "block",
  "names": {
    "en_us": "Multi Ore",
    "es_mx": "Mineral Multi"
  },
  "texture": {
    "mode": "reference",
    "faces": {
      "top":    "minecraft:block/coal_ore",
      "bottom": "minecraft:block/iron_ore",
      "north":  "minecraft:block/gold_ore",
      "south":  "minecraft:block/redstone_ore",
      "east":   "minecraft:block/emerald_ore",
      "west":   "minecraft:block/diamond_ore"
    }
  }
}
```

Usa `"side"` como atajo para aplicar la misma textura a las 4 caras horizontales.

### Bloque Direccional — Ejemplo

```json
{
  "id": "bloque_direccional",
  "type": "block",
  "directional": true,
  "names": {
    "en_us": "My Machine",
    "es_mx": "Mi Máquina"
  },
  "texture": {
    "mode": "reference",
    "faces": {
      "top":    "minecraft:block/stone",
      "bottom": "minecraft:block/stone",
      "side":   "minecraft:block/stone",
      "north":  "minecraft:block/furnace_front_on"
    }
  }
}
```

La cara `"north"` es la cara frontal — apunta hacia el jugador al colocar el bloque.

### Falling Block — Example

```json
{
  "id": "mi_bloque_con_caida",
  "type": "block",
  "gravity": true,
  "names": {
    "en_us": "My Falling Block",
    "es_mx": "Mi Bloque con Caída"
  },
  "sound": "sand",
  "destroy_time": 0.5,
  "explosion_resistance": 0.5,
  "texture": {
    "mode": "reference",
    "refs": { "block": "minecraft:block/sand" }
  }
}
```

---

## Recetas

CustomGear soporta recetas de crafteo nativas definidas directamente en JSON. No se requieren mods externos.

### Tipos de receta

| Tipo                 | Descripción                                      |
|----------------------|--------------------------------------------------|
| `shaped`             | Mesa de crafteo con patrón específico            |
| `shapeless`          | Mesa de crafteo, ingredientes en cualquier orden |
| `smelting`           | Horno                                            |
| `blasting`           | Alto horno                                       |
| `smithing_transform` | Mesa de herrería (template + base + adición)     |

### Tags como ingredientes

Cualquier casilla de ingrediente acepta un **tag** con el prefijo `#` — la
receta aceptará cualquier ítem de ese tag:

```json
"key": {
  "P": "#minecraft:planks",
  "I": "#c:ingots/iron",
  "S": "minecraft:stick"
}
```

Funciona en recetas shaped, shapeless, smelting, blasting y smithing. Las recetas con errores estructurales (filas desiguales, símbolos sin definir, keys sin usar, ID malformados) se descartan con un mensaje detallado en el log que nombra el ítem y el problema exacto.

### Receta de ítem individual

```json
"recipe": {
  "type": "shaped",
  "pattern": [" G ", " G ", " S "],
  "key": { "G": "mimod:mi_gema", "S": "minecraft:stick" }
}
```

Para múltiples recetas, usa un array:

```json
"recipe": [
  { "type": "shaped", ... },
  { "type": "smelting", "ingredient": "mimod:mi_mineral", "experience": 1.0, "cooking_time": 200 }
]
```

### Receta de set (armadura, herramientas, armas)

```json
"recipes": {
  "helmet":     { "type": "shaped", "pattern": ["GGG","G G","   "], "key": {"G": "mimod:mi_gema"} },
  "chestplate": { "type": "shaped", "pattern": ["G G","GGG","GGG"], "key": {"G": "mimod:mi_gema"} },
  "leggings":   { "type": "shaped", "pattern": ["GGG","G G","G G"], "key": {"G": "mimod:mi_gema"} },
  "boots":      { "type": "shaped", "pattern": ["   ","G G","G G"], "key": {"G": "mimod:mi_gema"} }
}
```

### Receta de mesa de herrería

```json
"recipe": {
  "type": "smithing_transform",
  "template": "minecraft:netherite_upgrade_smithing_template",
  "base": "mimod:mi_espada_diamante",
  "addition": "minecraft:netherite_ingot"
}
```

> Los ingredientes soportan cualquier ítem de cualquier mod instalado mediante resource location (ej. `"otromod:lingote_especial"`).

---

## Tags

Dos caras del mismo sistema:
- **Consumir** un tag en una receta usa el prefijo `#`: `"#minecraft:planks"` acepta cualquier ítem de ese tag (ver Recetas → Tags como ingredientes).
- **Pertenecer** a un tag usa el campo `tags` en tu contenido, SIN `#`, para que las recetas de otros mods — y las tuyas — acepten tu ítem/bloque/fluido.

```json
{
  "id": "ruby_ingot",
  "type": "item",
  "tags": ["c:ingots", "c:ingots/ruby"]
}
```

Un tag es simplemente la suma de todo lo que lo declara — puedes usar tags de vanilla, tags de convención (`c:`) compartidos entre mods, o inventar los tuyos (`customgear:magic_gems`). Tus tags se fusionan con los existentes del mismo nombre.

Los **bloques** se agregan a los registries de tags de bloque y de ítem (para qué las recetas, que consumen la forma de ítem, acepten tu bloque). Los **fluidos** etiquetan el fluido y su cubeta. **Los sets de armadura/ herramientas/armas aún no están soportados** (sus IDs se derivan por pieza).

### Tags comunes

Tags de vanilla (`minecraft:`) — hacen que tu contenido cuente como un material de vanilla:

| Tag                                                                                                   | Uso                                        |
|-------------------------------------------------------------------------------------------------------|--------------------------------------------|
| `minecraft:planks`                                                                                    | Cuenta como tablas en recetas de vanilla   |
| `minecraft:logs`                                                                                      | Troncos                                    |
| `minecraft:wool`                                                                                      | Lana                                       |
| `minecraft:leaves`                                                                                    | Hojas (se minan rápido con espada/tijeras) |
| `minecraft:swords` / `minecraft:pickaxes` / `minecraft:axes` / `minecraft:shovels` / `minecraft:hoes` | Herramienta de ese tipo                    |
| `minecraft:coals`                                                                                     | Combustibles tipo carbón                   |

Tags de convención (`c:`) — el estándar de interoperabilidad entre mods (los más útiles):

| Tag                                                | Uso                                                |
|----------------------------------------------------|----------------------------------------------------|
| `c:ingots` + `c:ingots/<material>`                 | Lingotes                                           |
| `c:gems` + `c:gems/<material>`                     | Gemas                                              |
| `c:ores` + `c:ores/<material>`                     | Menas                                              |
| `c:raw_materials` + `c:raw_materials/<material>`   | Materiales en bruto                                |
| `c:nuggets` + `c:nuggets/<material>`               | Pepitas                                            |
| `c:dusts` + `c:dusts/<material>`                   | Polvos                                             |
| `c:storage_blocks` + `c:storage_blocks/<material>` | Bloques de almacenamiento (bloque de X)            |
| `c:tools` + `c:tools/<tipo>`                       | Herramientas por tipo                              |
| `c:armors` + `c:armors/<pieza>`                    | Armaduras por pieza                                |
| `c:foods` + `c:foods/<tipo>`                       | Comida (`c:foods/fruits`, `c:foods/vegetables`...) |
| `c:dyes` + `c:dyes/<color>`                        | Tintes                                             |
| `c:seeds` / `c:crops`                              | Semillas y cultivos                                |

Consejo: usa el tag general Y el subtag de material (`c:ingots` y `c:ingots/ruby`) para máxima compatibilidad — el primero para "cualquier lingote", el segundo para "lingote de rubí específicamente".

---

## Texturas

Hay tres modos de textura disponibles:

| Modo        | Descripción                                                                |
|-------------|----------------------------------------------------------------------------|
| `default`   | Usa texturas de hierro/madera de vanilla como placeholders                 |
| `reference` | Reutiliza el modelo de otro ítem (vanilla o de otro mod)                   |
| `custom`    | Usa tus propios archivos PNG colocados en la carpeta `ultimatecustomgear/` |

### Ejemplo modo reference

```json
"texture": {
  "mode": "reference",
  "refs": { "sword": "minecraft:item/netherite_sword" }
}
```

Para arcos y ballestas, opcionalmente puedes incluir modelos de frames de tensado/carga personalizados:

```json
"texture": {
  "mode": "reference",
  "refs": {
    "bow":           "otromod:item/arco_epico",
    "bow_pulling_0": "otromod:item/arco_epico_pulling_0",
    "bow_pulling_1": "otromod:item/arco_epico_pulling_1",
    "bow_pulling_2": "otromod:item/arco_epico_pulling_2"
  }
}
```

---

## Packs de Contenido

Puedes distribuir tu contenido como un solo archivo **.zip** en vez de archivos sueltos. Coloca el zip en:

```
.minecraft/ultimatecustomgear/packs/tu-contenido.zip
```

Los JSON y texturas dentro del zip cargan exactamente igual que los archivos sueltos — las subcarpetas dentro del zip funcionan sin problema. Es la forma recomendada para que los dueños de servidor compartan contenido con sus jugadores: un solo archivo, imposible de descomprimir a medias o editar por accidente.

**Reglas:**
- Los zips solo se cargan desde la subcarpeta `packs/`. Un zip en cualquier otro lugar se ignora (con un mensaje en el log indicando a dónde moverlo).
- **Solo se admite `.zip`.** Si tienes un `.rar` o `.7z`, recomprímelo como zip: en Windows, selecciona los archivos → clic derecho → Enviar a → Carpeta comprimida.
- **Los archivos sueltos ganan sobre los zips.** Si un JSON suelto define el mismo `id` que uno dentro de un zip, se usa el archivo suelto por completo (stats, texturas, recetas) y la entrada del zip se descarta con un mensaje en el log. Puedes usar esto para sobreescribir localmente un ítem específico de un pack sin tocar el zip.
- Se permiten varios zips; cargan en orden alfabético.
- `/customgear reload` detecta zips agregados o actualizados sin reiniciar.

> Un pack distribuible debe ser **autocontenido**: cada textura que un JSON referencie debe estar dentro del mismo zip. Referenciar una textura suelta desde un JSON dentro de un zip funciona localmente, pero los jugadores que solo reciban el zip no tendrán el archivo suelto. (Los archivos sueltos siguen teniendo prioridad sobre el contenido del zip con la misma ruta — útil para ajustes locales.)

---

## Referencia de campos

### Campos comunes

| Campo            | Tipo    | Descripción                                                                                                                                                                                                                          |
|------------------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`             | String  | Identificador único. Solo letras minúsculas, números y guiones bajos. 2–64 caracteres.                                                                                                                                               |
| `type`           | String  | Tipo de ítem (ver tabla de Tipos soportados)                                                                                                                                                                                         |
| `names`          | Map     | Nombre completo por idioma (solo para ítems individuales — los sets usan `piece_names`, `tool_names` o `weapon_names`)                                                                                                               |
| `enchantable`    | Boolean | Si el ítem puede ser encantado                                                                                                                                                                                                       |
| `enchantability` | Int     | Mayor = mejores encantamientos. Hierro = 9, Oro = 25, Diamante = 10                                                                                                                                                                  |
| `tags`           | List    | Tags a los que pertenece este contenido, SIN `#` (p. ej. `["c:ingots", "c:ingots/ruby"]`). Permite que las recetas que aceptan `#ese_tag` lo usen. Ver **Tags** más abajo. Funciona en ítems, comida, bloques y fluidos (no en sets) |
| `fire_resistant` | Boolean | El ítem tirado sobrevive al fuego y la lava, como la netherita (en fluidos: la cubeta llena). No protege al portador del fuego. Requiere reiniciar                                                                                   |

### Campos de comida

| Campo                          | Tipo    | Por defecto | Descripción                                                                               |
|--------------------------------|---------|-------------|-------------------------------------------------------------------------------------------|
| `nutrition`                    | Int     | 0           | Puntos de hambre restaurados. Pan=5, Carne cocida=8, Manzana de oro=4                     |
| `saturation`                   | Float   | 0.6         | Modificador de saturación. Pan=0.6, Carne=0.8, Manzana de oro=1.2                         |
| `always_edible`                | Boolean | false       | Se puede comer aunque la barra de hambre esté llena                                       |
| `fast_food`                    | Boolean | false       | Se consume más rápido como el alga seca (16 ticks)                                        |
| `eat_duration`                 | Float   | -1          | Tiempo de consumo en segundos. 0=instantáneo, 1.6=velocidad vanilla normal, 10=muy lento. |
| `on_eat_effects`               | List    | —           | Efectos aplicados al consumir                                                             |
| `on_eat_effects[].effect`      | String  | —           | ID del efecto, ej. `"minecraft:regeneration"`                                             |
| `on_eat_effects[].amplifier`   | Int     | 0           | Nivel del efecto menos 1. 0=Nivel I, 1=Nivel II                                           |
| `on_eat_effects[].duration`    | Int     | 5           | Duración en segundos                                                                      |
| `on_eat_effects[].probability` | Float   | 1.0         | Probabilidad de aplicarse (0.0–1.0)                                                       |

### Campos de armadura

| Campo                         | Tipo   | Descripción                                                                                            |
|-------------------------------|--------|--------------------------------------------------------------------------------------------------------|
| `pieces`                      | Map    | Define cada pieza. Claves: `helmet`, `chestplate`, `leggings`, `boots`                                 |
| `pieces.durability`           | Int    | Durabilidad de esta pieza                                                                              |
| `pieces.defense`              | Int    | Puntos de armadura que provee esta pieza                                                               |
| `pieces.toughness`            | Float  | Resistencia de armadura por pieza. Netherite = 3.0                                                     |
| `pieces.knockback_resistance` | Float  | Resistencia al retroceso. Máximo 1.0. Valores mayores causan glitches de física                        |
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
| `tools.attack_speed`        | Float | Velocidad de ataque. Espada=1.6, Hacha=0.9, Pala=1.0                                                              |
| `tools.mining_speed`        | Float | Velocidad de minado. Netherite=9.0, Diamante=8.0, Hierro=6.0                                                      |
| `tools.harvest_level`       | Int   | 0=Madera, 1=Piedra, 2=Hierro, 3=Diamante, 4=Netherite                                                             |
| `tools.held_effects`        | List  | Efectos aplicados al sostener esta herramienta en la mano                                                         |
| `tools.till_radius`         | Int   | (Solo azadón) Radio de bloques a arar alrededor del objetivo. 0 = sin arado en área                               |
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

| Campo                | Tipo   | Descripción                                                                                                                                                                                                                       |
|----------------------|--------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode`       | String | `default`, `custom`, o `reference`                                                                                                                                                                                                |
| `texture.refs`       | Map    | Para `custom`: ruta relativa a un PNG dentro de `ultimatecustomgear/`. Para `reference`: resource location completo del modelo de otro mod                                                                                        |
| `armor_layers`       | Map    | (Solo sets de armadura) Rutas `layer_1` y `layer_2` para la textura de la armadura puesta. El valor especial `"transparent"` hace la armadura invisible al vestirla (stats y efectos intactos); en modo reference pon ambas capas |
| `texture.refs.block` | String | (Bloques simples) Resource location aplicada a las 6 caras mediante `cube_all`                                                                                                                                                    |
| `texture.faces`      | Objeto | (Solo bloques) Textura por cara. Claves: `top`, `bottom`, `north`, `south`, `east`, `west`, `side`                                                                                                                                |
| `texture.faces.side` | String | Atajo: aplica a `north`, `south`, `east`, `west` si no están definidas individualmente                                                                                                                                            |

> **`refs` vs `faces`:** `refs` lo maneja todo — una textura única con la clave `all` (`"refs": { "all": "..." }`), o texturas por cara con las claves de cara (`top`, `bottom`, `north`, `south`, `east`, `west`, `side`). `faces` es un alias legacy que solo sirve para texturas por cara. Usa `refs`. (La clave `block` es un alias legacy de `all`.) Nota: `all`/`block` solo funcionan dentro de `refs`, nunca dentro de `faces`.

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

### Campos de fluidos

| Campo                         | Tipo    | Por defecto    | Descripción                                                                                                                          |
|-------------------------------|---------|----------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `light_level`                 | Int     | 0              | Luz emitida por el bloque de fluido (0–15)                                                                                           |
| `color`                       | String  | `"0xFFFFFFFF"` | Color de tinte en ARGB hex, ej. `"0xFF3F76E4"`                                                                                       |
| `tick_rate`                   | Int     | 5              | Ticks entre cada paso de expansión. Menor = más rápido. Agua=5, Lava=30                                                              |
| `spread_distance`             | Int     | 8              | Expansión horizontal máxima en bloques. Agua=8, Lava=4                                                                               |
| `burns_entities`              | Boolean | false          | Prende fuego a las entidades como la lava — afecta a jugadores, mobs e ítems tirados (los mobs inmunes al fuego no se ven afectados) |
| `burn_duration`               | Int     | 5              | Segundos que dura el fuego. Solo si `burns_entities` es true                                                                         |
| `contact_effect_interval`     | Float   | 1.0            | Segundos entre cada aplicación de efectos mientras se está en el fluido                                                              |
| `contact_effects`             | List    | —              | Efectos aplicados a entidades vivas (jugadores y mobs) mientras están sumergidas                                                     |
| `contact_effects[].effect`    | String  | —              | ID del efecto, ej. `"minecraft:poison"`                                                                                              |
| `contact_effects[].amplifier` | Int     | 0              | Nivel del efecto menos 1                                                                                                             |
| `contact_effects[].duration`  | Int     | 3              | Duración en segundos por aplicación                                                                                                  |

> `contact_effects[].duration` debe ser al menos ~2 segundos mayor que `contact_effect_interval`, o los efectos de daño en el tiempo (veneno, wither) no alcanzan a hacer daño. Los valores por defecto (duration 3, interval 1.0) son seguros.

> ⚠️ Los ítems tirados dentro de un fluido con `burns_entities: true` se prenden fuego y se **destruyen**, exactamente como en la lava — los jugadores que mueran dentro perderán su loot. Diséñalo con eso en mente.

### Campos de Bloques

| Campo                  | Tipo    | Por defecto | Descripción                                                                                                                                                                                                                       |
|------------------------|---------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `light_level`          | Int     | 0           | Luz emitida por el bloque (0–15)                                                                                                                                                                                                  |
| `destroy_time`         | Float   | 3.0         | Tiempo para romper con la herramienta correcta en segundos. Obsidiana=9.5, bedrock=-1 (irrompible)                                                                                                                                |
| `explosion_resistance` | Float   | 3.0         | Resistencia a explosiones. Piedra=6.0, Obsidiana=1200.0                                                                                                                                                                           |
| `sound`                | String  | `stone`     | Sonido al colocar/romper/caminar. Ver [BLOCK_SOUNDS.md](BLOCK_SOUNDS.md) para todos los valores disponibles. Por defecto: `stone`                                                                                                 |
| `map_color`            | String  | `stone`     | Color del mapa para el bloque. Ver [MAP_COLORS.md](MAP_COLORS.md) para todos los valores disponibles. Por defecto: `stone`                                                                                                        |
| `required_tool`        | String  | `none`      | Herramienta que mina el bloque eficientemente: `pickaxe`, `axe`, `shovel`, `hoe`, `sword` o `none`. Solo otorga velocidad de minado — usa `harvest_level` para condicionar los drops                                              |
| `harvest_level`        | Int     | 0           | Nivel de herramienta que condiciona los drops: 0=sin requisito (dropea con cualquier cosa), 1=piedra, 2=hierro, 3=diamante, 4=netherite. El nivel 4 usa el requisito de diamante (vanilla no tiene tag de netherite para bloques) |
| `directional`          | Boolean | false       | Si es true, rota para apuntar al jugador al colocarse. Requiere `texture.faces.north` definido                                                                                                                                    |
| `gravity`              | Boolean | false       | Si es true, cae cuando no tiene soporte, como la arena. No compatible con `directional`                                                                                                                                           |
| `texture.refs.block`   | String  | —           | (Bloques simples) Resource location aplicada a las 6 caras                                                                                                                                                                        |
| `texture.faces`        | Objeto  | —           | Textura por cara. Claves: `top`, `bottom`, `north`, `south`, `east`, `west`, `side`                                                                                                                                               |
| `texture.faces.side`   | String  | —           | Atajo: aplica a `north`, `south`, `east`, `west` si no están definidas individualmente                                                                                                                                            |

> Con `harvest_level` ≥ 1, el bloque se comporta como las menas de vanilla: la herramienta equivocada o de nivel menor es lenta Y no suelta nada. Con nivel 0 (u omitido), `required_tool` solo da velocidad de minado — el bloque dropea con cualquier cosa, como la arena. Funciona con herramientas de otros mods que sigan los niveles de vanilla. Nota: cambiar `harvest_level` entre 0 y ≥1 requiere reiniciar (el requisito de drops se fija al arrancar); ajustarlo entre 1–4, o cambiar `required_tool`, aplica con `/customgear reload`.

### Drops de Mobs

Los ítems y la comida pueden caer de los mobs al morir mediante el objeto `mob_drops`:

```json
"mob_drops": {
  "chance": 0.10,
  "min": 1,
  "max": 3,
  "requires_player_kill": true,
  "entities": ["all"]
}
```

| Campo                  | Tipo    | Por defecto | Descripción                                                                                                                                                                                                                       |
|------------------------|---------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `chance`               | Double  | 0.05        | Probabilidad de drop por muerte (0.0 representando el 0% y 1.0 representando el 100%)                                                                                                                                             |
| `min` / `max`          | Int     | 1 / 1       | Rango de cantidad soltada                                                                                                                                                                                                         |
| `requires_player_kill` | Boolean | `true`      | Solo suelta cuando un jugador hizo la kill — evita que las granjas automáticas impriman dinero                                                                                                                                    |
| `entities`             | List    | —           | **Obligatorio.** `["all"]` = todos los mobs (vanilla y de mods); IDs exactos (`"minecraft:zombie"`); tags de entidad (`"#minecraft:undead"`); comodines de mod (`"mekanism:*"`). Omitido = drop desactivado (con aviso en el log) |

Los jugadores, armor stands, barcos y vagonetas nunca sueltan ítems. Los cambios aplican en vivo con `/customgear reload` — puedes ajustar la economía de tu servidor sin reiniciar.

Los ítems con `mob_drops` muestran una sección **"Lo sueltan:"** en su tooltip, con los mobs de origen y la probabilidad de drop.

---

## Comandos

| Comando              | Permiso    | Descripción                                              |
|----------------------|------------|----------------------------------------------------------|
| `/customgear reload` | OP nivel 2 | Recarga todos los archivos JSON y texturas sin reiniciar |

### Qué actualiza el comando reload
- Nombres de ítems
- Efectos al sostener (armas y herramientas)
- Efectos por pieza y bonos de conjunto (armaduras)
- Efectos de contacto y comportamiento de fuego de los fluidos
- Recetas (el comando recarga los datapacks automáticamente)
- Tags de minado de bloques — cambios de `required_tool` y ajustes de `harvest_level` (1–4)
- Durabilidad mostrada
- Texturas y modelos — **tras presionar F3+T** (el juego solo recarga los recursos del cliente bajo demanda)
- Configuración de drops de mobs (`chance`, `min`/`max`, `entities`)

### Qué requiere reinicio completo del juego
- Daño de ataque y velocidad de ataque
- Defensa, toughness y resistencia al retroceso de armadura
- Velocidad de minado y nivel de las herramientas (tool sets)
- Activar/desactivar el requisito de drops de un bloque (`harvest_level` 0 ↔ ≥1)
- Agregar o eliminar ítems (archivos JSON nuevos o eliminados)
- Cambiar ID de ítems
- Cambios de `fire_resistant`
- Cambiar una capa de armadura entre mecanismos (reference ↔ custom ↔ transparent)

---

## Multijugador

**El cliente y el servidor deben tener los mismos archivos de contenido.** Los stats de los ítems se fijan al arrancar el juego, así que archivos diferentes causan desincronizaciones invisibles (tu tooltip dice un daño y el servidor aplica otro). Para prevenirlo, el mod verifica el contenido al conectar comparando hashes — el empaque no importa: un servidor usando un zip coincide con clientes usando los mismos JSONs sueltos, y viceversa.

Lo que ocurre ante una diferencia se configura **en el servidor** en `config/ultimatecustomgear-common.toml`:

| Modo                    | Comportamiento                                                                                                                                                                                                                                                                       |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ENFORCE` (por defecto) | El cliente es desconectado con un mensaje que muestra ambos hashes y cómo corregirlo. Garantiza que todos los jugadores vean stats, nombres y recetas correctos.                                                                                                                     |
| `WARN`                  | El cliente puede entrar; el servidor registra la diferencia y el jugador recibe un aviso en el chat de que tooltips/nombres pueden no coincidir con los valores reales (el combate siempre usa los del servidor). Útil mientras distribuyes un zip actualizado sin expulsar a todos. |
| `OFF`                   | Sin verificación. Los clientes a los que les faltan ítems igual no pueden entrar (esa es la verificación propia de Minecraft), pero las diferencias de stats pasan desapercibidas.                                                                                                   |

Solo importa el valor del servidor — la config local del cliente no tiene efecto al conectarse a un servidor.

**Distribuir contenido:** empaca tus JSONs (y texturas) de `ultimatecustomgear` en un zip, compártelo, y que tus jugadores lo coloquen en su carpeta `.minecraft/ultimatecustomgear/packs/`.

> Ambos lados deben usar la misma versión del mod: los clientes 1.3.0 no pueden entrar a servidores anteriores y viceversa (cambio de protocolo de red).

---

Novedades v2.0.0 — Armaduras 3D con Geckolib

Ahora podés definir armaduras con modelos 3D animados (en vez de la textura plana de siempre) usando GeckoLib 4.9.2. Esto es completamente opcional: si no lo configurás, tu armadura sigue funcionando como una armadura clásica de Minecraft.

Requisitos


Tener el mod GeckoLib instalado (versión 4.9.2 o compatible).
Si GeckoLib no está cargado, el sistema ignora automáticamente el modelo 3D y usa la textura plana de siempre. No hace falta que quites la config si tus jugadores no tienen GeckoLib — simplemente no se va a renderizar en 3D para ellos.


Cómo activarlo

En tu archivo de datos de la armadura (JSON), agregá dentro del bloque texture el campo render_mode con el valor "model_3d":

json{
  "id": "dragon_scale",
  "type": "armor_set",
  "names": {
    "en_us": "Dragon Scale",
    "es_mx": "Escama de Dragón"
  },
  "durability": 550,
  "enchantable": true,
  "enchantability": 15,
  "pieces": {
    "helmet":     { "durability": 165, "defense": 3, "toughness": 2.0, "knockback_resistance": 0.0 },
    "chestplate": { "durability": 240, "defense": 8, "toughness": 2.0, "knockback_resistance": 0.0 },
    "leggings":   { "durability": 225, "defense": 6, "toughness": 2.0, "knockback_resistance": 0.0 },
    "boots":      { "durability": 195, "defense": 3, "toughness": 2.0, "knockback_resistance": 0.0 }
  },
  "set_bonus": {
    "required_pieces": 4,
    "effects": [
      { "effect": "minecraft:fire_resistance", "amplifier": 0 }
    ]
  },
  "texture": {
    "render_mode": "model_3d"
  }
}

Con eso alcanza. No hace falta indicar rutas de archivo: el sistema arma las rutas solo, usando el id de la armadura (dragon_scale en el ejemplo).

Dónde van los archivos del modelo

El sistema busca automáticamente estos tres archivos, todos basados en el id de tu armadura:

ArchivoRuta esperadaModelo (.geo.json)assets/customgear/geo/armor/<id>.geo.jsonAnimación (.animation.json)assets/customgear/animations/armor/<id>.animation.jsonTextura (.png)assets/customgear/textures/armor/<id>.png

Siguiendo el ejemplo de arriba (id: "dragon_scale"), necesitás:

assets/customgear/geo/armor/dragon_scale.geo.json
assets/customgear/animations/armor/dragon_scale.animation.json
assets/customgear/textures/armor/dragon_scale.png


⚠️ Los tres archivos son obligatorios para que la armadura 3D se vea bien. Si falta alguno, GeckoLib puede fallar al renderizar o mostrar la armadura sin textura/animación.



Comportamiento pieza por pieza

El modelo, la animación y la textura son por armadura completa, no por pieza — es decir, el casco, pechera, pantalones y botas de dragon_scale comparten el mismo .geo.json/.animation.json/.png, y es tu modelo 3D (hecho en Blockbench u otra herramienta compatible con GeckoLib) el que define cómo se ve/anima cada pieza según el slot equipado.

Fallback automático

Si en algún server GeckoLib no está instalado, o si no ponés render_mode: "model_3d", la armadura se registra igual pero como ítem clásico, usando la textura plana estándar de armadura de Minecraft. No hay que crear dos configuraciones distintas para esto — es automático.

## Referencia de ID

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
- Multijugador: el servidor y los clientes deben usar la misma versión del mod y archivos de contenido coincidentes (verificado automáticamente al conectar)

---

## Licencia

Licencia MIT — ver el archivo LICENSE para más detalles.