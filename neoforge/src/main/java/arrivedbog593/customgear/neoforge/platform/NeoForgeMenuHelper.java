package arrivedbog593.customgear.neoforge.platform;

import arrivedbog593.customgear.platform.services.IMenuHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class NeoForgeMenuHelper implements IMenuHelper {

    @Override
    public <M extends AbstractContainerMenu, D> MenuType<M> createMenuType(
            MenuFactory<M, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        return IMenuTypeExtension.create((id, inventory, buf) ->
                factory.create(id, inventory, codec.decode(buf)));
    }

    @Override
    public <D> void openMenu(ServerPlayer player, MenuProvider provider, D data,
                             StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        player.openMenu(provider, buf -> codec.encode(buf, data));
    }
}
