package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.TBCExCoreClient;
import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleMoveWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.MouseLockableScreen;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.CycleSelectionWheelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.HidingPanel;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class BattleMoveScreen extends TBCExScreen implements MouseLockableScreen {
    private final BattleParticipantHandle handle;
    private final World world;
    private final BattleHudContext hudContext;
    private final PathContext context;
    private final HidingPanel options;
    private final Supplier<Boolean> fallDamageOption;

    private boolean locked = false;
    private boolean altMode = false;

    public BattleMoveScreen(final BattleParticipantHandle handle, final World world, final BattleHudContext hudContext) {
        super(new LiteralText("Move"), new RootPanelWidget());
        this.handle = handle;
        this.world = world;
        this.hudContext = hudContext;
        context = new PathContext();
        passEvents = true;

        options = new HidingPanel();
        final ParentWidget widget = (ParentWidget) this.widget;
        widget.addWidget(options);
        final WidgetPosition center = WidgetPosition.of(0.5, 0.5, 1);
        final CycleSelectionWheelWidget.Builder builder = CycleSelectionWheelWidget.builder();
        fallDamageOption = builder.addEntry(BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, b -> {
            if (b) {
                return new LiteralText("Fall Damage Paths: Enabled");
            } else {
                return new LiteralText("Fall Damage Paths: Disabled");
            }
        }, b -> List.of(), b -> !b, b -> !b, () -> null, false);
        final CycleSelectionWheelWidget optionWheel = builder.build(0.15, 0.3, 0.3125, center);
        options.addWidget(optionWheel);
        widget.addWidget(new BattleMoveWidget(handle, world, context, this.hudContext));
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        final InputUtil.Key altModeKey = KeyBindingHelper.getBoundKeyOf(TBCExCoreClient.ALT_MODE_KEYBINDING);
        if ((altModeKey.getCategory() == InputUtil.Type.KEYSYM && altModeKey.getCode() == keyCode) || (altModeKey.getCategory() == InputUtil.Type.SCANCODE && altModeKey.getCode() == scanCode)) {
            altMode = !altMode;
            options.setHidden(!altMode);
            passEvents = !altMode;
            locked = !altMode;
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
        context.fallDamagePaths = fallDamageOption.get();
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
