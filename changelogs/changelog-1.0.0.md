# Custom Gear 1.0.0

*NeoForge and Fabric 1.21.1*

> **Ultimate Custom Gear, now on Fabric too.** Custom Gear continues Ultimate Custom Gear 1.8.0 as a new project, by the same author. Content, worlds and the JSON format carry over unchanged.

## ⚠️ Important Notes

- **Custom Gear replaces Ultimate Custom Gear.** Do not install both. Items keep their `customgear:` ids, so a world made with Ultimate Custom Gear loads with every item in place
- **The content folder is now `.minecraft/customgear/`.** The first time the game starts, the contents of `ultimatecustomgear/` are copied there, and the old folder is left untouched. See [Migration](../docs/en/migration.md)
- **New config files**: `config/customgear-common.toml` and `config/customgear-client.toml`, with the same settings as before. NeoForge's in-game config screen is gone: edit the files instead

## ✨ New Features

- **Fabric support.** The same features and the same content files on Fabric 1.21.1, with Fabric API
- **Trinkets support on Fabric**: backpacks can be worn in Trinkets slots, as they are in Curios slots on NeoForge
- **`accessory_slots`** replaces `curios_slots`, and takes slots written the Curios way (`back`) or the Trinkets way (`chest/back`). The slots both mods ship are translated for the other one. `curios_slots` still works

## 🐛 Bug Fixes

- **Fluids ignored `light_level` and `bucket_names`.** They were only read as `lightLevel` and `bucketNames`, which the docs never mentioned. Both spellings work now
- A recipe that needs another mod is now skipped cleanly on Fabric too, instead of logging a parsing error
- **A crossbow referencing vanilla's (`minecraft:item/crossbow`) showed the missing texture when unloaded.** The reference was read as a texture, but for weapons it names a model, as the docs say; bows and crossbows now inherit the referenced model

---

<details>
<summary><b>🇪🇸 Leer en español</b></summary>

# Custom Gear 1.0.0

*NeoForge y Fabric 1.21.1*

> **Ultimate Custom Gear, ahora también en Fabric.** Custom Gear continúa Ultimate Custom Gear 1.8.0 como un proyecto nuevo, del mismo autor. El contenido, los mundos y el formato JSON se conservan sin cambios.

## ⚠️ Notas importantes

- **Custom Gear reemplaza a Ultimate Custom Gear.** No instales ambos. Los ítems conservan sus ids `customgear:`, así que un mundo hecho con Ultimate Custom Gear carga con todos sus ítems
- **La carpeta de contenido ahora es `.minecraft/customgear/`.** La primera vez que arranca el juego, el contenido de `ultimatecustomgear/` se copia ahí, y la carpeta antigua no se toca. Ver [Migración](../docs/es/migration.md)
- **Nuevos archivos de configuración**: `config/customgear-common.toml` y `config/customgear-client.toml`, con los mismos ajustes que antes. La pantalla de configuración de NeoForge dentro del juego ya no existe: edita los archivos

## ✨ Novedades

- **Soporte para Fabric.** Las mismas funciones y los mismos archivos de contenido en Fabric 1.21.1, con Fabric API
- **Soporte de Trinkets en Fabric**: las mochilas pueden llevarse en espacios de Trinkets, igual que en los de Curios en NeoForge
- **`accessory_slots`** reemplaza a `curios_slots`, y acepta espacios escritos a la manera de Curios (`back`) o de Trinkets (`chest/back`). Los espacios que traen ambos mods se traducen para el otro. `curios_slots` sigue funcionando

## 🐛 Correcciones

- **Los fluidos ignoraban `light_level` y `bucket_names`.** Solo se leían como `lightLevel` y `bucketNames`, que la documentación nunca mencionó. Ahora funcionan las dos formas
- Una receta que necesita otro mod ahora también se omite limpiamente en Fabric, en vez de registrar un error de lectura
- **Una ballesta que referenciaba la vanilla (`minecraft:item/crossbow`) mostraba la textura faltante sin cargar.** La referencia se leía como textura, pero en las armas nombra un modelo, como dice la documentación; arcos y ballestas ahora heredan el modelo referenciado

</details>
