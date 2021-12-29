package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.widget.*;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class BattleActionRenderTargetsWidget extends AbstractWidget {
    private static final WidgetPosition ROOT = new BasicWidgetPosition(0, 0, 0);
    private final ParticipantActionInstance actionInstance;
    private final HidingPanel widget;
    private @Nullable TargetInstance hovered;

    public BattleActionRenderTargetsWidget(final ParticipantActionInstance actionInstance) {
        this.actionInstance = actionInstance;
        hovered = null;
        widget = new HidingPanel();
        final ParentWidget parentWidget = new BasicPanelWidget(new SuppliedWidgetPosition(() -> -(getScreenWidth() - 1) / 2, () -> -(getScreenHeight() - 1) / 2, () -> 1), 0.15, 0.125);
        parentWidget.addWidget(new TextWidget(new SuppliedWidgetPosition(() -> -(getScreenWidth() - 1) / 2 + 0.025, () -> -(getScreenHeight() - 1) / 2 + 0.025, () -> 1), () -> new LiteralText("Confirm (Press enter)"), () -> true, Colour.WHITE, () -> 255, 0.1, 0.075));
        widget.addWidget(parentWidget);
        widget.resize(getScreenWidth(), getScreenHeight(), getPixelWidth(), getPixelHeight());
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        widget.resize(width, height, pixelWidth, pixelHeight);
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return ROOT;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (widget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (hovered != null) {
                actionInstance.accept(hovered);
                hovered = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return widget.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return widget.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final Battle battle = ((BattleWorldSupplier) client.world).tbcex_getBattleWorld().getBattle(((BattleAwareEntity) client.player).tbcex_getCurrentBattle());
        if (battle != null) {
            final TargetType<?> nextType = actionInstance.getNextType();
            widget.setHidden(!actionInstance.canActivate());
            widget.render(matrices, mouseX, mouseY, delta);
            if (nextType != null) {
                updateTargeted(nextType, battle, delta);
            }
            actionInstance.render(hovered, delta);
            if (hovered != null) {
                renderInfo(matrices, mouseX, mouseY, delta);
            }
        }
    }

    private void renderInfo(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        if (hovered != null) {
            final List<TooltipComponent> description = actionInstance.getTargetDescription();
            if (description != null) {
                renderTooltip(matrices, description, mouseX, mouseY);
            }
        }
    }

    private void updateTargeted(final TargetType<?> nextType, final Battle battle, final float delta) {
        final Vec3d startVec = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(delta);
        final Vec3d lookVec = ClientUtil.getMouseVector();
        hovered = nextType.find(startVec, lookVec, actionInstance.getUser(), battle);
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (widget.keyPress(keyCode, scanCode, modifiers)) {
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ENTER) && actionInstance.canActivate()) {
            activate();
            return true;
        }
        return false;
    }

    private void activate() {
        widget.setHidden(true);
        actionInstance.activate();
        MinecraftClient.getInstance().setScreen(null);
    }
}
