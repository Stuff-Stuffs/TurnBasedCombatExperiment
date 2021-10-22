package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.ParticipantUnequipAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.NonTargettableParticipantActionInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface BattleEquipment {
    default List<ParticipantAction> getActions(final BattleStateView stateView, final BattleParticipantStateView participantView, final BattleEquipmentSlot slot) {
        return Collections.emptyList();
    }

    boolean validSlot(BattleEquipmentSlot slot);

    Set<BattleEquipmentSlot> getBlockedSlots();

    void initEvents(BattleParticipantState state);

    void deinitEvents();

    BattleEquipmentType getType();

    static ParticipantAction createUnequipAction(final BattleParticipantStateView participantView, final BattleEquipmentSlot slot) {
        return new ParticipantAction() {
            @Override
            public Text getName() {
                return new LiteralText("Unequip");
            }

            @Override
            public List<TooltipComponent> getTooltip() {
                return List.of(TooltipComponent.of(new LiteralText("Unequip this item").asOrderedText()));
            }

            @Override
            public ParticipantActionInstance createInstance(final BattleStateView battleState, final BattleParticipantHandle handle, final Consumer<BattleAction<?>> sender) {
                return new ParticipantActionInstance(new NonTargettableParticipantActionInfo((battleStateView, participantHandle) -> sender.accept(new ParticipantUnequipAction(handle, slot, 0.5))), battleState, handle);
            }
        };
    }
}
