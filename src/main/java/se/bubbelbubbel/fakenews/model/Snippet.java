package se.bubbelbubbel.fakenews.model;

public class Snippet {
	
	private String structureKey = "";
	private String snippetKey = "";
	private String snippetText = "";
	private String status = "";
	int snippetId;
	
	public int getSnippetId() {
		return snippetId;
	}

	public void setSnippetId(int snippetId) {
		this.snippetId = snippetId;
	}

	public static String STATUS_ACTIVE = "active";

	public String getStructureKey() {
		return structureKey;
	}

	public void setStructureKey(String structureKey) {
		this.structureKey = structureKey;
	}

	public String getSnippetKey() {
		return snippetKey;
	}

	public void setSnippetKey(String snippetKey) {
		this.snippetKey = snippetKey;
	}

	public String getSnippetText() {
		return snippetText;
	}

	public void setSnippetText(String snippetText) {
		this.snippetText = snippetText;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static String getSTATUS_ACTIVE() {
		return STATUS_ACTIVE;
	}

	public static void setSTATUS_ACTIVE(String sTATUS_ACTIVE) {
		STATUS_ACTIVE = sTATUS_ACTIVE;
	}
}
