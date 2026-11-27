package arrivedbog593.customgear.mixin;

import arrivedbog593.customgear.items.DynamicDurability;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets {@link DynamicDurability} items answer for their own max damage, the
 * same way on every loader.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void customgear$dynamicMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() instanceof DynamicDurability item) {
            int max = item.dynamicMaxDamage(self);
            if (max > 0) cir.setReturnValue(max);
        }
    }
}
