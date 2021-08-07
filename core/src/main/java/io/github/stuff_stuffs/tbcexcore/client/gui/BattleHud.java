package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public final class BattleHud {
    private final BattleHandle handle;
    private final PlayerEntity entity;
    private final EquipmentHud equipmentHud;

    public BattleHud(BattleHandle handle, PlayerEntity entity) {
        this.handle = handle;
        this.entity = entity;
        equipmentHud = new EquipmentHud(new BattleParticipantHandle(handle, entity.getUuid()));
    }

    public boolean matches(BattleHandle handle) {
        return this.handle.equals(handle);
    }

    public void render(MatrixStack matrices, float tickDelta) {
        equipmentHud.render(matrices, tickDelta);
    }

    public void tick() {
        final Battle battle = ((BattleWorldSupplier) entity.world).tbcex_getBattleWorld().getBattle(handle);
        if(battle!=null) {
            equipmentHud.tick(battle);
        }
    }
}
