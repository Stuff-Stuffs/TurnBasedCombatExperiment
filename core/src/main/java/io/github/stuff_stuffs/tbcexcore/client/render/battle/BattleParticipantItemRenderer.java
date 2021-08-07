package io.github.stuff_stuffs.tbcexcore.client.render.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface BattleParticipantItemRenderer {
    void render(BattleParticipantItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta);
}
