package se.bubbelbubbel.fakenews.model;

import java.time.LocalDateTime;
import java.util.Random;

public class Tweet {
	private int id;
	private String text= "";
	private LocalDateTime scheduledTime;
	private LocalDateTime sentTime;
	private String status = "";
	private int tweetRequestId;

	public Tweet(String text, TweetScheduleEntry tweetScheduleEntry, int tweetRequestId) { 
		this.setText(text);
		this.setStatus(PENDING);
		this.setTweetRequestId(tweetRequestId);
		this.setScheduledTime(calculateSendTime(tweetScheduleEntry));
	}
	
	private LocalDateTime calculateSendTime(TweetScheduleEntry tweetScheduleEntry) {
		LocalDateTime sendTime = LocalDateTime.now();
		Random random = new Random();
		if(tweetScheduleEntry.getTimeFactor().equals(TimeFactor.MINUTE)) {
			sendTime = sendTime.plusMinutes(tweetScheduleEntry.getTimeValue());
		}
		else if(tweetScheduleEntry.getTimeFactor().equals(TimeFactor.HOUR)) {
			sendTime = sendTime.plusHours(tweetScheduleEntry.getTimeValue());
		}
		else if(tweetScheduleEntry.getTimeFactor().equals(TimeFactor.DAY)) {
			sendTime = sendTime.plusDays(tweetScheduleEntry.getTimeValue());
		}
		else if(tweetScheduleEntry.getTimeFactor().equals(TimeFactor.WEEK)) {
			sendTime = sendTime.plusWeeks(tweetScheduleEntry.getTimeValue());
		}
		else if(tweetScheduleEntry.getTimeFactor().equals(TimeFactor.AUTO)) {
			if(tweetScheduleEntry.getTimeValue() == 0) {
				sendTime = sendTime.plusMinutes(random.nextInt(120));
			}
			else { 
				sendTime = sendTime.plusMinutes(random.nextInt(tweetScheduleEntry.getTimeValue() * 60));
			}
		}
		return sendTime;
	}

	public Tweet() {
		super();
	}
	
	public static String PENDING = "pending";
	public static String SENT = "sent";	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public LocalDateTime getScheduledTime() {
		return scheduledTime;
	}
	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	public LocalDateTime getSentTime() {
		return sentTime;
	}
	public void setSentTime(LocalDateTime sentTime) {
		this.sentTime = sentTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTweetRequestId() {
		return tweetRequestId;
	}
	public void setTweetRequestId(int tweetRequestId) {
		this.tweetRequestId = tweetRequestId;
	}
	@Override
	public String toString() {
		return "Tweet [id=" + id + ", text=" + text + ", scheduledTime=" + scheduledTime + ", sentTime=" + sentTime
				+ ", status=" + status + ", tweetRequestId=" + tweetRequestId + "]";
	}
}
