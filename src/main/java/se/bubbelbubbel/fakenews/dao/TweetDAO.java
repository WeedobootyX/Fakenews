package se.bubbelbubbel.fakenews.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import preparedstatementcreator.TweetRequestPSCreator;
import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.model.Tweet;
import se.bubbelbubbel.fakenews.model.TweetRequest;

@Component
public class TweetDAO {
	Logger logger = LoggerFactory.getLogger(TweetDAO.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	
	@Value("${fakenews.databaseName}")
	String DATABASE_NAME;

	public TweetRequest saveTweetRequest(TweetRequest tweetRequest) throws DatabaseErrorException {
		logger.debug("saveTweetRequest" );
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			TweetRequestPSCreator psCreator = new TweetRequestPSCreator(tweetRequest, DATABASE_NAME);
			jdbcTemplate.update(psCreator, keyHolder);
			int newId = keyHolder.getKey().intValue();
			tweetRequest.setId(newId);
			return tweetRequest;
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in saveTweetRequest for request: " + tweetRequest.toString() + " - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}

	public void saveTweet(Tweet tweet) throws DatabaseErrorException {
		logger.debug("saveTweet: " + tweet.getId());
		String INSERT_TWEET = 
				"INSERT INTO " + DATABASE_NAME + ".tweets " +
				"(text, scheduled_time, sent_time, status, tweet_request_id) " +
				"VALUES (?, ?, null, ?, ?) ";
		
		String UPDATE_TWEET =
				"UPDATE " + DATABASE_NAME + ".tweets " +
				"SET text = ?, scheduled_time = ?, sent_time = ?, status = ?, tweet_request_id = ? " + 
				"WHERE tweet_request_id = ? ";

		try {
			if(tweet.getId() == 0) {
				jdbcTemplate.update(INSERT_TWEET,
						new Object[] {tweet.getText(),
									  tweet.getScheduledTime(),
									  tweet.getStatus(),
									  tweet.getTweetRequestId()});
			}
			else {
				jdbcTemplate.update(UPDATE_TWEET,
						new Object[] {tweet.getText(),
									  tweet.getScheduledTime(),
									  tweet.getSentTime(),
									  tweet.getStatus(),
									  tweet.getTweetRequestId(),
									  tweet.getId()});
			}
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in saveTweet for tweet: " + tweet.toString() + " - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}
}
