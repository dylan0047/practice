package com.conaxgames.practice.util.adapter;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationTypeAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

    public Location deserialize(JsonElement element, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = element.getAsJsonObject();

        World world = Bukkit.getWorld(jsonObject.get("world").getAsString());
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();

        return new Location(world, x, y, z, yaw, pitch);
    }

    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        if (location == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", location.getWorld().getName());
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        jsonObject.addProperty("yaw", location.getYaw());
        jsonObject.addProperty("pitch", location.getPitch());
        return jsonObject;
    }
}
