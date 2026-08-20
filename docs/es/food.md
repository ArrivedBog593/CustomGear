# Comida

## Ítem Comestible — Ejemplo completo

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
    "refs": { "item": "minecraft:item/golden_apple" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": ["GGG","GAG","GGG"],
    "key": { "G": "minecraft:gold_block", "A": "minecraft:apple" }
  }
}
```

Este objeto sobrevive al fuego y la lava cuando se deja caer, y los zombis y otros no muertos tienen un 
15% de probabilidad de soltar 1-2 de ellos al morir.

También se soporta comida instantánea (`eat_duration: 0`) y comida lenta (`eat_duration: 10` = 10 segundos).

## Campos de comida

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
