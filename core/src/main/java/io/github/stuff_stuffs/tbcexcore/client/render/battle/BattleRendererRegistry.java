package io.github.stuff_stuffs.tbcexcore.client.render.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;

public final class BattleRendererRegistry {
    private static final Map<BattleParticipantItemType, BattleParticipantItemRenderer> BATTLE_ITEM_RENDERER_MAP = new Reference2ObjectOpenHashMap<>();

    public static void addItemRenderer(final BattleParticipantItemType type, final BattleParticipantItemRenderer renderer) {
        if (BATTLE_ITEM_RENDERER_MAP.containsKey(type)) {
            //TODO
            throw new RuntimeException();
        }
        BATTLE_ITEM_RENDERER_MAP.put(type, renderer);
    }

    public static BattleParticipantItemRenderer getItemRenderer(final BattleParticipantItemType type) {
         return BATTLE_ITEM_RENDERER_MAP.getOrDefault(type, BattleParticipantItemRenderer.DefaultRenderer.INSTANCE);
    }

    private BattleRendererRegistry() {
    }
}
