package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.EquipBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class EquipEntityAction implements EntityAction {
    private final BattleEquipment equipment;
    private final int slot;
    private final Supplier<Text> nameSupplier;
    private final Supplier<List<TooltipComponent>> tooltipSupplier;

    public EquipEntityAction(final BattleEquipment equipment, final int slot, final Supplier<Text> nameSupplier, final Supplier<List<TooltipComponent>> tooltipSupplier) {
        this.equipment = equipment;
        this.slot = slot;
        this.nameSupplier = nameSupplier;
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public Text getName() {
        return nameSupplier.get();
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        return tooltipSupplier.get();
    }

    @Override
    public BattleAction getAction(final EntityStateView entityState) {
        return new EquipBattleAction(entityState.getHandle(), equipment, slot);
    }
}