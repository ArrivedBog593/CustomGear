package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.CustomArmorItem;
import com.github.arrivedbog593.items.CustomSwordItem;
import com.github.arrivedbog593.items.CustomToolItem;
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
                case "armor_set" -> registerArmor(data);
                case "sword"     -> registerSword(data);
                case "tool_set"  -> registerToolSet(data);
                case "pickaxe", "axe", "shovel", "hoe" -> registerTool(data);
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

        String[] validTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};

        for (String toolType : validTypes) {
            if (!data.tools.containsKey(toolType)) continue;

            GearData.ToolData toolData = data.tools.get(toolType);
            String itemId = data.id + "_" + toolType;

            // Create a derived GearData for this specific tool
            GearData derived = buildDerived(data, toolType, toolData);

            ITEMS.register(itemId, () -> toolType.equals("sword")
                    ? new CustomSwordItem(derived)
                    : CustomToolItem.create(derived));

            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", itemId);
            GEAR_MAP.put(loc, derived);
            TOOL_TYPE_MAP.put(loc, toolType);
        }
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
        derived.enchantability = parent.enchantability;
        derived.heldEffects = toolData.heldEffects != null
                ? toolData.heldEffects
                : parent.heldEffects;
        derived.texture = parent.texture;
        return derived;
    }
}