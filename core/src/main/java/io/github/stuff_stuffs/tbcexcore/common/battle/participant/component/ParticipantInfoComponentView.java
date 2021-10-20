package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface ParticipantInfoComponentView extends ParticipantComponent {
    @Nullable BattleParticipantItemStack getItemStack(BattleParticipantInventoryHandle handle);

    @Nullable BattleEquipment getEquipment(BattleEquipmentSlot slot);

    @Nullable BattleParticipantItemStack getEquipmentStack(BattleEquipmentSlot slot);

    Iterator<BattleParticipantInventoryHandle> getInventoryIterator();

    double getStat(BattleParticipantStat stat);

    double getHealth();

    Text getName();

    double getEnergy();

    double getRawStat(BattleParticipantStat stat);
}
