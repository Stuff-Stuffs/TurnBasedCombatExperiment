package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.util.RenderUtil;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BattleInventoryWidget extends AbstractWidget {
    private static final int COLUMN_COUNT = 4;
    private final WidgetPosition position;
    private final BattleParticipantHandle handle;
    private final World world;
    private final List<ItemStackInfo> stacks;
    private final double borderThickness;
    private final double entryHeight;
    private final double verticalSpacing;
    private final double width;
    private final double height;
    private double listHeight = 0;
    private double pos = 0;

    public BattleInventoryWidget(final WidgetPosition position, final BattleParticipantHandle handle, final World world, final double borderThickness, final double entryHeight, final double verticalSpacing, final double width, final double height) {
        this.position = position;
        this.handle = handle;
        this.world = world;
        this.borderThickness = borderThickness;
        this.entryHeight = entryHeight;
        this.verticalSpacing = verticalSpacing;
        this.width = width;
        this.height = height;
        stacks = new ArrayList<>();
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - deltaY, 0), listHeight - (height - 2 * borderThickness));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - amount, 0), listHeight - (height - 2 * borderThickness));
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
        buffer.vertex(model, (float) (offsetX + width), (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) (offsetX + width), (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        final double scrollbarThickness = borderThickness / 3.0;
        final double scrollbarHeight = scrollbarThickness * 8;
        final double scrollAreaHeight = height - 2 * borderThickness - scrollbarHeight;
        final double progress = pos / (listHeight - (height - 2 * borderThickness));
        buffer.vertex(model, (float) (scrollbarThickness * 2), (float) (borderThickness + progress * scrollAreaHeight), 0).color(127, 127, 127, 192).next();
        buffer.vertex(model, (float) scrollbarThickness, (float) (borderThickness + progress * scrollAreaHeight), 0).color(127, 127, 127, 192).next();
        buffer.vertex(model, (float) scrollbarThickness, (float) (borderThickness + progress * scrollAreaHeight + scrollbarHeight), 0).color(127, 127, 127, 192).next();
        buffer.vertex(model, (float) (scrollbarThickness * 2), (float) (borderThickness + progress * scrollAreaHeight + scrollbarHeight), 0).color(127, 127, 127, 192).next();
        buffer.end();
        BufferRenderer.draw(buffer);
        ScissorStack.push(matrices, borderThickness + offsetX, borderThickness + offsetY, offsetX + width - borderThickness, (offsetY + height - borderThickness) * 1.1);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        matrices.push();
        matrices.translate(0, -pos, 0);
        final int hoverIndex = findHoverIndex(mouseX, mouseY + pos);
        for (int i = 0; i < stacks.size(); i++) {
            renderInfo(stacks.get(i), buffer, matrices, i, hoverIndex);
        }
        buffer.end();
        BufferRenderer.draw(buffer);
        for (int i = 0; i < stacks.size(); i++) {
            renderDecorations(stacks.get(i), matrices, i, hoverIndex);
        }
        matrices.pop();
        ScissorStack.pop();
    }

    private void renderDecorations(final ItemStackInfo info, final MatrixStack matrices, final int index, final int hoverIndex) {
        final float offsetX = (float) position.getX();
        final float offsetY = (float) position.getY();
        final double maxWidth = ((width - 2 * borderThickness) / (double) COLUMN_COUNT);
        final double y = offsetY + borderThickness + index * entryHeight + (index == 0 ? 0 : index - 1) * verticalSpacing;
        renderFitText(matrices, info.stack.getItem().getName(), offsetX + borderThickness, y, maxWidth, entryHeight, index == hoverIndex, -1);
        renderFitText(matrices, info.stack.getItem().getCategory().getName(), offsetX + borderThickness + maxWidth, y, maxWidth, entryHeight, index == hoverIndex, -1);
    }

    private void renderInfo(final ItemStackInfo info, final BufferBuilder buffer, final MatrixStack matrices, final int index, final int hoverIndex) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final Matrix4f model = matrices.peek().getModel();
        final float startX = (float) (offsetX + borderThickness);
        final float endX = (float) (offsetX + width - borderThickness);
        final float startY = (float) (offsetY + borderThickness + index * entryHeight + (index > 0 ? index - 1 : 0) * verticalSpacing);
        final float endY = (float) (offsetY + borderThickness + index * entryHeight + (index > 0 ? index - 1 : 0) * verticalSpacing + entryHeight);
        int backgroundColour = getBackgroundColour(index);
        if (hoverIndex == index) {
            backgroundColour |= 0xFF000000;
        }
        RenderUtil.colour(buffer.vertex(model, endX, startY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, startX, startY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, startX, endY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, endX, endY, 0), backgroundColour).next();
    }

    private static int getBackgroundColour(final int index) {
        return (index & 1) == 0 ? 0x77111111 : 0x77222222;
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        for (int i = 0; i < stacks.size(); i++) {
            final double startX = offsetX + borderThickness;
            final double endX = offsetX + width - borderThickness;
            final double startY = offsetY + borderThickness + i * entryHeight + (i > 0 ? i - 1 : 0) * verticalSpacing;
            final double endY = startY + entryHeight;
            final Rect2d rect = new Rect2d(startX, startY, endX, endY);
            if (rect.isIn(mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    public boolean tick() {
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return true;
        }
        stacks.clear();
        final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
        if (participant == null) {
            return true;
        }
        final Iterator<BattleParticipantInventoryHandle> iterator = participant.getInventoryIterator();
        while (iterator.hasNext()) {
            final BattleParticipantInventoryHandle next = iterator.next();
            stacks.add(new ItemStackInfo(next, participant.getItemStack(next), null));
        }
        for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            final BattleParticipantItemStack stack = participant.getEquipmentStack(slot);
            if (stack != null) {
                stacks.add(new ItemStackInfo(null, stack, slot));
            }
        }
        listHeight = stacks.size() * entryHeight + (stacks.size() - 1) * verticalSpacing;
        return false;
    }

    private static class ItemStackInfo {
        public final @Nullable BattleParticipantInventoryHandle handle;
        public final BattleParticipantItemStack stack;
        public final @Nullable BattleEquipmentSlot slot;

        public ItemStackInfo(@Nullable final BattleParticipantInventoryHandle handle, final BattleParticipantItemStack stack, @Nullable final BattleEquipmentSlot slot) {
            this.handle = handle;
            this.stack = stack;
            this.slot = slot;
        }
    }
}
