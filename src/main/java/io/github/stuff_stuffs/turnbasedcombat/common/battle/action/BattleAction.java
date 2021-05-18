package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public abstract class BattleAction {
    public static final Codec<BattleAction> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<BattleAction, T>> decode(final DynamicOps<T> ops, final T input) {
            return DataResult.success(Pair.of(deserialize(input, ops), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final BattleAction input, final DynamicOps<T> ops, final T prefix) {
            if (!ops.empty().equals(prefix) && prefix != null) {
                throw new RuntimeException();
            }
            return DataResult.success(input.encode(ops));
        }
    };
    private static final Map<String, Class<? extends BattleAction>> BATTLE_ACTION_CLASSES;
    private static final Map<Class<? extends BattleAction>, Method> DECODERS;

    protected BattleAction() {
    }

    public abstract void applyToState(BattleState state);

    public final <T> T serialize(final DynamicOps<T> ops) {
        final Map<T, T> map = new Object2ObjectArrayMap<>();
        final String name = getClass().getSimpleName();
        map.put(ops.createString("id"), ops.createString(name));
        map.put(ops.createString("data"), encode(ops));
        return ops.createMap(map);
    }

    protected abstract <T> T encode(DynamicOps<T> ops);

    //public static <T> BattleAction decode(T o, DynamicOps<T> ops)

    public static <T> BattleAction deserialize(final T o, final DynamicOps<T> ops) {
        final MapLike<T> mapLike = ops.getMap(o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });

        final String name = ops.getStringValue(mapLike.get("id")).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
        final T data = mapLike.get("data");
        try {
            return (BattleAction) DECODERS.get(BATTLE_ACTION_CLASSES.get(name)).invoke(null, data, ops);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register(final Class<? extends BattleAction> action) throws NoSuchMethodException, IllegalAccessException {
        if (!BATTLE_ACTION_CLASSES.containsValue(action)) {
            BATTLE_ACTION_CLASSES.put(action.getSimpleName(), action);
            final Method decode = action.getMethod("decode", Object.class, DynamicOps.class);
            if (!Modifier.isStatic(decode.getModifiers())) {
                throw new RuntimeException();
            }
            DECODERS.put(action, decode);
        }
    }

    static {
        BATTLE_ACTION_CLASSES = new Object2ReferenceOpenHashMap<>();
        DECODERS = new Reference2ReferenceOpenHashMap<>();
    }
}
