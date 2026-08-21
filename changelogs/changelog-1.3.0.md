# UltimateCustomGear 1.3.0

*NeoForge 1.21.1 · 2026-07-04*

## ⚠️ Important Notes
- **Servers and clients must both update to 1.3.0** — the network protocol changed, so older clients cannot join updated servers (they get a clear version-mismatch message).
- Held/set/piece effect icons now show a self-renewing ~12s timer instead of ∞. This is intentional (see Effects Rework below).
- Area-tilling hoes consume 1 durability per tilled block (e.g., radius 3 = up to 49 durability per use).

## ✨ New Features

### Blocks — Required Tool & Harvest Level
- New `required_tool` field — which tool mines the block: `sword`, `pickaxe`, `axe`, `shovel`, `hoe` or `none` (default)
- `required_tool: "sword"` makes the block mine faster with any sword (like leaves) — note: swords speed up mining but don't gate drops, and `harvest_level` doesn't apply to them
- New `harvest_level` field — tool tier required to get drops: 0 (wood), 1 (stone), 2 (iron), 3 (diamond), 4 (netherite)
- `required_tool` grants mining speed only (like sand + shovel); `harvest_level` ≥ 1 is what gates drops behind the correct tool tier, like vanilla ore
- Implemented via vanilla block tags injected as server data — fully compatible with modded tools that follow vanilla tiers

### Content Packs (.zip)
- Content can now be distributed as **.zip files** dropped into `.minecraft/ultimatecustomgear/packs/` — JSONs and textures inside load exactly like loose files
- Loose files take precedence over zips: you can locally override a single item from a pack by placing your own version loose
- Only `.zip` is supported; `.rar`/`.7z` files and archives left in the root folder produce log messages explaining how to fix it
- `/customgear reload` picks up added or updated zips without restarting

### Multiplayer Content Verification
- When a client joins, the server verifies the client loaded the same content JSONs (compared via content hash — packaging doesn't matter: zip vs. loose files with the same content match)
- New config `content_handshake_mode` in `ultimatecustomgear-common.toml`:
    - `ENFORCE` (default) — mismatching clients are disconnected with a message showing both hashes and how to fix it
    - `WARN` — mismatching clients are allowed in with a chat warning; the server logs the mismatch
    - `OFF` — the check is skipped entirely
- Only the server's setting matters — clients obey what the server sends
- The dynamic pack now advertises a content-derived version to vanilla's pack negotiation: if client and server recipes differ, the server's recipes are sent and used (no more silent "ghost recipe" desyncs)

### Recipe Tag Ingredients
- Any ingredient slot accepts a **tag** with the `#` prefix, e.g. `"#minecraft:planks"` (any plank) or `"#c:ingots/iron"` (iron ingots from any mod) — works in shaped, shapeless, smelting, blasting, and smithing

### Full Recipe Validation
- Shaped recipes are now fully validated with clear log messages: row length (1–3), uneven rows, pattern symbols missing from `key`, unused `key` entries, and malformed item/tag IDs
- Shapeless recipes with more than 9 ingredients are rejected with a message

## 🐛 Bug Fixes
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

## ♻️ Behavior Changes
- **Effects Rework**: held effects, set bonuses, and piece effects use beacon-style refreshed short durations instead of infinite ones. A real potion of the same effect always wins while it lasts; effects self-expire within seconds if tracking is lost
- If a player overlaps two custom fluids at once, only the dominant (deepest) one applies its effects — previously both did
- `/customgear reload` now triggers the data-pack reload itself: recipes update immediately without running `/reload`. Texture changes still require pressing F3+T; base stats still require a restart

## 🔧 Technical Changes
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

## 📦 Dependencies
No new dependencies were added.

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

## ⚠️ Notas Importantes
- **Servidores y clientes deben actualizar ambos a 1.3.0** — el protocolo de red cambió, así que los clientes con versiones anteriores no pueden entrar a servidores actualizados (reciben un mensaje claro de incompatibilidad).
- Los iconos de efectos al sostener/set/pieza ahora muestran un contador de ~12s que se renueva solo, en vez de ∞. Es intencional (ver Rediseño de Efectos abajo).
- Los azadones de área consumen 1 de durabilidad por bloque arado (p. ej. radio 3 = hasta 49 de durabilidad por uso).

## ✨ Nuevas Características

### Bloques — Herramienta Requerida y Nivel de Cosecha
- Nuevo campo `required_tool` — qué herramienta mina el bloque: `sword`, `pickaxe`, `axe`, `shovel`, `hoe` o `none` (por defecto)
- `required_tool: "sword"` hace que el bloque se mine más rápido con cualquier espada (como las hojas) — nota: las espadas aceleran el minado, pero no condicionan los drops, y `harvest_level` no aplica con ellas
- Nuevo campo `harvest_level` — nivel de herramienta requerido para obtener drops: 0 (madera), 1 (piedra), 2 (hierro), 3 (diamante), 4 (netherite)
- `required_tool` solo otorga velocidad de minado (como la arena con la pala); `harvest_level` ≥ 1 es lo que condiciona los drops a la herramienta del nivel correcto, como las menas de vanilla
- Implementado mediante tags de bloques de vanilla inyectados como datos de servidor — totalmente compatible con herramientas de otros mods que sigan los niveles de vanilla

### Packs de Contenido (.zip)
- El contenido ahora puede distribuirse como **archivos .zip** colocados en `.minecraft/ultimatecustomgear/packs/` — los JSON y texturas de adentro cargan exactamente igual que los archivos sueltos
- Los archivos sueltos tienen prioridad sobre los zips: puedes sobreescribir localmente un ítem específico de un pack poniendo tu propia versión suelta
- Solo se admite `.zip`; los `.rar`/`.7z` y los archivos dejados en la carpeta raíz generan mensajes en el log explicando cómo corregirlo
- `/customgear reload` detecta zips agregados o actualizados sin reiniciar

### Verificación de Contenido en Multijugador
- Al conectarse un cliente, el servidor verifica que haya cargado los mismos JSON de contenido (comparados por hash — el empaque no importa: zip vs. archivos sueltos con el mismo contenido coinciden)
- Nueva config `content_handshake_mode` en `ultimatecustomgear-common.toml`:
    - `ENFORCE` (por defecto) — los clientes con diferencias son desconectados con un mensaje que muestra ambos hashes y cómo corregirlo
    - `WARN` — los clientes con diferencias pueden entrar con un aviso en el chat; el servidor registra la diferencia
    - `OFF` — la verificación se omite por completo
- Solo importa el valor del servidor — los clientes obedecen lo que el servidor indica
- El pack dinámico ahora anuncia una versión derivada de su contenido en la negociación de packs de vanilla: si las recetas de cliente y servidor difieren, se envían y usan las del servidor (adiós a las "recetas fantasma" silenciosas)

### Tags como Ingredientes en Recetas
- Cualquier casilla de ingrediente acepta un **tag** con el prefijo `#`, p. ej. `"#minecraft:planks"` (cualquier tabla) o `"#c:ingots/iron"` (lingotes de hierro de cualquier mod) — funciona en shaped, shapeless, smelting, blasting y smithing

### Validación Completa de Recetas
- Las recetas shaped ahora se validan por completo con mensajes claros: longitud de filas (1-3), filas desiguales, símbolos del patrón sin definir en `key`, entradas de `key` sin usar, e ID de ítem/tag malformados
- Las recetas shapeless con más de 9 ingredientes se rechazan con mensaje

## 🐛 Correcciones
- Arreglados los **crashes de arranque por colisiones de ID entre tipos de contenido** (p. ej. un ítem y un bloque con el mismo id, un ítem que choca con un nombre derivado de set como `myset_sword`, o con el ítem `_bucket` de un fluido). Las entradas en conflicto ahora se descartan con un log que nombra a ambos dueños
- Arreglada la **desaparición de todas las recetas del mod tras `/reload`** — el comando de recarga no regeneraba las recetas en el pack dinámico
- Arreglado que los **efectos de daño en el tiempo (veneno, wither) nunca dañaran estando dentro de un fluido** — la reaplicación reiniciaba el efecto antes de su tick de daño
- Arreglado que las pociones reales se borraran al cambiar de ítem cuando el ítem otorgaba el mismo efecto
- Arreglados los efectos "fantasma" permanentes tras desconectarse sosteniendo un ítem con efectos
- Arreglado el doble registro del pack dinámico por escaneo
- Arreglada la resolución de texturas custom contra el nombre viejo de la carpeta del mod (`customgear/` en vez de `ultimatecustomgear/`)
- Arreglado que el contenido no cargara con launchers de working directory no estándar (la carpeta de config se resuelve contra el directorio real del juego)
- Arreglada la fuga de memoria y los contadores obsoletos del contacto con fluidos tras morir o cambiar de dimensión (ahora con UUID del jugador y limpieza al desconectar)
- Los azadones de área ahora se detienen al romperse a mitad del área y siempre aran primero el bloque clicado
- Arreglado que `burns_entities` y `contact_effects` solo afectaran a jugadores — los fluidos ahora afectan a **todas las entidades** (mobs, ítems tirados, proyectiles) como la lava de vanilla. Los ítems tirados en un fluido ardiente se destruyen; los mobs inmunes al fuego (blazes, etc.) no se ven afectados
- Arreglado que los bloques custom nunca suelten ítems en modo supervivencia — `requiresCorrectToolForDrops` se aplicaba incondicionalmente a todos los bloques, incluso aquellos sin requisito de herramienta
- Arreglado que los bloques custom nunca se soltaran a sí mismos al romperse — los bloques no tenían loot tables (en Minecraft, un bloque sin loot table no suelta nada). Cada bloque custom ahora recibe una loot table estándar de auto-drop con la condición de explosión de vanilla

## ♻️ Cambios de Comportamiento
- **Rediseño de Efectos**: los efectos al sostener, bonos de set y efectos por pieza usan duraciones cortas renovables (estilo beacon) en vez de infinitas. Una poción real del mismo efecto siempre gana mientras dure; los efectos expiran solos en segundos si se pierde el rastreo
- Si un jugador toca dos fluidos custom a la vez, solo aplica el dominante (el más profundo) — antes aplicaban ambos
- `/customgear reload` ahora dispara la recarga de datapacks él mismo: las recetas se actualizan de inmediato sin ejecutar `/reload`. Los cambios de textura siguen requiriendo F3+T; los stats base siguen requiriendo reiniciar

## 🔧 Cambios Técnicos
- `ContentRoots.java` — nuevo: monta la carpeta suelta más cada zip en `packs/` como raíces de contenido uniformes (orden alfabético determinista)
- `GlobalIdValidator.java` — nuevo: válida todos los ID finales derivados en los registros de ítems/bloques/fluidos antes del registro, con reclamos atómicos por entrada
- `ContentHasher.java` — nuevo: hash canónico (clave-ordenada, independiente de espacios/packaging) de todos los JSON de contenido en todas las raíces, capturado en el momento de la carga
- `CustomGearNetworking.java`, `HashCheckPayload.java`, `HashCheckAckPayload.java` — nuevo: handshake en la fase de configuración con la aplicación de reglas decidida por el servidor viajando en la carga; versión de protocolo "2"
- `CustomGearConfig.java` — nuevo: configuración COMÚN con `content_handshake_mode`
- `GearParser.java` / `UniversalParser.java` — recorre todas las raíces de contenido; claves en caché con prefijo por raíz (`packs/foo.zip!path`)
- `TextureLoader.java` — recursos personalizados resueltos a través de la sesión de contenido (arregla la ruta antigua `./customgear`)
- `RecipeLoader.java` — soporte de etiquetas de ingredientes, validación estructural completa, rechazo atómico por receta
- `EffectUtils.java` — duraciones refrescadas cada 240 ticks (iconos HUD estables), eliminación segura de pociones
- `SetBonusHandler.java` — aplicar/quitar basado en diferencias en lugar de quitar todo/reaplicar todo
- `CustomHoeItem.java` — revisión de durabilidad en el bucle de área, centrado primero
- `DynamicResourcePack.java` — sobrescritura de `location()` incluyendo un hash de contenido `KnownPack`; `ConcurrentHashMap`; hash en caché invalidado al mutar
- `CustomGearMod.java` / `CustomGearCommandHandler.java` — sesión de contenido (try-with-resources) alrededor de load/reload; recaptura del hash al recargar; recarga automática del data-pack después de `/customgear reload`
- `CustomLiquidBlock.java` — nuevo: aplica efectos de fuego y contacto a cada entidad mediante `entityInside` (estilo lava de vanilla), sin estado, con renovación de efectos amigable con DoT; reemplaza al `FluidContactHandler` eliminado
- `CustomFluid.java` — `createBlock` ahora crea un `CustomLiquidBlock`
- `BlockTagLoader.java` — nuevo: genera etiquetas `minecraft:tags/block/mineable/*` y `needs_*_tool` en el paquete dinámico (rutas de etiquetas singulares 1.21); se llama al iniciar y al recargar
- `CustomBlock.java` — `buildProperties` aplica `requiresCorrectToolForDrops()` solo cuando `harvest_level` ≥ 1 (`required_tool` por sí solo nunca limita los drops)
- `BlockLootLoader.java` — nuevo: genera tablas de botín de caída propia en el paquete dinámico; se llama al inicio y al recargar

## 📦 Dependencias
No se agregaron dependencias nuevas.

</details>