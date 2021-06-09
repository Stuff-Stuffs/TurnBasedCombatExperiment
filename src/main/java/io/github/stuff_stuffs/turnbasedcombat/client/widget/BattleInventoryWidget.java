package io.github.stuff_stuffs.turnbasedcombat.client.widget;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.AbstractSpruceParentWidget;
import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.EntityInventoryView;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BattleInventoryWidget extends AbstractSpruceParentWidget<SpruceWidget> {
    private final BattleParticipantHandle handle;
    private final List<BattleEquipmentSlotWidget> equipmentSlots;

    private final List<SpruceWidget> combined;

    public BattleInventoryWidget(final Position position, final BattleParticipantHandle handle) {
        super(position, SpruceWidget.class);
        this.handle = handle;
        equipmentSlots = new ReferenceArrayList<>();
        int height = 0;
        for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            equipmentSlots.add(new BattleEquipmentSlotWidget(Position.of(0, height), handle, slot, 20, 20));
            height += 20;
        }

        combined = new ReferenceArrayList<>();
        combined.addAll(equipmentSlots);
    }

    private @Nullable EntityInventoryView getInventory() {
        final ClientBattleWorld battleWorld = ClientBattleWorld.get(MinecraftClient.getInstance().world);
        final Battle battle = battleWorld.getBattle(handle.battleId());
        if (battle != null) {
            final EntityStateView view = battle.getStateView().getParticipant(handle);
            if (view != null) {
                return view.getInventory();
            }
        }
        return null;
    }

    @Override
    protected void renderWidget(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        final EntityInventoryView inventory = getInventory();
        if (inventory == null) {
            //TODO
        } else {
            for (final SpruceWidget widget : combined) {
                widget.render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public List<SpruceWidget> children() {
        return combined;
    }
}
