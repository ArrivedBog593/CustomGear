package com.github.arrivedbog593.items;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
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
    private final String piece;

    public CustomArmorItem(GearData data, String piece) {
        super(
                buildMaterial(data, piece),
                pieceToType(piece),
                new Item.Properties()
                        .durability(data.pieces.get(piece).durability)
        );
        this.initialGearData = data;
        this.piece = piece;
    }

    private GearData getGearData() {
        ResourceLocation itemLocation = BuiltInRegistries.ITEM.getKey(this);
        if (GearRegistry.GEAR_MAP.containsKey(itemLocation)) {
            return GearRegistry.GEAR_MAP.get(itemLocation);
        }
        return initialGearData;
    }

    public GearData getGearDataDirect() { return getGearData(); }

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
        int defense = pieceData.defense;
        float toughness = (float) pieceData.toughness;
        float knockbackResistance = (float) pieceData.knockback_resistance;

        ArmorMaterial material = new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET,     piece.equals("helmet")     ? defense : 0,
                        ArmorItem.Type.CHESTPLATE, piece.equals("chestplate") ? defense : 0,
                        ArmorItem.Type.LEGGINGS,   piece.equals("leggings")   ? defense : 0,
                        ArmorItem.Type.BOOTS,      piece.equals("boots")      ? defense : 0
                ),
                data.enchantability,
                SoundEvents.ARMOR_EQUIP_IRON,
                () -> Ingredient.EMPTY,
                List.of(),
                toughness,
                knockbackResistance
        );

        return Holder.direct(material);
    }

    private static ArmorItem.Type pieceToType(String piece) {
        return switch (piece) {
            case "helmet"     -> ArmorItem.Type.HELMET;
            case "chestplate" -> ArmorItem.Type.CHESTPLATE;
            case "leggings"   -> ArmorItem.Type.LEGGINGS;
            case "boots"      -> ArmorItem.Type.BOOTS;
            default -> throw new IllegalArgumentException("Pieza inválida: " + piece);
        };
    }

    public String getPiece() { return piece; }

    @Override
    public @NotNull net.minecraft.network.chat.Component getName(@NotNull ItemStack stack) {
        String lang = "en_us";
        try {
            lang = net.minecraft.client.Minecraft.getInstance()
                    .getLanguageManager().getSelected();
        } catch (Exception ignored) {}

        GearData data = getGearData();

        String setName = "Unknown";
        if (data.name != null) {
            setName = data.name.getOrDefault(lang,
                    data.name.getOrDefault("en_us", "Unknown"));
        }

        String pieceName = getDefaultPieceName(piece, lang);
        if (data.pieceNames != null) {
            Map<String, String> piecesForLang = data.pieceNames.getOrDefault(lang,
                    data.pieceNames.get("en_us"));
            if (piecesForLang != null && piecesForLang.containsKey(piece)) {
                pieceName = piecesForLang.get(piece);
            }
        }

        String format = "{piece} {name}";
        if (data.pieceNameFormat != null) {
            format = data.pieceNameFormat.getOrDefault(lang,
                    data.pieceNameFormat.getOrDefault("en_us", "{piece} {name}"));
        }

        String fullName = format
                .replace("{name}", setName)
                .replace("{piece}", pieceName);

        return net.minecraft.network.chat.Component.literal(fullName);
    }

    private String getDefaultPieceName(String piece, String lang) {
        if (lang.startsWith("es")) {
            return switch (piece) {
                case "helmet"     -> "Casco";
                case "chestplate" -> "Pechera";
                case "leggings"   -> "Pantalones";
                case "boots"      -> "Botas";
                default           -> piece;
            };
        }
        return switch (piece) {
            case "helmet"     -> "Helmet";
            case "chestplate" -> "Chestplate";
            case "leggings"   -> "Leggings";
            case "boots"      -> "Boots";
            default           -> piece;
        };
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
