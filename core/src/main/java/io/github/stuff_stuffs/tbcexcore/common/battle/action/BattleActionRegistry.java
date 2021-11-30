package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

public final class BattleActionRegistry {
    public static final Registry<Type<?>> REGISTRY = FabricRegistryBuilder.createSimple((Class<Type<?>>) (Object) Type.class, TBCExCore.createId("battle_actions")).buildAndRegister();
    public static final Codec<BattleAction<?>> CODEC = CodecUtil.createDependentPairCodecFirst(REGISTRY.getCodec(), type -> (Codec<BattleAction<?>>) type.codec, BattleAction::getType);
    public static final Type<ParticipantJoinBattleAction> PARTICIPANT_JOIN_BATTLE_ACTION = new Type<>(ParticipantJoinBattleAction.CODEC);
    public static final Type<TeleportBattleAction> TELEPORT_BATTLE_ACTION = new Type<>(TeleportBattleAction.CODEC);
    public static final Type<EndTurnBattleAction> END_TURN_BATTLE_ACTION = new Type<>(EndTurnBattleAction.CODEC);
    public static final Type<ParticipantEquipAction> PARTICIPANT_EQUIP_ACTION = new Type<>(ParticipantEquipAction.CODEC);
    public static final Type<ParticipantMoveBattleAction> PARTICIPANT_MOVE_BATTLE_ACTION = new Type<>(ParticipantMoveBattleAction.CODEC);
    public static final Type<BasicAttackBattleAction> BASIC_ATTACK_ACTION = new Type<>(BasicAttackBattleAction.CODEC);
    public static final Type<ParticipantUnequipAction> PARTICIPANT_UNEQUIP_ACTION = new Type<>(ParticipantUnequipAction.CODEC);

    private BattleActionRegistry() {
    }

    public static void init() {
        Registry.register(REGISTRY, TBCExCore.createId("join"), PARTICIPANT_JOIN_BATTLE_ACTION);
        Registry.register(REGISTRY, TBCExCore.createId("teleport"), TELEPORT_BATTLE_ACTION);
        Registry.register(REGISTRY, TBCExCore.createId("end_turn"), END_TURN_BATTLE_ACTION);
        Registry.register(REGISTRY, TBCExCore.createId("equip"), PARTICIPANT_EQUIP_ACTION);
        Registry.register(REGISTRY, TBCExCore.createId("move"), PARTICIPANT_MOVE_BATTLE_ACTION);
        Registry.register(REGISTRY, TBCExCore.createId("basic_attack"), BASIC_ATTACK_ACTION);
        Registry.register(REGISTRY, TBCExCore.createId("unequip"), PARTICIPANT_UNEQUIP_ACTION);
    }

    public static final class Type<T extends BattleAction<T>> {
        public final Codec<T> codec;

        public Type(final Codec<T> codec) {
            this.codec = codec;
        }
    }
}
