package com.github.arrivedbog593.items.gear;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.util.GearLookup;
import com.github.arrivedbog593.util.TooltipHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomArmorItem extends ArmorItem {

    private final GearData initialGearData;
    private final String   piece;

    public CustomArmorItem(GearData data, String piece) {
        super(
                buildMaterial(data, piece),
                pieceToType(piece),
                new Properties()
                        .durability(data.pieces.get(piece).durability)
        );
        this.initialGearData = data;
        this.piece = piece;
    }

    private GearData getGearData() {
        return GearLookup.getGearData(this, initialGearData);
    }

    /** Called by SetBonusHandler — keeps the existing public API. */
    public GearData getGearDataDirect() {
        return getGearData();
    }

    public String getPiece() {
        return piece;
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        GearData data = getGearData();
        if (data.pieces != null && data.pieces.containsKey(piece)) {
            int dur = data.pieces.get(piece).durability;
            return dur > 0 ? dur : super.getMaxDamage(stack);
        }
        return super.getMaxDamage(stack);
    }

    private static Holder<ArmorMaterial> buildMaterial(GearData data, String piece) {
        GearData.PieceData pieceData = data.pieces.get(piece);
        List<ArmorMaterial.Layer> layers = buildLayers(data);

        ArmorMaterial material = new ArmorMaterial(
                Map.of(
                        Type.HELMET,     piece.equals("helmet")     ? pieceData.defense : 0,
                        Type.CHESTPLATE, piece.equals("chestplate") ? pieceData.defense : 0,
                        Type.LEGGINGS,   piece.equals("leggings")   ? pieceData.defense : 0,
                        Type.BOOTS,      piece.equals("boots")      ? pieceData.defense : 0
                ),
                data.enchantability,
                SoundEvents.ARMOR_EQUIP_IRON,
                () -> Ingredient.EMPTY,
                layers,
                (float) pieceData.toughness,
                (float) pieceData.knockback_resistance
        );

        return Holder.direct(material);
    }

    private static List<ArmorMaterial.Layer> buildLayers(GearData data) {
        if (data.texture == null || data.texture.armorLayers == null) {
            return List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("iron")));
        }

        if (data.texture.mode != null && data.texture.mode.equals("reference")) {
            String layer1Ref = data.texture.armorLayers.get("layer_1");
            if (layer1Ref != null) {
                ResourceLocation rl = ResourceLocation.parse(layer1Ref);
                String path = rl.getPath();
                if (path.startsWith("models/armor/")) {
                    path = path.substring("models/armor/".length());
                }
                if (path.endsWith("_layer_1")) {
                    path = path.substring(0, path.length() - "_layer_1".length());
                }
                return List.of(new ArmorMaterial.Layer(
                        ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), path)));
            }
        }

        if (data.texture.mode != null && data.texture.mode.equals("custom")) {
            return List.of(new ArmorMaterial.Layer(
                    ResourceLocation.fromNamespaceAndPath("customgear", data.id)));
        }

        return List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("iron")));
    }

    private static Type pieceToType(String piece) {
        return switch (piece) {
            case "helmet"     -> Type.HELMET;
            case "chestplate" -> Type.CHESTPLATE;
            case "leggings"   -> Type.LEGGINGS;
            case "boots"      -> Type.BOOTS;
            default -> throw new IllegalArgumentException("Invalid piece: " + piece);
        };
    }

    @Override
    public @NotNull net.minecraft.network.chat.Component getName(@NotNull ItemStack stack) {
        GearData data = getGearData();
        if (data.pieceNames == null) return super.getName(stack);

        String lang = GearLookup.getCurrentLang();
        Map<String, String> namesForLang = data.pieceNames.getOrDefault(lang, data.pieceNames.get("en_us"));
        if (namesForLang != null && namesForLang.containsKey(piece)) {
            return net.minecraft.network.chat.Component.literal(namesForLang.get(piece));
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<net.minecraft.network.chat.Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        GearData data = getGearData();
        TooltipHelper.addPieceEffectsTooltip(tooltipComponents, data, piece);
        TooltipHelper.addSetBonusTooltip(tooltipComponents, data);
    }
}
