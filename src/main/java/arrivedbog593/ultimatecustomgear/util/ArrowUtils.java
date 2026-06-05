package arrivedbog593.ultimatecustomgear.util;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ArrowUtils {

    /**
     * Apply the custom damage of a bow or crossbow to an arrow.
     * Use arrowDamage as a base if it is defined; if not, use the arrow's base damage.
     * Then apply the multiplier and the bonus.
     */
    public static void applyArrowDamage(AbstractArrow arrow, GearData data) {
        float base = data.arrowDamage > 0 ? data.arrowDamage : (float) arrow.getBaseDamage();
        float multiplier = data.arrowDamageMultiplier > 0 ? data.arrowDamageMultiplier : 1.0f;
        float bonus = data.arrowDamageBonus;
        arrow.setBaseDamage((base * multiplier) + bonus);
    }
}