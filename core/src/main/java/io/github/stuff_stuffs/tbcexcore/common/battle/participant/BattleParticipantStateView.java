package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.*;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponentKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface BattleParticipantStateView {
    EventKey<PreEquipmentChangeEvent.Mut, PreEquipmentChangeEvent> PRE_EQUIPMENT_CHANGE_EVENT = EventKey.get(PreEquipmentChangeEvent.Mut.class, PreEquipmentChangeEvent.class);
    EventKey<PostEquipmentChangeEvent.Mut, PostEquipmentChangeEvent> POST_EQUIPMENT_CHANGE_EVENT = EventKey.get(PostEquipmentChangeEvent.Mut.class, PostEquipmentChangeEvent.class);
    EventKey<PreDamageEvent.Mut, PreDamageEvent> PRE_DAMAGE_EVENT = EventKey.get(PreDamageEvent.Mut.class, PreDamageEvent.class);
    EventKey<PostDamageEvent.Mut, PostDamageEvent> POST_DAMAGE_EVENT = EventKey.get(PostDamageEvent.Mut.class, PostDamageEvent.class);
    EventKey<PreMoveEvent.Mut, PreMoveEvent> PRE_MOVE_EVENT = EventKey.get(PreMoveEvent.Mut.class, PreMoveEvent.class);
    EventKey<PostMoveEvent.Mut, PostMoveEvent> POST_MOVE_EVENT = EventKey.get(PostMoveEvent.Mut.class, PostMoveEvent.class);
    EventKey<DeathEvent.Mut, DeathEvent> DEATH_EVENT = EventKey.get(DeathEvent.Mut.class, DeathEvent.class);

    BattleStateView getBattleState();

    <T, V> EventHolder<T, V> getEvent(EventKey<T, V> key);

    Team getTeam();

    int getLevel();

    BattleParticipantHandle getHandle();

    @Nullable BattleParticipantItemStack getItemStack(BattleParticipantInventoryHandle handle);

    @Nullable BattleParticipantItemStack getEquipmentStack(BattleEquipmentSlot slot);

    Iterator<BattleParticipantInventoryHandle> getInventoryIterator();

    double getStat(BattleParticipantStat stat);

    double getHealth();

    BlockPos getPos();

    BattleEquipment getEquipment(BattleEquipmentSlot slot);

    BattleParticipantBounds getBounds();

    Text getName();

    double getEnergy();

    boolean hasComponent(ParticipantComponentKey<?, ?> key);

    <View extends ParticipantComponent> @Nullable View getComponent(ParticipantComponentKey<?, View> key);
}
