package arrivedbog593.ultimatecustomgear.client.render;

import arrivedbog593.ultimatecustomgear.CustomGearMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Model layers this mod bakes.
 * <p>
 * ONE layer for every chest, not one per definition. The mesh is fixed — a JSON
 * changes the texture on it, never its shape — so registering per definition
 * would bake identical geometry once per container for nothing.
 */
public final class CustomModelLayers {

    public static final ModelLayerLocation CHEST_SINGLE = layer("chest");
    public static final ModelLayerLocation CHEST_LEFT   = layer("chest_left");
    public static final ModelLayerLocation CHEST_RIGHT  = layer("chest_right");

    private static ModelLayerLocation layer(String path) {
        return new ModelLayerLocation(
                ResourceLocation.fromNamespaceAndPath(CustomGearMod.MOD_ID, path), "main");
    }

    private CustomModelLayers() {}
}