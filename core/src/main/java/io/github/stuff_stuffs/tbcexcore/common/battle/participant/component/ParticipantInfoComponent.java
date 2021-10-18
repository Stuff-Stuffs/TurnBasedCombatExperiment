package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventListenerHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStatModifier;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStatModifiers;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStats;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class ParticipantInfoComponent extends AbstractParticipantComponent implements ParticipantInfoComponentView {
    public static final Codec<ParticipantInfoComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CodecUtil.TEXT_CODEC.fieldOf("name").forGetter(component -> component.name),
                    BattleParticipantInventory.CODEC.fieldOf("inventory").forGetter(component -> component.inventory),
                    BattleParticipantStats.CODEC.fieldOf("stats").forGetter(component -> component.stats),
                    Codec.DOUBLE.fieldOf("health").forGetter(component -> component.health),
                    Codec.DOUBLE.fieldOf("energy").forGetter(component -> component.energy)
            ).apply(instance, ParticipantInfoComponent::new)
    );
    private final Text name;
    private final BattleParticipantInventory inventory;
    private final BattleParticipantStats stats;
    private double health;
    private double energy;
    private EventListenerHandle turnEventHandle;
    private boolean setupEnergy;

    public ParticipantInfoComponent(final Text name, final BattleParticipantInventory inventory, final BattleParticipantStats stats, final double health, final double energy) {
        this.name = name;
        this.inventory = inventory;
        this.stats = stats;
        this.health = health;
        this.energy = energy;
        setupEnergy = true;
    }

    public ParticipantInfoComponent(final Text name, final BattleParticipantInventory inventory, final BattleParticipantStats stats, final double health) {
        this.name = name;
        this.inventory = inventory;
        this.stats = stats;
        this.health = health;
        setupEnergy = false;
    }

    @Override
    public void init(final BattleParticipantState state) {
        super.init(state);
        inventory.initEvents(state);
        turnEventHandle = state.getBattleState().getEvent(BattleStateView.ADVANCE_TURN_EVENT).register((battleState, current) -> {
            if (state.getHandle().equals(current)) {
                energy = stats.calculate(BattleParticipantStat.ENERGY_PER_TURN_STAT, battleState, state);
            }
        });
        if (!setupEnergy) {
            setupEnergy = true;
            energy = stats.calculate(BattleParticipantStat.ENERGY_PER_TURN_STAT, state.getBattleState(), state);
        }
    }

    @Override
    public void deinitEvents() {
        super.deinitEvents();
        inventory.deinitEvents();
        turnEventHandle.destroy();
    }

    @Override
    public ParticipantComponents.Type<?, ?> getType() {
        return ParticipantComponents.INFO_COMPONENT_TYPE;
    }

    public boolean equip(final BattleEquipmentSlot slot, final BattleParticipantItemStack equipment) {
        return inventory.equip(state, slot, equipment);
    }

    @Override
    public @Nullable BattleParticipantItemStack getItemStack(final BattleParticipantInventoryHandle handle) {
        if (handle.handle().equals(state.getHandle())) {
            return inventory.get(handle.id());
        } else {
            throw new RuntimeException();
        }
    }

    public BattleParticipantInventoryHandle giveItems(final BattleParticipantItemStack stack) {
        return new BattleParticipantInventoryHandle(state.getHandle(), inventory.give(stack));
    }

    public @Nullable BattleParticipantItemStack takeItems(final BattleParticipantInventoryHandle handle, final int amount) {
        if (handle.handle().equals(state.getHandle())) {
            return inventory.take(handle.id(), amount);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public @Nullable BattleEquipment getEquipment(final BattleEquipmentSlot slot) {
        return inventory.getEquipment(slot);
    }

    @Override
    public @Nullable BattleParticipantItemStack getEquipmentStack(final BattleEquipmentSlot slot) {
        return inventory.getEquipmentStack(slot);
    }

    @Override
    public Iterator<BattleParticipantInventoryHandle> getInventoryIterator() {
        return StreamSupport.stream(inventory.spliterator(), false).map(entry -> new BattleParticipantInventoryHandle(state.getHandle(), entry.getIntKey())).iterator();
    }

    public BattleParticipantStatModifiers.Handle addStatModifier(final BattleParticipantStat stat, final BattleParticipantStatModifier modifier) {
        final BattleParticipantStatModifiers.Handle handle = stats.modify(stat, modifier);
        final double maxHealth = stats.calculate(BattleParticipantStat.MAX_HEALTH_STAT, state.getBattleState(), state);
        if (maxHealth < 0) {
            health = 0;
        } else {
            health = Math.min(health, maxHealth);
        }
        return handle;
    }

    @Override
    public double getStat(final BattleParticipantStat stat) {
        return stats.calculate(stat, state.getBattleState(), state);
    }

    public @Nullable BattleDamagePacket damage(final BattleDamagePacket packet) {
        final BattleDamagePacket processed = state.getEvent(BattleParticipantStateView.PRE_DAMAGE_EVENT).invoker().onDamage(state, packet);
        if (processed.getTotalDamage() > 0.0001) {
            health -= processed.getTotalDamage();
            health = Math.max(health, 0);
            state.getEvent(BattleParticipantStateView.POST_DAMAGE_EVENT).invoker().onDamage(state, packet);
            return processed;
        }
        return null;
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public Text getName() {
        return name;
    }

    public boolean useEnergy(final double amount) {
        if (energy >= amount) {
            energy -= amount;
            return true;
        }
        return false;
    }

    @Override
    public double getEnergy() {
        return energy;
    }
}
