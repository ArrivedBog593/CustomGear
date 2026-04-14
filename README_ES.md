# CustomGear

**CustomGear** es un mod de NeoForge para Minecraft 1.21.1 basado en datos, que permite a administradores de servidores, creadores de modpacks y jugadores agregar sets de armadura, armas y herramientas completamente personalizados — todo mediante simples archivos JSON. No se requiere programar.

---

## Características

- Agrega sets de armadura personalizados con defensa, durabilidad, toughness y resistencia al retroceso por pieza individual
- Agrega armas y sets de herramientas personalizados (espada, pico, hacha, pala, azadón) con daño, velocidad y velocidad de minado personalizados
- Efectos por pieza de armadura al portarla individualmente (ej. el casco da Visión Nocturna)
- Efectos de bonus de set al usar la armadura completa
- Efectos al sostener por herramienta (ej. el pico da Prisa Minera, la espada da Fuerza)
- Soporte completo para nombres en múltiples idiomas con formato personalizable por idioma
- Texturas personalizadas, o reutiliza texturas de otros mods con sistema de referencia flexible
- Compatible con JEI
- Todos los objetos son encantables con encantamientos de vanilla y de otros mods
- Comando `/customgear reload` para recargar los JSON sin reiniciar el juego

---

## Instalación

1. Descarga e instala [NeoForge 1.21.1](https://neoforged.net/)
2. Coloca `customgear-1.0.0.jar` en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `customgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON a `.minecraft/customgear/`
5. Reinicia el juego

---

## Estructura de archivos JSON

Todos los archivos JSON van dentro de `.minecraft/customgear/`. Cada archivo define un set de armadura o un set de herramientas.

### Set de Armadura — Ejemplo completo

**Para el mejor soporte multilingüe (especialmente para idiomas no latinos como chino, ruso, japonés), use nombres específicos de piezas:**

```json
{
  "id": "my_armor",
  "type": "armor_set",
  "name": {
    "en_us": "My Armor",
    "es_mx": "Mi Armadura",
    "ja_jp": "マイアーマー"
  },
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
    "mode": "default"
  }
}
```

Alternativa Avanzada (Formato del Nombre de la Pieza):
Si prefieres usar marcadores de posición y deseas reutilizar el nombre del conjunto en todas las piezas:

```json
{
  "id": "my_armor",
  "type": "armor_set",
  "name": {
    "en_us": "My Armor",
    "es_mx": "Mi Armadura",
    "ja_jp": "マイアーマー"
  },
  "piece_name_format": {
    "en_us": "{name} {piece}",
    "es_mx": "{piece} de {name}",
    "ja_jp": "{name}の{piece}"
  },
  "piece_names": {
    "en_us": {
      "helmet": "Helmet",
      "chestplate": "Chestplate",
      "leggings": "Leggings",
      "boots": "Boots"
    },
    "es_mx": {
      "helmet": "Casco",
      "chestplate": "Pechera",
      "leggings": "Pantalones",
      "boots": "Botas"
    },
    "ja_jp": {
      "helmet": "兜",
      "chestplate": "胸当て",
      "leggings": "脚当て",
      "boots": "靴"
    }
  },
  "pieces": { "..."},
  "enchantable": true,
  "enchantability": 15,
  "piece_effects": { "..." },
  "set_bonus": { "..." },
  "texture": { "mode": "default" }
}
```

Nota: Si tanto piece_names (específico) como piece_name_format (marcador de posición) están presentes, piece_names tiene prioridad, proporcionando un mejor control para el soporte multilingüe complejo.

### Set de Herramientas — Ejemplo completo

**Para el mejor soporte multilingüe (especialmente para idiomas no latinos como chino, ruso, japonés), use nombres de herramientas específicos:**

```json
{
  "id": "my_tools",
  "type": "tool_set",
  "name": {
    "en_us": "My Tools",
    "es_mx": "Mis Herramientas",
    "ja_jp": "マイツール"
  },
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
    "mode": "default"
  }
}
```

Alternativa Avanzada (Formato del Nombre de la Herramienta):
Si prefieres usar marcadores de posición y quieres reutilizar el nombre del conjunto en todas las herramientas:

```json
{
  "id": "my_tools",
  "type": "tool_set",
  "name": {
    "en_us": "My Tools",
    "es_mx": "Mis Herramientas"
  },
  "tool_name_format": {
    "en_us": "{name} {tool}",
    "es_mx": "{tool} de {name}"
  },
  "tool_names": {
    "en_us": {
      "pickaxe": "Pickaxe",
      "axe": "Axe",
      "shovel": "Shovel",
      "hoe": "Hoe",
      "sword": "Sword"
    },
    "es_mx": {
      "pickaxe": "Pico",
      "axe": "Hacha",
      "shovel": "Pala",
      "hoe": "Azadón",
      "sword": "Espada"
    }
  },
  "tools": { "..." },
  "enchantable": true,
  "enchantability": 22,
  "texture": {
    "mode": "default"
  }
}
```

Nota: Si tanto tool_names (específico) como tool_name_format (marcador de posición) están presentes, tool_names tiene prioridad, proporcionando un mejor control para el soporte multilingüe complejo.

---

## Referencia de campos

### Campos comunes

| Campo            | Tipo    | Descripción                                                           |
|------------------|---------|-----------------------------------------------------------------------|
| `id`             | String  | Identificador único. Solo letras minúsculas, números y guiones bajos. |
| `type`           | String  | `armor_set` o `tool_set`                                              |
| `name`           | Map     | Nombre del ítem por código de idioma (ej. `en_us`, `es_mx`, `ja_jp`)  |
| `enchantable`    | Boolean | Si el ítem puede ser encantado                                        |
| `enchantability` | Int     | Mayor = mejores encantamientos. Hierro = 9, Oro = 25, Diamante = 10   |

### Campos de armadura

| Campo                         | Tipo   | Descripción                                                             |
|-------------------------------|--------|-------------------------------------------------------------------------|
| `pieces`                      | Map    | Define cada pieza. Claves: `helmet`, `chestplate`, `leggings`, `boots`  |
| `pieces.durability`           | Int    | Durabilidad de esta pieza                                               |
| `pieces.defense`              | Int    | Puntos de armadura que provee esta pieza                                |
| `pieces.toughness`            | Float  | Resistencia de armadura por pieza. Netherite = 3.0                      |
| `pieces.knockback_resistance` | Float  | Resistencia al retroceso. Máximo 1.0 (resistencia total)                |
| `piece_name_format`           | Map    | Formato del nombre por idioma. Usa `{name}` y `{piece}` como marcadores |
| `piece_names`                 | Map    | Nombres de cada pieza por idioma                                        |
| `piece_effects`               | Map    | Efectos aplicados al portar una pieza específica individualmente        |
| `set_bonus`                   | Object | Efectos aplicados al tener el número requerido de piezas equipadas      |
| `set_bonus.required_pieces`   | Int    | Número de piezas necesarias para activar el bonus                       |
| `set_bonus.effects`           | List   | Lista de efectos a aplicar cuando el set está completo                  |

### Campos de herramientas

| Campo                 | Tipo  | Descripción                                                                 |
|-----------------------|-------|-----------------------------------------------------------------------------|
| `tools`               | Map   | Define cada herramienta. Claves: `pickaxe`, `axe`, `shovel`, `hoe`, `sword` |
| `tools.durability`    | Int   | Durabilidad de esta herramienta                                             |
| `tools.attack_damage` | Float | Daño de ataque adicional                                                    |
| `tools.attack_speed`  | Float | Velocidad de ataque. Espada estándar = 1.6                                  |
| `tools.mining_speed`  | Float | Velocidad de minado. Netherite = 9.0, Diamante = 8.0                        |
| `tools.harvest_level` | Int   | 0=Madera, 1=Piedra, 2=Hierro, 3=Diamante, 4=Netherite                       |
| `tools.held_effects`  | List  | Efectos aplicados al sostener esta herramienta específica en la mano        |
| `tool_name_format`    | Map   | Formato del nombre por idioma. Usa `{name}` y `{tool}` como marcadores      |
| `tool_names`          | Map   | Nombres de cada tipo de herramienta por idioma                              |

### Objeto de efecto

| Campo       | Tipo   | Descripción                                                                   |
|-------------|--------|-------------------------------------------------------------------------------|
| `effect`    | String | ID del efecto en formato `namespace:nombre_efecto` (ej. `minecraft:strength`) |
| `amplifier` | Int    | Nivel del efecto menos 1. `0` = Nivel I, `1` = Nivel II, etc.                 |

### Campos de textura

| Campo          | Tipo   | Descripción                                                                                    |
|----------------|--------|------------------------------------------------------------------------------------------------|
| `texture.mode` | String | `default`, `custom`, o `reference`                                                             |
| `texture.path` | String | Ruta relativa a `.minecraft/customgear/textures/` (para modo `custom`)                         |
| `texture.ref`  | String | Textura global de fallback — agrega `_tipo` o `/tipo` automáticamente (para modo `reference`)  |
| `texture.refs` | Map    | Textura individual por herramienta/pieza — tiene prioridad sobre `ref` (para modo `reference`) |

---

## Modos de textura

### `default`
Usa las texturas de armadura/herramienta de hierro como placeholder. Ideal para pruebas.

### `custom`
Usa tus propios archivos PNG desde `.minecraft/customgear/textures/`.

Para sets de armadura, necesitas dos archivos de capa:
```
textures/mi_armadura_layer_1.png   ← textura del cuerpo
textures/mi_armadura_layer_2.png   ← textura de las piernas
```

Para sets de herramientas, un PNG por herramienta:
```
textures/mis_herramientas_pickaxe.png
textures/mis_herramientas_axe.png
textures/mis_herramientas_sword.png
```

### `reference`
Reutiliza texturas de otro mod ya instalado. Dos opciones:

**Opción A — Refs individuales (recomendado, más flexible):**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "pickaxe": "mekanismtools:item/steel/pickaxe",
    "axe":     "mekanismtools:item/steel/axe",
    "shovel":  "mekanismtools:item/steel/shovel",
    "hoe":     "mekanismtools:item/steel/hoe",
    "sword":   "mekanismtools:item/steel/sword"
  }
}
```

**Para armaduras en modo reference:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "helmet":     "othermod:item/myarmor/helmet",
    "chestplate": "othermod:item/myarmor/chestplate",
    "leggings":   "othermod:item/myarmor/leggings",
    "boots":      "othermod:item/myarmor/boots"
  },
  "armor_layers": {
    "layer_1": "othermod:textures/models/armor/myarmor_layer_1",
    "layer_2": "othermod:textures/models/armor/myarmor_layer_2"
  }
}
```

**Opción B — Ref global con sufijo automático:**

Si el ref termina en `/`, el tipo de herramienta se agrega directamente:
```json
"texture": {
  "mode": "reference",
  "ref": "otromod:item/material/"
}
```
Genera: `otromod:item/material/pickaxe`, `otromod:item/material/sword`, etc.

Si el ref NO termina en `/`, se agrega un guión bajo:
```json
"texture": {
  "mode": "reference",
  "ref": "otromod:item/material"
}
```
Genera: `otromod:item/material_pickaxe`, `otromod:item/material_sword`, etc.

Si tanto `ref` como `refs` están presentes, `refs` tiene prioridad por tipo de herramienta.

**Para armaduras en modo reference:**
```json
"texture": {
  "mode": "reference",
  "refs": {
    "helmet":     "otromod:item/miarmadura/helmet",
    "chestplate": "otromod:item/miarmadura/chestplate",
    "leggings":   "otromod:item/miarmadura/leggings",
    "boots":      "otromod:item/miarmadura/boots"
  }
}
```

---

## ID de efectos comunes de vanilla

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
| Suerte               | `minecraft:luck`            |

Los efectos de otros mods también funcionan — usa su ID en formato `modid:nombre_efecto`.

---

## Comandos

| Comando              | Permiso    | Descripción                                              |
|----------------------|------------|----------------------------------------------------------|
| `/customgear reload` | OP nivel 2 | Recarga todos los archivos JSON y texturas sin reiniciar |

---

## Agregar recetas

CustomGear no agrega recetas de fabricación por defecto. Para agregar recetas, usa [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) u otro mod similar. Los ID de tus ítems siguen el patrón:
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
