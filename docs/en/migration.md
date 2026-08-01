# Migration

Breaking changes only — things that make a JSON file that worked in one version
behave differently or stop loading in the next. Everything additive lives in the
[CHANGELOG](../../CHANGELOG.md).

> This page is incomplete. It currently covers 1.6.0, and the network protocol
> change in 1.3.0. Earlier-breaking changes need to be pulled out of the
> CHANGELOG.

## 1.6.0

### `cooking_time` is now in seconds

Applies to `smelting`, `blasting`, `smoking` and `campfire_cooking`.

```diff
- "cooking_time": 200
+ "cooking_time": 10
```

Divide your old value by 20. A file left untouched cooks 20× slower than
intended — `200` used to be the vanilla furnace default and is now three and a
half minutes.

Decimals are accepted, so `"cooking_time": 7.5` is valid.

### `requires_player_kill` now defaults to `false`

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

### `affected_by_looting` replaced by `looting_mode`

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

## 1.3.0

### Network protocol

1.3.0 clients cannot join pre-1.3.0 servers, and vice versa. Both sides must
update together. Nothing to change in your JSON files.
