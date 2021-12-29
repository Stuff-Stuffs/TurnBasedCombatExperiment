package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.AbstractParticipantStatListWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BooleanSupplier;

public class BattleMenuScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
    private final World world;
    private final BattleHudContext context;

    public BattleMenuScreen(final BattleParticipantHandle handle, final World world, final BattleHudContext context) {
        super(new LiteralText("battle_menu_screen"), new RootPanelWidget());
        this.handle = handle;
        this.world = world;
        this.context = context;
        final ParentWidget widget = (ParentWidget) this.widget;
        final BooleanSupplier isTurn = () -> {
            final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
            if (battle == null) {
                return false;
            }
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant == null) {
                return false;
            }
            return handle.equals(battle.getState().getCurrentTurn());
        };
        final WidgetPosition panelPos = WidgetPosition.of(0.5, 0.5, 1);
        final SelectionWheelWidget wheelWidget = SelectionWheelWidget.builder().
                addEntry(BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 127, () -> {
                    if(!isTurn.getAsBoolean()) {
                        return new LiteralText("Inventory").setStyle(Style.EMPTY.withColor(AbstractParticipantStatListWidget.NEGATIVE_COLOUR.pack()));
                    }
                    return new LiteralText("Inventory");
                }, List.of(), isTurn, () -> MinecraftClient.getInstance().setScreen(new BattleInventoryScreen(handle, context, world))).
                addEntry(BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 127, () -> {
                    if(!isTurn.getAsBoolean()) {
                        return new LiteralText("Move").setStyle(Style.EMPTY.withColor(AbstractParticipantStatListWidget.NEGATIVE_COLOUR.pack()));
                    }
                    return new LiteralText("Move");
                }, List.of(), isTurn, () -> MinecraftClient.getInstance().setScreen(new BattleMoveScreen(handle, world, context))).
                addEntry(BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 127, () -> new LiteralText("Self Info"), List.of(), () -> true, () -> MinecraftClient.getInstance().setScreen(new BattleParticipantSelfInfoScreen(world, handle))).
                addEntry(BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 127, () -> new LiteralText("Other Info"), List.of(), () -> true, () -> MinecraftClient.getInstance().setScreen(new BattleParticipantOtherInfoScreen(world, handle))).
                build(0.15, 0.3, 0.3125, panelPos);
        widget.addWidget(wheelWidget);
    }
}
