package io.github.stuff_stuffs.tbcexequipment.common.item;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.item.BattleItem;
import io.github.stuff_stuffs.tbcexequipment.common.battle.item.ParticipantEquipmentInstanceItem;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentInstance;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.World;

import java.util.Optional;

public class EquipmentInstanceItem extends Item implements BattleItem {
    public static final String INSTANCE_KEY = "tbcex_equipment_instance";

    public EquipmentInstanceItem(final Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(final ItemStack stack, final World world, final Entity entity, final int slot, final boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        final NbtCompound nbt = stack.getSubNbt(INSTANCE_KEY);
        if (nbt != null) {
            return;
        }
        stack.setCount(0);
    }

    @Override
    public BattleParticipantItemStack toBattleItem(final ItemStack stack) {
        final NbtCompound nbt = stack.getSubNbt(INSTANCE_KEY);
        if (nbt == null) {
            return null;
        }
        final Optional<EquipmentInstance> optional = EquipmentInstance.CODEC.parse(NbtOps.INSTANCE, nbt).result();
        return optional.map(ParticipantEquipmentInstanceItem::new).map(item -> new BattleParticipantItemStack(item, stack.getCount())).orElse(null);
    }
}
