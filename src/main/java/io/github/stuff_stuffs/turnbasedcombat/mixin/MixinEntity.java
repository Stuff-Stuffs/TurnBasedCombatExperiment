package io.github.stuff_stuffs.turnbasedcombat.mixin;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Team;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

//TODO remove, this is only for tests
@Mixin(Entity.class)
public class MixinEntity implements BattleEntity {
    @Override
    public Team getTeam() {
        return new Team(123456789);
    }

    @Override
    public boolean isActiveEntity() {
        return true;
    }
}
