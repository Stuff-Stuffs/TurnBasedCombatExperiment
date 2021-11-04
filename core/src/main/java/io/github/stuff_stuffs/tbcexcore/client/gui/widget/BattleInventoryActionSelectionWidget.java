package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.client.gui.BattleActionScreen;
import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.PressableButtonWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.List;
import java.util.function.BooleanSupplier;

public class BattleInventoryActionSelectionWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final BattleParticipantHandle handle;
    private final ParentWidget panel;
    private boolean shouldClose = false;

    public BattleInventoryActionSelectionWidget(WidgetPosition position, final BattleStateView battleState, final BattleParticipantHandle handle, final Battle battle, final List<ParticipantAction> actions) {
        this.position = position;
        this.handle = handle;
        final double v = position.getY() + (0.15 * actions.size() + 0.05);
        if (v < 0) {
            position = WidgetPosition.combine(position, WidgetPosition.of(0, 0.15 * actions.size() + 0.05, 0));
        } else if (v > 1) {
            position = WidgetPosition.combine(position, WidgetPosition.of(0, -(0.15 * actions.size() + 0.05), 0));
        }
        panel = new BasicPanelWidget(position, () -> false, () -> 2, 0.25, 0.15 * actions.size() + 0.05);
        int index = 0;
        for (final ParticipantAction action : actions) {
            final BooleanSupplier s = () -> {
                final ParticipantActionInstance instance = action.createInstance(battle.getState(), handle, this::send);
                return (instance.getNextType() == null && instance.canActivate()) || (instance.getNextType() != null && instance.getNextType().isAnyValid(instance.getUser(), battle));
            };
            panel.addWidget(
                    new PressableButtonWidget(
                            WidgetPosition.combine(
                                    WidgetPosition.of(0.025, index * 0.15 + 0.025, 0),
                                    position
                            ),
                            () -> 4,
                            s,
                            0.2,
                            0.15,
                            action::getName,
                            () -> {
                                if (s.getAsBoolean()) {
                                    return action.getTooltip();
                                } else {
                                    return List.of(TooltipComponent.of(new LiteralText("No available targets").asOrderedText()));
                                }
                            },
                            () -> {
                                final ParticipantActionInstance instance = action.createInstance(battleState, handle, this::send);
                                if (instance.getNextType() == null && instance.canActivate()) {
                                    instance.activate();
                                    shouldClose = true;
                                } else {
                                    MinecraftClient.getInstance().setScreen(new BattleActionScreen(handle, instance));
                                }
                            }
                    )
            );
            index++;
        }
    }

    public boolean shouldClose() {
        return shouldClose;
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        panel.resize(width, height, pixelWidth, pixelHeight);
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return panel.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return panel.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return panel.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        panel.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return panel.keyPress(keyCode, scanCode, modifiers);
    }

    private void send(final BattleAction<?> battleAction) {
        BattleActionSender.send(handle.battleId(), battleAction);
    }
}
