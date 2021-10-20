package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventMap;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.*;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class BattleParticipantState implements BattleParticipantStateView {
    public static final Codec<BattleParticipantState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(state -> state.handle),
            Team.CODEC.fieldOf("team").forGetter(state -> state.team),
            Codec.list(ParticipantComponent.CODEC).xmap(l -> {
                Map<ParticipantComponents.Type<?, ?>, ParticipantComponent> components = new Reference2ReferenceOpenHashMap<>();
                for (Pair<ParticipantComponents.Type<?, ?>, ParticipantComponent> pair : l) {
                    components.put(pair.getFirst(), pair.getSecond());
                }
                return components;
            }, BattleParticipantState::getComponentStream).fieldOf("components").forGetter(state -> state.componentsByType)
    ).apply(instance, BattleParticipantState::new));
    private final EventMap eventMap;
    private final BattleParticipantHandle handle;
    private final Team team;
    private final Map<ParticipantComponents.Type<?, ?>, ParticipantComponent> componentsByType;
    private final Map<ParticipantComponentKey<?, ?>, ParticipantComponent> componentsByKey;
    private boolean valid;
    private BattleState battleState;

    private BattleParticipantState(final BattleParticipantHandle handle, final Team team, final Map<ParticipantComponents.Type<?, ?>, ParticipantComponent> componentsByType) {
        this.handle = handle;
        this.team = team;
        this.componentsByType = componentsByType;
        componentsByKey = new Reference2ReferenceOpenHashMap<>();
        for (final Map.Entry<ParticipantComponents.Type<?, ?>, ParticipantComponent> entry : componentsByType.entrySet()) {
            componentsByKey.put(entry.getKey().key, entry.getValue());
        }
        eventMap = new EventMap();
        valid = false;
    }


    public BattleParticipantState(final BattleParticipantHandle handle, final BattleEntity entity, final BattleState battle) {
        this.handle = handle;
        team = entity.getTeam();
        eventMap = new EventMap();
        valid = true;
        battleState = battle;
        componentsByKey = new Reference2ReferenceOpenHashMap<>();
        componentsByType = new Reference2ReferenceOpenHashMap<>();
        boolean lastAdded = true;
        BattleParticipantEvents.setup(eventMap);
        while (lastAdded) {
            lastAdded = false;
            for (final ParticipantComponents.Type<?, ?> type : ParticipantComponents.REGISTRY) {
                if (componentsByType.containsKey(type)) {
                    continue;
                }
                boolean acceptable = true;
                for (final ParticipantComponentKey<?, ?> key : type.requiredComponents) {
                    if (!componentsByKey.containsKey(key)) {
                        acceptable = false;
                        break;
                    }
                }
                if (acceptable) {
                    final ParticipantComponent apply = type.extractor.apply(entity, this);
                    if (apply != null) {
                        componentsByType.put(type, apply);
                        componentsByKey.put(type.key, apply);
                        apply.init(this);
                        lastAdded = true;
                    }
                }
            }
        }
    }

    public void setBattleState(final BattleState battleState) {
        if (!valid) {
            if (this.battleState != null) {
                throw new RuntimeException("Tried to set battle of participant already in battle");
            }
            BattleParticipantEvents.setup(eventMap);
            this.battleState = battleState;
            valid = true;
            for (final ParticipantComponent component : componentsByKey.values()) {
                component.init(this);
            }
        }
    }

    @Override
    public BattleState getBattleState() {
        if (!valid) {
            throw new RuntimeException();
        }
        return battleState;
    }

    public <T, V> MutableEventHolder<T, V> getEventMut(final EventKey<T, V> key) {
        if (!valid) {
            throw new RuntimeException();
        }
        return eventMap.getMut(key);
    }

    @Override
    public <T, V> EventHolder<T, V> getEvent(final EventKey<T, V> key) {
        if (!valid) {
            throw new RuntimeException();
        }
        return eventMap.get(key);
    }

    @Override
    public Team getTeam() {
        if (!valid) {
            throw new RuntimeException();
        }
        return team;
    }

    @Override
    public BattleParticipantHandle getHandle() {
        if (!valid) {
            throw new RuntimeException();
        }
        return handle;
    }

    @Override
    public @Nullable BattleParticipantItemStack getItemStack(final BattleParticipantInventoryHandle handle) {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getItemStack(handle);
    }

    @Override
    public @Nullable BattleParticipantItemStack getEquipmentStack(final BattleEquipmentSlot slot) {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getEquipmentStack(slot);
    }

    @Override
    public Iterator<BattleParticipantInventoryHandle> getInventoryIterator() {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getInventoryIterator();
    }

    @Override
    public double getStat(final BattleParticipantStat stat) {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getStat(stat);
    }

    @Override
    public double getHealth() {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getHealth();
    }

    @Override
    public BlockPos getPos() {
        final ParticipantPosComponentView component = getComponent(ParticipantComponents.POS_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getPos();
    }

    @Override
    public BattleEquipment getEquipment(final BattleEquipmentSlot slot) {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getEquipment(slot);
    }

    @Override
    public BattleParticipantBounds getBounds() {
        final ParticipantPosComponentView component = getComponent(ParticipantComponents.POS_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getBounds();
    }

    @Override
    public Text getName() {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getName();
    }

    @Override
    public double getEnergy() {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getEnergy();
    }

    @Override
    public int getLevel() {
        final ParticipantInfoComponentView component = getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("required component is missing");
        }
        return component.getLevel();
    }

    public void leave() {
        if (!valid) {
            throw new RuntimeException();
        }
        for (final ParticipantComponent component : componentsByKey.values()) {
            component.deinitEvents();
        }
        valid = false;
    }

    @Override
    public boolean hasComponent(final ParticipantComponentKey<?, ?> key) {
        return componentsByKey.containsKey(key);
    }

    @Override
    public <View extends ParticipantComponent> @Nullable View getComponent(final ParticipantComponentKey<?, View> key) {
        return (View) componentsByKey.get(key);
    }

    public <Mut extends View, View extends ParticipantComponent> @Nullable Mut getMutComponent(final ParticipantComponentKey<Mut, View> key) {
        return (Mut) componentsByKey.get(key);
    }

    private static List<Pair<ParticipantComponents.Type<?, ?>, ParticipantComponent>> getComponentStream(final Map<ParticipantComponents.Type<?, ?>, ParticipantComponent> map) {
        final List<Pair<ParticipantComponents.Type<?, ?>, ParticipantComponent>> componentList = new ArrayList<>(map.size());
        for (final Map.Entry<ParticipantComponents.Type<?, ?>, ParticipantComponent> entry : map.entrySet()) {
            componentList.add(Pair.of(entry.getKey(), entry.getValue()));
        }
        return componentList;
    }
}
