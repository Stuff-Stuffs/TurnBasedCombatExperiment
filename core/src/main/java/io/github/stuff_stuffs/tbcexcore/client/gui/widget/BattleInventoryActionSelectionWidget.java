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
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.SelectionWheelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.List;

public class BattleInventoryActionSelectionWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final BattleParticipantHandle handle;
    private final SelectionWheelWidget actionWheel;
    private boolean shouldClose = false;

    public BattleInventoryActionSelectionWidget(final WidgetPosition position, final BattleStateView battleState, final BattleParticipantHandle handle, final Battle battle, final List<ParticipantAction> actions) {
        this.position = position;
        this.handle = handle;
        final SelectionWheelWidget.Builder builder = SelectionWheelWidget.builder();
        for (final ParticipantAction action : actions) {
            final List<TooltipComponent> tooltip;
            final ParticipantActionInstance testInstance = action.createInstance(battle.getState(), handle, this::send);
            final boolean s = (testInstance.getNextType() == null && testInstance.canActivate()) || (testInstance.getNextType() != null && testInstance.getNextType().isAnyValid(testInstance.getUser(), battle));
            if (s) {
                tooltip = action.getTooltip();
            } else {
                tooltip = List.of(TooltipComponent.of(new LiteralText("No available targets").asOrderedText()));
            }
            builder.addEntry(BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 223, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 223, action::getName, tooltip, () -> true, () -> {
                final ParticipantActionInstance instance = action.createInstance(battleState, handle, this::send);
                if (instance.getNextType() == null && instance.canActivate()) {
                    instance.activate();
                    shouldClose = true;
                } else {
                    MinecraftClient.getInstance().setScreen(new BattleActionScreen(handle, instance));
                }
            });
        }
        actionWheel = builder.build(0.15, 0.3, 0.3125, position);
    }

    public boolean shouldClose() {
        return shouldClose;
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        actionWheel.resize(width, height, pixelWidth, pixelHeight);
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return actionWheel.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return actionWheel.mouseReleased(mouseX,mouseY,button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return actionWheel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return actionWheel.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        actionWheel.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return actionWheel.keyPress(keyCode, scanCode, modifiers);
    }

    private void send(final BattleAction<?> battleAction) {
        BattleActionSender.send(handle.battleId(), battleAction);
    }
}
