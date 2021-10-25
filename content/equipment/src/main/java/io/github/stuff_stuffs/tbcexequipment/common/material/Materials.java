package io.github.stuff_stuffs.tbcexequipment.common.material;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.List;

public final class Materials {
    public static final Registry<Material> REGISTRY = FabricRegistryBuilder.createSimple(Material.class, TBCExEquipment.createId("materials")).attribute(RegistryAttribute.MODDED).buildAndRegister();
    public static final Material WOOD = new Material() {
        @Override
        public Text getName() {
            return new LiteralText("Wood");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("It's wood!"));
        }

        @Override
        public BattleParticipantItem.Rarity getRarity() {
            return BattleParticipantItem.Rarity.COMMON;
        }
    };
    public static final Material STONE = new Material() {
        @Override
        public Text getName() {
            return new LiteralText("Stone");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("It's stone!"));
        }

        @Override
        public BattleParticipantItem.Rarity getRarity() {
            return BattleParticipantItem.Rarity.COMMON;
        }
    };
    public static final Material IRON = new Material() {
        @Override
        public Text getName() {
            return new LiteralText("Iron");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("It's iron!"));
        }

        @Override
        public BattleParticipantItem.Rarity getRarity() {
            return BattleParticipantItem.Rarity.COMMON;
        }
    };
    public static final Material GOLD = new Material() {
        @Override
        public Text getName() {
            return new LiteralText("Gold");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("It's gold!"));
        }

        @Override
        public BattleParticipantItem.Rarity getRarity() {
            return BattleParticipantItem.Rarity.UNCOMMON;
        }
    };
    public static final Material DIAMOND = new Material() {
        @Override
        public Text getName() {
            return new LiteralText("Diamond");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("It's diamond!"));
        }

        @Override
        public BattleParticipantItem.Rarity getRarity() {
            return BattleParticipantItem.Rarity.UNCOMMON;
        }
    };

    public static void init() {
        Registry.register(REGISTRY, TBCExEquipment.createId("wood"), WOOD);
        Registry.register(REGISTRY, TBCExEquipment.createId("stone"), STONE);
        Registry.register(REGISTRY, TBCExEquipment.createId("iron"), IRON);
        Registry.register(REGISTRY, TBCExEquipment.createId("gold"), GOLD);
        Registry.register(REGISTRY, TBCExEquipment.createId("diamond"), DIAMOND);
    }

    private Materials() {
    }
}
