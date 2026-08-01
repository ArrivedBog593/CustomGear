# Fluids

## Fluid with Contact Effects — Full Example

```json
{
  "id": "poison_lake",
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

## Lava-like Fluid — Full Example

```json
{
  "id": "magma_fluid",
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

## Fluid Fields

| Field                         | Type    | Default        | Description                                                                                                 |
|-------------------------------|---------|----------------|-------------------------------------------------------------------------------------------------------------|
| `light_level`                 | Int     | 0              | Light emitted by the fluid block (0–15)                                                                     |
| `color`                       | String  | `"0xFFFFFFFF"` | Tint color in ARGB hex format, e.g. `"0xFF3F76E4"`                                                          |
| `tick_rate`                   | Int     | 5              | Ticks between each spread step. Lower = faster. Water=5, Lava=30                                            |
| `spread_distance`             | Int     | 8              | Max horizontal spread in blocks. Water=8, Lava=4                                                            |
| `burns_entities`              | Boolean | false          | Sets entities on fire like lava — affects players, mobs and dropped items (fire-immune mobs are unaffected) |
| `burn_duration`               | Int     | 5              | Seconds the entity burns. Only if `burns_entities` is true                                                  |
| `contact_effect_interval`     | Float   | 1.0            | Seconds between each effect application while in the fluid                                                  |
| `contact_effects`             | List    | —              | Effects applied to living entities (players and mobs) while submerged                                       |
| `contact_effects[].effect`    | String  | —              | Effect ID, e.g. `"minecraft:poison"`                                                                        |
| `contact_effects[].amplifier` | Int     | 0              | Effect level minus 1                                                                                        |
| `contact_effects[].duration`  | Int     | 3              | Duration in seconds per application                                                                         |

> `contact_effects[].duration` should be at least ~2 seconds longer than `contact_effect_interval`, or damage-over-time effects (poison, wither) won't get the chance to tick. The defaults (duration 3, interval 1.0) are safe.

> ⚠️ Dropped items inside a fluid with `burns_entities: true` catch fire and are **destroyed**, exactly like in lava — players who die in it will lose their drops. Design accordingly.
