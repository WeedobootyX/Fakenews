package se.bubbelbubbel.fakenews.model.star;

public class Keyboard {
	private String name = "";
	private Status status = null;
	private String keyPresses = "";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getKeyPresses() {
		return keyPresses;
	}
	public void setKeyPresses(String keyPresses) {
		this.keyPresses = keyPresses;
	}

}
