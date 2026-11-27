# Custom Gear

**Custom Gear** is a data-driven mod for NeoForge and Fabric, Minecraft 1.21.1, that lets server owners, modpack creators, and players add fully custom armor sets, weapons, tools, food, items, blocks, fluids, and storage — all through simple JSON files. No coding required.

[**Documentation**](docs/en/index.md) · [Español](README_ES.md)

---

## Install

1. Install [NeoForge](https://neoforged.net/) or [Fabric](https://fabricmc.net/) (with [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)) for Minecraft 1.21.1
2. Put the jar for your loader in your `mods/` folder: `customgear-neoforge-1.x.x+mc1.21.1.jar` or `customgear-fabric-1.x.x+mc1.21.1.jar`
3. Launch once to generate the `customgear/` folder inside `.minecraft/`
4. Add JSON files to `.minecraft/customgear/`, or drop a content `.zip` into `.minecraft/customgear/packs/`
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
| [Food](docs/en/food.md) · [Blocks](docs/en/blocks.md) · [Fluids](docs/en/fluids.md) · [Containers](docs/en/containers.md) | Everything else                     |
| [Textures & Models](docs/en/textures-and-models.md)                                                                 | Texture values, armor layers, GeckoLib    |
| [Recipes](docs/en/recipes.md) · [Recipe Examples](docs/en/recipe-examples.md) · [Tags](docs/en/tags.md)             | Crafting and interoperability             |
| [Mob Drops](docs/en/mob-drops.md) · [Damage Resistances](docs/en/damage-resistances.md)                             | Gameplay systems                          |
| [Content Packs](docs/en/content-packs.md) · [Multiplayer](docs/en/multiplayer.md) · [Commands](docs/en/commands.md) | Running a server                          |
| [Migration](docs/en/migration.md)                                                                                   | Breaking changes, version by version      |

**Reference lists** — [Block sound types](docs/en/block-sounds.md) · [Map colors](docs/en/map-colors.md) · [Damage types](docs/en/damage-types.md)

## Compatibility

- Minecraft 1.21.1 · NeoForge 21.1.x, or Fabric with Fabric API
- **JEI** — optional, recommended. Recipes are fully visible
- **GeckoLib** — optional, required only for 3D armor models
- **Curios** (NeoForge) or **Trinkets** (Fabric) — optional, for wearing backpacks
- Reference models, use ingredients, and tag content from any installed mod
- Modded enchantments work automatically on enchantable items
- Server and clients must run the same mod version, the same loader, and matching content files (verified at login)

## Building

The code lives in `common/`, shared by both loaders; `neoforge/` and `fabric/` only connect it to each loader.

- `./gradlew build` builds both jars into `neoforge/build/libs` and `fabric/build/libs`
- The IDE gets run configurations for each loader (NeoForge Client, Fabric Client, …)
- `./gradlew installTestPack` puts [`testpack/`](testpack/README.md), one example of every feature, into both client runs
- `java tools/CheckDocsParity.java` checks that the English and Spanish docs still match

## License

All Rights Reserved — see [LICENSE](LICENSE) for details.
