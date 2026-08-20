package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.registry.ContainerRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * The item form of any placeable container.
 * <p>
 * Everything that depends on the container CARRYING an inventory lives here —
 * the contents grid, the hint line and the nesting refusal — because a barrel
 * with keeps_contents does exactly what a shulker does, and giving the chest and
 * the shulker their own copies left the barrel with none.
 * <p>
 * The two subclasses add only a renderer.
 */
public class CustomContainerBlockItem extends BlockItem {

    public CustomContainerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    /** The definition behind this item, or null if it vanished on reload. */
    protected ContainerContentData definition() {
        return ContainerRegistry.CONTAINER_MAP.get(
                BuiltInRegistries.BLOCK.getKey(getBlock()));
    }

    /**
     * Only containers that carry their inventory refuse to nest.
     * <p>
     * A barrel that spills when broken holds nothing in the item, so there is no
     * NBT to nest and no reason to keep it out of a shulker — being stricter
     * than vanilla for no gain.
     */
    @Override
    public boolean canFitInsideContainerItems() {
        ContainerContentData d = definition();
        return d == null || d.container == null || !d.container.keepsContents();
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return ContainerItemTooltip.image(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ContainerContentData d = definition();
        ContainerItemTooltip.appendHint(d, stack, tooltip);
        ContainerItemTooltip.appendContentsHeader(d, stack, tooltip);
    }
}