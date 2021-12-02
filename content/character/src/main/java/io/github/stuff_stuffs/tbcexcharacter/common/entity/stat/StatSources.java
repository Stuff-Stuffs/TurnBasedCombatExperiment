package io.github.stuff_stuffs.tbcexcharacter.common.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.tbcexcharacter.common.TBCExCharacter;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.Function;

public final class StatSources {
    public static final Registry<Type<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(RegistryKey.<Type<?>>ofRegistry(TBCExCharacter.createId("sourced_stat")), Lifecycle.stable())).buildAndRegister();
    public static final Type<Unit> BASE_TYPE = new Type<>(Codec.unit(() -> Unit.INSTANCE), l -> new LiteralText("Base Stat"));
    public static final Type<Unit> PURCHASED_TYPE = new Type<>(Codec.unit(() -> Unit.INSTANCE), l -> new LiteralText("Purchased Stat"));

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

    public static void init() {
        Registry.register(REGISTRY, TBCExCharacter.createId("base"), BASE_TYPE);
        Registry.register(REGISTRY, TBCExCharacter.createId("purchased"), PURCHASED_TYPE);
    }

    private StatSources() {
    }

    public interface ForEach {
        void accept(StatSource<?> stat, boolean isLast);
    }
}
