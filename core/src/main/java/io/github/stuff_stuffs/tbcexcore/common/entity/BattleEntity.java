package io.github.stuff_stuffs.tbcexcore.common.entity;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BattleEntity {
    Team getTeam();

    Iterable<ItemStack> tbcex_getInventory();

    double tbcex_getMaxHealth();

    double tbcex_getCurrentHealth();

    double tbcex_getStrength();

    double tbcex_getIntelligence();

    double tbcex_getVitality();

    double tbcex_getDexterity();

    double tbcex_getPerception();

    int tbcex_getLevel();

    BattleParticipantBounds getBounds();

    @Nullable ItemStack tbcex_getEquipped(BattleEquipmentSlot slot);

    boolean tbcex_shouldSaveToTag();

    default void onBattleJoin(final BattleHandle handle) {
        ((Entity) this).remove(Entity.RemovalReason.DISCARDED);
    }

    default void onBattleEnd(final BattleHandle handle) {
    }
}
