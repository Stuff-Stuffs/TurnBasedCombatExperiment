package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;
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
        context.enterSection(getDebugName());
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
        context.pushTranslate(0, height * 2 / 3.0, 0);
        TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, currentTurnState.getTeam().getColour().pack(255), 0, false).draw(width, height / 3.0, currentTurnState.getName().asOrderedText(), context);
        context.popGuiTransform();

        final double percent = battle.getTurnTimerRemaining() / (double) battle.getTurnTimerMax();
        final BossBar.Color color = BossBar.Color.BLUE;
        final GuiRenderMaterial material = GuiRenderMaterialFinder.finder().depthTest(true).ignoreLight(true).ignoreTexture(false).translucent(false).texture(new Identifier("minecraft", "textures/gui/bars.png")).find();
        final GuiQuadEmitter emitter = context.getEmitter();
        final int c = IntRgbColour.WHITE.pack(255);
        emitter.rectangle(0, 0, width, height / 3.0, c, c, c, c);
        emitter.sprite(0, 0, 0);
        emitter.sprite(1, 182 / 256.0F, 0);
        emitter.sprite(2, 182 / 256.0F, (color.ordinal() * 5) / 256.0F);
        emitter.sprite(3, 0, (color.ordinal() * 5) / 256.0F);
        emitter.renderMaterial(material);
        emitter.emit();

        emitter.rectangle(0, 0, width * percent, height, c, c, c, c);
        emitter.depth(-0.01F);
        emitter.sprite(0, 0, 5 / 256.0F);
        emitter.sprite(1, 182 / 256.0F, 5 / 256.0F);
        emitter.sprite(2, 182 / 256.0F, (color.ordinal() * 5 + 5) / 256.0F);
        emitter.sprite(3, 0, (color.ordinal() * 5 + 5) / 256.0F);
        emitter.renderMaterial(material);
        emitter.emit();

        context.exitSection();
    }

    @Override
    public String getDebugName() {
        return "BattleHuiCurrentTurnWidget";
    }
}
