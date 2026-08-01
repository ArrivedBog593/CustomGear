# Getting Started

## Installation

1. Download and install [NeoForge 1.21.1](https://neoforged.net/)
2. Place `ultimatecustomgear-1.x.x.jar` in your `mods/` folder
3. Launch the game once to generate the `ultimatecustomgear/` folder inside `.minecraft/`
4. Add your JSON files to `.minecraft/ultimatecustomgear/` — or drop a content `.zip` into `.minecraft/ultimatecustomgear/packs/` (see [Content Packs](content-packs.md))
5. Restart the game

> **JSON syntax note:** Standard JSON does not allow trailing commas. A trailing comma after the last element in an object or array will cause the file to be silently skipped on load.

## JSON File Structure

All JSON files go inside `.minecraft/ultimatecustomgear/`. Each file defines one item, set, block, or fluid. Files can be organized in any subfolder structure you prefer.

Content can also be packaged as `.zip` files inside `packs/` — see [Content Packs](content-packs.md).

### Supported types

| Type         | Description                                                     |
|--------------|-----------------------------------------------------------------|
| `armor_set`  | Full armor set (helmet, chestplate, leggings, boots)            |
| `tool_set`   | Full tool set (pickaxe, axe, shovel, hoe)                       |
| `weapon_set` | Full weapon set (sword, bow, crossbow, shield)                  |
| `sword`      | Individual sword                                                |
| `bow`        | Individual bow                                                  |
| `crossbow`   | Individual crossbow                                             |
| `shield`     | Individual shield                                               |
| `pickaxe`    | Individual pickaxe                                              |
| `axe`        | Individual axe                                                  |
| `shovel`     | Individual shovel                                               |
| `hoe`        | Individual hoe                                                  |
| `food`       | Consumable food item                                            |
| `item`       | Simple non-consumable item                                      |
| `block`      | Block with optional per-face textures and directional placement |
| `fluid`      | Fluid with bucket, configurable spread and contact effects      |
