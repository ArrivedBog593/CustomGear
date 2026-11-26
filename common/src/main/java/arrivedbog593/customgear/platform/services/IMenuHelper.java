package arrivedbog593.customgear.platform.services;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Menus that carry extra data to the client when they open. Vanilla's
 * MenuType carries none; NeoForge adds it with IMenuTypeExtension, Fabric with
 * ExtendedScreenHandlerType. Both are covered by one codec for the data.
 */
public interface IMenuHelper {

    <M extends AbstractContainerMenu, D> MenuType<M> createMenuType(
            MenuFactory<M, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> codec);

    /** Opens the menu for this player and sends it {@code data}, written with the same codec. */
    <D> void openMenu(ServerPlayer player, MenuProvider provider, D data,
                      StreamCodec<? super RegistryFriendlyByteBuf, D> codec);

    @FunctionalInterface
    interface MenuFactory<M extends AbstractContainerMenu, D> {
        M create(int containerId, Inventory playerInventory, D data);
    }
}
