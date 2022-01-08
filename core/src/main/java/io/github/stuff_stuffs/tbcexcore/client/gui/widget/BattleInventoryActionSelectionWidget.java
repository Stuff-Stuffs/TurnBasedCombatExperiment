package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.client.gui.BattleActionScreen;
import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetModifiers;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.SelectionWheelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;

import java.util.List;

public class BattleInventoryActionSelectionWidget extends AbstractWidget implements PositionedWidget {
    private final BattleParticipantHandle handle;
    private final PositionedWidget actionWheel;
    private final double x;
    private final double y;
    private boolean shouldClose = false;

    public BattleInventoryActionSelectionWidget(final BattleStateView battleState, final BattleParticipantHandle handle, final Battle battle, final List<ParticipantAction> actions, final double mouseX, final double mouseY) {
        this.handle = handle;
        final SelectionWheelWidget.Builder builder = SelectionWheelWidget.builder();
        for (final ParticipantAction action : actions) {
            final List<OrderedText> tooltip;
            final ParticipantActionInstance testInstance = action.createInstance(battle.getState(), handle, this::send);
            final boolean s = (testInstance.getNextType() == null && testInstance.canActivate()) || (testInstance.getNextType() != null && testInstance.getNextType().isAnyValid(testInstance.getUser(), battle));
            if (s) {
                tooltip = action.getTooltip();
            } else {
                tooltip = List.of(new LiteralText("No available targets").asOrderedText());
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
        actionWheel = WidgetModifiers.positioned(builder.build(0.15, 0.3, 0.3125), () -> mouseX, () -> mouseY);
        x = mouseX;
        y = mouseY;
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
    public void render(final GuiContext context) {
        context.enterSection(getDebugName());
        actionWheel.render(context);
        context.exitSection();
    }

    @Override
    public String getDebugName() {
        return "BattleInventoryActionSelectionWidget";
    }

    private void send(final BattleAction<?> battleAction) {
        BattleActionSender.send(handle.battleId(), battleAction);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
