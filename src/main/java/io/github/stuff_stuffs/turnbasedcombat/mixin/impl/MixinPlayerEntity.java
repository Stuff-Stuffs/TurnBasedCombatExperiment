package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.SkillInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements BattleEntity {
    @Unique
    private static final Team TEAM = new Team("test_player");
    @Unique
    private static final SkillInfo SKILL_INFO = new SkillInfo(20, 20, 1);

    @Shadow public abstract Text getName();

    @Override
    public SkillInfo getSkillInfo() {
        return SKILL_INFO;
    }

    @Override
    public Team getTeam() {
        return TEAM;
    }

    @Override
    public Text getBattleName() {
        return getName();
    }
}
