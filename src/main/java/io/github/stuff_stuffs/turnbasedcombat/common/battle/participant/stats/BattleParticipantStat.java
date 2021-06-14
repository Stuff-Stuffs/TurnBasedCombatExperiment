package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.function.ToDoubleFunction;

public final class BattleParticipantStat {
    public static final Registry<BattleParticipantStat> REGISTRY = FabricRegistryBuilder.createSimple(BattleParticipantStat.class, TurnBasedCombatExperiment.createId("stat")).buildAndRegister();
    private final Text name;
    private final ToDoubleFunction<BattleEntity> extractor;

    public BattleParticipantStat(final Text name, final ToDoubleFunction<BattleEntity> extractor) {
        this.name = name;
        this.extractor = extractor;
    }

    public Text getName() {
        return name;
    }

    public double extract(final BattleEntity entity) {
        return extractor.applyAsDouble(entity);
    }
}
