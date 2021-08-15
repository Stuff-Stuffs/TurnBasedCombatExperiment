package io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.simple;

import io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.BattleParticipantModelFace;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.BattleParticipantModelPart;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Iterator;
import java.util.Set;

public class SimpleBattleParticipantModelPart implements BattleParticipantModelPart {
    private final Set<BattleParticipantModelFace> faces;

    public SimpleBattleParticipantModelPart(Set<BattleParticipantModelFace> faces) {
        this.faces = faces;
    }

    @Override
    public Iterator<BattleParticipantModelFace> getFaces() {
        return faces.iterator();
    }

    @Override
    public void tick(BattleStateView battleState, BattleParticipantHandle handle) {

    }

    @Override
    public void render(BattleStateView battleState, BattleParticipantHandle handle, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float delta) {
        for (BattleParticipantModelFace face : faces) {
            face.render(battleState,handle,matrices,vertexConsumers, light,delta);
        }
    }
}
