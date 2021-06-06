package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record BattleEquipmentSlot(Text name, BattleEquipmentType type,
                                  Function<BattleEntity, @Nullable BattleEquipment> extractor) {
    public static final Registry<BattleEquipmentSlot> REGISTRY = FabricRegistryBuilder.createSimple(BattleEquipmentSlot.class, TurnBasedCombatExperiment.createId("battle_equipment_slot")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public @Nullable BattleEquipment applyExtractor(final BattleEntity entity) {
        return extractor.apply(entity);
    }
}
