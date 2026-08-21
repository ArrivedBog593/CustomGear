# UltimateCustomGear Old Versions Changelog

*NeoForge 1.21.1 · from 2026-04-12 to 2026-06-30*

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

### 📦 Dependencies

No new dependencies added.

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

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

## [1.2.7] - 2026-06-30

### ✨ Nuevas Características

#### Bloques — Gravedad
- Nuevo campo `gravity: true` hace que el bloque caiga cuando no tiene soporte, como la arena o la grava
- No es compatible con `directional`

#### Bloques — Nuevas Propiedades Configurables
- `destroy_time` — tiempo en segundos para romper el bloque con la herramienta correcta (por defecto: 3.0)
- `explosion_resistance` — resistencia a explosiones (por defecto: 3.0, obsidiana: 1200.0)
- `sound` — tipo de sonido al colocar, romper o caminar sobre el bloque (por defecto: `stone`)
- Más de 100 tipos de sonido vanilla soportados: `wood`, `gravel`, `sand`, `deepslate`, `amethyst`, `copper`, `sculk`, `netherite`, etc.
- `map_color` — color del bloque en el mapa (por defecto: `stone`); soporta colores básicos, tintes y colores especiales como `gold`, `diamond`, `lapis`, `emerald`, `podzol` y `nether`

#### Bloques — Fix de light_level
- Corregido `light_level` que no funcionaba — faltaba la anotación `@SerializedName("light_level")` en `BlockData`

### 🔧 Cambios Técnicos
- `BlockData.java` — se agregaron los campos `destroyTime`, `explosionResistance`, `sound` con `@SerializedName`; corregido `@SerializedName("light_level")` en `lightLevel`
- `CustomBlock.java` — `buildProperties` ahora usa `data.destroyTime`, `data.explosionResistance` y `BlockSoundResolver.resolve(data.sound)`
- `CustomFallingBlock.java` — nueva clase que extiende `FallingBlock`; reutiliza `CustomBlock.buildProperties`
- `BlockRegistry.java` — detecta `gravity: true` y crea `CustomFallingBlock`
- `BlockSoundResolver.java` — nueva clase utilitaria en `util/` con 112 tipos de sonido vanilla

### 📦 Dependencias
No se agregaron nuevas dependencias.

---

## [1.2.6] - 2026-06-29

### ✨ Nuevas Características

#### Bloques — Texturas por Cara
- Los bloques ahora soportan texturas diferentes por cara mediante `texture.faces` en el JSON
- Claves: `top`, `bottom`, `north`, `south`, `east`, `west`
- El atajo `side` aplica a las 4 caras horizontales si no están definidas individualmente
- Funciona en modo `reference` (resource locations) y modo `custom` (archivos PNG)

#### Bloques — Colocación Direccional
- El nuevo campo `directional: true` hace que el bloque rote para apuntar al jugador al colocarse, como un horno
- La cara `texture.faces.north` se trata como la cara frontal
- Solo se soportan las 4 direcciones horizontales (norte, sur, este, oeste)

### 🔧 Cambios Técnicos
- `BlockData.java` — reemplazado `GearData.TextureData texture` por la nueva clase `BlockTextureData` con `mode`, `refs` y `faces`; `BlockFaces` movido dentro de `BlockTextureData`
- `CustomDirectionalBlock.java` — nueva clase que extiende `HorizontalDirectionalBlock`
- `BlockRegistry.java` — detecta `directional: true` y crea `CustomDirectionalBlock`
- `BlockModelGenerator.java` — añadidos `loadReferenceFacesBlock`, `loadCustomFacesBlock`, `generateCubeModel`, `generateDirectionalBlockState`

### 📦 Dependencias
No se agregaron nuevas dependencias.

---

## [1.2.5] - 2026-06-16

### ✨ Nuevas Características

#### Tooltips de Arco y Ballesta
- Los arcos y ballestas ahora muestran `arrow_damage`, `arrow_damage_bonus`, `arrow_damage_multiplier` y `charge_speed` en el tooltip

#### Tooltips de Herramientas
- Los picos, hachas, palas y azadones ahora muestran `harvest_level` y `mining_speed` en el tooltip

### 🐛 Corrección de Bugs
- Corregido: los fluidos que no queman ahora apagan el fuego del jugador — fluidos con `burns_entities: false` ahora llaman `player.clearFire()` al contacto

### 🔧 Cambios Técnicos
- `TextureLoader.java` — dividido en `GearModelGenerator`, `BlockModelGenerator`, `LangGenerator` y `ModelConstants`
- `TooltipHelper.java` — se agregaron `addBowTooltip()` y `addToolStatsTooltip()`
- `CustomBowItem.java` / `CustomCrossbowItem.java` — llaman `addBowTooltip()` en `appendHoverText`
- `CustomPickaxeItem.java` / `CustomAxeItem.java` / `CustomShovelItem.java` / `CustomHoeItem.java` — llaman `addToolStatsTooltip()` en `appendHoverText`
- `FluidContactHandler.java` — se agregó `player.clearFire()` para fluidos que no queman
- Limpieza del build: `build.gradle`, `gradle.properties` y `neoforge.mods.toml` actualizados a convenciones estándar de `moddev`

### 📦 Dependencias

No se agregaron nuevas dependencias.

---

## [1.2.4] - 2026-06-11

### ✨ Nuevas Características

#### Efectos y Comportamiento del Fluido al Contacto
- `burns_entities` — el fluido prende fuego a las entidades como la lava
- `burn_duration` — segundos que dura el fuego al tocar el fluido. Por defecto: 5
- `contact_effects` — efectos aplicados mientras se está sumergido, con `effect`, `amplifier` y `duration` (segundos)
- `contact_effect_interval` — cada cuántos segundos se aplican los efectos. Por defecto: 1.0
- `tick_rate` — controla qué tan rápido se expande el fluido. Menor = más rápido. Agua=5, Lava=30. Por defecto: 5
- `spread_distance` — bloques horizontales máximos que alcanza el fluido. Agua=8, Lava=4. Por defecto: 8

### 🐛 Corrección de Bugs
- Corregido el campo `eat_duration` — ahora se declara en segundos en vez de ticks, consistente con `burn_duration` y `contact_effect_interval`

### 🔧 Cambios Técnicos
- `FluidData.java` — se agregaron `tickRate`, `spreadDistance`, `burnsEntities`, `burnDuration`, `contactEffects`, `contactEffectInterval`
- `FluidRegistry.java` — `buildProps` ahora aplica `tickRate` y `levelDecreasePerBlock` derivado de `spreadDistance`
- `FluidContactHandler.java` — nuevo manejador de eventos usando `PlayerTickEvent.Post` para efectos de contacto y fuego
- `CustomGearMod.java` — registra `FluidContactHandler`
- `ItemData.java` — `eatDuration` cambiado de `int` (ticks) a `float` (segundos)
- `CustomFoodItem.java` — `getUseDuration` ahora convierte segundos a ticks

### 📦 Dependencias

No se agregaron nuevas dependencias.

---

## [1.2.3] - 2026-06-05

### ✨ Nuevas Características

#### Ítems Comestibles (`type: "food"`)
- Nuevo tipo de ítem `food` — ítems consumibles con nutrición, saturación y efectos personalizados
- Valores configurables de `nutrition` y `saturation`
- `always_edible` — permite comer aunque la barra de hambre esté llena (como las manzanas de oro)
- `fast_food` — el ítem se consume más rápido (como el alga seca)
- `eat_duration` — control fino del tiempo de consumo en ticks (0 = instantáneo, 200 = 10 segundos)
- `on_eat_effects` — lista de efectos aplicados al consumir, cada uno con `effect`, `amplifier`, `duration` (en segundos) y `probability`
- Soporte de tooltip via `TooltipHelper.addFoodEffectsTooltip()` mostrando el tiempo de consumo y todos los efectos con duración y nivel
- Se soportan ingredientes de cualquier mod instalado

### 🔧 Cambios Técnicos

- `ItemData.java` — se agregaron los campos `nutrition`, `saturation`, `alwaysEdible`, `fastFood`, `eatDuration`, `onEatEffects` y la clase interna `FoodEffectData`
- `CustomFoodItem.java` — nueva clase que extiende `Item` con `FoodProperties` construidas desde `ItemData`; sobreescribe `getUseDuration` para control de tiempo de consumo; delega el tooltip a `TooltipHelper`
- `ItemRegistry.java` — detecta `type = "food"` y crea `CustomFoodItem` en vez de `CustomItem`
- `UniversalParser.java` — `case "item", "food"` ahora maneja ambos tipos en la misma rama
- `TooltipHelper.java` — se agregó `addFoodEffectsTooltip()` reutilizando los métodos existentes `getEffectName()` y `toRoman()`

### 🐛 Corrección de Bugs

- Corregido el comando `/customgear reload` que usaba la ruta hardcodeada `"customgear"` en vez de `CustomGearMod.MOD_ID` — causaba que todas las texturas volvieran al placeholder magenta/negro después del reload

### 📦 Dependencias

No se agregaron nuevas dependencias.

---

## [1.1.0] - 2026-06-04

### ✨ Nuevas Características

#### Sistema de Recetas Nativo
- CustomGear ahora soporta **recetas de crafteo definidas directamente en los archivos JSON** — sin necesidad de mods externos
- Las recetas se inyectan como datos del servidor a través del pack dinámico, haciéndolas completamente visibles en JEI
- Tipos de receta soportados: `shaped`, `shapeless`, `smelting`, `blasting`, `smithing_transform`
- Los ítems individuales usan el campo `recipe` (objeto o array para múltiples recetas)
- Los sets (armadura, herramienta, arma) usan un mapa `recipes` con una entrada por pieza/herramienta/arma
- Los ingredientes y resultados soportan cualquier ítem de cualquier mod instalado mediante resource location

#### Corrección de Animación de Arco y Ballesta
- Los arcos personalizados ahora animan correctamente los tres frames de tensado al cargar
- Las ballestas personalizadas ahora animan correctamente los estados de carga y cargado
- El tiempo de animación escala correctamente con valores de `charge_speed` personalizados
- Corregido: los overrides no se heredaban del modelo padre — ahora se declaran explícitamente en el JSON generado

#### Renderizado 3D del Escudo
- Los escudos personalizados ahora renderizan su modelo 3D completo en mano e inventario
- Se implementó `CustomShieldBEWLR` — un Block Entity Without Level Renderer dedicado para escudos personalizados
- La animación de bloqueo funciona correctamente con los transforms correctos
- La textura del escudo se lee del atlas vanilla `shield_patterns`

### 🔧 Cambios Técnicos

- `RecipeData.java` — nuevo POJO para deserialización de datos de receta
- `RecipeListDeserializer.java` — deserializador Gson personalizado que permite que el campo `recipe` sea objeto o array
- `RecipeLoader.java` — genera archivos JSON de receta y los inyecta en `DynamicResourcePack` como datos del servidor
- `GearData.java` — se agregaron los campos `recipe` (List) y `recipes` (Map)
- `ItemData.java` — se agregó el campo `recipe`
- `BlockData.java` — se agregó el campo `recipe`
- `GearParser.java` — instancia Gson actualizada para incluir `RecipeListDeserializer`
- `UniversalParser.java` — instancia Gson actualizada para incluir `RecipeListDeserializer`
- `CustomGearMod.java` — pack dinámico registrado para `CLIENT_RESOURCES` y `SERVER_DATA`; se agregó llamada a `RecipeLoader.loadAll()`
- `ClientSetup.java` — registro de propiedad `blocking` del escudo movido dentro de `enqueueWork`; se agregó método `registerShieldProperties()`
- `CustomShieldItem.java` — se implementó `initializeClient()` usando `CustomShieldBEWLR`
- `CustomShieldBEWLR.java` — nueva clase que extiende `BlockEntityWithoutLevelRenderer` para renderizado de escudos
- `TextureLoader.java` — `generateShieldFlatModel()` ahora genera modelos `builtin/entity` con transforms vanilla y override de bloqueo; `generateBowModelWithRef()` y `generateCrossbowModelWithRef()` ahora aceptan mapa de refs para pulling models personalizados opcionales; modelos de escudo y arco/ballesta en modos default/weapon_set corregidos

### 🐛 Corrección de Bugs

- Corregido `SetBonusHandler`: los efectos fantasma ya no persisten después de que `/customgear reload` cambia el ID de un set
- Corregido `GearParser`: faltaba validación de límite inferior para campos de daño de armas
- Corregido `EffectUtils`: se agregó try-catch alrededor de `ResourceLocation.parse()`
- Corregido `ClientSetup`: la propiedad `blocking` del escudo se registraba fuera de `enqueueWork`
- Corregido estado muerto en `SetBonusHandler`: se eliminaron los mapas sin usar `activeSetBonuses` y `activePieceEffects`

### 📦 Dependencias

No se agregaron nuevas dependencias.

---

## [1.0.2] - 2026-04-13

### ✨ Mejoras

- **Soporte Multi-Idioma:** El sistema de nombres de items ahora soporta mejor idiomas complejos
- **Texturas de Armadura en Modo Referencia:** Los sets de armadura ahora pueden referenciar capas de otros mods usando el campo `armor_layers`
- **Actualizaciones en la Documentación:** README actualizado

### 🔧 Cambios Técnicos

- `CustomSwordItem.java` & `CustomArmorItem.java` — priorizar nombres específicos de items sobre los placeholders de formato
- `TextureLoader.java` — se agregó soporte para capas de armadura en modo referencia
- `GearData.java` — se agregó el campo `armor_layers` para configuración de textura
- `DynamicResourcePack.java` — se agregó el método `addReferenceTexture()`

### 📦 Dependencias

No se agregaron nuevas dependencias.

---

## [1.0.1] - 2026-04-13

### ✨ Mejoras

#### Comando `/customgear reload` — Ahora Funcional Completamente
- **Antes:** El comando solo recargaba texturas
- **Ahora:** El comando recarga todos los datos de los items dinámicamente en runtime

### 🔄 Cambios Técnicos

- `CustomSwordItem.java` hasta `CustomArmorItem.java` — se implementó lookup dinámico de datos via `getGearData()`
- `CustomGearCommandHandler.java` — se implementó `updateGearRegistry()`
- `SetBonusHandler.java` — actualizado para usar `getGearDataDirect()`

### 🧹 Limpieza de Código

- `GearData.java` — se eliminaron los campos sin usar `toughness` y `knockbackResistance` a nivel raíz
- `DynamicResourcePack.java` — se mejoró el comentario de `close()`

### 📦 Dependencias

No se agregaron nuevas dependencias.

---

## [1.0.0] - 2026-04-12

### ✨ Release Inicial

- Sistema data-driven basado en JSON
- Soporte para armor sets y tool sets personalizados
- Efectos al sostener y bonificaciones de set
- Texturas personalizadas o de referencia
- Soporte multiidioma
- Comando `/customgear reload` (funcionalidad limitada)

</details>