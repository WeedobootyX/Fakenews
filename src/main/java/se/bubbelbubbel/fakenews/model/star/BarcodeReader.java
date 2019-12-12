package se.bubbelbubbel.fakenews.model.star;

public class BarcodeReader {
	private String name = "";
	private Status status = null;
	private Scan scan = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Scan getScan() {
		return scan;
	}
	public void setScan(Scan scan) {
		this.scan = scan;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
}
