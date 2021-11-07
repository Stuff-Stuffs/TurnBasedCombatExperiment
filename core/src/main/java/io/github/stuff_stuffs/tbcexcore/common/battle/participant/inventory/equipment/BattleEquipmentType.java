package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public record BattleEquipmentType(Text name, Codec<BattleEquipment> codec) {
    public static final Registry<BattleEquipmentType> REGISTRY = FabricRegistryBuilder.createSimple(BattleEquipmentType.class, TBCExCore.createId("equipment_types")).buildAndRegister();
    public static final Codec<BattleEquipment> CODEC = CodecUtil.createDependentPairCodecFirst(REGISTRY, type -> type.codec, BattleEquipment::getType);
}
