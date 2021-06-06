package io.github.stuff_stuffs.turnbasedcombat.client.screen;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.SpruceToggleSwitch;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import dev.lambdaurora.spruceui.widget.container.tabbed.SpruceTabbedWidget;
import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class BattleScreen extends SpruceScreen {
    private SpruceTabbedWidget tabbedWidget;
    private SpruceContainerWidget equipmentActions;

    public BattleScreen() {
        super(new LiteralText("Battle Screen"));
    }

    @Override
    protected void init() {
        super.init();
        tabbedWidget = new SpruceTabbedWidget(Position.of(this, 0, 4), width, height - 35 - 4, title);
        tabbedWidget.addTabEntry(new LiteralText("Non-turn Actions"), /*TODO*/null, (width, height) -> {
            final var container = new SpruceContainerWidget(Position.origin(), width, height);
            container.addChildren((containerWidth, containerHeight, widgetAdder) -> {
                widgetAdder.accept(new SpruceToggleSwitch(Position.of(0, 16), 16, 4, new LiteralText("Freecam"), (button, newValue) -> {
                    //TODO
                }, true, true));
                widgetAdder.accept(new SpruceLabelWidget(Position.of(0, 48),
                        new LiteralText("These are actions you can perform when it is not your turn")
                                .formatted(Formatting.WHITE),
                        containerWidth, true));
            });
            return container;
        });
        tabbedWidget.addTabEntry(new LiteralText("Equipment Actions"), /*TODO*/ null, (width, height) -> {
            final var container = new SpruceContainerWidget(Position.origin(), width, height);
            equipmentActions = container;
            return container;
        });
        addDrawableChild(tabbedWidget);
    }

    @Override
    public void tick() {
        super.tick();
        if(isCurrentTurn()) {
            if(equipmentActions!=null) {
                equipmentActions.setActive(true);
                equipmentActions.setVisible(true);
            }
        } else {
            if(equipmentActions!=null) {
                equipmentActions.setActive(false);
                equipmentActions.setVisible(false);
            }
        }
    }

    private boolean isCurrentTurn() {
        Battle battle = ClientBattleWorld.get(MinecraftClient.getInstance().world).getBattle((BattleEntity) MinecraftClient.getInstance().player);
        if(battle==null) {
            return false;
        }
        return battle.getStateView().getCurrentTurn().getId().equals(MinecraftClient.getInstance().player.getUuid());
    }
}
