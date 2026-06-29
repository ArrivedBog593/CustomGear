# Changelog

All important changelog notes for the UltimateCustomGear project.

## [1.2.6] - 2026-06-28

### ✨ New Features

#### Blocks — Per-Face Textures
- Blocks now support different textures per face via `texture.faces` in the JSON
- Keys: `top`, `bottom`, `north`, `south`, `east`, `west`
- Shortcut `side` applies to all 4 horizontal faces if not individually defined
- Works in both `reference` mode (resource locations) and `custom` mode (PNG files)

#### Blocks — Directional Placement
- New `directional: true` field makes a block rotate to face the player when placed, like a furnace
- The `texture.faces.north` face is treated as the front face
- Only horizontal directions are supported (north, south, east, west)

### 🔧 Technical Changes
- `BlockData.java` — replaced `GearData.TextureData texture` with new `BlockTextureData` class containing `mode`, `refs` and `faces`; `BlockFaces` moved inside `BlockTextureData`
- `CustomDirectionalBlock.java` — new class extending `HorizontalDirectionalBlock`
- `BlockRegistry.java` — detects `directional: true` and creates `CustomDirectionalBlock`
- `BlockModelGenerator.java` — added `loadReferenceFacesBlock`, `loadCustomFacesBlock`, `generateCubeModel`, `generateDirectionalBlockState`

### 📦 Dependencies
No new dependencies added.

---

## [1.2.5] - 2026-06-16

### ✨ New Features

#### Bow & Crossbow Tooltips
- Bows and crossbows now show `arrow_damage`, `arrow_damage_bonus`, `arrow_damage_multiplier` and `charge_speed` in the tooltip

#### Tool Tooltips
- Pickaxes, axes, shovels and hoes now show `harvest_level` and `mining_speed` in the tooltip

### 🐛 Bug Fixes
- Fixed non-burning fluids not extinguishing fire — fluids with `burns_entities: false` now call `player.clearFire()` on contact

### 🔧 Technical Changes
- `TextureLoader.java` — split into `GearModelGenerator`, `BlockModelGenerator`, `LangGenerator` and `ModelConstants`
- `TooltipHelper.java` — added `addBowTooltip()` and `addToolStatsTooltip()`
- `CustomBowItem.java` / `CustomCrossbowItem.java` — call `addBowTooltip()` in `appendHoverText`
- `CustomPickaxeItem.java` / `CustomAxeItem.java` / `CustomShovelItem.java` / `CustomHoeItem.java` — call `addToolStatsTooltip()` in `appendHoverText`
- `FluidContactHandler.java` — added `player.clearFire()` for non-burning fluids
- Build system cleanup: `build.gradle`, `gradle.properties` and `neoforge.mods.toml` updated to standard `moddev` conventions

### 📦 Dependencies

No new dependencies added.

---

## [1.2.4] - 2026-06-11

### ✨ New Features

#### Fluid Contact Effects & Behavior
- `burns_entities` — fluid sets entities on fire like lava
- `burn_duration` — seconds the entity burns after touching the fluid. Default: 5
- `contact_effects` — list of effects applied while submerged, each with `effect`, `amplifier` and `duration` (seconds)
- `contact_effect_interval` — how often effects are applied in seconds. Default: 1.0
- `tick_rate` — controls how fast the fluid spreads. Lower = faster. Water=5, Lava=30. Default: 5
- `spread_distance` — max horizontal blocks the fluid reaches. Water=8, Lava=4. Default: 8

### 🐛 Bug Fixes
- Fixed `eat_duration` field — now declared in seconds instead of ticks, consistent with `burn_duration` and `contact_effect_interval`

### 🔧 Technical Changes
- `FluidData.java` — added `tickRate`, `spreadDistance`, `burnsEntities`, `burnDuration`, `contactEffects`, `contactEffectInterval`
- `FluidRegistry.java` — `buildProps` now applies `tickRate` and `levelDecreasePerBlock` derived from `spreadDistance`
- `FluidContactHandler.java` — new event handler using `PlayerTickEvent.Post` for fluid contact effects and fire
- `CustomGearMod.java` — registers `FluidContactHandler`
- `ItemData.java` — `eatDuration` changed from `int` (ticks) to `float` (seconds)
- `CustomFoodItem.java` — `getUseDuration` now converts seconds to ticks

### 📦 Dependencies

No new dependencies added.

---

## [1.2.3] - 2026-06-05

### ✨ New Features

#### Food Items (`type: "food"`)
- New item type `food` — consumable items with custom nutrition, saturation and effects
- Configurable `nutrition` and `saturation` values
- `always_edible` — allows eating even when the hunger bar is full (like golden apples)
- `fast_food` — item is consumed faster (like dried kelp)
- `eat_duration` — fine-grained control over consumption time in ticks (0 = instant, 200 = 10 seconds)
- `on_eat_effects` — list of effects applied on consumption, each with `effect`, `amplifier`, `duration` (seconds) and `probability`
- Tooltip support via `TooltipHelper.addFoodEffectsTooltip()` showing eat duration and all on-eat effects with duration and level
- Ingredients from any installed mod are supported

### 🔧 Technical Changes

- `ItemData.java` — added `nutrition`, `saturation`, `alwaysEdible`, `fastFood`, `eatDuration`, `onEatEffects` fields and `FoodEffectData` inner class
- `CustomFoodItem.java` — new class extending `Item` with `FoodProperties` built from `ItemData`; overrides `getUseDuration` for custom eat timing; delegates tooltip to `TooltipHelper`
- `ItemRegistry.java` — detects `type = "food"` and creates `CustomFoodItem` instead of `CustomItem`
- `UniversalParser.java` — `case "item", "food"` now handles both types in the same branch
- `TooltipHelper.java` — added `addFoodEffectsTooltip()` method reusing existing `getEffectName()` and `toRoman()`

### 🐛 Bug Fixes

- Fixed `/customgear reload` command using hardcoded `"customgear"` folder path instead of `CustomGearMod.MOD_ID` — caused all textures to revert to the magenta/black placeholder after reload

### 📦 Dependencies

No new dependencies added.

---

## [1.1.0] - 2026-06-04

### ✨ New Features

#### Native Recipe System
- CustomGear now supports **crafting recipes defined directly in JSON files** — no external mods required
- Recipes are injected as server data via the dynamic pack, making them fully visible in JEI
- Supported recipe types: `shaped`, `shapeless`, `smelting`, `blasting`, `smithing_transform`
- Individual items use a single `recipe` field (object or array for multiple recipes)
- Sets (armor, tool, weapon) use a `recipes` map with one entry per piece/tool/weapon
- Ingredients and results support any item from any installed mod via resource location

#### Bow and Crossbow Animation Fix
- Custom bows now correctly animate through all three pull frames when drawing
- Custom crossbows now correctly animate through loading and charged states
- Animation timing scales correctly with custom `charge_speed` values
- Fixed: overrides were not inherited from parent model — now declared explicitly in generated JSON

#### Shield 3D Rendering
- Custom shields now render their full 3D model in hand and inventory
- Implemented `CustomShieldBEWLR` — a dedicated Block Entity Without Level Renderer for custom shields
- Shield blocking animation works correctly with the correct transforms
- Shield texture is read from the vanilla `shield_patterns` atlas

### 🔧 Technical Changes

- `RecipeData.java` — new POJO for recipe data deserialization
- `RecipeListDeserializer.java` — custom Gson deserializer allowing `recipe` field to be either object or array
- `RecipeLoader.java` — generates recipe JSON files and injects them into `DynamicResourcePack` as server data
- `GearData.java` — added `recipe` (List) and `recipes` (Map) fields
- `ItemData.java` — added `recipe` field
- `BlockData.java` — added `recipe` field
- `GearParser.java` — updated Gson instance to include `RecipeListDeserializer`
- `UniversalParser.java` — updated Gson instance to include `RecipeListDeserializer`
- `CustomGearMod.java` — registered dynamic pack for both `CLIENT_RESOURCES` and `SERVER_DATA`; added `RecipeLoader.loadAll()` call
- `ClientSetup.java` — moved shield `blocking` property registration inside `enqueueWork`; added `registerShieldProperties()` method
- `CustomShieldItem.java` — implemented `initializeClient()` using `CustomShieldBEWLR`
- `CustomShieldBEWLR.java` — new class extending `BlockEntityWithoutLevelRenderer` for shield rendering
- `TextureLoader.java` — `generateShieldFlatModel()` now generates `builtin/entity` models with vanilla display transforms and blocking override; `generateBowModelWithRef()` and `generateCrossbowModelWithRef()` now accept a `refs` map for optional custom pulling models; shield and bow/crossbow models in default/weapon_set modes corrected

### 🐛 Bug Fixes

- Fixed `SetBonusHandler`: ghost effects no longer persist after `/customgear reload` changes a set ID
- Fixed `GearParser`: missing lower-bound validation for weapon damage fields (`attackDamage`, `arrowDamage`, `damageMultiplier`, `arrowDamageMultiplier`, `chargeSpeed`, `tillRadius`)
- Fixed `EffectUtils`: added try-catch around `ResourceLocation.parse()` to prevent server crash on malformed effect IDs from stale cache
- Fixed `ClientSetup`: shield `blocking` property was registered outside `enqueueWork`, causing potential race condition
- Fixed dead state in `SetBonusHandler`: removed unused `activeSetBonuses` and `activePieceEffects` maps

### 📦 Dependencies

No new dependencies added.

---

## [1.0.2] - 2026-04-13

### ✨ Improvements

- **Multi-Language Support:** Item naming system now better supports complex languages (Chinese, Russian, Japanese, etc.)
- **Armor Textures in Reference Mode:** Armor sets can now reference layers from other mods using `armor_layers` field
- **Documentation Updates:** README updated with multi-language examples and reference mode armor layer support

### 🔧 Technical Changes

- `CustomSwordItem.java` & `CustomArmorItem.java` — Prioritize specific item names over format placeholders
- `TextureLoader.java` — Added support for armor layers in reference mode
- `GearData.java` — Added `armor_layers` field for texture configuration
- `DynamicResourcePack.java` — Added `addReferenceTexture()` method

---

## [1.0.1] - 2026-04-13

### ✨ Improvements

#### `/customgear reload` Command — Now Fully Functional
- **Before:** The command only reloaded textures, but didn't update names, durability, effects, or attributes
- **Now:** The command reloads all item data dynamically at runtime

### 🔄 Technical Changes

- `CustomSwordItem.java` through `CustomArmorItem.java` — implemented dynamic data lookup via `getGearData()`
- `CustomGearCommandHandler.java` — implemented `updateGearRegistry()`
- `SetBonusHandler.java` — updated to use `getGearDataDirect()`

### 🧹 Code Cleanup

- `GearData.java` — removed unused `toughness` and `knockbackResistance` root fields
- `DynamicResourcePack.java` — improved `close()` comment

### 📦 Dependencies

No new dependencies added.

---

## [1.0.0] - 2026-04-12

### ✨ Initial Release

- Data-driven system based on JSON
- Support for custom armor sets and tool sets
- Held effects and set bonuses
- Custom or reference textures
- Multi-language support
- `/customgear reload` command (limited functionality)