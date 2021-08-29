package io.github.stuff_stuffs.tbcexanimation.client.model.bundle;

import io.github.stuff_stuffs.tbcexanimation.client.TBCExAnimationClient;
import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBoneInstance;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPartFactory;
import io.github.stuff_stuffs.tbcexanimation.client.resource.ModelPartIdentifier;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;

public final class ModelPartBundle {
    private final Map<String, Map<String, ModelPartIdentifier>> modelMap;

    private ModelPartBundle(final Map<String, Map<String, ModelPartIdentifier>> modelMap) {
        this.modelMap = modelMap;
    }

    public void apply(final Skeleton skeleton, final boolean partial, final boolean overwriteExisting) {
        if (!partial) {
            for (final String s : modelMap.keySet()) {
                if (!skeleton.containsBone(s)) {
                    return;
                }
                final Map<String, ModelPartIdentifier> partMap = modelMap.get(s);
                for (final String part : partMap.keySet()) {
                    if (skeleton.containsBone(part)) {
                        return;
                    }
                }
            }
        }
        for (final Map.Entry<String, Map<String, ModelPartIdentifier>> modelEntry : modelMap.entrySet()) {
            final ModelBoneInstance instance = skeleton.getBone(modelEntry.getKey());
            if (instance != null) {
                for (final Map.Entry<String, ModelPartIdentifier> partEntry : modelEntry.getValue().entrySet()) {
                    if (overwriteExisting || !instance.containsPart(partEntry.getKey())) {
                        final ModelPartFactory modelPartFactory = TBCExAnimationClient.MODEL_MANAGER.getModelFactoryPart(partEntry.getValue());
                        if (modelPartFactory != null) {
                            final ModelPart modelPart = modelPartFactory.create(skeleton);
                            instance.addPart(partEntry.getKey(), new WrappedModelPart(modelPart, this));
                        } else {
                            LoggerUtil.LOGGER.error("Missing model part: {}", partEntry.getValue());
                        }
                    }
                }
            }
        }
    }

    public void unApply(final Skeleton skeleton) {
        for (final Map.Entry<String, Map<String, ModelPartIdentifier>> modelEntry : modelMap.entrySet()) {
            final ModelBoneInstance instance = skeleton.getBone(modelEntry.getKey());
            if (instance != null) {
                for (final Map.Entry<String, ModelPartIdentifier> partEntry : modelEntry.getValue().entrySet()) {
                    final ModelPartFactory modelPartFactory = TBCExAnimationClient.MODEL_MANAGER.getModelFactoryPart(partEntry.getValue());
                    if (modelPartFactory != null && instance.containsPart(partEntry.getKey(), modelPartFactory.create(skeleton))) {
                        instance.removePart(partEntry.getKey());
                    } else {
                        LoggerUtil.LOGGER.error("Missing model part: {}", partEntry.getValue());
                    }
                }
            }
        }
    }

    public static final class WrappedModelPart implements ModelPart {
        private final ModelPart wrapped;
        private final ModelPartBundle parent;

        private WrappedModelPart(final ModelPart wrapped, final ModelPartBundle parent) {
            this.wrapped = wrapped;
            this.parent = parent;
        }

        @Override
        public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final World world, final Vec3d pos) {
            wrapped.render(matrices, vertexConsumers, world, pos);
        }

        @Override
        public void tick(final double timeSinceLast) {
            wrapped.tick(timeSinceLast);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof final WrappedModelPart that)) {
                return false;
            }

            return parent.equals(that.parent);
        }

        @Override
        public int hashCode() {
            return parent.hashCode();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Map<String, ModelPartIdentifier>> map = new Object2ReferenceOpenHashMap<>();
        private boolean built = false;

        private Builder() {
        }

        public Builder addPart(final String bone, final String partName, final ModelPartIdentifier part) {
            if (built) {
                throw new RuntimeException();
            }
            map.computeIfAbsent(bone, s -> new Object2ReferenceOpenHashMap<>()).put(partName, part);
            return this;
        }

        public ModelPartBundle build() {
            if (built) {
                throw new RuntimeException();
            }
            built = true;
            return new ModelPartBundle(map);
        }
    }
}
