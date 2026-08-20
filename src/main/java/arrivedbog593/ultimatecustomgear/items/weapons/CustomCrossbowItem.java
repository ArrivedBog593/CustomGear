package arrivedbog593.ultimatecustomgear.items.weapons;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.util.ArrowUtils;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomCrossbowItem extends CrossbowItem {

    private final GearData initialGearData;

    public CustomCrossbowItem(GearData data) {
        super(buildProps(data));
        this.initialGearData = data;
    }

    private static Properties buildProps(GearData data) {
        Properties p = new Properties().durability(data.durability);
        return data.fireResistant ? p.fireResistant() : p;
    }

    private GearData getGearData() {
        return GearLookup.getGearData(this, initialGearData);
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return data.durability > 0 ? data.durability : super.getMaxDamage(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        GearData data = getGearData();
        float chargeSpeed = data.chargeSpeed > 0 ? data.chargeSpeed : 1.0f;
        return Math.max((int) (25.0f / chargeSpeed) + 3, 28);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return Component.literal(CustomSwordItem.buildName(data, GearLookup.getCurrentLang(), "crossbow"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        TooltipHelper.addBowTooltip(tooltipComponents, getGearData());
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
        if (!TooltipHelper.detailsShown() && TooltipHelper.hasDetails(getGearData())) {
            TooltipHelper.addDetailsHint(tooltipComponents);
        }
    }

    /**
     * Applies this weapon's arrow damage at the moment it creates the
     * projectile.
     * <p>
     * THE WEAPON IS KNOWN HERE, which is the whole point. The old approach
     * guessed it from what the shooter was holding when the arrow appeared, so
     * firing a vanilla bow with a custom one in the other hand handed the custom
     * damage to the wrong arrow. 1.21.1 gives the spawned arrow no
     * back-reference to its weapon, and this is the last place that still has
     * both.
     */
    @Override
    protected @NotNull Projectile createProjectile(@NotNull Level level, @NotNull LivingEntity shooter,
                                                   @NotNull ItemStack weapon, @NotNull ItemStack ammo,
                                                   boolean isCrit) {
        Projectile projectile = super.createProjectile(level, shooter, weapon, ammo, isCrit);
        if (projectile instanceof AbstractArrow arrow) {
            ArrowUtils.applyArrowDamage(arrow, getGearData());
            // Marked so the legacy handler never scales the same arrow twice.
            arrow.getPersistentData().putBoolean("customgear_arrow_damage_applied", true);
        }
        return projectile;
    }
}
