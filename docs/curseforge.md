# UltimateCustomGear — CurseForge description

## Add custom gear to Minecraft without writing a line of Java

UltimateCustomGear is a data-driven NeoForge mod for Minecraft 26.1.2. Drop a
JSON file into a folder, restart, and your item exists — registered, textured,
craftable, and visible in JEI. It is built for server owners and modpack
creators who need custom content but do not want to maintain a mod for it.

## What you can make

- **Armor sets** with per-piece stats, worn effects, and full-set bonuses
- **Weapons** — swords, bows, crossbows, shields
- **Tools** — pickaxes, axes, shovels, hoes, with harvest tiers and area-tilling
- **Food** with nutrition, eating speed, and on-eat effects
- **Blocks**, including directional and gravity-affected ones, with per-face textures
- **Fluids** with custom color, spread, and contact effects

## What makes it different

**Damage resistances.** Armor can reduce damage by damage type, by attacker, or
by both combined — fire-proof armor, dragon-slayer armor, armor that only works
against undead mobs. Resolved by specificity, so a rule about skeleton arrows beats
the general arrow rule.

**Mob drops.** Any item can drop from any mob, with configurable chance, count,
entity filters, and Looting behavior. Enough to build a server economy out of
JSON alone.

**3D armor models.** With GeckoLib installed, armor renders as a full Blockbench
model with optional animations instead of flat layers. Without GeckoLib it falls
back to 2D — the mod still runs.

**Real recipes.** Every vanilla recipe type, defined in JSON and injected as
server data, so JEI shows them like any other recipe. Ingredients accept tags,
so `#minecraft:planks` means any plank from any mod. Recipes from other mods can
be passed through untouched, guarded so they vanish cleanly when that mod is
absent.

**Tags in both directions.** Your content can join existing tags so other mods'
recipes accept it, and you can add other mods' content to tags you care about.

**Content packs.** Ship everything as a single `.zip` your players drop into a
folder. One file, nothing to extract, nothing to install wrong.

**Multiplayer verification.** The server checks at login that clients have
matching content, so nobody plays with a tooltip that disagrees with the damage
they actually deal. Enforcement is configurable — kick, warn, or ignore.

**Live reload.** `/customgear reload` updates names, effects, recipes, drops, and
resistances without restarting. Balance your economy while players are online.

## A complete item, start to finish

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
    { "effect": "minecraft:regeneration", "amplifier": 1, "duration": 10 },
    { "effect": "minecraft:absorption",   "amplifier": 0, "duration": 120 }
  ],
  "texture": {
    "mode": "reference",
    "refs": { "item": "minecraft:item/golden_apple" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": ["GGG", "GAG", "GGG"],
    "key": { "G": "minecraft:gold_block", "A": "minecraft:apple" }
  }
}
```

That is the whole file. Undead mobs now have a 15% chance to drop it, it is
craftable, and JEI knows about it.

## Documentation

Full documentation — every field, every type, worked examples, and a migration
guide for breaking changes — is in the **wiki** (linked in the sidebar).

Start with **Getting Started** if you have never written a content JSON, or jump
to the **Field Reference** if you know what you are looking for.

## Compatibility

- Minecraft 26.1.2 · NeoForge 26.1.2.x · Java 25
- **JEI** — optional, recommended. Recipes show up fully
- **GeckoLib** — optional, only needed for 3D armor models
- Works with content from any installed mod: reference their models, use their
  items as ingredients, add their content to your tags
- Modded enchantments apply to enchantable custom items automatically

Server and clients need the same mod version and matching content files.

## License

MIT.
