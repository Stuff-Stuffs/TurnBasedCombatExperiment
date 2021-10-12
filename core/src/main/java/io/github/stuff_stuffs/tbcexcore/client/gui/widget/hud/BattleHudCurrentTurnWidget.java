package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BattleHudCurrentTurnWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width;
    private final double height;
    private final BattleHandle handle;
    private final World world;

    public BattleHudCurrentTurnWidget(final WidgetPosition position, final double width, final double height, final BattleHandle handle, final World world) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.handle = handle;
        this.world = world;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
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
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle);
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
            renderFitText(matrices, currentTurnState.getName(), position.getX(), position.getY() + height * 2 / 3.0, width, height / 3.0, false, currentTurnState.getTeam().getColour() | 0xFF000000, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers);

            final double percent = battle.getTurnTimerRemaining() / (double) battle.getTurnTimerMax();
            final double x = position.getX();
            final double y = position.getY();
            final double z = position.getZ();
            final BossBar.Color color = BossBar.Color.BLUE;
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(new Identifier("minecraft", "textures/gui/bars.png")));
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y, z, matrices), 0xFFFFFFFF), 0, 0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width, y, z, matrices), 0xFFFFFFFF), 182 / 256.0, 0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width, y + height / 3.0, z, matrices), 0xFFFFFFFF), 182 / 256.0, (color.ordinal() * 5) / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y + height / 3.0, z, matrices), 0xFFFFFFFF), 0, (color.ordinal() * 5) / 256.0).next();

            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y, z, matrices), 0xFFFFFFFF), 0, 5 / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width*percent, y, z, matrices), 0xFFFFFFFF), 182 / 256.0 * percent, 5 / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x + width*percent, y + height / 3.0, z, matrices), 0xFFFFFFFF), 182 / 256.0 * percent, (color.ordinal() * 5 + 5) / 256.0).next();
            RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, x, y + height / 3.0, z, matrices), 0xFFFFFFFF), 0, (color.ordinal() * 5 + 5) / 256.0).next();
        });
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }
}
