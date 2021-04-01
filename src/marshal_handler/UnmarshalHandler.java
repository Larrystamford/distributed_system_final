package marshal_handler;

import com.google.common.primitives.Bytes;
import remote_objects.Common.Marshal;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UnmarshalHandler {

    public static Marshal unmarshal(byte[] data) throws ClassNotFoundException {
        List<Byte> byteList = new ArrayList<>(Bytes.asList(data));

        // start by retrieving the object ID
        int id = unmarshalInt(byteList);
        // then get the classname of the object
        String className = unmarshalString(byteList);
        // with the object's classname we can unravel each field within the object
        Object o = unmarshalObject(byteList, Class.forName(className));

        // downcast back to marshal class
        Marshal marshalObject = (Marshal) o;
        marshalObject.setId(id);
        return marshalObject;
    }

    private static <T> T unmarshalObject(List<Byte> byteList, Class<T> _class) {
        T object;
        try {
            // create a new instance of the class
            object = _class.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // get the fields that the class contains
        Field[] fields = _class.getDeclaredFields();

        // unmarshal each field
        for (Field eachField : fields) {
            try {
                eachField.setAccessible(true); // need to do this in order to access private fields
                Object unmarshalledFieldValue = typeToUnmarshal(byteList, eachField.getGenericType());

                // set the object with the unmarshalled data
                eachField.set(object, unmarshalledFieldValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // unmarshalled object
        return object;
    }


    private static Object typeToUnmarshal(List<Byte> byteList, Type type) {
        // if field was not assigned any data, return null
        boolean notNull = unmarshalBoolean(byteList);
        if (!notNull) return null;

        // choose type to unmarshal
        String[] typeAndObjectName = type.getTypeName().split("[<>]");
        switch (typeAndObjectName[0]) {
            case "java.lang.Short":
            case "short":
                return unmarshalShort(byteList);
            case "java.lang.Integer":
            case "int":
                return unmarshalInt(byteList);
            case "java.lang.Float":
            case "float":
                return unmarshalFloat(byteList);
            case "java.lang.Double":
            case "double":
                return unmarshalDouble(byteList);
            case "java.lang.String":
                return unmarshalString(byteList);
            case "java.lang.Boolean":
            case "boolean":
                return unmarshalBoolean(byteList);
            case "java.util.List":
                Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
                return unmarshalList(byteList, genericType);
            default:
                return unmarshalObject(byteList, (Class<?>) type);
        }
    }


    private static String unmarshalString(List<Byte> byteList) {
        // unmarshal length of the string
        int size = getShortFromByteList(byteList);

        // unmarshal each char in the string
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append((char) byteList.remove(0).byteValue());
        }
        return builder.toString();
    }

    private static int unmarshalInt(List<Byte> byteList) {
        byte[] intBytes = new byte[Integer.BYTES];
        for (int i = 0; i < Integer.BYTES; i++) {
            intBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(intBytes).getInt();
    }

    private static float unmarshalFloat(List<Byte> byteList) {
        byte[] floatBytes = new byte[Float.BYTES];
        for (int i = 0; i < Float.BYTES; i++) {
            floatBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(floatBytes).getFloat();
    }

    private static double unmarshalDouble(List<Byte> byteList) {
        byte[] doubleBytes = new byte[Double.BYTES];
        for (int i = 0; i < Double.BYTES; i++) {
            doubleBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(doubleBytes).getDouble();
    }


    private static boolean unmarshalBoolean(List<Byte> byteList) {
        Byte bool = byteList.remove(0);

        if (bool == (byte) 1) {
            return true;
        }
        return false;
    }

    private static short unmarshalShort(List<Byte> byteList) {
        return getShortFromByteList(byteList);
    }

    private static short getShortFromByteList(List<Byte> byteList) {
        byte[] shortBytes = new byte[Short.BYTES];
        for (int i = 0; i < Short.BYTES; i++) {
            shortBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(shortBytes).getShort();
    }

    private static <T> List<T> unmarshalList(List<Byte> byteList, Type genericType) {
        List<T> list = new ArrayList<>();
        // unmarshal length of list
        int size = getShortFromByteList(byteList);

        // unmarshal each object in the list
        for (int i = 0; i < size; i++) {
            T obj = (T) typeToUnmarshal(byteList, genericType);
            list.add(obj);
        }
        return list;
    }
}
