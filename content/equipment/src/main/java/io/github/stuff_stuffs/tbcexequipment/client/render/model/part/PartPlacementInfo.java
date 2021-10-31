package io.github.stuff_stuffs.tbcexequipment.client.render.model.part;

import com.google.gson.*;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexutil.common.Matrix3d;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.lang.reflect.Type;

public final class PartPlacementInfo implements RenderContext.QuadTransform {
    public static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(PartPlacementInfo.class, new Deserializer()).create();
    public static final PartPlacementInfo DEFAULT = new PartPlacementInfo(Vec3d.ZERO, DoubleQuaternion.IDENTITY, new Vec3d(1, 1, 1), Vec3d.ZERO, DoubleQuaternion.IDENTITY, new Vec3d(1, 1, 1));
    private final Vec3d offset;
    private final Matrix3d rotation;
    private final Vec3d scale;
    private final Vec3d offsetSecond;
    private final Matrix3d rotationSecond;
    private final Vec3d scaleSecond;
    private final Vec3f vec = new Vec3f();

    public PartPlacementInfo(final Vec3d offset, final DoubleQuaternion rotation, final Vec3d scale, final Vec3d offsetSecond, final DoubleQuaternion rotationSecond, final Vec3d scaleSecond) {
        this.offset = offset;
        this.rotation = new Matrix3d(rotation);
        this.scale = scale;
        this.offsetSecond = offsetSecond;
        this.rotationSecond = new Matrix3d(rotationSecond);
        this.scaleSecond = scaleSecond;
    }

    public void transform(final Vec3f vec) {
        double x = vec.getX();
        double y = vec.getY();
        double z = vec.getZ();
        x += offset.x;
        y += offset.y;
        z += offset.z;
        double rotX = rotation.transformX(x, y, z);
        double rotY = rotation.transformY(x, y, z);
        double rotZ = rotation.transformZ(x, y, z);
        x = rotX;
        y = rotY;
        z = rotZ;
        x *= scale.x;
        y *= scale.y;
        z *= scale.z;
        x += offsetSecond.x;
        y += offsetSecond.y;
        z += offsetSecond.z;
        rotX = rotationSecond.transformX(x, y, z);
        rotY = rotationSecond.transformY(x, y, z);
        rotZ = rotationSecond.transformZ(x, y, z);
        x = rotX;
        y = rotY;
        z = rotZ;
        x *= scaleSecond.x;
        y *= scaleSecond.y;
        z *= scaleSecond.z;
        vec.set((float) x, (float) y, (float) z);
    }

    public Vec3d transformDir(final double x, final double y, final double z) {
        return rotationSecond.transform(rotation.transform(x, y, z));
    }

    @Override
    public boolean transform(final MutableQuadView quad) {
        for (int i = 0; i < 4; i++) {
            quad.copyPos(i, vec);
            transform(vec);
            quad.pos(i, vec);
        }
        return true;
    }

    public static final class Deserializer implements JsonDeserializer<PartPlacementInfo> {
        @Override
        public PartPlacementInfo deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final Vec3d offset;
            if (object.has("offset")) {
                final JsonObject obj = object.getAsJsonObject("offset");
                offset = new Vec3d(obj.getAsJsonPrimitive("x").getAsDouble(), obj.getAsJsonPrimitive("y").getAsDouble(), obj.getAsJsonPrimitive("z").getAsDouble());
            } else {
                offset = Vec3d.ZERO;
            }
            final Vec3d offsetSecond;
            if (object.has("offsetSecond")) {
                final JsonObject obj = object.getAsJsonObject("offsetSecond");
                offsetSecond = new Vec3d(obj.getAsJsonPrimitive("x").getAsDouble(), obj.getAsJsonPrimitive("y").getAsDouble(), obj.getAsJsonPrimitive("z").getAsDouble());
            } else {
                offsetSecond = Vec3d.ZERO;
            }

            final Vec3d scale;
            if (object.has("scale")) {
                final JsonObject obj = object.getAsJsonObject("scale");
                scale = new Vec3d(obj.getAsJsonPrimitive("x").getAsDouble(), obj.getAsJsonPrimitive("y").getAsDouble(), obj.getAsJsonPrimitive("z").getAsDouble());
            } else {
                scale = new Vec3d(1, 1, 1);
            }
            final Vec3d scaleSecond;
            if (object.has("scaleSecond")) {
                final JsonObject obj = object.getAsJsonObject("scaleSecond");
                scaleSecond = new Vec3d(obj.getAsJsonPrimitive("x").getAsDouble(), obj.getAsJsonPrimitive("y").getAsDouble(), obj.getAsJsonPrimitive("z").getAsDouble());
            } else {
                scaleSecond = new Vec3d(1, 1, 1);
            }

            final DoubleQuaternion rotation;
            if (object.has("rotation")) {
                final JsonObject obj = object.getAsJsonObject("rotation");
                rotation = new DoubleQuaternion(obj.getAsJsonPrimitive("roll").getAsDouble(), obj.getAsJsonPrimitive("pitch").getAsDouble(), obj.getAsJsonPrimitive("yaw").getAsDouble(), true);
            } else {
                rotation = DoubleQuaternion.IDENTITY;
            }
            final DoubleQuaternion rotationSecond;
            if (object.has("rotationSecond")) {
                final JsonObject obj = object.getAsJsonObject("rotationSecond");
                rotationSecond = new DoubleQuaternion(obj.getAsJsonPrimitive("roll").getAsDouble(), obj.getAsJsonPrimitive("pitch").getAsDouble(), obj.getAsJsonPrimitive("yaw").getAsDouble(), true);
            } else {
                rotationSecond = DoubleQuaternion.IDENTITY;
            }

            return new PartPlacementInfo(offset, rotation, scale, offsetSecond, rotationSecond, scaleSecond);
        }
    }
}
