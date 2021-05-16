package io.github.stuff_stuffs.turnbasedcombat.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public final class ClientUtil {
    public static Vec3d getMouseVector() {
        final MinecraftClient client = MinecraftClient.getInstance();
        final double fov = Math.toRadians(client.options.fov);
        final Vec3f vec3f = new Vec3f((float) (client.getWindow().getFramebufferWidth() / 2d - client.mouse.getX()), (float) (client.getWindow().getFramebufferHeight() / 2d - client.mouse.getY()), (client.getWindow().getFramebufferHeight() / 2f) / ((float) Math.tan(fov / 2d)));
        final Quaternion rotation = client.gameRenderer.getCamera().getRotation();
        vec3f.rotate(rotation);
        vec3f.normalize();
        return new Vec3d(vec3f);
    }

    private ClientUtil() {
    }
}
