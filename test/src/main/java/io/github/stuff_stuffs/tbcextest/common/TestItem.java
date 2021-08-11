package io.github.stuff_stuffs.tbcextest.common;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.item.BattleItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Random;

public class TestItem extends Item implements BattleItem {
    public TestItem() {
        super(new FabricItemSettings());
    }

    @Override
    public BattleParticipantItemStack toBattleItem(final ItemStack stack) {
        final Random random = new Random();
        Text[] texts = new Text[]{new LiteralText("short"), new LiteralText("long long long long"), new LiteralText("medium"), new LiteralText("p")};
        return new BattleParticipantItemStack(new TestBattleParticipantItem(texts[random.nextInt(texts.length)]), stack.getCount());
    }
}
