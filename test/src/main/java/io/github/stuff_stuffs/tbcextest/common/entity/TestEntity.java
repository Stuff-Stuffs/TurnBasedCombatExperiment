package io.github.stuff_stuffs.tbcextest.common.entity;

import io.github.stuff_stuffs.tbcexanimation.client.TBCExAnimationClient;
import io.github.stuff_stuffs.tbcexanimation.client.model.ImmutableSkeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPart;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
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
            final ImmutableSkeleton skeleton = new ImmutableSkeleton(1, TBCExAnimationClient.MODEL_MANAGER.getSkeletonData(new Identifier("test", "humanoid_skeleton")));
            final SimpleModelPart part = TBCExAnimationClient.MODEL_MANAGER.getSimpleModelPart(new Identifier("test", "simple/ilo"));
            if (part != null) {
                skeleton.getBone("left_arm").addPart("p", part);
                skeleton.getBone("right_arm").addPart("p", part);
                skeleton.getBone("left_leg").addPart("p", part);
                skeleton.getBone("right_leg").addPart("p", part);
                skeleton.getBone("spine").addPart("p", part);
            }
            return skeleton;
        } else {
            return ImmutableSkeleton.builder().build(1);
        }
    }

    public Skeleton getModel() {
        return skeleton;
    }

    @Override
    public void tick() {
        skeleton = createModel();
        if (skeleton.containsBone("left_arm")) {
            skeleton.getBone("left_arm").setRotation(new DoubleQuaternion(new Vec3d(1, 0, 0), world.getTime(), true));
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
