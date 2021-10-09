package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PostDamageEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PostEquipmentChangeEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PreDamageEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PreEquipmentChangeEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
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

    @Nullable BattleParticipantItemStack getEquipmentStack(BattleEquipmentSlot slot);

    Iterator<BattleParticipantInventoryHandle> getInventoryIterator();

    double getStat(BattleParticipantStat stat);

    double getHealth();

    BlockPos getPos();

    BattleEquipment getEquipment(BattleEquipmentSlot slot);

    HorizontalDirection getFacing();

    BattleParticipantBounds getBounds();

    BattleParticipantBounds getBounds(HorizontalDirection facing);
}
