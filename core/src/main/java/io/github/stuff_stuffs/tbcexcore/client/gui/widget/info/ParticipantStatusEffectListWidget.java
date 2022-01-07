package io.github.stuff_stuffs.tbcexcore.client.gui.widget.info;

import com.google.common.collect.Iterators;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffect;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffectComponentView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffects;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.world.World;

//TODO test :(
public class ParticipantStatusEffectListWidget extends AbstractWidget {
    private static final TextDrawer TEXT_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, true);
    private final double width;
    private final double height;
    private final double entryHeight;
    private final BattleParticipantHandle handle;
    private final World world;
    private double scrollPos;

    public ParticipantStatusEffectListWidget(final double width, final double height, final double entryHeight, final BattleParticipantHandle handle, final World world) {
        this.width = width;
        this.height = height;
        this.entryHeight = entryHeight;
        this.handle = handle;
        this.world = world;
    }

    private double getScrollBarMax() {
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return 0;
        }
        final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
        if (participant == null) {
            return 0;
        }
        final ParticipantStatusEffectComponentView component = participant.getComponent(ParticipantComponents.STATUS_EFFECT_COMPONENT_TYPE.key);
        if (component == null) {
            return 0;
        }
        return Math.max(Iterators.size(component.getActiveStatusEffects().iterator()) * entryHeight - height + entryHeight, 0);
    }

    public void setScrollPos(final double scrollPos) {
        this.scrollPos = Math.min(Math.max(scrollPos, 0), getScrollBarMax());
    }

    private boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (new Rect2d(0, 0, width, height).isIn(mouseX, mouseY)) {
            setScrollPos(scrollPos + amount);
            return true;
        }
        return false;
    }

    @Override
    public void render(final GuiContext context) {
        processEvents(context, event -> {
            if (event instanceof GuiInputContext.MouseDrag drag) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(drag.mouseX, drag.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(drag.mouseX + drag.deltaX, drag.mouseY + drag.deltaY)).subtract(mouse);
                return mouseScrolled(mouse.x, mouse.y, delta.y);
            } else if (event instanceof GuiInputContext.MouseScroll scroll) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY + scroll.amount)).subtract(mouse);
                return mouseScrolled(mouse.x, mouse.y, delta.y);
            }
            return false;
        });
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return;
        }
        final BattleParticipantStateView participantState = battle.getState().getParticipant(handle);
        if (participantState == null) {
            return;
        }
        final ParticipantStatusEffectComponentView component = participantState.getComponent(ParticipantComponents.STATUS_EFFECT_COMPONENT_TYPE.key);
        if (component == null) {
            return;
        }
        final double x = 0;
        final double y = 0;
        final Vec2d mouse = context.transformMouseCursor();
        context.pushScissor(0, 0, (float) width, (float) height);
        double h = -scrollPos;
        boolean odd = false;
        for (final ParticipantStatusEffects.Type type : component.getActiveStatusEffects()) {
            final ParticipantStatusEffect statusEffect = component.getStatusEffect(type);
            if (statusEffect != null) {
                context.pushTranslate(0, h, 1);
                final GuiQuadEmitter emitter = context.getEmitter();
                emitter.pos(0, 0, 0);
                emitter.pos(1, 0, (float) entryHeight);
                emitter.pos(2, (float) width, (float) entryHeight);
                emitter.pos(3, (float) width, 0);
                emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR);
                final int c = (odd ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR).pack(255);
                emitter.colour(c, c, c, c);
                emitter.emit();
                final boolean hovered = new Rect2d(0, h, width, entryHeight + h).isIn(mouse.x, mouse.y);
                renderStatus(statusEffect, hovered, context);
                context.popGuiTransform();
                h += entryHeight;
                odd = !odd;
            }
        }
        context.popGuiTransform();
    }

    private void renderStatus(final ParticipantStatusEffect effect, final boolean hovered, final GuiContext context) {
        final double x = 0;
        final double y = 0;
        context.pushTranslate(0, 0, 1);
        TEXT_DRAWER.draw(width, entryHeight, effect.getName().asOrderedText(), context);
        context.popGuiTransform();
        if (hovered) {
            context.addTooltip(effect.getDescription());
        }
    }
}
