package io.github.stuff_stuffs.turnbasedcombat.client.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Optional;

public interface BattleScreen {
    void init(BattleScreenParent parent);

    void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta);

    default void mouseClicked(final double mouseX, final double mouseY, final int button) {
    }

    default void mouseReleased(final double mouseX, final double mouseY, final int button) {
    }

    default void mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
    }

    default void mouseScrolled(final double mouseX, final double mouseY, final double amount) {
    }

    default void keyPressed(final int keyCode, final int scanCode, final int modifiers) {
    }

    default void keyReleased(final int keyCode, final int scanCode, final int modifiers) {
    }

    void close();

    interface BattleScreenParent {
        void push(BattleScreen screen);

        int getWidth();

        int getHeight();

        ItemRenderer getItemRenderer();

        TextRenderer getTextRenderer();
    }
}
