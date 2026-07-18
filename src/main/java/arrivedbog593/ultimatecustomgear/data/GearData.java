package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class GearData {
    // Common fields
    public String id;
    public String type;
    public Map<String, String> names;

    @SerializedName("piece_names")
    public Map<String, Map<String, String>> pieceNames;

    public int durability;
    public boolean enchantable;
    public int enchantability;

    // Armor
    public Map<String, PieceData> pieces;

    @SerializedName("piece_effects")
    public Map<String, List<EffectData>> pieceEffects;

    @SerializedName("set_bonus")
    public SetBonusData setBonus;

    // Individual weapons and tools
    @SerializedName("attack_damage")
    public float attackDamage;

    @SerializedName("attack_damage_bonus")
    public float attackDamageBonus;

    @SerializedName("attack_speed")
    public float attackSpeed;

    @SerializedName("mining_speed")
    public float miningSpeed;

    @SerializedName("harvest_level")
    public int harvestLevel;

    @SerializedName("held_effects")
    public List<EffectData> heldEffects;

    @SerializedName("till_radius")
    public int tillRadius;

    // Tool set
    public Map<String, ToolData> tools;

    @SerializedName("tool_names")
    public Map<String, Map<String, String>> toolNames;

    // Weapon set
    public Map<String, WeaponData> weapons;

    @SerializedName("weapon_names")
    public Map<String, Map<String, String>> weaponNames;

    // Damage multiplier (sword, axe, etc.)
    @SerializedName("damage_multiplier")
    public float damageMultiplier = 1.0f;

    // Bow / Crossbow specific
    @SerializedName("arrow_damage")
    public float arrowDamage;

    @SerializedName("arrow_damage_bonus")
    public float arrowDamageBonus;

    @SerializedName("arrow_damage_multiplier")
    public float arrowDamageMultiplier = 1.0f;

    @SerializedName("charge_speed")
    public float chargeSpeed = 1.0f;

    // Recipes (para sets: map por pieza, para individuales: lista)
    public Map<String, List<RecipeData>> recipes;
    public List<RecipeData> recipe;

    /** If true, the ITEM survives fire and lava when dropped (like netherite).
     *  Does NOT make the wearer fire-immune (use fire_resistance effects for that). */
    @SerializedName("fire_resistant")
    public boolean fireResistant = false;

    // Texture
    public TextureData texture;

    // --- Internal classes ---

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

        @SerializedName("attack_damage_bonus")
        public float attackDamageBonus;

        @SerializedName("attack_speed")
        public float attackSpeed;

        @SerializedName("mining_speed")
        public float miningSpeed;

        @SerializedName("harvest_level")
        public int harvestLevel;

        @SerializedName("held_effects")
        public List<EffectData> heldEffects;

        @SerializedName("till_radius")
        public int tillRadius;

        @SerializedName("damage_multiplier")
        public float damageMultiplier = 1.0f;
    }

    public static class WeaponData {
        public int durability;

        @SerializedName("attack_damage")
        public float attackDamage;

        @SerializedName("attack_damage_bonus")
        public float attackDamageBonus;

        @SerializedName("attack_speed")
        public float attackSpeed;

        @SerializedName("damage_multiplier")
        public float damageMultiplier = 1.0f;

        @SerializedName("arrow_damage")
        public float arrowDamage;

        @SerializedName("arrow_damage_bonus")
        public float arrowDamageBonus;

        @SerializedName("arrow_damage_multiplier")
        public float arrowDamageMultiplier = 1.0f;

        @SerializedName("charge_speed")
        public float chargeSpeed = 1.0f;

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
        public Map<String, String> refs;
        @SerializedName("armor_layers")
        public Map<String, String> armorLayers;

   
        @SerializedName("render_mode")
        public String renderMode;

        @SerializedName("armor_model")
        public String armorModel;

        @SerializedName("armor_animation")
        public String armorAnimation;

        @SerializedName("armor_texture")
        public String armorTexture;
    }
}