package se.bubbelbubbel.fakenews.model;

import java.time.LocalDateTime;

public class TweetRequest {
	private String text = "";
	private LocalDateTime sendTime;
	private String repeatSchedule = "";
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public LocalDateTime getSendTime() {
		return sendTime;
	}
	public void setSendTime(LocalDateTime sendTime) {
		this.sendTime = sendTime;
	}
	public String getRepeatSchedule() {
		return repeatSchedule;
	}
	public void setRepeatSchedule(String repeatSchedule) {
		this.repeatSchedule = repeatSchedule;
	}
	public boolean isValid() {
		boolean response = true;
		// TODO Auto-generated method stub
		return response;
	}

}
