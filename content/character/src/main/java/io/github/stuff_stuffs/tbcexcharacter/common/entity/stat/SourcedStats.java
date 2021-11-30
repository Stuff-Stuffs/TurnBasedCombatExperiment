package io.github.stuff_stuffs.tbcexcharacter.common.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.tbcexcharacter.common.TBCExCharacter;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.Function;

public final class SourcedStats {
    public static final Registry<Type<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(RegistryKey.<Type<?>>ofRegistry(TBCExCharacter.createId("sourced_stat")), Lifecycle.stable())).buildAndRegister();

    public static final class Type<T> {
        private final Codec<T> codec;
        private final Function<T, Text> textExtractor;

        public Type(final Codec<T> codec, final Function<T, Text> textExtractor) {
            this.codec = codec;
            this.textExtractor = textExtractor;
        }

        public Codec<T> getDataCodec() {
            return codec;
        }

        public Text extract(final T val) {
            return textExtractor.apply(val);
        }
    }

    private SourcedStats() {
    }
}
