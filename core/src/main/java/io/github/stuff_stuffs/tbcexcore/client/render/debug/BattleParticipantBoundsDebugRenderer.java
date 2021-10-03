package io.github.stuff_stuffs.tbcexcore.client.render.debug;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.ClientBattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Iterator;

public class BattleParticipantBoundsDebugRenderer implements DebugRenderer {
    public static final BattleParticipantBoundsDebugRenderer INSTANCE = new BattleParticipantBoundsDebugRenderer();

    private BattleParticipantBoundsDebugRenderer() {
    }

    @Override
    public void render(final WorldRenderContext context) {
        final ClientWorld world = context.world();
        final Iterator<Battle> battleIterator = ((ClientBattleWorld) ((BattleWorldSupplier) world).tbcex_getBattleWorld()).iterator();
        final VertexConsumer vertexConsumer = context.consumers().getBuffer(RenderLayer.LINES);
        final MatrixStack matrices = context.matrixStack();
        matrices.push();
        final Vec3d pos = context.camera().getPos();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        while (battleIterator.hasNext()) {
            final Battle battle = battleIterator.next();
            final BattleStateView view = battle.getState();
            final Iterator<BattleParticipantHandle> participants = view.getParticipants();
            while (participants.hasNext()) {
                final BattleParticipantStateView participant = view.getParticipant(participants.next());
                final BattleParticipantBounds bounds = participant.getBounds();
                for (final BattleParticipantBounds.Part part : bounds) {
                    WorldRenderer.drawBox(matrices, vertexConsumer, part.box, 1, 0, 0, 1);
                }
            }
        }
        matrices.pop();
    }
}
