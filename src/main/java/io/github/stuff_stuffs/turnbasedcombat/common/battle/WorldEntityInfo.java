package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class WorldEntityInfo {
    private final boolean player;
    private final NbtCompound nbt;
    private final Vec3d position;

    public WorldEntityInfo(BattleEntity battleEntity) {
        player = battleEntity instanceof PlayerEntity;
        boolean shouldRemove = !player;
        nbt = new NbtCompound();
        if(!player) {
            ((Entity) battleEntity).saveNbt(nbt);
        }
        position = ((Entity)battleEntity).getPos();
        if(shouldRemove) {
            ((Entity)battleEntity).remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public boolean isPlayer() {
        return player;
    }

    public Vec3d getPosition() {
        return position;
    }
}
