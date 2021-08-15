package io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.simple;

import io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.BattleParticipantModel;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.BattleParticipantModelBone;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.BattleParticipantModelPart;
import io.github.stuff_stuffs.tbcexcore.client.util.ClientUtil;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class SimpleBattleParticipantModel implements BattleParticipantModel {
    private final Map<String, BattleParticipantModelBone> bones;

    private SimpleBattleParticipantModel(final Map<String, BattleParticipantModelBone> bones) {
        this.bones = bones;
    }

    @Override
    public Iterator<BattleParticipantModelBone> getBones() {
        return bones.values().iterator();
    }

    @Override
    public @Nullable BattleParticipantModelBone getBone(final String name) {
        return bones.get(name);
    }

    @Override
    public void tick(final BattleStateView battleState, final BattleParticipantHandle handle) {
        for (final BattleParticipantModelBone bone : bones.values()) {
            bone.tick(battleState, handle);
        }
    }

    @Override
    public void render(final BattleStateView battleState, final BattleParticipantHandle handle, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final float delta, final boolean renderBones) {
        for (final BattleParticipantModelBone bone : bones.values()) {
            matrices.push();
            ClientUtil.multiply(bone.getTransform(), matrices);
            if (renderBones) {
                bone.renderBoneLines(matrices, vertexConsumers);
            } else {
                final Iterator<BattleParticipantModelPart> parts = bone.getParts();
                while (parts.hasNext()) {
                    final BattleParticipantModelPart part = parts.next();
                    part.render(battleState, handle, matrices, vertexConsumers, light, delta);
                }
            }
            matrices.pop();
        }
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, BattleParticipantModelBone> bones;
        private boolean built = false;
        private Builder() {
            bones = new Object2ObjectOpenHashMap<>();
        }

        public Builder addBone(String name, BattleParticipantModelBone bone) {
            if(bones.put(name, bone)!=null) {
                throw new RuntimeException();
            }
            return this;
        }

        public @Nullable BattleParticipantModelBone getBone(String name) {
            return bones.get(name);
        }

        public SimpleBattleParticipantModel build() {
            if(built) {
                throw new RuntimeException();
            }
            SimpleBattleParticipantModel model = new SimpleBattleParticipantModel(new Object2ObjectOpenHashMap<>(bones));
            built = true;
            return model;
        }
    }
}
