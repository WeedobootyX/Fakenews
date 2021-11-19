package se.bubbelbubbel.fakenews.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import se.bubbelbubbel.fakenews.model.MonitoredAccount;

public class MonitoredAccountRowMapper implements RowMapper <MonitoredAccount>{

	public static final String COLUMNS_SELECT =
			" user_name, name, image_url, user_id ";

	public static final String COLUMNS_UPDATE =
			" name = ?, image_url = ?, user_id = ? ";

	public MonitoredAccount mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
		MonitoredAccount monitoredAccount = new MonitoredAccount();
		monitoredAccount.setUserName(rs.getString(1));
		monitoredAccount.setName(rs.getString(2));
		monitoredAccount.setImageURL(rs.getString(3));
		monitoredAccount.setUserId(rs.getLong(4));
		return monitoredAccount;
	}
}
