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

public class GeckoArmorItem extends CustomArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoArmorItem(GearData data, String piece) {
        super(data, piece);

        if (!ModList.get().isLoaded("geckolib")) {
            throw new IllegalStateException(
                    "GeckoArmorItem('" + data.id + "', piece='" + piece + "') fue instanciado " +
                    "pero GeckoLib no está cargado. Esta clase SOLO debe crearse cuando " +
                    "ModList.get().isLoaded(\"geckolib\") devuelve true -- revisá el guard " +
                    "en tu fábrica de items (donde decidís entre CustomArmorItem y GeckoArmorItem)."
            );
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<GeckoArmorItem> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<T> original) {

                if (this.renderer == null) {
                    this.renderer = new GeoArmorRenderer<>(new GearArmorGeoModel(getGearDataDirect()));
                }

                return this.renderer;
            }
        });
    }

   
    private static class GearArmorGeoModel extends GeoModel<GeckoArmorItem> {
        private final ResourceLocation modelResource;
        private final ResourceLocation textureResource;
        private final ResourceLocation animationResource;

        GearArmorGeoModel(GearData data) {
            String id = data.id;

            this.modelResource = ResourceLocation.fromNamespaceAndPath(ModelConstants.NAMESPACE, "geo/armor/" + id + ".geo.json");
            this.animationResource = ResourceLocation.fromNamespaceAndPath(ModelConstants.NAMESPACE, "animations/armor/" + id + ".animation.json");
            this.textureResource = ResourceLocation.fromNamespaceAndPath(ModelConstants.NAMESPACE, "textures/armor/" + id + ".png");
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