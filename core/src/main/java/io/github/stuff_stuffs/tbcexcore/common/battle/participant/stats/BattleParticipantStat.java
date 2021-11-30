package io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.function.ToDoubleFunction;

public final class BattleParticipantStat {
    public static final Registry<BattleParticipantStat> REGISTRY = FabricRegistryBuilder.createSimple(BattleParticipantStat.class, TBCExCore.createId("stat")).buildAndRegister();
    public static final BattleParticipantStat MAX_HEALTH_STAT = new BattleParticipantStat(new LiteralText("max_health"), entity -> entity.tbcex_getStat(BattleParticipantStat.MAX_HEALTH_STAT));
    public static final BattleParticipantStat INTELLIGENCE_STAT = new BattleParticipantStat(new LiteralText("intelligence"), entity -> entity.tbcex_getStat(BattleParticipantStat.INTELLIGENCE_STAT));
    public static final BattleParticipantStat DEXTERITY_STAT = new BattleParticipantStat(new LiteralText("dexterity"), entity -> entity.tbcex_getStat(BattleParticipantStat.DEXTERITY_STAT));
    public static final BattleParticipantStat VITALITY_STAT = new BattleParticipantStat(new LiteralText("vitality"), entity -> entity.tbcex_getStat(BattleParticipantStat.VITALITY_STAT));
    public static final BattleParticipantStat STRENGTH_STAT = new BattleParticipantStat(new LiteralText("strength"), entity -> entity.tbcex_getStat(BattleParticipantStat.STRENGTH_STAT));
    public static final BattleParticipantStat PERCEPTION_STAT = new BattleParticipantStat(new LiteralText("perception"), entity -> entity.tbcex_getStat(BattleParticipantStat.PERCEPTION_STAT));
    public static final BattleParticipantStat ENERGY_PER_TURN_STAT = new BattleParticipantStat(new LiteralText("energy_per_turn"), e -> 10);
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

    public static void init() {
        Registry.register(REGISTRY, TBCExCore.createId("max_health"), MAX_HEALTH_STAT);
        Registry.register(REGISTRY, TBCExCore.createId("intelligence"), INTELLIGENCE_STAT);
        Registry.register(REGISTRY, TBCExCore.createId("dexterity"), DEXTERITY_STAT);
        Registry.register(REGISTRY, TBCExCore.createId("vitality"), VITALITY_STAT);
        Registry.register(REGISTRY, TBCExCore.createId("strength"), STRENGTH_STAT);
        Registry.register(REGISTRY, TBCExCore.createId("energy_per_turn"), ENERGY_PER_TURN_STAT);
        Registry.register(REGISTRY, TBCExCore.createId("perception"), PERCEPTION_STAT);
    }
}
