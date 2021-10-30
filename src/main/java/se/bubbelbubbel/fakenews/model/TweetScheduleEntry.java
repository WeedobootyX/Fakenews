package se.bubbelbubbel.fakenews.model;

public class TweetScheduleEntry {
	private int timeValue;
	private TimeFactor timeFactor;
	
	public TweetScheduleEntry(int timeValue, TimeFactor timeFactor) {
		super();
		this.timeValue = timeValue;
		this.timeFactor = timeFactor;
	}
	public int getTimeValue() {
		return timeValue;
	}
	public void setTimeValue(int timeValue) {
		this.timeValue = timeValue;
	}
	public TimeFactor getTimeFactor() {
		return timeFactor;
	}
	public void setTimeFactor(TimeFactor timeFactor) {
		this.timeFactor = timeFactor;
	}
}
