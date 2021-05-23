package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.SkillInfo;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import net.minecraft.text.Text;

import java.util.UUID;

public record BattleParticipant(Text name, UUID id,
                                Team team,
                                SkillInfo skillInfo) implements BattleParticipantView {
    public static final Codec<BattleParticipant> CODEC = RecordCodecBuilder.create(battleParticipantInstance -> battleParticipantInstance.group(
            CodecUtil.TEXT_CODEC.fieldOf("name").forGetter(BattleParticipant::getName),
            CodecUtil.UUID_CODEC.fieldOf("id").forGetter(BattleParticipant::getId),
            Team.CODEC.fieldOf("team").forGetter(BattleParticipant::getTeam),
            SkillInfo.CODEC.fieldOf("skillInfo").forGetter(BattleParticipant::getSkillInfo))
            .apply(battleParticipantInstance, BattleParticipant::new));

    @Override
    public Text getName() {
        return name;
    }

    @Override
    public UUID getId() {
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
