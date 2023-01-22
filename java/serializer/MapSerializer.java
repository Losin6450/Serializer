package serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MapSerializer implements SerializerAdapter<Map<String, ?>>{
    @Override
    public JsonElement serialize(Serializer serializer, Map<String, ?> object) {
        JsonObject finished = new JsonObject();
        for (String key : object.keySet()){
            finished.add(key, serializer.serialize(object.get(key)));
        }
        return finished;
    }

    @Override
    public Map<String, ?> deserialize(Serializer serializer, JsonElement object) {
        Map<String, Object> map = new HashMap<>();
        for (String key : object.getAsJsonObject().keySet()){
            map.put(key, serializer.deserialize(object.getAsJsonObject().get(key)));
        }
        return map;
    }

    @Override
    public String getClassName() {
        return Map.class.getName();
    }
}
