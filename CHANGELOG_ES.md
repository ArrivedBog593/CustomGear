# Changelog

Todas las notas de cambios importantes para el proyecto CustomGear.

## [1.0.2] - 2026-04-13

### ✨ Mejoras

#### Soporte Multi-Idioma Mejorado
- **Se mejoró:** El sistema de nombres de items ahora prioriza nombres específicos para mejor soporte multi-idioma
- Ahora soporta idiomas complejos como chino, ruso, japonés y otros con alfabetos no latinos sin problemas

### 🔄 Cambios Técnicos

#### Sistema de Nombres de Objetos
- `CustomSwordItem.java` - Se actualizó el método `buildName()` para priorizar nombres específicos de `toolNames` sobre `toolNameFormat`
- `CustomArmorItem.java` - Se actualizó el método `getName()` para priorizar nombres específicos de `pieceNames` sobre `pieceNameFormat`
- **Orden de prioridad:** Nombres específicos → Formato con placeholders → Fallback al nombre del set
- Proporciona mejor control y flexibilidad para idiomas no latinos

### 📚 Actualizaciones de Documentación

#### README.md
- Se reorganizó "Armor Set — Full Example" para mostrar los `piece_names` específicos como el enfoque recomendado
- Se reorganizó "Tool Set — Full Example" para mostrar los `tool_names` específicos como el enfoque recomendado
- Se agregaron secciones de "Alternativa Avanzada" explicando el uso de formatos con placeholders
- Se agregaron notas de aclaración sobre el sistema de prioridad de nombres
- Se mejoraron los ejemplos multi-idioma (chino, español, inglés, japonés)

#### README_ES.md
- Se actualizó con cambios idénticos para coincidir con la estructura de la documentación en inglés

### 📝 Prioridad del Sistema de Nombres

**Recomendado (Nuevo Método Principal):**
- Usar `piece_names` / `tool_names` específicos para cada item en cada idioma
- Funciona perfectamente con cualquier idioma, incluyendo aquellos con reglas gramaticales complejas

**Alternativa Avanzada (Fallback):**
- Usar `piece_name_format` / `tool_name_format` con placeholders `{name}` y `{piece}`/`{tool}`
- Todavía soportado pero solo recomendado para idiomas simples basados en el alfabeto latino

---

## [1.0.1] - 2026-04-13

### ✨ Mejoras

#### Comando `/customgear reload` - Ahora Funcional Completamente
- **Antes:** El comando solo recargaba texturas, pero no actualizaba nombres, durabilidad, efectos ni atributos
- **Ahora:** El comando recarga todos los datos de los items dinámicamente en runtime

### 🔄 Cambios Técnicos

#### Items (Herramientas y Armaduras)
- Se implementó sistema dinámico de búsqueda de datos desde `GEAR_MAP`
- Todos los items ahora buscan sus datos actualizados en cada acceso en lugar de usar copias locales cacheadas
- Se sobreescribió el método `getMaxDamage(ItemStack)` para actualizar durabilidad en runtime

**Archivos modificados:**
- `CustomSwordItem.java` - Se agregó `getGearData()` y `getMaxDamage()`
- `CustomPickaxeItem.java` - Se agregó `getGearData()` y `getMaxDamage()`
- `CustomAxeItem.java` - Se agregó `getGearData()` y `getMaxDamage()`
- `CustomShovelItem.java` - Se agregó `getGearData()` y `getMaxDamage()`
- `CustomHoeItem.java` - Se agregó `getGearData()` y `getMaxDamage()`
- `CustomArmorItem.java` - Se agregó `getGearData()` y `getMaxDamage()`

#### Comando Reload
- `CustomGearCommandHandler.java` - Se implementó el método `updateGearRegistry()` que actualiza `GEAR_MAP` y `TOOL_TYPE_MAP`
- Los datos de los items ahora se recargan completamente sin necesidad de reiniciar el cliente

#### Event Handlers
- `SetBonusHandler.java` - Se actualizó para usar `getGearDataDirect()` de los items

### 🧹 Limpieza de Código

#### GearData.java
- ❌ Se removió variable sin usar: `public float toughness;`
- ❌ Se removió variable sin usar: `public float knockbackResistance;`
- ✅ Las propiedades equivalentes siguen disponibles dentro de `PieceData` (donde sí se usan)

#### DynamicResourcePack.java
- Mejorado comentario en método `close()` para aclarar que es requerido por la interfaz `Closeable`
- Verificado que todos los métodos son necesarios (incluyendo `getRootResource()`)

### 📝 Lo Que Se Actualiza en Runtime

✅ **Actualización Inmediata (sin reiniciar cliente):**
- Nombres de items (en todos los idiomas)
- Durabilidad
- Efectos al sostener (`held_effects`)
- Efectos individuales de pieza (`piece_effects`)
- Bonificaciones de set completo (`set_bonus`)
- Texturas (con `F3 + T`)

⚠️ **Requiere Reinicio del Cliente:**
- Daño de ataque (`attack_damage`)
- Velocidad de ataque (`attack_speed`)
- Velocidad de minería (`mining_speed`)
- Nivel de recolección (`harvest_level`)

### 🔧 Uso del Comando Mejorado

/customgear reload
**Antes:** Solo recargaba texturas e idiomas
**Ahora:** Recarga completamente:
1. Todos los archivos JSON
2. Datos de items (nombres, durabilidad, efectos)
3. Texturas
4. Idiomas

### 📦 Dependencias

No se agregaron nuevas dependencias. Los cambios son internos del mod.

---

## [1.0.0] - 2026-04-12

### ✨ Release Inicial (No publicado inicialmente)

Versión inicial de CustomGear con:
- Sistema data-driven basado en JSON
- Soporte para armor sets y tool sets personalizados
- Efectos al sostener y bonificaciones de set
- Texturas personalizadas o de referencia
- Soporte multiidioma
- Comando `/customgear reload` (funcionalidad limitada)
