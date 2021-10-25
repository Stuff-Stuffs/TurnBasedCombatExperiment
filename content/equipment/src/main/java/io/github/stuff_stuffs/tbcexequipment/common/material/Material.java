package io.github.stuff_stuffs.tbcexequipment.common.material;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import net.minecraft.text.Text;

import java.util.List;

public interface Material {
    Text getName();

    List<Text> getDescription();

    BattleParticipantItem.Rarity getRarity();
}
