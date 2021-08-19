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

public final class Model {
    private final Map<String, ModelBoneInstance> bones;
    private Animation currentAnimation = null;
    private int lastTick = Integer.MIN_VALUE;
    private double lastPartialTick = 0;

    public Model() {
        bones = new Object2ReferenceOpenHashMap<>();
    }

    public boolean containsBone(String name) {
        return bones.containsKey(name);
    }

    public Set<String> getBones() {
        return new ObjectOpenHashSet<>(bones.keySet());
    }

    public @Nullable ModelBoneInstance getBone(String name) {
        return bones.get(name);
    }

    public void addBoneIfAbsent(ModelBone bone) {
        addBoneIfAbsentInternal(bone);
    }

    private void addBoneIfAbsentInternal(ModelBone bone) {
        final ModelBone parent = bone.getParent();
        if(parent!=null && !bones.containsKey(parent.getName())) {
            addBoneIfAbsentInternal(parent);
        }
        if(!bones.containsKey(bone.getName())) {
            ModelBoneInstance parentInstance;
            if(parent!=null) {
                parentInstance = bones.get(parent.getName());
            } else {
                parentInstance = null;
            }
            bones.put(bone.getName(), new ModelBoneInstance(bone, parentInstance));
        }
    }

    public void removeBoneIfPresent(String bone) {
        if(bones.containsKey(bone)) {
            bones.remove(bone);
            removeAllChildren(bone);
        }
    }

    private void removeAllChildren(String bone) {
        final Iterator<ModelBoneInstance> iterator = bones.values().iterator();
        Set<String> children = new ObjectOpenHashSet<>();
        while (iterator.hasNext()) {
            final ModelBoneInstance instance = iterator.next();
            final ModelBone parent = instance.getBone().getParent();
            if(parent!=null&&parent.getName().equals(bone)) {
                children.add(instance.getBone().getName());
                iterator.remove();
            }
        }
        for (String child : children) {
            removeAllChildren(child);
        }
    }

    public void tick(int ticks, double partialTick) {
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

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int ticks, double partialTick) {
        tick(ticks, partialTick);
        for (ModelBoneInstance boneInstance : bones.values()) {
            boneInstance.render(matrices, vertexConsumers);
        }
    }
}
