package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record BattleEquipmentType(Text name, Codec<BattleEquipment> codec) {
    public static final Registry<BattleEquipmentType> REGISTRY = FabricRegistryBuilder.createSimple(BattleEquipmentType.class, TurnBasedCombatExperiment.createId("equipment_types")).buildAndRegister();
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
            MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            Identifier typeId = Identifier.CODEC.parse(ops, map.get("id")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            BattleEquipmentType type = REGISTRY.get(typeId);
            return type.codec().decode(ops, map.get("data"));
        }
    };
}
