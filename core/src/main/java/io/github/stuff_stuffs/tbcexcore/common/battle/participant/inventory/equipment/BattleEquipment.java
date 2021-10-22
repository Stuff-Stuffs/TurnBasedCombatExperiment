package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment;

import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.ParticipantEquipAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.ParticipantUnequipAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.NonTargettableParticipantActionInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public interface BattleEquipment {
    default List<ParticipantAction> getActions(final BattleStateView stateView, final BattleParticipantStateView participantView, BattleEquipmentSlot slot) {
        return Collections.emptyList();
    }

    boolean validSlot(BattleEquipmentSlot slot);

    Set<BattleEquipmentSlot> getBlockedSlots();

    void initEvents(BattleParticipantState state);

    void deinitEvents();

    BattleEquipmentType getType();

    static ParticipantAction createUnequipAction(final BattleParticipantStateView participantView, BattleEquipmentSlot slot) {
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
            public ParticipantActionInstance createInstance(BattleStateView battleState, BattleParticipantHandle handle) {
                return new ParticipantActionInstance(new NonTargettableParticipantActionInfo(new BiConsumer<BattleStateView, BattleParticipantHandle>() {
                    @Override
                    public void accept(BattleStateView battleStateView, BattleParticipantHandle participantHandle) {
                        //TODO what happens on serverside when an ai tries this
                        BattleActionSender.send(handle.battleId(), new ParticipantUnequipAction(handle, slot, 0.5));
                    }
                }), battleState, handle);
            }
        };
    }
}
