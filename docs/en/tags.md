# Tags

Two sides of the same system:
- **Consuming** a tag in a recipe uses the `#` prefix: `"#minecraft:planks"` accepts any item in that tag (see Recipes → Tags as ingredients).
- **Belonging** to a tag uses the `tags` field on your content, WITHOUT `#`, so other mods' recipes — and your own — accept your item/block/fluid.

```json
{
  "id": "ruby_ingot",
  "type": "item",
  "tags": ["c:ingots", "c:ingots/ruby"]
}
```

A tag is just the sum of everything that declares it — you can use vanilla tags, convention (`c:`) tags shared across mods, or invent your own (`customgear:magic_gems`). Your tags merge with existing ones of the same name.

**Blocks** are added to both the block and item tag registries (so recipes, which consume the item form, accept your block). **Fluids** tag the fluid and their bucket item. **Gear** (armor, tools, and weapons) does not use the `tags` field, but receives the vanilla and convention tags it needs automatically — see [Automatic gear tags](#automatic-gear-tags).

## Automatic gear tags

Armor, tools, and weapons receive the tags that make them behave like real gear.
You never declare them:

| Content                | Tags received                                                                                    |
|------------------------|--------------------------------------------------------------------------------------------------|
| Armor piece            | Its slot tag (`#minecraft:chest_armor`…), plus `c:armors`                                        |
| Sword                  | `#minecraft:swords`, `breaks_decorated_pots`, `c:tools`, `c:tools/melee_weapon`                  |
| Pickaxe/axe/shovel/hoe | Its type tag (`#minecraft:pickaxes`…), `breaks_decorated_pots`, `c:tools`, `c:tools/mining_tool` |
| Bow / crossbow         | `c:tools`, `c:tools/ranged_weapon`, `c:tools/bow` or `c:tools/crossbow`                          |
| Shield                 | `c:tools`, `c:tools/shield`                                                                      |

When `enchantable` is `true`, the matching `#minecraft:enchantable/*` tags are
added too. That is what the enchanting table reads in 1.21 to decide what to
offer — with `enchantable: false` they are left out, so the item stays
genuinely unenchantable.

**Smithing trims** are opt-in with `"trimmable": true` on an `armor_set`. Off
by default because trims draw over the armor layers: with
`armor_layers: transparent` or a GeckoLib 3D model the trim applies but never
shows.

## Tag patches

The `tags` field says "my content belongs to tag X" — the value written is
always yours. A `tag_patch` flips it, so **foreign** content can be put into a
tag. That matters for anything you cannot register yourself: another mod's
damage types could not be referenced at all before.

```json
{
  "type": "tag_patch",
  "registry": "damage_type",
  "tag": "customgear:dragon_breath",
  "values": [
    "iceandfire:dragon_fire",
    "iceandfire:dragon_ice"
  ]
}
```

Which lets armor reference one tag instead of listing every ID:

```json
{
  "damage_resistances": { "#customgear:dragon_breath": 0.30 }
}
```

| Field      | Type     | Description                                                                             |
|------------|----------|-----------------------------------------------------------------------------------------|
| `registry` | String   | **Required.** `item`, `block`, `fluid`, `entity_type`, `damage_type`, `enchantment`…    |
| `tag`      | String   | **Required.** Full tag ID, no `#`                                                       |
| `values`   | String[] | Content IDs to add                                                                      |
| `remove`   | String[] | Content IDs to take out, even when another pack put them there                          |
| `comment`  | String   | Never parsed. A patch has no name, so without this the file gives no clue why it exists |

`values` and `remove` are both optional, but a patch with neither is rejected.
There is no `id`: a patch registers nothing and is identified by
`registry` + `tag`, so two patches naming the same pair merge.

Entries are always written as optional, so an uninstalled mod is ignored
instead of dropping the whole tag. The cost is that a typo fails exactly like
an absent mod — so a warning is logged when the namespace belongs to a mod
that *is* loaded. Datapack registries like `damage_type` cannot be checked
that way, which is unfortunately the most common case.

**Removals** exist because some tags are filled by inheritance rather than by
entries. Vanilla's `trimmable_armor` is the union of the four slot tags, so
armor lands in it just by being armor — not adding it changes nothing; it has
to be removed:

```json
{
  "type": "tag_patch",
  "registry": "item",
  "tag": "minecraft:trimmable_armor",
  "remove": ["othermod:some_chestplate"]
}
```

> ⚠️ Removing from a `minecraft:` or `c:` tag affects **every mod that reads
> it**. Taking an item out of `#minecraft:planks` breaks recipes across the
> pack, and whoever sees the breakage has no reason to connect it to a patch
> file. A warning is logged; heed it.

When the same ID is both added and removed, **the removal wins** — adding can
come from a broad rule, removing is always deliberate. A warning names the ID.

> Tag changes need `/reload` after `/customgear reload`. The tag manager only
> rebinds on a datapack reload.

## Common tags

Vanilla tags (`minecraft:`) — make your content count as a vanilla material:

| Tag                                                                                                   | Use                                     |
|-------------------------------------------------------------------------------------------------------|-----------------------------------------|
| `minecraft:planks`                                                                                    | Counts as planks in vanilla recipes     |
| `minecraft:logs`                                                                                      | Logs                                    |
| `minecraft:wool`                                                                                      | Wool                                    |
| `minecraft:leaves`                                                                                    | Leaves (fast to mine with sword/shears) |
| `minecraft:swords` / `minecraft:pickaxes` / `minecraft:axes` / `minecraft:shovels` / `minecraft:hoes` | Tool of that type                       |
| `minecraft:coals`                                                                                     | Coal-type fuels                         |

Convention tags (`c:`) — the cross-mod interoperability standard (most useful):

| Tag                                                | Use                                              |
|----------------------------------------------------|--------------------------------------------------|
| `c:ingots` + `c:ingots/<material>`                 | Ingots                                           |
| `c:gems` + `c:gems/<material>`                     | Gems                                             |
| `c:ores` + `c:ores/<material>`                     | Ores                                             |
| `c:raw_materials` + `c:raw_materials/<material>`   | Raw materials                                    |
| `c:nuggets` + `c:nuggets/<material>`               | Nuggets                                          |
| `c:dusts` + `c:dusts/<material>`                   | Dusts                                            |
| `c:storage_blocks` + `c:storage_blocks/<material>` | Storage blocks (block of X)                      |
| `c:tools` + `c:tools/<type>`                       | Tools by type                                    |
| `c:armors` + `c:armors/<slot>`                     | Armor by slot                                    |
| `c:foods` + `c:foods/<type>`                       | Food (`c:foods/fruits`, `c:foods/vegetables`...) |
| `c:dyes` + `c:dyes/<color>`                        | Dyes                                             |
| `c:seeds` / `c:crops`                              | Seeds and crops                                  |

Tip: use the general tag AND the material subtag (`c:ingots` and `c:ingots/ruby`) for maximum compatibility — the first for "any ingot", the second for "ruby ingot specifically".
