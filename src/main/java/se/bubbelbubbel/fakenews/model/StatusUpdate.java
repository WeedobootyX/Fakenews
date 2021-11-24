package se.bubbelbubbel.fakenews.model;

import java.time.LocalDateTime;

public class StatusUpdate {
	private String monitorer =  "";
	private String userName = "";
	private String text = "";
	private LocalDateTime createdAt;
	private long statusId;
	public String getMonitorer() {
		return monitorer;
	}
	public void setMonitorer(String monitorer) {
		this.monitorer = monitorer;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}
}