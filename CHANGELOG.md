# Changelog

All important changelog notes for the CustomGear project.

## [1.0.2] - 2026-04-13

### ✨ Improvements

- **Multi-Language Support:** Item naming system now better supports complex languages (Chinese, Russian, Japanese, etc.)
- **Armor Textures in Reference Mode:** Armor sets can now reference layers from other mods using `armor_layers` field
- **Documentation Updates:** README updated with multi-language examples and reference mode armor layer support

### 🔧 Technical Changes

- `CustomSwordItem.java` & `CustomArmorItem.java` - Prioritize specific item names over format placeholders
- `TextureLoader.java` - Added support for armor layers in reference mode
- `GearData.java` - Added `armor_layers` field for texture configuration
- `DynamicResourcePack.java` - Added `addReferenceTexture()` method

---

## [1.0.1] - 2026-04-13

### ✨ Improvements

#### `/customgear reload` Command - Now Fully Functional
- **Before:** The command only reloaded textures, but didn't update names, durability, effects, or attributes
- **Now:** The command reloads all item data dynamically at runtime

### 🔄 Technical Changes

#### Items (Tools and Armor)
- Implemented dynamic data lookup system from `GEAR_MAP`
- All items now look up their updated data on each access instead of using cached local copies
- Overridden `getMaxDamage(ItemStack)` method to update durability at runtime

**Modified Files:**
- `CustomSwordItem.java` - Added `getGearData()` and `getMaxDamage()`
- `CustomPickaxeItem.java` - Added `getGearData()` and `getMaxDamage()`
- `CustomAxeItem.java` - Added `getGearData()` and `getMaxDamage()`
- `CustomShovelItem.java` - Added `getGearData()` and `getMaxDamage()`
- `CustomHoeItem.java` - Added `getGearData()` and `getMaxDamage()`
- `CustomArmorItem.java` - Added `getGearData()` and `getMaxDamage()`

#### Reload Command
- `CustomGearCommandHandler.java` - Implemented `updateGearRegistry()` method that updates `GEAR_MAP` and `TOOL_TYPE_MAP`
- Item data is now fully reloaded without needing to restart the client

#### Event Handlers
- `SetBonusHandler.java` - Updated to use `getGearDataDirect()` from items

### 🧹 Code Cleanup

#### GearData.java
- ❌ Removed unused variable: `public float toughness;`
- ❌ Removed unused variable: `public float knockbackResistance;`
- ✅ Equivalent properties are still available within `PieceData` (where they are actually used)

#### DynamicResourcePack.java
- Improved comment in `close()` method to clarify that it's required by the `Closeable` interface
- Verified that all methods are necessary (including `getRootResource()`)

### 📝 What Gets Updated at Runtime

✅ **Immediate Update (without restarting client):**
- Item names (in all languages)
- Durability
- Held effects (`held_effects`)
- Individual piece effects (`piece_effects`)
- Full set bonuses (`set_bonus`)
- Textures (with `F3 + T`)

⚠️ **Requires Client Restart:**
- Attack damage (`attack_damage`)
- Attack speed (`attack_speed`)
- Mining speed (`mining_speed`)
- Harvest level (`harvest_level`)

### 🔧 Improved Command Usage

/customgear reload
**Before:** Only reloaded textures and languages
**Now:** Fully reloads:
1. All JSON files
2. Item data (names, durability, effects)
3. Textures
4. Languages

### 📦 Dependencies

No new dependencies were added. Changes are internal to the mod.

---

## [1.0.0] - 2026-04-12

### ✨ Initial Release (Not initially published)

Initial version of CustomGear with:
- Data-driven system based on JSON
- Support for custom armor sets and tool sets
- Held effects and set bonuses
- Custom or reference textures
- Multi-language support
- `/customgear reload` command (limited functionality)
