package io.github.stuff_stuffs.tbcexcore.common.battle.damage;

import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class BattleDamageType {
    public static final Registry<BattleDamageType> REGISTRY = FabricRegistryBuilder.createSimple(BattleDamageType.class, TurnBasedCombatExperiment.createId("damage_types")).buildAndRegister();
    //TODO translatable texts
    public static final BattleDamageType PHYSICAL = createRoot(new LiteralText("physical"));
    public static final BattleDamageType SLASHING = createChild(new LiteralText("slashing"), PHYSICAL);
    public static final BattleDamageType PIERCING = createChild(new LiteralText("piercing"), PHYSICAL);
    public static final BattleDamageType CRUSHING = createChild(new LiteralText("crushing"), PHYSICAL);

    public static final BattleDamageType ENVIRONMENTAL = createRoot(new LiteralText("environmental"));
    public static final BattleDamageType FIRE = createChild(new LiteralText("fire"), ENVIRONMENTAL);
    public static final BattleDamageType FREEZE = createChild(new LiteralText("freeze"), ENVIRONMENTAL);
    public static final BattleDamageType ASPHYXIATION = createChild(new LiteralText("asphyxiation"), ENVIRONMENTAL);

    public static final BattleDamageType MAGIC = createRoot(new LiteralText("magic"));

    private final Text name;
    private final Collection<BattleDamageType> parents;
    private final Set<BattleDamageType> ancestors;

    public BattleDamageType(final Text name, final Collection<BattleDamageType> parents) {
        this.name = name;
        this.parents = parents;
        ancestors = new ReferenceOpenHashSet<>();
        accumulateAncestors(this);
    }

    private void accumulateAncestors(final BattleDamageType damageType) {
        for (final BattleDamageType parent : damageType.parents) {
            ancestors.add(parent);
            accumulateAncestors(parent);
        }
    }

    public Text getName() {
        return name;
    }

    public boolean isChildOf(final BattleDamageType parent) {
        return parent.isParentOf(this);
    }

    public boolean isParentOf(final BattleDamageType child) {
        return child.ancestors.contains(this);
    }

    public Set<BattleDamageType> getAncestors() {
        return Collections.unmodifiableSet(ancestors);
    }

    @Override
    public String toString() {
        return name.asString();
    }

    public static boolean related(final BattleDamageType first, final BattleDamageType second) {
        if (first == second) {
            return true;
        }
        return first.isParentOf(second) || second.isParentOf(first);
    }

    public static BattleDamageType createRoot(final Text name) {
        return new BattleDamageType(name, Collections.emptySet());
    }

    public static BattleDamageType createChild(final Text name, final BattleDamageType... parents) {
        return new BattleDamageType(name, Arrays.asList(parents));
    }

    public static void init() {
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
