# Migration

Breaking changes only — things that make a JSON file that worked in one version
behave differently or stop loading in the next. Everything additive lives in the
[CHANGELOG](../../CHANGELOG.md).

## From Ultimate Custom Gear

Custom Gear continues Ultimate Custom Gear 1.8.0. **Content files work
unchanged**, and items keep their `customgear:` ids, so worlds load with every
item in place.

1. Remove Ultimate Custom Gear and install Custom Gear. Never both at once: they
   register the same items.
2. Start the game once. If `.minecraft/ultimatecustomgear/` exists and
   `.minecraft/customgear/` does not, its contents are copied over — loose
   files, zips and all. The old folder is left as it was; delete it once you are
   happy.
3. The config moved to `config/customgear-common.toml` and
   `config/customgear-client.toml`. The settings are the same, and the old files
   are not read: set `content_handshake_mode` again if you had changed it.

### Fields that changed

| Before          | Now               | Notes                                                                                   |
|-----------------|-------------------|-----------------------------------------------------------------------------------------|
| `curios_slots`  | `accessory_slots` | The old name still works. Now also covers Trinkets on Fabric (see [Containers](containers.md)) |
| `lightLevel` (fluids)  | `light_level`  | The documented name was never read; now both are                                   |
| `bucketNames` (fluids) | `bucket_names` | Same                                                                               |

The last two are fixes rather than changes: a fluid that declared `light_level`
or `bucket_names`, as the docs said, used to get no light and default bucket
names. It now gets what it asked for.

## Ultimate Custom Gear history

> These sections cover the versions of Ultimate Custom Gear and are kept for
> packs written against them. They are incomplete: 1.7.0, 1.6.0, and the
> network protocol change in 1.3.0.

### 1.7.0

**Nothing to change.** Packs written for 1.6.0 load unchanged.

#### `texture.mode` no longer does anything

```diff
  "texture": {
-   "mode": "reference",
    "refs": {
      "sword": "minecraft:item/netherite_sword"
    }
  }
```

What a value is now comes from the value itself: a `:` makes it another mod's
asset, a file extension makes it a file in your folder. A pack that still
declares `mode` works exactly as before — the field is read and ignored — so
deleting it is cleanup, not a fix.

The one thing that genuinely changed: a pack declaring `"mode": "default"` **and**
values in `refs` used to ignore those values and show the placeholder textures.
Those values now apply. Nobody writes both on purpose, but if you did, remove the
`refs` block rather than the `mode` line.

See [Textures & Models](textures-and-models.md) for the full rule.

#### Network protocol

1.7.0 clients cannot join 1.6.x servers, and vice versa: 1.7.0 adds network
channels the older version doesn't have, and the connection is refused before
joining. Both sides must update together. Nothing to change in your JSON files.

### 1.6.0

#### `cooking_time` is now in seconds

Applies to `smelting`, `blasting`, `smoking` and `campfire_cooking`.

```diff
- "cooking_time": 200
+ "cooking_time": 10
```

Divide your old value by 20. A file left untouched cooks 20× slower than
intended — `200` used to be the vanilla furnace default and is now three and a
half minutes.

Decimals are accepted, so `"cooking_time": 7.5` is valid.

#### `requires_player_kill` now defaults to `false`

```diff
  "mob_drops": {
    "entities": ["all"],
    "chance": 0.10,
+   "requires_player_kill": true
  }
```

It used to default to `true`. If your drops back are a server economy, declare it
explicitly — otherwise a fall-damage or lava farm prints currency with no player
involved. The parser warns when the field is left undeclared.

The check also changed meaning: it now asks whether a player damaged the mob
recently, not whether a player landed the killing blow. A mob finished off by
its own fall after a player hit it still counts.

#### `affected_by_looting` replaced by `looting_mode`

```diff
- "affected_by_looting": true
+ "looting_mode": "count"
```

```diff
- "affected_by_looting": false
+ "looting_mode": "none"
```

The new third value, `"chance"`, raises the drop probability instead of the
amount — vanilla uses one or the other, never both on the same drop. Default is
`"count"`.

### 1.3.0

#### Network protocol

1.3.0 clients cannot join pre-1.3.0 servers, and vice versa. Both sides must
update together. Nothing to change in your JSON files.
