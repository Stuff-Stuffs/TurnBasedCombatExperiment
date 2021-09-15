package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleActionRenderTargetsWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexgui.client.screen.MouseLockableScreen;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class BattleActionScreen extends TBCExScreen implements MouseLockableScreen {
    private boolean init = false;
    private final ParticipantActionInstance actionInstance;
    private final Screen prevScreen;
    private boolean locked = false;

    public BattleActionScreen(final ParticipantActionInstance actionInstance) {
        super(new LiteralText("Battle Action"), new RootPanelWidget());
        this.actionInstance = actionInstance;
        prevScreen = MinecraftClient.getInstance().currentScreen;
        passEvents = true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if(button== GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            locked = !locked;
            passEvents = locked;
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        if (!init) {
            init = true;
            final ParentWidget root = (ParentWidget) widget;
            root.addWidget(new BattleActionRenderTargetsWidget(actionInstance));
        }
    }

    @Override
    public void onClose() {
        MinecraftClient.getInstance().setScreen(prevScreen);
    }

    @Override
    public boolean shouldLockMouse() {
        return locked;
    }
}
