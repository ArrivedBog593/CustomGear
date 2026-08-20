package arrivedbog593.ultimatecustomgear.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/** Draws the container grid — nine per row, count in the corner of each cell. */
@OnlyIn(Dist.CLIENT)
public record ContainerTooltipRenderer(ContainerTooltip data) implements ClientTooltipComponent {

    private static final int PER_ROW = 9;
    private static final int CELL = 18;
    private static final int HEADER = 10;


    private int rows() {
        return Math.max(1, (data.entries().size() + PER_ROW - 1) / PER_ROW);
    }

    @Override
    public int getHeight() {
        return HEADER + rows() * CELL + (data.hidden() > 0 ? 12 : 0) + 4;
    }


    @Override
    public int getWidth(@NotNull Font font) {
        return Math.clamp(data.entries().size(), 1, PER_ROW) * CELL;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics g) {
        // Drawn here rather than as a tooltip line: vanilla inserts the image
        // immediately after the item's name, so any text line added from
        // appendHoverText lands BELOW it no matter what index it is given.
        g.drawString(font, Component.translatable("tooltip.ultimatecustomgear.container.contents")
                .withStyle(ChatFormatting.GOLD), x, y, -1, false);

        for (int i = 0; i < data.entries().size(); i++) {
            int cx = x + (i % PER_ROW) * CELL;
            int cy = y + HEADER + (i / PER_ROW) * CELL;
            ContainerTooltip.Entry entry = data.entries().get(i);
            // The count is drawn separately because the stack is kept at one:
            // a real count above 99 would render as a truncated number.
            g.renderItem(entry.stack(), cx, cy);
            g.renderItemDecorations(font, entry.stack(), cx, cy, String.valueOf(entry.total()));
        }

        if (data.hidden() > 0) {
            g.drawString(font,
                    Component.translatable("tooltip.ultimatecustomgear.container.more", data.hidden()),
                    x, y + HEADER + rows() * CELL + 2, 0xAAAAAA, false);
        }
    }
}