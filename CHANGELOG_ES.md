# Changelog

Todas las notas de cambios importantes para el proyecto CustomGear.

## [1.1.0] - 2026-06-04

### ✨ GRAN ACTUALIZACION - Nuevas Características

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

- Se corrigió `SetBonusHandler`: los efectos fantasma ya no persisten después de que `/customgear reload` cambia el ID de un set
- Se corrigió `GearParser`: faltaba validación de límite inferior para campos de daño de armas (`attackDamage`, `arrowDamage`, `damageMultiplier`, `arrowDamageMultiplier`, `chargeSpeed`, `tillRadius`)
- Se corrigió `EffectUtils`: se agregó try-catch alrededor de `ResourceLocation.parse()` para prevenir crash del servidor con IDs de efecto malformados de caché desactualizada
- Se corrigió `ClientSetup`: la propiedad `blocking` del escudo se registraba fuera de `enqueueWork`, causando una posible condición de carrera
- Se corrigió estado muerto en `SetBonusHandler`: se eliminaron los mapas sin usar `activeSetBonuses` y `activePieceEffects`

### 📦 Dependencias

No se agregaron nuevas dependencias.

---

## [1.0.2] - 2026-04-13

### ✨ Mejoras

- **Soporte Multi-Idioma:** El sistema de nombres de items ahora soporta mejor idiomas complejos (chino, ruso, japonés, etc.)
- **Texturas de Armadura en Modo Referencia:** Los sets de armadura ahora pueden referenciar capas de otros mods usando el campo `armor_layers`
- **Actualizaciones en la Documentación:** README actualizado con ejemplos multi-idioma y soporte para capas de armadura en modo referencia

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

#### Comando `/customgear reload` - Ahora Funcional Completamente
- **Antes:** El comando solo recargaba texturas, pero no actualizaba nombres, durabilidad, efectos ni atributos
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