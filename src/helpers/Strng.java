package helpers;
import java.util.ArrayList;

public class Strng {
	public static String removeIfStartsWith(String href, ArrayList<String> starts) {
		for (String start : starts) {
			if (href.startsWith(start)) {
				href = href.substring(start.length());
			}
		}
		return href;
	}
	
	public static String repeat(String strng, int amount) {
		return String.format("%0" + amount + "d", 0).replace("0", strng);
	}
	
	public static void builderAppendKeyValue(StringBuilder builder, String key, String value) {
		builder.append("\""+key+"\":"+"\""+value+"\",");
	}
	
	public static int nthLastIndexOf(int nth, String needle, String haystack) {
		if(haystack==null || haystack.length()<needle.length()) {
			return -1;
		}
		if (nth <= 0) {
			return haystack.length();
		}
		return nthLastIndexOf(--nth, needle, haystack.substring(0, haystack.lastIndexOf(needle)));
	}

	public static int countOccurrences(String haystack, String needle) {
		if(needle.length()>1) {
			return -1;
		}
		char neeldeC = needle.charAt(0);
		int count = 0;
		for (int i = 0; i < haystack.length(); i++) {
			if (haystack.charAt(i) == neeldeC) {
				count++;
			}
		}
		return count;
	}
}
