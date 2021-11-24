package se.bubbelbubbel.fakenews.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import se.bubbelbubbel.fakenews.model.StatusUpdate;

public class StatusUpdateRowMapper implements RowMapper <StatusUpdate>{

	public static final String COLUMNS_SELECT =
			" monitorer, user_name, text, created_at, status_id "; 

	public StatusUpdate mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
		StatusUpdate statusUpdate = new StatusUpdate();
		statusUpdate.setMonitorer(rs.getString(1));
		statusUpdate.setUserName(rs.getString(2));
		statusUpdate.setText(rs.getString(3));
		statusUpdate.setCreatedAt(rs.getTimestamp(4).toLocalDateTime());
		statusUpdate.setStatusId(rs.getLong(5));
		return statusUpdate;
	}
}
