package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.ParticipantEquipAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class ParticipantEquipActionUtil {
    public static final class EquipActionInfo implements ParticipantActionInfo {
        private final BattleParticipantInventoryHandle handle;
        private final BattleEquipmentSlot slot;
        private final Consumer<BattleAction<?>> sender;

        public EquipActionInfo(final BattleParticipantInventoryHandle handle, final BattleEquipmentSlot slot, final Consumer<BattleAction<?>> sender) {
            this.handle = handle;
            this.slot = slot;
            this.sender = sender;
        }

        @Override
        public @Nullable TargetType getNextTargetType(final List<TargetInstance> list) {
            return null;
        }

        @Override
        public boolean canActivate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
            return list.size() == 0;
        }

        @Override
        public void activate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
            sender.accept(new ParticipantEquipAction(user, slot, handle, 5));
        }

        @Override
        public @Nullable List<TooltipComponent> getDescription(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
            return null;
        }
    }

    public static ParticipantActionInstance create(final BattleStateView battleState, final BattleParticipantInventoryHandle handle, final BattleEquipmentSlot slot, Consumer<BattleAction<?>> sender) {
        return new ParticipantActionInstance(new EquipActionInfo(handle, slot, sender), battleState, handle.handle());
    }

    public static List<ParticipantAction> getActions(final BattleParticipantStateView participantState, final BattleParticipantInventoryHandle handle) {
        return BattleEquipmentSlot.REGISTRY.stream().filter(slot -> participantState.canEquip(handle, slot)).<ParticipantAction>map(slot -> new ParticipantAction() {
            @Override
            public Text getName() {
                return new LiteralText("Equip: ").append(slot.name());
            }

            @Override
            public List<TooltipComponent> getTooltip() {
                return List.of();
            }

            @Override
            public ParticipantActionInstance createInstance(final BattleStateView battleState, final BattleParticipantHandle participantHandle, Consumer<BattleAction<?>> sender) {
                return ParticipantEquipActionUtil.create(battleState, handle, slot, sender);
            }
        }).toList();
    }

    private ParticipantEquipActionUtil() {
    }
}
