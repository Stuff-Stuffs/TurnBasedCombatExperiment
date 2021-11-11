package io.github.stuff_stuffs.tbcexequipment.common.battle.item;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantEquipActionUtil;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.*;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexequipment.common.battle.equipment.EquipmentBattleEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentInstance;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;

import java.util.List;

public class ParticipantEquipmentInstanceItem implements BattleParticipantEquipmentItem {
    public static final Codec<BattleParticipantItem> CODEC = EquipmentInstance.CODEC.xmap(ParticipantEquipmentInstanceItem::new, item -> ((ParticipantEquipmentInstanceItem) item).equipmentInstance);
    private final EquipmentInstance equipmentInstance;

    public ParticipantEquipmentInstanceItem(final EquipmentInstance equipmentInstance) {
        this.equipmentInstance = equipmentInstance;
    }

    @Override
    public BattleEquipment createEquipmentInstance(final BattleParticipantItemStack stack) {
        return new EquipmentBattleEquipment(equipmentInstance);
    }

    @Override
    public List<ParticipantAction> getActions(final BattleStateView battleState, final BattleParticipantStateView participantState, final BattleParticipantInventoryHandle handle) {
        return ParticipantEquipActionUtil.getActions(participantState, handle);
    }

    @Override
    public BattleParticipantItemType getType() {
        return BattleEquipmentItemTypes.EQUIPMENT_INSTANCE_ITEM_TYPE;
    }

    @Override
    public boolean isInCategory(final BattleParticipantItemCategory category) {
        return equipmentInstance.getData().isInCategory(category);
    }

    @Override
    public Text getName() {
        return equipmentInstance.getType().getName();
    }

    @Override
    public List<Text> getTooltip() {
        return equipmentInstance.getData().getTooltip();
    }

    @Override
    public RarityInstance getRarity() {
        return equipmentInstance.getData().getRarity();
    }

    public NbtElement toNbt() {
        return EquipmentInstance.CODEC.encodeStart(NbtOps.INSTANCE, equipmentInstance).getOrThrow(false, s -> {
            throw new TBCExException(s);
        });
    }
}
