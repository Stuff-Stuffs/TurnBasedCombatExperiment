package io.github.stuff_stuffs.tbcexcharacter.common.battle.participant.stats;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;

public final class BattleParticipantStatInfo {
    private static final Map<BattleParticipantStat, BattleParticipantStatInfo> INFO_MAP = new Reference2ObjectOpenHashMap<>();
    private static final BattleParticipantStatInfo DEFAULT = new BattleParticipantStatInfo(true, 5, 1, 1);
    private final boolean purchasable;
    private final double base;
    private final double cost;
    private final double purchaseIncrement;

    private BattleParticipantStatInfo(final boolean purchasable, final double base, final double cost, final double purchaseIncrement) {
        this.purchasable = purchasable;
        this.base = base;
        this.cost = cost;
        this.purchaseIncrement = purchaseIncrement;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public double getCost() {
        return cost;
    }

    public double getPurchaseIncrement() {
        return purchaseIncrement;
    }

    public static void register(final BattleParticipantStat stat, final boolean purchasable, final double base, final double cost, final double purchaseIncrement) {
        if (INFO_MAP.put(stat, new BattleParticipantStatInfo(purchasable, base, cost, purchaseIncrement)) != null) {
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
