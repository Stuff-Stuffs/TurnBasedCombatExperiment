package io.github.stuff_stuffs.tbcexcore.client.util;

import io.github.stuff_stuffs.tbcexcore.mixin.impl.AccessorMatrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;

public final class ClientUtil {
    private ClientUtil() {
    }

    public static Vec3d getMouseVector() {
        final MinecraftClient client = MinecraftClient.getInstance();
        final double fov = Math.toRadians(client.options.fov);
        final Vec3f vec3f = new Vec3f((float) (client.getWindow().getFramebufferWidth() / 2d - client.mouse.getX()), (float) (client.getWindow().getFramebufferHeight() / 2d - client.mouse.getY()), (client.getWindow().getFramebufferHeight() / 2f) / ((float) Math.tan(fov / 2d)));
        final Quaternion rotation = client.gameRenderer.getCamera().getRotation();
        vec3f.rotate(rotation);
        vec3f.normalize();
        return new Vec3d(vec3f);
    }

    public static int tweakComponent(final int colour, final int componentIndex, final double factor) {
        assert 0 <= colour && componentIndex < 4;
        final int shift = componentIndex * 8;
        final int mask = 0xFF << shift;
        final int component = (colour & mask) >>> shift;
        final int tweaked = Math.max(Math.min((int) Math.round(component * factor), 255), 0);
        int notComponents = colour & ~mask;
        int shiftTweaked = tweaked << shift;
        return notComponents | shiftTweaked;
    }

    public static void multiply(Matrix4f matrix, MatrixStack matrices) {
        final MatrixStack.Entry peek = matrices.peek();
        Matrix3f rotation = extractRotation(matrix);
        peek.getModel().multiply(matrix);
        peek.getNormal().multiply(rotation);
    }

    public static Matrix3f extractRotation(Matrix4f matrix) {
        Matrix3f rotation = new Matrix3f();
        final AccessorMatrix4f accessor = (AccessorMatrix4f) (Object)matrix;
        final float a00 = accessor.getA00();
        final float a10 = accessor.getA10();
        final float a20 = accessor.getA20();
        float invFirstLength = MathHelper.fastInverseSqrt(a00 * a00 + a10 * a10 + a20 * a20);
        rotation.set(0,0, a00*invFirstLength);
        rotation.set(0,1, a10*invFirstLength);
        rotation.set(0,2, a20*invFirstLength);

        final float a01 = accessor.getA00();
        final float a11 = accessor.getA10();
        final float a21 = accessor.getA20();
        float invSecondLength = MathHelper.fastInverseSqrt(a01 * a01 + a11 * a11 + a21 * a21);
        rotation.set(1,0, a01*invSecondLength);
        rotation.set(1,1, a11*invSecondLength);
        rotation.set(1,2, a21*invSecondLength);

        final float a02 = accessor.getA02();
        final float a12 = accessor.getA12();
        final float a22 = accessor.getA22();
        float invThirdLength = MathHelper.fastInverseSqrt(a02 * a02 + a12 * a12 + a22 * a22);
        rotation.set(2,0, a02*invThirdLength);
        rotation.set(2,1, a12*invThirdLength);
        rotation.set(2,2, a22*invThirdLength);

        return rotation;
    }
}
