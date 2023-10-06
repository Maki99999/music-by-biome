package io.github.maki99999.musicbybiome;

import com.cupboard.config.ICommonConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config implements ICommonConfig {
    public static final List<String> tags = List.of(
            "is_hot",
            "is_hot/overworld",
            "is_hot/nether",
            "is_hot/end",
            "is_cold",
            "is_cold/overworld",
            "is_cold/nether",
            "is_cold/end",
            "is_sparse",
            "is_sparse/overworld",
            "is_sparse/nether",
            "is_sparse/end",
            "is_dense",
            "is_dense/overworld",
            "is_dense/nether",
            "is_dense/end",
            "is_wet",
            "is_wet/overworld",
            "is_wet/nether",
            "is_wet/end",
            "is_dry",
            "is_dry/overworld",
            "is_dry/nether",
            "is_dry/end",
            "is_coniferous",
            "is_spooky",
            "is_dead",
            "is_lush",
            "is_mushroom",
            "is_magical",
            "is_rare",
            "is_plateau",
            "is_modified",
            "is_water",
            "is_desert",
            "is_plains",
            "is_swamp",
            "is_sandy",
            "is_snowy",
            "is_wasteland",
            "is_void",
            "is_underground",
            "is_cave",
            "is_peak",
            "is_slope",
            "is_mountain"
            );
    public Map<String, List<String>> biomeTagStrings = new HashMap<>();

    public JsonObject serialize() {
        final JsonObject root = new JsonObject();

        for (var tag: tags) {
            JsonObject tagObject = new JsonObject();
            tagObject.addProperty("desc", tag); // Use the tag name as the description
            tagObject.add("songs", new JsonArray());

            root.add(tag, tagObject);
        }

        return root;
    }

    public void deserialize(JsonObject data) {
        for (String tag : tags) {
            if (data.has(tag) && data.get(tag).isJsonObject()) {
                JsonObject tagObject = data.getAsJsonObject(tag);
                JsonArray tagArray = tagObject.getAsJsonArray("songs");

                List<String> tagStrings = new ArrayList<>();
                if (tagArray != null) {
                    for (JsonElement element : tagArray) {
                        if (element.isJsonPrimitive()) {
                            tagStrings.add(element.getAsString());
                        }
                    }
                }

                biomeTagStrings.put(tag, tagStrings);
            }
        }
    }
}
