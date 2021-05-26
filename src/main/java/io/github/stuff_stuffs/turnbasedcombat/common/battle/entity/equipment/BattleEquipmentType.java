package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.BattleEntity;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record BattleEquipmentType(Text name,
                                  boolean canEquipMidBattle,
                                  Codec<BattleEquipment> codec,
                                  Function<BattleEntity, BattleEquipment> extractor) {
    public static final Registry<BattleEquipmentType> REGISTRY = FabricRegistryBuilder.createSimple(BattleEquipmentType.class, TurnBasedCombatExperiment.createId("battle_equipment_type")).buildAndRegister();
    public static final Codec<BattleEquipment> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<T> encode(final BattleEquipment input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "data",
                    input.getType().codec().encodeStart(ops, input)
            ).add(
                    "id",
                    Identifier.CODEC.encodeStart(ops, REGISTRY.getId(input.getType()))
            ).build(prefix);
        }

        @Override
        public <T> DataResult<Pair<BattleEquipment, T>> decode(final DynamicOps<T> ops, final T input) {
            MapLike<T> map = ops.getMap(input).getOrThrow(false, s-> {
                throw new RuntimeException(s);
            });
            Identifier typeId = Identifier.CODEC.parse(ops, map.get("id")).getOrThrow(false, s-> {
                throw new RuntimeException(s);
            });
            BattleEquipmentType type = REGISTRY.get(typeId);
            return type.codec().decode(ops, map.get("data"));
        }
    };

    public @Nullable BattleEquipment applyExtractor(final BattleEntity entity) {
        return extractor.apply(entity);
    }
}
