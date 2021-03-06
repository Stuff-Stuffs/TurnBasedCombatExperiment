package io.github.stuff_stuffs.tbcexcore.client.gui.widget.info;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;

public abstract class AbstractParticipantStatListWidget extends AbstractWidget {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###0.##");
    public static final double MAX_DISPLAYABLE = 9999.99;
    public static final Colour POSITIVE_COLOUR = new IntRgbColour(0, 255, 0);
    public static final Colour NEUTRAL_COLOUR = new IntRgbColour(200, 200, 200);
    public static final Colour NEGATIVE_COLOUR = new IntRgbColour(255, 0, 0);

    protected final double width;
    protected final double height;
    protected final double entryHeight;
    protected final BattleParticipantHandle handle;
    protected final BattleParticipantHandle target;
    protected final World world;
    private double scrollPos;

    public AbstractParticipantStatListWidget(final double width, final double height, final double entryHeight, final BattleParticipantHandle handle, final BattleParticipantHandle target, final World world) {
        this.width = width;
        this.height = height;
        this.entryHeight = entryHeight;
        this.handle = handle;
        this.target = target;
        this.world = world;
    }

    private double getScrollBarMax() {
        return Math.max(BattleParticipantStat.REGISTRY.getIds().size() * entryHeight - height + entryHeight, 0);
    }

    public double getScrollPos() {
        return scrollPos;
    }

    public void setScrollPos(final double scrollPos) {
        this.scrollPos = Math.min(Math.max(scrollPos, 0), getScrollBarMax());
    }

    protected boolean mouseDragged(final double mouseX, final double mouseY, final double deltaY) {
        if (new Rect2d(0, 0, width, height).isIn(mouseX, mouseY)) {
            setScrollPos(scrollPos + deltaY);
            return true;
        }
        return false;
    }

    protected boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (new Rect2d(0, 0, width, height).isIn(mouseX, mouseY)) {
            setScrollPos(scrollPos + amount);
            return true;
        }
        return false;
    }

    protected boolean keyPress(final int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_UP) {
            setScrollPos(scrollPos - entryHeight);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            setScrollPos(scrollPos + entryHeight);
            return true;
        }
        return false;
    }

    public static MutableText format(double val) {
        val = floorToNearestHundredth(val);
        final String formatted;
        if (val > MAX_DISPLAYABLE) {
            formatted = "\u221E";
        } else if (-val > MAX_DISPLAYABLE) {
            formatted = "-\u221E";
        } else {
            formatted = DECIMAL_FORMAT.format(val);
        }
        return new LiteralText(formatted).setStyle(Style.EMPTY.withColor(val == 0 ? NEUTRAL_COLOUR.pack() : val < 0 ? NEGATIVE_COLOUR.pack() : POSITIVE_COLOUR.pack()));
    }

    public static double floorToNearestHundredth(double val) {
        val *= 100;
        val = Math.floor(val);
        return val / 100;
    }
}
