package io.github.stuff_stuffs.tbcexcore.client.render.debug;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.ClientBattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexcore.mixin.api.ClientBattleWorldSupplier;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderer;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Iterator;

public final class BattleParticipantBoundsDebugRenderer implements DebugRenderer {
    public static final BattleParticipantBoundsDebugRenderer INSTANCE = new BattleParticipantBoundsDebugRenderer();

    private BattleParticipantBoundsDebugRenderer() {
    }

    @Override
    public void render(final WorldRenderContext context) {
        final ClientWorld world = context.world();
        final VertexConsumer vertexConsumer = context.consumers().getBuffer(RenderLayer.LINES);
        final MatrixStack matrices = context.matrixStack();
        matrices.push();
        final Vec3d pos = context.camera().getPos();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        for (final Battle battle : ((ClientBattleWorldSupplier) world).tbcex_getBattleWorld()) {
            final BattleStateView view = battle.getState();
            final Iterator<BattleParticipantHandle> participants = view.getParticipants();
            while (participants.hasNext()) {
                final BattleParticipantStateView participant = view.getParticipant(participants.next());
                final BattleParticipantBounds bounds = participant.getBounds();
                final Colour colour = participant.getTeam().getColour();
                for (final BattleParticipantBounds.Part part : bounds) {
                    RenderUtil.drawBox(matrices, vertexConsumer, part.box, colour);
                }
            }
        }
        matrices.pop();
    }
}
