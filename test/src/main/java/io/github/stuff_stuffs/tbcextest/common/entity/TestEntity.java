package io.github.stuff_stuffs.tbcextest.common.entity;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBone;
import io.github.stuff_stuffs.tbcexanimation.client.model.MutableSkeleton;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestEntity extends LivingEntity implements BattleEntity {
    private static final Team TEAM = new Team("test_non-player");
    private Skeleton skeleton;

    public TestEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
        skeleton = createModel();
    }

    private static Skeleton createModel() {
        final List<Pair<Vec3d, Vec3d>> spineLine = new ArrayList<>();
        spineLine.add(Pair.of(new Vec3d(0, 0, 0), new Vec3d(0, 12 / 16.0, 0)));
        spineLine.add(Pair.of(new Vec3d(-0.15, 0, 0), new Vec3d(0.15, 0, 0)));
        spineLine.add(Pair.of(new Vec3d(-0.275, 12 / 16.0, 0), new Vec3d(0.275, 12 / 16.0, 0)));
        final ModelBone torso = new ModelBone("spine", new Vec3d(0, 12 / 16.0, 0), Vec3d.ZERO, new DoubleQuaternion(), spineLine, null);

        final List<Pair<Vec3d, Vec3d>> leftLegLine = new ArrayList<>();
        leftLegLine.add(Pair.of(new Vec3d(0, 0, 0), new Vec3d(0, -12 / 16.0, 0)));
        final ModelBone leftLeg = new ModelBone("left_leg", new Vec3d(-0.15, 0, 0), Vec3d.ZERO, new DoubleQuaternion(), leftLegLine, torso);

        final List<Pair<Vec3d, Vec3d>> rightLegLine = new ArrayList<>();
        rightLegLine.add(Pair.of(new Vec3d(0, 0, 0), new Vec3d(0, -12 / 16.0, 0)));
        final ModelBone rightLeg = new ModelBone("right_leg", new Vec3d(0.15, 0, 0), Vec3d.ZERO, new DoubleQuaternion(), rightLegLine, torso);

        final List<Pair<Vec3d, Vec3d>> leftArmLine = new ArrayList<>();
        leftArmLine.add(Pair.of(new Vec3d(0, 0, 0), new Vec3d(0, -12 / 16.0, 0)));
        final ModelBone leftArm = new ModelBone("left_arm", new Vec3d(-0.275, 12 / 16.0, 0), Vec3d.ZERO, new DoubleQuaternion(), leftArmLine, torso);

        final List<Pair<Vec3d, Vec3d>> rightArmLine = new ArrayList<>();
        rightArmLine.add(Pair.of(new Vec3d(0, 0, 0), new Vec3d(0, -12 / 16.0, 0)));
        final ModelBone rightArm = new ModelBone("right_arm", new Vec3d(0.275, 12 / 16.0, 0), Vec3d.ZERO, new DoubleQuaternion(), rightArmLine, torso);

        final MutableSkeleton model = new MutableSkeleton(1);
        model.addBoneIfAbsent(torso);
        model.addBoneIfAbsent(leftLeg);
        model.addBoneIfAbsent(rightLeg);
        model.addBoneIfAbsent(leftArm);
        model.addBoneIfAbsent(rightArm);
        return model;
    }

    public Skeleton getModel() {
        return skeleton;
    }

    @Override
    public void tick() {
        skeleton = createModel();
        skeleton.getBone("left_arm").setRotation(new DoubleQuaternion(new Vec3d(1, 0, 0), world.getTime(), true));
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
