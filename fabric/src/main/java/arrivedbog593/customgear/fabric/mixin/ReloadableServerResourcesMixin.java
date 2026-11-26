package arrivedbog593.customgear.fabric.mixin;

import arrivedbog593.customgear.events.RecipePatchHandler;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies recipe_patch once a datapack load has bound its tags, before the
 * recipes are sent to players: the point where NeoForge fires TagsUpdatedEvent
 * (SERVER_DATA_LOAD), so both loaders patch at the same moment.
 */
@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {

    @Shadow
    @Final
    private RecipeManager recipes;

    @Shadow
    @Final
    private ReloadableServerRegistries.Holder fullRegistryHolder;

    @Inject(method = "updateRegistryTags()V", at = @At("TAIL"))
    private void customgear$patchRecipes(CallbackInfo ci) {
        RecipePatchHandler.onDataLoaded(recipes, fullRegistryHolder.get());
    }
}
