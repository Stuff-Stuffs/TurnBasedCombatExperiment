package io.github.stuff_stuffs.turnbasedcombat.client.screen.battle;

import io.github.stuff_stuffs.turnbasedcombat.client.screen.AbstractBattleScreen;
import io.github.stuff_stuffs.turnbasedcombat.client.util.ClientUtil;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.path.PathNode;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.path.TestPathEnumerator;
import io.github.stuff_stuffs.turnbasedcombat.common.util.MathUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Map;
import java.util.Set;

public class MoveBattleScreen extends AbstractBattleScreen {
    public static final Set<BlockPos> POSITIONS = new ObjectOpenHashSet<>(1024);

    @Override
    public void init(final BattleScreenParent parent) {
        super.init(parent);
        final ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        final Battle battle = Components.BATTLE_WORLD_COMPONENT_KEY.get(playerEntity.world).fromHandle(Components.BATTLE_PLAYER_COMPONENT_KEY.get(playerEntity).getBattleHandle());
        final Map<BlockPos, PathNode> paths = TestPathEnumerator.INSTANCE.getPaths(MinecraftClient.getInstance().world, playerEntity.getBlockPos(), playerEntity, battle.getBounds(), 32);
        POSITIONS.addAll(paths.keySet());
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float tickDelta) {
        super.render(matrices, mouseX, mouseY, tickDelta);
    }

    @Override
    public void mouseClicked(final double mouseX, final double mouseY, final int button) {
        super.mouseClicked(mouseX, mouseY, button);
        final Vec3d mouseDir = ClientUtil.getMouseVector();
        final Vec3d eyePos = MinecraftClient.getInstance().cameraEntity.getCameraPosVec(MinecraftClient.getInstance().getTickDelta());
        final HitResult hitResult = MathUtil.rayCastBlock(eyePos, eyePos.add(mouseDir.multiply(64)), MinecraftClient.getInstance().world, RaycastContext.ShapeType.COLLIDER);
        if (hitResult != null) {
            final BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            MinecraftClient.getInstance().world.setBlockState(blockHitResult.getBlockPos(), Blocks.YELLOW_CONCRETE.getDefaultState());
        } else {
            final BlockPos pos = new BlockPos(eyePos.add(mouseDir.multiply(64)));
            MinecraftClient.getInstance().world.setBlockState(pos, Blocks.YELLOW_CONCRETE.getDefaultState());
        }
    }

    @Override
    public void close() {
        POSITIONS.clear();
    }
}
