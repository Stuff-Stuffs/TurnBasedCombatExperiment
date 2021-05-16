package io.github.stuff_stuffs.turnbasedcombat.client.screen.battle;

import io.github.stuff_stuffs.turnbasedcombat.client.screen.AbstractBattleScreen;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.path.TestPathEnumerator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class BaseBattleScreen extends AbstractBattleScreen {

    @Override
    public void init(final BattleScreenParent parent) {
        super.init(parent);
        //TODO
        final ButtonWidget buttonWidget = new ButtonWidget(0, 0, 100, 20, new LiteralText(TurnBasedCombatExperiment.MOD_ID + "move"), button -> getParent().push(new MoveBattleScreen()));
        addButton(buttonWidget);

    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float tickDelta) {
        super.render(matrices, mouseX, mouseY, tickDelta);
    }

    @Override
    public void close() {

    }
}
