# Damage Types Reference

All damage type IDs and damage type tags available in Minecraft 1.21.1, for use
in the `damage_resistances` field and in the `damage` key of
`conditional_resistances`.

- **With `#`** = a tag (a group of damage types), e.g. `"#minecraft:is_projectile"`
- **Without `#`** = one exact damage type, e.g. `"minecraft:arrow"`

```json
{
  "damage_resistances": {
    "#minecraft:is_projectile": 0.125, 
    "minecraft:lava": 0.20, 
    "iceandfire:dragon_fire": 0.225
  }
}
```

Values are **per equipped piece**: `0.125` on a four-piece set is 50% when the
full set is worn. See the **Damage Resistances** section of the README for how
the three resistance layers interact.

---

## Recommended tags

These are the broad buckets most armor designs want. Anything not listed here is
usually too narrow or too situational to build a set around.

| Tag                           | Covers                                                                                                             |
|-------------------------------|--------------------------------------------------------------------------------------------------------------------|
| `#minecraft:is_projectile`    | `arrow`, `trident`, `mob_projectile`, `fireball`, `unattributed_fireball`, `wither_skull`, `thrown`, `wind_charge` |
| `#minecraft:is_fire`          | `in_fire`, `campfire`, `on_fire`, `lava`, `hot_floor`, `fireball`, `unattributed_fireball`                         |
| `#minecraft:is_explosion`     | `explosion`, `player_explosion`, `fireworks`, `bad_respawn_point`                                                  |
| `#minecraft:is_fall`          | `fall`, `stalagmite`                                                                                               |
| `#minecraft:is_freezing`      | `freeze`                                                                                                           |
| `#minecraft:is_drowning`      | `drown`                                                                                                            |
| `#minecraft:is_lightning`     | `lightning_bolt`                                                                                                   |
| `#minecraft:is_player_attack` | `player_attack`                                                                                                    |

> Note the overlap: `fireball` belongs to **both** `is_projectile` and `is_fire`.
> If you declare both tags, they are two entries at the same level and their
> values add together for that one damage type.

---

## All damage types

### Combat

| ID                                | When it happens                                           |
|-----------------------------------|-----------------------------------------------------------|
| `minecraft:player_attack`         | Melee hit from a player                                   |
| `minecraft:mob_attack`            | Melee hit from a mob                                      |
| `minecraft:mob_attack_no_aggro`   | Melee hit that doesn't provoke retaliation                |
| `minecraft:arrow`                 | Arrow (bow, crossbow, dispenser)                          |
| `minecraft:trident`               | Thrown trident                                            |
| `minecraft:mob_projectile`        | Mob projectile that isn't an arrow (shulker bullet, etc.) |
| `minecraft:thrown`                | Snowball, egg, thrown potion impact                       |
| `minecraft:fireball`              | Fireball with a known shooter (ghast, blaze)              |
| `minecraft:unattributed_fireball` | Fireball with no known shooter                            |
| `minecraft:wither_skull`          | Wither skull projectile                                   |
| `minecraft:wind_charge`           | Wind charge                                               |
| `minecraft:spit`                  | Llama spit                                                |
| `minecraft:sting`                 | Bee sting                                                 |
| `minecraft:thorns`                | Thorns enchantment reflection                             |
| `minecraft:sonic_boom`            | Warden's sonic blast (ignores armor and enchantments)     |
| `minecraft:magic`                 | Direct magic (harming potion applied, evoker fangs)       |
| `minecraft:indirect_magic`        | Magic from a source entity (thrown harming potion)        |
| `minecraft:dragon_breath`         | Ender dragon breath cloud                                 |
| `minecraft:wither`                | Wither status effect                                      |

### Explosions

| ID                            | When it happens                                 |
|-------------------------------|-------------------------------------------------|
| `minecraft:explosion`         | Explosion not caused by a player (creeper, TNT) |
| `minecraft:player_explosion`  | Explosion caused by a player                    |
| `minecraft:fireworks`         | Firework rocket                                 |
| `minecraft:bad_respawn_point` | Bed / respawn anchor exploding                  |

### Fire and heat

| ID                    | When it happens                        |
|-----------------------|----------------------------------------|
| `minecraft:in_fire`   | Standing in fire                       |
| `minecraft:on_fire`   | Burning (the burning ticks themselves) |
| `minecraft:lava`      | Standing in lava                       |
| `minecraft:hot_floor` | Walking on a magma block               |
| `minecraft:campfire`  | Standing on a campfire                 |

### Environment

| ID                             | When it happens                |
|--------------------------------|--------------------------------|
| `minecraft:fall`               | Fall damage                    |
| `minecraft:stalagmite`         | Falling onto pointed dripstone |
| `minecraft:falling_block`      | Sand / gravel landing on you   |
| `minecraft:falling_anvil`      | Anvil landing on you           |
| `minecraft:falling_stalactite` | Dripstone tip falling on you   |
| `minecraft:cactus`             | Touching a cactus              |
| `minecraft:sweet_berry_bush`   | Walking through a berry bush   |
| `minecraft:freeze`             | Powder snow                    |
| `minecraft:lightning_bolt`     | Lightning strike               |
| `minecraft:drown`              | Drowning                       |
| `minecraft:dry_out`            | Aquatic mob out of water       |
| `minecraft:starve`             | Starvation (ignores effects)   |
| `minecraft:in_wall`            | Suffocating inside a block     |
| `minecraft:cramming`           | Too many entities in one block |
| `minecraft:fly_into_wall`      | Elytra kinetic impact          |
| `minecraft:outside_border`     | Outside the world border       |

### System — do not resist these

| ID                       | When it happens                                                |
|--------------------------|----------------------------------------------------------------|
| `minecraft:out_of_world` | The void, and `/kill`-style unblockable damage                 |
| `minecraft:generic_kill` | Forced kill                                                    |
| `minecraft:generic`      | Damage with no specific cause (used by some mods and commands) |

> ⚠️ Giving resistance to `out_of_world` or `generic_kill` is a bad idea: they
> exist specifically to be unavoidable. At high values a player can end up
> stuck alive in the void, unable to die and unable to get out.
> `generic` is a catch-all that some mods reuse for unrelated damage —
> resisting it can silently protect against far more than you intended.

---

## Vanilla behaviour tags

These tags exist for vanilla's own mechanics, not for balance grouping. They
work in `damage_resistances`, but they cut across categories in ways that are
rarely what you want (`#minecraft:bypasses_armor` includes fall damage, magic,
starving and the void all at once). Listed for completeness:

`#minecraft:always_hurts_ender_dragons`, `#minecraft:always_kills_armor_stands`,
`#minecraft:always_most_significant_fall`, `#minecraft:always_triggers_silverfish`,
`#minecraft:avoids_guardian_thorns`, `#minecraft:burn_from_stepping`,
`#minecraft:burns_armor_stands`, `#minecraft:bypasses_armor`,
`#minecraft:bypasses_effects`, `#minecraft:bypasses_enchantments`,
`#minecraft:bypasses_invulnerability`, `#minecraft:bypasses_resistance`,
`#minecraft:bypasses_shield`, `#minecraft:bypasses_wolf_armor`,
`#minecraft:can_break_armor_stand`, `#minecraft:damages_helmet`,
`#minecraft:ignites_armor_stands`, `#minecraft:no_anger`, `#minecraft:no_impact`,
`#minecraft:no_knockback`, `#minecraft:panic_causes`,
`#minecraft:panic_environmental_causes`, `#minecraft:witch_resistant_to`,
`#minecraft:wither_immune_to`

> `#minecraft:bypasses_resistance` and `#minecraft:bypasses_invulnerability`
> describe how vanilla's *Resistance effect* behaves. They do not disable this
> mod's resistances — those are applied by the mod itself, independently of
> armor points and vanilla effects.

---

## Modded damage types

Any mod can register its own damage types, and they work here exactly like
vanilla ones — just use the mod's namespace:

```json
{
  "damage_resistances": {
    "iceandfire:dragon_fire":      0.225, 
    "iceandfire:dragon_ice":       0.225, 
    "iceandfire:dragon_lightning": 0.225
  }
}
```

**How to find a mod's damage type IDs** — in-game, type
`/damage @s 1 ` and press **Tab**. The autocomplete lists every damage type
registered in that instance, vanilla and modded. This is more reliable than a
wiki, since forks and configs vary.

The same command is the best way to test a resistance:

```
/damage @s 10 minecraft:arrow by @e[type=skeleton,limit=1,sort=nearest]
```

If a mod deals damage through a vanilla type instead of registering its own,
resist the vanilla type. If a mod's damage type isn't loaded (the mod is
absent), the entry is simply inert — it costs nothing to leave declared.

---

## Grouping several damage types

There is no way to declare your own damage type tag from this mod yet. To group
IDs under a single name (for example, all three Ice and Fire dragon breaths under
`#customgear:dragon_breath`), write a small datapack by hand at
`world/datapacks/<name>/data/customgear/tags/damage_type/dragon_breath.json`:

```json
{
  "replace": false,
  "values": [
    "iceandfire:dragon_fire",
    "iceandfire:dragon_ice",
    "iceandfire:dragon_lightning"
  ]
}
```

Note the **singular** `tags/damage_type/` path — 1.21 changed it from the older
plural form. Once loaded, `"#customgear:dragon_breath"` works in any armor JSON.
