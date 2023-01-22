package serializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Serializer {

    private SerializerAdapter<Object> defaultAdapter = null;
    private final List<SerializerAdapter<Object>> serializerAdapters;
    private final boolean serializeNulls;
    private Gson gson;
    public final String CLASS_PROPERTY = "??class??";

    public Serializer(boolean useDCA, List<SerializerAdapter<Object>> serializers, boolean serializeNulls){
        this.serializerAdapters = serializers;
        this.serializeNulls = serializeNulls;
        if(useDCA) this.defaultAdapter = new DefaultObjectSerializer();

    }


    public JsonElement serialize(Object object){
        if(object instanceof String) return new JsonPrimitive((String) object);
        if(object instanceof Number) return new JsonPrimitive((Number) object);
        if(object instanceof Boolean) return new JsonPrimitive((Boolean) object);
        if(object instanceof Character) return new JsonPrimitive((Character) object);
        if(object instanceof List) return new ListSerializer().serialize(this, (List<?>) object);
        if(object instanceof Map) return new MapSerializer().serialize(this, (Map<String, ?>) object);
        if(object == null){
            if(serializeNulls){
                return JsonNull.INSTANCE;
            } else {
                return null;
            }
        }
        for(SerializerAdapter<Object> serializer : serializerAdapters){
            if(object.getClass().getName().equals(serializer.getClassName())){
                return serializer.serialize(this, object);
            }
        }
        if(defaultAdapter != null) return defaultAdapter.serialize(this, object);
        return null;
    }

    public Object deserialize(JsonElement element){
        if(element.isJsonPrimitive()){
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if(primitive.isString()) return primitive.getAsString();
            if(primitive.isNumber()) return primitive.getAsNumber();
            if(primitive.isBoolean()) return primitive.getAsBoolean();
            if(primitive.isJsonNull()) return null;
        }
        if(element.isJsonObject()){
            if(element.getAsJsonObject().has(CLASS_PROPERTY)){
                for(SerializerAdapter<Object> serializer : serializerAdapters) {
                    if (element.getAsJsonObject().get(CLASS_PROPERTY).getAsString().equals(serializer.getClassName())) {
                        return serializer.deserialize(this, element);
                    }
                }
                if(defaultAdapter != null){
                    return defaultAdapter.deserialize(this, element);
                }
            } else return new MapSerializer().deserialize(this, element);
        }
        if(element.isJsonArray()) return new ListSerializer().deserialize(this, element);
        return null;
    }


    public static class Builder {

        private boolean useDCA = false;
        private List<SerializerAdapter<Object>> serializers;
        private boolean serializeNulls = true;

        public Builder(){
            this.serializers = new ArrayList<>();
        }

        public Builder useDefaultClassAdapter(boolean value){
            this.useDCA = value;
            return this;
        }

        public Builder addSerializers(SerializerAdapter ...serializers){
            for (SerializerAdapter serializer : serializers){
                this.serializers.add(serializer);
            }
            return this;
        }

        public Builder serializeNulls(boolean value){
            this.serializeNulls = value;
            return this;
        }

        public Serializer build(){
            return new Serializer(this.useDCA, this.serializers, serializeNulls);
        }
    }
}
