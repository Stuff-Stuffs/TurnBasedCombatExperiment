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
import net.minecraft.world.World;

import java.util.Random;

public class ParticipantOtherStatListWidget extends AbstractParticipantStatListWidget {
    private static final TextDrawer TITLE_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, true);
    private static final TextDrawer INFO_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, NEUTRAL_COLOUR.pack(255), 0, false);

    public ParticipantOtherStatListWidget(final double width, final double height, final double entryHeight, final BattleParticipantHandle handle, final BattleParticipantHandle target, final World world) {
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
                return mouseScrolled(mouse.x, mouse.y, scroll.amount);
            } else if (event instanceof GuiInputContext.KeyPress keyPress) {
                return keyPress(keyPress.keyCode);
            }
            return false;
        });
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return;
        }
        final BattleParticipantStateView curParticipant = battle.getState().getParticipant(handle);
        final BattleParticipantStateView targetParticipant = battle.getState().getParticipant(target);
        if (curParticipant == null || targetParticipant == null) {
            return;
        }
        final ParticipantInfoComponentView curComponent = curParticipant.getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        final ParticipantInfoComponentView targetComponent = targetParticipant.getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (curComponent == null || targetComponent == null) {
            return;
        }
        final double perception = curComponent.getStat(BattleParticipantStat.PERCEPTION_STAT);
        final double targetPerception = targetComponent.getLevel();
        final double effect = calcEffect(targetPerception - perception);
        final Random rangeRandom = new Random(target.participantId().getLeastSignificantBits() ^ target.participantId().getMostSignificantBits());

        final double x = 0;
        final double y = 0;
        context.pushTranslate(0, 0, 2);
        TITLE_DRAWER.draw(width * 0.35, entryHeight, new LiteralText("Stat").asOrderedText(), context);
        context.pushTranslate(x + width * 0.4, y, 0);
        TITLE_DRAWER.draw(width * 0.6, entryHeight, new LiteralText("Possible Range(lo-hi)").asOrderedText(), context);
        context.popGuiTransform();

        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.depth(-1);
        emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR);
        emitter.pos(0, 0, 0);
        emitter.pos(1, 0, (float) entryHeight);
        emitter.pos(2, (float) width, (float) entryHeight);
        emitter.pos(3, (float) width, 0);
        int c = BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR.pack(255);
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

            renderStatEntry(targetComponent.getStat(stat), effect, rangeRandom, stat, context);
            h += entryHeight;
            context.popGuiTransform();
            odd = !odd;
        }
        context.popGuiTransform();
    }

    private void renderStatEntry(final double val, final double rangeSize, final Random rangeFinder, final BattleParticipantStat stat, final GuiContext context) {
        final double roundedRangeSize;
        if (stat == BattleParticipantStat.MAX_HEALTH_STAT) {
            roundedRangeSize = 0;
        } else {
            roundedRangeSize = floorToNearestHundredth(rangeSize);
        }
        final double rangeOffset = roundedRangeSize * rangeFinder.nextDouble();
        final double top = floorToNearestHundredth(val + rangeOffset);
        final double bottom = top - roundedRangeSize;
        final boolean same = floorToNearestHundredth(Math.abs(top - bottom)) == 0;
        MutableText text = new LiteralText("");
        if (bottom < 0) {
            text = text.append("(");
        }
        text = text.append(format(bottom));
        if (bottom < 0) {
            text.append(")");
        }
        if (!same) {
            text = text.append(new LiteralText(" - "));
            if (top < 0) {
                text.append(new LiteralText("("));
            }
            text = text.append(format(top));
            if (top < 0) {
                text.append(new LiteralText(")"));
            }
        }

        INFO_DRAWER.draw(width * 0.35, entryHeight, stat.getName().asOrderedText(), context);
        context.pushTranslate(0.05, 0, 0);
        INFO_DRAWER.draw(width * 0.6, entryHeight, text.asOrderedText(), context);
        context.popGuiTransform();
    }

    private static double calcEffect(double perceptionDelta) {
        perceptionDelta += 5;
        if (perceptionDelta <= 0) {
            return 0;
        } else if (perceptionDelta <= 10) {
            return 2 * Math.log(perceptionDelta);
        } else {
            return 0.5 * perceptionDelta - 0.4;
        }
    }
}
