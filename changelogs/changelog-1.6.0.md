# UltimateCustomGear 1.6.0

*NeoForge 1.21.1 · 2026-08-01*

## 💥 BREAKING CHANGES

**Two fields changed meaning. Existing content packs need editing.**

### `cooking_time` is now in SECONDS, not ticks
Every other time field in this mod is in seconds (`eat_duration`, effect `duration`), and `cooking_time` was the odd one out. **Divide your existing values by 20**: a recipe written as `"cooking_time": 200` meant 10 seconds and now means 200 seconds.

Nothing errors — the recipe still works, just 20x slower — so this will not show up as a crash. Check every `smelting`, `blasting`, `smoking` and `campfire_cooking` recipe you have.

### `requires_player_kill` now defaults to `false`
It used to default to `true`. Vanilla drops do not care who landed the blow, and the old default made every custom drop behave unlike everything else in the game.

If you run an item economy, **declare `"requires_player_kill": true` explicitly** — otherwise a fall damage farm can print currency. The parser warns on every item that leaves the field undeclared, so the log will tell you exactly which files to check.

Note it asks for *recent player damage*, not the killing blow: a mob you hit and a creeper finished still counts as yours.

## ⚠️ Important Notes
- Tags need `/reload` after `/customgear reload` — the tag manager only rebinds on a datapack reload. Everything else stays hot-reloadable
- `passthrough` recipes are copied verbatim and are only protected against the target mod being **missing**, not against it being a different **version**. A mod that changes its recipe schema can break datapack loading entirely
- `charge_speed` no longer appears in tooltips: it never affected the real charge time. See Known Limitations

## ✨ New Features

### Tag Patches
- New content type `tag_patch` — declares that **foreign** content belongs to a tag, the inverse of the `tags` field. Needed for anything that is not an item you registered: damage types from other mods could not be referenced at all before
- Identified by `registry` + `tag`, no `id`. Two patches naming the same pair merge
- Entries are always emitted with `required: false`, so an uninstalled mod is ignored instead of dropping the whole tag. A typo is warned about when the namespace belongs to a mod that IS loaded
- `remove` takes content **out** of a tag, even when another pack puts it there. That is the only way out of an inherited tag
- Removals from `minecraft:` or `c:` tags are warned about — they affect every mod that reads them

### Gear Tags
- Armor, tools, and weapons now receive the vanilla tags they always should have had. Gear never went through the tag system at all, which is why a custom sword was offered nothing at the enchanting table while a diamond sword was offered everything
- Slot tags (`#minecraft:chest_armor`…), behavior tags (`#minecraft:swords`, `breaks_decorated_pots`) and, when `enchantable: true`, the matching `#minecraft:enchantable/*` tags
- Convention tags too (`c:armors`, `c:tools`, `c:tools/melee_weapon`…) so other mods recognize this mod's gear generically
- New `trimmable` field on `armor_set` (default `false`). Off by default because trims draw over the armor layers: with `armor_layers: transparent` or a GeckoLib 3D model the trim applies but never shows

### Recipes
- Three vanilla types that were missing: `smoking`, `campfire_cooking` and `stonecutting`
- `stonecutting` honours `result_count` — cutting one block into several is the normal case
- New `passthrough` type: hands a raw recipe body to the game untouched, so another mod's recipe type (`create:mixing`, `create:pressing`…) can produce this mod's content without this mod knowing that schema. A `neoforge:mod_loaded` condition is derived from the inner type's namespace automatically; extra mods go in `requires`
- Declaring a field the recipe type ignores now logs a warning naming it

### Mob Drops
- New `looting_mode` replaces `affected_by_looting`, mirroring vanilla's two separate mechanisms:
  - `"count"` (default) — adds 0..levels to the rolled amount, like common drops (rotten flesh, string)
  - `"chance"` — raises the drop probability instead and leaves the amount alone, like rare drops (wither skeleton skulls)
  - `"none"` — Looting does nothing
- Vanilla never applies both to the same drop, and neither does this
- `looting_chance_bonus` (default `0.01`) sets how much probability each Looting level adds in `chance` mode. Not a universal constant in vanilla either — loot tables declare their own
- In `count` mode the bonus applies **before** the empty check and is **not** clamped to `max`, matching vanilla

### Dynamic Pack Icon
- The dynamic pack now shows the mod's logo in the resource pack screen
- Override it by dropping your own `dynamic_pack_icon.png` in the `ultimatecustomgear/` folder — square, power of two (64×64 or 128×128)
- The icon is deliberately excluded from the content hash, so branding your pack never desyncs clients

## 🐛 Bug Fixes
- **Two tag loaders could silently overwrite each other.** A block declaring `"tags": ["minecraft:mineable/pickaxe"]` landed on the exact path `required_tool` generates, and the last one written won — entries vanished with no warning. All tag sources now feed one accumulator that merges and emits once
- **Individual bows and crossbows never worked in `custom` texture mode.** They fell through to the generic tool branch: no pulling frames, no display transforms, rendering at a tool scale. Crossbows had no custom-mode support at all, not even inside a `weapon_set`
- **The bow's pull animation was desynced from the shot.** It scaled with `charge_speed`, which does not affect the real draw — at `charge_speed: 0.25` the animation ran 16x too fast and skipped `bow_pulling_0` entirely
- **The crossbow could be fired before its animation finished.** The pull predicate divided by the use duration instead of the charge duration, so the crossbow loaded while the animation was still on the first frame
- A missing frame texture dropped every pulling override instead of just that one, freezing the animation with no visible cause

## 🔧 Technical Changes
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

## ⚙️ Known Limitations
- **`charge_speed` does not change the real charge time.** `BowItem.getPowerForTime` and `CrossbowItem.getChargeDuration` are static and cannot be overridden, so a bow always draws in 1.0s and a crossbow in 1.25s. The field only stretches how long the click can be held, which is already minutes either way. The tooltip line was removed rather than keep promising something that never happened
- **Smithing trims cannot be removed from other mods' armor**, only from this mod's. Vanilla's `trimmable_armor` is the union of the four slot tags, so armor inherits it just by being armor
- Tag changes need `/reload` after `/customgear reload`

## 📦 Dependencies

No new dependencies added.

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

## 💥 CAMBIOS ROMPIENTES

**Dos campos cambiaron de significado. Los packs de contenido existentes necesitan edición.**

### `cooking_time` ahora está en SEGUNDOS, no en ticks
Todos los demás campos de tiempo del mod están en segundos (`eat_duration`, `duration` de efectos), y `cooking_time` era la excepción. **Divide tus valores actuales entre 20**: una receta escrita como `"cooking_time": 200` significaba 10 segundos y ahora significa 200 segundos.

No falla nada — la receta sigue funcionando, solo que 20 veces más lenta — así que esto no aparecerá como un error. Revisa todas tus recetas de `smelting`, `blasting`, `smoking` y `campfire_cooking`.

### `requires_player_kill` ahora vale `false` por defecto
Antes valía `true`. A los drops de vanilla no les importa quién dio el golpe final, y el default anterior hacía que cada drop personalizado se comportara distinto a todo lo demás del juego.

Si llevas una economía de ítems, **declara `"requires_player_kill": true` explícitamente** — si no, una granja de daño por caída puede imprimir moneda. El parser avisa por cada ítem que deje el campo sin declarar, así que el log te dirá exactamente qué archivos revisar.

Ojo que pide *daño reciente de un jugador*, no el golpe final: un mob que golpeaste y remató un creeper sigue contando como tuyo.

## ⚠️ Notas Importantes
- Los tags necesitan `/reload` después de `/customgear reload` — el gestor de tags solo revincula en una recarga de datapacks. Todo lo demás sigue siendo recargable en caliente
- Las recetas `passthrough` se copian tal cual y solo están protegidas contra que el mod objetivo **falte**, no contra que sea otra **versión**. Un mod que cambie su esquema de recetas puede romper la carga del datapack entera
- `charge_speed` ya no aparece en los tooltips: nunca afectó al tiempo de carga real. Ver Limitaciones Conocidas

## ✨ Nuevas Características

### Parches de Tags
- Nuevo tipo de contenido `tag_patch` — declara que contenido **ajeno** pertenece a un tag, lo inverso del campo `tags`. Necesario para todo lo que no sea un ítem que tú registraste: los tipos de daño de otros mods no se podían referenciar en absoluto
- Se identifica por `registry` + `tag`, sin `id`. Dos parches que nombren el mismo par se fusionan
- Las entradas se emiten siempre con `required: false`, así que un mod no instalado se ignora en vez de tirar el tag completo. Un typo sí se avisa cuando el namespace pertenece a un mod que SÍ está cargado
- `remove` saca contenido **de** un tag, incluso si otro pack lo metió ahí. Es la única forma de salir de un tag heredado
- Las eliminaciones sobre tags de `minecraft:` o `c:` se avisan — afectan a todos los mods que los lean

### Tags de Equipamiento
- Armaduras, herramientas y armas ahora reciben los tags de vanilla que siempre debieron tener. El equipamiento nunca pasó por el sistema de tags, y por eso una espada personalizada no recibía ninguna oferta en la mesa de encantamientos mientras que una de diamante las recibía todas
- Tags de slot (`#minecraft:chest_armor`…), tags de comportamiento (`#minecraft:swords`, `breaks_decorated_pots`) y, cuando `enchantable: true`, los `#minecraft:enchantable/*` correspondientes
- También tags de convención (`c:armors`, `c:tools`, `c:tools/melee_weapon`…) para que otros mods reconozcan el equipamiento de este mod de forma genérica
- Nuevo campo `trimmable` en `armor_set` (por defecto `false`). Apagado por defecto porque los adornos se dibujan sobre las capas de armadura: con `armor_layers: transparent` o un modelo 3D de GeckoLib el adorno se aplica pero nunca se ve

### Recetas
- Tres tipos de vanilla que faltaban: `smoking`, `campfire_cooking` y `stonecutting`
- `stonecutting` respeta `result_count` — cortar un bloque en varios es el caso normal
- Nuevo tipo `passthrough`: entrega un cuerpo de receta crudo al juego sin tocarlo, para que el tipo de receta de otro mod (`create:mixing`, `create:pressing`…) pueda producir contenido de este mod sin que este mod conozca ese esquema. La condición `neoforge:mod_loaded` se deduce automáticamente del namespace del tipo interno; los mods extra van en `requires`
- Declarar un campo que el tipo de receta ignora ahora genera un aviso nombrándolo

### Drops de Mobs
- El nuevo `looting_mode` reemplaza a `affected_by_looting`, reflejando los dos mecanismos separados de vanilla:
  - `"count"` (por defecto) — suma 0..nivel a la cantidad tirada, como los drops comunes (carne podrida, cuerda)
  - `"chance"` — sube la probabilidad de drop y deja la cantidad intacta, como los drops raros (cabezas de esqueleto wither)
  - `"none"` — el Saqueo no hace nada
- Vanilla nunca aplica ambos al mismo drop, y este mod tampoco
- `looting_chance_bonus` (por defecto `0.01`) define cuánta probabilidad suma cada nivel de Saqueo en modo `chance`. Tampoco es una constante universal en vanilla — cada loot table declara la suya
- En modo `count` el bonus se aplica **antes** del descarte por cantidad cero y **no** se recorta contra `max`, igual que vanilla

### Icono del Pack Dinámico
- El pack dinámico ahora muestra el logo del mod en la pantalla de paquetes de recursos
- Sustitúyelo poniendo tu propio `dynamic_pack_icon.png` en la carpeta `ultimatecustomgear/` — cuadrado y potencia de dos (64×64 o 128×128)
- El icono queda deliberadamente fuera del hash de contenido, así que personalizar tu pack nunca desincroniza a los clientes

## 🐛 Correcciones
- **Dos cargadores de tags podían sobrescribirse en silencio.** Un bloque que declarara `"tags": ["minecraft:mineable/pickaxe"]` aterrizaba en la ruta exacta que genera `required_tool`, y ganaba el último escrito — las entradas desaparecían sin aviso. Ahora todas las fuentes de tags alimentan un acumulador que fusiona y emite una sola vez
- **Los arcos y ballestas individuales nunca funcionaron en modo de textura `custom`.** Caían a la rama genérica de herramientas: sin fases de tensado, sin transformaciones de display, renderizando a escala de herramienta. Las ballestas no tenían soporte de modo custom en absoluto, ni siquiera dentro de un `weapon_set`
- **La animación de tensado del arco estaba desincronizada del disparo.** Escalaba con `charge_speed`, que no afecta al tensado real — con `charge_speed: 0.25` la animación iba 16 veces más rápido y se saltaba `bow_pulling_0` por completo
- **La ballesta se podía disparar antes de terminar su animación.** El predicado de tensado dividía entre la duración de uso en vez de la duración de carga, así que la ballesta se cargaba mientras la animación seguía en el primer frame
- Una textura de frame faltante tiraba todos los overrides de tensado en vez de solo ese, congelando la animación sin causa visible

## 🔧 Cambios Técnicos
- `TagFileBuilder.java` — nuevo en `loader/`: acumulador compartido para todos los archivos de tag. `add` llena `values`, `remove` emite el array `remove` de NeoForge. Cuando el mismo id se añade y se quita, **gana la eliminación**, con un aviso nombrándolo
- `TagPatchData.java` / `TagPatchLoader.java` — nuevos: el tipo de contenido `tag_patch`
- `GearTagLoader.java` — nuevo en `loader/`: deriva tags de vanilla y de convención del equipamiento registrado
- `ItemTagLoader.java` / `BlockTagLoader.java` — ya no escriben al pack; alimentan el acumulador compartido
- `RecipeLoader.java` — añadidos `buildStonecutting` y `buildPassthrough`; `buildCooking` ahora resuelve segundos a ticks; `warnUnusedFields` compara los campos declarados contra el tipo
- `RecipeData.java` — `json` y `requires` para passthrough; `experience` y `resultCount` boxeados para poder distinguir un valor sin declarar de uno declarado con el default
- `MobDropHandler.java` — el Saqueo se resuelve una vez por muerte a través del registro dinámico de encantamientos; la participación del jugador ahora pregunta por daño reciente en vez del golpe final
- `GearModelGenerator.java` — añadidos `loadCustomWeapon`, `loadCustomCrossbowTextures` y `customFrame`; las armas individuales se enrutan correctamente en modo `custom`
- `ClientSetup.java` — el `pull` del arco divide entre 20 ticks fijos, el de la ballesta entre `getChargeDuration`
- `DynamicResourcePack.java` — `addRootFile` y un `getRootResource` funcional, que antes devolvía `null` incondicionalmente
- `TextureLoader.java` — `loadPackIcon`

## ⚙️ Limitaciones Conocidas
- **`charge_speed` no cambia el tiempo de carga real.** `BowItem.getPowerForTime` y `CrossbowItem.getChargeDuration` son estáticos y no se pueden sobrescribir, así que un arco siempre se tensa en 1.0s y una ballesta en 1.25s. El campo solo alarga cuánto tiempo se puede mantener el clic, que ya son minutos de todos modos. Se quitó la línea del tooltip en vez de seguir prometiendo algo que nunca ocurrió
- **Los adornos de herrería no se pueden quitar a las armaduras de otros mods**, solo a las de este. El `trimmable_armor` de vanilla es la unión de los cuatro tags de slot, así que una armadura lo hereda por el mero hecho de ser armadura
- Los cambios de tags necesitan `/reload` después de `/customgear reload`

## 📦 Dependencias
No se agregaron dependencias nuevas.

</details>