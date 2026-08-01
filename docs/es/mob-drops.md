# Drops de mobs

Los ítems y la comida pueden caer de los mobs al morir mediante el objeto `mob_drops`:

```json
{
  "mob_drops": {
    "chance": 0.10,
    "min": 1,
    "max": 3,
    "requires_player_kill": true,
    "looting_mode": "count",
    "entities": ["all"]
  }
}
```

| Campo                  | Tipo    | Por defecto | Descripción                                                                                                                                                                                                                       |
|------------------------|---------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `chance`               | Double  | 0.05        | Probabilidad de drop por muerte (0.0 = 0%, 1.0 = 100%)                                                                                                                                                                            |
| `min` / `max`          | Int     | 1 / 1       | Rango de cantidad soltada                                                                                                                                                                                                         |
| `requires_player_kill` | Boolean | `false`     | Requiere daño reciente de un jugador — no el golpe final. Ponlo en `true` para una economía, o una granja de daño por caída puede imprimir moneda                                                                                 |
| `looting_mode`         | String  | `"count"`   | Cómo afecta el Saqueo a este drop: `"count"`, `"chance"` o `"none"`. Ver abajo                                                                                                                                                    |
| `looting_chance_bonus` | Double  | 0.01        | Probabilidad que suma cada nivel de Saqueo, solo en modo `"chance"`                                                                                                                                                               |
| `entities`             | Lista   | —           | **Obligatorio.** `["all"]` = todos los mobs (vanilla y de mods); IDs exactos (`"minecraft:zombie"`); tags de entidad (`"#minecraft:undead"`); comodines de mod (`"mekanism:*"`). Omitido = drop desactivado (con aviso en el log) |

> 💥 **`requires_player_kill` cambió en la 1.6.0.** Antes valía `true` por
> defecto. Decláralo explícitamente si llevas una economía de ítems — el parser
> avisa por cada ítem que lo deje sin declarar.

**Modos de saqueo.** Vanilla usa dos mecanismos separados y nunca ambos a la
vez, así que eliges uno:

| Modo       | Efecto                                                                                                     | Equivalente en vanilla         |
|------------|------------------------------------------------------------------------------------------------------------|--------------------------------|
| `"count"`  | Suma 0..nivel a la cantidad tirada. Se aplica antes del descarte por cero y **no** se recorta contra `max` | Carne podrida, cuerda, pólvora |
| `"chance"` | Sube la probabilidad de drop, dejando la cantidad intacta                                                  | Cabezas de esqueleto wither    |
| `"none"`   | El Saqueo no hace nada                                                                                     | —                              |

El Saqueo se lee de la mano principal del matador, así que un mob al que
dañaste, pero no remataste no da bonus.

> Con el modo `"chance"` y `max: 0` nunca puede caer nada: subir la
> probabilidad sigue llevando a una tirada de cero. Usa `"count"` si quieres
> que el Saqueo cree ítems desde una base de cero.

`min` puede ser `0`, igual que los drops de vanilla como la carne podrida
(0-2). Recuerda que se compone con `chance`: un 25% de probabilidad de 0-2
suelta algo aproximadamente el 17% de las veces.

Los jugadores, armor stands, barcos y vagonetas nunca sueltan ítems. Los cambios aplican en vivo con `/customgear reload` — puedes ajustar la economía de tu servidor sin reiniciar.

Los ítems con `mob_drops` muestran una sección **"Lo sueltan:"** en su tooltip, con los mobs de origen y la probabilidad de drop.
