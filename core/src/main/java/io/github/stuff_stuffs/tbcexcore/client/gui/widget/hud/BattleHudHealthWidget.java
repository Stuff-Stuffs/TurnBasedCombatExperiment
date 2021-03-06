package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.client.TBCExCoreClient;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.AbstractParticipantStatListWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.HsvColour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Iterator;

public class BattleHudHealthWidget extends AbstractWidget {
    private static final Quaternion FLIP_Z_AXIS = Vec3f.NEGATIVE_Z.getDegreesQuaternion(180);
    private static final Identifier BOSS_BAR_TEXTURE = new Identifier("minecraft", "textures/gui/bars.png");
    private final BattleHandle handle;
    private final World world;

    public BattleHudHealthWidget(final BattleHandle handle, final World world) {
        this.handle = handle;
        this.world = world;
    }

    @Override
    public void render(final GuiContext context) {
        context.enterSection(getDebugName());
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle);
        if (battle == null) {
            return;
        }
        TBCExCoreClient.addRenderPrimitive(ctx -> {
                    final Iterator<BattleParticipantHandle> iterator = battle.getState().getParticipants();
                    while (iterator.hasNext()) {
                        final BattleParticipantHandle next = iterator.next();
                        final BattleParticipantStateView participant = battle.getState().getParticipant(next);
                        if (participant == null) {
                            throw new TBCExException("missing battle participant in battle");
                        }
                        BattleParticipantBounds.Part max = null;
                        for (final BattleParticipantBounds.Part part : participant.getBounds()) {
                            if (max == null || part.box.maxY > max.box.maxY) {
                                max = part;
                            }
                        }
                        if (max == null) {
                            continue;
                        }
                        final Vec3d topCenter = max.box.getCenter().add(0, max.box.getYLength() / 2.0 + 0.5, 0);
                        final double health = participant.getHealth();
                        final double maxHealth = participant.getStat(BattleParticipantStat.MAX_HEALTH_STAT);
                        final double percent;
                        if (maxHealth == 0) {
                            percent = 0;
                        } else {
                            percent = Math.min(health / maxHealth, 1);
                        }
                        final HsvColour colour = new HsvColour((float) MathHelper.lerp(percent, 0, 128), 1, 1);
                        final VertexConsumerProvider vertexConsumers = ctx.consumers();
                        final MatrixStack matrixStack = ctx.matrixStack();
                        final Camera camera = ctx.camera();
                        matrixStack.push();
                        matrixStack.translate(topCenter.x, topCenter.y, topCenter.z);
                        matrixStack.multiply(camera.getRotation());

                        double dist = camera.getPos().squaredDistanceTo(topCenter);
                        dist = 1 / MathHelper.fastInverseSqrt(dist);
                        dist = Math.min(Math.max(dist / 4, 1), 4);

                        final double width = dist;
                        final double height = dist / 8.0;
                        final VertexConsumer posColour = vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_TRANSPARENT_LAYER);
                        RenderUtil.colour(RenderUtil.position(posColour, width * 1.05 / 2.0, 0, 0, matrixStack), IntRgbColour.BLACK, 127).next();
                        RenderUtil.colour(RenderUtil.position(posColour, -width * 1.05 / 2.0, 0, 0, matrixStack), IntRgbColour.BLACK, 127).next();
                        RenderUtil.colour(RenderUtil.position(posColour, -width * 1.05 / 2.0, height * 3, 0, matrixStack), IntRgbColour.BLACK, 127).next();
                        RenderUtil.colour(RenderUtil.position(posColour, width * 1.05 / 2.0, height * 3, 0, matrixStack), IntRgbColour.BLACK, 127).next();

                        final VertexConsumer posColourTex = vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(BOSS_BAR_TEXTURE));
                        matrixStack.translate(-width / 2.0, 0, 0);
                        //background
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, 0, 0, 0, matrixStack), colour, 255), 0, (6 * 10) / 256.0).next();
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, width, 0, 0, matrixStack), colour, 255), 182 / 256.0, (6 * 10) / 256.0).next();
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, width, height, 0, matrixStack), colour, 255), 182 / 256.0, (6 * 10 + 5) / 256.0).next();
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, 0, height, 0, matrixStack), colour, 255), 0, (6 * 10 + 5) / 256.0).next();
                        //foreground
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, 0, 0, 0, matrixStack), colour, 255), 0, (6 * 10 + 5) / 256.0).next();
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, width * percent, 0, 0, matrixStack), colour, 255), 182 / 256.0 * percent, (6 * 10 + 5) / 256.0).next();
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, width * percent, height, 0, matrixStack), colour, 255), 182 / 256.0 * percent, (6 * 10 + 10) / 256.0).next();
                        RenderUtil.uv(RenderUtil.colour(RenderUtil.position(posColourTex, 0, height, 0, matrixStack), colour, 255), 0, (6 * 10 + 10) / 256.0).next();
                        matrixStack.translate(width / 2.0, height, -0.001);
                        matrixStack.multiply(FLIP_Z_AXIS);
                        final Text text = AbstractParticipantStatListWidget.format(health).setStyle(Style.EMPTY).append(new LiteralText("/")).append(AbstractParticipantStatListWidget.format(maxHealth).setStyle(Style.EMPTY));
                        renderFitText(matrixStack, text, -width / 2.0, 0, width, height, true, IntRgbColour.WHITE, 255, vertexConsumers);
                        final Colour teamColour = participant.getTeam().getColour();
                        MutableText name = participant.getName().copy();
                        name = name.append(new LiteralText("("));
                        name = name.append(new LiteralText("" + participant.getLevel()));
                        name = name.append(")");
                        renderFitText(matrixStack, name, -width / 2.0, -height * 2, width, height, false, teamColour, 255, vertexConsumers);
                        MutableText teamText = new LiteralText("(");
                        teamText = teamText.append(new LiteralText(participant.getTeam().teamId()).setStyle(Style.EMPTY.withColor(teamColour.pack())));
                        teamText = teamText.append(")");
                        renderFitText(matrixStack, teamText, -width / 2.0, -height, width, height, false, teamColour, 255, vertexConsumers);
                        matrixStack.pop();
                    }
                }
        );
        context.exitSection();
    }

    @Override
    public String getDebugName() {
        return "BattleHudHealthWidget";
    }

    private void renderFitText(final MatrixStack matrixStack, final Text text, final double x, final double y, final double width, final double height, final boolean shadow, final Colour colour, final int alpha, final VertexConsumerProvider vertexConsumers) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final double yFactor = textRenderer.fontHeight / height;
        final int textWidth = textRenderer.getWidth(text);
        final double xFactor = textWidth / width;
        final double factor = Math.max(xFactor, yFactor);
        matrixStack.push();
        matrixStack.scale(1 / (float) factor, 1 / (float) factor, 1);
        final double center = (x + width / 2.0) * factor;
        if (shadow) {
            textRenderer.drawWithShadow(matrixStack, text, (float) (center - textWidth / 2.0), (float) (y * factor), colour.pack(alpha));
        } else {
            textRenderer.draw(matrixStack, text, (float) (center - textWidth / 2.0), (float) (y * factor), colour.pack(alpha));
        }
        matrixStack.pop();
    }
}
