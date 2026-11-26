# Resistencias de daño

Las armaduras pueden reducir el daño entrante mediante tres capas, disponibles a
nivel de conjunto y dentro de `pieces.<pieza>`:

```json
{
  "damage_resistances": {
    "#minecraft:is_projectile": 0.125,
    "minecraft:lava": 0.20,
    "iceandfire:dragon_fire": 0.225
  },
  "attacker_resistances": {
    "minecraft:skeleton": 0.05,
    "#minecraft:undead": 0.10,
    "mekanism:*": 0.05,
    "player:AlgunNombre": 1.0
  },
  "conditional_resistances": [
    {
      "attacker": "minecraft:skeleton",
      "damage": "#minecraft:is_projectile",
      "amount": 0.10
    },
    {
      "attacker": "minecraft:skeleton",
      "damage": "minecraft:mob_attack",
      "amount": 0.15
    }
  ],
  "show_player_resistances": false
}
```

| Campo                     | Tipo | Descripción                                                                                                                 |
|---------------------------|------|-----------------------------------------------------------------------------------------------------------------------------|
| `damage_resistances`      | Map  | Tipo de daño → reducción. ID exactos, tags (`#`) y tipos de mods. Ver [Tipos de daño](damage-types.md)                      |
| `attacker_resistances`    | Map  | Atacante → reducción. ID de entidad exactos, tags de entidad (`#`), comodines de mod (`mod:*`), jugadores (`player:Nombre`) |
| `conditional_resistances` | List | Reglas con `attacker`, `damage` y `amount` — solo aplican cuando ambos coinciden                                            |
| `show_player_resistances` | Bool | `false` por defecto: las entradas `player:` nunca aparecen en el tooltip                                                    |
| `inherit_set_resistances` | Bool | Dentro de una pieza: `false` hace que ignore todas las resistencias del conjunto. Por defecto `true`                        |

**Los valores son por pieza equipada.** `0.125` en las cuatro piezas es 50% con el set 
completo puesto, y 25% con dos piezas. Diseña pensando en el set completo y luego divide.

**Nivel de conjunto y por pieza.** Las entradas declaradas dentro de `pieces.<pieza>` 
**se fusionan** con las del conjunto, ganando solo en las claves que declaran. El alcance
fusiona; las capas reemplazan. Son dos reglas independientes.

```json
{
  "damage_resistances": {
    "#minecraft:is_fall": 0.10,
    "#minecraft:is_fire": 0.10
  },
  "pieces": {
    "boots": {
      "durability": 481,
      "defense": 3,
      "damage_resistances": {
        "#minecraft:is_fall": 0.15
      }
    }
  }
}
```

Las botas resisten caída al `0.15` **y siguen resistiendo fuego al `0.10`**; las otras tres
piezas conservan `0.10` en ambas. Reducción total de caída con el conjunto completo: 45%.

Para sacar una pieza por completo de las resistencias del conjunto, pon
`inherit_set_resistances` en `false` dentro de ella. Esa pieza usará solo lo que
declare — y una que no declare nada no aporta ninguna resistencia:

```json
{
  "pieces": {
    "chestplate": {
      "durability": 592,
      "defense": 8,
      "inherit_set_resistances": false,
      "damage_resistances": {
        "#minecraft:is_projectile": 0.15
      }
    },
    "helmet": {
      "durability": 407,
      "defense": 3,
      "inherit_set_resistances": false
    }
  }
}
```

Las reglas condicionales se fusionan por su par `attacker` + `damage`: una regla
de pieza con el mismo par reemplaza a la del conjunto, y cualquier otra se suma.

**Especificidad, no acumulación.** Cada pieza equipada se resuelve por su
cuenta: las tres capas se evalúan en orden — `conditional` → `attacker` →
`damage` — y **la primera capa con alguna coincidencia reemplaza a las más
generales para esa pieza, aunque su valor sea menor.** Dentro de una capa, las
entradas que coinciden se suman; después se suman las piezas.

Como cada pieza se resuelve por separado, una regla condicional en una pieza no
silencia a las demás: las otras siguen aportando por la capa que les haya
coincidido.

Con un conjunto completo usando los valores de arriba:

| Ataque entrante              | Capa que gana      | Reducción |
|------------------------------|--------------------|-----------|
| Flecha de esqueleto          | conditional (0.10) | 40%       |
| Melee de esqueleto           | conditional (0.15) | 60%       |
| Flecha de pillager o jugador | damage (0.125)     | 50%       |
| Melee de zombi               | ninguna            | 0%        |

Fíjate en la primera y la tercera fila: la flecha de un **esqueleto** se reduce *menos* (40%)
que la flecha de cualquier otro (50%), porque la regla específica reemplazó a la general.
Si quieres que el caso específico sea más fuerte, dale un valor mayor.

Las reducciones se limitan a 1.0 (100%). **No hay techo de balance** — la
inmunidad total es una decisión de diseño válida y el mod no la va a cuestionar.

Los proyectiles se atribuyen a su dueño: una flecha cuenta como
`minecraft:skeleton`, no como la entidad flecha. Esto aplica a
`attacker_resistances` y a la mitad `attacker` de las reglas condicionales.

**Entradas de jugador ocultas.** `player:Nombre` coincide con un nombre de
usuario exacto de Minecraft (no el de Discord, y distingue mayúsculas). Estas
entradas se excluyen del tooltip para que una armadura sorpresa o de evento no
se delate — pon `show_player_resistances` en `true` para mostrarlas.

Todo lo de esta sección aplica en vivo con `/customgear reload`.
