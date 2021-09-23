package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleParticipantItemRenderer;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleRendererRegistry;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class BattleInventoryPreviewWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final double infoFraction;
    private final BattleStateView battleStateView;
    private final Supplier<@Nullable ItemStackInfo> stackSupplier;
    private long lastMillis;
    private DoubleQuaternion rotation;
    private boolean rotating;

    public BattleInventoryPreviewWidget(final WidgetPosition position, final DoubleSupplier width, final DoubleSupplier height, final double infoFraction, final BattleStateView battleStateView, final Supplier<@Nullable ItemStackInfo> stackSupplier) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.infoFraction = infoFraction;
        this.battleStateView = battleStateView;
        this.stackSupplier = stackSupplier;
        rotation = new DoubleQuaternion();
        rotating = true;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final double x = position.getX();
        final double y = position.getY();
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        final Rect2d rect = new Rect2d(x, y, x + width, y + height);
        if (rect.isIn(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            rotating = !rotating;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double x = position.getX();
        final double y = position.getY();
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        final Rect2d rect = new Rect2d(x, y, x + width, y + height);
        if (rect.isIn(mouseX, mouseY)) {
            rotation = new DoubleQuaternion(new Vec3d(1, 0, 0), deltaY * 360, true).multiply(rotation);
            rotation = new DoubleQuaternion(new Vec3d(0, -1, 0), deltaX * 360, true).multiply(rotation);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final double x = position.getX();
        final double y = position.getY();
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        render(vertexConsumers -> {
            matrices.push();
            final double scale = Math.min(width, height * (1 - infoFraction));
            matrices.translate(x + width / 2, y + height * (1 - infoFraction) / 2, 0);
            matrices.scale((float) scale * 0.5f, (float) scale * 0.5f, 1);
            matrices.multiply(rotation.toFloatQuat());
            final ItemStackInfo info = stackSupplier.get();
            renderItem(matrices, delta, info, vertexConsumers);
            matrices.pop();

            if (lastMillis != 0) {
                final long current = Util.getMeasuringTimeMs();
                final double seconds = (current - lastMillis) / 1000.0;
                final double speed = 0.05;
                if (rotating) {
                    rotation = rotation.multiply(new DoubleQuaternion(new Vec3d(0, 1, 0), (speed * seconds) * 360, true));
                }
                lastMillis = current;
            } else {
                lastMillis = Util.getMeasuringTimeMs();
            }

            matrices.push();
            renderInfo(matrices, x, y, width, height, info, vertexConsumers);
            matrices.pop();
        });
    }

    private void renderInfo(final MatrixStack matrices, final double x, double y, final double width, final double height, @Nullable final ItemStackInfo stackInfo, final VertexConsumerProvider vertexConsumers) {
        if (stackInfo != null) {
            y += height - (height * infoFraction);
            matrices.translate(0, 0, 1);
            renderFitText(matrices, stackInfo.stack.getItem().getName(), x + getHorizontalPixel(), y + getVerticalPixel(), width - 2 * getHorizontalPixel(), height * infoFraction * 0.25 - 2 * getVerticalPixel(), true, -1, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers);
            matrices.translate(0, 0, -1);
            renderTooltipBackground(x, y, width, height * infoFraction * 0.25, matrices, vertexConsumers);
            renderTooltipBackground(x, y + height * infoFraction * 0.25, width, height * infoFraction * 0.75, matrices, vertexConsumers);
            matrices.translate(0, 0, 1);
            renderTextLines(matrices, stackInfo.stack.getItem().getTooltip(), x + getHorizontalPixel(), y + height * infoFraction * 0.25 + getVerticalPixel(), width - 2 * getHorizontalPixel(), height * infoFraction * 0.75 - 2 * getVerticalPixel(), false, false, -1, vertexConsumers);
            matrices.translate(0, 0, -1);
        }
    }

    private void renderItem(final MatrixStack matrices, final float delta, @Nullable final ItemStackInfo stackInfo, final VertexConsumerProvider vertexConsumers) {
        if (stackInfo != null) {
            final BattleParticipantStateView participantState = battleStateView.getParticipant(stackInfo.location.map(BattleParticipantInventoryHandle::handle, Pair::getFirst));
            if (participantState == null) {
                return;
            }
            final BattleParticipantItemStack stack = stackInfo.stack;
            final BattleParticipantItemRenderer renderer = BattleRendererRegistry.getItemRenderer(stack.getItem().getType());
            renderer.render(stack, participantState, matrices, vertexConsumers, delta);
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }
}
