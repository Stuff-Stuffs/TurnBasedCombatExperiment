package io.github.stuff_stuffs.tbcextest.common.battle.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
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
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetStreams;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcextest.common.Test;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

//TODO arrows?
public class TestBowEquipment implements BattleEquipment {
    public static final Codec<BattleEquipment> CODEC = Codec.unit(TestBowEquipment::new).xmap(Function.identity(), o -> (TestBowEquipment) o);
    private static final BattleDamageComposition COMPOSITION = BattleDamageComposition.builder().addWeight(BattleDamageType.PHYSICAL, 1).build();
    private static final Function<BattleParticipantHandle, BattleDamagePacket> DAMAGE_PACKET_FACTORY = participantHandle -> new BattleDamagePacket(COMPOSITION, new BattleDamageSource(Optional.of(participantHandle)), 10);
    private static final Identifier EYE_PART = TBCExCore.createId("head");

    @Override
    public boolean validSlot(final BattleEquipmentSlot slot) {
        return slot == BattleEquipmentSlot.MAIN_HAND_SLOT;
    }

    @Override
    public Set<BattleEquipmentSlot> getBlockedSlots() {
        return Set.of(BattleEquipmentSlot.OFF_HAND_SLOT);
    }

    @Override
    public void initEvents(final BattleParticipantState state) {

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
            public ParticipantActionInstance createInstance(final BattleStateView battleState, final BattleParticipantHandle handle, final Consumer<BattleAction<?>> sender) {
                return new ParticipantActionInstance(
                        new SingleTargetParticipantActionInfo<>(
                                new ParticipantTargetType(
                                        (battleStateView, handle1) -> {
                                            final TargetStreams.Context context = new TargetStreams.Context(battleStateView, handle1);
                                            return () -> TargetStreams.getParticipantStream(context, false).
                                                    filter(TargetStreams.team(context, false)).
                                                    filter(TargetStreams.visibleParticipant(context, EYE_PART)).iterator();
                                        }
                                ), (battleState12, user, target) ->
                                new BasicAttackBattleAction(
                                        user,
                                        target.getHandle(),
                                        DAMAGE_PACKET_FACTORY.apply(user),
                                        1
                                ), sender, List.of()
                        ), battleState, handle
                );
            }
        }, BattleEquipment.createUnequipAction(participantView, slot));
    }
}
