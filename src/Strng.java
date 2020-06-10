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

	static int nthLastIndexOf(int nth, String ch, String string) {
		if (nth <= 0) {
			return string.length();
		}
		return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
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
