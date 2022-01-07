package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.client.TBCExCoreClient;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleParticipantItemRenderer;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleRendererRegistry;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.text.OrderedTextUtil;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedWidget;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class BattleInventoryPreviewWidget extends AbstractWidget implements PositionedWidget {
    private static final TextDrawer TITLE_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, true);
    private static final TextDrawer DESCRIPTION_DRAWER = TextDrawers.lineBreaking(TextDrawers.HorizontalJustification.LEFT, -1, 0, false);
    private final DoubleSupplier x;
    private final DoubleSupplier y;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final double infoFraction;
    private final BattleStateView battleStateView;
    private final Supplier<@Nullable ItemStackInfo> stackSupplier;
    private DoubleQuaternion rotation;
    private boolean rotating;
    private long lastMillis;

    public BattleInventoryPreviewWidget(final DoubleSupplier x, final DoubleSupplier y, final DoubleSupplier width, final DoubleSupplier height, final double infoFraction, final BattleStateView battleStateView, final Supplier<@Nullable ItemStackInfo> stackSupplier) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.infoFraction = infoFraction;
        this.battleStateView = battleStateView;
        this.stackSupplier = stackSupplier;
        rotation = new DoubleQuaternion();
        rotating = true;
    }

    private boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final double x = 0;
        final double y = 0;
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        final Rect2d rect = new Rect2d(x, y, x + width, y + height);
        if (rect.isIn(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            rotating = !rotating;
            return true;
        }
        return false;
    }

    private boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double x = 0;
        final double y = 0;
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
    public void render(final GuiContext context) {
        processEvents(context, event -> {
            if (event instanceof GuiInputContext.MouseClick click) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(click.mouseX, click.mouseY));
                return mouseClicked(mouse.x, mouse.y, click.button);
            } else if (event instanceof GuiInputContext.MouseDrag drag) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(drag.mouseX, drag.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(drag.mouseX + drag.deltaX, drag.mouseY + drag.deltaY)).subtract(mouse);
                return mouseDragged(mouse.x, mouse.y, drag.button, delta.x, delta.y);
            }
            return false;
        });
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        final ItemStackInfo info = stackSupplier.get();
        renderItem(context, context.getTickDelta(), info);

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

        renderInfo(context, width, height, info);
    }

    private void renderItem(final GuiContext context, final float delta, @Nullable final ItemStackInfo stackInfo) {
        if (stackInfo != null) {
            final BattleParticipantStateView participantState = battleStateView.getParticipant(stackInfo.location.map(BattleParticipantInventoryHandle::handle, Pair::getFirst));
            if (participantState == null) {
                return;
            }
            final BattleParticipantItemStack stack = stackInfo.stack;
            final BattleParticipantItemRenderer renderer = BattleRendererRegistry.getItemRenderer(stack.getItem().getType());
            TBCExCoreClient.addRenderPrimitive(worldRenderContext -> {
                final MatrixStack matrices = worldRenderContext.matrixStack();
                matrices.push();
                final VertexConsumerProvider vertexConsumers = worldRenderContext.consumers();
                final double scale = Math.min(width.getAsDouble(), height.getAsDouble() * (1 - infoFraction));
                matrices.translate(width.getAsDouble() / 2, height.getAsDouble() * (1 - infoFraction) / 2, 0);
                matrices.scale((float) scale * 0.5f, (float) scale * 0.5f, 1);
                matrices.multiply(rotation.toFloatQuat());
                renderer.render(stack, participantState, worldRenderContext.matrixStack(), vertexConsumers, delta);
                matrices.pop();
            });
        }
    }

    private void renderInfo(final GuiContext context, final double width, final double height, @Nullable final ItemStackInfo stackInfo) {
        if (stackInfo != null) {
            final double y = height - (height * infoFraction);
            context.pushTranslate(0, 0, 1);
            TITLE_DRAWER.draw(width, height * infoFraction * 0.25, stackInfo.stack.getItem().getName().asOrderedText(), context);
            context.popGuiTransform();
            context.renderTooltipBackground(0.0, y, width, height * infoFraction * 0.25);
            context.renderTooltipBackground(0.0, y + height * infoFraction * 0.25, width, height * infoFraction * 0.75);
            context.pushTranslate(0, 0, 1);
            DESCRIPTION_DRAWER.draw(width, height * infoFraction * 0.75, stackInfo.stack.getItem().getTooltip().stream().reduce(null, (text, text2) -> {
                if (text == null) {
                    return text2.asOrderedText();
                }
                return OrderedText.concat(text, OrderedTextUtil.of('\n', Style.EMPTY), text2.asOrderedText());
            }, (text, text2) -> OrderedText.concat(text, OrderedTextUtil.of('\n', Style.EMPTY), text2)), context);
            context.popGuiTransform();
        }
    }

    @Override
    public double getX() {
        return x.getAsDouble();
    }

    @Override
    public double getY() {
        return y.getAsDouble();
    }
}
