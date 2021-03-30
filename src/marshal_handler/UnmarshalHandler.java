package marshal_handler;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.*;

import com.google.common.primitives.Bytes;
import remote_objects.Common.Marshal;

public class UnmarshalHandler {

    public static Object unmarshall(byte[] data) throws ClassNotFoundException {
        List<Byte> byteList = new LinkedList<>(Bytes.asList(data));
        String className = unmarshallString(byteList);
        int id = unmarshallInt(byteList);
        Object o = unmarshallObject(byteList, Class.forName(className));
        if (o instanceof Marshal) {
            Marshal m = (Marshal) o;
            m.setId(id);
            return m;
        }
        return o;
    }

    private static <T> T unmarshallObject(List<Byte> byteList, Class<T> clazz) {
        T obj;
        try {
            obj = clazz.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object unmarshalledFieldValue = unmarshallSelect(byteList, f.getGenericType());
                System.out.println(unmarshalledFieldValue);
                f.set(obj, unmarshalledFieldValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }



    private static Object unmarshallSelect(List<Byte> byteList, Type type) {

        boolean notNull = unmarshallBoolean(byteList);
        if (!notNull) return null;

        String[] typeWithGeneric = type.getTypeName().split("[<>]");
        switch (typeWithGeneric[0]) {
            case "java.lang.String":
                return unmarshallString(byteList);
            case "java.lang.Short":
            case "short":
                return unmarshallShort(byteList);
            case "java.lang.Integer":
            case "int":
                return unmarshallInt(byteList);
            case "java.lang.Float":
            case "float":
                return unmarshallFloat(byteList);
            case "java.lang.Double":
            case "double":
                return unmarshallDouble(byteList);
            case "java.lang.Boolean":
            case "boolean":
                return unmarshallBoolean(byteList);
            case "java.util.List":
                System.out.println(typeWithGeneric);
                Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
                System.out.println(genericType);
                return unmarshallList(byteList, genericType);
            default:
                return unmarshallObject(byteList, (Class<?>) type);
        }
    }




    private static <T> List<T> unmarshallList(List<Byte> byteList, Type genericType) {
        List<T> list = new ArrayList<>();
        int size = shortFromByteList(byteList);

        for (int i = 0; i < size; i++) {
            T obj = (T) unmarshallSelect(byteList, genericType);
            list.add(obj);
        }

        return list;
    }



    private static int unmarshallInt(List<Byte> byteList) {
        return intFromByteList(byteList);
    }



    private static short unmarshallShort(List<Byte> byteList) {
        return shortFromByteList(byteList);
    }



    private static float unmarshallFloat(List<Byte> byteList) {
        return floatFromByteList(byteList);
    }



    private static double unmarshallDouble(List<Byte> byteList) {
        return doubleFromByteList(byteList);
    }



    private static boolean unmarshallBoolean(List<Byte> byteList) {
        return booleanFromByteList(byteList);
    }



    private static String unmarshallString(List<Byte> byteList) {
        return stringFromByteList(byteList);
    }




    private static int intFromByteList(List<Byte> byteList) {
        byte[] intBytes = new byte[Integer.BYTES];
        for (int i = 0; i < Integer.BYTES; i++) {
            intBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(intBytes).getInt();
    }


    private static String stringFromByteList(List<Byte> byteList) {
        int size = shortFromByteList(byteList);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append((char) byteList.remove(0).byteValue());
        }
        return builder.toString();
    }



    private static short shortFromByteList(List<Byte> byteList) {
        byte[] shortBytes = new byte[Short.BYTES];
        for (int i = 0; i < Short.BYTES; i++) {
            shortBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(shortBytes).getShort();
    }




    private static double doubleFromByteList(List<Byte> byteList) {
        byte[] doubleBytes = new byte[Double.BYTES];
        for (int i = 0; i < Double.BYTES; i++) {
            doubleBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(doubleBytes).getDouble();
    }


    private static float floatFromByteList(List<Byte> byteList) {
        byte[] floatBytes = new byte[Float.BYTES];
        for (int i = 0; i < Float.BYTES; i++) {
            floatBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(floatBytes).getFloat();
    }



    private static boolean booleanFromByteList(List<Byte> byteList) {
        Byte bool = byteList.remove(0);
        return bool != (byte) 0;
    }


}
