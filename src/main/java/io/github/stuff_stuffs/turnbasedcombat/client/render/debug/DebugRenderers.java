package io.github.stuff_stuffs.turnbasedcombat.client.render.debug;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import java.util.Collection;
import java.util.Map;

public final class DebugRenderers {
    private static final Map<String, Pair<DebugRender, Stage>> DEBUG_RENDERS_UNINIT = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, Pair<DebugRender, Stage>> DEBUG_RENDERS = new Object2ReferenceOpenHashMap<>();
    private static final Object2BooleanMap<String> TOGGLES = new Object2BooleanOpenHashMap<>();

    private DebugRenderers() {
    }

    public static void init() {
        for (final Map.Entry<String, Pair<DebugRender, Stage>> entry : DEBUG_RENDERS_UNINIT.entrySet()) {
            if(DEBUG_RENDERS.put(entry.getKey(), entry.getValue())!=null) {
                throw new RuntimeException("Duplicate debug renderers");
            }
            TOGGLES.put(entry.getKey(), false);
            final String name = entry.getKey();
            final DebugRender debugRender = entry.getValue().getFirst();
            switch (entry.getValue().getSecond()) {
                case POST_ENTITY -> WorldRenderEvents.AFTER_ENTITIES.register(context -> {
                    if (TOGGLES.getBoolean(name)) {
                        debugRender.render(context);
                    }
                });
                case POST_TRANSLUCENT -> WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
                    if (TOGGLES.getBoolean(name)) {
                        debugRender.render(context);
                    }
                });
                case PRE_DEBUG -> WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
                    if (TOGGLES.getBoolean(name)) {
                        debugRender.render(context);
                    }
                });
            }
        }
        DEBUG_RENDERS_UNINIT.clear();
    }

    public static void register(final String name, final DebugRender debugRender, final Stage stage) {
        if (DEBUG_RENDERS_UNINIT.put(name, new Pair<>(debugRender, stage)) != null || DEBUG_RENDERS.containsKey(name)) {
            throw new RuntimeException("Duplicate named debug renderers");
        }
    }

    public static boolean contains(final String s) {
        return TOGGLES.containsKey(s) || DEBUG_RENDERS_UNINIT.containsKey(s);
    }

    public static void set(final String renderer, final boolean on) {
        if(DEBUG_RENDERS_UNINIT.containsKey(renderer)) {
            init();
        }
        TOGGLES.put(renderer, on);
    }

    public static Collection<String> getKeys() {
        return TOGGLES.keySet();
    }


    public enum Stage {
        POST_ENTITY,
        POST_TRANSLUCENT,
        PRE_DEBUG
    }
}
