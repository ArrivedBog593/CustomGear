# UltimateCustomGear

[![CurseForge](https://cf.way2muchnoise.eu/versions/ultimate-custom-gear.svg)](https://www.curseforge.com/minecraft/mc-mods/ultimate-custom-gear)
[![CurseForge](https://cf.way2muchnoise.eu/ultimate-custom-gear.svg)](https://www.curseforge.com/minecraft/mc-mods/ultimate-custom-gear)

**UltimateCustomGear** is a data-driven NeoForge mod for Minecraft 26.1.2 that lets server owners, modpack creators, and players add fully custom armor sets, weapons, tools, food, items, blocks, and fluids — all through simple JSON files. No coding required.

[**Documentation**](docs/en/index.md) · [Español](README_ES.md)

---

## Install

1. Install [NeoForge 26.1.2](https://neoforged.net/)
2. Put `ultimatecustomgear-1.x.x.jar` in your `mods/` folder
3. Launch once to generate the `ultimatecustomgear/` folder inside `.minecraft/`
4. Add JSON files to `.minecraft/ultimatecustomgear/`, or drop a content `.zip` into `.minecraft/ultimatecustomgear/packs/`
5. Restart

## A complete item

```json
{
  "id": "magic_apple",
  "type": "food",
  "names": { "en_us": "Magic Apple" },
  "nutrition": 4,
  "saturation": 1.2,
  "always_edible": true,
  "mob_drops": {
    "entities": ["#minecraft:undead"],
    "chance": 0.15,
    "min": 1,
    "max": 2
  },
  "on_eat_effects": [
    { "effect": "minecraft:regeneration", "amplifier": 1, "duration": 10 }
  ],
  "texture": {
    "refs": { "item": "minecraft:item/golden_apple" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": ["GGG", "GAG", "GGG"],
    "key": { "G": "minecraft:gold_block", "A": "minecraft:apple" }
  }
}
```

That is the whole file. Undead mobs now drop it, it is craftable, and JEI knows about it.

## Documentation

|                                                                                                                     |                                           |
|---------------------------------------------------------------------------------------------------------------------|-------------------------------------------|
| [Getting Started](docs/en/getting-started.md)                                                                       | Install, where files go, content types    |
| [Common Fields](docs/en/common-fields.md)                                                                           | Shared fields, effect object, ID patterns |
| [Armor Sets](docs/en/armor-sets.md) · [Weapons](docs/en/weapons.md) · [Tools](docs/en/tools.md)                     | Gear                                      |
| [Food](docs/en/food.md) · [Blocks](docs/en/blocks.md) · [Fluids](docs/en/fluids.md)                                 | Everything else                           |
| [Textures & Models](docs/en/textures-and-models.md)                                                                 | Texture modes, armor layers, GeckoLib     |
| [Recipes](docs/en/recipes.md) · [Tags](docs/en/tags.md)                                                             | Crafting and interoperability             |
| [Mob Drops](docs/en/mob-drops.md) · [Damage Resistances](docs/en/damage-resistances.md)                             | Gameplay systems                          |
| [Content Packs](docs/en/content-packs.md) · [Multiplayer](docs/en/multiplayer.md) · [Commands](docs/en/commands.md) | Running a server                          |
| [Migration](docs/en/migration.md)                                                                                   | Breaking changes, version by version      |

**Reference lists** — [Block sound types](docs/en/block-sounds.md) · [Map colors](docs/en/map-colors.md) · [Damage types](docs/en/damage-types.md)

## Compatibility

- Minecraft 26.1.2 · NeoForge 26.1.2.x · Java 25
- **JEI** — optional, recommended. Recipes are fully visible
- **GeckoLib** — optional, required only for 3D armor models
- Reference models, use ingredients, and tag content from any installed mod
- Modded enchantments work automatically on enchantable items
- Server and clients must run the same mod version and matching content files (verified at login)

## License

MIT License — see [LICENSE](LICENSE) for details.
