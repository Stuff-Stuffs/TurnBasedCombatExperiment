package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleLog;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class ClientBattleLog implements BattleLog {
    private final List<BattleAction> actions;
    private ClientBattleImpl battle;

    public ClientBattleLog() {
        actions = new ObjectArrayList<>();
    }

    public void setBattle(final ClientBattleImpl battle) {
        if(this.battle!=null) {
            throw new RuntimeException();
        }
        this.battle = battle;
        for (BattleAction action : actions) {
            action.apply(battle, battle.getWorld());
        }
    }

    @Override
    public void push(final BattleAction battleAction) {
        actions.add(battleAction);
        if (battle != null) {
            battleAction.apply(battle, battle.getWorld());
        }
    }

    @Override
    public int size() {
        return actions.size();
    }

    @Override
    public BattleAction getAction(final int index) {
        return actions.get(index);
    }
}
