package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventMap;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PostEquipmentChangeEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PreEquipmentChangeEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipmentState;

public final class BattleParticipantState implements BattleParticipantStateView {
    private final EventMap eventMap;
    private final BattleParticipantHandle handle;
    private final Team team;
    private final BattleEquipmentState equipmentState;
    private BattleState battleState;


    public BattleParticipantState(final BattleParticipantHandle handle, final Team team) {
        this.handle = handle;
        this.team = team;
        eventMap = new EventMap();
        registerEvents();
        equipmentState = new BattleEquipmentState();
    }

    private void registerEvents() {
        eventMap.register(PRE_EQUIPMENT_CHANGE_EVENT, new MutableEventHolder.BasicEventHolder<>(PRE_EQUIPMENT_CHANGE_EVENT, view -> (state, slot, oldEquipment, newEquipment) -> {
            view.onEquipmentChange(state, slot, oldEquipment, newEquipment);
            return false;
        }, events -> (state, slot, oldEquipment, newEquipment) -> {
            boolean canceled = false;
            for (final PreEquipmentChangeEvent.Mut event : events) {
                canceled |= event.onEquipmentChange(state, slot, oldEquipment, newEquipment);
            }
            return canceled;
        }));
        eventMap.register(POST_EQUIPMENT_CHANGE_EVENT, new MutableEventHolder.BasicEventHolder<>(POST_EQUIPMENT_CHANGE_EVENT, view -> view::onEquipmentChange, events -> (state, slot, oldEquipment, newEquipment) -> {
            for (final PostEquipmentChangeEvent.Mut event : events) {
                event.onEquipmentChange(state, slot, oldEquipment, newEquipment);
            }
        }));
    }

    public void setBattleState(final BattleState battleState) {
        if (this.battleState != null) {
            throw new RuntimeException("Tried to set battle of participant already in battle");
        }
        this.battleState = battleState;
    }

    @Override
    public BattleState getBattleState() {
        return battleState;
    }

    public <T, V> MutableEventHolder<T, V> getEventMut(final EventKey<T, V> key) {
        return eventMap.getMut(key);
    }

    @Override
    public <T, V> EventHolder<T, V> getEvent(final EventKey<T, V> key) {
        return eventMap.get(key);
    }

    public boolean equip(final BattleEquipmentSlot slot, final BattleEquipment equipment) {
        return equipmentState.equip(this, slot, equipment);
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public BattleParticipantHandle getHandle() {
        return handle;
    }
}
