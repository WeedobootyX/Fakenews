package se.bubbelbubbel.fakenews.model;

import java.util.GregorianCalendar;

public class Newsflash {
	private int newsflashId;
	GregorianCalendar sendTime = null;
	private String newsText = "";
	private String  status = "";
	private String structureKey = "";
	
	public Newsflash() {
		super();
	}
	public Newsflash(int newsflashId, String newsText) {
		super();
		this.newsflashId = newsflashId;
		this.newsText = newsText;
	}
	public String getStructureKey() {
		return structureKey;
	}
	public void setStructureKey(String structureKey) {
		this.structureKey = structureKey;
	}
	public static String STATUS_PENDING = "pending";
	public static String STATUS_PUBLISHED = "published";

	public int getNewsflashId() {
		return newsflashId;
	}
	public void setNewsflashId(int newsflashId) {
		this.newsflashId = newsflashId;
	}
	public GregorianCalendar getSendTime() {
		return sendTime;
	}
	public void setSendTime(GregorianCalendar sendTime) {
		this.sendTime = sendTime;
	}
	public String getNewsText() {
		return newsText;
	}
	public void setNewsText(String newsText) {
		this.newsText = newsText;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
