package se.bubbelbubbel.fakenews.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import se.bubbelbubbel.fakenews.model.Newsflash;
import se.bubbelbubbel.fakenews.model.helper.FunctionHelper;

public class NewsflashRowMapper implements RowMapper <Newsflash>{

	public static final String NEWSFLASH_COLUMN_LIST =
			" newsflash_id, send_time, news_text, status ";

	public static final String NEWSFLASH_UPDATE_COLUMN_LIST =
			" send_time, news_text, status ";

	public Newsflash mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
		Newsflash newsflash = new Newsflash();
		newsflash.setNewsflashId(rs.getInt(1));
	  	if(rs.getTimestamp(2) != null) {
    		newsflash.setSendTime(FunctionHelper.dateToGregCal(rs.getTimestamp(2)));
		};
		newsflash.setNewsText(rs.getString(3));
		newsflash.setStatus(rs.getString(4));
		return newsflash;
	}
}
