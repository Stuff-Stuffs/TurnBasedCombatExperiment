package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.SkillInfo;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.EntityInventory;
import io.github.stuff_stuffs.turnbasedcombat.common.item.BattleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements BattleEntity {
    @Unique
    private static final Team TEAM = new Team("test_player");
    @Unique
    private static final SkillInfo SKILL_INFO = new SkillInfo(20, 20, 10, 10, 10, 10, 1);

    @Shadow public abstract Text getName();

    @Shadow @Final private PlayerInventory inventory;

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
        EntityInventory inventory = new EntityInventory();
        for (int i = 0; i < PlayerInventory.MAIN_SIZE + PlayerInventory.getHotbarSize(); i++) {
            final ItemStack stack = this.inventory.getStack(i);
            if(stack.getItem() instanceof BattleItem battleItem) {
                inventory.setSlot(i, battleItem.getBattleItem(stack));
            }
        }
        return inventory;
    }
}
