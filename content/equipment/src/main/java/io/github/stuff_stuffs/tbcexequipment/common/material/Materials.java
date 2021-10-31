package io.github.stuff_stuffs.tbcexequipment.common.material;

import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.List;

public final class Materials {
    public static final RegistryKey<Registry<Material>> REGISTRY_KEY = RegistryKey.ofRegistry(TBCExEquipment.createId("materials"));
    public static final Registry<Material> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable())).buildAndRegister();
    public static final Material WOOD = new Material(new LiteralText("Wood"), List.of(new LiteralText("It's wood!")), BattleParticipantItem.Rarity.COMMON);
    public static final Material STONE = new Material(new LiteralText("Stone"), List.of(new LiteralText("It's stone!")), BattleParticipantItem.Rarity.COMMON);
    public static final Material COPPER = new Material(new LiteralText("Copper"), List.of(new LiteralText("It's copper!")), BattleParticipantItem.Rarity.COMMON);
    public static final Material IRON = new Material(new LiteralText("Iron"), List.of(new LiteralText("It's iron!")), BattleParticipantItem.Rarity.COMMON);
    public static final Material GOLD = new Material(new LiteralText("Gold"), List.of(new LiteralText("It's gold!")), BattleParticipantItem.Rarity.UNCOMMON);
    public static final Material DIAMOND = new Material(new LiteralText("Diamond"), List.of(new LiteralText("It's diamond!")), BattleParticipantItem.Rarity.UNCOMMON);

    public static void init() {
        Registry.register(REGISTRY, TBCExEquipment.createId("wood"), WOOD);
        Registry.register(REGISTRY, TBCExEquipment.createId("stone"), STONE);
        Registry.register(REGISTRY, TBCExEquipment.createId("iron"), IRON);
        Registry.register(REGISTRY, TBCExEquipment.createId("copper"), COPPER);
        Registry.register(REGISTRY, TBCExEquipment.createId("gold"), GOLD);
        Registry.register(REGISTRY, TBCExEquipment.createId("diamond"), DIAMOND);
    }

    private Materials() {
    }
}
