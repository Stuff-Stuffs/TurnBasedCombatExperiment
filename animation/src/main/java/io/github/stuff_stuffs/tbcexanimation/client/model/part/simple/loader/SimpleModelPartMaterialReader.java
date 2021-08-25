package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.loader;

import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPartMaterial;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

//Based on HavenKing's Myron library
public final class SimpleModelPartMaterialReader {
    private SimpleModelPartMaterialReader() {
    }

    public static List<SimpleModelPartMaterial> read(final BufferedReader reader) throws IOException {
        final List<SimpleModelPartMaterial> materials = new ArrayList<>();
        String currentMtlName = null;
        SimpleModelPartMaterial.RenderType currentMtlRenderType = null;
        Identifier currentMtlTexture = null;
        int currentMtlColour = -1;
        boolean currentMtlEmissive = false;
        String line = reader.readLine();
        while (line != null) {
            final int comment = line.indexOf('#');
            if (comment > 0) {
                line = line.substring(0, comment);
            }
            final StringTokenizer tokenizer = new StringTokenizer(line);
            if (!tokenizer.hasMoreTokens()) {
                continue;
            }
            final String token = tokenizer.nextToken().toLowerCase(Locale.ROOT);
            if (token.equals("newmtl")) {
                if (currentMtlName != null) {
                    materials.add(createMaterial(currentMtlName, currentMtlRenderType, currentMtlTexture, currentMtlColour, currentMtlEmissive));
                }
                currentMtlName = line.substring("newmtl".length()).trim();
            }
            switch (token) {
                case "texture", "Texture", "TEXTURE" -> currentMtlTexture = new Identifier(line.substring(token.length()).trim());
                case "render_type", "Render_Type", "RENDER_TYPE" -> currentMtlRenderType = SimpleModelPartMaterial.RenderType.valueOf(tokenizer.nextToken().toUpperCase(Locale.ROOT));
                case "colour", "Colour", "COLOUR" -> currentMtlColour = parseInt(tokenizer.nextToken(), 16);
                case "emissive", "Emissive", "EMISSIVE" -> currentMtlEmissive = parseBoolean(tokenizer.nextToken());
            }
            line = next(reader);
        }
        if (currentMtlName != null) {
            materials.add(createMaterial(currentMtlName, currentMtlRenderType, currentMtlTexture, currentMtlColour, currentMtlEmissive));
        }
        return materials;
    }

    private static SimpleModelPartMaterial createMaterial(String name, SimpleModelPartMaterial.RenderType renderType, Identifier texture, int colour, boolean emissive) throws IOException {
        if (renderType == null || texture == null) {
            throw new IOException("Missing critical element in material");
        }
        return new SimpleModelPartMaterial(name, renderType, texture, colour, emissive);
    }

    private static String next(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        line = line.trim();
        return line;
    }

    private static int parseInt(String s, final int radix) throws IOException {
        if (radix == 16 && s.startsWith("0x")) {
            s = s.substring(2);
        }
        try {
            return Integer.parseInt(s, radix);
        } catch (final NumberFormatException e) {
            throw new IOException(e);
        }
    }

    private static boolean parseBoolean(final String s) {
        final boolean isTrue = s.equalsIgnoreCase("true");
        if (isTrue) {
            return true;
        } else {
            final boolean isFalse = s.equalsIgnoreCase("false");
            if (isFalse) {
                return false;
            } else {
                throw new RuntimeException();
            }
        }
    }
}
