package io.github.stuff_stuffs.tbcexequipment.common.battle.item;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.item.EquipmentInstanceItem;
import io.github.stuff_stuffs.tbcexequipment.common.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.List;

public final class BattleEquipmentItemTypes {
    public static final BattleParticipantItemType EQUIPMENT_INSTANCE_ITEM_TYPE = new BattleParticipantItemType(ParticipantEquipmentInstanceItem.CODEC, (i, j) -> false, (i, j) -> {
        throw new UnsupportedOperationException();
    }, itemStack -> {
        final ItemStack stack = new ItemStack(Items.EQUIPMENT_INSTANCE, itemStack.getCount());
        stack.setSubNbt(EquipmentInstanceItem.INSTANCE_KEY, ((ParticipantEquipmentInstanceItem) itemStack.getItem()).toNbt());
        return List.of(stack);
    });

    public static void init() {
        Registry.register(BattleParticipantItemType.REGISTRY, TBCExEquipment.createId("equipment"), EQUIPMENT_INSTANCE_ITEM_TYPE);
    }

    private BattleEquipmentItemTypes() {
    }
}
