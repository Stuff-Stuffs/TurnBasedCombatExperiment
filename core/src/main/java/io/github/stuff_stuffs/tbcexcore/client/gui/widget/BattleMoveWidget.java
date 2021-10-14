package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.tbcexcore.client.gui.BattleMoveScreen;
import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.ParticipantMoveBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.ParticipantPathGatherer;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.path.Movement;
import io.github.stuff_stuffs.tbcexutil.common.path.MovementType;
import io.github.stuff_stuffs.tbcexutil.common.path.Path;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BattleMoveWidget extends AbstractWidget {
    private static final WidgetPosition ROOT = WidgetPosition.of(0, 0, 0);
    private final BattleParticipantHandle handle;
    private final World world;
    private final BattleMoveScreen.PathContext context;
    private final BattleHudContext hudContext;
    private BlockPos lastPos = null;
    private List<Path> paths = null;
    private List<EndPoint> endPoints = null;
    private boolean foundPaths = false;
    private boolean fallDamagePaths = false;

    public BattleMoveWidget(final BattleParticipantHandle handle, final World world, final BattleMoveScreen.PathContext context, final BattleHudContext hudContext) {
        this.handle = handle;
        this.world = world;
        this.context = context;
        this.hudContext = hudContext;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return ROOT;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            final BattleWorld world = ((BattleWorldSupplier) this.world).tbcex_getBattleWorld();
            if (world == null) {
                throw new RuntimeException();
            }
            final Battle battle = world.getBattle(handle.battleId());
            if (battle == null) {
                throw new RuntimeException();
            }
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant != null && foundPaths) {
                final Vec3d mouseVector = ClientUtil.getMouseVector();
                final Vec3d eyePos = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(1);
                final Vec3d endPos = eyePos.add(mouseVector.multiply(64));
                double closestDist = Double.POSITIVE_INFINITY;
                EndPoint closest = null;
                Path closestPath = null;
                for (int i = 0; i < endPoints.size(); i++) {
                    final EndPoint endPoint = endPoints.get(i);
                    final Optional<Vec3d> raycast = endPoint.box.raycast(eyePos, endPos);
                    if (raycast.isPresent()) {
                        final double sq = raycast.get().squaredDistanceTo(eyePos);
                        final Path path = paths.get(i);
                        if (sq < closestDist && path.getCost() <= participant.getEnergy()) {
                            closest = endPoint;
                            closestDist = sq;
                            closestPath = paths.get(i);
                        }
                    }
                }
                if (closest != null) {
                    BattleActionSender.send(handle.battleId(), new ParticipantMoveBattleAction(handle, closestPath));
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        check();
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
        if (!foundPaths) {
            lastPos = participant.getPos();
            foundPaths = true;
            final ParticipantPathGatherer.Builder builder = ParticipantPathGatherer.builder().addMovementTypes(MovementType.LAND);
            if (context.fallDamagePaths) {
                builder.fallDamage(4, 2);
            } else {
                builder.fallDamage(4, Double.POSITIVE_INFINITY);
            }
            paths = builder.build().gather(participant, this.world);
            endPoints = paths.stream().map(p -> {
                final Movement last = p.getMovements().get(p.getMovements().size() - 1);
                return new EndPoint(last.getStartPos(), last.getEndPos(), last);
            }).toList();
        }
        if (foundPaths) {
            final Vec3d mouseVector = ClientUtil.getMouseVector();
            final Vec3d eyePos = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(delta);
            final Vec3d endPos = eyePos.add(mouseVector.multiply(64));
            double closestDist = Double.POSITIVE_INFINITY;
            EndPoint closest = null;
            int index = -1;
            for (int i = 0; i < endPoints.size(); i++) {
                final EndPoint endPoint = endPoints.get(i);
                final Optional<Vec3d> raycast = endPoint.box.raycast(eyePos, endPos);
                if (raycast.isPresent()) {
                    final double sq = raycast.get().squaredDistanceTo(eyePos);
                    final Path path = paths.get(i);
                    if (sq < closestDist && path.getCost() <= participant.getEnergy()) {
                        closest = endPoint;
                        closestDist = sq;
                        index = i;
                    }
                }
            }
            if (closest != null) {
                final double r = 0;
                final double g = 1;
                TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(closest.box, r, g, 0, 1));
                TurnBasedCombatExperimentClient.addRenderPrimitive(renderPath(paths.get(index)));
                hudContext.setPotentialActionCost(paths.get(index).getCost());
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

    private void check() {
        if (context.fallDamagePaths != fallDamagePaths) {
            fallDamagePaths = context.fallDamagePaths;
            foundPaths = false;
        }
        final BattleWorld world = ((BattleWorldSupplier) this.world).tbcex_getBattleWorld();
        if (world != null) {
            final Battle battle = world.getBattle(handle.battleId());
            if (battle != null) {
                final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
                if (participant != null) {
                    if (!participant.getPos().equals(lastPos)) {
                        foundPaths = false;
                    }
                }
                if (!handle.equals(battle.getState().getCurrentTurn())) {
                    MinecraftClient.getInstance().setScreen(null);
                }
            }
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
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
