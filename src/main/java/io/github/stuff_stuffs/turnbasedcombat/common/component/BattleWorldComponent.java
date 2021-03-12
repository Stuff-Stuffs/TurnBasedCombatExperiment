package io.github.stuff_stuffs.turnbasedcombat.common.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface BattleWorldComponent extends ComponentV3, CommonTickingComponent {
    @Nullable Battle fromHandle(BattleHandle handle);

    static @Nullable Battle getFromHandle(final BattleHandle handle, final World world) {
        return Components.BATTLE_WORLD_COMPONENT_KEY.get(world).fromHandle(handle);
    }
}
