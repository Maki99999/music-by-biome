package io.github.maki99999.biomebeats.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MusicTrackIdsByConditionIdDeserializer extends JsonDeserializer<Map<String, Collection<String>>> {
    @Override
    public Map<String, Collection<String>> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);

        Map<String, Collection<String>> result = new HashMap<>();

        node.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();
            Set<String> values = mapper.convertValue(valueNode,
                    mapper.getTypeFactory().constructCollectionType(Set.class, String.class));
            result.put(key, values);
        });

        return result;
    }
}
