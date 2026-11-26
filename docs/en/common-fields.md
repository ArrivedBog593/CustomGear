# Common Fields

| Field            | Type    | Description                                                                                                                                                                                               |
|------------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`             | String  | Unique identifier. Lowercase letters, numbers, and underscores only. 2–64 characters.                                                                                                                     |
| `type`           | String  | Item type (see Supported Types table)                                                                                                                                                                     |
| `names`          | Map     | Full item name per language (individual items only — sets use `piece_names`, `tool_names`, or `weapon_names`)                                                                                             |
| `enchantable`    | Boolean | Whether the item can be enchanted                                                                                                                                                                         |
| `enchantability` | Int     | Higher = better enchantments. Iron = 9, Gold = 25, Diamond = 10                                                                                                                                           |
| `tags`           | List    | Tags this content belongs to, WITHOUT `#` (e.g. `["c:ingots", "c:ingots/ruby"]`). Lets recipes that accept `#that_tag` use it. See [Tags](tags.md). Works on items, food, blocks and fluids (not on sets) |
| `fire_resistant` | Boolean | The dropped item survives fire and lava, like netherite (on fluids: the filled bucket). Does not protect the wearer from fire. Requires restart                                                           |

## Effect Object

| Field       | Type   | Description                                                             |
|-------------|--------|-------------------------------------------------------------------------|
| `effect`    | String | Effect ID in `namespace:effect_name` format (e.g. `minecraft:strength`) |
| `amplifier` | Int    | Effect level minus 1. `0` = Level I, `1` = Level II, etc.               |

## Item ID Reference

| Type               | ID pattern                     | Example                       |
|--------------------|--------------------------------|-------------------------------|
| Armor set pieces   | `customgear:<set_id>_<piece>`  | `customgear:my_armor_helmet`  |
| Tool set tools     | `customgear:<set_id>_<tool>`   | `customgear:my_tools_pickaxe` |
| Weapon set weapons | `customgear:<set_id>_<weapon>` | `customgear:my_weapons_sword` |
| Individual items   | `customgear:<id>`              | `customgear:my_sword`         |
| Blocks             | `customgear:<id>`              | `customgear:my_ore`           |
| Fluid buckets      | `customgear:<id>_bucket`       | `customgear:my_fluid_bucket`  |
