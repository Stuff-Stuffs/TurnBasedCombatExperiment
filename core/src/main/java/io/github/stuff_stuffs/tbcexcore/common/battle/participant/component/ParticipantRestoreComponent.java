package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventListenerHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexcore.common.network.PlayerLeaveBattleSender;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Function;

public final class ParticipantRestoreComponent implements ParticipantComponent {
    public static final Codec<ParticipantRestoreComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.optionalField("nbt", CodecUtil.NBT_CODEC).forGetter(component -> component.nbtData),
                    CodecUtil.VEC3D_CODEC.fieldOf("pos").forGetter(component -> component.pos)
            ).apply(instance, ParticipantRestoreComponent::new)
    );
    private final Optional<NbtElement> nbtData;
    private final Vec3d pos;
    private EventListenerHandle leaveHandle;
    private EventListenerHandle battleEndHandle;

    public ParticipantRestoreComponent(final Optional<NbtElement> nbtData, final Vec3d pos) {
        this.nbtData = nbtData;
        this.pos = pos;
    }

    @Override
    public void init(final BattleParticipantState state) {
        leaveHandle = state.getBattleState().getEventMut(BattleStateView.POST_PARTICIPANT_LEAVE_EVENT).registerMut((battleState, participantState) -> {
            if (participantState.getHandle().equals(state.getHandle())) {
                restore(battleState, state, PlayerLeaveBattleSender.Reason.LEFT);
            }
        });
        battleEndHandle = state.getBattleState().getEventMut(BattleStateView.BATTLE_END_EVENT).registerMut(battleState -> {
            if (battleState.getParticipant(state.getHandle()) != null) {
                restore(battleState, state, PlayerLeaveBattleSender.Reason.ENDED);
            }
        });
    }

    private void restore(final BattleState battleState, final BattleParticipantState state, PlayerLeaveBattleSender.Reason reason) {
        final Entity entity;
        final World world = battleState.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        if (nbtData.isPresent()) {
            entity = EntityType.loadEntityWithPassengers((NbtCompound) nbtData.get(), serverWorld, Function.identity());
            if (entity != null) {
                serverWorld.spawnEntityAndPassengers(entity);
                entity.setPos(pos.x, pos.y, pos.z);
            }
        } else {
            entity = serverWorld.getEntity(state.getHandle().participantId());
            if (entity != null) {
                entity.updatePosition(pos.x, pos.y, pos.z);
            }
        }
        if (entity instanceof BattleEntity battleEntity) {
            battleEntity.tbcex_onBattleEnd(state.getBattleState().getHandle());
            if (entity instanceof BattleAwareEntity awareEntity) {
                awareEntity.tbcex_setCurrentBattle(null);
                if(entity instanceof ServerPlayerEntity player) {
                    player.networkHandler.requestTeleport(pos.x, pos.y, pos.z, 0, 0);
                    PlayerLeaveBattleSender.send(player, reason);
                }
            }
        }
    }

    @Override
    public void deinitEvents() {
        leaveHandle.destroy();
        battleEndHandle.destroy();
    }

    @Override
    public ParticipantComponents.Type<?, ?> getType() {
        return ParticipantComponents.RESTORE_COMPONENT_TYPE;
    }
}
