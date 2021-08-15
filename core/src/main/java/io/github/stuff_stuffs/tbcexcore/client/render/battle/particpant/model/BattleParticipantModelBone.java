package io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class BattleParticipantModelBone {
    private final String name;
    private final Set<Pair<Vec3d,Vec3d>> boneLines;
    private final BattleParticipantModelBone parent;
    private final Map<String, BattleParticipantModelPart> parts;
    private Matrix4f matrix;

    public BattleParticipantModelBone(final String name, Set<Pair<Vec3d, Vec3d>> boneLines, final BattleParticipantModelBone parent) {
        this.name = name;
        this.boneLines = boneLines;
        this.parent = parent;
        parts = new Reference2ObjectOpenHashMap<>();
        matrix = new Matrix4f();
        matrix.loadIdentity();
    }

    public void addPart(final String name, final BattleParticipantModelPart part) {
        if (parts.put(name, part) != null) {
            throw new RuntimeException();
        }
    }

    public void tick(final BattleStateView battleState, final BattleParticipantHandle handle) {
        for (final BattleParticipantModelPart part : parts.values()) {
            part.tick(battleState, handle);
        }
    }

    public Iterator<BattleParticipantModelPart> getParts() {
        return parts.values().iterator();
    }

    public void removePart(final String name) {
        parts.remove(name);
    }

    public @Nullable BattleParticipantModelPart getPart(final String name) {
        return parts.get(name);
    }

    public @Nullable BattleParticipantModelBone getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public Matrix4f getTransform() {
        if (parent != null) {
            final Matrix4f transform = parent.getTransform().copy();
            transform.multiply(matrix);
            return transform;
        } else {
            return matrix.copy();
        }
    }

    public Matrix4f getMatrix() {
        return matrix;
    }

    public void setMatrix(final Matrix4f matrix) {
        this.matrix = matrix;
    }

    public void renderBoneLines(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        final VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        Matrix4f model = matrices.peek().getModel();
        for (Pair<Vec3d, Vec3d> boneLine : boneLines) {
            consumer.vertex(model, (float)boneLine.getLeft().x, (float)boneLine.getLeft().y, (float) boneLine.getLeft().z).next();
            consumer.vertex(model, (float)boneLine.getRight().x, (float)boneLine.getRight().y, (float) boneLine.getRight().z).next();
        }
    }
}