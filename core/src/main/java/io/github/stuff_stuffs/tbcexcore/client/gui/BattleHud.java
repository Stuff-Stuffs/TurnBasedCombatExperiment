package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public final class BattleHud {
    private final BattleHandle handle;
    private final PlayerEntity entity;

    public BattleHud(final BattleHandle handle, final PlayerEntity entity) {
        this.handle = handle;
        this.entity = entity;
    }

    public boolean matches(final BattleHandle handle) {
        return this.handle.equals(handle);
    }

    public void render(final MatrixStack matrices, final float tickDelta) {
        final float width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        final float height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        matrices.push();
        matrices.scale(width, height, 1);
        if (width > height) {
            matrices.scale(height / width, 1, 1);
            matrices.translate((width / (double) height - 1) / 2d, 0, 0);
        } else if (width < height) {
            matrices.scale(1, width / height, 1);
            matrices.translate(0, (height / (double) width - 1) / 2d, 0);
        }
        matrices.pop();
    }

    public void tick() {
        final Battle battle = ((BattleWorldSupplier) entity.world).tbcex_getBattleWorld().getBattle(handle);
        if (battle != null) {
        }
    }
}
