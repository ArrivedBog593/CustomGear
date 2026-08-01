# Changelog

All important changelog notes for the UltimateCustomGear project.

## [1.6.0] - 2026-08-01

### 💥 BREAKING CHANGES

**Two fields changed meaning. Existing content packs need editing.**

#### `cooking_time` is now in SECONDS, not ticks
Every other time field in this mod is in seconds (`eat_duration`, effect `duration`), and `cooking_time` was the odd one out. **Divide your existing values by 20**: a recipe written as `"cooking_time": 200` meant 10 seconds and now means 200 seconds.

Nothing errors — the recipe still works, just 20x slower — so this will not show up as a crash. Check every `smelting`, `blasting`, `smoking` and `campfire_cooking` recipe you have.

#### `requires_player_kill` now defaults to `false`
It used to default to `true`. Vanilla drops do not care who landed the blow, and the old default made every custom drop behave unlike everything else in the game.

If you run an item economy, **declare `"requires_player_kill": true` explicitly** — otherwise a fall damage farm can print currency. The parser warns on every item that leaves the field undeclared, so the log will tell you exactly which files to check.

Note it asks for *recent player damage*, not the killing blow: a mob you hit and a creeper finished still counts as yours.

### ⚠️ Important Notes
- Tags need `/reload` after `/customgear reload` — the tag manager only rebinds on a datapack reload. Everything else stays hot-reloadable
- `passthrough` recipes are copied verbatim and are only protected against the target mod being **missing**, not against it being a different **version**. A mod that changes its recipe schema can break datapack loading entirely
- `charge_speed` no longer appears in tooltips: it never affected the real charge time. See Known Limitations

### ✨ New Features

#### Tag Patches
- New content type `tag_patch` — declares that **foreign** content belongs to a tag, the inverse of the `tags` field. Needed for anything that is not an item you registered: damage types from other mods could not be referenced at all before
- Identified by `registry` + `tag`, no `id`. Two patches naming the same pair merge
- Entries are always emitted with `required: false`, so an uninstalled mod is ignored instead of dropping the whole tag. A typo is warned about when the namespace belongs to a mod that IS loaded
- `remove` takes content **out** of a tag, even when another pack puts it there. That is the only way out of an inherited tag
- Removals from `minecraft:` or `c:` tags are warned about — they affect every mod that reads them

#### Gear Tags
- Armor, tools, and weapons now receive the vanilla tags they always should have had. Gear never went through the tag system at all, which is why a custom sword was offered nothing at the enchanting table while a diamond sword was offered everything
- Slot tags (`#minecraft:chest_armor`…), behavior tags (`#minecraft:swords`, `breaks_decorated_pots`) and, when `enchantable: true`, the matching `#minecraft:enchantable/*` tags
- Convention tags too (`c:armors`, `c:tools`, `c:tools/melee_weapon`…) so other mods recognize this mod's gear generically
- New `trimmable` field on `armor_set` (default `false`). Off by default because trims draw over the armor layers: with `armor_layers: transparent` or a GeckoLib 3D model the trim applies but never shows

#### Recipes
- Three vanilla types that were missing: `smoking`, `campfire_cooking` and `stonecutting`
- `stonecutting` honours `result_count` — cutting one block into several is the normal case
- New `passthrough` type: hands a raw recipe body to the game untouched, so another mod's recipe type (`create:mixing`, `create:pressing`…) can produce this mod's content without this mod knowing that schema. A `neoforge:mod_loaded` condition is derived from the inner type's namespace automatically; extra mods go in `requires`
- Declaring a field the recipe type ignores now logs a warning naming it

#### Mob Drops
- New `looting_mode` replaces `affected_by_looting`, mirroring vanilla's two separate mechanisms:
  - `"count"` (default) — adds 0..levels to the rolled amount, like common drops (rotten flesh, string)
  - `"chance"` — raises the drop probability instead and leaves the amount alone, like rare drops (wither skeleton skulls)
  - `"none"` — Looting does nothing
- Vanilla never applies both to the same drop, and neither does this
- `looting_chance_bonus` (default `0.01`) sets how much probability each Looting level adds in `chance` mode. Not a universal constant in vanilla either — loot tables declare their own
- In `count` mode the bonus applies **before** the empty check and is **not** clamped to `max`, matching vanilla

#### Dynamic Pack Icon
- The dynamic pack now shows the mod's logo in the resource pack screen
- Override it by dropping your own `dynamic_pack_icon.png` in the `ultimatecustomgear/` folder — square, power of two (64×64 or 128×128)
- The icon is deliberately excluded from the content hash, so branding your pack never desyncs clients

### 🐛 Bug Fixes
- **Two tag loaders could silently overwrite each other.** A block declaring `"tags": ["minecraft:mineable/pickaxe"]` landed on the exact path `required_tool` generates, and the last one written won — entries vanished with no warning. All tag sources now feed one accumulator that merges and emits once
- **Individual bows and crossbows never worked in `custom` texture mode.** They fell through to the generic tool branch: no pulling frames, no display transforms, rendering at a tool scale. Crossbows had no custom-mode support at all, not even inside a `weapon_set`
- **The bow's pull animation was desynced from the shot.** It scaled with `charge_speed`, which does not affect the real draw — at `charge_speed: 0.25` the animation ran 16x too fast and skipped `bow_pulling_0` entirely
- **The crossbow could be fired before its animation finished.** The pull predicate divided by the use duration instead of the charge duration, so the crossbow loaded while the animation was still on the first frame
- A missing frame texture dropped every pulling override instead of just that one, freezing the animation with no visible cause

### 🔧 Technical Changes
- `TagFileBuilder.java` — new in `loader/`: shared accumulator for every tag file. `add` fills `values`, `remove` emits NeoForge's `remove` array. When the same id is both added and removed, the **removal wins**, with a warning naming it
- `TagPatchData.java` / `TagPatchLoader.java` — new: the `tag_patch` content type
- `GearTagLoader.java` — new in `loader/`: derives vanilla and convention tags from registered gear
- `ItemTagLoader.java` / `BlockTagLoader.java` — no longer write to the pack; they feed the shared builder
- `RecipeLoader.java` — `buildStonecutting` and `buildPassthrough` added; `buildCooking` now resolves seconds to ticks; `warnUnusedFields` checks declared fields against the type
- `RecipeData.java` — `json` and `requires` for passthrough; `experience` and `resultCount` boxed so an undeclared value is distinguishable from a declared default
- `MobDropHandler.java` — Looting resolved once per death through the dynamic enchantment registry; player involvement now asks for recent damage rather than the killing blow
- `GearModelGenerator.java` — `loadCustomWeapon`, `loadCustomCrossbowTextures` and `customFrame` added; individual weapons routed correctly in `custom` mode
- `ClientSetup.java` — bow `pull` divides by a fixed 20 ticks, crossbow `pull` by `getChargeDuration`
- `DynamicResourcePack.java` — `addRootFile` and a working `getRootResource`, which previously returned `null` unconditionally
- `TextureLoader.java` — `loadPackIcon`

### ⚙️ Known Limitations
- **`charge_speed` does not change the real charge time.** `BowItem.getPowerForTime` and `CrossbowItem.getChargeDuration` are static and cannot be overridden, so a bow always draws in 1.0s and a crossbow in 1.25s. The field only stretches how long the click can be held, which is already minutes either way. The tooltip line was removed rather than keep promising something that never happened
- **Smithing trims cannot be removed from other mods' armor**, only from this mod's. Vanilla's `trimmable_armor` is the union of the four slot tags, so armor inherits it just by being armor
- Tag changes need `/reload` after `/customgear reload`

### 📦 Dependencies

No new dependencies added.

---

## [1.5.0] - 2026-07-19

### ⚠️ Important Notes
- Resistances are fully hot-reloadable — tune every value live with `/customgear reload`
- Adding or removing the `armor_3d` block requires a game restart (the item class is chosen at registration)
- GeckoLib is an **optional** dependency: without it the mod starts normally, and 3D armor falls back to `armor_layers` (or to the vanilla iron layers if none were declared)

### ✨ New Features

#### Mob Drops
- `mob_drops.min` now accepts `0`, matching vanilla drops that can roll empty (rotten flesh is 0–2). It compounds with `chance`: a 25% chance of 0–2 drops something roughly 17% of the time

#### Damage Resistances (three layers)
- Three new fields on armor — `damage_resistances` (by damage type), `attacker_resistances` (by attacker) and `conditional_resistances` (attacker + damage type combined).
- Available at a set level and per piece. Piece entries **merge** with the set ones, winning only on the keys they declare — the rest of the set still applies to that piece. `inherit_set_resistances: false` opts a piece out of the set entirely
- Each equipped piece resolves its own specificity and the pieces then add up, so a rule on one piece never silences the others
- Values are **per equipped piece**: `0.125` on a four-piece set is 50% with the full set worn
- **Specificity model, not accumulation:** `conditional` > `attacker` > `damage`. The first layer with any match *replaces* the more general ones **for that piece**, even when its value is lower. Within a layer, matching entries add up; the pieces then add up
- Accepts exact damage types (`"minecraft:arrow"`), damage type tags (`"#minecraft:is_projectile"`) and modded types (`"iceandfire:dragon_fire"`) — see [damage-types.md](docs/en/damage-types.md) for the full list
- `attacker_resistances` accepts exact entities, entity tags (`"#minecraft:undead"`), mod wildcards (`"mekanism:*"`) and specific players (`"player:Name"`)
- Projectiles inherit their owner: an arrow is attributed to the skeleton that fired it, not to the arrow
- Clamped to 1.0, with **no balance ceiling** — total immunity is a legitimate design choice
- `player:` entries are hidden from the tooltip by default, so surprise armor stays a surprise; the new `show_player_resistances` field (default `false`) makes them visible
- New tooltip sections for each layer, capped at 4 entries with "…and N more"

#### 3D Armor with GeckoLib
- New optional `texture.armor_3d` block with `model`, `texture` (both required) and `animation` (optional) — its mere presence switches the piece to 3D rendering
- Respects the surrounding `mode`: in `custom` the files are copied into the dynamic pack; in `reference` they are resource locations belonging to another mod, which then becomes required
- Falls back cleanly to 2D layers when GeckoLib is absent

### 🐛 Bug Fixes
- Food `on_eat_effects` were baked into `FoodProperties` at construction: the tooltip updated on reload, but eating still applied the old effects. Effects (and `getUseDuration`) now read the live item map, so reload works end to end
- Armor with no `armor_layers` declared produced broken (magenta) inventory icons — an early return skipped item model generation. Layers are now genuinely optional
- The "…and N more" counter in the **Dropped by** tooltip section counted blank entries, reporting more sources than existed

### 🔧 Technical Changes
- `DamageResistanceHandler.java` — new: resolves the three layers at damage time reading `GEAR_MAP` through `GearLookup` (hot-reloadable); attributes projectiles via `source.getEntity()`
- `MobDropHandler.java` — `min` may now be `0`; a roll of zero simply drops nothing instead of being clamped up to one
- `EntityMatcher.java` — new in `util/`: entity matching (exact ID, tags, `mod:*`, `player:`) extracted from `MobDropHandler` so the resistance handler can share it
- `ResistanceResolver.java` — new in `util/`: merges set-level and piece-level resistances and honors `inherit_set_resistances`. Both `DamageResistanceHandler` and `TooltipHelper` resolve through it so the tooltip can never drift from the real reduction
- `GearData.java` — new `damageResistances`, `attackerResistances`, `conditionalResistances` (set and per piece), `showPlayerResistances`, `ConditionalResistance` class, and `Armor3DData` inside `TextureData`
- `GearParser.java` — validation of the three resistance fields
- `UniversalParser.java` — `mob_drops.min` now accepts `0` (the whole item was previously rejected), with separate messages for a negative `min` and for `max` below `min`
- `TooltipHelper.java` — three new resistance sections, entries filtered before rendering so hidden and zero-valued ones never reach the "…and N more" count; the same fix applied to the existing "Dropped by" section
- `GeckoArmorItem.java` — new in `items/gear/geo/`; `GearRegistry.registerArmor` picks the item class, `GearModelGenerator` copies the model files in `custom` mode
- `CustomFoodItem.java` — `on_eat_effects` moved out of `FoodProperties` and applied in `finishUsingItem`

### 📦 Dependencies
- **GeckoLib** — optional, only required for `armor_3d`

---

## [1.4.0] - 2026-07-16

### ⚠️ Important Notes
- `fire_resistant` and armor layer changes are baked at startup — they require a game restart, not `/customgear reload`
- Mob drops ARE hot-reloadable: tune `chance`/`min`/`max`/`entities` live with `/customgear reload`

### ✨ New Features

#### Mob Drops (item economy)
- New `mob_drops` field on items and food — mobs drop your item on death, with configurable `chance` (0–1), `min`/`max` count and entity filter
- `entities` is explicit: `["all"]` for every mob (vanilla and modded), exact IDs (`"minecraft:zombie"`), entity tags (`"#minecraft:undead"`) or mod wildcards (`"mekanism:*"`). Omitting it disables the drop with a log warning
- `requires_player_kill` (default `true`) — mobs killed by the environment drop nothing, so automated farms can't print currency
- Boats, minecarts, and armor stands never drop items; players are always excluded
- Fully hot-reloadable — balance your server economy live
- Items with mob drops show a **"Dropped by"** tooltip section with the source mobs and drop chance (localized entity names, long lists truncated; updates with reload)

#### Fire-Resistant Items
- New `fire_resistant` field on items, food, gear (applies to every piece of a set), blocks, and fluids — the dropped item survives fire and lava, like netherite
- On fluids, it protects the filled bucket (yes, vanilla lava buckets burn in lava — yours don't have to)
- Does NOT make the wearer fire-immune (use fire resistance effects for that)

#### Transparent Armor
- New `"transparent"` value for `armor_layers` — the armor keeps all stats, effects, and set bonuses but draws nothing on the body. Ideal for stat "accessories" that don't cover the skin
- Works in both texture modes. In reference mode it applies to the whole armor (set both layers); per-layer mixing is only available in custom mode

### 🐛 Bug Fixes
- Fixed custom-mode gear textures (armor pieces, armor layers, tools, weapons, bow pulling frames) resolving against the mod's old folder name and never loading from content pack zips

### 🔧 Technical Changes
- `MobDropHandler.java` — new: `LivingDropsEvent` handler reading `ITEM_MAP` at event time (hot-reloadable); supports `all`, `#entity_tags` and `mod:*` wildcards; excludes players and armor stands
- `ItemData.java` — new `mob_drops` (`MobDropsData`) and `fire_resistant` fields; `GearData`, `BlockData`, `FluidData` — new `fire_resistant` field
- All `Custom*Item` classes — properties construction extracted to `buildProps` helpers applying `fireResistant()`; `BlockRegistry` (`blockItemProps`) and `FluidRegistry` (`bucketProps`) apply it to BlockItems and buckets
- `GearModelGenerator.java` — removed the stale `GEAR_FOLDER` constant; custom gear textures resolve through `TextureLoader.resolveUserResource` (content roots); injects a generated fully-transparent PNG for `"transparent"` layers
- `CustomArmorItem.java` — `buildLayers` intercepts `"transparent"` and points the armor material at the injected transparent texture

### 📦 Dependencies
No new dependencies added.

---

## [1.3.1] - 2026-07-05

### ✨ New Features

#### Item / Block / Fluid Tags
- New `tags` field on items, food, blocks, and fluids — declare which tags your content belongs to (no `#` prefix), e.g. `"tags": ["c:ingots", "c:ingots/ruby"]`
- Makes your content usable in other mods' recipes (and your own) that accept those tags: a block tagged `minecraft:planks` works anywhere planks are accepted; an item tagged `c:ingots` is recognized by any mod using that tag
- Blocks are added to both the block and item tag registries (so the block and its item form both count); fluids tag the fluid and their bucket item
- Tags merge with vanilla/other-mod tags of the same name (`"replace": false`); you can also invent your own tags (`customgear:magic_gems`)
- Not available for armor/tool/weapon sets in this version (their per-piece IDs need a separate schema — planned)

#### Unified texture keys for blocks
- `refs` now handles every block texture case: single texture (`all`) or per-face (`top`/`bottom`/`north`/`south`/`east`/`west`/`side`)
- `all` is the canonical single-texture key; `block` is kept as a legacy alias
- `faces` still works for per-face textures (legacy alias)

### 🐛 Bug Fixes
- Fixed custom-mode block textures rejecting the `all`/`block` key (the mode only accepted the per-face path before)
- Fixed custom block textures not loading from content pack zips — they resolved against a hardcoded old folder path instead of the content roots (loose folder + zips). Custom block/face textures now load from zips like everything else
- Fixed placed custom fluids showing a raw translation key in Jade/WAILA instead of their name (the fluid block's name key was missing)
- Fixed being unable to place blocks into a custom fluid to fill or remove it — fluid blocks are now `replaceable` and flagged as liquid, like water/lava

### 🔧 Technical Changes
- `ItemTagLoader.java` — new: generates item/block/fluid tag files from the `tags` field and injects them into the dynamic pack (routes blocks to both block+item registries, fluids to fluid+bucket; 1.21 singular tag paths, `"replace": false`); called at startup and on reload
- `ItemData.java`, `BlockData.java`, `FluidData.java` — new `tags` field (`List<String>`)
- `BlockModelGenerator.java` — unified texture key resolution: `allRef()` (accepts `all`/`block`) and `resolveFaces()` (reads face keys from `refs` or the `faces` object); custom textures now resolve through `TextureLoader.resolveUserResource` (content roots), fixing the hardcoded `./ultimatecustomgear` path that ignored pack zips
- `LangGenerator.java` — emits `block.customgear.<id>` for fluids (Jade/WAILA/F3 name)
- `CustomFluid.java` — fluid block properties now include `.replaceable()` and `.liquid()`
- `CustomGearCommandHandler.java` — reload now also regenerates block tags, block loot tables, and item tags (all pack-injected loaders must run on reload)

### 📦 Dependencies
No new dependencies added.

---

## [1.3.0] - 2026-07-04

### ⚠️ Important Notes
- **Servers and clients must both update to 1.3.0** — the network protocol changed, so older clients cannot join updated servers (they get a clear version-mismatch message).
- Held/set/piece effect icons now show a self-renewing ~12s timer instead of ∞. This is intentional (see Effects Rework below).
- Area-tilling hoes consume 1 durability per tilled block (e.g., radius 3 = up to 49 durability per use).

### ✨ New Features

#### Blocks — Required Tool & Harvest Level
- New `required_tool` field — which tool mines the block: `sword`, `pickaxe`, `axe`, `shovel`, `hoe` or `none` (default)
- `required_tool: "sword"` makes the block mine faster with any sword (like leaves) — note: swords speed up mining but don't gate drops, and `harvest_level` doesn't apply to them
- New `harvest_level` field — tool tier required to get drops: 0 (wood), 1 (stone), 2 (iron), 3 (diamond), 4 (netherite)
- `required_tool` grants mining speed only (like sand + shovel); `harvest_level` ≥ 1 is what gates drops behind the correct tool tier, like vanilla ore
- Implemented via vanilla block tags injected as server data — fully compatible with modded tools that follow vanilla tiers

#### Content Packs (.zip)
- Content can now be distributed as **.zip files** dropped into `.minecraft/ultimatecustomgear/packs/` — JSONs and textures inside load exactly like loose files
- Loose files take precedence over zips: you can locally override a single item from a pack by placing your own version loose
- Only `.zip` is supported; `.rar`/`.7z` files and archives left in the root folder produce log messages explaining how to fix it
- `/customgear reload` picks up added or updated zips without restarting

#### Multiplayer Content Verification
- When a client joins, the server verifies the client loaded the same content JSONs (compared via content hash — packaging doesn't matter: zip vs. loose files with the same content match)
- New config `content_handshake_mode` in `ultimatecustomgear-common.toml`:
    - `ENFORCE` (default) — mismatching clients are disconnected with a message showing both hashes and how to fix it
    - `WARN` — mismatching clients are allowed in with a chat warning; the server logs the mismatch
    - `OFF` — the check is skipped entirely
- Only the server's setting matters — clients obey what the server sends
- The dynamic pack now advertises a content-derived version to vanilla's pack negotiation: if client and server recipes differ, the server's recipes are sent and used (no more silent "ghost recipe" desyncs)

#### Recipe Tag Ingredients
- Any ingredient slot accepts a **tag** with the `#` prefix, e.g. `"#minecraft:planks"` (any plank) or `"#c:ingots/iron"` (iron ingots from any mod) — works in shaped, shapeless, smelting, blasting, and smithing

#### Full Recipe Validation
- Shaped recipes are now fully validated with clear log messages: row length (1–3), uneven rows, pattern symbols missing from `key`, unused `key` entries, and malformed item/tag IDs
- Shapeless recipes with more than 9 ingredients are rejected with a message

### 🐛 Bug Fixes
- Fixed **startup crashes from ID collisions across content types** (e.g., an item and a block sharing an id, an item colliding with a set-derived name like `myset_sword`, or with a fluid's `_bucket` item). Colliding entries are now skipped with a log naming both owners
- Fixed **all mod recipes disappearing after `/reload`** — the reload command wasn't regenerating recipes into the dynamic pack
- Fixed **damage-over-time contact effects (poison, wither) never dealing damage while inside a fluid** — reapplication was resetting the effect before its damage tick
- Fixed real potions being wiped when swapping away from an item granting the same effect
- Fixed "ghost" permanent effects persisting after logging out while holding an effect-granting item
- Fixed the dynamic pack being registered twice per pack scan
- Fixed custom item textures resolving against the mod's old folder name (`customgear/` instead of `ultimatecustomgear/`)
- Fixed content not loading under launchers with a nonstandard working directory (config folder now resolves against the real game directory)
- Fixed fluid contact state leaking memory and using stale counters after death or dimension change (now keyed by player UUID, cleaned on logout)
- Area-tilling hoes now stop when the hoe breaks mid-area and always till the clicked block first
- Fixed `burns_entities` and `contact_effects` only affecting players — fluids now affect **all entities** (mobs, dropped items, projectiles) like vanilla lava. Dropped items in a burning fluid are destroyed; fire-immune mobs (blazes, etc.) are unaffected
- Fixed custom blocks never dropping items in survival — `requiresCorrectToolForDrops` was applied unconditionally to all blocks, even those without any tool requirement
- Fixed custom blocks never dropping themselves when broken — blocks had no loot tables (Minecraft blocks drop nothing without one). Every custom block now gets a standard self-drop loot table with the vanilla explosion condition

### ♻️ Behavior Changes
- **Effects Rework**: held effects, set bonuses, and piece effects use beacon-style refreshed short durations instead of infinite ones. A real potion of the same effect always wins while it lasts; effects self-expire within seconds if tracking is lost
- If a player overlaps two custom fluids at once, only the dominant (deepest) one applies its effects — previously both did
- `/customgear reload` now triggers the data-pack reload itself: recipes update immediately without running `/reload`. Texture changes still require pressing F3+T; base stats still require a restart

### 🔧 Technical Changes
- `ContentRoots.java` — new: mounts the loose folder plus every zip in `packs/` as uniform content roots (deterministic alphabetical order)
- `GlobalIdValidator.java` — new: validates all final derived IDs across item/block/fluid registries before registration, atomic per-entry claims
- `ContentHasher.java` — new: canonical (key-sorted, whitespace/packaging independent) hash of all content JSONs across all roots, captured at load time
- `CustomGearNetworking.java`, `HashCheckPayload.java`, `HashCheckAckPayload.java` — new: configuration-phase handshake with server-decided enforcement traveling in the payload; protocol version "2"
- `CustomGearConfig.java` — new: COMMON config with `content_handshake_mode`
- `GearParser.java` / `UniversalParser.java` — iterate all content roots; cache keys prefixed per-root (`packs/foo.zip!path`)
- `TextureLoader.java` — custom resources resolved through the content session (fixes the legacy `./customgear` path)
- `RecipeLoader.java` — tag ingredient support, full structural validation, atomic per-recipe rejection
- `EffectUtils.java` — 240-tick refreshed durations (steady HUD icons), potion-safe removal
- `SetBonusHandler.java` — diff-based apply/remove instead of remove-all/reapply-all
- `CustomHoeItem.java` — durability check in the area loop, center-first
- `DynamicResourcePack.java` — `location()` override carrying a content-hash `KnownPack`; `ConcurrentHashMap`; cached hash invalidated on mutation
- `CustomGearMod.java` / `CustomGearCommandHandler.java` — content session (try-with-resources) around load/reload; hash re-capture on reload; automatic data-pack reload after `/customgear reload`
- `CustomLiquidBlock.java` — new: applies fire and contact effects to every entity via `entityInside` (vanilla-lava style), stateless, with the DoT-friendly effect refresh; replaces the removed `FluidContactHandler`
- `CustomFluid.java` — `createBlock` now creates a `CustomLiquidBlock`
- `BlockTagLoader.java` — new: generates `minecraft:tags/block/mineable/*` and `needs_*_tool` tags into the dynamic pack (1.21 singular tag paths); called at startup and on reload
- `CustomBlock.java` — `buildProperties` applies `requiresCorrectToolForDrops()` only when `harvest_level` ≥ 1 (`required_tool` alone never gates drops)
- `BlockLootLoader.java` — new: generates self-drop loot tables into the dynamic pack; called at startup and on reload

### 📦 Dependencies
No new dependencies added.

---

## [1.2.7] - 2026-06-30

### ✨ New Features

#### Blocks — Gravity
- New `gravity: true` field makes a block fall when unsupported, like sand or gravel
- Cannot be combined with `directional`

#### Blocks — New Configurable Properties
- `destroy_time` — time in seconds to break the block with the correct tool (default: 3.0)
- `explosion_resistance` — resistance to explosions (default: 3.0, obsidian: 1200.0)
- `sound` — block sound type when placing, breaking or walking on it (default: `stone`)
- Over 100 vanilla sound types are supported: `wood`, `gravel`, `sand`, `deepslate`, `amethyst`, `copper`, `sculk`, `netherite`, etc.
- `map_color` — block map color (default: `stone`); supports basic colors, dyes, and special colors like `gold`, `diamond`, `lapis`, `emerald`, `podzol`, and `nether`

#### Blocks — Fix light_level
- Fixed `light_level` not working — missing `@SerializedName("light_level")` annotation in `BlockData`

### 🔧 Technical Changes
- `BlockData.java` — added `destroyTime`, `explosionResistance`, `sound` fields with `@SerializedName`; fixed `@SerializedName("light_level")` on `lightLevel`
- `CustomBlock.java` — `buildProperties` now uses `data.destroyTime`, `data.explosionResistance` and `BlockSoundResolver.resolve(data.sound)`
- `CustomFallingBlock.java` — new class extending `FallingBlock`; reuses `CustomBlock.buildProperties`
- `BlockRegistry.java` — detects `gravity: true` and creates `CustomFallingBlock`
- `BlockSoundResolver.java` — new utility class in `util/` with 112 vanilla sound types

### 📦 Dependencies
No new dependencies added.

---

## [1.2.6] - 2026-06-29

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
- Pickaxes, axes, shovels, and hoes now show `harvest_level` and `mining_speed` in the tooltip

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
- New item type `food` — consumable items with custom nutrition, saturation, and effects
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
- Fixed: overrides were not inherited from the parent model — now declared explicitly in generated JSON

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
- Fixed `EffectUtils`: added try-catch around `ResourceLocation.parse()` to prevent a server crash on malformed effect IDs from stale cache
- Fixed `ClientSetup`: shield `blocking` property was registered outside `enqueueWork`, causing a potential race condition
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
- **Before:** The command only reloaded textures but didn't update names, durability, effects, or attributes
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