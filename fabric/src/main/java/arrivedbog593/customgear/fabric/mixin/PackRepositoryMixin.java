package arrivedbog593.customgear.fabric.mixin;

import arrivedbog593.customgear.CustomGear;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the dynamic pack to every pack repository, as NeoForge's
 * AddPackFindersEvent does: the client's resource packs and the server's data
 * packs. A repository says which it is through its built-in source
 * (ClientPackSource or ServerPacksSource); one without is not touched.
 * <p>
 * The source asks for the pack on every scan, so it does not matter that the
 * client builds its repository before the content is loaded.
 */
@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {

    @Shadow
    @Final
    @Mutable
    private java.util.Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void customgear$addDynamicPack(RepositorySource[] given, CallbackInfo ci) {
        PackType type = null;
        for (RepositorySource source : this.sources) {
            if (source instanceof BuiltInPackSource builtIn) {
                type = ((BuiltInPackSourceAccessor) builtIn).customgear$getPackType();
                break;
            }
        }
        if (type == null) return;

        PackType packType = type;
        RepositorySource dynamic = consumer -> {
            Pack pack = CustomGear.createPack(packType);
            if (pack != null) consumer.accept(pack);
        };
        this.sources = ImmutableSet.<RepositorySource>builder()
                .addAll(this.sources)
                .add(dynamic)
                .build();
    }
}
