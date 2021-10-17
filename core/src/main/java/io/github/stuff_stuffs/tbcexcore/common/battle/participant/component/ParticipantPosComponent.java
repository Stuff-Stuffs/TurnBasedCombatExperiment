package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import net.minecraft.util.math.BlockPos;

public final class ParticipantPosComponent extends AbstractParticipantComponent implements ParticipantPosComponentView {
    public static final Codec<ParticipantPosComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(component -> component.pos),
                    BattleParticipantBounds.CODEC.fieldOf("bounds").forGetter(component -> component.bounds),
                    HorizontalDirection.CODEC.fieldOf("facing").forGetter(component -> component.facing)
            ).apply(instance, ParticipantPosComponent::new)
    );
    private BlockPos pos;
    private BattleParticipantBounds bounds;
    private HorizontalDirection facing;

    public ParticipantPosComponent(final BlockPos pos, final BattleParticipantBounds bounds, final HorizontalDirection facing) {
        this.pos = pos;
        this.bounds = bounds;
        this.facing = facing;
    }

    @Override
    public HorizontalDirection getFacing() {
        return facing;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public BattleParticipantBounds getBounds() {
        return getBounds(facing);
    }

    @Override
    public BattleParticipantBounds getBounds(final HorizontalDirection facing) {
        return bounds.withRotation(facing);
    }

    public void setPos(final BlockPos pos) {
        final BlockPos newPos = state.getBattleState().getBounds().getNearest(pos);
        bounds = bounds.withCenter(newPos.getX() + 0.5, newPos.getY(), newPos.getZ() + 0.5);
        this.pos = newPos;
    }

    public void setFacing(final HorizontalDirection facing) {
        this.facing = facing;
        bounds = bounds.withRotation(facing);
    }

    @Override
    public ParticipantComponents.Type<?, ?> getType() {
        return ParticipantComponents.POS_COMPONENT_TYPE;
    }
}
