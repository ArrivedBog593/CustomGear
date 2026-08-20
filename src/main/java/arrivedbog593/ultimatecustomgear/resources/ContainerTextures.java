package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.data.ContainerData;
import arrivedbog593.ultimatecustomgear.items.containers.CustomShulkerBlock;
import arrivedbog593.ultimatecustomgear.registry.ContainerRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.DEFAULT_CHEST_TEXTURE;

/**
 * Resolves the entity-style texture a rendered container draws with.
 * <p>
 * NOT AN ATLAS. Vanilla puts chest textures in a dedicated atlas and addresses
 * them through Material; Sheets.chestSheet() is just entityCutout of that atlas,
 * so pointing entityCutout straight at the PNG gives the same result without
 * generating an atlas definition for every user texture. The UVs come from the
 * 64x64 declared in the LayerDefinition, which without an atlas maps to the
 * whole file — exactly vanilla's unwrap.
 * <p>
 * CACHED PER BLOCK AND HALF. The renderer runs once per container per frame, and
 * building a ResourceLocation there would allocate for nothing. Cleared on
 * reload, since a definition can name a different texture afterward.
 */
public final class ContainerTextures {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    /**
     * Cache key: a chest needs three textures, one per half, and they are NOT
     * the single one cropped — the halves are 15 wide and unwrap differently.
     */
    private record Key(Block block, ChestType type) {}

    private static final Map<Key, ResourceLocation> CACHE = new ConcurrentHashMap<>();

    private ContainerTextures() {}

    /** The texture for a placed container, or vanilla's when it declares none. */
    public static ResourceLocation forBlock(Block block, ChestType type) {
        return CACHE.computeIfAbsent(new Key(block, type), ContainerTextures::resolve);
    }

    /** Items are always single, so this is the form the BEWLR calls. */
    public static ResourceLocation forBlock(Block block) {
        return forBlock(block, ChestType.SINGLE);
    }
    /** A shulker has one 64x64 unwrap, under the same 'single' key as a chest. */
    public static ResourceLocation forShulker(Block block) {
        return CACHE.computeIfAbsent(new Key(block, ChestType.SINGLE), ContainerTextures::resolve);
    }

    /** Called from /customgear reload — a definition may name a new texture. */
    public static void clearCache() {
        CACHE.clear();
    }

    // ── Resolution ────────────────────────────────────────────────────────────

    /** The refs key each half reads. */
    private static String keyFor(ChestType type) {
        return switch (type) {
            case LEFT  -> "left";
            case RIGHT -> "right";
            default    -> "single";
        };
    }

    private static String vanillaFor(ChestType type) {
        return switch (type) {
            case LEFT  -> "minecraft:textures/entity/chest/normal_left.png";
            case RIGHT -> "minecraft:textures/entity/chest/normal_right.png";
            default    -> DEFAULT_CHEST_TEXTURE;
        };
    }

    private static ResourceLocation resolve(Key key) {
        ContainerContentData data = ContainerRegistry.CONTAINER_MAP.get(
                BuiltInRegistries.BLOCK.getKey(key.block()));
        String fallback = key.block() instanceof CustomShulkerBlock
                ? "minecraft:textures/entity/shulker/shulker.png"
                : vanillaFor(key.type());

        if (data == null || data.texture == null || data.texture.refs == null) {
            return parse(fallback);
        }

        String value = data.texture.refs.get(keyFor(key.type()));
        // A chest that only declares 'single' keeps vanilla's halves rather than
        // stretching its own texture across a mesh it was not drawn for. The
        // parser warns about this when the definition allows doubling.
        if (value == null) return parse(fallback);

        // A resource location points at somebody else's texture and is used as
        // written; anything else is a file this pack ships, which the generator
        // already copied into the dynamic pack under a name derived from the id.
        if (value.contains(":")) {
            // Two accepted forms, told apart by the extension. Without this the
            // chest would be the one place in the mod where a reference needs
            // 'textures/' and '.png' spelled out, while every block face gets
            // them added for free — same namespace, same assets folder, two
            // different rules to remember for no reason.
            String normalized = TextureRef.expandTexturePath(value);
            ResourceLocation parsed = ResourceLocation.tryParse(normalized);
            if (parsed != null) return parsed;
            LOGGER.error("[CustomGear] Container '{}': '{}' is not a valid resource location — "
                    + "using the default chest texture.", data.id, value);
            return parse(fallback);
        }
        return packLocation(data.id, key.type());
    }

    private static ResourceLocation packLocation(String id, ChestType type) {
        String key = keyFor(type);
        String suffix = key.equals("single") ? "" : "_" + key;
        return ResourceLocation.fromNamespaceAndPath(
                ModelConstants.NAMESPACE, "textures/entity/container/" + id + suffix + ".png");
    }

    private static ResourceLocation parse(String s) {
        ResourceLocation rl = ResourceLocation.tryParse(s);
        if (rl == null) throw new IllegalStateException("Bad built-in texture path: " + s);
        return rl;
    }

    // ── Injection ─────────────────────────────────────────────────────────────

    /**
     * Copies a chest's own textures into the dynamic pack. Called from the model
     * generator, which is the only place with a PackSink.
     */
    public static void inject(PackSink pack, ContainerContentData data) {
        String kind = data.container.kind();
        if (!ContainerData.KIND_CHEST.equals(kind) && !ContainerData.KIND_SHULKER.equals(kind)) return;
        if (data.texture == null || data.texture.refs == null) return;

        for (ChestType type : ChestType.values()) {
            String value = data.texture.refs.get(keyFor(type));
            if (value == null || value.contains(":")) continue;   // absent or foreign

            Optional<Path> file = TextureLoader.resolveUserResource(value);
            if (file.isEmpty()) {
                LOGGER.error("[CustomGear] Container '{}': texture file not found in the config "
                        + "folder or any pack zip: {}", data.id, value);
                continue;
            }
            pack.addTextureWithMeta(packLocation(data.id, type), file.get());
        }
    }
}