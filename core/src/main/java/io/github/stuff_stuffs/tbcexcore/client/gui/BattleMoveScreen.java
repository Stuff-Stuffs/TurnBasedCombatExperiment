package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleMoveWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.MouseLockableScreen;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.SuppliedWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.CycleButton;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.HidingPanel;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.DoubleSupplier;

public class BattleMoveScreen extends TBCExScreen implements MouseLockableScreen {
    private final BattleParticipantHandle handle;
    private final World world;
    private final BattleHudContext hudContext;
    private final PathContext context;
    private final HidingPanel options;
    private final CycleButton<Boolean> fallDamageOption;

    private boolean locked = false;
    private boolean altMode = false;

    public BattleMoveScreen(final BattleParticipantHandle handle, final World world, BattleHudContext hudContext) {
        super(new LiteralText("Move"), new RootPanelWidget());
        this.handle = handle;
        this.world = world;
        this.hudContext = hudContext;
        context = new PathContext();
        passEvents = true;

        options = new HidingPanel();
        final ParentWidget widget = (ParentWidget) this.widget;
        final DoubleSupplier left = () -> (Math.max(width / (double)height, 1) - 1) / -2.0;
        final DoubleSupplier top = () -> (Math.max(height / (double)width, 1) - 1) / -2.0;
        final SuppliedWidgetPosition optionsPanelPos = new SuppliedWidgetPosition(left, top, () -> 10);
        final BasicPanelWidget optionsPanel = new BasicPanelWidget(optionsPanelPos, () -> false, () -> 1, 0.275, 0.25);
        fallDamageOption = new CycleButton<>(WidgetPosition.combine(optionsPanelPos, WidgetPosition.of(0.005, 0.005, 1)), () -> 1, () -> true, 0.265, 0.075, false, b -> !b, b -> {
            if (b) {
                return new LiteralText("Fall damage paths enabled");
            } else {
                return new LiteralText("Fall damage paths disabled");
            }
        }, b -> List.of());
        optionsPanel.addWidget(fallDamageOption);
        widget.addWidget(options);
        options.addWidget(optionsPanel);
        widget.addWidget(new BattleMoveWidget(handle, world, context, this.hudContext));
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        final InputUtil.Key altModeKey = KeyBindingHelper.getBoundKeyOf(TurnBasedCombatExperimentClient.ALT_MODE_KEYBINDING);
        if ((altModeKey.getCategory() == InputUtil.Type.KEYSYM && altModeKey.getCode() == keyCode) || (altModeKey.getCategory() == InputUtil.Type.SCANCODE && altModeKey.getCode() == scanCode)) {
            altMode = !altMode;
            options.setHidden(!altMode);
            return true;
        }
        if (altMode && keyCode == GLFW.GLFW_KEY_F) {
            fallDamageOption.click();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            locked = !locked;
            passEvents = locked;
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        context.fallDamagePaths = fallDamageOption.getCurrentState();
    }

    @Override
    public boolean shouldLockMouse() {
        return locked;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static class PathContext {
        public boolean fallDamagePaths = false;
    }
}
