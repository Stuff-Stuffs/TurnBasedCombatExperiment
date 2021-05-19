package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipant;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import net.minecraft.nbt.NbtOps;

public final class JoinBattleAction extends BattleAction {
    public static final Codec<JoinBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(action -> action.handle),
            BattleParticipant.CODEC.fieldOf("participant").forGetter(action -> action.participant)
    ).apply(instance, JoinBattleAction::new));
    private final BattleParticipant participant;

    public JoinBattleAction(final BattleParticipantHandle handle, final BattleParticipant participant) {
        super(handle);
        this.participant = participant;
    }

    @Override
    public void applyToState(final BattleState state) {
        //TODO implement actual copying
        final BattleParticipant participant = decode(encode(NbtOps.INSTANCE), NbtOps.INSTANCE).participant;
        state.addParticipant(participant);
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> JoinBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
