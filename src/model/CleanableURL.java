package model;
import java.util.ArrayList;

import helpers.Strng;

public class CleanableURL {
	private String url;
	private boolean wasCleaned;
	
	public CleanableURL(String url, boolean changed) {
		this.setSt(url);
		this.setChanged(changed);
	}

	public String getSt() {
		return url;
	}

	public void setSt(String st) {
		this.url = st;
	}

	public boolean wasCleaned() {
		return wasCleaned;
	}

	public void setChanged(boolean changed) {
		this.wasCleaned = changed;
	}
	
	public static CleanableURL clean(String src) {
		src = src.toLowerCase();
		ArrayList<String> starts = new ArrayList<String>();
		starts.add("https://");
		starts.add("http://");
		starts.add("//");
		starts.add("www.");
		String cleaned = Strng.removeIfStartsWith(src, starts);
		boolean changed = cleaned.length() != src.length();
		return new CleanableURL(cleaned, changed);
	}
}
