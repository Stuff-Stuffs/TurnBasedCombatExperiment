package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.MouseLockableScreen;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.path.*;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BattleMoveScreen extends TBCExScreen implements MouseLockableScreen {
    private final BattleParticipantHandle handle;
    private final World world;
    private List<Path> paths = null;
    private List<EndPoint> endPoints = null;
    private boolean init = false;
    private boolean foundPaths = false;
    private boolean locked = false;

    protected BattleMoveScreen(final BattleParticipantHandle handle, final World world) {
        super(new LiteralText("Move"), new RootPanelWidget());
        this.handle = handle;
        this.world = world;
        passEvents = true;
    }

    @Override
    public void tick() {
        if (!init) {
            init = true;
            final ParentWidget widget = (ParentWidget) this.widget;
        }
        if(!foundPaths) {
            final Pather pather = new DjikstraPather();
            final BattleWorld world = ((BattleWorldSupplier) this.world).tbcex_getBattleWorld();
            if (world == null) {
                throw new RuntimeException();
            }
            final Battle battle = world.getBattle(handle.battleId());
            if (battle == null) {
                throw new RuntimeException();
            }
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant == null) {
                return;
            }
            foundPaths = true;
            paths = pather.getPaths(participant.getPos(), participant.getFacing(), participant.getBounds(), battle.getBounds().getBox(), this.world, MovementType.LAND);
            endPoints = paths.stream().map(p -> {
                final Movement last = p.getMovements().get(p.getMovements().size() - 1);
                return new EndPoint(last.getStartPos(), last.getEndPos(), last);
            }).toList();
        }
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
    public boolean shouldLockMouse() {
        return locked;
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if(foundPaths) {
            final Pather pather = new DjikstraPather();
            final BattleWorld world = ((BattleWorldSupplier) this.world).tbcex_getBattleWorld();
            if (world == null) {
                throw new RuntimeException();
            }
            final Battle battle = world.getBattle(handle.battleId());
            if (battle == null) {
                throw new RuntimeException();
            }
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant == null) {
                return;
            }
            foundPaths = true;
            paths = pather.getPaths(participant.getPos(), participant.getFacing(), participant.getBounds(), battle.getBounds().getBox(), this.world, MovementType.LAND);
            endPoints = paths.stream().map(p -> {
                final Movement last = p.getMovements().get(p.getMovements().size() - 1);
                return new EndPoint(last.getStartPos(), last.getEndPos(), last);
            }).toList();
            final Vec3d mouseVector = ClientUtil.getMouseVector();
            final Vec3d eyePos = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(delta);
            final Vec3d endPos = eyePos.add(mouseVector.multiply(64));
            double closestDist = Double.POSITIVE_INFINITY;
            EndPoint closest = null;
            for (final EndPoint endPoint : endPoints) {
                final Optional<Vec3d> raycast = endPoint.box.raycast(eyePos, endPos);
                if (raycast.isPresent()) {
                    final double sq = raycast.get().squaredDistanceTo(eyePos);
                    if (sq < closestDist) {
                        closest = endPoint;
                        closestDist = sq;
                    }
                }
            }
            for (int i = 0; i < endPoints.size(); i++) {
                final EndPoint endPoint = endPoints.get(i);
                final double r;
                final double g;
                if (endPoint == closest) {
                    r = 0;
                    g = 1;
                    TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(endPoint.box, r, g, 0, 1));
                    TurnBasedCombatExperimentClient.addRenderPrimitive(renderPath(paths.get(i)));
                } else {
                    r = 1;
                    g = 0;
                }
            }
        }
    }

    private static Consumer<WorldRenderContext> renderPath(final Path path) {
        return context -> {
            final MatrixStack matrices = context.matrixStack();
            final VertexConsumer vertexConsumer = context.consumers().getBuffer(RenderLayer.LINES);
            for (final Movement movement : path.getMovements()) {
                final Vec3d start = Vec3d.ofCenter(movement.getStartPos());
                Vec3d prev = start;
                for (int i = 0; i < 7; i++) {
                    final Vec3d next = movement.interpolate(start, movement.getLength() * i / 8.0);
                    RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, prev, matrices), 0xFF00FF00), prev, next, matrices).next();
                    RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, next, matrices), 0xFF00FF00), prev, next, matrices).next();
                    prev = next;
                }
                final Vec3d next = movement.interpolate(start, movement.getLength());
                RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, prev, matrices), 0xFF00FF00), prev, next, matrices).next();
                RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, next, matrices), 0xFF00FF00), prev, next, matrices).next();
            }
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class EndPoint {
        public final BlockPos start;
        public final BlockPos end;
        public final Movement movement;
        public final Box box;

        private EndPoint(final BlockPos start, final BlockPos end, final Movement movement) {
            this.start = start;
            this.end = end;
            this.movement = movement;
            box = new Box(end);
        }
    }
}
