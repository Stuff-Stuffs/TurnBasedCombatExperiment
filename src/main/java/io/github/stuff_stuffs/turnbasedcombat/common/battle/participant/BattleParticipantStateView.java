package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PostDamageEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PostEquipmentChangeEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PreDamageEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PreEquipmentChangeEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats.BattleParticipantStat;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface BattleParticipantStateView {
    EventKey<PreEquipmentChangeEvent.Mut, PreEquipmentChangeEvent> PRE_EQUIPMENT_CHANGE_EVENT = new EventKey<>(PreEquipmentChangeEvent.Mut.class, PreEquipmentChangeEvent.class);
    EventKey<PostEquipmentChangeEvent.Mut, PostEquipmentChangeEvent> POST_EQUIPMENT_CHANGE_EVENT = new EventKey<>(PostEquipmentChangeEvent.Mut.class, PostEquipmentChangeEvent.class);
    EventKey<PreDamageEvent.Mut, PreDamageEvent> PRE_DAMAGE_EVENT = new EventKey<>(PreDamageEvent.Mut.class, PreDamageEvent.class);
    EventKey<PostDamageEvent.Mut, PostDamageEvent> POST_DAMAGE_EVENT = new EventKey<>(PostDamageEvent.Mut.class, PostDamageEvent.class);

    BattleStateView getBattleState();

    <T, V> EventHolder<T, V> getEvent(EventKey<T, V> key);

    Team getTeam();

    BattleParticipantHandle getHandle();

    @Nullable BattleParticipantItemStack getItemStack(BattleParticipantInventoryHandle handle);

    Iterator<Pair<BattleParticipantItemStack, BattleParticipantInventoryHandle>> getInventoryIterator();

    double getStat(BattleParticipantStat stat);

    double getHealth();

    BlockPos getPos();
}
