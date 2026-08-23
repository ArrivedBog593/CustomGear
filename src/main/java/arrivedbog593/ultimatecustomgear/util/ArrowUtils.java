package arrivedbog593.ultimatecustomgear.util;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

public class ArrowUtils {

    /**
     * Apply the custom damage of a bow or crossbow to an arrow.
     * Use arrowDamage as a base if it is defined; if not, use the arrow's base damage.
     * Then apply the multiplier and the bonus.
     */
    public static void applyArrowDamage(AbstractArrow arrow, GearData data) {
        double vanillaBase = arrow.baseDamage;

        double finalDamage;
        if (data.arrowDamage > 0) {
            double scale = data.arrowDamage / 9.0;
            finalDamage = vanillaBase * scale;
        } else {
            finalDamage = vanillaBase;
        }

        float multiplier = data.arrowDamageMultiplier > 0 ? data.arrowDamageMultiplier : 1.0f;
        float bonus = data.arrowDamageBonus;

        arrow.setBaseDamage((finalDamage * multiplier) + bonus);
    }
}