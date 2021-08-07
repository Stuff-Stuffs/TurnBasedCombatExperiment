package io.github.stuff_stuffs.tbcexcore.client.render.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;

public final class BattleRendererRegistry {
    private static final Map<BattleParticipantItemType, BattleParticipantItemRenderer> BATTLE_ITEM_RENDERER_MAP = new Reference2ObjectOpenHashMap<>();
    private static final Map<BattleEquipmentSlot, EquipmentSlotInfo> EQUIPMENT_INFO_MAP = new Reference2ObjectOpenHashMap<>();

    public static void putBattleItemRenderer(final BattleParticipantItemType type, final BattleParticipantItemRenderer renderer) {
        if (BATTLE_ITEM_RENDERER_MAP.containsKey(type)) {
            //TODO
            throw new RuntimeException();
        }
        BATTLE_ITEM_RENDERER_MAP.put(type, renderer);
    }

    public static void putEquipmentInfo(final BattleEquipmentSlot slot, final double x, final double y) {
        if (EQUIPMENT_INFO_MAP.containsKey(slot)) {
            //TODO
            throw new RuntimeException();
        }
        EQUIPMENT_INFO_MAP.put(slot, new EquipmentSlotInfo(x, y));
    }

    public static BattleParticipantItemRenderer getRenderer(final BattleParticipantItemType type) {
        final BattleParticipantItemRenderer renderer = BATTLE_ITEM_RENDERER_MAP.get(type);
        if (renderer == null) {
            //TODO
            throw new RuntimeException();
        }
        return renderer;
    }

    public static EquipmentSlotInfo getEquipmentSlotInfo(BattleEquipmentSlot slot) {
        final EquipmentSlotInfo info = EQUIPMENT_INFO_MAP.get(slot);
        if (info == null) {
            //TODO
            throw new RuntimeException();
        }
        return info;
    }

    private BattleRendererRegistry() {
    }

    public final static class EquipmentSlotInfo {
        public final double x;
        public final double y;

        private EquipmentSlotInfo(final double x, final double y) {
            this.x = x;
            this.y = y;
        }
    }
}
