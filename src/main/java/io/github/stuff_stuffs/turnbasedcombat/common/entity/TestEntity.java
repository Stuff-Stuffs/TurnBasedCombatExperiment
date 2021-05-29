package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.SkillInfo;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.EntityInventory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import java.util.Collections;

public class TestEntity extends LivingEntity implements BattleEntity {
    private static final Team TEAM = new Team("test_non-player");
    private static final SkillInfo SKILL_INFO = new SkillInfo(20, 20, 1, 1, 1, 1, 0);

    public TestEntity(EntityType<? extends LivingEntity> entityType, final World world) {
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
    public SkillInfo getSkillInfo() {
        return SKILL_INFO;
    }

    @Override
    public Team getTeam() {
        return TEAM;
    }

    @Override
    public Text getBattleName() {
        return getName();
    }

    @Override
    public EntityInventory getBattleInventory() {
        return new EntityInventory();
    }
}
