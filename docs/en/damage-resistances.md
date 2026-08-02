# Damage Resistances

Armor can reduce incoming damage through three layers, available at a set level 
and inside `pieces.<piece>`:

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
    "player:SomeName": 1.0
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

| Field                     | Type | Description                                                                                                 |
|---------------------------|------|-------------------------------------------------------------------------------------------------------------|
| `damage_resistances`      | Map  | Damage type → reduction. Exact IDs, tags (`#`), and modded types. See [Damage types](damage-types.md)       |
| `attacker_resistances`    | Map  | Attacker → reduction. Exact entity IDs, entity tags (`#`), mod wildcards (`mod:*`), players (`player:Name`) |
| `conditional_resistances` | List | Rules with `attacker`, `damage` and `amount` — applies only when both match                                 |
| `show_player_resistances` | Bool | `false` by default: `player:` entries never appear in the tooltip                                           |
| `inherit_set_resistances` | Bool | Inside a piece: `false` makes it ignore every set-level resistance. Default `true`                          |

**Values are per equipped piece.** `0.125` on all four pieces is 50% with the
full set worn, 25% with two pieces. Design around the full set, then divide.

**Set level and per piece.** Entries declared inside `pieces.<piece>` **merge**
with the set-level ones, winning only on the keys they declare. Scope merges;
layers are replaced. The two rules are independent.

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

The boots resist fall at `0.15` **and still resist fire at `0.10`**; the other
three pieces keep `0.10` for both. Total fall reduction with the full set: 45%.

To take a piece out of the set-level resistances entirely, set
`inherit_set_resistances` to `false` inside it. That piece then uses only what
it declares — and one that declares nothing contributes no resistance at all:

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

Conditional rules merge by their `attacker` + `damage` pair: a piece rule with
the same pair replaces the set rule, and any other one is added alongside it.

**Specificity, not accumulation.** Each equipped piece resolves on its own: the
three layers are evaluated in order — `conditional` → `attacker` → `damage` —
and **the first layer with any match replaces the more general ones for that
piece, even when its value is lower.** Within a layer, matching entries add
together; the pieces then add together.

Because pieces resolve independently, a conditional rule on one piece does not
silence the others: they keep contributing through whichever layer matched for
them.

With a full set using the values above:

| Incoming attack             | Layer that wins    | Reduction |
|-----------------------------|--------------------|-----------|
| Skeleton arrow              | conditional (0.10) | 40%       |
| Skeleton melee              | conditional (0.15) | 60%       |
| Pillager or player arrow    | damage (0.125)     | 50%       |
| Zombie melee                | none               | 0%        |

Note the third and first rows: a **skeleton's** arrow is reduced *less* (40%)
than anyone else's arrow (50%), because the specific rule replaced the general
one. If you want the specific case to be stronger, give it a higher value.

Reductions are clamped to 1.0 (100%). **There is no balance ceiling** — total
immunity is a valid design choice, and the mod will not second-guess it.

Projectiles are attributed to their owner: an arrow counts as
`minecraft:skeleton`, not as the arrow entity. This applies to
`attacker_resistances` and to the `attacker` half of conditional rules.

**Hidden player entries.** `player:Name` matches one exact Minecraft username
(not a Discord name, and it is case-sensitive). These entries are excluded from
the tooltip so that surprise or event armor doesn't announce itself — set
`show_player_resistances` to `true` to display them.

Everything here applies live with `/customgear reload`.
