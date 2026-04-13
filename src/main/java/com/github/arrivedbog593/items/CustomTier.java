package com.github.arrivedbog593.items;

import com.github.arrivedbog593.data.GearData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CustomTier implements Tier {

    private final GearData data;

    public CustomTier(GearData data) {
        this.data = data;
    }

    @Override
    public int getUses() {
        return data.durability;
    }

    @Override
    public float getSpeed() {
        return data.miningSpeed; // Aquí lee directo del JSON
    }

    @Override
    public float getAttackDamageBonus() {
        return data.attackDamage;
    }

    @Override
    @NotNull
    public TagKey<Block> getIncorrectBlocksForDrops() {
        // Basado en harvestLevel del JSON
        return switch (data.harvestLevel) {
            case 0  -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
            case 1  -> BlockTags.INCORRECT_FOR_STONE_TOOL;
            case 2  -> BlockTags.INCORRECT_FOR_IRON_TOOL;
            case 3  -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
            case 4  -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
            default -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return data.enchantability;
    }

    @Override
    @NotNull
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY; // Sin reparación por ítem por ahora
    }
}