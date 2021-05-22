package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.SkillInfo;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import net.minecraft.text.Text;

public final class BattleParticipant implements BattleParticipantView {
    public static final Codec<BattleParticipant> CODEC = RecordCodecBuilder.create(battleParticipantInstance -> battleParticipantInstance.group(
            CodecUtil.TEXT_CODEC.fieldOf("name").forGetter(BattleParticipant::getName),
            Codec.INT.fieldOf("id").forGetter(BattleParticipant::getId),
            Team.CODEC.fieldOf("team").forGetter(BattleParticipant::getTeam),
            SkillInfo.CODEC.fieldOf("skillInfo").forGetter(BattleParticipant::getSkillInfo))
            .apply(battleParticipantInstance, BattleParticipant::new));
    private final Text name;
    private final int id;
    private final Team team;
    private final SkillInfo skillInfo;

    public BattleParticipant(final Text name, final int id, final Team team, final SkillInfo skillInfo) {
        this.name = name;
        this.id = id;
        this.team = team;
        this.skillInfo = skillInfo;
    }

    @Override
    public Text getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    public SkillInfo getSkillInfo() {
        return skillInfo;
    }
}
