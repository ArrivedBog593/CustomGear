package arrivedbog593.customgear.fabric.platform;

import arrivedbog593.customgear.platform.services.IMenuHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricMenuHelper implements IMenuHelper {

    @Override
    public <M extends AbstractContainerMenu, D> MenuType<M> createMenuType(
            MenuFactory<M, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        return new ExtendedScreenHandlerType<>(factory::create, codec);
    }

    @Override
    public <D> void openMenu(ServerPlayer player, MenuProvider provider, D data,
                             StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        player.openMenu(new ExtendedScreenHandlerFactory<D>() {
            @Override
            public D getScreenOpeningData(ServerPlayer opener) {
                return data;
            }

            @Override
            public @NotNull Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory,
                                                              @NotNull Player opener) {
                return provider.createMenu(id, inventory, opener);
            }
        });
    }
}
