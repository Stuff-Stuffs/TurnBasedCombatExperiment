package io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface BattleParticipantModelFace {
    Vec3d getVertex(int index);

    Vec3d getCenter();

    void render(BattleStateView battleState, BattleParticipantHandle handle, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float delta);
}
