package main.java.marshal_handler;

import com.google.common.primitives.Bytes;
import main.java.remote_objects.Common.Marshal;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarshalHandler {

    public static byte[] marshall(Marshal object) {
        List<Byte> marshalingList = new ArrayList<>();

        String className = object.getClass().getName();
        marshalString(className, marshalingList);
        marshalInt(object.getId(), marshalingList);
        marshallObject(object, marshalingList);

        byte[] resBytes = Bytes.toArray(marshalingList);
        return resBytes;
    }


    private static void marshallObject(Object object, List<Byte> marshalingList) {

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field f : fields) {
            String type = f.getGenericType().getTypeName();
            f.setAccessible(true);
            try {
                Object o = f.get(object);

                String[] typeWithGeneric = type.split("[<>]");
                marshallSelect(typeWithGeneric, o, marshalingList);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void marshallSelect(String[] typeWithGeneric, Object object, List<Byte> marshalingList) {

        if (object == null) {
            marshalBoolean(false, marshalingList);
            return;
        }
        marshalBoolean(true, marshalingList);

        switch (typeWithGeneric[0]) {
            case "java.lang.String":
                marshalString(object, marshalingList);
                break;
            case "java.lang.Short":
            case "short":
                marshalShort(object, marshalingList);
                break;
            case "java.lang.Integer":
            case "int":
                marshalInt(object, marshalingList);
                break;
            case "java.lang.Float":
            case "float":
                marshalFloat(object, marshalingList);
                break;
            case "java.lang.Double":
            case "double":
                marshalDouble(object, marshalingList);
                break;
            case "java.lang.Boolean":
            case "boolean":
                marshalBoolean(object, marshalingList);
                break;
            case "java.util.List":
                marshalList(object, Arrays.copyOfRange(typeWithGeneric, 1, typeWithGeneric.length), marshalingList);
                break;
            default:
                marshallObject(object, marshalingList);
                break;
        }

    }


    private static void marshalList(Object object, String[] typeWithGeneric, List<Byte> marshalingList) {
        List<?> list = (List<?>) object;

        // marshal list length
        short listLength = (short) list.size();
        byte[] array = ByteBuffer.allocate(Short.BYTES).putShort(listLength).array();
        marshalingList.addAll(Bytes.asList(array));

        // marshal each item in the list
        for (Object item : list) {
            marshallSelect(typeWithGeneric, item, marshalingList);
        }
    }

    private static void marshalString(Object object, List<Byte> marshalingList) {
        String value = (String) object;

        // marshal string length
        short strLength = (short) value.length();
        byte[] array = ByteBuffer.allocate(Short.BYTES).putShort(strLength).array();
        marshalingList.addAll(Bytes.asList(array));

        // marshal string
        marshalingList.addAll(Bytes.asList(value.getBytes()));
    }

    private static void marshalInt(Object object, List<Byte> marshalingList) {
        int value = (int) object;
        byte[] array = ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
        marshalingList.addAll(Bytes.asList(array));
    }


    private static void marshalShort(Object object, List<Byte> marshalingList) {
        short value = (short) object;
        byte[] array = ByteBuffer.allocate(Short.BYTES).putShort(value).array();
        marshalingList.addAll(Bytes.asList(array));
    }


    private static void marshalFloat(Object object, List<Byte> marshalingList) {
        float value = (float) object;
        byte[] array = ByteBuffer.allocate(Float.BYTES).putFloat(value).array();

        marshalingList.addAll(Bytes.asList(array));
    }


    private static void marshalDouble(Object object, List<Byte> marshalingList) {
        double value = (double) object;
        byte[] array = ByteBuffer.allocate(Double.BYTES).putDouble(value).array();
        marshalingList.addAll(Bytes.asList(array));
    }


    private static void marshalBoolean(Object object, List<Byte> marshalingList) {
        byte value;
        if ((boolean) object) {
            value = (byte) 1;
        } else {
            value = (byte) 0;
        }

        marshalingList.addAll(Bytes.asList(value));
    }

}
