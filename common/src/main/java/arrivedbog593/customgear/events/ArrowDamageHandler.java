package arrivedbog593.customgear.events;

import arrivedbog593.customgear.data.GearData;
import arrivedbog593.customgear.items.weapons.CustomBowItem;
import arrivedbog593.customgear.items.weapons.CustomCrossbowItem;
import arrivedbog593.customgear.registry.GearRegistry;
import arrivedbog593.customgear.util.ArrowUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Applies a custom bow's or crossbow's arrow damage to the projectiles it fires.
 * <p>
 * KNOWN LIMITATION: the weapon is inferred from what the shooter is HOLDING, not
 * from what actually fired the projectile — 1.21.1 gives the spawned arrow no
 * back-reference to its weapon. A vanilla bow fired while a custom bow sits in
 * the other hand therefore still gets the custom damage. Fixing that properly
 * means overriding the shooting path in CustomBowItem/CustomCrossbowItem, where
 * the weapon is known; the guards below only remove the cases that can be told
 * apart from here.
 */
public class ArrowDamageHandler {

    /**
     * Marks an arrow whose damage has already been rewritten. Stored in the
     * entity's tags (vanilla "Tags" NBT), so it survives saving and chunk reloads.
     */
    public static final String DAMAGE_APPLIED = "customgear_arrow_damage_applied";

    /** Called by each loader when an entity is added to a level, spawned or loaded. */
    public static void onEntityJoinLevel(Entity entity, Level level) {
        if (level.isClientSide()) return;
        if (!(entity instanceof AbstractArrow arrow)) return;

        // A trident extends AbstractArrow but is never fired by a bow. Without
        // this, throwing one while a custom bow sits in the other hand rewrites
        // the trident's damage with the bow's values.
        if (arrow instanceof ThrownTrident) return;

        // EntityJoinLevelEvent also fires when a PERSISTED arrow is re-added to
        // the level, typically on chunk load. applyArrowDamage reads the current
        // base damage and writes a scaled value back, so a second pass would
        // compound the scaling every time the chunk cycles.
        if (arrow.getTags().contains(DAMAGE_APPLIED)) return;

        if (!(arrow.getOwner() instanceof LivingEntity living)) return;

        // Players go through createProjectile, where the weapon that fired is
        // known for certain. Mobs do not: performRangedAttack builds the arrow
        // itself without consulting the item, so for them the held weapon is
        // still the only clue there is — and it is a reliable one, since a mob
        // shooting one bow while holding another is not a case that arises.
        if (living instanceof Player) return;

        ItemStack weapon = getWeaponStack(living);
        if (weapon.isEmpty()) return;

        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(weapon.getItem());
        GearData data = GearRegistry.lookupGear(loc);
        if (data == null) return;

        if (weapon.getItem() instanceof CustomBowItem
                || weapon.getItem() instanceof CustomCrossbowItem) {
            ArrowUtils.applyArrowDamage(arrow, data);
            arrow.addTag(DAMAGE_APPLIED);
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