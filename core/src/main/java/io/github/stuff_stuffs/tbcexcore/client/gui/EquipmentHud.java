package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.SingleHotbarSlotWidget;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleParticipantItemRenderer;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleRendererRegistry;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class EquipmentHud {
    private final BattleParticipantHandle handle;
    private final List<Slot> slots;

    public EquipmentHud(final BattleParticipantHandle handle) {
        if (handle.isUniversal()) {
            throw new IllegalArgumentException();
        }
        this.handle = handle;
        slots = new ArrayList<>((int) BattleEquipmentSlot.REGISTRY.stream().count());
        for (final BattleEquipmentSlot equipmentSlot : BattleEquipmentSlot.REGISTRY) {
            final BattleRendererRegistry.EquipmentSlotInfo info = BattleRendererRegistry.getEquipmentSlotInfo(equipmentSlot);
            final Slot slot = new Slot(equipmentSlot, info.x, info.y);
            slots.add(slot);
        }
    }

    public void render(final MatrixStack matrices, final float tickDelta) {
        for (final Slot slot : slots) {
            slot.widget.render(matrices, -1, -1, tickDelta);
        }
    }

    public void tick(Battle battle) {
        for (Slot slot : slots) {
            slot.tick(battle);
        }
    }

    private final class Slot {
        private final BattleEquipmentSlot slot;
        private final SingleHotbarSlotWidget widget;
        private boolean selected;
        private final MutableObject<@Nullable BattleParticipantItemStack> stackHolder;

        private Slot(final BattleEquipmentSlot slot, final double x, final double y) {
            this.slot = slot;
            stackHolder = new MutableObject<>(null);
            widget = new SingleHotbarSlotWidget(WidgetPosition.of(x, y, 0), 1 / 16d, () -> 1, () -> selected, new SingleHotbarSlotWidget.Handler() {
            }, (matrices, mouseX, mouseY, delta) -> {
                final VertexConsumerProvider.Immediate vertexConsumers = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                final BattleParticipantItemStack stack = stackHolder.getValue();
                if (stack != null) {
                    matrices.push();
                    matrices.translate(x, y, 0);
                    matrices.scale(1 / 16f, 1 / 16f, 1);
                    final BattleParticipantItemRenderer renderer = BattleRendererRegistry.getRenderer(stack.getItem().getType());
                    renderer.render(stack, matrices, vertexConsumers, delta);
                    matrices.pop();
                }
            });
        }

        public void tick(Battle battle) {
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant != null) {
                stackHolder.setValue(participant.getEquipmentStack(slot));
            } else {
                stackHolder.setValue(null);
            }
        }

        public void setSelected(final boolean selected) {
            this.selected = selected;
        }
    }
}
