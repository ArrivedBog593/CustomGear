package arrivedbog593.ultimatecustomgear.items.gear.geo;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomArmorItem;
import arrivedbog593.ultimatecustomgear.resources.ModelConstants;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * Armor piece that renders as a GeckoLib 3D model when worn.
 * <p>
 * Extends CustomArmorItem, so every stat, effect, set bonus and damage
 * resistance keeps working exactly the same — this class only changes HOW the
 * armor is drawn on the body.
 * <p>
 * This class is instantiated ONLY when GearRegistry confirms that
 * texture.armor_3d is declared AND GeckoLib is loaded. Everywhere else the
 * plain CustomArmorItem is registered instead, which is why the mod still
 * works without GeckoLib installed (the armor falls back to armor_layers).
 * <p>
 * Asset resolution follows the mod's usual texture "mode" grammar:
 * <ul>
 *   <li><b>custom</b> — armor_3d paths point to the user's own files; the
 *       GearModelGenerator has already copied them into the dynamic pack at
 *       customgear:geo/armor/&lt;id&gt;.geo.json (and siblings), so the model
 *       reads them from there.</li>
 *   <li><b>reference</b> — armor_3d values are resource locations of assets
 *       shipped by another mod (or vanilla); they are used as-is, nothing is
 *       copied. NOTE: this makes that mod REQUIRED for the armor to render.</li>
 * </ul>
 */
@SuppressWarnings("deprecation")
public class GeckoArmorItem extends CustomArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoArmorItem(GearData data, String piece) {
        super(data, piece);

        if (!ModList.get().isLoaded("geckolib")) {
            throw new IllegalStateException(
                    "GeckoArmorItem('" + data.id + "', piece='" + piece + "') was instantiated "
                            + "but GeckoLib is not loaded. This class must only be created when "
                            + "ModList.get().isLoaded(\"geckolib\") is true — see the guard in "
                            + "GearRegistry.registerArmor().");
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // No controllers: animations declared in the .animation.json play through
        // GeckoLib's default handling. Custom controllers would need per-armor
        // animation names, which the JSON schema does not expose (yet).
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<GeckoArmorItem> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot,
                    HumanoidModel<T> original) {

                if (this.renderer == null) {
                    this.renderer = new GeoArmorRenderer<>(
                            new GearArmorGeoModel(getGearDataDirect()));
                }
                return this.renderer;
            }
        });
    }

    /**
     * Resolves the three GeckoLib resources for this armor, honouring the
     * texture mode: reference uses the declared resource locations directly,
     * custom points at the copies injected into the dynamic pack.
     */
    private static class GearArmorGeoModel extends GeoModel<GeckoArmorItem> {

        private final ResourceLocation modelResource;
        private final ResourceLocation textureResource;
        private final ResourceLocation animationResource;

        GearArmorGeoModel(GearData data) {
            GearData.Armor3DData armor3d = data.texture != null ? data.texture.armor3d : null;
            boolean reference = data.texture != null && "reference".equals(data.texture.mode);

            this.modelResource = reference && armor3d != null
                    ? parseOrDefault(armor3d.model, "geo/armor/" + data.id + ".geo.json")
                    : packLoc("geo/armor/" + data.id + ".geo.json");

            this.textureResource = reference && armor3d != null
                    ? parseOrDefault(armor3d.texture, "textures/armor/" + data.id + ".png")
                    : packLoc("textures/armor/" + data.id + ".png");

            this.animationResource = reference && armor3d != null
                    ? parseOrDefault(armor3d.animation, "animations/armor/" + data.id + ".animation.json")
                    : packLoc("animations/armor/" + data.id + ".animation.json");
        }

        private static ResourceLocation packLoc(String path) {
            return ResourceLocation.fromNamespaceAndPath(ModelConstants.NAMESPACE, path);
        }

        /** Reference mode: parse the declared RL, falling back to the pack path. */
        private static ResourceLocation parseOrDefault(String ref, String fallbackPath) {
            if (ref == null || ref.isBlank()) return packLoc(fallbackPath);
            ResourceLocation rl = ResourceLocation.tryParse(ref);
            return rl != null ? rl : packLoc(fallbackPath);
        }

        @Override
        public ResourceLocation getModelResource(GeckoArmorItem animatable) {
            return this.modelResource;
        }

        @Override
        public ResourceLocation getTextureResource(GeckoArmorItem animatable) {
            return this.textureResource;
        }

        @Override
        public ResourceLocation getAnimationResource(GeckoArmorItem animatable) {
            return this.animationResource;
        }
    }
}