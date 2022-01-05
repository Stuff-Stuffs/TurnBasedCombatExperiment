package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import com.google.common.base.Preconditions;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;

import java.util.function.IntSupplier;

public class GriddedPanelWidget extends AbstractPanelWidget<PositionedWidget> {
    private final Widget[] cells;
    private final int horizontalCells;
    private final int verticalCells;
    private final double cellWidth;
    private final double cellHeight;
    private final boolean scissor;

    public GriddedPanelWidget(final int horizontalCells, final int verticalCells, final double cellWidth, final double cellHeight, final boolean scissor, final IntSupplier colour) {
        super(cellWidth * horizontalCells, cellHeight * verticalCells, colour);
        this.horizontalCells = horizontalCells;
        this.verticalCells = verticalCells;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.scissor = scissor;
        cells = new Widget[horizontalCells * verticalCells];
    }

    public void setSlot(final Widget widget, final int x, final int y) {
        Preconditions.checkArgument(0 <= x && x < horizontalCells);
        Preconditions.checkArgument(0 <= y && y < verticalCells);
        cells[getIndex(x, y)] = widget;
        widget.resize(getWidth(), getHeight(), getPixelWidth(), getPixelHeight());
    }

    private int getIndex(final int x, final int y) {
        return x + y * horizontalCells;
    }

    @Override
    protected void renderChildren(final GuiContext context) {
        for (int i = 0; i < verticalCells; i++) {
            for (int j = 0; j < horizontalCells; j++) {
                context.pushTranslate(j * cellWidth, i * cellHeight, 1);
                if (scissor) {
                    context.pushScissor(0, 0, (float) cellWidth, (float) cellHeight);
                }
                cells[getIndex(j, i)].render(context);
                if (scissor) {
                    context.popGuiTransform();
                }
                context.popGuiTransform();
            }
        }
    }

    @Override
    protected void resizeChildren(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        for (final Widget cell : cells) {
            if (cell != null) {
                cell.resize(width, height, pixelWidth, pixelHeight);
            }
        }
    }
}
