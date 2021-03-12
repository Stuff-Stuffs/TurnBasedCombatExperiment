package io.github.stuff_stuffs.turnbasedcombat.common.impl.component;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleEntityComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BattleEntityComponentImpl implements BattleEntityComponent {
    private final Entity entity;
    private BattleHandle battleHandle;

    public BattleEntityComponentImpl(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public @Nullable BattleHandle getBattleHandle() {
        return battleHandle;
    }

    public void setBattleHandle(final BattleHandle battleHandle) {
        this.battleHandle = battleHandle;
    }

    @Override
    public boolean isInBattle() {
        final World world = entity.world;
        if (battleHandle != null && world != null) {
            final BattleWorldComponent battleWorld = Components.BATTLE_WORLD_COMPONENT_KEY.get(world);
            final Battle battle = battleWorld.fromHandle(battleHandle);
            if (battle != null) {
                return battle.isActive();
            }
        }
        return false;
    }

    @Override
    public void tick() {
        if (battleHandle != null) {
            final Battle battle = Components.BATTLE_WORLD_COMPONENT_KEY.get(entity.world).fromHandle(battleHandle);
            if (battle != null && !battle.isActive()) {
                battleHandle = null;
            }
        }
    }

    @Override
    public void readFromNbt(final CompoundTag tag) {
        if (tag.contains("battleHandle")) {
            final BattleHandle tmp = BattleHandle.fromTag(tag.getCompound("battleHandle"));
            if (battleHandle != null && !battleHandle.equals(tmp)) {
                throw new RuntimeException();
            }
            battleHandle = tmp;
        }

    }

    @Override
    public void writeToNbt(final CompoundTag tag) {
        if (battleHandle != null) {
            tag.put("battleHandle", battleHandle.toNbt());
        }
    }
}
