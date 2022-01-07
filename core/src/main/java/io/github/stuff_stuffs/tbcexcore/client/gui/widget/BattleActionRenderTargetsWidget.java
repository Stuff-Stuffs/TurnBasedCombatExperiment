package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.*;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.GriddedPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.InvisiblePanelWidget;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BattleActionRenderTargetsWidget extends AbstractWidget {
    private final ParticipantActionInstance actionInstance;
    private final HidingWidget widget;
    private final MutableBoolean hidden = new MutableBoolean(false);
    private @Nullable TargetInstance hovered;

    public BattleActionRenderTargetsWidget(final ParticipantActionInstance actionInstance) {
        this.actionInstance = actionInstance;
        hovered = null;
        final InvisiblePanelWidget<Collection<PositionedWidget>, PositionedWidget> parentWidget = new InvisiblePanelWidget<>(LayoutAlgorithm.BASIC, new ArrayList<>());
        widget = new HidingWidget(parentWidget, hidden::booleanValue);
        final GriddedPanelWidget panel = new GriddedPanelWidget(1, 1, 0.15, 0.125, false, () -> -1);
        parentWidget.addChild(WidgetModifiers.positioned(panel, () -> -(getScreenWidth() - 1) / 2, () -> -(getScreenHeight() - 1) / 2));
        panel.setSlot(new TextWidget(() -> new LiteralText("Confirm (Press enter)"), () -> true, Colour.WHITE.pack(255), 0.15, 0.125), 0, 0);
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        widget.resize(width, height, pixelWidth, pixelHeight);
    }

    private boolean mouseClicked(final int button) {
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
    public void render(final GuiContext context) {
        processEvents(context, event -> {
            if (event instanceof GuiInputContext.MouseClick click) {
                return mouseClicked(click.button);
            } else if (event instanceof GuiInputContext.KeyPress keyPress) {
                return keyPress(keyPress.keyCode);
            }
            return false;
        });
        final MinecraftClient client = MinecraftClient.getInstance();
        final Battle battle = ((BattleWorldSupplier) client.world).tbcex_getBattleWorld().getBattle(((BattleAwareEntity) client.player).tbcex_getCurrentBattle());
        if (battle != null) {
            final TargetType<?> nextType = actionInstance.getNextType();
            hidden.setValue(!actionInstance.canActivate());
            widget.render(context);
            if (nextType != null) {
                updateTargeted(nextType, battle, context.getTickDelta());
            }
            actionInstance.render(hovered, context.getTickDelta());
            if (hovered != null) {
                renderInfo(context);
            }
        }
    }

    private void renderInfo(final GuiContext context) {
        if (hovered != null) {
            final List<OrderedText> description = actionInstance.getTargetDescription();
            if (description != null) {
                context.addTooltip(description);
            }
        }
    }

    private void updateTargeted(final TargetType<?> nextType, final Battle battle, final float delta) {
        final Vec3d startVec = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(delta);
        final Vec3d lookVec = ClientUtil.getMouseVector();
        hovered = nextType.find(startVec, lookVec, actionInstance.getUser(), battle);
    }

    private boolean keyPress(final int keyCode) {
        if ((keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ENTER) && actionInstance.canActivate()) {
            activate();
            return true;
        }
        return false;
    }

    private void activate() {
        hidden.setTrue();
        actionInstance.activate();
        MinecraftClient.getInstance().setScreen(null);
    }
}
