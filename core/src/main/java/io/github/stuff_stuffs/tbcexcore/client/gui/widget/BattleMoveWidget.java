package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexcore.client.TBCExCoreClient;
import io.github.stuff_stuffs.tbcexcore.client.gui.BattleMoveScreen;
import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.ParticipantMoveBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattlePath;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.ParticipantPathGatherer;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.FloatRgbColour;
import io.github.stuff_stuffs.tbcexutil.common.colour.HsvColour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import io.github.stuff_stuffs.tbcexutil.common.path.Movement;
import io.github.stuff_stuffs.tbcexutil.common.path.MovementType;
import io.github.stuff_stuffs.tbcexutil.common.path.Path;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BattleMoveWidget extends AbstractWidget {
    public static final Colour PATH_COLOUR = new IntRgbColour(0, 255, 0);
    private static final Quaternion[] QUATERNION_DIRECTIONS = Util.make(new Quaternion[6], arr -> {
        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            arr[i] = directions[i].getRotationQuaternion();
        }
    });

    private final BattleParticipantHandle handle;
    private final World world;
    private final BattleMoveScreen.PathContext context;
    private final BattleHudContext hudContext;
    private final VertexBuffer vertexBuffer;
    private BlockPos lastPos = null;
    private List<BattlePath> paths = null;
    private List<EndPoint> endPoints = null;
    private boolean foundPaths = false;
    private boolean fallDamagePaths = false;

    public BattleMoveWidget(final BattleParticipantHandle handle, final World world, final BattleMoveScreen.PathContext context, final BattleHudContext hudContext) {
        this.handle = handle;
        this.world = world;
        this.context = context;
        this.hudContext = hudContext;
        vertexBuffer = new VertexBuffer();
    }

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
                BattlePath closestPath = null;
                for (int i = 0; i < endPoints.size(); i++) {
                    final EndPoint endPoint = endPoints.get(i);
                    final Optional<Vec3d> raycast = endPoint.box.raycast(eyePos, endPos);
                    if (raycast.isPresent()) {
                        final double sq = raycast.get().squaredDistanceTo(eyePos);
                        final BattlePath path = paths.get(i);
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
    public void render(final GuiContext guiContext) {
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
        processEvents(guiContext, event -> {
            if (event instanceof GuiInputContext.MouseClick click) {
                final Vec2d mouse = guiContext.transformMouseCursor(new Vec2d(click.mouseX, click.mouseY));
                return mouseClicked(mouse.x, mouse.y, click.button);
            }
            return false;
        });
        if (!foundPaths) {
            lastPos = participant.getPos();
            foundPaths = true;
            final ParticipantPathGatherer.Builder builder = ParticipantPathGatherer.builder().addMovementTypes(MovementType.LAND);
            if (context.fallDamagePaths) {
                builder.fallDamage(4, 2);
            } else {
                builder.fallDamage(4, Double.POSITIVE_INFINITY);
            }
            paths = builder.build(ParticipantPathGatherer.DEFAULT).gather(participant, this.world);
            endPoints = paths.stream().map(p -> {
                final Movement last = p.getPath().getMovements().get(p.getPath().getMovements().size() - 1);
                return new EndPoint(last.getStartPos(), last.getEndPos(), last);
            }).toList();
            final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            final MatrixStack matrixStack = new MatrixStack();
            for (int i = 0; i < endPoints.size(); i++) {
                final EndPoint endPoint = endPoints.get(i);
                final double cost = paths.get(i).getCost();
                if (cost > hudContext.getEnergy()) {
                    continue;
                }
                final float percent = (float) Math.min(Math.max(hudContext.getEnergy() - cost, 0) / hudContext.getMaxEnergy(), 1);
                final FloatRgbColour colour = new FloatRgbColour(new HsvColour(MathHelper.lerp(percent, 0, 244), 1, 1).pack(255));
                matrixStack.push();
                final Vec3d center = endPoint.box.getCenter();
                matrixStack.translate(center.x, center.y, center.z);
                for (final Quaternion quaternionDirection : QUATERNION_DIRECTIONS) {
                    matrixStack.push();
                    matrixStack.multiply(quaternionDirection);
                    matrixStack.translate(0, 0.25, 0);
                    final Matrix4f model = matrixStack.peek().getPositionMatrix();
                    bufferBuilder.vertex(model, -0.25f, 0, -0.25f).color(colour.r, colour.g, colour.b, 0.25f).next();
                    bufferBuilder.vertex(model, -0.25f, 0, 0.25f).color(colour.r, colour.g, colour.b, 0.25f).next();
                    bufferBuilder.vertex(model, 0.25f, 0, 0.25f).color(colour.r, colour.g, colour.b, 0.25f).next();
                    bufferBuilder.vertex(model, 0.25f, 0, -0.25f).color(colour.r, colour.g, colour.b, 0.25f).next();
                    matrixStack.pop();
                }
                matrixStack.pop();
            }
            bufferBuilder.end();
            vertexBuffer.submitUpload(bufferBuilder);
        }
        if (foundPaths) {
            final Vec3d mouseVector = ClientUtil.getMouseVector();
            final Vec3d eyePos = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(guiContext.getTickDelta());
            final Vec3d endPos = eyePos.add(mouseVector.multiply(64));
            double closestDist = Double.POSITIVE_INFINITY;
            EndPoint closest = null;
            int index = -1;
            for (int i = 0; i < endPoints.size(); i++) {
                final EndPoint endPoint = endPoints.get(i);
                final Optional<Vec3d> raycast = endPoint.box.raycast(eyePos, endPos);
                if (raycast.isPresent()) {
                    final double sq = raycast.get().squaredDistanceTo(eyePos);
                    final BattlePath path = paths.get(i);
                    if (sq < closestDist && path.getCost() <= participant.getEnergy()) {
                        closest = endPoint;
                        closestDist = sq;
                        index = i;
                    }
                }
            }
            if (closest != null) {
                final BattlePath path = paths.get(index);
                TBCExCoreClient.addRenderPrimitive(renderPath(path.getPath()));
                TBCExCoreClient.addBoxInfo(new BoxInfo(closest.box, 0, 1, 0, 1));
                hudContext.setPotentialActionCost(path.getCost());
            }
            TBCExCoreClient.addRenderPrimitive(context -> {
                if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                    MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer().beginWrite(false);
                }
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.enableCull();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                vertexBuffer.setShader(context.matrixStack().peek().getPositionMatrix(), context.projectionMatrix(), GameRenderer.getPositionColorShader());
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableCull();
                if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                    MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
                }
            });
        }
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

    private static Consumer<WorldRenderContext> renderPath(final Path path) {
        return context -> {
            final MatrixStack matrices = context.matrixStack();
            final VertexConsumer vertexConsumer = context.consumers().getBuffer(RenderLayer.LINES);
            for (final Movement movement : path.getMovements()) {
                final Vec3d start = Vec3d.ofCenter(movement.getStartPos());
                Vec3d prev = start;
                for (int i = 0; i < 7; i++) {
                    final Vec3d next = movement.interpolate(start, movement.getLength() * i / 8.0);
                    RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, prev, matrices), PATH_COLOUR, 255), prev, next, matrices).next();
                    RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, next, matrices), PATH_COLOUR, 255), prev, next, matrices).next();
                    prev = next;
                }
                final Vec3d next = movement.interpolate(start, movement.getLength());
                RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, prev, matrices), PATH_COLOUR, 255), prev, next, matrices).next();
                RenderUtil.lineNormal(RenderUtil.colour(RenderUtil.position(vertexConsumer, next, matrices), PATH_COLOUR, 255), prev, next, matrices).next();
            }
        };
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
