package se.bubbelbubbel.fakenews.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.SystemParameterNotFoundException;

@Component
public class SystemDAO {
	Logger logger = LoggerFactory.getLogger(SystemDAO.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	
	@Value("${fakenews.databaseName}")
	String DATABASE_NAME;

	public String getSystemParameter(String key) throws DatabaseErrorException, SystemParameterNotFoundException {
		logger.debug("getSystemParameter: " + key);
		String SELECT_SYSTEM_PARAMETER =
				"SELECT parameter_value " +
				"FROM " + DATABASE_NAME + ".system_parameters " +
				"WHERE parameter_key = ? ";
		try {
			String value = jdbcTemplate.queryForObject(SELECT_SYSTEM_PARAMETER,
																new Object[] {key},
																String.class);
			return value;
		}
		catch (EmptyResultDataAccessException ere) {
			String errorMsg = "No system parameter with key: " + key + " was found";
			logger.error(errorMsg);
			throw new SystemParameterNotFoundException(errorMsg);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getSystemParameter: " + key + " - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}
}
