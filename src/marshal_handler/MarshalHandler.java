package marshal_handler;

import com.google.common.primitives.Bytes;
import remote_objects.Common.Marshal;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarshalHandler {

    public static byte[] marshall(Marshal object) {
        List<Byte> marshalingList = new ArrayList<>();

        String className = object.getClass().getName();

        // start by marshaling the object id
        marshalInt(object.getId(), marshalingList);
        // then marshal the classname
        marshalString(className, marshalingList);
        // finally marshal all the fields within the object
        marshallObject(object, marshalingList);

        return Bytes.toArray(marshalingList);
    }


    private static void marshallObject(Object object, List<Byte> marshalingList) {
        Field[] fields = object.getClass().getDeclaredFields();

        // marshal each field that the object contains
        for (Field eachField : fields) {
            try {
                eachField.setAccessible(true); // need to do this in order to access private fields

                String type = eachField.getGenericType().getTypeName(); // example type = java.lang.String<remote_objects.Client.ClientRequest>
                Object o = eachField.get(object);

                // most of the time we only care about the type
                // however when dealing with list, we will need the objectName as well. Example: java.util.List<String>
                String[] typeAndObjectName = type.split("[<>]");

                typeToMarshal(typeAndObjectName, o, marshalingList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void typeToMarshal(String[] typeAndObjectName, Object object, List<Byte> marshalingList) {
        // to unmarshal a field, we need to first know if the field was assigned any data
        // marshal true if have data, else marshal false
        if (object == null) {
            marshalBoolean(false, marshalingList);
            return;
        }
        marshalBoolean(true, marshalingList);

        // choose type to marshal
        switch (typeAndObjectName[0]) {
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
                marshalList(object, Arrays.copyOfRange(typeAndObjectName, 1, typeAndObjectName.length), marshalingList);
                break;
            default:
                marshallObject(object, marshalingList);
                break;
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

    private static void marshalList(Object object, String[] typeAndObjectName, List<Byte> marshalingList) {
        List<?> list = (List<?>) object;

        // marshal list length
        short listLength = (short) list.size();
        byte[] array = ByteBuffer.allocate(Short.BYTES).putShort(listLength).array();
        marshalingList.addAll(Bytes.asList(array));

        // marshal each item in the list
        for (Object item : list) {
            typeToMarshal(typeAndObjectName, item, marshalingList);
        }
    }

}
