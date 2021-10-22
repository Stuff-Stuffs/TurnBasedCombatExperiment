package io.github.stuff_stuffs.tbcextest.common.battle.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BasicAttackBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageComposition;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageSource;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.SingleTargetParticipantActionInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.ParticipantTargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcextest.common.Test;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class TestBowEquipment implements BattleEquipment {
    public static final Codec<BattleEquipment> CODEC = Codec.unit(TestBowEquipment::new).xmap(Function.identity(), o -> (TestBowEquipment) o);
    @Override
    public boolean validSlot(BattleEquipmentSlot slot) {
        return slot==BattleEquipmentSlot.MAIN_HAND_SLOT;
    }

    @Override
    public Set<BattleEquipmentSlot> getBlockedSlots() {
        return Set.of(BattleEquipmentSlot.OFF_HAND_SLOT);
    }

    @Override
    public void initEvents(BattleParticipantState state) {

    }

    @Override
    public void deinitEvents() {

    }

    @Override
    public BattleEquipmentType getType() {
        return Test.TEST_BOW_EQUIPMENT_TYPE;
    }

    @Override
    public List<ParticipantAction> getActions(final BattleStateView stateView, final BattleParticipantStateView participantView, final BattleEquipmentSlot slot) {
        return List.of(new ParticipantAction() {
            @Override
            public Text getName() {
                return new LiteralText("Ranged Attack");
            }

            @Override
            public List<TooltipComponent> getTooltip() {
                return List.of();
            }

            @Override
            public ParticipantActionInstance createInstance(final BattleStateView battleState, final BattleParticipantHandle handle, Consumer<BattleAction<?>> sender) {
                return new ParticipantActionInstance(new SingleTargetParticipantActionInfo(new ParticipantTargetType((battleState1, handle1) -> {
                    final List<BattleParticipantHandle> handles = new ArrayList<>();
                    battleState1.getParticipants().forEachRemaining(h -> {
                        if (!h.equals(handle1)) {
                            handles.add(h);
                        }
                    });
                    return handles;
                }), (battleState12, user, target) -> sender.accept(
                        new BasicAttackBattleAction(
                                user,
                                ((ParticipantTargetType.ParticipantTargetInstance) target).getHandle(),
                                new BattleDamagePacket(
                                        BattleDamageComposition.builder().addWeight(BattleDamageType.PHYSICAL, 1).build(),
                                        new BattleDamageSource(Optional.of(user)),
                                        10
                                ),
                                1
                        )
                ), List.of()), battleState, handle);
            }
        }, BattleEquipment.createUnequipAction(participantView, slot));
    }
}
