package se.bubbelbubbel.fakenews.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Structure {
	private String structureKey = "";
	private String structure = "";
	private int weight;
	private String status = "";
	private String comment = "";
	
	public static String STATUS_ACTIVE = "active";
	
	public String getStructureKey() {
		return structureKey;
	}
	public void setStructureKey(String structureKey) {
		this.structureKey = structureKey;
	}
	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public List<String> getSnippetKeys() {
		List<String> outputSnippetList = new ArrayList<String>();
		String[] strArr = structure.split("\\{");
		List<String> cuts = Arrays.asList(strArr);
		for(String str : cuts) {
			int endIndex = str.indexOf("}");
			if(endIndex > 0) { //split creates an empty string if he split string begins with bracket
				String cleanedStr = str.substring(0, endIndex);
				System.out.println(cleanedStr);
				outputSnippetList.add(cleanedStr);
			}
		}
		return outputSnippetList;
	}
}
