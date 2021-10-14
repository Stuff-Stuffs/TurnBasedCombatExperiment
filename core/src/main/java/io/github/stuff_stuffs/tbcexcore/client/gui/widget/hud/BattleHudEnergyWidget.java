package io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud;

import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BattleHudEnergyWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width;
    private final double height;
    private final BattleHudContext context;
    private final BattleParticipantHandle handle;
    private final World world;

    public BattleHudEnergyWidget(final WidgetPosition position, final double width, final double height, final BattleHudContext context, final BattleParticipantHandle handle, final World world) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.context = context;
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
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return;
        }
        if (handle.equals(battle.getState().getCurrentTurn())) {
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant == null) {
                return;
            }
            final double percent = Math.min(participant.getEnergy() / participant.getStat(BattleParticipantStat.ENERGY_PER_TURN_STAT), 1);
            final double percentPartial = Math.min(Math.max(participant.getEnergy() - context.getPotentialActionCost(), 0) / participant.getStat(BattleParticipantStat.ENERGY_PER_TURN_STAT), 1);
            final BossBar.Color color;
            if (percent > 3 / 4.0) {
                color = BossBar.Color.BLUE;
            } else if (percent > 2 / 4.0) {
                color = BossBar.Color.GREEN;
            } else if (percent > 1 / 4.0) {
                color = BossBar.Color.YELLOW;
            } else {
                color = BossBar.Color.RED;
            }

            final BossBar.Color colorPartial;
            if (percentPartial > 3 / 4.0) {
                colorPartial = BossBar.Color.BLUE;
            } else if (percentPartial > 2 / 4.0) {
                colorPartial = BossBar.Color.GREEN;
            } else if (percentPartial > 1 / 4.0) {
                colorPartial = BossBar.Color.YELLOW;
            } else {
                colorPartial = BossBar.Color.RED;
            }
            render(vertexConsumers -> {
                final double x = position.getX();
                final double y = position.getY();
                final double z = position.getZ();
                final VertexConsumer opaque = vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(new Identifier("minecraft", "textures/gui/bars.png"), true));
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), 0xFDFFFFFF), 0, (color.ordinal() * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y, z, matrices), 0xFFFFFFFF), 182 / 256.0, (color.ordinal() * 10) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width, y + height, z, matrices), 0xFFFFFFFF), 182 / 256.0, (color.ordinal() * 10 + 5) / 256.0).next();
                RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), 0xFFFFFFFF), 0, (color.ordinal() * 10 + 5) / 256.0).next();
                if (percent != percentPartial) {
                    final double time = (MinecraftClient.getInstance().world.getTime() + delta) / 10.0;
                    final double tweaker = (MathHelper.sin((float) time) + 1) / 2.0;
                    final int tweaked = ClientUtil.tweakComponent(0xFFFFFFFF, 3, tweaker);
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), tweaked), 0, (color.ordinal() * 10 + 5) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y, z, matrices), tweaked), 182 / 256.0 * percent, (color.ordinal() * 10 + 5) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y + height, z, matrices), tweaked), 182 / 256.0 * percent, (color.ordinal() * 10 + 10) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), tweaked), 0, (color.ordinal() * 10 + 10) / 256.0).next();

                    final int tweakedInv = ClientUtil.tweakComponent(0xFFFFFFFF, 3, 1-tweaker);
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), tweakedInv), 0, (colorPartial.ordinal() * 10 + 5) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percentPartial, y, z, matrices), tweakedInv), 182 / 256.0 * percentPartial, (colorPartial.ordinal() * 10 + 5) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percentPartial, y + height, z, matrices), tweakedInv), 182 / 256.0 * percentPartial, (colorPartial.ordinal() * 10 + 10) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), tweakedInv), 0, (colorPartial.ordinal() * 10 + 10) / 256.0).next();
                } else {
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y, z, matrices), 0xFFFFFFFF), 0, (color.ordinal() * 10 + 5) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y, z, matrices), 0xFFFFFFFF), 182 / 256.0 * percent, (color.ordinal() * 10 + 5) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x + width * percent, y + height, z, matrices), 0xFFFFFFFF), 182 / 256.0 * percent, (color.ordinal() * 10 + 10) / 256.0).next();
                    RenderUtil.uv(RenderUtil.colour(RenderUtil.position(opaque, x, y + height, z, matrices), 0xFFFFFFFF), 0, (color.ordinal() * 10 + 10) / 256.0).next();
                }
            });
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }
}
