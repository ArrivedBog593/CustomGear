package arrivedbog593.ultimatecustomgear.events;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.weapons.CustomBowItem;
import arrivedbog593.ultimatecustomgear.items.weapons.CustomCrossbowItem;
import arrivedbog593.ultimatecustomgear.registry.GearRegistry;
import arrivedbog593.ultimatecustomgear.util.ArrowUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

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
@EventBusSubscriber(modid = "ultimatecustomgear")
public class ArrowDamageHandler {

    /**
     * Marks an arrow whose damage has already been rewritten. Stored in the
     * entity's persistent data, so it survives saving and chunk reloads.
     */
    private static final String DAMAGE_APPLIED = "customgear_arrow_damage_applied";

    @SubscribeEvent
    public static void onArrowJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;

        // A trident extends AbstractArrow but is never fired by a bow. Without
        // this, throwing one while a custom bow sits in the other hand rewrites
        // the trident's damage with the bow's values.
        if (arrow instanceof ThrownTrident) return;

        // EntityJoinLevelEvent also fires when a PERSISTED arrow is re-added to
        // the level, typically on chunk load. applyArrowDamage reads the current
        // base damage and writes a scaled value back, so a second pass would
        // compound the scaling every time the chunk cycles.
        CompoundTag persistent = arrow.getPersistentData();
        if (persistent.getBooleanOr(DAMAGE_APPLIED, false)) return;

        if (!(arrow.getOwner() instanceof LivingEntity living)) return;

        // Players go through createProjectile, where the weapon that fired is
        // known for certain. Mobs do not: performRangedAttack builds the arrow
        // itself without consulting the item, so for them the held weapon is
        // still the only clue there is — and it is a reliable one, since a mob
        // shooting one bow while holding another is not a case that arises.
        if (living instanceof Player) return;

        ItemStack weapon = getWeaponStack(living);
        if (weapon.isEmpty()) return;

        Identifier loc = BuiltInRegistries.ITEM.getKey(weapon.getItem());
        GearData data = GearRegistry.lookupGear(loc);
        if (data == null) return;

        if (weapon.getItem() instanceof CustomBowItem
                || weapon.getItem() instanceof CustomCrossbowItem) {
            ArrowUtils.applyArrowDamage(arrow, data);
            persistent.putBoolean(DAMAGE_APPLIED, true);
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