package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.TargetType;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.widget.*;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.PressableButtonWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.HidingPanel;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.common.MathUtil;
import io.github.stuff_stuffs.tbcexutil.common.OptionalEither;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BattleActionRenderTargetsWidget extends AbstractWidget {
    private static final WidgetPosition ROOT = new BasicWidgetPosition(0, 0, 0);
    private final ParticipantActionInstance actionInstance;
    private final HidingPanel widget;
    private OptionalEither<BlockPos, BattleParticipantHandle> targeted;

    public BattleActionRenderTargetsWidget(final ParticipantActionInstance actionInstance) {
        this.actionInstance = actionInstance;
        targeted = OptionalEither.neither();
        widget = new HidingPanel();
        ParentWidget parentWidget = new BasicPanelWidget(new SuppliedWidgetPosition(() -> -(getScreenWidth()-1)/2, () -> -(getScreenHeight()-1)/2, () -> 1), () -> false, () -> 2, 0.15, 0.125);
        parentWidget.addWidget(new PressableButtonWidget(new SuppliedWidgetPosition(() -> -(getScreenWidth()-1)/2 + 0.025, () -> -(getScreenHeight()-1)/2 + 0.025, () -> 1), () -> 2, () -> true, 0.1, 0.075, () -> new LiteralText("Confirm (Press enter)"), Collections::emptyList, this::activate));
        widget.addWidget(parentWidget);
        widget.resize(getScreenWidth(), getScreenHeight(), getPixelWidth(), getPixelHeight());
    }

    @Override
    public void resize(double width, double height, int pixelWidth, int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        widget.resize(width, height, pixelWidth, pixelHeight);
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return ROOT;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if(widget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            targeted.consume(actionInstance::acceptPosition, actionInstance::acceptParticipant, () -> {
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return widget.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return widget.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final Battle battle = ((BattleWorldSupplier) client.world).tbcex_getBattleWorld().getBattle(((BattleAwareEntity) client.player).tbcex_getCurrentBattle());
        if (battle != null) {
            targeted = OptionalEither.neither();
            final TargetType nextType = actionInstance.getNextType();
            widget.setHidden(!actionInstance.canActivate());
            widget.render(matrices, mouseX, mouseY, delta);
            if (nextType != null) {
                updateTargeted(nextType, battle, delta);
            }

            if (nextType == TargetType.POSITION || nextType == TargetType.ANY) {
                renderBlockTargets(actionInstance);
            }
            if (nextType == TargetType.PARTICIPANT || nextType == TargetType.ANY) {
                renderEntityTargets(actionInstance);
            }
            if (!targeted.isNeither()) {
                renderInfo(matrices, mouseX, mouseY, delta);
            }
        }
    }

    private void renderInfo(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        if (!targeted.isNeither()) {
            final List<TooltipComponent> description = actionInstance.getTargetDescription();
            if (description != null) {
                renderTooltip(matrices, description, mouseX, mouseY);
            }
        }
    }

    private void updateTargeted(final TargetType nextType, final Battle battle, final float delta) {
        final Vec3d startVec = MinecraftClient.getInstance().cameraEntity.getClientCameraPosVec(delta);
        final Vec3d lookVec = ClientUtil.getMouseVector();
        final Vec3d endVec = startVec.add(lookVec.multiply(64));
        final Set<BlockPos> targetPositions = new ObjectOpenHashSet<>();
        if (nextType == TargetType.POSITION || nextType == TargetType.ANY) {
            Iterables.addAll(targetPositions, Objects.requireNonNullElse(actionInstance.getValidTargetPositions(), Collections.emptyList()));
        }

        final List<Pair<BattleParticipantHandle, Box>> targetParticipants = new ReferenceArrayList<>();
        if (nextType == TargetType.PARTICIPANT || nextType == TargetType.ANY) {
            targetParticipants.addAll(StreamSupport.stream(Objects.<Iterable<BattleParticipantHandle>>requireNonNullElse(actionInstance.getValidTargetParticipants(), Collections.emptyList()).spliterator(), false).map(handle -> battle.getState().getParticipant(handle)).filter(Objects::nonNull).map(battleParticipantStateView -> Pair.of(battleParticipantStateView.getHandle(), boxParticipant(battleParticipantStateView))).collect(Collectors.toList()));
        }
        final HitResult hitResult = MathUtil.rayCast(startVec, endVec, blockPos -> {
            if (targetPositions.contains(blockPos)) {
                targeted = OptionalEither.left(blockPos);
                return new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.NORTH, blockPos, false);
            }
            return null;
        });
        double dist;
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            dist = hitResult.getPos().squaredDistanceTo(startVec);
        } else {
            dist = Double.POSITIVE_INFINITY;
        }
        for (final Pair<BattleParticipantHandle, Box> pair : targetParticipants) {
            final Optional<Vec3d> collision = pair.getSecond().raycast(startVec, endVec);
            if (collision.isPresent()) {
                final double v = collision.get().squaredDistanceTo(startVec);
                if (dist > v) {
                    dist = v;
                    targeted = OptionalEither.right(pair.getFirst());
                }
            }
        }
    }

    private static Box boxParticipant(final BattleParticipantStateView view) {
        final BlockPos pos = view.getPos();
        //TODO per participant size
        return new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if(widget.keyPress(keyCode, scanCode, modifiers)) {
            return true;
        } else if((keyCode==GLFW.GLFW_KEY_KP_ENTER||keyCode==GLFW.GLFW_KEY_ENTER)&&actionInstance.canActivate()) {
            activate();
            return true;
        }
        return false;
    }

    private void renderEntityTargets(final ParticipantActionInstance actionInstance) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final Battle battle = ((BattleWorldSupplier) client.world).tbcex_getBattleWorld().getBattle(((BattleAwareEntity) client.player).tbcex_getCurrentBattle());
        final BattleStateView battleState = battle.getState();
        for (final BattleParticipantHandle participant : actionInstance.getValidTargetParticipants()) {
            final BattleParticipantStateView state = battleState.getParticipant(participant);
            if (state != null) {
                boolean target = false;
                final Optional<BattleParticipantHandle> right = targeted.getRight();
                if (right.isPresent()) {
                    if (participant.equals(right.get())) {
                        target = true;
                    }
                }
                final BlockPos position = state.getPos();
                if (!target) {
                    TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(position.getX(), position.getY(), position.getZ(), position.getX() + 1, position.getY() + 2, position.getZ() + 1, 1, 0, 0, 1));
                } else {
                    TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(position.getX(), position.getY(), position.getZ(), position.getX() + 1, position.getY() + 2, position.getZ() + 1, 0, 1, 0, 1));
                }
            }
        }
    }

    private void renderBlockTargets(final ParticipantActionInstance actionInstance) {
        final Optional<BlockPos> target = targeted.getLeft();
        for (final BlockPos position : actionInstance.getValidTargetPositions()) {
            if (target.isPresent() && target.get().equals(position)) {
                TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(position.getX(), position.getY(), position.getZ(), position.getX() + 1, position.getY() + 1, position.getZ() + 1, 0, 1, 0, 1));
                return;
            }
            TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(position.getX(), position.getY(), position.getZ(), position.getX() + 1, position.getY() + 1, position.getZ() + 1, 1, 0, 0, 1));
        }
    }

    private void activate() {
        widget.setHidden(true);
        actionInstance.activate();
        MinecraftClient.getInstance().setScreen(null);
    }
}
