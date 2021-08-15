package io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.simple;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.client.render.SpriteLike;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.particpant.model.BattleParticipantModelFace;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.util.RenderUtil;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Map;

public class SimpleTexturedBattleParticipantModelFace implements BattleParticipantModelFace {
    private final Vec3d[] vertices;
    private final Vec3d[] normals;
    private final int[] colours;
    private final Vec3d center;
    private final SpriteLike sprite;
    private final RenderLayer layer;

    public SimpleTexturedBattleParticipantModelFace(final Vec3d[] vertices, final int[] colours, final Vec3d center, final SpriteLike sprite, final RenderLayer layer) {
        this.colours = colours;
        this.sprite = sprite;
        this.layer = layer;
        if (vertices.length != 4) {
            throw new RuntimeException();
        }
        this.vertices = vertices;
        this.center = center;
        normals = new Vec3d[4];
        normals[0] = (vertices[1].subtract(vertices[0])).crossProduct(vertices[3].subtract(vertices[0]));
        normals[1] = (vertices[0].subtract(vertices[1])).crossProduct(vertices[2].subtract(vertices[1]));
        normals[2] = (vertices[1].subtract(vertices[2])).crossProduct(vertices[3].subtract(vertices[2]));
        normals[3] = (vertices[2].subtract(vertices[3])).crossProduct(vertices[0].subtract(vertices[3]));
    }

    @Override
    public Vec3d getVertex(final int index) {
        return vertices[index];
    }

    @Override
    public Vec3d getCenter() {
        return center;
    }

    @Override
    public void render(final BattleStateView battleState, final BattleParticipantHandle handle, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final float delta) {
        final Matrix4f model = matrices.peek().getModel();
        final Matrix3f normal = matrices.peek().getNormal();
        final VertexConsumer consumer = vertexConsumers.getBuffer(layer);
        RenderUtil.colour(consumer.vertex(model, (float) vertices[0].x, (float) vertices[0].y, (float) vertices[0].z), colours[0]).texture(sprite.getMinU(), sprite.getMinV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, (float) normals[0].x, (float) normals[0].y, (float) normals[0].z).next();
        RenderUtil.colour(consumer.vertex(model, (float) vertices[1].x, (float) vertices[1].y, (float) vertices[1].z), colours[1]).texture(sprite.getMinU(), sprite.getMaxV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, (float) normals[1].x, (float) normals[1].y, (float) normals[1].z).next();
        RenderUtil.colour(consumer.vertex(model, (float) vertices[2].x, (float) vertices[2].y, (float) vertices[2].z), colours[2]).texture(sprite.getMaxU(), sprite.getMaxV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, (float) normals[2].x, (float) normals[2].y, (float) normals[2].z).next();
        RenderUtil.colour(consumer.vertex(model, (float) vertices[3].x, (float) vertices[3].y, (float) vertices[3].z), colours[3]).texture(sprite.getMaxU(), sprite.getMinV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, (float) normals[3].x, (float) normals[3].y, (float) normals[3].z).next();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final int[] colours = new int[4];
        private Vec3d center;

        private Builder() {
            Arrays.fill(colours, -1);
        }

        public Builder setColour(final int colour, final int index) {
            colours[index] = colour;
            return this;
        }

        public Builder setCenter(final Vec3d center) {
            this.center = center;
            return this;
        }

        public SimpleTexturedBattleParticipantModelFace build(final Vec3d minMin, final Vec3d minMax, final Vec3d maxMax, final Vec3d maxMin, final RenderLayer layer, final SpriteLike sprite) {
            return build(new Vec3d[]{minMin, minMax, maxMax, maxMin}, layer, sprite);
        }

        public SimpleTexturedBattleParticipantModelFace build(final Vec3d[] vertices, final RenderLayer layer, final SpriteLike sprite) {
            if (center == null) {
                vertices[0].add(vertices[1].add(vertices[2].add(vertices[3]))).multiply(1 / 4.0);
            }
            return new SimpleTexturedBattleParticipantModelFace(vertices, Arrays.copyOf(colours, 4), center, sprite, layer);
        }
    }

    public static SimpleTexturedBattleParticipantModelFace[] buildCuboid(final Map<Direction, Pair<RenderLayer, SpriteLike>> spriteMap, final Vec3d min, final Vec3d max) {
        final SimpleTexturedBattleParticipantModelFace[] faces = new SimpleTexturedBattleParticipantModelFace[spriteMap.size()];
        int index = 0;
        if (spriteMap.containsKey(Direction.UP)) {
            final Builder builder = builder();
            final Pair<RenderLayer, SpriteLike> pair = spriteMap.get(Direction.UP);
            faces[index] = builder.build(new Vec3d(min.x, max.y, min.z), new Vec3d(min.x, max.y, max.z), max, new Vec3d(max.x, max.y, min.z), pair.getFirst(), pair.getSecond());
            index++;
        }

        if (spriteMap.containsKey(Direction.DOWN)) {
            final Builder builder = builder();
            final Pair<RenderLayer, SpriteLike> pair = spriteMap.get(Direction.DOWN);
            faces[index] = builder.build(new Vec3d(max.x, min.y, min.z), min, new Vec3d(min.x, min.y, max.z), new Vec3d(min.x, min.y, min.z), pair.getFirst(), pair.getSecond());
            index++;
        }

        return faces;
    }
}
