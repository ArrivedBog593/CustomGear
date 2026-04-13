package com.github.arrivedbog593.items;

import com.github.arrivedbog593.data.GearData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomArmorItem extends ArmorItem {

    private final GearData gearData;
    private final String piece;

    public CustomArmorItem(GearData data, String piece) {
        super(
                buildMaterial(data, piece),
                pieceToType(piece),
                new Item.Properties()
                        .durability(data.pieces.get(piece).durability)
        );
        this.gearData = data;
        this.piece = piece;
    }

    private static Holder<ArmorMaterial> buildMaterial(GearData data, String piece) {
        GearData.PieceData pieceData = data.pieces.get(piece);
        int defense = pieceData.defense;
        float toughness = (float) pieceData.toughness;
        float knockbackResistance = (float) pieceData.knockback_resistance;

        // En 1.21.x ArmorMaterial es un record, se construye así:
        ArmorMaterial material = new ArmorMaterial(
                // defensa por slot
                Map.of(
                        ArmorItem.Type.HELMET,     piece.equals("helmet")     ? defense : 0,
                        ArmorItem.Type.CHESTPLATE, piece.equals("chestplate") ? defense : 0,
                        ArmorItem.Type.LEGGINGS,   piece.equals("leggings")   ? defense : 0,
                        ArmorItem.Type.BOOTS,      piece.equals("boots")      ? defense : 0
                ),
                data.enchantability,
                SoundEvents.ARMOR_EQUIP_IRON,
                () -> Ingredient.EMPTY,   // sin reparación por item por ahora
                List.of(),                // no overlay de textura vanilla
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

    // Getters por si los necesitas en el SetBonusHandler
    public GearData getGearData() { return gearData; }
    public String getPiece() { return piece; }


    @Override
    public @NotNull net.minecraft.network.chat.Component getName(@NotNull ItemStack stack) {
        String lang = "en_us";
        try {
            lang = net.minecraft.client.Minecraft.getInstance()
                    .getLanguageManager().getSelected();
        } catch (Exception ignored) {}

        // Obtener nombre del set en el idioma correcto
        String setName = "Unknown";
        if (gearData.name != null) {
            setName = gearData.name.getOrDefault(lang,
                    gearData.name.getOrDefault("en_us", "Unknown"));
        }

        // Obtener nombre de la pieza en el idioma correcto
        String pieceName = getDefaultPieceName(piece, lang);
        if (gearData.pieceNames != null) {
            Map<String, String> piecesForLang = gearData.pieceNames.getOrDefault(lang,
                    gearData.pieceNames.get("en_us"));
            if (piecesForLang != null && piecesForLang.containsKey(piece)) {
                pieceName = piecesForLang.get(piece);
            }
        }

        // Obtener formato en el idioma correcto
        String format = "{piece} {name}"; // fallback inglés
        if (gearData.pieceNameFormat != null) {
            format = gearData.pieceNameFormat.getOrDefault(lang,
                    gearData.pieceNameFormat.getOrDefault("en_us", "{piece} {name}"));
        }

        // Construir nombre final
        String fullName = format
                .replace("{name}", setName)
                .replace("{piece}", pieceName);

        return net.minecraft.network.chat.Component.literal(fullName);
    }

    // Valores por defecto en inglés si no se define piece_names en el JSON
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
        TooltipHelper.addPieceEffectsTooltip(tooltipComponents, gearData, piece);
        TooltipHelper.addSetBonusTooltip(tooltipComponents, gearData);
    }
}