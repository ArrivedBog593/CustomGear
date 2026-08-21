# UltimateCustomGear 1.5.0

*NeoForge 1.21.1 · 2026-07-19*

## ⚠️ Important Notes
- Resistances are fully hot-reloadable — tune every value live with `/customgear reload`
- Adding or removing the `armor_3d` block requires a game restart (the item class is chosen at registration)
- GeckoLib is an **optional** dependency: without it the mod starts normally, and 3D armor falls back to `armor_layers` (or to the vanilla iron layers if none were declared)

## ✨ New Features

### Mob Drops
- `mob_drops.min` now accepts `0`, matching vanilla drops that can roll empty (rotten flesh is 0–2). It compounds with `chance`: a 25% chance of 0–2 drops something roughly 17% of the time

### Damage Resistances (three layers)
- Three new fields on armor — `damage_resistances` (by damage type), `attacker_resistances` (by attacker) and `conditional_resistances` (attacker + damage type combined).
- Available at a set level and per piece. Piece entries **merge** with the set ones, winning only on the keys they declare — the rest of the set still applies to that piece. `inherit_set_resistances: false` opts a piece out of the set entirely
- Each equipped piece resolves its own specificity and the pieces then add up, so a rule on one piece never silences the others
- Values are **per equipped piece**: `0.125` on a four-piece set is 50% with the full set worn
- **Specificity model, not accumulation:** `conditional` > `attacker` > `damage`. The first layer with any match *replaces* the more general ones **for that piece**, even when its value is lower. Within a layer, matching entries add up; the pieces then add up
- Accepts exact damage types (`"minecraft:arrow"`), damage type tags (`"#minecraft:is_projectile"`) and modded types (`"iceandfire:dragon_fire"`) — see [damage-types.md](../docs/en/damage-types.md) for the full list
- `attacker_resistances` accepts exact entities, entity tags (`"#minecraft:undead"`), mod wildcards (`"mekanism:*"`) and specific players (`"player:Name"`)
- Projectiles inherit their owner: an arrow is attributed to the skeleton that fired it, not to the arrow
- Clamped to 1.0, with **no balance ceiling** — total immunity is a legitimate design choice
- `player:` entries are hidden from the tooltip by default, so surprise armor stays a surprise; the new `show_player_resistances` field (default `false`) makes them visible
- New tooltip sections for each layer, capped at 4 entries with "…and N more"

### 3D Armor with GeckoLib
- New optional `texture.armor_3d` block with `model`, `texture` (both required) and `animation` (optional) — its mere presence switches the piece to 3D rendering
- Respects the surrounding `mode`: in `custom` the files are copied into the dynamic pack; in `reference` they are resource locations belonging to another mod, which then becomes required
- Falls back cleanly to 2D layers when GeckoLib is absent

## 🐛 Bug Fixes
- Food `on_eat_effects` were baked into `FoodProperties` at construction: the tooltip updated on reload, but eating still applied the old effects. Effects (and `getUseDuration`) now read the live item map, so reload works end to end
- Armor with no `armor_layers` declared produced broken (magenta) inventory icons — an early return skipped item model generation. Layers are now genuinely optional
- The "…and N more" counter in the **Dropped by** tooltip section counted blank entries, reporting more sources than existed

## 🔧 Technical Changes
- `DamageResistanceHandler.java` — new: resolves the three layers at damage time reading `GEAR_MAP` through `GearLookup` (hot-reloadable); attributes projectiles via `source.getEntity()`
- `MobDropHandler.java` — `min` may now be `0`; a roll of zero simply drops nothing instead of being clamped up to one
- `EntityMatcher.java` — new in `util/`: entity matching (exact ID, tags, `mod:*`, `player:`) extracted from `MobDropHandler` so the resistance handler can share it
- `ResistanceResolver.java` — new in `util/`: merges set-level and piece-level resistances and honors `inherit_set_resistances`. Both `DamageResistanceHandler` and `TooltipHelper` resolve through it so the tooltip can never drift from the real reduction
- `GearData.java` — new `damageResistances`, `attackerResistances`, `conditionalResistances` (set and per piece), `showPlayerResistances`, `ConditionalResistance` class, and `Armor3DData` inside `TextureData`
- `GearParser.java` — validation of the three resistance fields
- `UniversalParser.java` — `mob_drops.min` now accepts `0` (the whole item was previously rejected), with separate messages for a negative `min` and for `max` below `min`
- `TooltipHelper.java` — three new resistance sections, entries filtered before rendering so hidden and zero-valued ones never reach the "…and N more" count; the same fix applied to the existing "Dropped by" section
- `GeckoArmorItem.java` — new in `items/gear/geo/`; `GearRegistry.registerArmor` picks the item class, `GearModelGenerator` copies the model files in `custom` mode
- `CustomFoodItem.java` — `on_eat_effects` moved out of `FoodProperties` and applied in `finishUsingItem`

## 📦 Dependencies
- **GeckoLib** — optional, only required for `armor_3d`

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

## ⚠️ Notas Importantes
- Las resistencias son totalmente recargables en caliente — ajusta cualquier valor en vivo con `/customgear reload`
- Agregar o quitar el bloque `armor_3d` requiere reiniciar el juego (la clase del ítem se decide al registrarlo)
- GeckoLib es una dependencia **opcional**: sin él el mod arranca normal y la armadura 3D cae a `armor_layers` (o a las capas de hierro vanilla si no se declararon)

## ✨ Nuevas Características

### Drops de Mobs
- `mob_drops.min` ahora acepta `0`, igual que las caídas normales que pueden salir vacías (la carne podrida es 0-2). Esto se combina con `chance`: un 25% de posibilidades de que salga algo de 0-2 ocurre aproximadamente un 17% del tiempo

### Resistencias de Daño (tres capas)
- Tres campos nuevos en armaduras — `damage_resistances` (por tipo de daño), `attacker_resistances` (por atacante) y `conditional_resistances` (atacante + tipo de daño combinados).
- Disponibles a nivel de conjunto y por pieza. Las entradas de pieza **se fusionan** con las del conjunto, ganando solo en las claves que declaran — el resto del conjunto sigue aplicando a esa pieza. `inherit_set_resistances: false` saca a una pieza del conjunto por completo
- Cada pieza equipada resuelve su propia especificidad y luego se suman las piezas, así que una regla en una pieza nunca silencia a las demás
- Los valores son **por pieza equipada**: `0.125` en un set de cuatro piezas es 50% con el conjunto completo puesto
- **Modelo de especificidad, no de acumulación:** `conditional` > `attacker` > `damage`. La primera capa con alguna coincidencia *reemplaza* a las más generales **para esa pieza**, aunque su valor sea menor. Dentro de una capa, las entradas que coinciden se suman; después se suman las piezas
- Acepta tipos de daño exactos (`"minecraft:arrow"`), tags de tipo de daño (`"#minecraft:is_projectile"`) y tipos de mods (`"iceandfire:dragon_fire"`) — la lista completa está en [damage-types.md](../docs/es/damage-types.md)
- `attacker_resistances` acepta entidades exactas, tags de entidad (`"#minecraft:undead"`), comodines de mod (`"mekanism:*"`) y jugadores específicos (`"player:Nombre"`)
- Los proyectiles heredan a su dueño: una flecha se atribuye al esqueleto que la disparó, no a la flecha
- Se limita a 1.0, **sin techo de balance** — la inmunidad total es una decisión de diseño legítima
- Las entradas `player:` se ocultan del tooltip por defecto para que una armadura sorpresa siga siendo sorpresa; el nuevo campo `show_player_resistances` (por defecto `false`) las hace visibles
- Secciones nuevas de tooltip para cada capa, recortadas a 4 entradas con "…y N más"

### Armaduras 3D con GeckoLib
- Nuevo bloque opcional `texture.armor_3d` con `model`, `texture` (ambos obligatorios) y `animation` (opcional) — su sola presencia cambia la pieza a renderizado 3D
- Respeta el `mode` que lo rodea: en `custom` los archivos se copian al pack dinámico; en `reference` son resource locations de otro mod, que pasa a ser obligatorio
- Cae limpiamente a las capas 2D cuando GeckoLib no está presente

## 🐛 Correcciones
- Los `on_eat_effects` de la comida se horneaban en `FoodProperties` al construirse: el tooltip se actualizaba con el reload, pero al comer se aplicaban los efectos viejos. Ahora los efectos (y `getUseDuration`) leen el mapa vivo de ítems, así que el reload funciona de punta a punta
- Las armaduras sin `armor_layers` declaradas generaban íconos rotos (magenta) en el inventario — un return temprano se saltaba la generación de los modelos de ítem. Las capas ahora sí son opcionales
- El contador "…y N más" de la sección **Lo sueltan:** del tooltip contaba entradas vacías, reportando más fuentes de las que existían

## 🔧 Cambios Técnicos
- `DamageResistanceHandler.java` — nuevo: resuelve las tres capas al momento del daño leyendo `GEAR_MAP` vía `GearLookup` (recargable en caliente); atribuye los proyectiles con `source.getEntity()`
- `MobDropHandler.java` — `min` ahora puede ser `0`; un resultado de cero simplemente no deja nada en lugar de ajustarse a uno
- `EntityMatcher.java` — nuevo en `util/`: comparación de entidades (ID exacto, tags, `mod:*`, `player:`) extraída de `MobDropHandler` para que el handler de resistencias la comparta
- `ResistanceResolver.java` — nuevo en `util/`: fusiona las resistencias del conjunto con las de la pieza y respeta `inherit_set_resistances`. Tanto `DamageResistanceHandler` como `TooltipHelper` resuelven a través de él, así que el tooltip nunca puede desviarse de la reducción real
- `GearData.java` — nuevos `damageResistances`, `attackerResistances`, `conditionalResistances` (conjunto y por pieza), `showPlayerResistances`, la clase `ConditionalResistance`, y `Armor3DData` dentro de `TextureData`
- `GearParser.java` — validación de los tres campos de resistencia
- `UniversalParser.java` — `mob_drops.min` ahora acepta `0` (antes se rechazaba el ítem completo), con mensajes separados para un `min` negativo y para un `max` menor que `min`
- `TooltipHelper.java` — tres nuevas secciones de resistencia, las entradas se filtran antes de mostrarse para qué las ocultas y las de valor cero nunca lleguen al conteo de "…y N más"; la misma corrección aplicada a la sección existente "Causado por"
- `GeckoArmorItem.java` — nuevo en `items/gear/geo/`; `GearRegistry.registerArmor` elige la clase del ítem y `GearModelGenerator` copia los archivos del modelo en modo `custom`
- `CustomFoodItem.java` — los `on_eat_effects` salieron de `FoodProperties` y se aplican en `finishUsingItem`

## 📦 Dependencias
- **GeckoLib** — opcional, solo necesaria para `armor_3d`

</details>