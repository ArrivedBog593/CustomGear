package com.github.arrivedbog593.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicResourcePack extends AbstractPackResources {

    private final Map<ResourceLocation, byte[]> resources = new HashMap<>();

    public DynamicResourcePack(PackLocationInfo info) {
        super(info);
    }

    public void addTexture(ResourceLocation location, Path texturePath) {
        try {
            resources.put(location, Files.readAllBytes(texturePath));
        } catch (IOException e) {
            System.err.println("[CustomGear] No se pudo leer textura: " + texturePath);
        }
    }

    public void addRaw(ResourceLocation location, byte[] data) {
        resources.put(location, data);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String @NotNull ... elements) {
        return null;
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
        for (Map.Entry<ResourceLocation, byte[]> entry : resources.entrySet()) {
            ResourceLocation loc = entry.getKey();
            // Aseguramos que el prefix siempre tenga slash al final para comparar bien
            String normalizedPrefix = prefix.endsWith("/") ? prefix : prefix + "/";
            if (loc.getNamespace().equals(namespace) &&
                    (loc.getPath().startsWith(normalizedPrefix) ||
                            loc.getPath().startsWith(prefix))) {
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
    public @Nullable <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> deserializer)
            throws IOException {
        if (deserializer == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(
                    Component.literal("CustomGear Dynamic Resources"),
                    34,  // pack_format para 1.21.1
                    java.util.Optional.empty()
            );
        }
        return null;
    }

    @Override
    public void close() {}
}