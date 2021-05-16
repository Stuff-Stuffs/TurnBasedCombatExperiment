package io.github.stuff_stuffs.turnbasedcombat.client.screen;

import io.github.stuff_stuffs.turnbasedcombat.client.screen.battle.BaseBattleScreen;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.Optional;

public class StackedBattleScreen extends Screen implements BattleScreen.BattleScreenParent {
    private final ObjectArrayList<BattleScreen> screenStack;

    public StackedBattleScreen() {
        //TODO
        super(new LiteralText(TurnBasedCombatExperiment.MOD_ID + ".battle_screen"));
        screenStack = new ObjectArrayList<>(6);
        screenStack.push(new BaseBattleScreen());
    }

    @Override
    protected void init() {
        for (final BattleScreen battleScreen : screenStack) {
            battleScreen.init(this);
        }
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        screenStack.top().render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        screenStack.top().mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        screenStack.top().mouseReleased(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        screenStack.top().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return true;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        screenStack.top().mouseScrolled(mouseX, mouseY, amount);
        return true;
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        screenStack.top().keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 256) {
            final BattleScreen screen = screenStack.pop();
            screen.close();
            if (screenStack.isEmpty()) {
                onClose();
                return true;
            }
        }
        screenStack.top().keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public Optional<Element> hoveredElement(final double mouseX, final double mouseY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void push(final BattleScreen screen) {
        screenStack.push(screen);
        screen.init(this);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ItemRenderer getItemRenderer() {
        return itemRenderer;
    }

    @Override
    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
