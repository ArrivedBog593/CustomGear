# Tags

Dos caras del mismo sistema:
- **Consumir** un tag en una receta usa el prefijo `#`: `"#minecraft:planks"` acepta cualquier ítem de ese tag (ver Recetas → Tags como ingredientes).
- **Pertenecer** a un tag usa el campo `tags` en tu contenido, SIN `#`, para que las recetas de otros mods — y las tuyas — acepten tu ítem/bloque/fluido.

```json
{
  "id": "ruby_ingot",
  "type": "item",
  "tags": ["c:ingots", "c:ingots/ruby"]
}
```

Un tag es simplemente la suma de todo lo que lo declara — puedes usar tags de vanilla, tags de convención (`c:`) compartidos entre mods, o inventar los tuyos (`customgear:magic_gems`). Tus tags se fusionan con los existentes del mismo nombre.

Los **bloques** se agregan a los registries de tags de bloque y de ítem (para que las recetas, que consumen la forma de ítem, acepten tu bloque). Los **fluidos** etiquetan el fluido y su cubeta. El **equipamiento** (armaduras, herramientas y armas) no usa el campo `tags`, pero recibe automáticamente los tags de vanilla y de convención que necesita — ver [Tags automáticos del equipamiento](#tags-automáticos-del-equipamiento).

## Tags automáticos del equipamiento

Las armaduras, herramientas y armas reciben los tags que las hacen comportarse
como equipamiento de verdad. Nunca los declaras tú:

| Contenido             | Tags que recibe                                                                                    |
|-----------------------|----------------------------------------------------------------------------------------------------|
| Pieza de armadura     | Su tag de slot (`#minecraft:chest_armor`…), más `c:armors`                                         |
| Espada                | `#minecraft:swords`, `breaks_decorated_pots`, `c:tools`, `c:tools/melee_weapon`                    |
| Pico/hacha/pala/azada | Su tag de tipo (`#minecraft:pickaxes`…), `breaks_decorated_pots`, `c:tools`, `c:tools/mining_tool` |
| Arco / ballesta       | `c:tools`, `c:tools/ranged_weapon`, `c:tools/bow` o `c:tools/crossbow`                             |
| Escudo                | `c:tools`, `c:tools/shield`                                                                        |

Cuando `enchantable` es `true`, también se añaden los `#minecraft:enchantable/*`
correspondientes. Eso es lo que lee la mesa de encantamientos en 1.21 para
decidir qué ofrecer — con `enchantable: false` se omiten, así que el ítem
queda realmente no encantable.

Los **adornos de herrería** son opcionales con `"trimmable": true` en un
`armor_set`. Apagados por defecto porque los adornos se dibujan sobre las capas
de armadura: con `armor_layers: transparent` o un modelo 3D de GeckoLib el
adorno se aplica, pero nunca se ve.

## Parches de tags

El campo `tags` dice "mi contenido pertenece al tag X" — el valor escrito
siempre es tuyo. Un `tag_patch` lo invierte, para poder meter contenido
**ajeno** en un tag. Eso importa para todo lo que no puedes registrar tú: los
tipos de daño de otro mod no se podían referenciar en absoluto.

```json
{
  "type": "tag_patch",
  "registry": "damage_type",
  "tag": "customgear:dragon_breath",
  "values": [
    "iceandfire:dragon_fire",
    "iceandfire:dragon_ice"
  ]
}
```

Lo que permite que una armadura referencie un tag en vez de listar cada ID:

```json
{
  "damage_resistances": { "#customgear:dragon_breath": 0.30 }
}
```

| Campo      | Tipo     | Descripción                                                                                                   |
|------------|----------|---------------------------------------------------------------------------------------------------------------|
| `registry` | String   | **Obligatorio.** `item`, `block`, `fluid`, `entity_type`, `damage_type`, `enchantment`…                       |
| `tag`      | String   | **Obligatorio.** ID completo del tag, sin `#`                                                                 |
| `values`   | String[] | IDs de contenido a añadir                                                                                     |
| `remove`   | String[] | IDs de contenido a sacar, incluso si otro pack los metió ahí                                                  |
| `comment`  | String   | Nunca se parsea. Un parche no tiene nombre, así que sin esto el archivo no da ninguna pista de por qué existe |

`values` y `remove` son ambos opcionales, pero un parche sin ninguno de los dos
se rechaza. No hay `id`: un parche no registra nada y se identifica por
`registry` + `tag`, así que dos parches que nombren el mismo par se fusionan.

Las entradas se escriben siempre como opcionales, así que un mod no instalado
se ignora en vez de tirar el tag completo. El coste es que un typo falla
exactamente igual que un mod ausente — por eso se avisa cuando el namespace
pertenece a un mod que *sí* está cargado. Los registries de datapack como
`damage_type` no se pueden comprobar así, que es justamente el caso más común.

Las **eliminaciones** existen porque algunos tags se llenan por herencia y no
por entradas. El `trimmable_armor` de vanilla es la unión de los cuatro tags de
slot, así que una armadura entra ahí por el mero hecho de ser armadura — no
añadirlo no cambia nada, hay que quitarlo:

```json
{
  "type": "tag_patch",
  "registry": "item",
  "tag": "minecraft:trimmable_armor",
  "remove": ["othermod:some_chestplate"]
}
```

> ⚠️ Quitar de un tag de `minecraft:` o `c:` afecta a **todos los mods que lo
> lean**. Sacar un ítem de `#minecraft:planks` rompe recetas por todo el pack, y
> quien vea el fallo no tiene motivo para relacionarlo con un archivo de parche.
> Se avisa en el log; hazle caso.

Cuando el mismo ID se añade y se quita, **gana la eliminación** — añadir puede
venir de una regla general, quitar siempre es deliberado. Un aviso nombra el ID.

> Los cambios de tags necesitan `/reload` después de `/customgear reload`. El
> gestor de tags solo revincula en una recarga de datapacks.

## Tags comunes

Tags de vanilla (`minecraft:`) — hacen que tu contenido cuente como un material de vanilla:

| Tag                                                                                                   | Uso                                        |
|-------------------------------------------------------------------------------------------------------|--------------------------------------------|
| `minecraft:planks`                                                                                    | Cuenta como tablas en recetas de vanilla   |
| `minecraft:logs`                                                                                      | Troncos                                    |
| `minecraft:wool`                                                                                      | Lana                                       |
| `minecraft:leaves`                                                                                    | Hojas (se minan rápido con espada/tijeras) |
| `minecraft:swords` / `minecraft:pickaxes` / `minecraft:axes` / `minecraft:shovels` / `minecraft:hoes` | Herramienta de ese tipo                    |
| `minecraft:coals`                                                                                     | Combustibles tipo carbón                   |

Tags de convención (`c:`) — el estándar de interoperabilidad entre mods (los más útiles):

| Tag                                                | Uso                                                |
|----------------------------------------------------|----------------------------------------------------|
| `c:ingots` + `c:ingots/<material>`                 | Lingotes                                           |
| `c:gems` + `c:gems/<material>`                     | Gemas                                              |
| `c:ores` + `c:ores/<material>`                     | Menas                                              |
| `c:raw_materials` + `c:raw_materials/<material>`   | Materiales en bruto                                |
| `c:nuggets` + `c:nuggets/<material>`               | Pepitas                                            |
| `c:dusts` + `c:dusts/<material>`                   | Polvos                                             |
| `c:storage_blocks` + `c:storage_blocks/<material>` | Bloques de almacenamiento (bloque de X)            |
| `c:tools` + `c:tools/<tipo>`                       | Herramientas por tipo                              |
| `c:armors` + `c:armors/<pieza>`                    | Armaduras por pieza                                |
| `c:foods` + `c:foods/<tipo>`                       | Comida (`c:foods/fruits`, `c:foods/vegetables`...) |
| `c:dyes` + `c:dyes/<color>`                        | Tintes                                             |
| `c:seeds` / `c:crops`                              | Semillas y cultivos                                |

Consejo: usa el tag general Y el subtag de material (`c:ingots` y `c:ingots/ruby`) para máxima compatibilidad — el primero para "cualquier lingote", el segundo para "lingote de rubí específicamente".
