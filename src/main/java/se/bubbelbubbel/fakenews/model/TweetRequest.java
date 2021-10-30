package se.bubbelbubbel.fakenews.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bubbelbubbel.fakenews.exception.IllegalTweetRequestException;

public class TweetRequest {
	Logger logger = LoggerFactory.getLogger(TweetRequest.class);
	private int id;
	private String text = "";
	private String sendSchedule = "";

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSendSchedule() {
		return sendSchedule;
	}
	public void setSendSchedule(String sendSchedule) {
		this.sendSchedule = sendSchedule;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "TweetRequest [logger=" + logger + ", id=" + id + ", text=" + text + ", sendSchedule=" + sendSchedule
				+ "]";
	}
	public List<TweetScheduleEntry> getScheduleEntries() {
		List<TweetScheduleEntry> scheduleEntries = new ArrayList<TweetScheduleEntry>();
		String[] entries = this.sendSchedule.split(",");
		if(entries.length > 0) {
			Stream<String> entStr = Arrays.stream(entries);
			scheduleEntries =
			entStr.map(this::makeScheduleEntry)
				  .collect(toList());
		}
		return scheduleEntries;
	}
	
	private TweetScheduleEntry makeScheduleEntry(String entry) {
		logger.debug("makeScheduleEntry from: " + entry);
		Integer timeValue = null;
		TimeFactor tf = null;
		entry = entry.replace(" ", "");
		try {
			String timeValueStr = entry.substring(0, entry.length() - 1);
			String timeFactorStr = entry.substring(entry.length() - 1);
			logger.debug("entry: " + entry + ", timeValueStr is " + timeValueStr + " and timeFactorStr is " + timeFactorStr); 
			if(timeFactorStr.equals("m")) {
				tf = TimeFactor.MINUTE;
			}
			else if(timeFactorStr.equals("h")) {
				tf = TimeFactor.HOUR;
			}
			else if(timeFactorStr.equals("d")) {
				tf = TimeFactor.DAY;
			}
			else if(timeFactorStr.equals("w") || timeFactorStr.equals("v")) {
				tf = TimeFactor.WEEK;
			}
			else if(timeFactorStr.equals("a")) {
				tf = TimeFactor.AUTO;
			}
			else {
				throw new IllegalTweetRequestException("Invalid entry: " + entry);
			}
			timeValue = Integer.valueOf(timeValueStr);
		}
		catch (Exception e) {
			//malformed scheduling becomes auto-schedule
			logger.error("Exception in makeScheduleEntry for entry: " + entry + " - " + e.getClass() + ": " + e.getMessage());
			timeValue = Integer.valueOf(0);
			tf = TimeFactor.AUTO;
		}
		return new TweetScheduleEntry(timeValue, tf);
	}
}
