package io.github.stuff_stuffs.tbcextest.common.entity;

import io.github.stuff_stuffs.tbcexanimation.client.TBCExAnimationClient;
import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.client.model.ImmutableSkeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.SkeletonData;
import io.github.stuff_stuffs.tbcexanimation.client.model.bundle.ModelPartBundle;
import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class TestEntity extends LivingEntity implements BattleEntity {
    private static final Team TEAM = new Team("test_non-player");
    private static final BattleParticipantBounds BOUNDS = BattleParticipantBounds.builder().add(TurnBasedCombatExperiment.createId("body"), new Box(-0.5, 0, -0.5, 0.5, 1.5, 0.5)).add(TurnBasedCombatExperiment.createId("head"), new Box(-0.25, 1.5, -0.25, 0.25, 2, 0.25)).build();
    private Skeleton skeleton;
    private boolean slim = false;

    public TestEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
        skeleton = createModel();
    }

    private Skeleton createModel() {
        if (TBCExAnimationClient.MODEL_MANAGER.isInitialized()) {
            final SkeletonData skeletonData = TBCExAnimationClient.MODEL_MANAGER.getSkeletonData(new Identifier("test", "humanoid_skeleton"));
            if (skeletonData != null) {
                final ImmutableSkeleton skeleton = new ImmutableSkeleton(1, skeletonData);
                final ModelPartBundle bundle;
                if (slim) {
                    bundle = TBCExAnimationClient.MODEL_MANAGER.getModelPartBundle(new Identifier("test", "basic_humanoid_slim"));
                } else {
                    bundle = TBCExAnimationClient.MODEL_MANAGER.getModelPartBundle(new Identifier("test", "basic_humanoid"));
                }
                slim = !slim;
                if (bundle != null) {
                    bundle.apply(skeleton, true, true);
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
        Animation animation = skeleton.getCurrentAnimation();
        if (animation == null || animation.isFinished()) {
            skeleton = createModel();
            animation = TBCExAnimationClient.MODEL_MANAGER.getAnimation(new Identifier("test", "simple_animation_reset"));
            if (animation != null) {
                skeleton.setAnimation(animation, false);
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
    public BattleParticipantBounds getBounds() {
        return BOUNDS;
    }

    @Override
    public @Nullable ItemStack tbcex_getEquipped(final BattleEquipmentSlot slot) {
        return null;
    }
}
