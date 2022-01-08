package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.HsvColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BattleHudEnergyWidget extends AbstractWidget {
    private final double width;
    private final double height;
    private final BattleHudContext context;
    private final BattleParticipantHandle handle;
    private final World world;

    public BattleHudEnergyWidget(final double width, final double height, final BattleHudContext context, final BattleParticipantHandle handle, final World world) {
        this.width = width;
        this.height = height;
        this.context = context;
        this.handle = handle;
        this.world = world;
    }

    @Override
    public void render(final GuiContext guiContext) {
        guiContext.enterSection(getDebugName());
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return;
        }
        if (handle.equals(battle.getState().getCurrentTurn())) {
            final double percent = Math.min(context.getEnergy() / context.getMaxEnergy(), 1);
            final double percentPartial = Math.min(Math.max(context.getEnergy() - context.getPotentialActionCost(), 0) / context.getMaxEnergy(), 1);
            final Colour colour = new HsvColour((float) MathHelper.lerp(percent, 0, 244), 1, 1);
            int c = colour.pack(255);

            final Colour colourPartial = new HsvColour((float) MathHelper.lerp(percentPartial, 0, 244), 1, 1);
            final GuiRenderMaterial material = GuiRenderMaterialFinder.finder().depthTest(true).translucent(true).ignoreLight(true).ignoreTexture(false).texture(new Identifier("minecraft", "textures/gui/bars.png")).find();

            final GuiQuadEmitter emitter = guiContext.getEmitter();
            if (percent != percentPartial) {
                final double time = (MinecraftClient.getInstance().world.getTime() + guiContext.getTickDelta()) / 10.0;
                final double tweaker = (MathHelper.sin((float) time) + 1) / 2.0;
                final int tweakedAlpha = (int) Math.round(tweaker * 255);
                final int tweakedAlphaInv = 255 - tweakedAlpha;
                c = colourPartial.pack(tweakedAlphaInv);
                emitter.rectangle(0, 0, width, height, c, c, c, c);
                emitter.sprite(0, 0, (6 * 10) / 256.0F);
                emitter.sprite(1, 182 / 256.0F, (6 * 10) / 256.0F);
                emitter.sprite(2, 182 / 256.0F, (6 * 10 + 5) / 256.0F);
                emitter.sprite(3, 0, (6 * 10 + 5) / 256.0F);
                emitter.renderMaterial(material);
                emitter.emit();

                emitter.rectangle(0, 0, width, height, c, c, c, c);
                emitter.sprite(0, 0, (6 * 10) / 256.0F);
                emitter.sprite(1, 182 / 256.0F, (6 * 10) / 256.0F);
                emitter.sprite(2, 182 / 256.0F, (6 * 10 + 5) / 256.0F);
                emitter.sprite(3, 0, (6 * 10 + 5) / 256.0F);
                emitter.renderMaterial(material);
                emitter.emit();

                c = colour.pack(tweakedAlpha);

                emitter.rectangle(0, 0, width * percent, height, c, c, c, c);
                emitter.sprite(0, 0, (6 * 10) / 256.0F);
                emitter.sprite(1, 182 / 256.0F * (float) percent, (6 * 10) / 256.0F);
                emitter.sprite(2, 182 / 256.0F * (float) percent, (6 * 10 + 5) / 256.0F);
                emitter.sprite(3, 0, (6 * 10 + 5) / 256.0F);
                emitter.renderMaterial(material);
                emitter.depth(-0.01F);
                emitter.emit();

                emitter.rectangle(0, 0, width * percent, height, c, c, c, c);
                emitter.sprite(0, 0, (6 * 10) / 256.0F);
                emitter.sprite(1, 182 / 256.0F * (float) percent, (6 * 10) / 256.0F);
                emitter.sprite(2, 182 / 256.0F * (float) percent, (6 * 10 + 5) / 256.0F);
                emitter.sprite(3, 0, (6 * 10 + 5) / 256.0F);
                emitter.renderMaterial(material);
                emitter.depth(-0.01F);
                emitter.emit();
            } else {
                emitter.rectangle(0, 0, width, height, c, c, c, c);
                emitter.sprite(0, 0, (6 * 10) / 256.0F);
                emitter.sprite(1, 182 / 256.0F, (6 * 10) / 256.0F);
                emitter.sprite(2, 182 / 256.0F, (6 * 10 + 5) / 256.0F);
                emitter.sprite(3, 0, (6 * 10 + 5) / 256.0F);
                emitter.renderMaterial(material);
                emitter.emit();

                emitter.rectangle(0, 0, width * percent, height, c, c, c, c);
                emitter.sprite(0, 0, (6 * 10 + 5) / 256.0F);
                emitter.sprite(1, 182 / 256.0F * (float) percent, (6 * 10 + 5) / 256.0F);
                emitter.sprite(2, 182 / 256.0F * (float) percent, (6 * 10 + 10) / 256.0F);
                emitter.sprite(3, 0, (6 * 10 + 10) / 256.0F);
                emitter.renderMaterial(material);
                emitter.depth(-0.01F);
                emitter.emit();
            }
        }
        guiContext.exitSection();
    }

    @Override
    public String getDebugName() {
        return "BattleHudEnergyWidget";
    }
}
