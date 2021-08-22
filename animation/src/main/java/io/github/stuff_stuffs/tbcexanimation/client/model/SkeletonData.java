package io.github.stuff_stuffs.tbcexanimation.client.model;

import java.util.Set;

public final class SkeletonData {
    private final Set<ModelBone> bones;

    public SkeletonData(final Set<ModelBone> bones) {
        this.bones = bones;
        for (final ModelBone bone : bones) {
            checkBone(bone);
        }
    }

    private void checkBone(final ModelBone bone) {
        if (bone.getParent() != null) {
            if (bones.contains(bone.getParent())) {
                checkBone(bone.getParent());
            } else {
                throw new RuntimeException();
            }
        }
    }

    public Set<ModelBone> getBones() {
        return bones;
    }
}
