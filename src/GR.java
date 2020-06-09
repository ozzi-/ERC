
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
}
