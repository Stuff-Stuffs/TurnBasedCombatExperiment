package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;

public class BattleHudHealthWidget extends AbstractWidget {
    private static final WidgetPosition ROOT = WidgetPosition.of(0,0,0);
    private final BattleHandle handle;
    private final World world;

    public BattleHudHealthWidget(BattleHandle handle, World world) {
        this.handle = handle;
        this.world = world;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return ROOT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, float delta) {

    }

    @Override
    public boolean keyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
