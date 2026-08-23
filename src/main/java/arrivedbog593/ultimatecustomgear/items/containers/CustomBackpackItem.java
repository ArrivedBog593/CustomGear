package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.menu.CustomContainerMenu;
import arrivedbog593.ultimatecustomgear.network.ContainerOpenData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * A container that is carried instead of placed.
 * <p>
 * The only subtype with no block at all, which is why it is an Item rather than
 * a BlockItem: there is nothing to register in the block registry and nothing to
 * place. Its inventory lives in the ItemStack's component, so keeps_contents is
 * not a choice here — the parser refuses to turn it off.
 */
@SuppressWarnings("deprecation")
public class CustomBackpackItem extends Item {

    private final ContainerContentData data;

    public CustomBackpackItem(ContainerContentData data, Properties properties) {
        super(properties);
        this.data = data;
    }

    public ContainerContentData getData() { return data; }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level,
                                         @NotNull Player player,
                                         @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BackpackAnchor anchor = hand == InteractionHand.OFF_HAND
                ? new BackpackAnchor.Offhand()
                : new BackpackAnchor.InventorySlot(player.getInventory().getSelectedSlot());

        open(player, anchor, stack);
        return InteractionResult.CONSUME;
    }

    /**
     * Opens the menu for a backpack wherever it is. Shared by the right-click
     * path and, later, by the keybind that opens one from any slot.
     */
    public void open(Player player, BackpackAnchor anchor, ItemStack stack) {
        if (!(player instanceof ServerPlayer)) return;
        if (data.container == null) return;

        int size = data.container.slots;
        int columns = data.container.columnsFor(size);
        BackpackContainer inventory = new BackpackContainer(player, anchor, stack, size);

        // The offhand is not one of the menu's slots, so there is nothing to
        // freeze there — stillValid is the only guard for that case.
        int locked = anchor instanceof BackpackAnchor.InventorySlot(int index) ? index : -1;

        MenuProvider provider = new SimpleMenuProvider(
                (id, playerInv, p) -> new CustomContainerMenu(
                        id, playerInv, inventory, size, columns, locked),
                stack.getHoverName());

        player.openMenu(provider, buf -> new ContainerOpenData(
                size,
                columns,
                (byte) 0,          // a backpack has nowhere durable to keep a sort mode
                false,
                true,              // it always refuses to nest — see ContainerNesting
                locked
        ).write(buf));
    }

    /**
     * Never inside another container item, whatever it belongs to. The one
     * exception anyone makes is backpack-inside-backpack behind an opt-in
     * upgrade, and that does not exist here.
     */
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return ContainerItemTooltip.image(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull net.minecraft.world.item.component.TooltipDisplay display,
                                @NotNull java.util.function.Consumer<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        ContainerItemTooltip.appendHint(data, stack, tooltip);
        ContainerItemTooltip.appendContentsHeader(data, stack, tooltip);
    }
}