# UltimateCustomGear 2.0.1

*NeoForge 26.1.2 · 2026-08-23*

> **Fixes for the 26.1.2 port.** Eight things the port broke or brought with it, five of which stopped content from working at all.

## ⚠️ Important Notes

**Your content files load unchanged**, references included. Chest and shulker textures accept the folder path as well as the sprite name, `armor_layers` accepts both the old layer texture and the new equipment asset, and `armor_3d` accepts the file path as well as the id GeckoLib caches it under. Where a form changed, both are read.

- **One thing is outside this mod's control: GeckoLib moved its models.** What lived under `geo/` in a 1.21.1 jar is under `geckolib/models/` now, so a reference into another mod's assets needs that segment updated — `"othermod:geckolib/models/armor/their.geo.json"`, or the short `"othermod:armor/their"`. Both work. `texture` is unaffected and keeps its full path with `.png`
- Double chests saved before this version have their contents split across both halves. Opening one shows the same items, in different slots; nothing was lost

## 🐛 Bug Fixes

- **The game crashed on startup unless every piece of gear declared `enchantability`.** The field is optional, so omitting it is the ordinary case: it arrived as `0`, and both material types now reject a value that is not positive. A material's value only grades the offers — whether gear can be enchanted at all is still the `enchantable` flag
- **Custom blocks and containers showed their raw translation key** instead of their name, and a placed container had no title. An item's key now comes from its own registry entry unless the properties say otherwise, and a `BlockItem` no longer inherits its block's
- **A container with `keeps_contents` dropped nothing when broken**, and spilled its contents on the ground instead. The drop was emitted after the block entity was already gone, so the code that packs the inventory into the item never ran
- **GeckoLib 3D armor did not render.** GeckoLib 5 scans one fixed directory for models and serves them from a cache keyed by a stripped id, so files written anywhere else were never found. Both the location and the id are now what it expects
- **Chest and shulker texture references only resolved in their shortest form.** `"minecraft:entity/chest/christmas"` drew the missing-texture checkerboard, because the trim to a sprite name keyed on a `textures/` segment that this spelling does not have. Every form now trims to the same sprite
- **Copying the main half of a double chest with Ctrl+pick duplicated its contents.** The item carried the inventory twice — once as the half this mod writes, once inside the block entity data vanilla attaches — and placing it put one copy inside and dropped the other on the floor
- **A double chest split its contents across both halves on every world load.** The merge that forms a pair did not record that it had already run, so it ran again on an inventory that was already merged
- **An empty backpack could be placed inside another backpack.** Nesting is refused for anything that carries an inventory, but the check only ever looked at block-backed containers, and a backpack is a plain item

## 📦 Dependencies

- **Minecraft 26.1.2** · **NeoForge 26.1.2** · **Java 25**
- **JEI 29.29.0.77** — optional, recommended
- **GeckoLib 5.5.2** — optional, required only for 3D armor models
- **Curios 15.0.0** — optional, only needed for `curios_slots`

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

> **Correcciones del port a 26.1.2.** Ocho cosas que el port rompió o trajo consigo, cinco de ellas impedían que el contenido funcionara.

## ⚠️ Notas Importantes

**Tus archivos de contenido cargan sin cambios**, referencias incluidas. Las texturas de cofre y shulker aceptan tanto la ruta de carpeta como el nombre del sprite, `armor_layers` acepta tanto la textura de capa antigua como el equipment asset nuevo, y `armor_3d` acepta tanto la ruta del fichero como el ID con el que GeckoLib lo cachea. Donde cambió la forma, se leen las dos.

- **Una cosa queda fuera del control de este mod: GeckoLib movió sus modelos.** Lo que en un jar de 1.21.1 vivía en `geo/` está ahora en `geckolib/models/`, así que una referencia a los assets de otro mod necesita ese segmento actualizado — `"othermod:geckolib/models/armor/su.geo.json"`, o el corto `"othermod:armor/su"`. Los dos funcionan. `texture` no cambia y conserva su ruta completa con `.png`
- Los cofres dobles guardados antes de esta versión tienen su contenido repartido entre las dos mitades. Al abrirlos verás los mismos ítems en slots distintos; no se perdió nada

## 🐛 Correcciones

- **El juego crasheaba al arrancar salvo que todo el equipo declarara `enchantability`.** El campo es opcional, así que omitirlo es lo normal: llegaba como `0`, y los dos tipos de material rechazan ahora un valor que no sea positivo. El valor del material solo gradúa las ofertas — que el equipo se pueda encantar lo sigue decidiendo el flag `enchantable`
- **Los bloques y contenedores personalizados mostraban su clave de traducción en crudo** en vez de su nombre, y un contenedor colocado no tenía título. La clave de un ítem sale ahora de su propia entrada de registro salvo que las propiedades digan otra cosa, y un `BlockItem` ya no hereda la de su bloque
- **Un contenedor con `keeps_contents` no soltaba nada al romperlo**, y derramaba su contenido en el suelo. El drop se emitía después de que el block entity ya no existiera, así que el código que empaqueta el inventario en el ítem nunca corría
- **La armadura 3D de GeckoLib no se dibujaba.** GeckoLib 5 escanea un directorio fijo para sus modelos y los sirve desde una caché indexada por un id recortado, así que los archivos escritos en otro sitio no se encontraban nunca. La ubicación y el id son ahora los que espera
- **Las referencias de textura de cofre y shulker solo resolvían en su forma más corta.** `"minecraft:entity/chest/christmas"` dibujaba el cuadriculado de textura faltante, porque el recorte a nombre de sprite se apoyaba en un segmento `textures/` que esa forma no tiene. Ahora todas las formas se recortan al mismo sprite
- **Copiar la mitad principal de un cofre doble con Ctrl+clic duplicaba su contenido.** El ítem llevaba el inventario dos veces — una como la mitad que escribe este mod, otra dentro de los datos de block entity que adjunta vanilla — y al colocarlo una copia entraba dentro y la otra caía al suelo
- **Un cofre doble repartía su contenido entre las dos mitades en cada carga del mundo.** La unión que forma la pareja no registraba que ya se había hecho, así que volvía a ejecutarse sobre un inventario ya unido
- **Una mochila vacía se podía meter dentro de otra mochila.** El anidamiento se rechaza para cualquier cosa que lleve un inventario, pero la comprobación solo miraba contenedores que son bloque, y una mochila es un ítem normal

## 📦 Dependencias

- **Minecraft 26.1.2** · **NeoForge 26.1.2** · **Java 25**
- **JEI 29.29.0.77** — opcional, recomendada
- **GeckoLib 5.5.2** — opcional, solo necesaria para modelos de armadura 3D
- **Curios 15.0.0** — opcional, solo necesaria para `curios_slots`

</details>