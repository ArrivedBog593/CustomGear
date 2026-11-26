package arrivedbog593.customgear.fabric.mixin;

import arrivedbog593.customgear.events.DamageResistanceHandler;
import arrivedbog593.customgear.events.MobDropHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The two death and damage hooks NeoForge has as events and Fabric API does
 * not: LivingIncomingDamageEvent and LivingDropsEvent.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected int lastHurtByPlayerTime;

    /** Armor damage resistances, applied to the damage before anything else reads it. */
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float customgear$applyResistances(float amount, DamageSource source) {
        return DamageResistanceHandler.modifyIncomingDamage((LivingEntity) (Object) this, source, amount);
    }

    /**
     * Mob drops, after the loot table and equipment. "Recently hit" is the
     * same test vanilla runs at the top of this method for player-only loot.
     */
    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void customgear$addMobDrops(ServerLevel level, DamageSource source, CallbackInfo ci) {
        MobDropHandler.onLivingDrops((LivingEntity) (Object) this, source,
                this.lastHurtByPlayerTime > 0, level::addFreshEntity);
    }
}
