# CustomGear

**CustomGear** es un mod de NeoForge para Minecraft 1.21.1 basado en datos, que permite a administradores de servidores, creadores de modpacks y jugadores agregar sets de armadura, armas y herramientas completamente personalizados — todo mediante simples archivos JSON. No se requiere programar.

---

## Características

- Agrega **sets de armadura** personalizados con defensa, durabilidad, toughness y resistencia al retroceso por pieza individual
- Agrega **sets de herramientas** (pico, hacha, pala, azadón) e herramientas individuales con daño, velocidad y velocidad de minado personalizados
- Agrega **sets de armas** (espada, arco, ballesta, escudo) y armas individuales
- Agrega **arcos** personalizados con daño de flecha y velocidad de carga configurables
- Agrega **ballestas** personalizadas con daño de flecha y velocidad de carga configurables
- Agrega **escudos** personalizados con durabilidad configurable
- Efectos por pieza de armadura al portarla individualmente (ej. el casco da Visión Nocturna)
- Efectos de bonus de set al tener el número requerido de piezas equipadas
- Efectos al sostener por herramienta/arma (ej. el pico da Prisa, la espada da Fuerza)
- Soporte completo para nombres en múltiples idiomas — define el nombre completo por idioma sin restricciones de formato
- Texturas personalizadas con sistema de rutas flexible, o reutiliza modelos de otros mods
- Los archivos JSON pueden organizarse en cualquier estructura de subcarpetas dentro de `.minecraft/customgear/`
- Compatible con JEI
- Todos los ítems son encantables con encantamientos de vanilla y de otros mods
- Comando `/customgear reload` para recargar nombres, texturas y efectos sin reiniciar el juego

---

## Instalación

1. Descarga e instala [NeoForge 1.21.1](https://neoforged.net/)
2. Coloca `customgear-1.0.jar` en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `customgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON a `.minecraft/customgear/`
5. Reinicia el juego

> **Nota de sintaxis JSON:** El JSON estándar no permite comas al final del último elemento. Una coma sobrante al final de un objeto o lista hará que el archivo sea ignorado silenciosamente al cargar.

---

## Estructura de archivos JSON

Todos los archivos JSON van dentro de `.minecraft/customgear/`. Cada archivo define un set de ítems o un arma. Los archivos pueden organizarse en cualquier estructura de subcarpetas.

### Set de Armadura — Ejemplo completo

```json
{
  "id": "mi_armadura",
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
      "layer_1": "models/mi_armadura/layer_1.png",
      "layer_2": "models/mi_armadura/layer_2.png"
    },
    "refs": {
      "helmet":     "item/armor/casco.png",
      "chestplate": "item/armor/pechera.png",
      "leggings":   "item/armor/pantalones.png",
      "boots":      "item/armor/botas.png"
    }
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
    "mode": "custom",
    "refs": {
      "pickaxe": "item/herramientas/pico.png",
      "axe":     "item/herramientas/hacha.png",
      "shovel":  "item/herramientas/pala.png",
      "hoe":     "item/herramientas/azadon.png"
    }
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

### Espada individual — Ejemplo completo

```json
{
  "id": "mi_espada",
  "type": "sword",
  "names": {
    "en_us": "My Custom Sword",
    "es_mx": "Mi Espada Personalizada"
  },
  "durability": 2031,
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
    "refs": {
      "bow": "minecraft:item/bow"
    }
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
    "refs": {
      "crossbow": "minecraft:item/crossbow"
    }
  }
}
```

---

## Referencia de campos

### Campos comunes

| Campo            | Tipo    | Descripción                                                                                                            |
|------------------|---------|------------------------------------------------------------------------------------------------------------------------|
| `id`             | String  | Identificador único. Solo letras minúsculas, números y guiones bajos.                                                  |
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

> **Nota sobre charge_speed:** Valores mayores a 1.0 no están soportados actualmente para ballestas y serán ignorados. Solo valores ≤ 1.0 (más lento que vanilla) tienen efecto.

### Campos para armas individuales (no en weapon_set)

Las armas individuales (`type: "sword"`, `type: "bow"`, etc.) usan los mismos campos que los anteriores pero al nivel raíz del JSON en lugar de dentro de `weapons`:

```json
{
  "id": "mi_arco",
  "type": "bow",
  "durability": 384,
  "arrow_damage": 8.0,
  "charge_speed": 1.0,
  ...
}
```

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

| Campo                  | Tipo   | Descripción                                                                                                                                                                                                                                                                                 |
|------------------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode`         | String | `default`, `custom`, o `reference`                                                                                                                                                                                                                                                          |
| `texture.refs`         | Map    | Para `custom`: ruta relativa a un archivo PNG dentro de `.minecraft/customgear/`. Para `reference`: resource location completo del modelo de otro mod (ej. `minecraft:item/netherite_sword`). **Para herramientas y armas en modo reference, se usa la ruta del modelo, no de la textura.** |
| `texture.armor_layers` | Map    | Texturas de capa del modelo de armadura (`layer_1`, `layer_2`). Requerido para armaduras en modo `custom`. En modo `reference`, usa el resource location del material de armadura.                                                                                                          |

---

## Modos de textura

### `default`
Usa las texturas de armadura/herramienta de hierro como placeholder. Ideal para pruebas.
```json
"texture": {
  "mode": "default"
}
```

### `custom`
Usa tus propios archivos PNG. Todas las rutas en `refs` y `armor_layers` son relativas a `.minecraft/customgear/`.

**Para sets de armadura:**
```json
"texture": {
  "mode": "custom",
  "armor_layers": {
    "layer_1": "models/mi_armadura/layer_1.png",
    "layer_2": "models/mi_armadura/layer_2.png"
  },
  "refs": {
    "helmet":     "item/armor/casco.png",
    "chestplate": "item/armor/pechera.png",
    "leggings":   "item/armor/pantalones.png",
    "boots":      "item/armor/botas.png"
  }
}
```

**Para sets de herramientas:**
```json
"texture": {
  "mode": "custom",
  "refs": {
    "pickaxe": "item/herramientas/pico.png",
    "axe":     "item/herramientas/hacha.png",
    "shovel":  "item/herramientas/pala.png",
    "hoe":     "item/herramientas/azadon.png"
  }
}
```

**Para sets de armas (el arco requiere fotogramas de la animación de tensado):**
```json
"texture": {
  "mode": "custom",
  "refs": {
    "sword":         "item/armas/espada.png",
    "bow":           "item/armas/arco.png",
    "bow_pulling_0": "item/armas/arco_tensando_0.png",
    "bow_pulling_1": "item/armas/arco_tensando_1.png",
    "bow_pulling_2": "item/armas/arco_tensando_2.png"
  }
}
```

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

### `reference`
Reutiliza modelos de otro mod ya instalado. Para herramientas y armas, `refs` debe apuntar a un **resource location de modelo** (no de textura). Para `refs` de armadura, apunta al ícono de inventario del ítem.

**Para sets de herramientas:**
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

**Para sets de armas:**
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

**Para sets de armadura:**
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

> **Cómo encontrar resource locations:** Abre el `.jar` del mod como ZIP y navega a `assets/<modid>/models/item/`. El resource location sigue el patrón `modid:item/nombre_archivo` sin la extensión `.json`.

---

## IDs de efectos comunes de vanilla

| Efecto               | ID                          |
|----------------------|-----------------------------|
| Velocidad            | `minecraft:speed`           |
| Prisa minera         | `minecraft:haste`           |
| Fuerza               | `minecraft:strength`        |
| Salto                | `minecraft:jump_boost`      |
| Regeneración         | `minecraft:regeneration`    |
| Resistencia          | `minecraft:resistance`      |
| Resistencia al fuego | `minecraft:fire_resistance` |
| Visión nocturna      | `minecraft:night_vision`    |
| Respiración acuática | `minecraft:water_breathing` |
| Invisibilidad        | `minecraft:invisibility`    |
| Caída lenta          | `minecraft:slow_falling`    |
| Aumento de salud     | `minecraft:health_boost`    |
| Suerte               | `minecraft:luck`            |

Los efectos de otros mods también funcionan — usa su ID en formato `modid:nombre_efecto`.

---

## Comandos

| Comando              | Permiso    | Descripción                                              |
|----------------------|------------|----------------------------------------------------------|
| `/customgear reload` | OP nivel 2 | Recarga todos los archivos JSON y texturas sin reiniciar |

### Lo que actualiza el comando reload
- Nombres de los ítems
- Texturas y modelos
- Efectos al sostener (armas y herramientas)
- Efectos por pieza y bonus de set (armadura)
- Visualización de durabilidad

### Lo que requiere reiniciar el juego
- Daño de ataque y velocidad de ataque
- Defensa, toughness y resistencia al retroceso de armadura
- Velocidad de minado y harvest level
- Agregar nuevos ítems (nuevos archivos JSON)
- Eliminar ítems existentes (archivos JSON eliminados)
- Cambiar IDs de ítems

---

## Agregar recetas

CustomGear no agrega recetas de crafteo por defecto. Para agregar recetas, usa [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) u otro mod similar. Los IDs de tus ítems siguen el patrón:
- Armadura: `customgear:mi_armadura_helmet`, `customgear:mi_armadura_chestplate`, etc.
- Herramientas: `customgear:mis_herramientas_pickaxe`, `customgear:mis_herramientas_axe`, etc.
- Armas: `customgear:mis_armas_sword`, `customgear:mis_armas_bow`, etc.
- Ítems individuales: `customgear:mi_espada`, `customgear:mi_arco`, etc.

---

## Compatibilidad

- Minecraft 1.21.1
- NeoForge 21.1.x
- JEI (opcional, recomendado)
- Compatible con KubeJS para recetas
- Los encantamientos de otros mods funcionan automáticamente en ítems encantables
- Los modelos de cualquier mod instalado pueden referenciarse con el modo `reference`

---

## Licencia

Licencia MIT — ver archivo LICENSE para más detalles.