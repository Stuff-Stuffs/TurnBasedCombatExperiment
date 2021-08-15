package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.client.util.ClientUtil;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.util.RenderUtil;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;

public class BattleInventorySorterWidget extends AbstractWidget {
    private static final Comparator<ItemStackInfo> DEFAULT_COMPARATOR = (first, second) -> {
        final Optional<Pair<BattleParticipantHandle, BattleEquipmentSlot>> firstRight = first.location.right();
        final Optional<Pair<BattleParticipantHandle, BattleEquipmentSlot>> secondRight = second.location.right();
        if (firstRight.isPresent() && secondRight.isPresent()) {
            return Integer.compare(BattleEquipmentSlot.REGISTRY.getRawId(firstRight.get().getSecond()), BattleEquipmentSlot.REGISTRY.getRawId(secondRight.get().getSecond()));
        }
        final Optional<BattleParticipantInventoryHandle> firstLeft = first.location.left();
        final Optional<BattleParticipantInventoryHandle> secondLeft = second.location.left();
        if (firstLeft.isPresent()) {
            if (secondLeft.isPresent()) {
                return Integer.compare(firstLeft.get().id(), secondLeft.get().id());
            } else {
                return 1;
            }
        }
        return -1;
    };
    private static final Comparator<ItemStackInfo> ALPHABETICAL_COMPARATOR = Comparator.comparing(o -> o.stack.getItem().getName().getString());
    private static final Comparator<ItemStackInfo> COUNT_COMPARATOR = Comparator.comparingInt(o -> o.stack.getCount());
    private static final Comparator<ItemStackInfo> CATEGORY_COMPARATOR = Comparator.comparing(o -> o.stack.getItem().getCategory().getName().getString());
    private static final Comparator<ItemStackInfo> RARITY_COMPARATOR = Comparator.<ItemStackInfo>comparingInt(o -> o.stack.getItem().getRarity().getRarity().ordinal()).thenComparingDouble(o -> o.stack.getItem().getRarity().getProgress());
    public static final List<Sort> DEFAULTS = Util.make(new ArrayList<>(), list -> {
        list.add(new Sort() {
            @Override
            public Comparator<ItemStackInfo> getComparator() {
                return ALPHABETICAL_COMPARATOR;
            }

            @Override
            public Text getName() {
                return new LiteralText("ALPHABETICAL");
            }
        });
        list.add(new Sort() {
            @Override
            public Comparator<ItemStackInfo> getComparator() {
                return COUNT_COMPARATOR;
            }

            @Override
            public Text getName() {
                return new LiteralText("COUNT");
            }
        });
        list.add(new Sort() {
            @Override
            public Comparator<ItemStackInfo> getComparator() {
                return CATEGORY_COMPARATOR;
            }

            @Override
            public Text getName() {
                return new LiteralText("CATEGORY");
            }
        });
        list.add(new Sort() {
            @Override
            public Comparator<ItemStackInfo> getComparator() {
                return RARITY_COMPARATOR;
            }

            @Override
            public Text getName() {
                return new LiteralText("RARITY");
            }
        });
    });
    private final WidgetPosition position;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final double borderThickness;
    private final double entryWidth;
    private final double horizontalSpacing;
    private final List<Sort> sorts;
    private final IntConsumer onSelect;
    private double pos = 0;
    private int selectedIndex = 0;
    private boolean reversedSort = false;
    private int prevSelectedIndex = -1;
    private boolean prevReversedSort = false;


    public BattleInventorySorterWidget(final WidgetPosition position, final DoubleSupplier width, final DoubleSupplier height, final double borderThickness, final double entryWidth, final double horizontalSpacing, final List<Sort> sorts, final IntConsumer onSelect) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;
        this.entryWidth = entryWidth;
        this.horizontalSpacing = horizontalSpacing;
        this.sorts = sorts;
        this.onSelect = onSelect;
    }

    public void sort(final List<ItemStackInfo> infos) {
        if (0 <= selectedIndex && selectedIndex < sorts.size()) {
            Comparator<ItemStackInfo> first = sorts.get(selectedIndex).getComparator();
            if(reversedSort) {
                first = first.reversed();
            }
            Comparator<ItemStackInfo> second;
            if (0 <= prevSelectedIndex && prevSelectedIndex < sorts.size()) {
                second = sorts.get(prevSelectedIndex).getComparator();
            } else {
                second = DEFAULT_COMPARATOR;
            }
            if(prevReversedSort) {
                second = second.reversed();
            }
            infos.sort(first.thenComparing(second));
        } else {
            infos.sort(DEFAULT_COMPARATOR);
        }
    }

    public void setSelectedIndex(final int selectedIndex) {
        if (0 <= selectedIndex && selectedIndex < sorts.size()) {
            if (selectedIndex != this.selectedIndex) {
                pos = (position.getX() + selectedIndex * entryWidth + selectedIndex * horizontalSpacing + entryWidth / 2) - (width.getAsDouble() - 2 * borderThickness) / 2;
                prevSelectedIndex = this.selectedIndex;
                prevReversedSort = reversedSort;
                this.selectedIndex = selectedIndex;
            } else {
                reversedSort = !reversedSort;
            }
            onSelect.accept(selectedIndex);
        } else {
            this.selectedIndex = -1;
            onSelect.accept(-1);
        }
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height.getAsDouble()).isIn(mouseX, mouseY)) {
            final int index = findHoverIndex(mouseX + pos, mouseY);
            setSelectedIndex(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height.getAsDouble()).isIn(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double width = height.getAsDouble();
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height.getAsDouble()).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos + deltaX, -(width - 2 * borderThickness) / 2), getListWidth() - (width - 2 * borderThickness) / 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double width = this.width.getAsDouble();
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height.getAsDouble()).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos + amount, -(width - 2 * borderThickness) / 2), getListWidth() - (width - 2 * borderThickness) / 2);
            return true;
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final Matrix4f model = matrices.peek().getModel();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        buffer.vertex(model, (float) (offsetX + width), (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) (offsetX + width), (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        buffer.end();
        BufferRenderer.draw(buffer);

        matrices.push();
        matrices.translate(-pos, 0, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        final int hoverIndex = findHoverIndex(mouseX + pos, mouseY);
        for (int i = 0; i < sorts.size(); i++) {
            renderInfo(sorts.get(i), buffer, matrices, i, hoverIndex);
        }
        buffer.end();
        BufferRenderer.draw(buffer);

        for (int i = 0; i < sorts.size(); i++) {
            renderDecorations(sorts.get(i), matrices, i, hoverIndex);
        }

        matrices.pop();
    }

    private void renderDecorations(final Sort sort, final MatrixStack matrices, final int index, final int hoverIndex) {
        final float offsetX = (float) position.getX();
        final float offsetY = (float) position.getY();
        final double startX = offsetX + borderThickness + index * entryWidth + index * horizontalSpacing;
        final float endX = (float) (offsetX + borderThickness + index * entryWidth + index * horizontalSpacing + entryWidth);
        final double y = offsetY + borderThickness;
        final double centerX = (startX + endX) / 2.0;
        double dist = Math.abs(centerX - (pos + (width.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final double offset = height.getAsDouble() / 4;
        final double scale = Math.max(offset - dist, 0) / offset;
        final boolean shadow = index == hoverIndex || selectedIndex == index;
        renderFitText(matrices, sort.getName(), startX, y, (endX-startX) * scale, (height.getAsDouble() - 2 * borderThickness)*scale, shadow, ClientUtil.tweakComponent(-1, 3, scale));
    }

    private void renderInfo(final Sort category, final BufferBuilder buffer, final MatrixStack matrices, final int index, final int hoverIndex) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final Matrix4f model = matrices.peek().getModel();
        final float startX = (float) (offsetX + borderThickness + index * entryWidth + index * horizontalSpacing);
        final float endX = (float) (offsetX + borderThickness + index * entryWidth + index * horizontalSpacing + entryWidth);
        final float xLen = (endX - startX);
        final float startY = (float) (offsetY + borderThickness);
        final float endY = (float) (offsetY + height.getAsDouble() - borderThickness);
        final float centerX = (startX + endX) / 2f;
        final float yLen = (endY - startY);
        float dist = Math.abs(centerX - (float) (pos + (width.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final float offset = ((float) width.getAsDouble()) / 4f;
        final float scale = Math.max(offset - dist, 0) / offset;
        int backgroundColour = getBackgroundColour(index);
        if (hoverIndex == index || selectedIndex == index) {
            backgroundColour |= 0xFF000000;
        }
        backgroundColour = ClientUtil.tweakComponent(backgroundColour, 3, scale);
        RenderUtil.colour(buffer.vertex(model, startX + xLen * scale, startY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, startX, startY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, startX, startY + yLen * scale, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, startX + xLen * scale, startY + yLen * scale, 0), backgroundColour).next();
    }

    private static int getBackgroundColour(final int index) {
        return (index & 1) == 0 ? 0x77111111 : 0x77222222;
    }


    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            setSelectedIndex(selectedIndex - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            setSelectedIndex(selectedIndex + 1);
            return true;
        }
        return false;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double height = this.height.getAsDouble();
        for (int index = 0; index < sorts.size(); index++) {
            final double startX = offsetX + borderThickness + index * entryWidth + index * horizontalSpacing;
            final double endX = startX + entryWidth;
            final double startY = offsetY + borderThickness;
            final double endY = offsetY + height - borderThickness;
            if (new Rect2d(startX, startY, endX, endY).isIn(mouseX, mouseY)) {
                return index;
            }
        }
        return -1;
    }

    private double getListWidth() {
        final int size = sorts.size();
        return size * entryWidth + size * horizontalSpacing;
    }

    public interface Sort {
        Comparator<ItemStackInfo> getComparator();

        Text getName();
    }
}
