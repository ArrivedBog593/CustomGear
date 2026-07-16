# Changelog

Todas las notas de cambios importantes para el proyecto UltimateCustomGear.

## [1.4.0] - 2026-07-16

### ⚠️ Notas Importantes
- `fire_resistant` y los cambios de capas de armadura se fijan al arrancar — requieren reiniciar el juego, no `/customgear reload`
- Los drops de mobs SÍ son recargables en caliente: ajusta `chance`/`min`/`max`/`entities` en vivo con `/customgear reload`

### ✨ Nuevas Características

#### Drops de Mobs (economía de ítems)
- Nuevo campo `mob_drops` en ítems y comida — los mobs sueltan tu ítem al morir, con `chance` configurable (0-1), cantidad `min`/`max` y filtro de entidades
- `entities` es explícito: `["all"]` para todos los mobs (vanilla y de mods), ID exactos (`"minecraft:zombie"`), tags de entidad (`"#minecraft:undead"`) o comodines de mod (`"mekanism:*"`). Omitirlo desactiva el drop con un aviso en el log
- `requires_player_kill` (por defecto `true`) — los mobs muertos por el entorno no sueltan nada, así las granjas automáticas no imprimen dinero
- Los barcos, vagonetas y armor stands nunca sueltan ítems; los jugadores siempre están excluidos
- Totalmente recargable en caliente — balancea la economía de tu servidor en vivo
- Los ítems con drops de mobs muestran una sección **"Lo sueltan:"** en el tooltip con los mobs de origen y la probabilidad (nombres de entidad traducidos, listas largas recortadas; se actualiza con el reload)

#### Ítems Resistentes al Fuego
- Nuevo campo `fire_resistant` en ítems, comida, gear (aplica a todas las piezas del set), bloques y fluidos — el ítem tirado sobrevive al fuego y la lava, como la netherita
- En fluidos protege la cubeta llena (sí, las cubetas de lava de vanilla se queman en la lava — las tuyas no tienen por qué)
- NO hace al portador inmune al fuego (usa efectos de resistencia al fuego para eso)

#### Armadura Transparente
- Nuevo valor `"transparent"` para `armor_layers` — la armadura conserva stats, efectos y bonos de conjunto, pero no se dibuja sobre el cuerpo. Ideal para "accesorios" con stats que no tapan el skin
- Funciona en ambos modos de textura. En modo reference aplica a la armadura completa (pon ambas capas); la mezcla por capa solo está disponible en modo custom

### 🐛 Correcciones
- Arregladas las texturas de gear en modo custom (piezas de armadura, capas, herramientas, armas, frames de tensado de arcos) que se resolvían contra el nombre viejo de la carpeta del mod y nunca cargaban desde los zips de packs de contenido

### 🔧 Cambios Técnicos
(mismas clases que la lista en inglés: `MobDropHandler` nuevo, campos en las 4 clases de datos, helpers `buildProps` en todas las clases `Custom*Item` + `BlockRegistry`/`FluidRegistry`, `GearModelGenerator` sin `GEAR_FOLDER` y con PNG transparente, intercept en `CustomArmorItem.buildLayers`)

### 📦 Dependencias
No se agregaron dependencias nuevas.

## [1.3.1] - 2026-07-05

### ✨ Nuevas Características

#### Tags de Ítems / Bloques / Fluidos
- Nuevo campo `tags` en ítems, comida, bloques y fluidos — declara a qué tags pertenece tu contenido (sin prefijo `#`), p. ej. `"tags": ["c:ingots", "c:ingots/ruby"]`
- Hace que tu contenido sea usable en recetas de otros mods (y las tuyas) que acepten esos tags: un bloque con el tag `minecraft:planks` funciona donde se aceptan tablas; un ítem con `c:ingots` es reconocido por cualquier mod que use ese tag
- Los bloques se agregan a los registries de tags de bloque y de ítem (para que cuenten tanto el bloque como su forma de ítem); los fluidos etiquetan el fluido y su cubeta
- Los tags se fusionan con los de vanilla u otros mods del mismo nombre (`"replace": false`); también puedes inventar tus propios tags (`customgear:magic_gems`)
- No disponible para sets de armadura/herramientas/armas en esta versión (sus ID por pieza necesitan un esquema aparte — planeado)

#### Claves de textura unificadas para bloques
- `refs` ahora maneja todos los casos de textura de bloque: textura única (`all`) o por cara (`top`/`bottom`/`north`/`south`/`east`/`west`/`side`)
- `all` es la clave canónica para textura única; `block` se mantiene como alias legacy
- `faces` sigue funcionando para texturas por cara (alias legacy)

### 🐛 Correcciones
- Arreglado que las texturas de bloque en modo custom rechazaran la clave `all`/`block` (antes el modo solo aceptaba la ruta por cara)
- Arreglado que las texturas custom de bloques no cargaran desde los zips de packs de contenido — se resolvían contra una ruta de carpeta vieja fija en vez de las raíces de contenido (carpeta suelta + zips). Las texturas custom de bloque/cara ahora cargan desde zips como todo lo demás
- Arreglado que los fluidos custom colocados mostraran una key de traducción cruda en Jade/WAILA en vez de su nombre (faltaba la key del nombre del bloque del fluido)
- Arreglado no poder colocar bloques dentro de un fluido custom para taparlo o quitarlo — los bloques de fluido ahora son `replaceable` y están marcados como líquido, como el agua/lava

### 🔧 Cambios Técnicos
- `ItemTagLoader.java` — nuevo: genera los archivos de tags de ítem/bloque/fluido desde el campo `tags` y los inyecta en el pack dinámico (rutea los bloques a los registries de bloque+ítem, los fluidos a fluido+cubeta; rutas de tags singulares de 1.21, `"replace": false`); se llama al arrancar y en el reload
- `ItemData.java`, `BlockData.java`, `FluidData.java` — nuevo campo `tags` (`List<String>`)
- `BlockModelGenerator.java` — resolución unificada de claves de textura: `allRef()` (acepta `all`/`block`) y `resolveFaces()` (lee claves de cara desde `refs` o el objeto `faces`); las texturas custom ahora se resuelven a través de `TextureLoader.resolveUserResource` (raíces de contenido), arreglando la ruta fija `./ultimatecustomgear` que ignoraba los zips de packs
- `LangGenerator.java` — emite `block.customgear.<id>` para los fluidos (nombre en Jade/WAILA/F3)
- `CustomFluid.java` — las propiedades del bloque de fluido ahora incluyen `.replaceable()` y `.liquid()`
- `CustomGearCommandHandler.java` — el reload ahora también regenera los tags de bloque, las loot tables de bloque y los tags de ítem (todos los loaders que inyectan al pack deben correr en el reload)

### 📦 Dependencias
No se agregaron dependencias nuevas.

---

## [1.3.0] - 2026-07-04

### ⚠️ Notas Importantes
- **Servidores y clientes deben actualizar ambos a 1.3.0** — el protocolo de red cambió, así que los clientes con versiones anteriores no pueden entrar a servidores actualizados (reciben un mensaje claro de incompatibilidad).
- Los iconos de efectos al sostener/set/pieza ahora muestran un contador de ~12s que se renueva solo, en vez de ∞. Es intencional (ver Rediseño de Efectos abajo).
- Los azadones de área consumen 1 de durabilidad por bloque arado (p. ej. radio 3 = hasta 49 de durabilidad por uso).

### ✨ Nuevas Características

#### Bloques — Herramienta Requerida y Nivel de Cosecha
- Nuevo campo `required_tool` — qué herramienta mina el bloque: `sword`, `pickaxe`, `axe`, `shovel`, `hoe` o `none` (por defecto)
- `required_tool: "sword"` hace que el bloque se mine más rápido con cualquier espada (como las hojas) — nota: las espadas aceleran el minado, pero no condicionan los drops, y `harvest_level` no aplica con ellas
- Nuevo campo `harvest_level` — nivel de herramienta requerido para obtener drops: 0 (madera), 1 (piedra), 2 (hierro), 3 (diamante), 4 (netherite)
- `required_tool` solo otorga velocidad de minado (como la arena con la pala); `harvest_level` ≥ 1 es lo que condiciona los drops a la herramienta del nivel correcto, como las menas de vanilla
- Implementado mediante tags de bloques de vanilla inyectados como datos de servidor — totalmente compatible con herramientas de otros mods que sigan los niveles de vanilla

#### Packs de Contenido (.zip)
- El contenido ahora puede distribuirse como **archivos .zip** colocados en `.minecraft/ultimatecustomgear/packs/` — los JSON y texturas de adentro cargan exactamente igual que los archivos sueltos
- Los archivos sueltos tienen prioridad sobre los zips: puedes sobreescribir localmente un ítem específico de un pack poniendo tu propia versión suelta
- Solo se admite `.zip`; los `.rar`/`.7z` y los archivos dejados en la carpeta raíz generan mensajes en el log explicando cómo corregirlo
- `/customgear reload` detecta zips agregados o actualizados sin reiniciar

#### Verificación de Contenido en Multijugador
- Al conectarse un cliente, el servidor verifica que haya cargado los mismos JSON de contenido (comparados por hash — el empaque no importa: zip vs. archivos sueltos con el mismo contenido coinciden)
- Nueva config `content_handshake_mode` en `ultimatecustomgear-common.toml`:
    - `ENFORCE` (por defecto) — los clientes con diferencias son desconectados con un mensaje que muestra ambos hashes y cómo corregirlo
    - `WARN` — los clientes con diferencias pueden entrar con un aviso en el chat; el servidor registra la diferencia
    - `OFF` — la verificación se omite por completo
- Solo importa el valor del servidor — los clientes obedecen lo que el servidor indica
- El pack dinámico ahora anuncia una versión derivada de su contenido en la negociación de packs de vanilla: si las recetas de cliente y servidor difieren, se envían y usan las del servidor (adiós a las "recetas fantasma" silenciosas)

#### Tags como Ingredientes en Recetas
- Cualquier casilla de ingrediente acepta un **tag** con el prefijo `#`, p. ej. `"#minecraft:planks"` (cualquier tabla) o `"#c:ingots/iron"` (lingotes de hierro de cualquier mod) — funciona en shaped, shapeless, smelting, blasting y smithing

#### Validación Completa de Recetas
- Las recetas shaped ahora se validan por completo con mensajes claros: longitud de filas (1-3), filas desiguales, símbolos del patrón sin definir en `key`, entradas de `key` sin usar, e ID de ítem/tag malformados
- Las recetas shapeless con más de 9 ingredientes se rechazan con mensaje

### 🐛 Correcciones
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

### ♻️ Cambios de Comportamiento
- **Rediseño de Efectos**: los efectos al sostener, bonos de set y efectos por pieza usan duraciones cortas renovables (estilo beacon) en vez de infinitas. Una poción real del mismo efecto siempre gana mientras dure; los efectos expiran solos en segundos si se pierde el rastreo
- Si un jugador toca dos fluidos custom a la vez, solo aplica el dominante (el más profundo) — antes aplicaban ambos
- `/customgear reload` ahora dispara la recarga de datapacks él mismo: las recetas se actualizan de inmediato sin ejecutar `/reload`. Los cambios de textura siguen requiriendo F3+T; los stats base siguen requiriendo reiniciar

### 🔧 Cambios Técnicos
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

### 📦 Dependencias
No se agregaron dependencias nuevas.

---

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