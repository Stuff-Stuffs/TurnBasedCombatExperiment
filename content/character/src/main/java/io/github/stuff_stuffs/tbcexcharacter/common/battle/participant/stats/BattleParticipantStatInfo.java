package io.github.stuff_stuffs.tbcexcharacter.common.battle.participant.stats;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;

public final class BattleParticipantStatInfo {
    private static final Map<BattleParticipantStat, BattleParticipantStatInfo> INFO_MAP = new Reference2ObjectOpenHashMap<>();
    private static final BattleParticipantStatInfo DEFAULT = new BattleParticipantStatInfo(true, true, 5);
    private final boolean purchasable;
    private final boolean visible;
    private final double base;

    private BattleParticipantStatInfo(final boolean purchasable, boolean visible, double base) {
        this.purchasable = purchasable;
        this.visible = visible;
        this.base = base;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public boolean isVisible() {
        return visible;
    }

    public static void register(final BattleParticipantStat stat, final boolean purchasable, boolean visible, double base) {
        if (INFO_MAP.put(stat, new BattleParticipantStatInfo(purchasable, visible, base)) != null) {
            throw new TBCExException("Tried to register stat info twice");
        }
    }

    public static BattleParticipantStatInfo get(final BattleParticipantStat stat) {
        return INFO_MAP.getOrDefault(stat, DEFAULT);
    }

    public double getBase() {
        return base;
    }
}
