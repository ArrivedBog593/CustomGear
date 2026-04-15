package com.github.arrivedbog593.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class GearData {
    // Campos comunes
    public String id;
    public String type;
    public Map<String, String> name;

    @SerializedName("piece_name_format")
    public Map<String, String> pieceNameFormat;

    @SerializedName("piece_names")
    public Map<String, Map<String, String>> pieceNames;

    public int durability;
    public boolean enchantable;
    public int enchantability;

    // Armadura
    public Map<String, PieceData> pieces;

    @SerializedName("piece_effects")
    public Map<String, List<EffectData>> pieceEffects;

    @SerializedName("set_bonus")
    public SetBonusData setBonus;

    // Armas y herramientas individuales
    @SerializedName("attack_damage")
    public float attackDamage;

    @SerializedName("attack_speed")
    public float attackSpeed;

    @SerializedName("mining_speed")
    public float miningSpeed;

    @SerializedName("harvest_level")
    public int harvestLevel;

    @SerializedName("held_effects")
    public List<EffectData> heldEffects;

    // Tool set
    public Map<String, ToolData> tools;

    @SerializedName("tool_name_format")
    public Map<String, String> toolNameFormat;

    @SerializedName("tool_names")
    public Map<String, Map<String, String>> toolNames;

    // Textura
    public TextureData texture;

    // --- Clases internas ---

    public static class PieceData {
        public int durability;
        public int defense;
        public double knockback_resistance;
        public double toughness;
    }

    public static class ToolData {
        public int durability;

        @SerializedName("attack_damage")
        public float attackDamage;

        @SerializedName("attack_speed")
        public float attackSpeed;

        @SerializedName("mining_speed")
        public float miningSpeed;

        @SerializedName("harvest_level")
        public int harvestLevel;

        @SerializedName("held_effects")
        public List<EffectData> heldEffects;
    }

    public static class EffectData {
        public String effect;
        public int amplifier;
    }

    public static class SetBonusData {
        @SerializedName("required_pieces")
        public int requiredPieces;
        public List<EffectData> effects;
    }

    public static class TextureData {
        public String mode;
        public String path;
        public Map<String, String> refs;
        @SerializedName("armor_layers")
        public Map<String, String> armorLayers;
    }
}