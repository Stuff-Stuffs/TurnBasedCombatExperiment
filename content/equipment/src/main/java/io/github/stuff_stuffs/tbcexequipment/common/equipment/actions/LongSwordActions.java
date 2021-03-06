package io.github.stuff_stuffs.tbcexequipment.common.equipment.actions;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BasicAttackBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageComposition;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageSource;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.SingleTargetParticipantActionInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.ParticipantTargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetStreams;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentActions;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentTypes;
import io.github.stuff_stuffs.tbcexequipment.common.part.stats.PartStatContext;
import io.github.stuff_stuffs.tbcexequipment.common.part.stats.PartStats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class LongSwordActions {
    private LongSwordActions() {
    }

    public static void init() {
        EquipmentActions.register(EquipmentTypes.LONG_SWORD_EQUIPMENT, (data, battleState, handle, slot) -> {
            final double damage = TBCExEquipment.PART_STAT_MANAGER.get(data.getBlade().getData(), PartStats.LONG_SWORD_BLADE_DAMAGE, PartStatContext.of(data));
            return new ParticipantAction() {
                @Override
                public Text getName() {
                    return new LiteralText("Basic Attack");
                }

                @Override
                public List<OrderedText> getTooltip() {
                    return List.of(new LiteralText("Attack with a base damage of " + damage).asOrderedText());
                }

                @Override
                public ParticipantActionInstance createInstance(final BattleStateView battleState, final BattleParticipantHandle handle, final Consumer<BattleAction<?>> sender) {
                    return new ParticipantActionInstance(
                            new SingleTargetParticipantActionInfo<>(
                                    new ParticipantTargetType(
                                            TargetStreams.setupParticipantContext(ctx -> TargetStreams.getParticipantStream(ctx, false).filter(TargetStreams.withinRange(ctx, 1.5)).toList())
                                    ),
                                    (battleState1, user, target) -> new BasicAttackBattleAction(user, target.getHandle(), new BattleDamagePacket(BattleDamageComposition.builder().addWeight(BattleDamageType.PHYSICAL, 1).build(), new BattleDamageSource(Optional.of(user)), damage), 1),
                                    sender,
                                    List.of()
                            ),
                            battleState,
                            handle
                    );
                }
            };
        });
    }
}
