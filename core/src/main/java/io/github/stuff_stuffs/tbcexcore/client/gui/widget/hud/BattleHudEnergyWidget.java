package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
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
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return;
        }
        /*fixme if (handle.equals(battle.getState().getCurrentTurn())) {
            final double percent = Math.min(context.getEnergy() / context.getMaxEnergy(), 1);
            final double percentPartial = Math.min(Math.max(context.getEnergy() - context.getPotentialActionCost(), 0) / context.getMaxEnergy(), 1);
            final Colour colour = new HsvColour((float) MathHelper.lerp(percent, 0, 244), 1, 1);

            final Colour colourPartial = new HsvColour((float) MathHelper.lerp(percentPartial, 0, 244), 1, 1);
            final double x = 0;
            final double y = 0;
            final double z = 0;
            final VertexConsumer opaque = vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(new Identifier("minecraft", "textures/gui/bars.png"), true));

            if (percent != percentPartial) {
                final double time = (MinecraftClient.getInstance().world.getTime() + delta) / 10.0;
                final double tweaker = (MathHelper.sin((float) time) + 1) / 2.0;
                final int tweakedAlpha = (int) Math.round(tweaker * 255);
                final int tweakedAlphaInv = 255 - tweakedAlpha;
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), colour, 255), 0, (6 * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y, z, matrices), colour, 255), 182 / 256.0, (6 * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y + height, z, matrices), colour, 255), 182 / 256.0, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), colour, 255), 0, (6 * 10 + 5) / 256.0).next();

                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), colourPartial, tweakedAlphaInv), 0, (6 * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y, z, matrices), colourPartial, tweakedAlphaInv), 182 / 256.0, (6 * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y + height, z, matrices), colourPartial, tweakedAlphaInv), 182 / 256.0, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), colourPartial, tweakedAlphaInv), 0, (6 * 10 + 5) / 256.0).next();

                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), colour, tweakedAlpha), 0, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y, z, matrices), colour, tweakedAlpha), 182 / 256.0 * percent, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y + height, z, matrices), colour, tweakedAlpha), 182 / 256.0 * percent, (6 * 10 + 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), colour, tweakedAlpha), 0, (6 * 10 + 10) / 256.0).next();


                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), colourPartial, tweakedAlphaInv), 0, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percentPartial, y, z, matrices), colourPartial, tweakedAlphaInv), 182 / 256.0 * percentPartial, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percentPartial, y + height, z, matrices), colourPartial, tweakedAlphaInv), 182 / 256.0 * percentPartial, (6 * 10 + 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), colourPartial, tweakedAlphaInv), 0, (6 * 10 + 10) / 256.0).next();
            } else {
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), colour, 255), 0, (6 * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y, z, matrices), colour, 255), 182 / 256.0, (6 * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y + height, z, matrices), colour, 255), 182 / 256.0, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), colour, 255), 0, (6 * 10 + 5) / 256.0).next();

                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), colour, 255), 0, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y, z, matrices), colour, 255), 182 / 256.0 * percent, (6 * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y + height, z, matrices), colour, 255), 182 / 256.0 * percent, (6 * 10 + 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), colour, 255), 0, (6 * 10 + 10) / 256.0).next();
            }
        }*/
    }
}
