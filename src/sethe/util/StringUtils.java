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
              "[+,'\"!@#$&\\*()\\[\\]\\-/\\\\:;\\.\\?|]",
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

	public static boolean isAnyValue(String cat) {
		String s = cat.replaceAll("\\(", "");
		s = s.replaceAll("\\)", "");
		return s.equals(".*") || s.equals(".") || s.equals(".+");
	}

	public static boolean isPlusExpression(String text) {
		if(text.startsWith("(") && text.endsWith(")+")) {
			return true;
		}
		return false;
//		int i = text.lastIndexOf("+");
//		if(i > 0) {
//			String result = sanitize(text);
//			result = result.replace('w', ' ').trim();
//			return !result.isEmpty();
//		}
//		return false;
	}

	public static boolean isPlusAnyExpression(String text) {
		if(finishWithStar(text))
			return false;

		if(text.startsWith("(") && text.endsWith(")*")) {
			return true;
		}
		return false;

//		int i = text.lastIndexOf("*");
//		if(i > 0) {
//			String result = sanitize(text);
//			result = result.replace('w', ' ').trim();
//			return !result.isEmpty();
//		}
//		return false;
	}
	
	private static boolean finishWithStar(String text) {
		return text.endsWith("(( \\w*))*");
	}
}