# UltimateCustomGear 1.3.1

*NeoForge 1.21.1 · 2026-07-05*

## ✨ New Features

### Item / Block / Fluid Tags
- New `tags` field on items, food, blocks, and fluids — declare which tags your content belongs to (no `#` prefix), e.g. `"tags": ["c:ingots", "c:ingots/ruby"]`
- Makes your content usable in other mods' recipes (and your own) that accept those tags: a block tagged `minecraft:planks` works anywhere planks are accepted; an item tagged `c:ingots` is recognized by any mod using that tag
- Blocks are added to both the block and item tag registries (so the block and its item form both count); fluids tag the fluid and their bucket item
- Tags merge with vanilla/other-mod tags of the same name (`"replace": false`); you can also invent your own tags (`customgear:magic_gems`)
- Not available for armor/tool/weapon sets in this version (their per-piece IDs need a separate schema — planned)

### Unified texture keys for blocks
- `refs` now handles every block texture case: single texture (`all`) or per-face (`top`/`bottom`/`north`/`south`/`east`/`west`/`side`)
- `all` is the canonical single-texture key; `block` is kept as a legacy alias
- `faces` still works for per-face textures (legacy alias)

## 🐛 Bug Fixes
- Fixed custom-mode block textures rejecting the `all`/`block` key (the mode only accepted the per-face path before)
- Fixed custom block textures not loading from content pack zips — they resolved against a hardcoded old folder path instead of the content roots (loose folder + zips). Custom block/face textures now load from zips like everything else
- Fixed placed custom fluids showing a raw translation key in Jade/WAILA instead of their name (the fluid block's name key was missing)
- Fixed being unable to place blocks into a custom fluid to fill or remove it — fluid blocks are now `replaceable` and flagged as liquid, like water/lava

## 🔧 Technical Changes
- `ItemTagLoader.java` — new: generates item/block/fluid tag files from the `tags` field and injects them into the dynamic pack (routes blocks to both block+item registries, fluids to fluid+bucket; 1.21 singular tag paths, `"replace": false`); called at startup and on reload
- `ItemData.java`, `BlockData.java`, `FluidData.java` — new `tags` field (`List<String>`)
- `BlockModelGenerator.java` — unified texture key resolution: `allRef()` (accepts `all`/`block`) and `resolveFaces()` (reads face keys from `refs` or the `faces` object); custom textures now resolve through `TextureLoader.resolveUserResource` (content roots), fixing the hardcoded `./ultimatecustomgear` path that ignored pack zips
- `LangGenerator.java` — emits `block.customgear.<id>` for fluids (Jade/WAILA/F3 name)
- `CustomFluid.java` — fluid block properties now include `.replaceable()` and `.liquid()`
- `CustomGearCommandHandler.java` — reload now also regenerates block tags, block loot tables, and item tags (all pack-injected loaders must run on reload)

## 📦 Dependencies
No new dependencies added.

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

## ✨ Nuevas Características

### Tags de Ítems / Bloques / Fluidos
- Nuevo campo `tags` en ítems, comida, bloques y fluidos — declara a qué tags pertenece tu contenido (sin prefijo `#`), p. ej. `"tags": ["c:ingots", "c:ingots/ruby"]`
- Hace que tu contenido sea usable en recetas de otros mods (y las tuyas) que acepten esos tags: un bloque con el tag `minecraft:planks` funciona donde se aceptan tablas; un ítem con `c:ingots` es reconocido por cualquier mod que use ese tag
- Los bloques se agregan a los registries de tags de bloque y de ítem (para que cuenten tanto el bloque como su forma de ítem); los fluidos etiquetan el fluido y su cubeta
- Los tags se fusionan con los de vanilla u otros mods del mismo nombre (`"replace": false`); también puedes inventar tus propios tags (`customgear:magic_gems`)
- No disponible para sets de armadura/herramientas/armas en esta versión (sus ID por pieza necesitan un esquema aparte — planeado)

### Claves de textura unificadas para bloques
- `refs` ahora maneja todos los casos de textura de bloque: textura única (`all`) o por cara (`top`/`bottom`/`north`/`south`/`east`/`west`/`side`)
- `all` es la clave canónica para textura única; `block` se mantiene como alias legacy
- `faces` sigue funcionando para texturas por cara (alias legacy)

## 🐛 Correcciones
- Arreglado que las texturas de bloque en modo custom rechazaran la clave `all`/`block` (antes el modo solo aceptaba la ruta por cara)
- Arreglado que las texturas custom de bloques no cargaran desde los zips de packs de contenido — se resolvían contra una ruta de carpeta vieja fija en vez de las raíces de contenido (carpeta suelta + zips). Las texturas custom de bloque/cara ahora cargan desde zips como todo lo demás
- Arreglado que los fluidos custom colocados mostraran una key de traducción cruda en Jade/WAILA en vez de su nombre (faltaba la key del nombre del bloque del fluido)
- Arreglado no poder colocar bloques dentro de un fluido custom para taparlo o quitarlo — los bloques de fluido ahora son `replaceable` y están marcados como líquido, como el agua/lava

## 🔧 Cambios Técnicos
- `ItemTagLoader.java` — nuevo: genera los archivos de tags de ítem/bloque/fluido desde el campo `tags` y los inyecta en el pack dinámico (rutea los bloques a los registries de bloque+ítem, los fluidos a fluido+cubeta; rutas de tags singulares de 1.21, `"replace": false`); se llama al arrancar y en el reload
- `ItemData.java`, `BlockData.java`, `FluidData.java` — nuevo campo `tags` (`List<String>`)
- `BlockModelGenerator.java` — resolución unificada de claves de textura: `allRef()` (acepta `all`/`block`) y `resolveFaces()` (lee claves de cara desde `refs` o el objeto `faces`); las texturas custom ahora se resuelven a través de `TextureLoader.resolveUserResource` (raíces de contenido), arreglando la ruta fija `./ultimatecustomgear` que ignoraba los zips de packs
- `LangGenerator.java` — emite `block.customgear.<id>` para los fluidos (nombre en Jade/WAILA/F3)
- `CustomFluid.java` — las propiedades del bloque de fluido ahora incluyen `.replaceable()` y `.liquid()`
- `CustomGearCommandHandler.java` — el reload ahora también regenera los tags de bloque, las loot tables de bloque y los tags de ítem (todos los loaders que inyectan al pack deben correr en el reload)

## 📦 Dependencias
No se agregaron dependencias nuevas.

</details>