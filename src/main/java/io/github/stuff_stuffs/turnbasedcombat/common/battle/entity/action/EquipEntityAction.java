package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.EquipBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentSlot;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class EquipEntityAction implements UsableBattleAction {
    private final BattleEquipmentSlot slot;
    private final BattleEquipment equipment;
    private final int inventorySlot;
    private final Supplier<Text> nameSupplier;
    private final Supplier<List<TooltipComponent>> tooltipSupplier;

    public EquipEntityAction(BattleEquipmentSlot slot, final BattleEquipment equipment, final int inventorySlot, final Supplier<Text> nameSupplier, final Supplier<List<TooltipComponent>> tooltipSupplier) {
        this.slot = slot;
        this.equipment = equipment;
        this.inventorySlot = inventorySlot;
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
    public BattleAction apply(final EntityStateView entityState) {
        return new EquipBattleAction(entityState.getHandle(), slot, equipment, inventorySlot);
    }
}
