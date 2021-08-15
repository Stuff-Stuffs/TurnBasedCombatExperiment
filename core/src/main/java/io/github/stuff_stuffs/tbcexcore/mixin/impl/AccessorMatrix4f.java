package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
public interface AccessorMatrix4f {
    @Accessor(value = "a00")
    float getA00();

    @Accessor(value = "a01")
    float getA01();

    @Accessor(value = "a02")
    float getA02();

    @Accessor(value = "a03")
    float getA03();

    @Accessor(value = "a10")
    float getA10();

    @Accessor(value = "a11")
    float getA11();

    @Accessor(value = "a12")
    float getA12();

    @Accessor(value = "a13")
    float getA13();

    @Accessor(value = "a20")
    float getA20();

    @Accessor(value = "a21")
    float getA21();

    @Accessor(value = "a22")
    float getA22();

    @Accessor(value = "a23")
    float getA23();

    @Accessor(value = "a30")
    float getA30();

    @Accessor(value = "a31")
    float getA31();

    @Accessor(value = "a32")
    float getA32();

    @Accessor(value = "a33")
    float getA33();
}
