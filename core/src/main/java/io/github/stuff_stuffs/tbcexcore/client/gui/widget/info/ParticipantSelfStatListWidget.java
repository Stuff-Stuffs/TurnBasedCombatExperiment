package io.github.stuff_stuffs.tbcexcore.client.gui.widget.info;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantInfoComponentView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR;

//TODO Stat icons
//TODO Scrollbar
public class ParticipantSelfStatListWidget extends AbstractParticipantStatListWidget {
    private static final TextDrawer TEXT_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, NEUTRAL_COLOUR.pack(255), 0, false);

    public ParticipantSelfStatListWidget(final double width, final double height, final double entryHeight, final BattleParticipantHandle handle, final BattleParticipantHandle target, final World world) {
        super(width, height, entryHeight, handle, target, world);
    }

    @Override
    public void render(final GuiContext context) {
        processEvents(context, event -> {
            if (event instanceof GuiInputContext.MouseDrag drag) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(drag.mouseX, drag.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(drag.mouseX + drag.deltaX, drag.mouseY + drag.deltaY)).subtract(mouse);
                return mouseDragged(mouse.x, mouse.y, delta.y);
            } else if (event instanceof GuiInputContext.MouseScroll scroll) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY + scroll.amount)).subtract(mouse);
                return mouseScrolled(mouse.x, mouse.y, delta.y);
            } else if (event instanceof GuiInputContext.KeyPress keyPress) {
                return keyPress(keyPress.keyCode);
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
        final ParticipantInfoComponentView component = participantState.getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            return;
        }
        context.pushTranslate(0, 0, 2);
        TEXT_DRAWER.draw(width * 0.45, entryHeight, new LiteralText("Stat").asOrderedText(), context);
        context.pushTranslate(0.5, 0, 0);
        TEXT_DRAWER.draw(width * 0.45, entryHeight, new LiteralText("Value(Base+Bonus)").asOrderedText(), context);
        context.popGuiTransform();
        context.popGuiTransform();
        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR);
        emitter.pos(0, 0, 0);
        emitter.pos(1, 0, (float) entryHeight);
        emitter.pos(2, (float) width, (float) entryHeight);
        emitter.pos(3, (float) width, 0);
        emitter.depth(-1);
        int c = FIRST_BACKGROUND_COLOUR.pack(255);
        emitter.colour(c, c, c, c);
        emitter.emit();
        context.pushScissor(0, (float) entryHeight, (float) width, (float) (height - entryHeight));
        double h = -getScrollPos() + entryHeight;
        boolean odd = false;
        for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
            context.pushTranslate(0, h, 10);

            emitter.pos(0, 0, 0);
            emitter.pos(1, 0, (float) entryHeight);
            emitter.pos(2, (float) width, (float) entryHeight);
            emitter.pos(4, (float) width, 0);
            c = (odd ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR).pack(255);
            emitter.colour(c, c, c, c);
            emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR);
            emitter.emit();
            renderStatEntry(component.getRawStat(stat), component.getStat(stat), stat, context);
            h += entryHeight;
            context.popGuiTransform();
            odd = !odd;
        }
        context.popGuiTransform();
    }

    private void renderStatEntry(double val, double raw, final BattleParticipantStat stat, final GuiContext context) {
        val = floorToNearestHundredth(val);
        raw = floorToNearestHundredth(raw);
        final double diff = val - raw;
        final Text valText = format(val);
        final Text rawText = format(raw);
        final Text diffText = format(diff);
        MutableText text = new LiteralText("");
        text = text.append(valText);
        text = text.append("(");
        text = text.append(rawText);
        if (diff >= 0) {
            text = text.append("+");
        }
        text = text.append(diffText);
        text = text.append(")");
        context.pushTranslate(0, 0, 1);
        TEXT_DRAWER.draw(width * 0.45, entryHeight, stat.getName().asOrderedText(), context);
        context.pushTranslate(0, 0.5, 0);
        TEXT_DRAWER.draw(width * 0.5, entryHeight, text.asOrderedText(), context);
        context.popGuiTransform();
        context.popGuiTransform();
    }
}
