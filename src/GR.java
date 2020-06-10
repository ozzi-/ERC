import java.util.ArrayList;

public class GR {
	private String st;
	private boolean changed;
	
	public GR(String st, boolean changed) {
		this.setSt(st);
		this.setChanged(changed);
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public static GR clean(String src) {
		src = src.toLowerCase();
		ArrayList<String> starts = new ArrayList<String>();
		starts.add("https://");
		starts.add("http://");
		starts.add("//");
		starts.add("www.");
		String cleaned = Strng.removeIfStartsWith(src, starts);
		boolean changed = cleaned.length() != src.length();
		return new GR(cleaned, changed);
	}
}
