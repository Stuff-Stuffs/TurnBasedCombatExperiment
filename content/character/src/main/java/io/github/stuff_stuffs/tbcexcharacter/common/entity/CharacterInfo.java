package io.github.stuff_stuffs.tbcexcharacter.common.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcharacter.common.entity.stat.StatSourceContainer;
import io.github.stuff_stuffs.tbcexcharacter.common.entity.stat.StatSources;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;

public class CharacterInfo {
    public static final Codec<CharacterInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            XpContainer.CODEC.fieldOf("xp").forGetter(info -> info.xpContainer),
            StatSourceContainer.CODEC.fieldOf("stats").forGetter(info -> info.statContainer)
    ).apply(instance, CharacterInfo::new));
    private final XpContainer xpContainer;
    private final StatSourceContainer statContainer;

    private CharacterInfo(XpContainer xpContainer, StatSourceContainer statContainer) {
        this.xpContainer = xpContainer;
        this.statContainer = statContainer;
    }

    public CharacterInfo() {
        xpContainer = new XpContainer(0,0);
        statContainer = new StatSourceContainer();
    }

    public double getStat(BattleParticipantStat stat) {
        return statContainer.getStat(stat);
    }

    public void forEachSourcedStat(BattleParticipantStat stat, StatSources.ForEach forEach) {
        statContainer.forEach(stat, forEach);
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
