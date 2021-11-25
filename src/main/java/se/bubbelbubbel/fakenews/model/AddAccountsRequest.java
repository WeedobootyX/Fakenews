package se.bubbelbubbel.fakenews.model;

import java.util.Arrays;
import java.util.List;

public class AddAccountsRequest {
	private String monitorer = "";
	private String strUserNames = "";

	public String getMonitorer() {
		return monitorer;
	}
	public void setMonitorer(String monitorer) {
		this.monitorer = monitorer;
	}
	public String getStrUserNames() {
		return strUserNames;
	}
	public void setStrUserNames(String strUserNames) {
		this.strUserNames = strUserNames;
	}

	public List<String> getUserNameList() {
		String[] userNames = this.strUserNames.split(",");
		List<String> userNameList = Arrays.asList(userNames);
		return userNameList;
	}
}
