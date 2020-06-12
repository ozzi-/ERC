package model;
import java.util.ArrayList;

public class Finding {
	private String source;
	private String finding;
	
	public static ArrayList<Finding> findings = new ArrayList<Finding>();
	
	public Finding(String source, String finding) {
		this.source=source;
		this.finding=finding;
	}

	public String getSource() {
		return source;
	}
	public String getFinding() {
		return finding;
	}
}
