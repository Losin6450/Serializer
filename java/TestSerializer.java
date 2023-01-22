import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import serializer.DefaultObjectSerializer;
import serializer.Serializer;
import serializer.SerializerAdapter;
import sun.misc.Unsafe;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSerializer {

    public static void main(String[] args) {
        Serializer serializer = new Serializer.Builder()
                .serializeNulls(false)
                .addSerializers(new SerializerAdapter<Foo>() {

                    @Override
                    public JsonElement serialize(Serializer serializer, Foo object) {
                        JsonObject finished = new JsonObject();
                        finished.addProperty(serializer.CLASS_PROPERTY, Foo.class.getName());
                        finished.addProperty("name", object.getName());
                        finished.addProperty("age", object.getAge());
                        return finished;
                    }

                    @Override
                    public Foo deserialize(Serializer serializer, JsonElement object) {
                        return new Foo(object.getAsJsonObject().get("name").getAsString(), object.getAsJsonObject().get("age").getAsInt());
                    }

                    @Override
                    public String getClassName() {
                        return Foo.class.getName();
                    }
                })
                .useDefaultClassAdapter(true)
                .build();


        Gson gson = new Gson();
        long start = new Date().getTime();
        for (int i = 0; i < 100000; i++){
            List list = List.of(List.of("Hej", List.of("Med", "Erik"), "Dig"), List.of("Kirkepladsen", 10));
            //System.out.println(serializer.serialize(list));
            System.out.println(gson.toJsonTree(list));
        }
        long end = new Date().getTime();

        System.out.println("it took " + (end - start) + " milliseconds");




    }

    public static class Foo {

        private String name;

        private int age;

        private Unsafe unsafe;

        public Foo(String name, int age){
            this.name = name;
            this.age = age;
            unsafe = DefaultObjectSerializer.unsafe;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "Foo{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", unsafe=" + unsafe +
                    '}';
        }
    }

    public static class Boo extends Foo {

        private final String test = "Hej med dig";

        private String name = "Bo Jensen";

        public Boo(String name, int age) {
            super(name, age);
        }

        @Override
        public String toString() {
            return "Boo{" +
                    "name='" + name + '\'' +
                    ", age=" + super.age +
                    ", unsafe=" + super.unsafe +
                    ", test='" + test + '\'' +
                    '}';
        }
    }
}
