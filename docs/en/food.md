# Food

## Food Item — Full Example

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
  "fire_resistant": true,
  "mob_drops": {
    "entities": ["minecraft:zombie", "#minecraft:undead"],
    "chance": 0.15,
    "min": 1,
    "max": 2
  },
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

This item survives fire and lava when dropped, and zombies and other undead have
a 15% chance to drop 1–2 of it upon death.

Instant food (`eat_duration: 0`) and slow food (`eat_duration: 10` = 10 seconds) are also supported.

## Food Fields

| Field                          | Type    | Default | Description                                                                                               |
|--------------------------------|---------|---------|-----------------------------------------------------------------------------------------------------------|
| `nutrition`                    | Int     | 0       | Hunger points restored. Bread=5, Cooked beef=8, Golden apple=4                                            |
| `saturation`                   | Float   | 0.6     | Saturation modifier. Bread=0.6, Cooked beef=0.8, Golden apple=1.2                                         |
| `always_edible`                | Boolean | false   | Can eat even when the hunger bar is full                                                                  |
| `fast_food`                    | Boolean | false   | Consumed faster like dried kelp (16 ticks)                                                                |
| `eat_duration`                 | Float   | -1      | Consumption time in seconds. 0=instant, 1.6=normal vanilla, 10=very slow. Overrides `fast_food` when set. |
| `on_eat_effects`               | List    | —       | Effects applied on consumption                                                                            |
| `on_eat_effects[].effect`      | String  | —       | Effect ID, e.g. `"minecraft:regeneration"`                                                                |
| `on_eat_effects[].amplifier`   | Int     | 0       | Effect level minus 1. 0=Level I, 1=Level II                                                               |
| `on_eat_effects[].duration`    | Int     | 5       | Duration in seconds                                                                                       |
| `on_eat_effects[].probability` | Float   | 1.0     | Probability of applying (0.0–1.0)                                                                         |
