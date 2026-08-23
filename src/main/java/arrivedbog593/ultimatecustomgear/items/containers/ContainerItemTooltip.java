package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.client.ContainerTooltip;
import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.data.components.ContainerContents;
import arrivedbog593.ultimatecustomgear.registry.ComponentRegistry;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.Optional;

/**
 * The contents tooltip, shared by the two item hierarchies.
 * <p>
 * A placeable container is a BlockItem and a backpack is a plain Item, so there
 * is no common superclass to hang this on — and duplicating it left the backpack
 * with no tooltip at all until someone noticed.
 */
public final class ContainerItemTooltip {

    private ContainerItemTooltip() {}

    /**
     * The hint line.
     * <p>
     * Shown when the container CAN hold something — so an empty backpack still
     * teaches the player the key exists — or when this particular stack already
     * does, which covers a creative copy of a container whose contents would
     * normally spill on break. A grid nobody was told about is worse than a hint
     * that turns out to say "empty".
     */
    public static void appendHint(ContainerContentData data, ItemStack stack,
                                  Consumer<Component> tooltip) {
        if (TooltipHelper.detailsShown()) return;

        boolean canHold = data != null && data.container != null
                && data.container.keepsContents();
        boolean hasContents = stack.has(ComponentRegistry.CONTAINER_CONTENTS.get());
        if (!canHold && !hasContents) return;

        TooltipHelper.addContentsHint(tooltip);
    }

    /**
     * The "nothing here" line. When there IS content the header is drawn by the
     * tooltip component instead — vanilla inserts the image right after the
     * item's name, so a text line can never sit above it.
     */
    public static void appendContentsHeader(ContainerContentData data, ItemStack stack,
                                            Consumer<Component> tooltip) {
        if (!TooltipHelper.detailsShown()) return;

        boolean canHold = data != null && data.container != null
                && data.container.keepsContents();
        ContainerContents contents = stack.get(ComponentRegistry.CONTAINER_CONTENTS.get());
        boolean hasContents = contents != null && !contents.isEmpty();

        if (hasContents || !canHold) return;
        tooltip.accept(Component.translatable("tooltip.ultimatecustomgear.container.empty")
                .withStyle(ChatFormatting.YELLOW));
    }

    /** The grid itself, or nothing when there is nothing to draw. */
    public static Optional<TooltipComponent> image(ItemStack stack) {
        ContainerContents contents = stack.get(ComponentRegistry.CONTAINER_CONTENTS.get());
        if (contents == null || contents.isEmpty() || !TooltipHelper.detailsShown()) {
            return Optional.empty();
        }
        return Optional.of(ContainerTooltip.of(contents.stacks()));
    }
}