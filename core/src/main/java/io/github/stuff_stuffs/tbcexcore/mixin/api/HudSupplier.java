package io.github.stuff_stuffs.tbcexcore.mixin.api;

import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHud;
import org.jetbrains.annotations.Nullable;

public interface HudSupplier {
    @Nullable BattleHud getBattleHud();
}
