package se.bubbelbubbel.fakenews.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import se.bubbelbubbel.fakenews.model.Tweet;

public class TweetRowMapper implements RowMapper <Tweet>{

	public static final String COLUMNS_SELECT =
			" tweet_id, text, scheduled_time, sent_time, status, tweet_request_id ";

	public static final String COLUMNS_UPDATE =
			" text = ?, scheduled_time = ?, sent_time = ?, status = ?, tweet_request_id = ? ";

	public static final String COLUMNS_INSERT =
			" text, scheduled_time, sent_time, status, tweet_request_id ";

	public Tweet mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
		Tweet tweet = new Tweet();
		tweet.setId(rs.getInt(1));
		tweet.setText(rs.getString(2));
		tweet.setScheduledTime(rs.getTimestamp(3).toLocalDateTime());
		if(rs.getTimestamp(4) != null) {
			tweet.setSentTime(rs.getTimestamp(4).toLocalDateTime());
		}
		tweet.setStatus(rs.getString(5));
		tweet.setTweetRequestId(rs.getInt(6));
		return tweet;
	}
}
