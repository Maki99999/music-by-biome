package io.github.maki99999.musicbybiome;

import com.cupboard.config.ICommonConfig;
import com.google.gson.JsonObject;

public class Config implements ICommonConfig
{
    public double delayModifier = 0.0;

    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Modifies the delay between songs, lower = shorter delay (1.0 = vanilla). Default = 0.25");
        entry.addProperty("delayModifier", delayModifier);
        root.add("delayModifier", entry);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        delayModifier = data.get("delayModifier").getAsJsonObject().get("delayModifier").getAsDouble();
    }
}
