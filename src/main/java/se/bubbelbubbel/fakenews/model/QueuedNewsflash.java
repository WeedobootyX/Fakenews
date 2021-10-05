package se.bubbelbubbel.fakenews.model;

public class QueuedNewsflash {
	private int sendMinutes;
	private String newsText = "";
	private boolean recycle;
	
	public boolean isRecycle() {
		return recycle;
	}
	public void setRecycle(boolean recycle) {
		this.recycle = recycle;
	}
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
