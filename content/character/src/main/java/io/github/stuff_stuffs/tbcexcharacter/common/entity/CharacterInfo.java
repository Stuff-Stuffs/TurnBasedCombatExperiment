package io.github.stuff_stuffs.tbcexcharacter.common.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcharacter.common.entity.stat.SourcedStat;
import io.github.stuff_stuffs.tbcexcharacter.common.entity.stat.SourcedStatContainer;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;

import java.util.function.Consumer;

public class CharacterInfo {
    public static final Codec<CharacterInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            XpContainer.CODEC.fieldOf("xp").forGetter(info -> info.xpContainer),
            SourcedStatContainer.CODEC.fieldOf("stats").forGetter(info -> info.statContainer)
    ).apply(instance, CharacterInfo::new));
    private final XpContainer xpContainer;
    private final SourcedStatContainer statContainer;

    private CharacterInfo(XpContainer xpContainer, SourcedStatContainer statContainer) {
        this.xpContainer = xpContainer;
        this.statContainer = statContainer;
    }

    public CharacterInfo() {
        xpContainer = new XpContainer(0,0);
        statContainer = new SourcedStatContainer();
    }

    public double getStat(BattleParticipantStat stat) {
        return statContainer.getStat(stat);
    }

    public void forEachSourcedStat(BattleParticipantStat stat, Consumer<SourcedStat<?>> consumer) {
        statContainer.forEach(stat, consumer);
    }

    public boolean tryLevelUp() {
        return xpContainer.tryLevelUp();
    }

    public void addXp(double xp) {
        xpContainer.addXp(xp);
    }

    public int getLevel() {
        return xpContainer.getLevel();
    }

    public double getCumulativeXp() {
        return xpContainer.getCumulativeXp();
    }

    public double getCurrentXp() {
        return xpContainer.getCurrentXp();
    }

    public double getCurrentLevelProgress() {
        return xpContainer.getCurrentLevelProgress();
    }
}
