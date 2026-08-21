# UltimateCustomGear 1.4.0

*NeoForge 1.21.1 · 2026-07-16*

## ⚠️ Important Notes
- `fire_resistant` and armor layer changes are baked at startup — they require a game restart, not `/customgear reload`
- Mob drops ARE hot-reloadable: tune `chance`/`min`/`max`/`entities` live with `/customgear reload`

## ✨ New Features

#### Mob Drops (item economy)
- New `mob_drops` field on items and food — mobs drop your item on death, with configurable `chance` (0–1), `min`/`max` count and entity filter
- `entities` is explicit: `["all"]` for every mob (vanilla and modded), exact IDs (`"minecraft:zombie"`), entity tags (`"#minecraft:undead"`) or mod wildcards (`"mekanism:*"`). Omitting it disables the drop with a log warning
- `requires_player_kill` (default `true`) — mobs killed by the environment drop nothing, so automated farms can't print currency
- Boats, minecarts, and armor stands never drop items; players are always excluded
- Fully hot-reloadable — balance your server economy live
- Items with mob drops show a **"Dropped by"** tooltip section with the source mobs and drop chance (localized entity names, long lists truncated; updates with reload)

### Fire-Resistant Items
- New `fire_resistant` field on items, food, gear (applies to every piece of a set), blocks, and fluids — the dropped item survives fire and lava, like netherite
- On fluids, it protects the filled bucket (yes, vanilla lava buckets burn in lava — yours don't have to)
- Does NOT make the wearer fire-immune (use fire resistance effects for that)

### Transparent Armor
- New `"transparent"` value for `armor_layers` — the armor keeps all stats, effects, and set bonuses but draws nothing on the body. Ideal for stat "accessories" that don't cover the skin
- Works in both texture modes. In reference mode it applies to the whole armor (set both layers); per-layer mixing is only available in custom mode

## 🐛 Bug Fixes
- Fixed custom-mode gear textures (armor pieces, armor layers, tools, weapons, bow pulling frames) resolving against the mod's old folder name and never loading from content pack zips

## 🔧 Technical Changes
- `MobDropHandler.java` — new: `LivingDropsEvent` handler reading `ITEM_MAP` at event time (hot-reloadable); supports `all`, `#entity_tags` and `mod:*` wildcards; excludes players and armor stands
- `ItemData.java` — new `mob_drops` (`MobDropsData`) and `fire_resistant` fields; `GearData`, `BlockData`, `FluidData` — new `fire_resistant` field
- All `Custom*Item` classes — properties construction extracted to `buildProps` helpers applying `fireResistant()`; `BlockRegistry` (`blockItemProps`) and `FluidRegistry` (`bucketProps`) apply it to BlockItems and buckets
- `GearModelGenerator.java` — removed the stale `GEAR_FOLDER` constant; custom gear textures resolve through `TextureLoader.resolveUserResource` (content roots); injects a generated fully-transparent PNG for `"transparent"` layers
- `CustomArmorItem.java` — `buildLayers` intercepts `"transparent"` and points the armor material at the injected transparent texture

## 📦 Dependencies
No new dependencies added.

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

## ⚠️ Notas Importantes
- `fire_resistant` y los cambios de capas de armadura se fijan al arrancar — requieren reiniciar el juego, no `/customgear reload`
- Los drops de mobs SÍ son recargables en caliente: ajusta `chance`/`min`/`max`/`entities` en vivo con `/customgear reload`

## ✨ Nuevas Características

### Drops de Mobs (economía de ítems)
- Nuevo campo `mob_drops` en ítems y comida — los mobs sueltan tu ítem al morir, con `chance` configurable (0-1), cantidad `min`/`max` y filtro de entidades
- `entities` es explícito: `["all"]` para todos los mobs (vanilla y de mods), ID exactos (`"minecraft:zombie"`), tags de entidad (`"#minecraft:undead"`) o comodines de mod (`"mekanism:*"`). Omitirlo desactiva el drop con un aviso en el log
- `requires_player_kill` (por defecto `true`) — los mobs muertos por el entorno no sueltan nada, así las granjas automáticas no imprimen dinero
- Los barcos, vagonetas y armor stands nunca sueltan ítems; los jugadores siempre están excluidos
- Totalmente recargable en caliente — balancea la economía de tu servidor en vivo
- Los ítems con drops de mobs muestran una sección **"Lo sueltan:"** en el tooltip con los mobs de origen y la probabilidad (nombres de entidad traducidos, listas largas recortadas; se actualiza con el reload)

### Ítems Resistentes al Fuego
- Nuevo campo `fire_resistant` en ítems, comida, gear (aplica a todas las piezas del set), bloques y fluidos — el ítem tirado sobrevive al fuego y la lava, como la netherita
- En fluidos protege la cubeta llena (sí, las cubetas de lava de vanilla se queman en la lava — las tuyas no tienen por qué)
- NO hace al portador inmune al fuego (usa efectos de resistencia al fuego para eso)

### Armadura Transparente
- Nuevo valor `"transparent"` para `armor_layers` — la armadura conserva stats, efectos y bonos de conjunto, pero no se dibuja sobre el cuerpo. Ideal para "accesorios" con stats que no tapan el skin
- Funciona en ambos modos de textura. En modo reference aplica a la armadura completa (pon ambas capas); la mezcla por capa solo está disponible en modo custom

## 🐛 Correcciones
- Arregladas las texturas de gear en modo custom (piezas de armadura, capas, herramientas, armas, frames de tensado de arcos) que se resolvían contra el nombre viejo de la carpeta del mod y nunca cargaban desde los zips de packs de contenido

## 🔧 Cambios Técnicos
- `MobDropHandler.java` — nuevo: handler de `LivingDropsEvent` que lee `ITEM_MAP` en el momento del evento (recargable en caliente); soporta comodines `all`, `#entity_tags` y `mod:*`; excluye jugadores y estandartes de armadura
- `ItemData.java` — nuevos campos `mob_drops` (`MobDropsData`) y `fire_resistant`; `GearData`, `BlockData`, `FluidData` — nuevo campo `fire_resistant`
- Todas las clases `Custom*Item` — la construcción de propiedades se extrajo a ayudas `buildProps` aplicando `fireResistant()`; `BlockRegistry` (`blockItemProps`) y `FluidRegistry` (`bucketProps`) lo aplican a BlockItems y cubos
- `GearModelGenerator.java` — eliminada la constante obsoleta `GEAR_FOLDER`; texturas de equipo personalizadas se resuelven mediante `TextureLoader.resolveUserResource` (raíces de contenido); inyecta un PNG totalmente transparente generado para capas "transparent"
- `CustomArmorItem.java` — `buildLayers` intercepta "transparent" y apunta el material de la armadura a la textura transparente inyectada

## 📦 Dependencias
No se agregaron dependencias nuevas.

</details>