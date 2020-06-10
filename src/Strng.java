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
	    if (nth <= 0) return string.length();
	    return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
	}
}
