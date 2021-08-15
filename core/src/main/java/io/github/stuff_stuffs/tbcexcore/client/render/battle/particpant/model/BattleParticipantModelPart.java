package io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Iterator;

public interface BattleParticipantModelPart {
    Iterator<BattleParticipantModelFace> getFaces();

    void tick(BattleStateView battleState, BattleParticipantHandle handle);

    void render(BattleStateView battleState, BattleParticipantHandle handle, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float delta);
}
