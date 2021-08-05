package io.github.stuff_stuffs.turnbasedcombat.client.gui;

import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.SingleHotbarSlotWidget;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.BattleWorldSupplier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public final class BattleHud {
    private final BattleHandle handle;
    private final PlayerEntity entity;

    public BattleHud(BattleHandle handle, PlayerEntity entity) {
        this.handle = handle;
        this.entity = entity;
        final int size = BattleEquipmentSlot.REGISTRY.getIds().size();
    }

    public boolean matches(BattleHandle handle) {
        return this.handle.equals(handle);
    }

    public void render(MatrixStack matrices, float tickDelta) {

    }
}
