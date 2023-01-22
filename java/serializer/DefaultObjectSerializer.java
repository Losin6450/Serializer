package serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DefaultObjectSerializer implements SerializerAdapter<Object>{

    public static Unsafe unsafe;

    static {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            unsafe = (Unsafe) singleoneInstanceField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public JsonElement serialize(Serializer serializer, Object object) {
        JsonObject finished = new JsonObject();
        finished.addProperty(serializer.CLASS_PROPERTY, object.getClass().getName());
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = object.getClass();
        while(currentClass != Object.class){
            fields.addAll(List.of(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        for (Field field : fields){
            try {
                field.setAccessible(true);
                if (field.canAccess(object)) {
                    if(field.getDeclaringClass() != object.getClass() && finished.has(field.getName())) continue;
                    finished.add(field.getName(), serializer.serialize(field.get(object)));
                }

            } catch (Exception ignored){
            }
        }
        return finished;
    }

    @Override
    public Object deserialize(Serializer serializer, JsonElement object) {
        Object classObject = null;
        try {
            Class<?> cls = Class.forName(object.getAsJsonObject().get(serializer.CLASS_PROPERTY).getAsString());
            classObject = unsafe.allocateInstance(cls);
            JsonObject jsonObject = object.getAsJsonObject();
            jsonObject.remove(serializer.CLASS_PROPERTY);
            for (String key : jsonObject.keySet()){
                Field field = null;
                try {
                    field = ReflectionUtils.getSuperField(cls, key);
                } catch (Exception ignored){}
                if(field != null){
                    field.setAccessible(true);
                    Object value = serializer.deserialize(jsonObject.get(key));
                    if(jsonObject.get(key).getAsJsonPrimitive().isNumber()){
                        JsonPrimitive prim = jsonObject.get(key).getAsJsonPrimitive();
                        Number num = prim.getAsNumber();
                        Class<?> type = field.getType();
                        if(type.equals(Integer.class) || type.equals(Integer.TYPE)) value = num.intValue();
                        if(type.equals(Long.class) || type.equals(Long.TYPE)) value = num.longValue();
                        if(type.equals(Double.class) || type.equals(Double.TYPE)) value = num.doubleValue();
                        if(type.equals(Short.class) || type.equals(Short.TYPE)) value = num.shortValue();
                        if(type.equals(Float.class) || type.equals(Float.TYPE)) value = num.floatValue();
                        if(type.equals(Byte.class) || type.equals(Byte.TYPE)) value = num.byteValue();
                    }
                    if(field.canAccess(classObject)){

                        field.set(classObject, value);

                    }
                }
            }
        } catch (Exception ignored){}
        return classObject;
    }

    @Override
    public String getClassName() {
        return "java.lang.Object";
    }
}
