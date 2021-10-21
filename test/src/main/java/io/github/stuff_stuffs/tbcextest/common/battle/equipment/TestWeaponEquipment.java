package io.github.stuff_stuffs.tbcextest.common.battle.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BasicAttackBattleAction;
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
import java.util.function.Function;

public class TestWeaponEquipment implements BattleEquipment {
    public static final Codec<BattleEquipment> CODEC = Codec.unit(TestWeaponEquipment::new).xmap(Function.identity(), o -> (TestWeaponEquipment) o);

    @Override
    public boolean validSlot(final BattleEquipmentSlot slot) {
        return slot == BattleEquipmentSlot.MAIN_HAND_SLOT;
    }

    @Override
    public Set<BattleEquipmentSlot> getBlockedSlots() {
        return Set.of(BattleEquipmentSlot.OFF_HAND_SLOT);
    }

    @Override
    public List<ParticipantAction> getActions(final BattleStateView stateView, final BattleParticipantStateView participantView) {
        return List.of(new ParticipantAction() {
            @Override
            public Text getName() {
                return new LiteralText("Ranged Attack");
            }

            @Override
            public List<TooltipComponent> getTooltip() {
                return List.of(TooltipComponent.of(new LiteralText("How are you ranged attacking with a sword?").asOrderedText()));
            }

            @Override
            public ParticipantActionInstance createInstance(final BattleStateView battleState, final BattleParticipantHandle handle) {
                return new ParticipantActionInstance(new SingleTargetParticipantActionInfo(new ParticipantTargetType((battleState1, handle1) -> {
                    final List<BattleParticipantHandle> handles = new ArrayList<>();
                    battleState1.getParticipants().forEachRemaining(h -> {
                        if (!h.equals(handle1)) {
                            handles.add(h);
                        }
                    });
                    return handles;
                }), (battleState12, user, target) -> BattleActionSender.send(user.battleId(),
                        new BasicAttackBattleAction(
                                user,
                                ((ParticipantTargetType.ParticipantTargetInstance) target).getHandle(),
                                new BattleDamagePacket(
                                        BattleDamageComposition.builder().addWeight(BattleDamageType.PHYSICAL, 1).build(),
                                        new BattleDamageSource(Optional.of(user)),
                                        3
                                ),
                                1
                        )
                ), List.of()), battleState, handle);
            }
        });
    }

    @Override
    public void initEvents(final BattleParticipantState state) {

    }

    @Override
    public void deinitEvents() {

    }

    @Override
    public BattleEquipmentType getType() {
        return Test.TEST_WEAPON_EQUIPMENT_TYPE;
    }
}
