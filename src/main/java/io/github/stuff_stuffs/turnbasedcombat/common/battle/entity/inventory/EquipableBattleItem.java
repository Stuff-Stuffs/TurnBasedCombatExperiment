package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EntityAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EquipEntityAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentSlot;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class EquipableBattleItem implements BattleItem {
    @Override
    public Collection<EntityAction> useAction(final EntityStateView entityState) {
        final BattleEquipment equipment = getEquipment(entityState);
        return BattleEquipmentSlot.REGISTRY.stream().filter(slot -> slot.type() == equipment.getType()).map(slot -> new EquipEntityAction(slot, equipment, entityState.getInventory().getSlot(this), getName(entityState), getTooltip(entityState))).collect(Collectors.toList());
    }

    protected abstract Supplier<Text> getName(EntityStateView entityState);

    protected abstract Supplier<List<TooltipComponent>> getTooltip(EntityStateView entityState);

    protected abstract BattleEquipment getEquipment(EntityStateView entityState);
}
