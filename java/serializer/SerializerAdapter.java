package serializer;

import com.google.gson.JsonElement;

public interface SerializerAdapter<T> {

    JsonElement serialize(Serializer serializer, T object);

    T deserialize(Serializer serializer, JsonElement object);

    String getClassName();
}
