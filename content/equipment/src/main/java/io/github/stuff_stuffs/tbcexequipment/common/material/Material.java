package io.github.stuff_stuffs.tbcexequipment.common.material;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import net.minecraft.text.Text;

import java.util.List;

public final class Material {
    private final Text name;
    private final List<Text> description;
    private final BattleParticipantItem.Rarity rarity;

    public Material(final Text name, final List<Text> description, final BattleParticipantItem.Rarity rarity) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
    }

    public Text getName() {
        return name;
    }

    public List<Text> getDescription() {
        return description;
    }

    public BattleParticipantItem.Rarity getRarity() {
        return rarity;
    }

    @Override
    public String toString() {
        return "Material{" + getName().asString() + "}";
    }
}
