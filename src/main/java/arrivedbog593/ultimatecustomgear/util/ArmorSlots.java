package arrivedbog593.ultimatecustomgear.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The four worn armour stacks of a living entity.
 * <p>
 * WHY THIS EXISTS. {@code LivingEntity.getArmorSlots()} is gone — equipment now
 * lives in a component and is read one slot at a time. Two handlers walked that
 * iterable and both would otherwise grow the same four-line loop, which is the
 * kind of duplication that drifts: the day BODY armour becomes worth counting,
 * it has to change in one place, not two.
 */
public final class ArmorSlots {

    private ArmorSlots() {}

    /**
     * BODY is deliberately absent. It is the animal-armour slot; nothing this
     * mod registers can go there, and including it would make a saddle or a
     * wolf's armour count towards a set bonus.
     */
    private static final EquipmentSlot[] HUMANOID_ARMOR = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    public static List<ItemStack> of(LivingEntity entity) {
        List<ItemStack> worn = new ArrayList<>(HUMANOID_ARMOR.length);
        for (EquipmentSlot slot : HUMANOID_ARMOR) {
            worn.add(entity.getItemBySlot(slot));
        }
        return worn;
    }
}
