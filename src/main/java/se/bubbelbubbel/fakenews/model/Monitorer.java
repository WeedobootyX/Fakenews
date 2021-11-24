package se.bubbelbubbel.fakenews.model;

public class Monitorer {
	private String userName = "";

	public Monitorer(String userName) {
		super();
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
