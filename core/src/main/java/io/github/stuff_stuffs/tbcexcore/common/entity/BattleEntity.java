package io.github.stuff_stuffs.tbcexcore.common.entity;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BattleEntity {
    Team tbcex_getTeam();

    Iterable<ItemStack> tbcex_getInventory();

    double tbcex_getStat(BattleParticipantStat stat);

    double tbcex_getCurrentHealth();

    int tbcex_getLevel();

    BattleParticipantBounds tbcex_getBounds();

    @Nullable ItemStack tbcex_getEquipped(BattleEquipmentSlot slot);

    boolean tbcex_shouldSaveToTag();

    default void tbcex_onBattleJoin(final BattleHandle handle) {
        ((Entity) this).remove(Entity.RemovalReason.DISCARDED);
    }

    default void tbcex_onBattleEnd(final BattleHandle handle) {
    }
}
