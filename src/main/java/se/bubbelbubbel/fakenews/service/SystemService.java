package se.bubbelbubbel.fakenews.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.bubbelbubbel.fakenews.dao.SystemDAO;
import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.SystemParameterNotFoundException;

@Component
public class SystemService {

	@Autowired private SystemDAO systemDAO;
	
	public String getSystemParameter(String key) throws DatabaseErrorException, SystemParameterNotFoundException {
		return systemDAO.getSystemParameter(key);
	}
}
