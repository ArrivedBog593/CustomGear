# Tipos de daño

Todos los ID y tags de tipo de daño disponibles en Minecraft 26.1.2, para usar
en el campo `damage_resistances` y en la clave `damage` de
`conditional_resistances`.

- **Con `#`** = un tag (un grupo de tipos de daño), p. ej. `"#minecraft:is_projectile"`
- **Sin `#`** = un tipo de daño exacto, p. ej. `"minecraft:arrow"`

```json
{
  "damage_resistances": {
    "#minecraft:is_projectile": 0.125, 
    "minecraft:lava": 0.20, 
    "iceandfire:dragon_fire": 0.225
  }
}
```

Los valores son **por pieza equipada**: `0.125` en un set de cuatro piezas es 50%
con el set completo puesto. Ver [Resistencias de daño](damage-resistances.md)
para saber cómo interactúan las tres capas.

---

## Tags recomendados

Estos son los grupos amplios que la mayoría de los diseños de armadura quieren.
Lo que no está aquí suele ser demasiado estrecho o demasiado situacional como
para construir un set alrededor.

| Tag                           | Cubre                                                                                                              |
|-------------------------------|--------------------------------------------------------------------------------------------------------------------|
| `#minecraft:is_projectile`    | `arrow`, `trident`, `mob_projectile`, `fireball`, `unattributed_fireball`, `wither_skull`, `thrown`, `wind_charge` |
| `#minecraft:is_fire`          | `in_fire`, `campfire`, `on_fire`, `lava`, `hot_floor`, `fireball`, `unattributed_fireball`                         |
| `#minecraft:is_explosion`     | `explosion`, `player_explosion`, `fireworks`, `bad_respawn_point`                                                  |
| `#minecraft:is_fall`          | `fall`, `stalagmite`                                                                                               |
| `#minecraft:is_freezing`      | `freeze`                                                                                                           |
| `#minecraft:is_drowning`      | `drown`                                                                                                            |
| `#minecraft:is_lightning`     | `lightning_bolt`                                                                                                   |
| `#minecraft:is_player_attack` | `player_attack`                                                                                                    |

> Fíjate en el solapamiento: `fireball` pertenece a **ambos**, `is_projectile` e
> `is_fire`. Si declaras los dos tags, son dos entradas del mismo nivel y sus
> valores se suman para ese tipo de daño.

---

## Todos los tipos de daño

### Combate

| ID                                | Cuándo ocurre                                                  |
|-----------------------------------|----------------------------------------------------------------|
| `minecraft:player_attack`         | Golpe cuerpo a cuerpo de un jugador                            |
| `minecraft:mob_attack`            | Golpe cuerpo a cuerpo de un mob                                |
| `minecraft:mob_attack_no_aggro`   | Golpe cuerpo a cuerpo que no provoca represalia                |
| `minecraft:arrow`                 | Flecha (arco, ballesta, dispensador)                           |
| `minecraft:trident`               | Tridente lanzado                                               |
| `minecraft:mob_projectile`        | Proyectil de mob que no es flecha (bala de shulker, etc.)      |
| `minecraft:thrown`                | Bola de nieve, huevo, impacto de poción arrojadiza             |
| `minecraft:fireball`              | Bola de fuego con lanzador conocido (ghast, blaze)             |
| `minecraft:unattributed_fireball` | Bola de fuego sin lanzador conocido                            |
| `minecraft:wither_skull`          | Proyectil de calavera de wither                                |
| `minecraft:wind_charge`           | Carga de viento                                                |
| `minecraft:spit`                  | Escupitajo de llama                                            |
| `minecraft:sting`                 | Picadura de abeja                                              |
| `minecraft:thorns`                | Reflejo del encantamiento Espinas                              |
| `minecraft:sonic_boom`            | Estallido sónico del warden (ignora armadura y encantamientos) |
| `minecraft:magic`                 | Magia directa (poción de daño aplicada, colmillos de evocador) |
| `minecraft:indirect_magic`        | Magia desde una entidad fuente (poción de daño arrojada)       |
| `minecraft:dragon_breath`         | Nube de aliento del dragón del End                             |
| `minecraft:wither`                | Efecto de estado Wither                                        |

### Explosiones

| ID                            | Cuándo ocurre                                        |
|-------------------------------|------------------------------------------------------|
| `minecraft:explosion`         | Explosión no causada por un jugador (creeper, TNT)   |
| `minecraft:player_explosion`  | Explosión causada por un jugador                     |
| `minecraft:fireworks`         | Cohete de fuegos artificiales                        |
| `minecraft:bad_respawn_point` | Cama / ancla de reaparición explotando               |

### Fuego y calor

| ID                    | Cuándo ocurre                            |
|-----------------------|------------------------------------------|
| `minecraft:in_fire`   | Estar parado en fuego                    |
| `minecraft:on_fire`   | Estar quemándose (los ticks de quemado)  |
| `minecraft:lava`      | Estar parado en lava                     |
| `minecraft:hot_floor` | Caminar sobre un bloque de magma         |
| `minecraft:campfire`  | Estar parado en una fogata               |

### Entorno

| ID                             | Cuándo ocurre                          |
|--------------------------------|----------------------------------------|
| `minecraft:fall`               | Daño por caída                         |
| `minecraft:stalagmite`         | Caer sobre goteo puntiagudo            |
| `minecraft:falling_block`      | Arena / grava cayéndote encima         |
| `minecraft:falling_anvil`      | Yunque cayéndote encima                |
| `minecraft:falling_stalactite` | Punta de goteo cayéndote encima        |
| `minecraft:cactus`             | Tocar un cactus                        |
| `minecraft:sweet_berry_bush`   | Caminar entre un arbusto de bayas      |
| `minecraft:freeze`             | Nieve polvo                            |
| `minecraft:lightning_bolt`     | Impacto de rayo                        |
| `minecraft:drown`              | Ahogarse                               |
| `minecraft:dry_out`            | Mob acuático fuera del agua            |
| `minecraft:starve`             | Inanición (ignora efectos)             |
| `minecraft:in_wall`            | Asfixiarse dentro de un bloque         |
| `minecraft:cramming`           | Demasiadas entidades en un bloque      |
| `minecraft:fly_into_wall`      | Impacto cinético con élitros           |
| `minecraft:outside_border`     | Fuera del borde del mundo              |

### Sistema — no resistas estos

| ID                       | Cuándo ocurre                                                       |
|--------------------------|---------------------------------------------------------------------|
| `minecraft:out_of_world` | El vacío, y el daño imbloqueable estilo `/kill`                     |
| `minecraft:generic_kill` | Muerte forzada                                                      |
| `minecraft:generic`      | Daño sin causa específica (lo usan algunos mods y comandos)         |

> ⚠️ Dar resistencia a `out_of_world` o `generic_kill` es mala idea: existen
> precisamente para ser inevitables. Con valores altos un jugador puede acabar
> atrapado vivo en el vacío, sin poder morir ni salir.
> `generic` es un cajón de sastre que algunos mods reutilizan para daños que no
> tienen nada que ver — resistirlo puede protegerte en silencio de mucho más de
> lo que pretendías.

---

## Tags de comportamiento de vanilla

Estos tags existen para las mecánicas propias de vanilla, no para agrupar por
balance. Funcionan en `damage_resistances`, pero cruzan categorías de formas que
rara vez son lo que quieres (`#minecraft:bypasses_armor` incluye daño por caída,
magia, inanición y el vacío, todo junto). Se listan por completitud:

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

> `#minecraft:bypasses_resistance` y `#minecraft:bypasses_invulnerability`
> describen cómo se comporta el *efecto Resistencia* de vanilla. No desactivan
> las resistencias de este mod — esas las aplica el mod mismo, independientemente
> de los puntos de armadura y de los efectos de vanilla.

---

## Tipos de daño de otros mods

Cualquier mod puede registrar sus propios tipos de daño, y aquí funcionan
exactamente como los de vanilla — solo usa el namespace del mod:

```json
{
  "damage_resistances": {
    "iceandfire:dragon_fire":      0.225, 
    "iceandfire:dragon_ice":       0.225, 
    "iceandfire:dragon_lightning": 0.225
  }
}
```

**Cómo encontrar los ID de tipo de daño de un mod** — dentro del juego, escribe
`/damage @s 1 ` y presiona **Tab**. El autocompletado lista todos los tipos de
daño registrados en esa instancia, de vanilla y de mods. Es más confiable que una
wiki, porque los forks y las configuraciones varían.

El mismo comando es la mejor forma de probar una resistencia:

```
/damage @s 10 minecraft:arrow by @e[type=skeleton,limit=1,sort=nearest]
```

Si un mod hace daño a través de un tipo de vanilla en vez de registrar el suyo,
resiste el tipo de vanilla. Si el tipo de daño de un mod no está cargado (el mod
no está), la entrada simplemente queda inerte — no cuesta nada dejarla declarada.

---

## Agrupar varios tipos de daño

Todavía no hay forma de declarar tu propio tag de tipo de daño desde este mod.
Para agrupar ID bajo un solo nombre (por ejemplo, los tres alientos de dragón de
Ice and Fire bajo `#customgear:dragon_breath`), escribe a mano un datapack
pequeño en
`world/datapacks/<nombre>/data/customgear/tags/damage_type/dragon_breath.json`:

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

Fíjate en la ruta `tags/damage_type/` en **singular** — 1.21 la cambió desde la
forma plural anterior. Una vez cargado, `"#customgear:dragon_breath"` funciona en
cualquier JSON de armadura.
