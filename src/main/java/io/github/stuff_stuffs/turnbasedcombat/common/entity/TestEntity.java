package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import java.util.Collections;

public class TestEntity extends LivingEntity implements BattleEntity {
    private static final Team TEAM = new Team("test_non-player");

    public TestEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
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
}
