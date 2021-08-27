package io.github.stuff_stuffs.tbcexanimation.client.model.bundle;

import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBoneInstance;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;

public final class ModelPartBundle {
    private final Map<String, Map<String, WrappedModelPart>> modelMap;

    private ModelPartBundle(final Map<String, Map<String, WrappedModelPart>> modelMap) {
        this.modelMap = modelMap;
    }

    public void apply(final Skeleton skeleton, final boolean partial, final boolean overwriteExisting) {
        if (!partial) {
            for (final String s : modelMap.keySet()) {
                if (!skeleton.containsBone(s)) {
                    return;
                }
                final Map<String, WrappedModelPart> partMap = modelMap.get(s);
                for (final String part : partMap.keySet()) {
                    if (skeleton.containsBone(part)) {
                        return;
                    }
                }
            }
        }
        for (final Map.Entry<String, Map<String, WrappedModelPart>> modelEntry : modelMap.entrySet()) {
            final ModelBoneInstance instance = skeleton.getBone(modelEntry.getKey());
            if (instance != null) {
                for (final Map.Entry<String, WrappedModelPart> partEntry : modelEntry.getValue().entrySet()) {
                    if (overwriteExisting || !instance.containsPart(partEntry.getKey())) {
                        instance.addPart(partEntry.getKey(), partEntry.getValue());
                    }
                }
            }
        }
    }

    public void unApply(final Skeleton skeleton) {
        for (final Map.Entry<String, Map<String, WrappedModelPart>> modelEntry : modelMap.entrySet()) {
            final ModelBoneInstance instance = skeleton.getBone(modelEntry.getKey());
            if (instance != null) {
                for (final Map.Entry<String, WrappedModelPart> partEntry : modelEntry.getValue().entrySet()) {
                    if (instance.containsPart(partEntry.getKey(), partEntry.getValue())) {
                        instance.removePart(partEntry.getKey());
                    }
                }
            }
        }
    }

    public static final class WrappedModelPart implements ModelPart {
        public final ModelPart wrapped;

        private WrappedModelPart(final ModelPart wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final World world, final Vec3d pos) {
            wrapped.render(matrices, vertexConsumers, world, pos);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Map<String, WrappedModelPart>> map = new Object2ReferenceOpenHashMap<>();
        private boolean built = false;

        private Builder() {
        }

        public Builder addPart(final String bone, final String partName, final ModelPart part) {
            if (built) {
                throw new RuntimeException();
            }
            map.computeIfAbsent(bone, s -> new Object2ReferenceOpenHashMap<>()).put(partName, new WrappedModelPart(part));
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
