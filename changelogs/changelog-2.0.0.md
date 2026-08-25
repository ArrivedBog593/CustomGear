# UltimateCustomGear 2.0.0

*NeoForge 26.1.2 · 2026-08-23*

> **The port to Minecraft 26.1.2.** Your JSON does not change: no field was added, removed or renamed. What changed is everything underneath it.

## ⚠️ Important Notes

**Your content files load unchanged.** Every field this mod has ever read means the same thing it did in 1.7.0. If you have a pack, copy it across and it works.

- **Minecraft 26.1.2 only.** This version does not run on 1.21.1, and 1.7.0 does not run on 26.1.2. They are separate branches, not an upgrade path in one jar
- **3.0.0 is this same port, for Minecraft 26.2.** The two share their code; the version number is what tells the jars apart, since one game version cannot read the other's
- **Java 25 is required**, up from Java 21. Your launcher almost certainly handles this for you, but a server pinned to an old JDK will refuse to start
- Every dependency moved with the game: **GeckoLib 5.5.2**, **JEI 29.29.0.77**, **Curios 15.0.0**. Older builds of those will not load
- Texture changes still need **F3+T** to show, as always

## ✨ New Features

### Chest and shulker textures accept the short form

A container texture can now be written the way the game addresses it:

```json
"refs": { "single": "minecraft:normal" }
```

The long form you have been writing keeps working — `minecraft:textures/entity/chest/normal.png` is trimmed to the same thing — so nothing needs editing. The short form exists because that is what these textures actually are now: entries in an atlas, not loose files.

### Armour layers accept the short form too

```json
"armor_layers": { "layer_1": "othermod:diamond" }
```

The old `othermod:models/armor/diamond_layer_1` still resolves to the same asset. Both forms are read; the shorter one matches how equipment is named in this version.

## 🐛 Bug Fixes

- **The mod crashed on startup when no containers were defined.** The block entity type was built from an empty list of blocks, which the game now rejects outright — so an instance with gear but no containers, the most ordinary setup there is, would not boot at all
- **Fluid blocks generated no blockstate and no model.** Every custom fluid logged a missing-model error on each load, and breaking or splashing one produced the black-and-magenta square instead of a particle. Vanilla writes its own water exactly the way this now does
- **Every bucket model was generated twice.** Two loops ran over the fluid list, the second a copy of the first with a texture step added

## 🔧 Technical Changes

Almost all of this is invisible from a JSON file. It is listed because the mod is 80 files smaller and several subsystems no longer exist.

- **Items are no longer built by subclassing.** `ArmorItem`, `SwordItem`, `PickaxeItem` and friends are gone from the game; an item is now a plain `Item` carrying data components. `CustomTier` produces a vanilla `ToolMaterial` instead of implementing a `Tier` interface
- **Armour points at an equipment asset.** The renderer no longer builds the texture path from a prefix plus `_layer_1`. `GearModelGenerator` writes `equipment/<id>.json` naming one texture per body layer, and the two layers land in two folders rather than one
- **Item models split into two files.** A model under `models/item/` still holds geometry and textures, but *which* model an item renders is now decided in `items/<id>.json`. The new `ItemDefinitions` class writes those
- **The `overrides` array is gone**, and with it the bug it invited: the crossbow's entries were matched last-wins, so *charged* had to sit after *pulling* and reordering the array broke the item silently. The replacement says it structurally — what the crossbow is loaded with is checked above how far it is drawn
- **Three renderers deleted.** The shield, chest and shulker each had a `BlockEntityWithoutLevelRenderer` to draw a shape no model could hold. That class no longer exists; the shape is declared in the item definition as a `minecraft:special` entry naming vanilla's own renderer
- **The `ItemProperties` registrations are gone.** `ClientSetup` used to walk every registered item on client start to attach bow, crossbow and shield predicates. Model switching is data now, so nothing runs
- **The chest mesh is no longer written by hand.** Vanilla exposes `ChestModel` and its three model layers, with the geometry this mod was copying cube by cube
- **Block entity renderers extract state instead of drawing.** `ChestRenderer` was rebuilt around that: it snapshots what it needs on the main thread and submits later, off it
- `FluidTextures.java` — new in `resources/`: sprite and tint resolution, moved out of `FluidRegistry`. Three callers need it, and one of them runs before the game has bootstrapped, where touching a registry throws
- `ContainerRenderState.java` — new in `client/render/`: what one container looks like this frame
- `ShulkerRenderer.java` and `CustomModelLayers.java` — deleted; vanilla's `ShulkerBoxRenderer` and model layers replace both
- NBT on block entities moved to `ValueInput` / `ValueOutput`, and `saveToItem` to `collectImplicitComponents`
- The access transformer gained `AbstractContainerScreen.imageWidth` / `imageHeight`, which became final, and `AbstractArrow.baseDamage`, which lost its getter

## ⚙️ Known Limitations

Everything listed under 1.7.0 still applies. On top of it:

- **The client has not been verified end to end.** Loading, registration and the generated resource pack are tested — a dedicated server starts clean with content of every type, and every generated file was checked against vanilla's own. What has not been watched on a screen is the rendering itself: item icons, the bow bending, a chest in the hand. The formats match vanilla file for file, but that is not the same as having seen it
- **A shield still cannot have its own texture.** It renders through vanilla's shield renderer, which reads the vanilla atlas
- Parchment publishes no mappings for 26.x yet, so parameter names in a decompiled view are the obfuscated ones. This affects nobody writing JSON

## 📦 Dependencies

- **Minecraft 26.1.2** · **NeoForge 26.1.2** · **Java 25**
- **JEI 29.29.0.77** — optional, recommended
- **GeckoLib 5.5.2** — optional, required only for 3D armor models
- **Curios 15.0.0** — optional, only needed for `curios_slots`

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

> **El port a Minecraft 26.1.2.** Tu JSON no cambia: no se añadió, quitó ni renombró ningún campo. Lo que cambió es todo lo que hay debajo.

## ⚠️ Notas Importantes

**Tus archivos de contenido cargan sin cambios.** Todos los campos que este mod ha leído alguna vez significan lo mismo que en 1.7.0. Si tienes un pack, cópialo y funciona.

- **Solo Minecraft 26.1.2.** Esta versión no corre en 1.21.1, y la 1.7.0 no corre en 26.1.2. Son ramas separadas, no una actualización dentro del mismo jar
- **La 3.0.0 es este mismo port, para Minecraft 26.2.** Comparten el código; el número de versión es lo que distingue los jars, porque una versión del juego no puede leer la de la otra
- **Requiere Java 25**, antes Java 21. Tu launcher casi seguro se encarga solo, pero un servidor anclado a un JDK viejo no arrancará
- Todas las dependencias se movieron con el juego: **GeckoLib 5.5.2**, **JEI 29.29.0.77**, **Curios 15.0.0**. Las versiones anteriores no cargan
- Los cambios de textura siguen necesitando **F3+T** para verse, como siempre

## ✨ Nuevas Características

### Las texturas de cofre y shulker aceptan la forma corta

La textura de un contenedor ya se puede escribir como el juego la direcciona:

```json
"refs": { "single": "minecraft:normal" }
```

La forma larga que venías escribiendo sigue funcionando — `minecraft:textures/entity/chest/normal.png` se recorta a lo mismo — así que no hay nada que editar. La forma corta existe porque es lo que esas texturas son ahora: entradas de un atlas, no archivos sueltos.

### Las capas de armadura también aceptan la forma corta

```json
"armor_layers": { "layer_1": "othermod:diamond" }
```

El viejo `othermod:models/armor/diamond_layer_1` sigue resolviendo al mismo recurso. Se leen las dos formas; la corta es la que coincide con cómo se nombra el equipamiento en esta versión.

## 🐛 Correcciones

- **El mod crasheaba al arrancar si no había contenedores definidos.** El tipo de block entity se construía con una lista vacía de bloques, algo que el juego ahora rechaza de plano — así que una instancia con equipo pero sin contenedores, que es la configuración más normal que existe, no arrancaba
- **Los bloques de fluido no generaban blockstate ni modelo.** Cada fluido custom registraba un error de modelo faltante en cada carga, y romperlo o salpicar producía el cuadro negro y magenta en vez de una partícula. Vanilla escribe su propia agua exactamente como se hace ahora
- **Cada modelo de cubo se generaba dos veces.** Había dos bucles sobre la lista de fluidos, el segundo copia del primero con un paso de textura añadido

## 🔧 Cambios Técnicos

Casi nada de esto se ve desde un archivo JSON. Se lista porque el mod es 80 archivos más pequeño y varios subsistemas dejaron de existir.

- **Los items ya no se construyen por herencia.** `ArmorItem`, `SwordItem`, `PickaxeItem` y compañía desaparecieron del juego; un item es ahora un `Item` normal con componentes de datos. `CustomTier` produce un `ToolMaterial` de vanilla en lugar de implementar una interfaz `Tier`
- **La armadura apunta a un equipment asset.** El renderer ya no construye la ruta de textura a partir de un prefijo más `_layer_1`. `GearModelGenerator` escribe `equipment/<id>.json` nombrando una textura por capa del cuerpo, y las dos capas van a dos carpetas en vez de a una
- **Los modelos de item se parten en dos archivos.** Un modelo en `models/item/` sigue llevando geometría y texturas, pero *cuál* modelo dibuja un item se decide ahora en `items/<id>.json`. La nueva clase `ItemDefinitions` los escribe
- **El array `overrides` desapareció**, y con él el bug que invitaba: las entradas de la ballesta se resolvían por "gana la última", así que *cargada* tenía que ir después de *tensando* y reordenar el array rompía el item en silencio. Lo que lo sustituye lo dice estructuralmente: lo que la ballesta lleva cargado se comprueba por encima de cuánto está tensada
- **Tres renderers eliminados.** El escudo, el cofre y el shulker tenían cada uno un `BlockEntityWithoutLevelRenderer` para dibujar una forma que ningún modelo podía contener. Esa clase ya no existe; la forma se declara en la definición del item como una entrada `minecraft:special` que nombra el renderer de vanilla
- **Los registros de `ItemProperties` desaparecieron.** `ClientSetup` recorría todos los items registrados al arrancar el cliente para colgarles los predicados de arco, ballesta y escudo. El cambio de modelo es datos ahora, así que no se ejecuta nada
- **La malla del cofre ya no se escribe a mano.** Vanilla expone `ChestModel` y sus tres capas de modelo, con la geometría que este mod venía copiando cubo a cubo
- **Los renderers de block entity extraen estado en vez de dibujar.** `ChestRenderer` se rehízo sobre eso: fotografía lo que necesita en el hilo principal y lo envía después, fuera de él
- `FluidTextures.java` — nuevo en `resources/`: la resolución de sprite y tinte, sacada de `FluidRegistry`. Tres sitios la necesitan, y uno corre antes de que el juego haya arrancado, donde tocar un registro lanza excepción
- `ContainerRenderState.java` — nuevo en `client/render/`: cómo se ve un contenedor este fotograma
- `ShulkerRenderer.java` y `CustomModelLayers.java` — eliminados; el `ShulkerBoxRenderer` y las capas de modelo de vanilla sustituyen a ambos
- El NBT de los block entities pasó a `ValueInput` / `ValueOutput`, y `saveToItem` a `collectImplicitComponents`
- El access transformer ganó `AbstractContainerScreen.imageWidth` / `imageHeight`, que pasaron a ser final, y `AbstractArrow.baseDamage`, que perdió su getter

## ⚙️ Limitaciones Conocidas

Todo lo listado en 1.7.0 sigue aplicando. Además:

- **El cliente no está verificado de punta a punta.** La carga, el registro y el resource pack generado sí están probados — un servidor dedicado arranca limpio con contenido de todos los tipos, y cada archivo generado se comparó con los de vanilla. Lo que no se ha mirado en una pantalla es el renderizado en sí: los iconos de los items, el arco tensándose, un cofre en la mano. Los formatos coinciden con vanilla archivo por archivo, pero eso no es lo mismo que haberlo visto
- **Un escudo aún no puede tener su propia textura.** Se dibuja con el renderer de escudo de vanilla, que lee el atlas de vanilla
- Parchment todavía no publica mappings para 26.x, así que los nombres de parámetro en una vista descompilada son los ofuscados. Esto no afecta a nadie que escriba JSON

## 📦 Dependencias

- **Minecraft 26.1.2** · **NeoForge 26.1.2.x** · **Java 25**
- **JEI 29.29.0.77** — opcional, recomendado
- **GeckoLib 5.5.2** — opcional, solo necesaria para modelos de armadura 3D
- **Curios 15.0.0** — opcional, solo necesaria para `curios_slots`

</details>
