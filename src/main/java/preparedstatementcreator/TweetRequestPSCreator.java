package preparedstatementcreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;

import se.bubbelbubbel.fakenews.model.TweetRequest;

public class TweetRequestPSCreator implements PreparedStatementCreator{

		private static final Logger logger = LoggerFactory.getLogger(TweetRequestPSCreator.class);
		
		private String DATABASE_NAME;
		private TweetRequest tweetRequest;
		
		public TweetRequestPSCreator(TweetRequest tweetRequest, String databaseName){
			super();
			this.tweetRequest = tweetRequest;
			this.DATABASE_NAME = databaseName;
		}
		
		@Override
		public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			logger.debug("createPreparedStatement");
			String INSERT_TWEET_REQUEST = 
						"INSERT INTO " + DATABASE_NAME + ".tweet_requests " +
						"(text, send_schedule, received ) " + 
						"VALUES(?, ?, now()) ";
			PreparedStatement ps = connection.prepareStatement(INSERT_TWEET_REQUEST, new String[] {"id"});
			ps.setString(1, tweetRequest.getText());
			ps.setString(2, tweetRequest.getSendSchedule());
			return ps;
		}
	}
