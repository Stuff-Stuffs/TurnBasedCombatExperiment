package io.github.stuff_stuffs.tbcexcore.mixin.api;

import io.github.stuff_stuffs.tbcexcore.client.gui.BattleHud;
import org.jetbrains.annotations.Nullable;

public interface HudSupplier {
    @Nullable BattleHud getBattleHud();
}
