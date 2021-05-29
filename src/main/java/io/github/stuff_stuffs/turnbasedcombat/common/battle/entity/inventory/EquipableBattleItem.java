package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EntityAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EquipEntityAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public abstract class EquipableBattleItem implements BattleItem {
    @Override
    public EntityAction useAction(final EntityStateView entityState) {
        return new EquipEntityAction(getEquipment(entityState), entityState.getInventory().getSlot(this), getName(entityState), getTooltip(entityState));
    }

    protected abstract Supplier<Text> getName(EntityStateView entityState);

    protected abstract Supplier<List<TooltipComponent>> getTooltip(EntityStateView entityState);

    protected abstract BattleEquipment getEquipment(EntityStateView entityState);
}
