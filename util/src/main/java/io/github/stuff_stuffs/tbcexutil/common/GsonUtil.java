package io.github.stuff_stuffs.tbcexutil.common;

import com.google.gson.*;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Type;

public final class GsonUtil {
    private GsonUtil() {
    }

    public static final class Vec3dJson implements JsonSerializer<Vec3d>, JsonDeserializer<Vec3d> {
        @Override
        public Vec3d deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            return new Vec3d(object.getAsJsonPrimitive("x").getAsDouble(), object.getAsJsonPrimitive("y").getAsDouble(), object.getAsJsonPrimitive("z").getAsDouble());
        }

        @Override
        public JsonElement serialize(final Vec3d src, final Type typeOfSrc, final JsonSerializationContext context) {
            final JsonObject dst = new JsonObject();
            dst.addProperty("x", src.x);
            dst.addProperty("y", src.y);
            dst.addProperty("z", src.z);
            return dst;
        }
    }

    public static final class DoubleQuaternionJson implements JsonDeserializer<DoubleQuaternion> {
        private final boolean degrees;

        public DoubleQuaternionJson(boolean degrees) {
            this.degrees = degrees;
        }

        @Override
        public DoubleQuaternion deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            return new DoubleQuaternion(object.getAsJsonPrimitive("x").getAsDouble(), object.getAsJsonPrimitive("y").getAsDouble(), object.getAsJsonPrimitive("z").getAsDouble(), degrees);
        }
    }
}
