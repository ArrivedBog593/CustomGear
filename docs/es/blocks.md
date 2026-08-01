# Bloques

## Bloque — Ejemplo Simple

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

## Bloque con Texturas por Cara — Ejemplo

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

## Bloque Direccional — Ejemplo

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

## Falling Block — Example

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

## Campos de Bloques

| Campo                  | Tipo    | Por defecto | Descripción                                                                                                                                                                                                                       |
|------------------------|---------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `light_level`          | Int     | 0           | Luz emitida por el bloque (0–15)                                                                                                                                                                                                  |
| `destroy_time`         | Float   | 3.0         | Tiempo para romper con la herramienta correcta en segundos. Obsidiana=9.5, bedrock=-1 (irrompible)                                                                                                                                |
| `explosion_resistance` | Float   | 3.0         | Resistencia a explosiones. Piedra=6.0, Obsidiana=1200.0                                                                                                                                                                           |
| `sound`                | String  | `stone`     | Sonido al colocar/romper/caminar. Ver [Tipos de sonido de bloque](block-sounds.md) para todos los valores disponibles. Por defecto: `stone`                                                                                       |
| `map_color`            | String  | `stone`     | Color del mapa para el bloque. Ver [Colores de mapa](map-colors.md) para todos los valores disponibles. Por defecto: `stone`                                                                                                      |
| `required_tool`        | String  | `none`      | Herramienta que mina el bloque eficientemente: `pickaxe`, `axe`, `shovel`, `hoe`, `sword` o `none`. Solo otorga velocidad de minado — usa `harvest_level` para condicionar los drops                                              |
| `harvest_level`        | Int     | 0           | Nivel de herramienta que condiciona los drops: 0=sin requisito (dropea con cualquier cosa), 1=piedra, 2=hierro, 3=diamante, 4=netherite. El nivel 4 usa el requisito de diamante (vanilla no tiene tag de netherite para bloques) |
| `directional`          | Boolean | false       | Si es true, rota para apuntar al jugador al colocarse. Requiere `texture.faces.north` definido                                                                                                                                    |
| `gravity`              | Boolean | false       | Si es true, cae cuando no tiene soporte, como la arena. No compatible con `directional`                                                                                                                                           |
| `texture.refs.block`   | String  | —           | (Bloques simples) Resource location aplicada a las 6 caras                                                                                                                                                                        |
| `texture.faces`        | Objeto  | —           | Textura por cara. Claves: `top`, `bottom`, `north`, `south`, `east`, `west`, `side`                                                                                                                                               |
| `texture.faces.side`   | String  | —           | Atajo: aplica a `north`, `south`, `east`, `west` si no están definidas individualmente                                                                                                                                            |

> Con `harvest_level` ≥ 1, el bloque se comporta como las menas de vanilla: la herramienta equivocada o de nivel menor es lenta Y no suelta nada. Con nivel 0 (u omitido), `required_tool` solo da velocidad de minado — el bloque se obtiene con cualquier cosa, como la arena. Funciona con herramientas de otros mods que sigan los niveles de vanilla. Nota: cambiar `harvest_level` entre 0 y ≥1 requiere reiniciar (el requisito de drops se fija al arrancar); ajustarlo entre 1–4, o cambiar `required_tool`, aplica con `/customgear reload`.
