package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleActionRenderTargetsWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.MouseLockableScreen;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetModifiers;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class BattleActionScreen extends TBCExScreen implements MouseLockableScreen {
    private final BattleParticipantHandle handle;
    private final ParticipantActionInstance actionInstance;
    private final Screen prevScreen;
    private boolean locked = false;

    public BattleActionScreen(final BattleParticipantHandle handle, final ParticipantActionInstance actionInstance) {
        super(new LiteralText("Battle Action"), new RootPanelWidget(false));
        this.handle = handle;
        this.actionInstance = actionInstance;
        prevScreen = MinecraftClient.getInstance().currentScreen;
        passEvents = true;
        final RootPanelWidget root = (RootPanelWidget) widget;
        root.addChild(WidgetModifiers.positioned(new BattleActionRenderTargetsWidget(actionInstance), () -> 0, () -> 0));
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            locked = !locked;
            passEvents = locked;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        MinecraftClient.getInstance().setScreen(prevScreen);
        if (prevScreen != null) {
            prevScreen.tick();
        }
    }

    @Override
    public void tick() {
        super.tick();
        final Battle battle = ((BattleWorldSupplier) MinecraftClient.getInstance().world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null || !handle.equals(battle.getState().getCurrentTurn())) {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldLockMouse() {
        return locked;
    }
}
