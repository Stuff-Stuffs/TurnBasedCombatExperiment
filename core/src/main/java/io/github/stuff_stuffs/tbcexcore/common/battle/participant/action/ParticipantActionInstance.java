package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ParticipantActionInstance {
    private final ParticipantActionInfo info;
    private final BattleStateView battleState;
    private final BattleParticipantHandle user;
    private final List<TargetInstance> list;
    private TargetType nextTargetType;

    public ParticipantActionInstance(final ParticipantActionInfo info, final BattleStateView battleState, final BattleParticipantHandle user) {
        this.info = info;
        this.battleState = battleState;
        this.user = user;
        list = new ArrayList<>();
        update();
    }

    public BattleParticipantHandle getUser() {
        return user;
    }

    public @Nullable TargetType getNextType() {
        return nextTargetType;
    }

    public boolean canActivate() {
        return info.canActivate(battleState, user, list);
    }

    public void activate() {
        if (info.canActivate(battleState, user, list)) {
            info.activate(battleState, user, list);
        } else {
            throw new RuntimeException();
        }
    }

    public List<TooltipComponent> getTargetDescription() {
        return info.getDescription(battleState, user, list);
    }

    public void accept(final TargetInstance instance) {
        if (instance.getType().equals(nextTargetType)) {
            list.add(instance);
            update();
        } else {
            throw new TBCExException("target instance type does not match expected");
        }
    }

    private void update() {
        nextTargetType = info.getNextTargetType(list);
    }

    public void render(@Nullable final TargetInstance targeted, final float tickDelta) {
        final Set<TargetType> types = new ObjectOpenHashSet<>();
        for (final TargetInstance instance : list) {
            types.add(instance.getType());
        }
        if (targeted != null) {
            types.add(targeted.getType());
        }
        for (final TargetType type : types) {
            type.render(targeted, list, user, battleState, tickDelta);
        }
        if (nextTargetType != null) {
            nextTargetType.render(targeted, list, user, battleState, tickDelta);
        }
    }
}
