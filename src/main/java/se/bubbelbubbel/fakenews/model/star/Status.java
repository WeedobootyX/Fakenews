package se.bubbelbubbel.fakenews.model.star;

public class Status {
	private boolean connected;
	private boolean claimed;
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public boolean isClaimed() {
		return claimed;
	}
	public void setClaimed(boolean claimed) {
		this.claimed = claimed;
	}

}
