# Recipes

UltimateCustomGear supports native crafting recipes defined directly in JSON. No external mods are required.

## Recipe types

| Type                 | Description                                        |
|----------------------|----------------------------------------------------|
| `shaped`             | Crafting table with a specific pattern             |
| `shapeless`          | Crafting table, ingredients in any order           |
| `smelting`           | Furnace                                            |
| `blasting`           | Blast furnace                                      |
| `smoking`            | Smoker                                             |
| `campfire_cooking`   | Campfire                                           |
| `stonecutting`       | Stonecutter                                        |
| `smithing_transform` | Smithing table (template + base + addition)        |
| `passthrough`        | Another mod's recipe type, copied verbatim         |

## Tags as ingredients

Any ingredient slot accepts a **tag** with the `#` prefix — the recipe will
accept any item in that tag:

```json
{
  "key": {
    "P": "#minecraft:planks",
    "I": "#c:ingots/iron",
    "S": "minecraft:stick"
  }
}
```

Works in every recipe type this mod builds itself. Inside `passthrough` you write tags the way the target mod expects them (usually `{"tag": "c:ingots/gold"}`), not with the `#` shorthand — that body is copied verbatim and never translated. Recipes with structural errors (uneven pattern rows, undefined pattern symbols, unused keys, malformed IDs) are skipped with a detailed message in the log naming the item and the exact problem.

## Individual item recipe

```json
{
  "recipe": {
    "type": "shaped",
    "pattern": [
      " G ",
      " G ",
      " S "
    ],
    "key": {
      "G": "mymod:my_gem",
      "S": "minecraft:stick"
    }
  }
}
```

For multiple recipes, use an array:

```json
{
  "recipe": [
    {
      "type": "shaped",
      ...
    },
    {
      "type": "smelting",
      "ingredient": "mymod:my_ore",
      "experience": 1.0,
      "cooking_time": 10
    }
  ]
}
```

## Set recipe (armor, tools, weapons)

```json
{
  "recipes": {
    "helmet": {
      "type": "shaped",
      "pattern": [
        "GGG",
        "G G",
        "   "
      ],
      "key": {
        "G": "mymod:my_gem"
      }
    },
    "chestplate": {
      "type": "shaped",
      "pattern": [
        "G G",
        "GGG",
        "GGG"
      ],
      "key": {
        "G": "mymod:my_gem"
      }
    },
    "leggings": {
      "type": "shaped",
      "pattern": [
        "GGG",
        "G G",
        "G G"
      ],
      "key": {
        "G": "mymod:my_gem"
      }
    },
    "boots": {
      "type": "shaped",
      "pattern": [
        "   ",
        "G G",
        "G G"
      ],
      "key": {
        "G": "mymod:my_gem"
      }
    }
  }
}
```

## Smithing table recipe

```json
{
  "recipe": {
    "type": "smithing_transform",
    "template": "minecraft:netherite_upgrade_smithing_template",
    "base": "mymod:my_diamond_sword",
    "addition": "minecraft:netherite_ingot"
  }
}
```

> Ingredients support any item from any installed mod via resource location (e.g. `"othermod:special_ingot"`).

## Passthrough recipes (other mods)

`passthrough` hands a raw recipe body to the game without this mod
interpreting it, so another mod's recipe type can produce your content:

```json
{
  "recipe": {
    "type": "passthrough",
    "json": {
      "type": "create:mixing",
      "heat_requirement": "heated",
      "ingredients": [
        { "item": "minecraft:amethyst_shard" },
        { "tag": "c:ingots/gold" }
      ],
      "results": [
        { "count": 1, "id": "customgear:my_gem" }
      ]
    }
  }
}
```

Everything inside `json` is copied exactly as written, including how tags are
expressed — `{"tag": "c:ingots/gold"}` in Create's case, not this mod's `#`
shorthand. Write it the way that mod documents it; this mod does not translate
or validate the schema, because doing so would mean knowing it.

Whether a given field accepts tags at all is up to that mod, not this one.
Most use vanilla's `Ingredient` system and take tags anywhere an item goes,
but a field expecting a literal ID will reject one — and the error will come
from their deserializer, not from here.

A `neoforge:mod_loaded` condition is derived from the namespace of the inner
`type`, so the recipe is skipped when that mod is absent instead of breaking
the datapack. For recipes spanning several mods, list the extras:

```json
{
  "type": "passthrough",
  "requires": ["create", "createaddition"],
  "json": { "type": "create:mixing", "...": "..." }
}
```

**The result is not this mod's to control.** Every other type builds the
result from the item that owns the recipe; here the result lives inside your
`json`, in whatever shape that mod uses. A passthrough recipe attached to
`ruby_gem` can produce something else entirely.

> ⚠️ `mod_loaded` protects against the mod being **missing**, not against it
> being a different **version**. Mods change their recipe schemas between
> versions, and a deserializer that throws can take down datapack loading
> entirely — not just that one recipe. Re-check your passthrough recipes when
> you update the target mod.

## Recipe Fields

| Field          | Type         | Description                                                                                                   |
|----------------|--------------|---------------------------------------------------------------------------------------------------------------|
| `recipe`       | Object/Array | Recipe for individual items. Can be a single object or an array for multiple recipes                          |
| `recipes`      | Map          | Recipes for sets. One entry per piece/tool/weapon key                                                         |
| `type`         | String       | See the [recipe types](#recipe-types) table                                                                   |
| `pattern`      | String[]     | (shaped) 1–3 rows of up to 3 characters each                                                                  |
| `key`          | Map          | (shaped) Maps each pattern character to an item ID                                                            |
| `ingredients`  | String[]     | (shapeless) List of item IDs                                                                                  |
| `ingredient`   | String       | (cooking/stonecutting) Single input item ID                                                                   |
| `experience`   | Float        | (cooking) XP granted on completion. Default: 0.1                                                              |
| `cooking_time` | Float        | (cooking) **SECONDS** to cook. Decimals allowed. Default: 10s smelting, 5s blasting, 5s smoking, 30s campfire |
| `template`     | String       | (smithing_transform) Template item ID                                                                         |
| `base`         | String       | (smithing_transform) Base item ID to upgrade                                                                  |
| `addition`     | String       | (smithing_transform) Upgrade material item ID                                                                 |
| `json`         | Object       | (passthrough) Raw recipe body, copied verbatim                                                                |
| `requires`     | String[]     | (passthrough) Extra mod IDs beyond the one deduced from the inner type                                        |
| `result_count` | Int          | Number of items produced. Default: 1. Applies to shaped, shapeless and stonecutting                           |

> 💥 **`cooking_time` changed in 1.6.0.** It used to be in ticks. Divide your
> existing values by 20 — a recipe written as `200` meant 10 seconds and now
> means 200 seconds. Nothing errors, the recipe is just 20x slower.

Declaring a field the type ignores (like `experience` on a stonecutting
recipe) logs a warning naming it. The recipe still works; the value just does
nothing.
