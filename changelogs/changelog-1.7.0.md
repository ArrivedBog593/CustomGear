# UltimateCustomGear 1.7.0

*NeoForge 1.21.1 · 2026-08-20*

> **Nothing breaks.** Packs written for 1.6.0 load unchanged.

## ⚠️ Important Notes

**Nothing breaks. Packs written for 1.6.0 load unchanged.** The `mode` field inside `texture` is gone, but a pack that still declares it works exactly as before — the value is simply ignored now. Delete it whenever you feel like it.

- Containers are new in this version, so none of their rules are a change to anything
- `container.slots` is baked at registration: changing it needs a **restart**, not `/customgear reload`. Everything else about a container is hot-reloadable
- `curios_slots` needs `/reload` **as well as** `/customgear reload` — Curios reads its slot assignment outside the reload this mod triggers. The log warns when the set changes
- Texture changes still need **F3+T** to show, as always

## ✨ New Features

### Containers

- New content type `container`, with four subtypes chosen by `container.type`:
    - **`barrel`** — a full cube placed on any of six axes, with a real open state. No renderer, so it costs nothing to draw
    - **`chest`** — the vanilla chest shape with an animated lid, and it **joins into a double** with a neighbor
    - **`shulker`** — attaches to whatever surface it is placed against, its collision box grows as the lid rises, and it refuses to open when there is no room
    - **`backpack`** — not a block at all: an item you carry, open with right click or a keybind, and can equip in a Curios slot
- `container.type` is **required**. The four differ in shape, in how they are placed, and in whether breaking one keeps its contents — a silent default would decide that last one behind your back
- `keeps_contents` decides whether breaking one drops the contents or stores them in the item. Defaults per subtype: `true` for shulker and backpack, `false` for barrel and chest. A barrel that keeps its contents is perfectly legal
- `slots` has no hard ceiling. Past 128 the parser warns: that is where vanilla's click packet gives up, and a single action changing more slots than that at once disconnects the player
- **Automatic column count.** Omit `columns` and the width is worked out so the contents fit in nine rows, capped by `max_columns` (default 12). Declare `columns` and that exact width is used
- A double chest keeps **one** inventory of twice the size. Breaking either half hands back that half's contents, exactly as vanilla does — and Ctrl+picking one copies only its half
- Hoppers, comparators and mod overlays read a double chest correctly from **either** side
- Containers refuse to nest: a shulker does not go inside another shulker, or inside a vanilla one. A container that spills when broken carries no NBT, so it nests freely

### Backpacks and Curios
- Open with right click, or with the new **Open backpack** keybind (B by default) from anywhere in the inventory
- The keybind looks in this order: the selected hotbar slot, the offhand, **equipped Curios slots**, then the rest of the inventory. Equipped beats loose — someone wearing a storage ring is wearing it to have it at hand
- New `curios_slots` field lists which Curios slot types accept this backpack (`"back"`, `"ring"`, `"charm"`…). The slot types are also **assigned to the player**, so they exist without needing another mod to provide them
- Only the declared slots are assigned. Installing this mod for a sword does not fill your Curios inventory with empty slots
- The slot the backpack occupies is frozen while its screen is open, and the screen closes by itself if the backpack leaves — dropped, taken, or lost on death
- Curios is an **optional** dependency

### Texture paths are read from the value
- **`mode` is gone.** Whether a value is another mod's asset or a file of yours is deduced from the value itself: a `:` makes it a resource location, an extension makes it a file in your content folder
- **Values can now be mixed within one object.** A barrel with its own top and vanilla sides was impossible while a single `mode` decided for the whole object
- A value that is neither is rejected with an error naming both valid forms. `item/bundle` is ambiguous and is no longer guessed
- **Animated textures work.** Drop a `.mcmeta` next to your PNG — `my_fluid.png` and `my_fluid.png.mcmeta` — and both travel into the pack, exactly as they would in an ordinary resource pack

### Tooltips
- The long sections — effects, set bonuses, the three resistance layers and mob drops — now hide behind **Shift**, with a line telling you they are there. Tool and weapon stats stay visible, because comparing two at a glance is the point of a tooltip
- A container that can hold contents shows them as a **grid of items** while Shift is held, most numerous first, with "…and N more types" when there are too many

## 🐛 Bug Fixes

- **The access transformer never made it into the built jar.** Everything depending on it worked in the development environment and threw `IllegalAccessError` in a real installation — which meant opening any container disconnected the player. This is the kind of failure no amount of testing in `runClient` can find
- **The content cache skipped validation entirely.** A cached file was accepted without being checked again, so rules that changed between versions never applied to files nobody had edited, and validation warnings were shown once and never again. Validation now runs on every load; the cache still saves the disk read and the JSON parse
- **Custom fluid textures never worked.** The registry pointed a custom fluid at `customgear:fluid/<id>_still`, but nothing ever copied a file there. Fluid textures are also stitched from `block/` now, because the block atlas is built from a fixed list of directories and an arbitrary `fluid/` is not on it
- **Arrow damage came from what the shooter was holding, not from what fired.** Firing a vanilla bow with a custom one in the other hand handed the custom damage to the wrong arrow. The damage is now applied where the weapon is known for certain. Mobs still resolve by held weapon — they build their arrows without consulting the item
- **`armor_3d` was skipped in reference mode**, so an armor could not have vanilla-referenced icons and its own GeckoLib model at the same time. A `.geo.json` has no reference form, so it is now copied whenever it is declared — and referencing another mod's model works too, which avoids redistributing assets that are not yours
- **The bow's pull animation still ignored `charge_speed`.** The fix shipped in 1.6.0 described the right behavior in a comment but the code kept dividing by a fixed 20 ticks
- A debug `System.out.println` fired on every container sort-mode change

## 🔧 Technical Changes

- `TextureRef.java` — new in `resources/`: the single place that decides what a texture value points at. Both the parsers and the generators resolve through it
- `TextureData.java` — new in `data/`: the shared texture block. `BlockData`, `GearData` and `FluidData` had three near-identical copies of it
- `PackSink.addTextureWithMeta` — copies a texture and its `.mcmeta` together
- `ContainerData.java` / `ContainerContentData.java` / `ContainerContents.java` — new: container definitions and the component their contents live in
- `ContainerRegistry.java` — new in `loader/`: containers register their own Block, Item and BlockEntityType. Kept out of `BlockRegistry` because a backpack is not a block
- `CustomContainerBlock` and `CustomContainerBlockEntity` — abstract bases; `CustomBarrelBlock`, `CustomChestBlock` and `CustomShulkerBlock` add only what is theirs
- `ChestRenderer.java` / `ShulkerRenderer.java` — new in `client/render/`. The chest's three meshes are built by hand; the shulker reuses vanilla's `ShulkerModel`
- `ContainerOpenData.java` — new in `network/`: the container-open packet, now **versioned**. Its fields used to be written and read inline in two files with no check, so adding one silently shifted every value after it
- `BackpackAnchor.java` — new: where a carried backpack lives, so its menu can notice it leaving. Three cases: an inventory slot, the offhand, a Curios slot
- `CuriosCompat.java` — new in `compat/`: the only class that touches the Curios API, so nothing else breaks when it is absent
- `CuriosTagLoader.java` — new in `loader/`: emits the item tags and the entity slot assignment
- `ContainerTooltip.java` / `ContainerTooltipRenderer.java` — new in `client/`: the contents grid
- `ArrowDamageHandler.java` — reduced to mobs; players resolve through `createProjectile`, where the weapon is a parameter
- `GearModelGenerator.java` — the `loadCustom` / `loadReference` split collapsed into one path per gear type
- `build.gradle` — the access transformer is now copied into the jar and declared in `neoforge.mods.toml`

## ⚙️ Known Limitations

- **A container past 128 slots can disconnect the player.** A single action changing more slots than that at once — a long drag, a mass shift-click — exceeds what vanilla's click packet can carry. Hard to reach in survival, trivial in creative
- **A backpack's contents travel inside its ItemStack**, and that stack is re-sent every time anything moves in the player's inventory. A large backpack full of items with heavy NBT is the case where slot count costs the most
- **A shield still cannot have its own texture.** It renders through vanilla's atlas materials, and pointing at a PNG directly means losing banner patterns — a trade worth making deliberately, not by accident
- **Changing `curios_slots` needs `/reload` as well.** Curios reads its slot assignment outside the reload this mod triggers
- Trying to nest a container flickers for a frame before the server refuses it: the client's stand-in inventory knows the rule, but the prediction runs first

## 📦 Dependencies

- **Curios API** — optional, only needed for `curios_slots`

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

> **Nada se rompe.** Los packs escritos para 1.6.0 cargan sin cambios.

## ⚠️ Notas Importantes

**No se rompe nada. Los packs escritos para 1.6.0 cargan sin cambios.** El campo `mode` dentro de `texture` desapareció, pero un pack que aún lo declare funciona igual que antes — el valor simplemente se ignora. Bórralo cuando quieras.

- Los contenedores son nuevos en esta versión, así que ninguna de sus reglas cambia nada existente
- `container.slots` se fija al registrar: cambiarlo requiere **reiniciar**, no `/customgear reload`. Todo lo demás de un contenedor se recarga en caliente
- `curios_slots` requiere `/reload` **además de** `/customgear reload` — Curios lee su asignación de slots fuera de la recarga que dispara este mod. El log avisa cuando el conjunto cambia
- Los cambios de textura siguen necesitando **F3+T** para verse, como siempre

## ✨ Nuevas Características

### Contenedores

- Nuevo tipo de contenido `container`, con cuatro subtipos elegidos por `container.type`:
    - **`barrel`** — un cubo completo colocado en cualquiera de seis ejes, con estado de apertura real. Sin renderer, así que dibujarlo no cuesta nada
    - **`chest`** — la forma del cofre de vanilla con tapa animada, y **se une en doble** con un vecino
    - **`shulker`** — se pega a la superficie donde lo coloques, su caja de colisión crece al abrirse la tapa, y se niega a abrir si no hay sitio
    - **`backpack`** — no es un bloque: un objeto que llevas encima, se abre con clic derecho o con una tecla, y se puede equipar en un slot de Curios
- `container.type` es **obligatorio**. Los cuatro difieren en forma, en cómo se colocan y en sí al romperlos conservan el contenido — un valor por defecto decidiría eso último a tus espaldas
- `keeps_contents` decide si al romperlo el contenido se suelta o se guarda en el objeto. Por defecto según el subtipo: `true` para shulker y mochila, `false` para barril y cofre. Un barril que conserva su contenido es perfectamente válido
- `slots` no tiene tope duro. Pasando de 128 el parser avisa: ahí es donde el paquete de clic de vanilla se rinde, y una sola acción que cambie más slots que eso a la vez desconecta al jugador
- **Columnas automáticas.** Omite `columns` y el ancho se calcula para que el contenido quepa en nueve filas, con techo en `max_columns` (12 por defecto). Declara `columns` y se usa ese ancho exacto
- Un cofre doble mantiene **un solo** inventario del doble de tamaño. Romper cualquiera de las mitades devuelve el contenido de esa mitad, igual que en vanilla — y copiar una con Ctrl+clic central copia solo su mitad
- Tolvas, comparadores y superposiciones de otros mods leen un cofre doble correctamente desde **ambos** lados
- Los contenedores no se anidan: un shulker no entra en otro shulker, ni en uno de vanilla. Un contenedor que suelta al romperse no lleva NBT dentro, así que sí se anida libremente

### Mochilas y Curios
- Se abren con clic derecho, o con la nueva tecla **Abrir mochila** (B por defecto) desde cualquier parte del inventario
- La tecla busca en este orden: el slot activo de la barra, la mano izquierda, **los slots de Curios equipados**, y luego el resto del inventario. Lo equipado gana sobre lo suelto — quien lleva un anillo de almacenamiento puesto lo lleva para tenerlo a mano
- Nuevo campo `curios_slots` con los tipos de slot de Curios que aceptan esta mochila (`"back"`, `"ring"`, `"charm"`…). Esos tipos de slot **también se le asignan al jugador**, así que existen sin depender de que otro mod los proporcione
- Solo se asignan los slots declarados. Instalar este mod para hacer una espada no te llena el inventario de Curios de huecos vacíos
- El slot que ocupa la mochila queda bloqueado mientras su pantalla está abierta, y la pantalla se cierra sola si la mochila desaparece — tirada, robada o perdida al morir
- Curios es una dependencia **opcional**

### Las rutas de textura se leen del valor
- **`mode` desapareció.** Si un valor es el recurso de otro mod o un archivo tuyo se deduce del propio valor: un `:` lo convierte en resource location, una extensión lo convierte en un archivo de tu carpeta de contenido
- **Ya se pueden mezclar valores dentro del mismo objeto.** Un barril con la tapa propia y los lados de vanilla era imposible mientras un solo `mode` decidía por todo el objeto
- Un valor que no es ninguna de las dos cosas se rechaza con un error que nombra las dos formas válidas. `item/bundle` es ambiguo y ya no se adivina
- **Las texturas animadas funcionan.** Pon un `.mcmeta` junto a tu PNG — `mi_fluido.png` y `mi_fluido.png.mcmeta` — y ambos viajan al pack, igual que en un resource pack normal

### Tooltips
- Las secciones largas — efectos, bono de conjunto, las tres capas de resistencias y los drops de mobs — ahora se ocultan tras **Shift**, con una línea que avisa de que están ahí. Las estadísticas de herramientas y armas siguen visibles, porque comparar dos de un vistazo es para lo que sirve un tooltip
- Un contenedor que puede guardar contenido lo muestra como una **rejilla de objetos** al mantener Shift, ordenada de mayor a menor cantidad, con «…y N tipos más» cuando hay demasiados

## 🐛 Correcciones
- **El access transformer nunca llegaba al jar compilado.** Todo lo que dependía de él funcionaba en el entorno de desarrollo y lanzaba `IllegalAccessError` en una instalación real — lo que significaba que abrir cualquier contenedor desconectaba al jugador. Es el tipo de fallo que ninguna prueba en `runClient` puede encontrar
- **La caché de contenido se saltaba la validación entera.** Un archivo cacheado se aceptaba sin volver a comprobarlo, así que las reglas que cambiaban entre versiones nunca se aplicaban a archivos que nadie había editado, y los avisos de validación se mostraban una vez y nunca más. La validación ahora corre en cada carga; la caché sigue ahorrando la lectura de disco y el parseo del JSON
- **Las texturas propias de fluido nunca funcionaron.** El registro apuntaba un fluido custom a `customgear:fluid/<id>_still`, pero nada copiaba jamás un archivo ahí. Las texturas de fluido ahora se toman de `block/`, porque el atlas de bloques se construye desde una lista fija de directorios y un `fluid/` arbitrario no está en ella
- **El daño de flecha salía de lo que el tirador llevaba en la mano, no de lo que disparó.** Disparar un arco de vanilla con uno custom en la otra mano le daba el daño custom a la flecha equivocada. El daño ahora se aplica donde el arma se conoce con certeza. Los mobs siguen resolviéndose por el arma que llevan — construyen sus flechas sin consultar el objeto
- **`armor_3d` se saltaba en modo referencia**, así que una armadura no podía tener iconos referenciados a vanilla y su propio modelo de GeckoLib a la vez. Un `.geo.json` no tiene forma de referencia, así que ahora se copia siempre que se declare — y referenciar el modelo de otro mod también funciona, lo que evita redistribuir recursos que no son tuyos
- **La animación de tensado del arco seguía ignorando `charge_speed`.** El arreglo publicado en 1.6.0 describía el comportamiento correcto en un comentario, pero el código seguía dividiendo entre 20 ticks fijos
- Un `System.out.println` de depuración saltaba en cada cambio de modo de ordenación de un contenedor

## 🔧 Cambios Técnicos
- `TextureRef.java` — nuevo en `resources/`: el único sitio que decide a qué apunta un valor de textura. Tanto los parsers como los generadores resuelven a través de él
- `TextureData.java` — nuevo en `data/`: el bloque de texturas compartido. `BlockData`, `GearData` y `FluidData` tenían tres copias casi idénticas
- `PackSink.addTextureWithMeta` — copia una textura y su `.mcmeta` juntos
- `ContainerData.java` / `ContainerContentData.java` / `ContainerContents.java` — nuevos: las definiciones de contenedor y el componente donde vive su contenido
- `ContainerRegistry.java` — nuevo en `loader/`: los contenedores registran su propio Block, Item y BlockEntityType. Fuera de `BlockRegistry` porque una mochila no es un bloque
- `CustomContainerBlock` y `CustomContainerBlockEntity` — bases abstractas; `CustomBarrelBlock`, `CustomChestBlock` y `CustomShulkerBlock` añaden solo lo suyo
- `ChestRenderer.java` / `ShulkerRenderer.java` — nuevos en `client/render/`. Las tres mallas del cofre se construyen a mano; el shulker reutiliza el `ShulkerModel` de vanilla
- `ContainerOpenData.java` — nuevo en `network/`: el paquete de apertura de contenedor, ahora **versionado**. Sus campos se escribían y leían campo a campo en dos archivos sin ninguna comprobación, así que añadir uno desplazaba en silencio todos los valores siguientes
- `BackpackAnchor.java` — nuevo: dónde vive una mochila que se lleva encima, para que su menú note que se va. Tres casos: un slot del inventario, la mano izquierda, un slot de Curios
- `CuriosCompat.java` — nuevo en `compat/`: la única clase que toca la API de Curios, para que nada más se rompa cuando falta
- `CuriosTagLoader.java` — nuevo en `loader/`: emite los tags de objeto y la asignación de slots a la entidad
- `ContainerTooltip.java` / `ContainerTooltipRenderer.java` — nuevos en `client/`: la rejilla de contenido
- `ArrowDamageHandler.java` — reducido a los mobs; los jugadores resuelven por `createProjectile`, donde el arma es un parámetro
- `GearModelGenerator.java` — la división `loadCustom` / `loadReference` colapsada en un solo camino por tipo de gear
- `build.gradle` — el access transformer ahora se copia al jar y se declara en `neoforge.mods.toml`

## ⚙️ Limitaciones Conocidas
- **Un contenedor de más de 128 slots puede desconectar al jugador.** Una sola acción que cambie más slots que eso a la vez — un arrastre largo, un shift+clic masivo — supera lo que el paquete de clic de vanilla puede llevar. Difícil de alcanzar en supervivencia, trivial en creativo
- **El contenido de una mochila viaja dentro de su ItemStack**, y ese stack se reenvía cada vez que algo se mueve en el inventario del jugador. Una mochila grande llena de objetos con NBT pesado es el caso donde el número de slots más cuesta
- **Un escudo aún no puede tener su propia textura.** Se dibuja con los materiales del atlas de vanilla, y apuntar directamente a un PNG significa perder los patrones de estandarte — un intercambio que conviene hacer a propósito, no por accidente
- **Cambiar `curios_slots` requiere también `/reload`.** Curios lee su asignación de slots fuera de la recarga que dispara este mod
- Intentar anidar un contenedor parpadea un fotograma antes de que el servidor lo rechace: el inventario suplente del cliente conoce la regla, pero la predicción corre primero

## 📦 Dependencias
- **Curios API** — opcional, solo necesaria para `curios_slots`

</details>