package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;

public class BattleHudCurrentTurnWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width;
    private final double height;
    private final BattleHandle handle;
    private final World world;

    public BattleHudCurrentTurnWidget(WidgetPosition position, double width, double height, BattleHandle handle, World world) {
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
        Battle battle = ((BattleWorldSupplier)world).tbcex_getBattleWorld().getBattle(handle);
        if(battle==null) {
            return;
        }
        final BattleParticipantHandle currentTurn = battle.getState().getCurrentTurn();
        if(currentTurn==null) {
            return;
        }
        BattleParticipantStateView currentTurnState = battle.getState().getParticipant(currentTurn);
        if(currentTurnState==null) {
            throw new RuntimeException();
        }
        render(vertexConsumers -> renderFitText(matrices, currentTurnState.getName(), position.getX(), position.getY(), width, height, false, currentTurnState.getTeam().getColour() | 0xFF000000, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers));
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }
}
