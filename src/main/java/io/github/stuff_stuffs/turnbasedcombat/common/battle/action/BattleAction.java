package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

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
    private static final Map<Class<? extends BattleAction>, Decoder<?>> DECODERS;
    protected final BattleParticipantHandle handle;

    protected BattleAction(final BattleParticipantHandle handle) {
        this.handle = handle;
    }

    public BattleParticipantHandle getHandle() {
        return handle;
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
        return DECODERS.get(BATTLE_ACTION_CLASSES.get(name)).decode(data, ops);
    }

    public static <T extends BattleAction> void register(final Class<T> action, final Decoder<T> decoder) {
        if (!BATTLE_ACTION_CLASSES.containsValue(action)) {
            BATTLE_ACTION_CLASSES.put(action.getSimpleName(), action);
            DECODERS.put(action, decoder);
        }
    }

    public interface Decoder<Type extends BattleAction> {
        <T> Type decode(T o, DynamicOps<T> ops);
    }

    static {
        BATTLE_ACTION_CLASSES = new Object2ReferenceOpenHashMap<>();
        DECODERS = new Reference2ReferenceOpenHashMap<>();
    }
}
