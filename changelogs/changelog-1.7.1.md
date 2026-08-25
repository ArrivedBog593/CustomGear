# UltimateCustomGear 1.7.1

*NeoForge 1.21.1 · 2026-08-24*

> **Two container fixes.** Nothing else changed, and nothing you wrote needs editing.

## ⚠️ Important Notes

- Double chests saved before this version have their contents split across both halves. Opening one shows the same items in different slots; nothing was lost, and the layout stops moving from the second world load onward
- If you already have empty backpacks stored inside another backpack, they stay there. The rule only refuses new ones going in

## 🐛 Bug Fixes

- **A double chest split its contents across both halves on every world load.** The merge that joins a pair into one inventory of twice the size did not record that it had already run, so it ran again on an inventory that was already merged: everything was pushed into the high half, and whatever no longer fit came back at slot 0. With half a chest filled it looked like the contents had moved down a row and one stack had stayed behind
- **An empty backpack could be placed inside another backpack.** Nesting is refused for any container that keeps its contents in the item, because each level multiplies the size of the stack that travels over the network. The check only ever looked at containers that are blocks, and a backpack is a plain item — so an empty one, which carries no contents component yet, walked straight past it

## 🔧 Technical Changes

- `CustomContainerBlockEntity.java` — the `joined` flag is written to and read from NBT. It was only ever a field, so it came back false on every load and the merge tick had no way to know it had already run
- `ContainerNesting.java` — a backpack is refused before the `BlockItem` check, which is the only branch that could have caught one and never did

## 📦 Dependencies

- **Curios API** — optional, only needed for `curios_slots`

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

> **Dos correcciones de contenedores.** Nada más cambió, y nada de lo que escribiste necesita editarse.

## ⚠️ Notas Importantes

- Los cofres dobles guardados antes de esta versión tienen su contenido repartido entre las dos mitades. Al abrirlos verás los mismos ítems en slots distintos; no se perdió nada, y la distribución deja de moverse a partir de la segunda carga del mundo
- Si ya tienes mochilas vacías guardadas dentro de otra mochila, ahí se quedan. La regla solo rechaza que entren nuevas

## 🐛 Correcciones

- **Un cofre doble repartía su contenido entre las dos mitades en cada carga del mundo.** La unión que junta una pareja en un solo inventario del doble de tamaño no registraba que ya se había hecho, así que volvía a ejecutarse sobre un inventario ya unido: todo se empujaba a la mitad alta y lo que ya no cabía reaparecía en el slot 0. Con medio cofre lleno parecía que el contenido había bajado una fila y que un stack se había quedado arriba
- **Una mochila vacía se podía meter dentro de otra mochila.** El anidamiento se rechaza para cualquier contenedor que guarde su contenido en el ítem, porque cada nivel multiplica el tamaño del stack que viaja por la red. La comprobación solo miraba contenedores que son bloque, y una mochila es un ítem normal — así que una vacía, que todavía no lleva componente de contenido, pasaba de largo

## 🔧 Cambios Técnicos

- `CustomContainerBlockEntity.java` — el flag `joined` se escribe y se lee del NBT. Antes solo era un campo, así que volvía en falso en cada carga y el tick de unión no tenía forma de saber que ya se había ejecutado
- `ContainerNesting.java` — una mochila se rechaza antes de la comprobación de `BlockItem`, que era la única rama que podría haberla pillado y nunca lo hacía

## 📦 Dependencias

- **Curios API** — opcional, solo necesaria para `curios_slots`

</details>