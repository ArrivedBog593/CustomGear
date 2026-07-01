# Changelog

Todas las notas de cambios importantes para el proyecto UltimateCustomGear.

## [1.2.7] - 2026-06-29

### ✨ Nuevas Características

#### Bloques — Gravedad
- Nuevo campo `gravity: true` hace que el bloque caiga cuando no tiene soporte, como la arena o la grava
- No es compatible con `directional`

#### Bloques — Nuevas Propiedades Configurables
- `destroy_time` — tiempo en segundos para romper el bloque con la herramienta correcta (por defecto: 3.0)
- `explosion_resistance` — resistencia a explosiones (por defecto: 3.0, obsidiana: 1200.0)
- `sound` — tipo de sonido al colocar, romper o caminar sobre el bloque (por defecto: `stone`)
- Más de 100 tipos de sonido vanilla soportados: `wood`, `gravel`, `sand`, `deepslate`, `amethyst`, `copper`, `sculk`, `netherite`, etc.
- `map_color` — color del bloque en el mapa (por defecto: `none`); soporta colores básicos, tintes y colores especiales como `gold`, `diamond`, `lapis`, `emerald`, `podzol` y `nether`

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

## [1.2.6] - 2026-06-28

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