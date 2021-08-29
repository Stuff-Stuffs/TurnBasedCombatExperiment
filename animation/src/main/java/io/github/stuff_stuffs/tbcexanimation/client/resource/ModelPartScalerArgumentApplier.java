package io.github.stuff_stuffs.tbcexanimation.client.resource;

import com.google.gson.*;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPartFactory;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.RenderType;
import io.github.stuff_stuffs.tbcexutil.common.GsonUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.lang.reflect.Type;

public final class ModelPartScalerArgumentApplier implements ModelPartArgumentApplier {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(OffsetAndScale.class, new OffsetAndScaleJson()).registerTypeHierarchyAdapter(Vec3d.class, new GsonUtil.Vec3dJson()).create();

    @Override
    public ModelPartFactory apply(final ModelPartFactory modelPartFactory, final String argument) {
        if (argument.isEmpty()) {
            return modelPartFactory;
        }
        final OffsetAndScale offsetAndScale = GSON.fromJson(argument, OffsetAndScale.class);
        return new ScaledModelPartFactory(offsetAndScale.scale, offsetAndScale.offset, modelPartFactory);
    }

    private static final class ScaledModelPartFactory implements ModelPartFactory {
        private final Vec3d scale;
        private final Vec3d offset;
        private final ModelPartFactory wrapped;

        private ScaledModelPartFactory(Vec3d scale, Vec3d offset, ModelPartFactory wrapped) {
            if (wrapped instanceof ScaledModelPartFactory scaledModelPart) {
                offset = offset.multiply(scaledModelPart.scale).add(scaledModelPart.offset);
                scale = scale.multiply(scaledModelPart.scale);
                wrapped = scaledModelPart.wrapped;
            }
            this.offset = offset;
            this.scale = scale;
            this.wrapped = wrapped;
        }

        @Override
        public ModelPart create(Skeleton context) {
            return new ScaledModelPart(scale, offset, wrapped.create(context));
        }

        @Override
        public ModelPartFactory remapTexture(Identifier target, Identifier replace, RenderType replacedRenderType) {
            return new ScaledModelPartFactory(scale, offset, wrapped.remapTexture(target, replace, replacedRenderType));
        }
    }

    private static final class ScaledModelPart implements ModelPart {
        private final Vec3d scale;
        private final Vec3d offset;
        private final ModelPart wrapped;

        private ScaledModelPart(Vec3d scale, Vec3d offset, ModelPart wrapped) {
            this.scale = scale;
            this.offset = offset;
            this.wrapped = wrapped;
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, Vec3d pos) {
            matrices.push();
            matrices.translate(offset.x, offset.y, offset.z);
            matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);
            wrapped.render(matrices, vertexConsumers, world, pos);
            matrices.pop();
        }

        @Override
        public void tick(double timeSinceLast) {
            wrapped.tick(timeSinceLast);
        }
    }

    private static final class OffsetAndScale {
        private final Vec3d offset;
        private final Vec3d scale;

        private OffsetAndScale(final Vec3d offset, final Vec3d scale) {
            this.offset = offset;
            this.scale = scale;
        }
    }

    private static final class OffsetAndScaleJson implements JsonDeserializer<OffsetAndScale> {
        @Override
        public OffsetAndScale deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final Vec3d offset = context.deserialize(json.getAsJsonObject().get("offset"), Vec3d.class);
            final Vec3d scale = context.deserialize(json.getAsJsonObject().get("scale"), Vec3d.class);
            return new OffsetAndScale(offset, scale);
        }
    }
}
