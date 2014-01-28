package password.string.safety;

/**
 * Contains methods for string conversion for better password safety.
 * @author Jacob Dorman
 */
public abstract class StringConverter {
    
    public static String convertToString(char[] charArray) {
        String string = "";
        for (int i = 0; i < charArray.length; i++) {
            string += charArray[i];
        }
        return string;
    }
}
