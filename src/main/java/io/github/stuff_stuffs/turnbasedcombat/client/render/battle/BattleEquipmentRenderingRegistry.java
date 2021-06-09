package io.github.stuff_stuffs.turnbasedcombat.client.render.battle;

import io.github.stuff_stuffs.turnbasedcombat.client.render.SpriteLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class BattleEquipmentRenderingRegistry {
    private static final Map<BattleEquipmentType, TypeInfo> TYPE_INFO_MAP = new Reference2ObjectOpenHashMap<>();
    private static final TypeInfo MISSING = new TypeInfo(SpriteLike.MISSING, BattleEquipmentRenderer.MISSING);

    private BattleEquipmentRenderingRegistry() {
    }

    public static SpriteLike getBackgroundSprite(final BattleEquipmentType type) {
        final TypeInfo info = TYPE_INFO_MAP.get(type);
        return Objects.requireNonNullElse(info, MISSING).sprite.get();
    }

    public static BattleEquipmentRenderer getRenderer(final BattleEquipmentType type) {
        final TypeInfo info = TYPE_INFO_MAP.get(type);
        return Objects.requireNonNullElse(info, MISSING).renderer;
    }

    private static class TypeInfo {
        private final Supplier<SpriteLike> sprite;
        private final BattleEquipmentRenderer renderer;

        private TypeInfo(final Supplier<SpriteLike> sprite, final BattleEquipmentRenderer renderer) {
            this.sprite = sprite;
            this.renderer = renderer;
        }
    }

    private static class SlotInfo {
        //TODO conflict resolution
        private final int slotX, slotY;

        private SlotInfo(final int slotX, final int slotY) {
            this.slotX = slotX;
            this.slotY = slotY;
        }
    }
}
