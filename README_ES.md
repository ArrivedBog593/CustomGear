# CustomGear

**CustomGear** es un mod de NeoForge para Minecraft 1.21.1 basado en datos, que permite a administradores de servidores, creadores de modpacks y jugadores agregar sets de armadura, armas y herramientas completamente personalizados — todo mediante simples archivos JSON. No se requiere programar.

---

## Características

- Agrega sets de armadura personalizados con defensa, durabilidad, toughness y resistencia al retroceso por pieza individual
- Agrega armas y sets de herramientas personalizados (espada, pico, hacha, pala, azadón) con daño, velocidad y velocidad de minado personalizados
- Efectos por pieza de armadura al portarla individualmente (ej. el casco da Visión Nocturna)
- Efectos de bonus de set al usar la armadura completa
- Efectos al sostener por herramienta (ej. el pico da Prisa Minera, la espada da Fuerza)
- Soporte completo para nombres en múltiples idiomas — define el nombre completo por idioma sin restricciones de formato
- Texturas personalizadas con sistema de rutas flexible, o reutiliza texturas de otros mods
- Los archivos JSON pueden organizarse en cualquier estructura de subcarpetas dentro de `.minecraft/customgear/`
- Compatible con JEI
- Todos los objetos son encantables con encantamientos de vanilla y de otros mods
- Comando `/customgear reload` para recargar nombres, texturas y efectos sin reiniciar el juego

---

## Instalación

1. Descarga e instala [NeoForge 1.21.1](https://neoforged.net/)
2. Coloca `customgear-1.0.0.jar` en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `customgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON a `.minecraft/customgear/`
5. Reinicia el juego

---

## Estructura de archivos JSON

Todos los archivos JSON van dentro de `.minecraft/customgear/`. Cada archivo define un set de armadura o un set de herramientas. Los archivos pueden organizarse en cualquier estructura de subcarpetas.

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
    },
    "ja_jp": {
      "helmet": "マイアーマーヘルメット",
      "chestplate": "マイアーマーチェストプレート",
      "leggings": "マイアーマーレギンス",
      "boots": "マイアーマーブーツ"
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
    "chestplate": [
      { "effect": "minecraft:fire_resistance", "amplifier": 0 }
    ],
    "leggings": [
      { "effect": "minecraft:speed", "amplifier": 0 }
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

```json
{
  "id": "mis_herramientas",
  "type": "tool_set",
  "tool_names": {
    "en_us": {
      "pickaxe": "My Pickaxe",
      "axe": "My Axe",
      "shovel": "My Shovel",
      "hoe": "My Hoe",
      "sword": "My Sword"
    },
    "es_mx": {
      "pickaxe": "Mi Pico",
      "axe": "Mi Hacha",
      "shovel": "Mi Pala",
      "hoe": "Mi Azada",
      "sword": "Mi Espada"
    },
    "ja_jp": {
      "pickaxe": "マイピッケル",
      "axe": "マイ斧",
      "shovel": "マイシャベル",
      "hoe": "マイ鍬",
      "sword": "マイソード"
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
    },
    "sword": {
      "durability": 5000,
      "attack_damage": 20.0,
      "attack_speed": 1.6,
      "held_effects": [
        { "effect": "minecraft:strength", "amplifier": 2 }
      ]
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
      "hoe":     "item/herramientas/azadon.png",
      "sword":   "item/herramientas/espada.png"
    }
  }
}
```

### Herramienta individual - Ejemplo completo
```json
{
  "id": "mi_espada",
  "type": "sword",
  "name": {
    "en_us": "My Custom Sword",
    "es_mx": "Mi Espada Personalizada"
  },
  "durability": 1561,
  "attack_damage": 5.0,
  "attack_damage_bonus": 3.0,
  "attack_speed": 1.6,
  "enchantable": true,
  "enchantability": 10,
  "held_effects": [
    { "effect": "minecraft:strength", "amplifier": 1 }
  ],
  "texture": {
    "mode": "custom",
    "refs": {
      "sword": "item/armas/my_sword.png"
    }
  }
}
```

---

## Referencia de campos

### Campos comunes

| Campo            | Tipo    | Descripción                                                                 |
|------------------|---------|-----------------------------------------------------------------------------|
| `id`             | String  | Identificador único. Solo letras minúsculas, números y guiones bajos.       |
| `type`           | String  | `armor_set` o `tool_set`                                                    |
| `name`           | Map     | Nombre completo del objeto por idioma (solo para herramientas individuales) |
| `enchantable`    | Boolean | Si el ítem puede ser encantado                                              |
| `enchantability` | Int     | Mayor = mejores encantamientos. Hierro = 9, Oro = 25, Diamante = 10         |

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
| `tools`                     | Map   | Define cada herramienta. Claves: `pickaxe`, `axe`, `shovel`, `hoe`, `sword`                                       |
| `tools.durability`          | Int   | Durabilidad de esta herramienta                                                                                   |
| `tools.attack_damage`       | Float | Daño de ataque adicional                                                                                          |
| `tools.attack_damage_bonus` | Float | Daño adicional de armas (bonus). Añadido a 'attack_damage'                                                        |
| `tools.attack_speed`        | Float | Velocidad de ataque. Espada estándar = 1.6                                                                        |
| `tools.mining_speed`        | Float | Velocidad de minado. Netherite = 9.0, Diamante = 8.0                                                              |
| `tools.harvest_level`       | Int   | 0=Madera, 1=Piedra, 2=Hierro, 3=Diamante, 4=Netherite                                                             |
| `tools.held_effects`        | List  | Efectos aplicados al sostener esta herramienta específica en la mano                                              |
| `tools.till_radius`         | Int   | (Solo azada) Radio de bloques a arar alrededor del bloque objetivo. 0 = sin arado en área                         |
| `tool_names`                | Map   | Nombre completo de cada herramienta por idioma. Cada idioma define todas las herramientas de forma independiente. |

### Explicación del daño de ataque
- **`attack_damage`**: Daño base de ataque del arma.
- **`attack_damage_bonus`**: Daño adicional que se añade a 'attack_damage'.

**Nota**: Para referencia, herramientas vanilla:
- Espada de madera: 4.0 de daño de ataque
- Espada de piedra: 5.0 de daño de ataque
- Espada de hierro: 6.0 de daño de ataque
- Espada de diamante: 7.0 de daño de ataque
- Espada de netherita: 8.0 de daño de ataque

### Objeto de efecto

| Campo       | Tipo   | Descripción                                                                   |
|-------------|--------|-------------------------------------------------------------------------------|
| `effect`    | String | ID del efecto en formato `namespace:nombre_efecto` (ej. `minecraft:strength`) |
| `amplifier` | Int    | Nivel del efecto menos 1. `0` = Nivel I, `1` = Nivel II, etc.                 |

### Campos de textura

| Campo                  | Tipo   | Descripción                                                                                                                                      |
|------------------------|--------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode`         | String | `default`, `custom`, o `reference`                                                                                                               |
| `texture.refs`         | Map    | Ruta de textura por pieza/herramienta. Para `custom`: ruta relativa a `.minecraft/customgear/`. Para `reference`: resource location de otro mod. |
| `texture.armor_layers` | Map    | Texturas de capa del modelo de armadura (`layer_1`, `layer_2`). Requerido para armaduras en modos `custom` y `reference`.                        |

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

**Para sets de armadura, debes proporcionar:**
- armor_layers: Texturas de las capas del modelo de armadura
- refs: Rutas individuales para cada pieza de armadura
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

**Para sets de herramientas, un PNG por herramienta:**
- refs: Rutas individuales para cada herramienta
```json
"texture": {
  "mode": "custom",
  "refs": {
    "pickaxe": "item/herramientas/pico.png",
    "axe":     "item/herramientas/hacha.png",
    "shovel":  "item/herramientas/pala.png",
    "hoe":     "item/herramientas/azadon.png",
    "sword":   "item/herramientas/espada.png"
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
│   └── herramientas/
│       ├── pico.png
│       ├── hacha.png
│       ├── pala.png
│       ├── azadon.png
│       └── espada.png
├── armadura1.json
├── herramientas1.json
└── json/
    ├── armadura2.json
    ├── armaduras/
    │   └── armadura3.json
    └── herramientas/
        └── herramientas2.json
```

Los archivos JSON pueden colocarse en cualquier subcarpeta — CustomGear escanea todas las subcarpetas automáticamente.

### `reference`
Reutiliza texturas de otro mod ya instalado. Todos los refs deben usar el resource location completo (`modid:ruta/a/textura`).

**Para sets de herramientas:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "pickaxe": "otromod:item/miherramienta/pickaxe",
    "axe":     "otromod:item/miherramienta/axe",
    "shovel":  "otromod:item/miherramienta/shovel",
    "hoe":     "otromod:item/miherramienta/hoe",
    "sword":   "otromod:item/miherramienta/sword"
  }
}
```

**Para sets de armadura:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "helmet":     "otromod:item/miarmadura/helmet",
    "chestplate": "otromod:item/miarmadura/chestplate",
    "leggings":   "otromod:item/miarmadura/leggings",
    "boots":      "otromod:item/miarmadura/boots"
  },
  "armor_layers": {
    "layer_1": "otromod:textures/models/armor/miarmadura_layer_1",
    "layer_2": "otromod:textures/models/armor/miarmadura_layer_2"
  }
}
```

> **Nota:** Para encontrar el resource location correcto de una textura de otro mod, abre el `.jar` del mod (es un ZIP) y navega a `assets/<modid>/textures/`. El resource location sigue el patrón `modid:ruta/dentro/de/textures/carpeta` sin la extensión `.png` para el modo `reference`.

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
- Texturas
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
- Herramientas: `customgear:mis_herramientas_pickaxe`, `customgear:mis_herramientas_sword`, etc.

---

## Compatibilidad

- Minecraft 1.21.1
- NeoForge 21.1.x
- JEI (opcional, recomendado)
- Compatible con KubeJS para recetas
- Los encantamientos de otros mods funcionan automáticamente en ítems encantables
- Las texturas de cualquier mod instalado pueden referenciarse con el modo `reference`

---

## Licencia

Licencia MIT — ver archivo LICENSE para más detalles.