# CustomGear

**CustomGear** es un mod de NeoForge para Minecraft 1.21.1 basado en datos, que permite a administradores de servidores, creadores de modpacks y jugadores agregar sets de armadura, armas y herramientas completamente personalizados — todo mediante simples archivos JSON. No se requiere programar.

---

## Características

- Agrega sets de armadura personalizados con defensa, durabilidad, toughness y resistencia al retroceso por pieza
- Agrega armas y sets de herramientas personalizados (espada, pico, hacha, pala, azadón) con daño, velocidad y velocidad de minado personalizados
- Efectos por pieza de armadura (ej. el casco da Visión Nocturna al portarlo)
- Efectos de bonus de set al usar la armadura completa
- Efectos al sostener armas y herramientas (ej. la espada da Fuerza al sostenerla)
- Soporte completo para nombres en múltiples idiomas
- Texturas personalizadas, o reutiliza texturas de otros mods
- Compatible con JEI
- Todos los objetos son encantables con encantamientos de vanilla y de otros mods

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

### Set de Armadura

```json
{
  "id": "mi_armadura",
  "type": "armor_set",
  "name": {
    "en_us": "My Armor",
    "es_mx": "Mi Armadura"
  },
  "piece_name_format": {
    "en_us": "{name} {piece}",
    "es_mx": "{piece} de {name}"
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
    ]
  },
  "set_bonus": {
    "required_pieces": 4,
    "effects": [
      { "effect": "minecraft:speed", "amplifier": 1 },
      { "effect": "minecraft:strength", "amplifier": 0 }
    ]
  },
  "texture": {
    "mode": "default"
  }
}
```

### Set de Herramientas

```json
{
  "id": "mis_herramientas",
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

---

## Referencia de campos

### Campos comunes

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | Identificador único. Solo letras minúsculas, números y guiones bajos. |
| `type` | String | `armor_set` o `tool_set` |
| `name` | Map | Nombre del ítem por código de idioma (ej. `en_us`, `es_mx`, `ja_jp`) |
| `enchantable` | Boolean | Si el ítem puede ser encantado |
| `enchantability` | Int | Mayor = mejores encantamientos. Hierro = 9, Oro = 25, Diamante = 10 |

### Campos de armadura

| Campo | Tipo | Descripción |
|---|---|---|
| `pieces` | Map | Define cada pieza. Claves: `helmet`, `chestplate`, `leggings`, `boots` |
| `pieces.durability` | Int | Durabilidad de esta pieza |
| `pieces.defense` | Int | Puntos de armadura que provee esta pieza |
| `pieces.toughness` | Float | Resistencia de armadura. Netherite = 3.0 por pieza |
| `pieces.knockback_resistance` | Float | Resistencia al retroceso. Máximo 1.0 (resistencia total) |
| `piece_name_format` | Map | Formato del nombre por idioma. Usa `{name}` y `{piece}` como marcadores |
| `piece_names` | Map | Nombres de cada pieza por idioma |
| `piece_effects` | Map | Efectos aplicados al portar una pieza específica |
| `set_bonus` | Object | Efectos aplicados al tener el número requerido de piezas equipadas |
| `set_bonus.required_pieces` | Int | Número de piezas necesarias para activar el bonus |
| `set_bonus.effects` | List | Lista de efectos a aplicar |

### Campos de herramientas

| Campo | Tipo | Descripción |
|---|---|---|
| `tools` | Map | Define cada herramienta. Claves: `pickaxe`, `axe`, `shovel`, `hoe`, `sword` |
| `tools.durability` | Int | Durabilidad de esta herramienta |
| `tools.attack_damage` | Float | Daño de ataque adicional |
| `tools.attack_speed` | Float | Velocidad de ataque. Espada estándar = 1.6 |
| `tools.mining_speed` | Float | Velocidad de minado. Netherite = 9.0, Diamante = 8.0 |
| `tools.harvest_level` | Int | 0=Madera, 1=Piedra, 2=Hierro, 3=Diamante, 4=Netherite |
| `tools.held_effects` | List | Efectos aplicados al sostener esta herramienta en la mano |
| `tool_name_format` | Map | Formato del nombre por idioma. Usa `{name}` y `{tool}` como marcadores |
| `tool_names` | Map | Nombres de cada tipo de herramienta por idioma |

### Objeto de efecto

| Campo | Tipo | Descripción |
|---|---|---|
| `effect` | String | ID del efecto en formato `namespace:nombre_efecto` (ej. `minecraft:strength`) |
| `amplifier` | Int | Nivel del efecto menos 1. `0` = Nivel I, `1` = Nivel II, etc. |

### Campo de textura

| Campo | Tipo | Descripción |
|---|---|---|
| `texture.mode` | String | `default`, `custom`, o `reference` |
| `texture.path` | String | Ruta relativa a `.minecraft/customgear/` (para modo `custom`) |
| `texture.ref` | String | Ubicación de recurso de textura de otro mod (para modo `reference`) |

---

## Modos de textura

### `default`
Usa la textura de armadura/herramienta de hierro como placeholder. Ideal para pruebas.

### `custom`
Usa tus propios archivos PNG colocados en `.minecraft/customgear/textures/`.

Para sets de armadura, necesitas dos archivos de capa:
```
textures/mi_armadura_layer_1.png   ← textura del cuerpo
textures/mi_armadura_layer_2.png   ← textura de las piernas
```

Para herramientas, un PNG por tipo de herramienta:
```
textures/mis_herramientas_pickaxe.png
textures/mis_herramientas_sword.png
```

### `reference`
Reutiliza una textura de otro mod ya instalado:
```json
"texture": {
  "mode": "reference",
  "ref": "otromod:item/alguna_espada"
}
```

---

## IDs de efectos comunes de vanilla

| Efecto | ID |
|---|---|
| Velocidad | `minecraft:speed` |
| Prisa minera | `minecraft:haste` |
| Fuerza | `minecraft:strength` |
| Salto | `minecraft:jump_boost` |
| Regeneración | `minecraft:regeneration` |
| Resistencia | `minecraft:resistance` |
| Resistencia al fuego | `minecraft:fire_resistance` |
| Visión nocturna | `minecraft:night_vision` |
| Respiración acuática | `minecraft:water_breathing` |
| Invisibilidad | `minecraft:invisibility` |
| Caída lenta | `minecraft:slow_falling` |
| Suerte | `minecraft:luck` |

---

## Agregar recetas

CustomGear no agrega recetas de crafteo por defecto. Para agregar recetas a tus ítems personalizados, usa [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) u otro mod similar. Los IDs de tus ítems siguen el patrón `customgear:mi_armadura_helmet`, `customgear:mis_herramientas_sword`, etc.

---

## Compatibilidad

- Minecraft 1.21.1
- NeoForge 21.1.x
- JEI (opcional, recomendado)
- Compatible con KubeJS para recetas
- Los encantamientos personalizados de otros mods funcionan automáticamente si el ítem es encantable

---

## Licencia

Licencia MIT — ver archivo LICENSE para más detalles.
