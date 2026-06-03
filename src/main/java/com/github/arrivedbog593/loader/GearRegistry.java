package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.gear.CustomArmorItem;
import com.github.arrivedbog593.items.weapons.CustomSwordItem;
import com.github.arrivedbog593.items.tools.CustomToolItem;
import com.github.arrivedbog593.items.weapons.CustomBowItem;
import com.github.arrivedbog593.items.weapons.CustomCrossbowItem;
import com.github.arrivedbog593.items.weapons.CustomShieldItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GearRegistry {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, "customgear");

    public static final Map<ResourceLocation, GearData> GEAR_MAP = new HashMap<>();

    // Additional map: item id → tool type (for tool_set)
    public static final Map<ResourceLocation, String> TOOL_TYPE_MAP = new HashMap<>();

    public static void register(IEventBus modEventBus, List<GearData> gearList) {
        for (GearData data : gearList) {
            switch (data.type) {
                case "armor_set"  -> registerArmor(data);
                case "tool_set"   -> registerToolSet(data);
                case "weapon_set" -> registerWeaponSet(data);
                case "sword"      -> registerSword(data);
                case "bow"        -> registerBow(data);
                case "crossbow"   -> registerCrossbow(data);
                case "shield"     -> registerShield(data);
                case "pickaxe", "axe", "shovel", "hoe" -> registerTool(data);
                case "block", "item", "fluid", "advancement" -> {}
                default -> LOGGER.warn("[CustomGear] Unknown type: {}", data.type);
            }
        }
        ITEMS.register(modEventBus);
    }

    private static void registerArmor(GearData data) {
        if (data.pieces == null) return;

        String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
        for (String piece : pieces) {
            if (!data.pieces.containsKey(piece)) continue;
            String itemId = data.id + "_" + piece;
            ITEMS.register(itemId, () -> new CustomArmorItem(data, piece));
            GEAR_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", itemId), data);
        }
    }

    private static void registerSword(GearData data) {
        ITEMS.register(data.id, () -> new CustomSwordItem(data));
        GEAR_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
    }

    private static void registerTool(GearData data) {
        ITEMS.register(data.id, () -> CustomToolItem.create(data));
        GEAR_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
    }

    private static void registerToolSet(GearData data) {
        if (data.tools == null) return;
        String[] validTypes = {"pickaxe", "axe", "shovel", "hoe"};
        for (String toolType : validTypes) {
            if (!data.tools.containsKey(toolType)) continue;
            GearData.ToolData toolData = data.tools.get(toolType);
            String itemId = data.id + "_" + toolType;
            GearData derived = buildDerived(data, toolType, toolData);
            ITEMS.register(itemId, () -> CustomToolItem.create(derived));
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", itemId);
            GEAR_MAP.put(loc, derived);
            TOOL_TYPE_MAP.put(loc, toolType);
        }
    }

    private static void registerBow(GearData data) {
        ITEMS.register(data.id, () -> new CustomBowItem(data));
        GEAR_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
    }

    private static void registerCrossbow(GearData data) {
        ITEMS.register(data.id, () -> new CustomCrossbowItem(data));
        GEAR_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
    }

    private static void registerShield(GearData data) {
        ITEMS.register(data.id, () -> new CustomShieldItem(data));
        GEAR_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
    }

    private static void registerWeaponSet(GearData data) {
        if (data.weapons == null) return;
        String[] validTypes = {"sword", "bow", "crossbow", "shield"};
        for (String weaponType : validTypes) {
            if (!data.weapons.containsKey(weaponType)) continue;
            GearData.WeaponData weaponData = data.weapons.get(weaponType);
            String itemId = data.id + "_" + weaponType;
            GearData derived = buildWeaponDerived(data, weaponType, weaponData);
            ITEMS.register(itemId, () -> switch (weaponType) {
                case "sword"    -> new CustomSwordItem(derived);
                case "bow"      -> new CustomBowItem(derived);
                case "crossbow" -> new CustomCrossbowItem(derived);
                case "shield"   -> new CustomShieldItem(derived);
                default -> throw new IllegalArgumentException("Invalid weapon type: " + weaponType);
            });
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", itemId);
            GEAR_MAP.put(loc, derived);
        }
    }

    public static GearData buildWeaponDerived(GearData parent, String weaponType,
                                              GearData.WeaponData weaponData) {
        GearData derived = new GearData();
        derived.id = parent.id + "_" + weaponType;
        derived.type = weaponType;
        derived.name = parent.name;
        derived.weaponNames = parent.weaponNames;
        derived.durability = weaponData.durability > 0 ? weaponData.durability : parent.durability;
        derived.attackDamage = weaponData.attackDamage;
        derived.attackDamageBonus = weaponData.attackDamageBonus;
        derived.attackSpeed = weaponData.attackSpeed;
        derived.damageMultiplier = weaponData.damageMultiplier > 0 ? weaponData.damageMultiplier : 1.0f;
        derived.arrowDamage = weaponData.arrowDamage;
        derived.arrowDamageBonus = weaponData.arrowDamageBonus;
        derived.arrowDamageMultiplier = weaponData.arrowDamageMultiplier > 0 ? weaponData.arrowDamageMultiplier : 1.0f;
        derived.chargeSpeed = weaponData.chargeSpeed > 0 ? weaponData.chargeSpeed : 1.0f;
        derived.enchantable = parent.enchantable;
        derived.enchantability = parent.enchantability;
        derived.heldEffects = weaponData.heldEffects != null ? weaponData.heldEffects : parent.heldEffects;
        derived.texture = parent.texture;
        return derived;
    }

    // Builds a GearData for a specific tool from the tool_set and the tool's data'
    public static GearData buildDerived(GearData parent, String toolType,
                                         GearData.ToolData toolData) {
        GearData derived = new GearData();
        derived.id = parent.id + "_" + toolType;
        derived.type = toolType;
        derived.name = parent.name;
        derived.toolNames = parent.toolNames;
        derived.durability = toolData.durability > 0 ? toolData.durability : parent.durability;
        derived.attackDamage = toolData.attackDamage;
        derived.attackDamageBonus = toolData.attackDamageBonus;
        derived.attackSpeed = toolData.attackSpeed;
        derived.miningSpeed = toolData.miningSpeed;
        derived.harvestLevel = toolData.harvestLevel;
        derived.tillRadius = toolData.tillRadius;
        derived.enchantable = parent.enchantable;
        derived.damageMultiplier = toolData.damageMultiplier > 0 ? toolData.damageMultiplier : 1.0f;
        derived.enchantability = parent.enchantability;
        derived.heldEffects = toolData.heldEffects != null
                ? toolData.heldEffects
                : parent.heldEffects;
        derived.texture = parent.texture;
        return derived;
    }
}