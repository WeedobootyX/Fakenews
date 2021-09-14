package se.bubbelbubbel.fakenews.model;

public class QueuedNewsflash {
	int sendMinutes;
	private String newsText = "";
	
	public int getSendMinutes() {
		return sendMinutes;
	}
	public void setSendMinutes(int sendMinutes) {
		this.sendMinutes = sendMinutes;
	}
	public String getNewsText() {
		return newsText;
	}
	public void setNewsText(String newsText) {
		this.newsText = newsText;
	}
}
