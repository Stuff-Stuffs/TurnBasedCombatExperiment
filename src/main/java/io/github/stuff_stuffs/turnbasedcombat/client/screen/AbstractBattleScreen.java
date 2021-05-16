package io.github.stuff_stuffs.turnbasedcombat.client.screen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractBattleScreen implements BattleScreen {
    private final ObjectArrayList<AbstractButtonWidget> buttonWidgets = new ObjectArrayList<>();
    private BattleScreenParent parent;

    @Override
    public void init(final BattleScreenParent parent) {
        this.parent = parent;
        buttonWidgets.clear();
    }

    protected void addButton(final AbstractButtonWidget widget) {
        buttonWidgets.add(widget);
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float tickDelta) {
        for (final AbstractButtonWidget widget : buttonWidgets) {
            widget.render(matrices, mouseX, mouseY, tickDelta);
        }
    }

    @Override
    public void mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (final AbstractButtonWidget buttonWidget : buttonWidgets) {
            buttonWidget.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(final double mouseX, final double mouseY, final int button) {
        for (final AbstractButtonWidget buttonWidget : buttonWidgets) {
            buttonWidget.mouseReleased(mouseX, mouseY, button);
        }
    }

    public BattleScreenParent getParent() {
        return parent;
    }
}
