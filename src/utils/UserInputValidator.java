package utils;

public class UserInputValidator {

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isNumericAndWithinRange(String str, int start, int end) {
        if (!isNumeric(str)){
            return false;
        }
        int val = Integer.parseInt(str);
        return val >= start && val <= end;
    }


}
