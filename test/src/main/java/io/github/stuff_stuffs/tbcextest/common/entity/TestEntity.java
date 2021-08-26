package io.github.stuff_stuffs.tbcextest.common.entity;

import io.github.stuff_stuffs.tbcexanimation.client.TBCExAnimationClient;
import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.KeyframeAnimationData;
import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.SimpleKeyframeAnimation;
import io.github.stuff_stuffs.tbcexanimation.client.model.ImmutableSkeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.SkeletonData;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPart;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class TestEntity extends LivingEntity implements BattleEntity {
    private static final Team TEAM = new Team("test_non-player");
    private Skeleton skeleton;

    public TestEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
        skeleton = createModel();
    }

    private static Skeleton createModel() {
        if (TBCExAnimationClient.MODEL_MANAGER.isInitialized()) {
            final SkeletonData skeletonData = TBCExAnimationClient.MODEL_MANAGER.getSkeletonData(new Identifier("test", "humanoid_skeleton"));
            if (skeletonData != null) {
                final ImmutableSkeleton skeleton = new ImmutableSkeleton(1, skeletonData);
                final SimpleModelPart left_arm = TBCExAnimationClient.MODEL_MANAGER.getSimpleModelPart(new Identifier("test", "simple/humanoid/left_arm"));
                final SimpleModelPart right_arm = TBCExAnimationClient.MODEL_MANAGER.getSimpleModelPart(new Identifier("test", "simple/humanoid/right_arm"));
                final SimpleModelPart left_leg = TBCExAnimationClient.MODEL_MANAGER.getSimpleModelPart(new Identifier("test", "simple/humanoid/left_leg"));
                final SimpleModelPart right_leg = TBCExAnimationClient.MODEL_MANAGER.getSimpleModelPart(new Identifier("test", "simple/humanoid/right_leg"));
                final SimpleModelPart body = TBCExAnimationClient.MODEL_MANAGER.getSimpleModelPart(new Identifier("test", "simple/humanoid/body"));
                if (left_arm != null && right_arm != null) {
                    skeleton.getBone("left_arm").addPart("main", left_arm);
                    skeleton.getBone("right_arm").addPart("main", right_arm);
                }
                if (left_leg != null && right_leg != null) {
                    skeleton.getBone("left_leg").addPart("main", left_leg);
                    skeleton.getBone("right_leg").addPart("main", right_leg);
                }
                if (body != null) {
                    skeleton.getBone("spine").addPart("main", body);
                }
                return skeleton;
            }
        }
        return ImmutableSkeleton.builder().build(1);
    }

    public Skeleton getModel() {
        return skeleton;
    }

    @Override
    public void tick() {
        final Animation animation = skeleton.getCurrentAnimation();
        if (animation == null || animation.isFinished()) {
            skeleton = createModel();
            final KeyframeAnimationData animationData = TBCExAnimationClient.MODEL_MANAGER.getAnimationData(new Identifier("test", "simple_animation/simple.s"));
            if (animationData != null) {
                skeleton.setAnimation(new SimpleKeyframeAnimation(animationData), false);
            }
        }
        super.tick();
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptySet();
    }

    @Override
    public ItemStack getEquippedStack(final EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(final EquipmentSlot slot, final ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public Team getTeam() {
        return TEAM;
    }

    @Override
    public Iterable<ItemStack> tbcex_getInventory() {
        return Collections.emptySet();
    }

    @Override
    public double tbcex_getMaxHealth() {
        return 20;
    }

    @Override
    public double tbcex_getCurrentHealth() {
        return 20;
    }

    @Override
    public double tbcex_getStrength() {
        return 1;
    }

    @Override
    public double tbcex_getIntelligence() {
        return 1;
    }

    @Override
    public double tbcex_getVitality() {
        return 1;
    }

    @Override
    public double tbcex_getDexterity() {
        return 1;
    }

    @Override
    public @Nullable ItemStack tbcex_getEquipped(final BattleEquipmentSlot slot) {
        return null;
    }
}
