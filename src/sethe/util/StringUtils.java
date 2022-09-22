package sethe.util;

import java.nio.charset.Charset;

public class StringUtils {

  public static boolean isEmpty(String s) {
    if (s == null) return true;
    return s.trim().equals("");
  }

  public static String stringSql(String s) {
    if (s != null) {
      return s.replaceAll("'", "''");
    }
    return null;
  }

  public static String sanitize(String string) {
    int countSpace = 0;
    String stringCleaned = "";

    for (int i = 0; i < string.length(); i++) {
      Character character = string.charAt(i);

      if (Charset.forName("US-ASCII").newEncoder().canEncode(character)) {
        String characterCleaned =
          (character + "").replaceAll(
              "[+,'!@#$&\\*()\\[\\]\\-/\\\\:;\\.\\?|]",
              ""
            );

        if (characterCleaned.isEmpty()) continue;

        if (Character.isWhitespace(character)) {
          countSpace++;
        } else {
          countSpace = 0;
        }

        if (countSpace <= 1) {
          stringCleaned += character + "";
        }
      }
    }
    stringCleaned = stringCleaned.trim();

    return stringCleaned;
  }
}
