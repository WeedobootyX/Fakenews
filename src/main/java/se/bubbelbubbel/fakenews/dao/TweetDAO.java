package se.bubbelbubbel.fakenews.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.model.Tweet;
import se.bubbelbubbel.fakenews.model.TweetRequest;
import se.bubbelbubbel.fakenews.preparedstatementcreator.TweetRequestPSCreator;
import se.bubbelbubbel.fakenews.rowmapper.MonitoredAccountRowMapper;
import se.bubbelbubbel.fakenews.rowmapper.TweetRowMapper;
import twitter4j.Status;
import se.bubbelbubbel.fakenews.model.MonitoredAccount;

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
				"( " + TweetRowMapper.COLUMNS_INSERT + " ) " +
				"VALUES (?, ?, null, ?, ?) ";
		
		String UPDATE_TWEET =
				"UPDATE " + DATABASE_NAME + ".tweets " +
				"SET " + TweetRowMapper.COLUMNS_UPDATE + 
				"WHERE tweet_id = ? ";

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

	public List<Tweet> getUnpublishedTweets() {
		logger.debug("getUnPublishedTweets");
		String SELECT_DUE_TWEETS =
			"SELECT " + TweetRowMapper.COLUMNS_SELECT +
			"FROM " + DATABASE_NAME + ".tweets " +
			"WHERE status = ? " +
			"AND scheduled_time < now() ";
		
		try {
			List<Tweet> tweets = jdbcTemplate.query(SELECT_DUE_TWEETS,
							   						new Object[] {Tweet.PENDING},
							   						new TweetRowMapper());
			return tweets;
		}
		catch (Exception e) {
			String errMsg = "Exception caught in getUnpublishedTweets: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
			return new ArrayList<Tweet>();
		}
	}

	public List<MonitoredAccount> getMonitoredAccounts() throws DatabaseErrorException {
		logger.debug("getMonitoredAccounts");
		String SELECT_MONITORED_ACCOUNTS =
			"SELECT " + MonitoredAccountRowMapper.COLUMNS_SELECT +
			"FROM " + DATABASE_NAME + ".monitored_accounts ";
		
		try {
			List<MonitoredAccount> monitoredAccounts = jdbcTemplate.query(SELECT_MONITORED_ACCOUNTS,
							   											  new MonitoredAccountRowMapper());
			return monitoredAccounts;
		}
		catch (Exception e) {
			String errMsg = "Exception caught in getMonitoredAccounts: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
			throw new DatabaseErrorException(errMsg);
		}
	}
	
	public void saveMonitoredAccount(MonitoredAccount monitoredAccount) throws DatabaseErrorException {
		String UPDATE_MONITORED_ACCOUNT =
				"UPDATE " + DATABASE_NAME + ".monitored_accounts " +
				"SET " + MonitoredAccountRowMapper.COLUMNS_UPDATE + 
				"WHERE user_name = ? ";

		try {
				jdbcTemplate.update(UPDATE_MONITORED_ACCOUNT,
						new Object[] {monitoredAccount.getName(),
									  monitoredAccount.getImageURL(),
									  monitoredAccount.getUserId(),
									  monitoredAccount.getUserName()});
			}
		catch (Exception e) {
			String errorMsg = " Exception caught in saveMonitoredAccount for user name: " + monitoredAccount.getUserName() + " - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}

	public void saveStatus(Status status) throws DatabaseErrorException {
		String INSERT_STATUS =
			"INSERT INTO " + DATABASE_NAME + ".status_updates " +
			"(monitorer, user_name, text, created_at, status_id) values (?, ?, ?, ?, ?) ";
		try {
			jdbcTemplate.update(INSERT_STATUS,
					new Object[] {"valhajen",
								  status.getUser().getName(),
								  status.getText(),
								  LocalDateTime.ofInstant(status.getCreatedAt().toInstant(), ZoneId.systemDefault()),
								  status.getId()}
			);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in saveStatus for monitorer valhajen: " + " - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}
	
	public void statusCleanup() {
		logger.debug("deleting old statuses");
		String DELETE_STATUSES =
			"DELETE FROM " + DATABASE_NAME + ".status_updates " +
			"WHERE created_at > DATE_SUB(NOW(), INTERVAL 1 DAY)";
		try {
			jdbcTemplate.update(DELETE_STATUSES);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in statusCleanup - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}
} 
