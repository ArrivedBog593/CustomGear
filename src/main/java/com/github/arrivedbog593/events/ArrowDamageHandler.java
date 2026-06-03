package com.github.arrivedbog593.events;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.weapons.CustomBowItem;
import com.github.arrivedbog593.items.weapons.CustomCrossbowItem;
import com.github.arrivedbog593.loader.GearRegistry;
import com.github.arrivedbog593.util.ArrowUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = "customgear")
public class ArrowDamageHandler {

    @SubscribeEvent
    public static void onArrowJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;

        if (!(arrow.getOwner() instanceof LivingEntity living)) return;

        ItemStack weapon = getWeaponStack(living);
        if (weapon.isEmpty()) return;

        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(weapon.getItem());
        GearData data = GearRegistry.GEAR_MAP.get(loc);
        if (data == null) return;

        if (weapon.getItem() instanceof CustomBowItem
                || weapon.getItem() instanceof CustomCrossbowItem) {
            ArrowUtils.applyArrowDamage(arrow, data);
        }
    }

    private static ItemStack getWeaponStack(LivingEntity entity) {
        ItemStack main = entity.getMainHandItem();
        if (main.getItem() instanceof CustomBowItem
                || main.getItem() instanceof CustomCrossbowItem) return main;
        ItemStack off = entity.getOffhandItem();
        if (off.getItem() instanceof CustomBowItem
                || off.getItem() instanceof CustomCrossbowItem) return off;
        return ItemStack.EMPTY;
    }
}