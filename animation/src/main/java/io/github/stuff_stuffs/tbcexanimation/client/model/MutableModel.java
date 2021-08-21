package io.github.stuff_stuffs.tbcexanimation.client.model;

import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class MutableModel implements Model {
    private final Map<String, ModelBoneInstance> bones;
    private final double scale;
    private Animation currentAnimation = null;
    private int lastTick = Integer.MIN_VALUE;
    private double lastPartialTick = 0;

    private MutableModel(Map<String, ModelBoneInstance> bones, final double scale) {
        this.scale = scale;
        this.bones = bones;
    }

    public MutableModel(final double scale) {
        this.scale = scale;
        bones = new Object2ReferenceOpenHashMap<>();
    }

    @Override
    public MutableModel copy(boolean copyState) {
        Builder builder = builder();
        for (ModelBoneInstance boneInstance : bones.values()) {
            builder.addBone(boneInstance.getBone());
        }
        MutableModel model = builder.build(scale);
        if(copyState) {
            for (Map.Entry<String, ModelBoneInstance> entry : bones.entrySet()) {
                final ModelBoneInstance bone = model.getBone(entry.getKey());
                if(bone==null) {
                    throw new RuntimeException();
                }
                bone.setRotation(entry.getValue().getRotation());
                bone.setOffset(entry.getValue().getOffset());
            }
        }
        return model;
    }

    @Override
    public boolean containsBone(final String name) {
        return bones.containsKey(name);
    }

    @Override
    public Set<String> getBones() {
        return new ObjectOpenHashSet<>(bones.keySet());
    }

    @Override
    public @Nullable ModelBoneInstance getBone(final String name) {
        return bones.get(name);
    }

    public void addBoneIfAbsent(final ModelBone bone) {
        addBoneIfAbsentInternal(bone);
    }

    private void addBoneIfAbsentInternal(final ModelBone bone) {
        final ModelBone parent = bone.getParent();
        if (parent != null && !bones.containsKey(parent.getName())) {
            addBoneIfAbsentInternal(parent);
        }
        if (!bones.containsKey(bone.getName())) {
            final ModelBoneInstance parentInstance;
            if (parent != null) {
                parentInstance = bones.get(parent.getName());
            } else {
                parentInstance = null;
            }
            bones.put(bone.getName(), new ModelBoneInstance(bone, parentInstance));
        }
    }

    public void removeBoneIfPresent(final String bone) {
        if (bones.containsKey(bone)) {
            bones.remove(bone);
            removeAllChildren(bone);
        }
    }

    private void removeAllChildren(final String bone) {
        final Iterator<ModelBoneInstance> iterator = bones.values().iterator();
        final Set<String> children = new ObjectOpenHashSet<>();
        while (iterator.hasNext()) {
            final ModelBoneInstance instance = iterator.next();
            final ModelBone parent = instance.getBone().getParent();
            if (parent != null && parent.getName().equals(bone)) {
                children.add(instance.getBone().getName());
                iterator.remove();
            }
        }
        for (final String child : children) {
            removeAllChildren(child);
        }
    }

    @Override
    public void tick(final int ticks, final double partialTick) {
        if (lastTick != Integer.MIN_VALUE) {
            if (currentAnimation != null) {
                currentAnimation.update(this, (ticks + partialTick) - (lastTick + lastPartialTick));
                if (currentAnimation.isFinished() || currentAnimation.isCancelled()) {
                    currentAnimation = null;
                }
            }
        }
        lastTick = ticks;
        lastPartialTick = partialTick;
    }

    @Override
    public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int ticks, final double partialTick) {
        tick(ticks, partialTick);
        matrices.push();
        matrices.scale((float) scale, (float) scale, (float) scale);
        for (final ModelBoneInstance boneInstance : bones.values()) {
            boneInstance.render(matrices, vertexConsumers);
        }
        matrices.pop();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, ModelBone> bones;

        private Builder() {
            bones = new Object2ReferenceOpenHashMap<>();
        }

        public Builder addBone(final ModelBone bone) {
            if (bones.put(bone.getName(), bone) != null) {
                throw new RuntimeException();
            }
            return this;
        }

        public MutableModel build(final double scale) {
            final Map<String, ModelBoneInstance> boneInstances = new Object2ReferenceOpenHashMap<>();
            boolean added = true;
            while (added) {
                added = false;
                for (final ModelBone bone : bones.values()) {
                    final ModelBone parent = bone.getParent();
                    if (!boneInstances.containsKey(bone.getName())) {
                        if (parent == null || boneInstances.containsKey(parent.getName())) {
                            boneInstances.put(bone.getName(), new ModelBoneInstance(bone, parent == null ? null : boneInstances.get(parent.getName())));
                            added = true;
                        }
                    }
                }
            }
            return new MutableModel(boneInstances, scale);
        }
    }
}