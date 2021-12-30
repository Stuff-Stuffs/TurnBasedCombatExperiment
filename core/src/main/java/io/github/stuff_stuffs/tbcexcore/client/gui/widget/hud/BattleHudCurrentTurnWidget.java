package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import net.minecraft.world.World;

public class BattleHudCurrentTurnWidget extends AbstractWidget {
    private final double width;
    private final double height;
    private final BattleHandle handle;
    private final World world;

    public BattleHudCurrentTurnWidget(final double width, final double height, final BattleHandle handle, final World world) {
        this.width = width;
        this.height = height;
        this.handle = handle;
        this.world = world;
    }

    @Override
    public void render(final GuiContext context) {
        /* fixme final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle);
        if (battle == null) {
            return;
        }
        final BattleParticipantHandle currentTurn = battle.getState().getCurrentTurn();
        if (currentTurn == null) {
            return;
        }
        final BattleParticipantStateView currentTurnState = battle.getState().getParticipant(currentTurn);
        if (currentTurnState == null) {
            throw new RuntimeException();
        }
        render(vertexConsumers -> {
            renderFitText(matrices, currentTurnState.getName(), position.getX(), position.getY() + height * 2 / 3.0, width, height / 3.0, false, currentTurnState.getTeam().getColour(), 255, IntRgbColour.BLACK, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers);

            final double percent = battle.getTurnTimerRemaining() / (double) battle.getTurnTimerMax();
            final double x = position.getX();
            final double y = position.getY();
            final double z = position.getZ();
            final BossBar.Color color = BossBar.Color.BLUE;
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(new Identifier("minecraft", "textures/gui/bars.png")));
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y, z, matrices), IntRgbColour.WHITE, 255), 0, 0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width, y, z, matrices), IntRgbColour.WHITE, 255), 182 / 256.0, 0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width, y + height / 3.0, z, matrices), IntRgbColour.WHITE, 255), 182 / 256.0, (color.ordinal() * 5) / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y + height / 3.0, z, matrices), IntRgbColour.WHITE, 255), 0, (color.ordinal() * 5) / 256.0).next();

            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y, z, matrices), IntRgbColour.WHITE, 255), 0, 5 / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width * percent, y, z, matrices), IntRgbColour.WHITE, 255), 182 / 256.0 * percent, 5 / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width * percent, y + height / 3.0, z, matrices), IntRgbColour.WHITE, 255), 182 / 256.0 * percent, (color.ordinal() * 5 + 5) / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y + height / 3.0, z, matrices), IntRgbColour.WHITE, 255), 0, (color.ordinal() * 5 + 5) / 256.0).next();
        });*/
    }
}
