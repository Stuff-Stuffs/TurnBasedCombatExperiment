package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.TBCExCoreClient;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.ParticipantOtherStatListWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.ParticipantStatusEffectListWidget;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.MouseLockableScreen;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetHandle;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.colour.FloatRgbColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.Iterator;

public class BattleParticipantOtherInfoScreen extends TBCExScreen implements MouseLockableScreen {
    private static final FloatRgbColour SELECTED = new FloatRgbColour(0, 1, 0);
    private static final FloatRgbColour NON_SELECTED = new FloatRgbColour(1, 0, 0);
    private final World world;
    private final BattleParticipantHandle handle;
    private BattleParticipantHandle targetHandle = null;
    private WidgetHandle statListHandle;
    private boolean locked = true;

    public BattleParticipantOtherInfoScreen(final World world, final BattleParticipantHandle participantHandle) {
        super(new LiteralText("Participant Info"), new RootPanelWidget());
        this.world = world;
        handle = participantHandle;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (statListHandle == null && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            locked = !locked;
            passEvents = locked;
        }
        if (statListHandle == null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
            if (battle != null) {
                final Vec3d start = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(1);
                final Vec3d end = start.add(ClientUtil.getMouseVector().multiply(64));
                double bestDist = Double.POSITIVE_INFINITY;
                BattleParticipantHandle best = null;
                final Iterator<BattleParticipantHandle> iterator = battle.getState().getParticipants();
                while (iterator.hasNext()) {
                    final BattleParticipantHandle nextHandle = iterator.next();
                    if (!nextHandle.equals(handle)) {
                        final BattleParticipantStateView participant = battle.getState().getParticipant(nextHandle);
                        final BattleParticipantBounds.RaycastResult raycast = participant.getBounds().raycast(start, end);
                        if (raycast != null) {
                            final double d = raycast.hitPoint().squaredDistanceTo(start);
                            if (d < bestDist) {
                                bestDist = d;
                                best = nextHandle;
                            }
                        }
                    }
                }
                if (best != null) {
                    targetHandle = best;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && statListHandle != null) {
            ((ParentWidget) widget).removeWidget(statListHandle);
            statListHandle = null;
            targetHandle = null;
            passEvents = true;
            locked = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        if (statListHandle == null && targetHandle != null) {
            final BasicPanelWidget panel = new BasicPanelWidget(WidgetPosition.of(0.125, 0.125, 1), 0.75, 0.75);
            final ParticipantOtherStatListWidget statListWidget = new ParticipantOtherStatListWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0.005, 0.380, 0)), 0.365, 0.365, 0.05, handle, targetHandle, world);
            final ParticipantStatusEffectListWidget statusEffectWidget = new ParticipantStatusEffectListWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0.380, 0.380, 0)), 0.365, 0.365, 0.05, targetHandle, world);
            panel.addWidget(statListWidget);
            panel.addWidget(statusEffectWidget);
            statListHandle = ((ParentWidget) widget).addWidget(panel);
        } else {
            final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
            if (battle != null) {
                final Vec3d start = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(1);
                final Vec3d end = start.add(ClientUtil.getMouseVector().multiply(64));
                double bestDist = Double.POSITIVE_INFINITY;
                BattleParticipantHandle best = null;
                Iterator<BattleParticipantHandle> iterator = battle.getState().getParticipants();
                while (iterator.hasNext()) {
                    final BattleParticipantHandle nextHandle = iterator.next();
                    if (!nextHandle.equals(handle)) {
                        final BattleParticipantStateView participant = battle.getState().getParticipant(nextHandle);
                        final BattleParticipantBounds.RaycastResult raycast = participant.getBounds().raycast(start, end);
                        if (raycast != null) {
                            final double d = raycast.hitPoint().squaredDistanceTo(start);
                            if (d < bestDist) {
                                bestDist = d;
                                best = nextHandle;
                            }
                        }
                    }
                }
                iterator = battle.getState().getParticipants();
                while (iterator.hasNext()) {
                    final BattleParticipantHandle nextHandle = iterator.next();
                    if (!nextHandle.equals(handle)) {
                        final boolean selected = nextHandle.equals(best);
                        final BattleParticipantStateView participant = battle.getState().getParticipant(nextHandle);
                        for (final BattleParticipantBounds.Part part : participant.getBounds()) {
                            if (selected) {
                                TBCExCoreClient.addBoxInfo(new BoxInfo(part.box, SELECTED.r, SELECTED.g, SELECTED.b, 1));
                            } else {
                                TBCExCoreClient.addBoxInfo(new BoxInfo(part.box, NON_SELECTED.r, NON_SELECTED.g, NON_SELECTED.b, 1));
                            }
                        }
                    }
                }
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
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
