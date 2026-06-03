package com.github.arrivedbog593.items.gear;

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
        return data.durability > 0 ? data.durability : 64;
    }

    @Override
    public float getSpeed() {
        return data.miningSpeed > 0 ? data.miningSpeed : 1.0f;
    }

    @Override
    public float getAttackDamageBonus() {
        return data.attackDamageBonus > 0 ? data.attackDamageBonus : 0;
    }

    @Override
    @NotNull
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return switch (data.harvestLevel) {
            case 0  -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
            case 1  -> BlockTags.INCORRECT_FOR_STONE_TOOL;
            case 2  -> BlockTags.INCORRECT_FOR_IRON_TOOL;
            case 3  -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
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
        return Ingredient.EMPTY;
    }
}