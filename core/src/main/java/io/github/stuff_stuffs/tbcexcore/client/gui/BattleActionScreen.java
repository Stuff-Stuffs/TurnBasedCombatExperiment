package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.TargetType;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;

public class BattleActionScreen extends TBCExScreen {
    private final Screen prevScreen;
    private final ParticipantActionInstance actionInstance;

    public BattleActionScreen(final Screen prevScreen, final ParticipantActionInstance actionInstance) {
        super(new LiteralText("battle_action_screen"), new RootPanelWidget());
        this.prevScreen = prevScreen;
        this.actionInstance = actionInstance;
        passEvents = true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
    }

    private void renderEntityTargets(final ParticipantActionInstance actionInstance) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final Battle battle = ((BattleWorldSupplier) client.world).tbcex_getBattleWorld().getBattle(((BattleAwareEntity) client.player).tbcex_getCurrentBattle());
        final BattleStateView battleState = battle.getState();
        for (final BattleParticipantHandle participant : actionInstance.getValidTargetParticipants()) {
            final BlockPos position = battleState.getParticipant(participant).getPos();
            TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(position.getX(), position.getY(), position.getZ(), position.getX() + 1, position.getY() + 1, position.getZ() + 1, 0, 0, 1, 1));
        }
    }

    private void renderBlockTargets(final ParticipantActionInstance actionInstance) {
        for (final BlockPos position : actionInstance.getValidTargetPositions()) {
            TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(position.getX(), position.getY(), position.getZ(), position.getX() + 1, position.getY() + 1, position.getZ() + 1, 0, 0, 1, 1));
        }
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        final TargetType nextType = actionInstance.getNextType();
        if (nextType == TargetType.POSITION || nextType == TargetType.ANY) {
            renderBlockTargets(actionInstance);
        }
        if (nextType == TargetType.PARTICIPANT || nextType == TargetType.ANY) {
            renderEntityTargets(actionInstance);
        }
    }

    @Override
    public void onClose() {
        client.setScreen(prevScreen);
    }

    public static void open(final ParticipantActionInstance actionInstance) {
        final MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new BattleActionScreen(client.currentScreen, actionInstance));
    }
}
