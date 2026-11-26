# Fluidos

## Fluido con Efectos al Contacto — Ejemplo completo

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
  "tick_rate": 10,
  "spread_distance": 6,
  "burns_entities": false,
  "contact_effect_interval": 2.0,
  "contact_effects": [
    { "effect": "minecraft:poison",   "amplifier": 0, "duration": 3 },
    { "effect": "minecraft:slowness", "amplifier": 1, "duration": 3 }
  ],
  "texture": {
    "refs": {
      "still": "minecraft:block/lava_still",
      "flowing": "minecraft:block/lava_flow"
    }
  }
}
```

## Fluido tipo Lava — Ejemplo completo

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
    "refs": {
      "still": "minecraft:block/lava_still",
      "flowing": "minecraft:block/lava_flow"
    }
  }
}
```

> **Las texturas de fluido usan la forma corta** — `minecraft:block/lava_still`,
> no la ruta completa. Un sprite de fluido se cose al atlas de bloques, y así es
> como el atlas lo direcciona.
>
> No declarar ni `still` ni `flowing` convierte al fluido en un reskin del agua,
> que es también lo que le da el tinte azul. Declarar uno y no el otro se
> rechaza: el fluido dibujaría su propia textura estática y la de flujo del agua.
>
> Los fluidos animados necesitan su `.mcmeta` junto al PNG — consulta
> [Texturas y Modelos](textures-and-models.md).

## Campos de fluidos

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
