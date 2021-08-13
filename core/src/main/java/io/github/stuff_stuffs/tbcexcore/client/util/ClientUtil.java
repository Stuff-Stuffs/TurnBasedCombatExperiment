package io.github.stuff_stuffs.tbcexcore.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

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
}
