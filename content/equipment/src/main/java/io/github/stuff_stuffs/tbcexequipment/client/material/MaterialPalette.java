package io.github.stuff_stuffs.tbcexequipment.client.material;

import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.minecraft.util.Identifier;

import java.util.Locale;

public final class MaterialPalette {
    private final Entry lightOutline;
    private final Entry darkOutline;
    private final Entry white;
    private final Entry brightest;
    private final Entry mid;
    private final Entry bright;
    private final Entry dark;
    private final Entry darkest;

    public MaterialPalette(final Entry lightOutline, final Entry darkOutline, final Entry white, final Entry brightest, Entry mid, final Entry bright, final Entry dark, final Entry darkest) {
        this.lightOutline = lightOutline;
        this.darkOutline = darkOutline;
        this.white = white;
        this.brightest = brightest;
        this.mid = mid;
        this.bright = bright;
        this.dark = dark;
        this.darkest = darkest;
    }

    public Entry getEntry(final EntryType type) {
        return switch (type) {
            case BRIGHT_OUTLINE -> lightOutline;
            case DARK_OUTLINE -> darkOutline;
            case WHITE -> white;
            case BRIGHTEST -> brightest;
            case BRIGHT -> bright;
            case MID -> mid;
            case DARK -> dark;
            case DARKEST -> darkest;
        };
    }

    public Colour getColour(final EntryType type) {
        return getEntry(type).colour();
    }

    public boolean isEmissive(final EntryType type) {
        return getEntry(type).emissive();
    }

    public boolean isTranslucent(final EntryType type) {
        return getEntry(type).alpha() != 255;
    }

    public record Entry(Colour colour, boolean emissive, int alpha) {
    }

    public enum EntryType {
        BRIGHT_OUTLINE,
        DARK_OUTLINE,
        WHITE,
        BRIGHTEST,
        BRIGHT,
        MID,
        DARK,
        DARKEST;

        public Identifier findTexture(final Identifier directory) {
            return new Identifier(directory.getNamespace(), directory.getPath() + '/' + name().toLowerCase(Locale.ROOT));
        }
    }
}
