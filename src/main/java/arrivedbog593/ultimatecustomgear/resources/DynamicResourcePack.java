package arrivedbog593.ultimatecustomgear.resources;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicResourcePack extends AbstractPackResources implements PackSink {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    // ConcurrentHashMap: Minecraft loads resources on parallel worker threads,
    // and /customgear reload mutates this map at runtime. A plain HashMap risks
    // ConcurrentModificationException if a resource reload overlaps a mutation.
    private final Map<ResourceLocation, byte[]> resources = new ConcurrentHashMap<>();
    private final Map<String, byte[]> rootResources = new ConcurrentHashMap<>();

    /** Cached content hash — invalidated on every mutation (addTexture/addRaw/clear). */
    private volatile String cachedHash = null;

    public DynamicResourcePack(PackLocationInfo info) {
        super(info);
    }

    /**
     * The KnownPack negotiation reads pack identity from the PackResources
     * itself via location().knownPack() — NOT from the Pack object built in
     * AddPackFindersEvent. Overriding here makes the content-hash KnownPack
     * the single source of truth: every Resource loaded from this pack
     * carries it, so vanilla only skips re-sending data to a client whose
     * generated pack is byte-identical to the server's.
     */
    @Override
    public @NotNull PackLocationInfo location() {
        PackLocationInfo base = super.location();
        return new PackLocationInfo(
                base.id(),
                base.title(),
                base.source(),
                Optional.of(new KnownPack("customgear", "dynamic", contentHash()))
        );
    }

    /**
     * Maximum size for a user-referenced file. Armor layers are 64x32 and
     * GeckoLib skins are typically 64x64 or 128x128, so this is orders of
     * magnitude above anything legitimate — it exists to turn "the client
     * froze" into a log line naming the file.
     */
    private static final long MAX_RESOURCE_BYTES = 8L * 1024 * 1024;

    @Override
    public void addTexture(ResourceLocation location, Path texturePath) {
        try {
            long size = Files.size(texturePath);
            if (size > MAX_RESOURCE_BYTES) {
                LOGGER.error("[CustomGear] Skipping '{}': {} MB exceeds the {} MB limit for a "
                                + "single resource. Check that the path points at a texture and "
                                + "not at another kind of file.",
                        texturePath, size / (1024 * 1024), MAX_RESOURCE_BYTES / (1024 * 1024));
                return;
            }
            resources.put(location, Files.readAllBytes(texturePath));
            cachedHash = null;
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Couldn't read texture: {}", texturePath);
        }
    }

    @Override
    public void addRaw(ResourceLocation location, byte[] data) {
        resources.put(location, data);
        cachedHash = null;
    }

    /**
     * Adds a file to the pack ROOT, e.g. addRootFile("pack.png", bytes).
     * <p>
     * Deliberately does NOT invalidate cachedHash: root files are cosmetic and
     * stay out of contentHash. See contentHash for why.
     */
    @Override
    public void addRootFile(String name, byte[] data) {
        rootResources.put(name, data);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String @NotNull ... elements) {
        byte[] data = rootResources.get(String.join("/", elements));
        if (data == null) return null;
        return () -> new ByteArrayInputStream(data);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NotNull PackType type, @NotNull ResourceLocation location) {
        byte[] data = resources.get(location);
        if (data == null) return null;
        return () -> new ByteArrayInputStream(data);
    }

    @Override
    public void listResources(@NotNull PackType type, @NotNull String namespace,
                              @NotNull String prefix, @NotNull ResourceOutput output) {
        // FIX: `loc.getPath().startsWith(prefix)` was used as a fallback without a separator,
        // which caused false positives (e.g., prefix="block" matched "blockstates/foo").
        // Now only the prefix normalized with a slash at the end is used.
        String normalizedPrefix = prefix.endsWith("/") ? prefix : prefix + "/";

        for (Map.Entry<ResourceLocation, byte[]> entry : resources.entrySet()) {
            ResourceLocation loc = entry.getKey();
            if (loc.getNamespace().equals(namespace) &&
                    loc.getPath().startsWith(normalizedPrefix)) {
                byte[] data = entry.getValue();
                output.accept(loc, () -> new ByteArrayInputStream(data));
            }
        }
    }

    @Override
    @NotNull
    public Set<String> getNamespaces(@NotNull PackType type) {
        Set<String> namespaces = new HashSet<>();
        for (ResourceLocation loc : resources.keySet()) {
            namespaces.add(loc.getNamespace());
        }
        return namespaces;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> deserializer) {
        if (deserializer == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(
                    Component.literal("CustomGear Dynamic Resources"),
                    34,  // pack_format for 1.21.1
                    java.util.Optional.empty()
            );
        }
        return null;
    }

    @Override
    public void close() {}

    public void clear() {
        resources.clear();
        rootResources.clear();
        cachedHash = null;
    }

    /**
     * Deterministic SHA-256 hash of the full pack contents (keys + bytes),
     * sorted by ResourceLocation so map iteration order never affects the result.
     * Used as the KnownPack version: if client and server generated identical
     * packs, the hashes match and vanilla skips re-sending the data. If they
     * differ in ANY byte, the versions differ and the server sends its data —
     * which is exactly the fallback we want instead of silent desync.
     * <p>
     * Cached because location() may be called many times per resource load;
     * the cache is invalidated by any mutation of the resources' map.
     * <p>
     * Root files (pack.png) are deliberately EXCLUDED. The hash drives the
     * multiplayer handshake, so anything inside it must be something that
     * changes gameplay. A server owner branding their pack with a custom icon
     * would otherwise desync every client and, under ENFORCE, kick them all
     * over a decorative image — and a player with their own local icon could
     * not join any server at all.
     */
    public String contentHash() {
        String cached = cachedHash;
        if (cached != null) return cached;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            resources.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(
                            Comparator.comparing(ResourceLocation::toString)))
                    .forEach(e -> {
                        digest.update(e.getKey().toString().getBytes(StandardCharsets.UTF_8));
                        digest.update((byte) 0); // separator: key/value boundary
                        digest.update(e.getValue());
                    });
            byte[] hash = digest.digest();
            StringBuilder sb = new StringBuilder(16);
            for (int i = 0; i < 8; i++) sb.append(String.format("%02x", hash[i]));
            String result = sb.toString();
            cachedHash = result;
            return result;
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 exists on every JVM; defensive fallback only
            return "unhashed-" + resources.size();
        }
    }
}