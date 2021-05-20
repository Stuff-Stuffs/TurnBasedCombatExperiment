package io.github.stuff_stuffs.turnbasedcombat.common.battle.data;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipant;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface BattleWorld {
    @Nullable Battle getBattle(BattleHandle handle);

    BattleParticipant create(Text name, Team team);
}
