package serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class ListSerializer implements SerializerAdapter<List<?>>{
    @Override
    public JsonElement serialize(Serializer serializer, List<?> object) {
        JsonArray finished = new JsonArray();
        for (Object o : object){
            finished.add(serializer.serialize(o));
        }
        return finished;
    }

    @Override
    public List<?> deserialize(Serializer serializer, JsonElement object) {
        List<Object> finished = new ArrayList<>();
        for (JsonElement element : object.getAsJsonArray().asList()){
            finished.add(serializer.deserialize(element));
        }
        return finished;
    }

    @Override
    public String getClassName() {
        return List.class.getName();
    }
}
