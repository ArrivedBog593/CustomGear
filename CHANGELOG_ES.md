# Changelog

Todas las notas de cambios importantes para el proyecto CustomGear.

## [1.0.1] - 2026-04-13

### ✨ Mejoras

#### Comando `/customgear reload` - Ahora Funcional Completamente
- **Antes:** El comando solo recargaba texturas, pero no actualizaba nombres, durabilidad, efectos ni atributos
- **Ahora:** El comando recarga todos los datos de los items dinámicamente en runtime

### 🔄 Cambios Técnicos

#### Items (Herramientas y Armaduras)
- Implementado sistema dinámico de búsqueda de datos desde `GEAR_MAP`
- Todos los items ahora buscan sus datos actualizados en cada acceso en lugar de usar copias locales cacheadas
- Sobrescrito método `getMaxDamage(ItemStack)` para actualizar durabilidad en runtime

**Archivos modificados:**
- `CustomSwordItem.java` - Agregado `getGearData()` y `getMaxDamage()`
- `CustomPickaxeItem.java` - Agregado `getGearData()` y `getMaxDamage()`
- `CustomAxeItem.java` - Agregado `getGearData()` y `getMaxDamage()`
- `CustomShovelItem.java` - Agregado `getGearData()` y `getMaxDamage()`
- `CustomHoeItem.java` - Agregado `getGearData()` y `getMaxDamage()`
- `CustomArmorItem.java` - Agregado `getGearData()` y `getMaxDamage()`

#### Comando Reload
- `CustomGearCommandHandler.java` - Implementado método `updateGearRegistry()` que actualiza `GEAR_MAP` y `TOOL_TYPE_MAP`
- Los datos de los items ahora se recargan completamente sin necesidad de reiniciar el cliente

#### Event Handlers
- `SetBonusHandler.java` - Actualizado para usar `getGearDataDirect()` de los items

### 🧹 Limpieza de Código

#### GearData.java
- ❌ Removida variable sin usar: `public float toughness;`
- ❌ Removida variable sin usar: `public float knockbackResistance;`
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
