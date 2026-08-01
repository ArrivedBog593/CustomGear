# Mob Drops

Items and food can drop from mobs on death via the `mob_drops` object:

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

| Field                  | Type    | Default   | Description                                                                                                                                                                                                   |
|------------------------|---------|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `chance`               | Double  | 0.05      | Drop probability per kill (0.0 representing 0% chance and 1.0 representing 100% chance)                                                                                                                       |
| `min` / `max`          | Int     | 1 / 1     | Dropped count range                                                                                                                                                                                           |
| `requires_player_kill` | Boolean | `false`   | Requires recent player damage — not the killing blow. Set to `true` for an economy, or a fall damage farm can print currency                                                                                  |
| `looting_mode`         | String  | `"count"` | How Looting affects this drop: `"count"`, `"chance"` or `"none"`. See below                                                                                                                                   |
| `looting_chance_bonus` | Double  | 0.01      | Probability added per Looting level, in `"chance"` mode only                                                                                                                                                  |
| `entities`             | List    | —         | **Required.** `["all"]` = every mob (vanilla and modded); exact IDs (`"minecraft:zombie"`); entity tags (`"#minecraft:undead"`); mod wildcards (`"mekanism:*"`). Omitted = drop disabled (with a log warning) |

> 💥 **`requires_player_kill` changed in 1.6.0.** It used to default to `true`.
> Declare it explicitly if you run an item economy — the parser warns on every
> item that leaves it undeclared.

**Looting modes.** Vanilla uses two separate mechanisms and never both at
once, so you pick one:

| Mode       | Effect                                                                                           | Vanilla equivalent              |
|------------|--------------------------------------------------------------------------------------------------|---------------------------------|
| `"count"`  | Adds 0..level to the rolled amount. Applied before the empty check, and **not** clamped to `max` | Rotten flesh, string, gunpowder |
| `"chance"` | Raises the drop probability, leaving the amount alone                                            | Wither skeleton skulls          |
| `"none"`   | Looting does nothing                                                                             | —                               |

Looting is read from the killer's main hand, so a mob you damaged but did not
finish gets no bonus.

> With `"chance"` mode and `max: 0` nothing can ever drop: raising the
> probability still leads to a roll of zero. Use `"count"` if you want Looting
> to create items from a zero base.

`min` may be `0`, matching vanilla drops like rotten flesh (0–2). Remember it
compounds with `chance`: a 25% chance of 0–2 drops something roughly 17% of the
time.

Players, armor stands, boats, and minecarts never drop items. Changes apply live with `/customgear reload` — you can tune your server's economy without restarting.

Items with `mob_drops` show a **"Dropped by"** section in their tooltip, with the source mobs and the drop chance.
