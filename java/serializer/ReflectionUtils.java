package serializer;

import java.lang.reflect.Field;

public class ReflectionUtils {


    public static Field getSuperField(Class<?> cls, String field){
        Class<?> currentClass = cls;
        while(currentClass != Object.class){
            try {
                return currentClass.getDeclaredField(field);
            } catch (Exception ignored){}
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }
}
