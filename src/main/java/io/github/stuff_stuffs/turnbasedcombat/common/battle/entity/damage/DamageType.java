package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage;

import com.google.common.collect.Iterators;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.*;

public final class DamageType implements Iterable<DamageType> {
    public static final Registry<DamageType> REGISTRY = FabricRegistryBuilder.createSimple(DamageType.class, TurnBasedCombatExperiment.createId("damage_types")).buildAndRegister();
    public static final DamageType PHYSICAL = createRoot(new LiteralText("physical"));
    public static final DamageType SLASHING = createChild(new LiteralText("slashing"), PHYSICAL);
    public static final DamageType PIERCING = createChild(new LiteralText("piercing"), PHYSICAL);
    public static final DamageType CRUSHING = createChild(new LiteralText("crushing"), PHYSICAL);

    public static final DamageType ENVIRONMENTAL = createRoot(new LiteralText("environmental"));
    public static final DamageType FIRE = createChild(new LiteralText("fire"), ENVIRONMENTAL);
    public static final DamageType FREEZE = createChild(new LiteralText("freeze"), ENVIRONMENTAL);
    public static final DamageType ASPHYXIATION = createChild(new LiteralText("asphyxiation"), ENVIRONMENTAL);

    public static final DamageType MAGIC = createRoot(new LiteralText("magic"));


    private final Text name;
    private final List<DamageType> parents;
    private final Set<DamageType> ancestors;

    private DamageType(final Text name, final List<DamageType> parents) {
        this.name = name;
        this.parents = parents;
        ancestors = new ReferenceOpenHashSet<>();
        accumulateAncestors(this);
    }

    private void accumulateAncestors(final DamageType damageType) {
        for (final DamageType parent : damageType.parents) {
            ancestors.add(parent);
            accumulateAncestors(parent);
        }
    }

    public Text getName() {
        return name;
    }

    @Override
    public Iterator<DamageType> iterator() {
        return Iterators.unmodifiableIterator(parents.iterator());
    }

    public boolean isChildOf(final DamageType parent) {
        return parent.isParentOf(this);
    }

    public boolean isParentOf(final DamageType child) {
        return child.ancestors.contains(this);
    }

    @Override
    public String toString() {
        return name.asString();
    }

    public static boolean related(final DamageType first, final DamageType second) {
        if (first == second) {
            return true;
        }
        return first.isParentOf(second) || second.isParentOf(first);
    }

    public static DamageType createRoot(final Text name) {
        return new DamageType(name, Collections.emptyList());
    }

    public static DamageType createChild(final Text name, final DamageType... parents) {
        return new DamageType(name, Arrays.asList(parents));
    }

    static {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("physical"), PHYSICAL);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("slashing"), SLASHING);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("piercing"), PIERCING);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("crushing"), CRUSHING);

        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("environmental"), ENVIRONMENTAL);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("fire"), FIRE);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("freeze"), FREEZE);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("asphyxiation"), ASPHYXIATION);

        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("magic"), MAGIC);
    }
}
