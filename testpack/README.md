# Test pack

One example of everything the mod does, using only vanilla textures and one
PNG of its own, so it runs in a bare dev instance. `./gradlew installTestPack`
copies `customgear/` into the client run of both loaders, replacing whatever
was there.

| File | Covers |
|---|---|
| `items/test_gem.json` | `item`, a texture from a file, tags, `fire_resistant`, mob drops (player kill, looting count), shapeless with `result_count`, smelting, blasting |
| `items/test_food.json` | `food`, eat effects with probability, `eat_duration`, `always_edible`, mob drops (looting chance), smoking, campfire |
| `blocks/test_ore.json` | `block`, light, `harvest_level` (iron pickaxe or better to drop), sound, map colour, tags, smelted into `test_gem` |
| `blocks/test_machine.json` | directional block, per-face textures, a recipe that asks for tags (`#c:ingots/iron`, `#c:gems/test`) |
| `blocks/test_sand.json` | falling block, stonecutting |
| `fluids/test_fluid.json` | `fluid`, tint, light, spread, tick rate, contact effects, bucket names, fluid tag |
| `gear/test_tools.json` | `tool_set`, held effect, hoe `till_radius` |
| `gear/test_weapons.json` | `weapon_set`: sword (smithing), bow, crossbow, shield |
| `gear/test_dagger.json` | an individual `sword`, `damage_multiplier` |
| `gear/test_armor.json` | `armor_set`, armor layers, piece effects, set bonus, all three resistance layers, a per-piece override, `trimmable` |
| `gear/test_gecko_armor.json` | GeckoLib 3D armor (`geo/test_gecko_armor.geo.json`, a vanilla texture) with flat layers as the fallback |
| `containers/test_barrel.json` | barrel, open-lid texture |
| `containers/test_chest.json` | chest, double chests, single/left/right textures |
| `containers/test_shulker.json` | shulker, `columns`, keeps its contents |
| `containers/test_backpack.json` | backpack, `accessory_slots` in both forms (`back` → Trinkets `chest/back`; `chest/necklace` → Curios `necklace`; `charm` is Curios-only) |
| `patches/test_tag_patches.json` | `tag_patch` on damage types, which `test_armor` resists |
| `patches/test_item_tag_patch.json` | `tag_patch` on items: amethyst joins `#c:gems/test` |
| `patches/test_recipe_patch.json` | `recipe_patch`: add (plus a Create passthrough that must be skipped without Create), remove, remove_output, replace |
| `packs/test_pack.zip` | a content pack: `test_coin`, with its JSON and texture inside the zip. Built from `zip-src/test_pack` |

## What to check

- Every item is in the Custom Gear creative tab with its name (English and Spanish) and texture.
- The log has no `[CustomGear]` errors. The one expected warning: the Create passthrough recipe is skipped.
- `/customgear recipes` on a hopper shows the replaced recipe; golden apples have no crafting recipe; saddles and name tags can be crafted.
- `test_ore` drops only to an iron pickaxe or better; `test_sand` falls; `test_machine` faces the player.
- The fluid places, flows 5 blocks, glows, tints green, poisons and slows. On Fabric, swimming in it does not work yet.
- The armor gives night vision, jump boost and, with all four pieces, Resistance; cactus damage is reduced.
- The 3D armor shows horns and shoulder pads made of diamond blocks.
- Containers open, sort and search; two chests join; a shulker keeps its items when broken.
- The backpack opens with B and can be worn: Curios on NeoForge (back, necklace, charm), Trinkets on Fabric (back, necklace).
- Undead drop test gems when killed by a player; cows and pigs sometimes drop the stew.
- `/customgear reload` works and keeps everything above.
