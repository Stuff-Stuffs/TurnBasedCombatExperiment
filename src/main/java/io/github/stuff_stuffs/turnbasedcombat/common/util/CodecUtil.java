package io.github.stuff_stuffs.turnbasedcombat.common.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.text.Text;

public final class CodecUtil {
    public static final Codec<Text> TEXT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Text, T>> decode(final DynamicOps<T> ops, final T input) {
            final JsonElement jsonElement = ops.convertTo(JsonOps.INSTANCE, input);
            try {
                return DataResult.success(Pair.of(Text.Serializer.fromJson(jsonElement), ops.empty()));
            } catch (final Exception e) {
                return DataResult.error(e.getMessage());
            }
        }

        @Override
        public <T> DataResult<T> encode(final Text input, final DynamicOps<T> ops, final T prefix) {
            if (prefix != null && prefix != ops.empty()) {
                throw new IllegalArgumentException();
            }
            final JsonElement element = Text.Serializer.toJsonTree(input);
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, element));
        }
    };

    private CodecUtil() {
    }
}
