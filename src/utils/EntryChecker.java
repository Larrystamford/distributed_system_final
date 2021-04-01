package utils;

public class EntryChecker {

    public static boolean validInteger(String str) {
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

    public static boolean isAppropriateInteger(String str, int start, int end) {
        if (!validInteger(str)){
            return false;
        }
        int val = Integer.parseInt(str);
        return val >= start && val <= end;
    }


}
