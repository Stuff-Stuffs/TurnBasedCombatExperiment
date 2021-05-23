package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.SkillInfo;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import net.minecraft.text.Text;

import java.util.UUID;

public record BattleParticipant(Text name, UUID id,
                                Team team,
                                SkillInfo skillInfo) {
    public static final Codec<BattleParticipant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtil.TEXT_CODEC.fieldOf("name").forGetter(BattleParticipant::name),
            CodecUtil.UUID_CODEC.fieldOf("id").forGetter(BattleParticipant::id),
            Team.CODEC.fieldOf("team").forGetter(BattleParticipant::team),
            SkillInfo.CODEC.fieldOf("skillInfo").forGetter(BattleParticipant::skillInfo)
    ).apply(instance, BattleParticipant::new));

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BattleParticipant that)) {
            return false;
        }

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
