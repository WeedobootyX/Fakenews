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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.model.Tweet;
import se.bubbelbubbel.fakenews.model.TweetRequest;
import se.bubbelbubbel.fakenews.preparedstatementcreator.TweetRequestPSCreator;
import se.bubbelbubbel.fakenews.rowmapper.MonitoredAccountRowMapper;
import se.bubbelbubbel.fakenews.rowmapper.StatusUpdateRowMapper;
import se.bubbelbubbel.fakenews.rowmapper.TweetRowMapper;
import twitter4j.Status;
import se.bubbelbubbel.fakenews.model.MonitoredAccount;
import se.bubbelbubbel.fakenews.model.Monitorer;
import se.bubbelbubbel.fakenews.model.StatusUpdate;

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

	public List<MonitoredAccount> getMonitoredAccounts(Monitorer monitorer) {
		logger.debug("getMonitoredAccounts");
		String SELECT_MONITORED_ACCOUNTS =
			"SELECT " + MonitoredAccountRowMapper.COLUMNS_SELECT +
			"FROM " + DATABASE_NAME + ".monitored_accounts " +
			"WHERE monitorer =? ";
		
		List<MonitoredAccount> monitoredAccounts = new ArrayList<MonitoredAccount>();
		try {
			monitoredAccounts = jdbcTemplate.query(SELECT_MONITORED_ACCOUNTS,
												   new Object[] {monitorer.getUserName()},
							   					   new MonitoredAccountRowMapper());
		}
		catch (Exception e) {
			String errMsg = "Exception caught in getMonitoredAccounts: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
		}
		return monitoredAccounts;
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

	public void saveStatus(Status status, Monitorer monitorer) {
		String INSERT_STATUS =
			"INSERT INTO " + DATABASE_NAME + ".status_updates " +
			"(monitorer, user_name, text, created_at, status_id) values (?, ?, ?, ?, ?) ";
		try {
			jdbcTemplate.update(INSERT_STATUS,
					new Object[] {monitorer.getUserName(),
								  status.getUser().getName(),
								  status.getText(),
								  LocalDateTime.ofInstant(status.getCreatedAt().toInstant(), ZoneId.systemDefault()),
								  status.getId()}
			);
		}
		catch(DuplicateKeyException e) {
			//this is normal
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in saveStatus: " + " - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}
	
	public void statusCleanup() {
		logger.debug("deleting old statuses");
		String DELETE_STATUSES =
			"DELETE FROM " + DATABASE_NAME + ".status_updates " +
			"WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 DAY)";
		try {
			jdbcTemplate.update(DELETE_STATUSES);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in statusCleanup - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}

	public List<StatusUpdate> getMonitoredStatusUpdates(Monitorer monitorer) {
		String SELECT_UPDATES_BY_MONITORER =
			"SELECT " + StatusUpdateRowMapper.COLUMNS_SELECT +
			"FROM " + DATABASE_NAME + ".status_updates " +
			"WHERE monitorer = ? ";
		
		try {
			List<StatusUpdate> statusUpdates = jdbcTemplate.query(SELECT_UPDATES_BY_MONITORER,
							   						new Object[] {monitorer.getUserName()},
							   						new StatusUpdateRowMapper());
			return statusUpdates;
		}
		catch (Exception e) {
			String errMsg = "Exception caught in getMonitoredStatusUpdates: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
			return new ArrayList<StatusUpdate>();
		}
	}

	public void incrementWord(Monitorer monitorer, String word) {
		String INSERT_WORD =
			"INSERT INTO " + DATABASE_NAME + ".trending_words " +
			"(monitorer, word, count) values (?, ?, 1) ";
		
		String INCREMENT_WORD =
			"UPDATE " + DATABASE_NAME + ".trending_words " + 
			"SET count = count +1 " + 
			"WHERE monitorer = ? "+
			"AND word = ? ";
		
		try {
			if(wordExists(monitorer, word)) {
				jdbcTemplate.update(INCREMENT_WORD,
						new Object[] {monitorer.getUserName(), word});
			}
			else {
				jdbcTemplate.update(INSERT_WORD,
						new Object[] {monitorer.getUserName(), word});
				
			}
		}
		catch (Exception e) {
			String errMsg = "Exception caught in incrementWord: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
		}
	}

	private boolean wordExists(Monitorer monitorer, String word) {
		String COUNT_WORD =
			"SELECT COUNT(1) "+
			"FROM " + DATABASE_NAME + ".trending_words " +
			"WHERE monitorer = ? AND word = ? ";
		boolean response = false;
		
		try {
			int wordCount = jdbcTemplate.queryForObject(COUNT_WORD,
														new Object[] {monitorer.getUserName(), word},
														Integer.class);
			if(wordCount > 0) {
				response = true;
			}
		}
		catch (Exception e) {
			String errMsg = "Exception caught in wordExists: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
		}
		return response;
	}
	
	public List<Monitorer> getMonitorers() {
		String SELECT_MONITORERS =
			"SELECT user_name FROM " + DATABASE_NAME + ".monitorers ";
		List<Monitorer> monitorers = new ArrayList<Monitorer>();
		try {
			List<String> userNames = jdbcTemplate.queryForList(SELECT_MONITORERS, String.class);
			userNames.forEach(userName -> monitorers.add(new Monitorer(userName)));
		}
		catch (Exception e) {
			String errMsg = "Exception caught in getMonitorers: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
		}
		return monitorers;
	}

	public void cleanupWords() {
		String TRUNCATE_TABLE =
			"TRUNCATE TABLE " + DATABASE_NAME + ".trending_words ";
		try {
			jdbcTemplate.update(TRUNCATE_TABLE);
		}
		catch (Exception e) {
			String errMsg = "Exception caught in cleanupWords: " + e.getClass() + " - " + e.getMessage();
			logger.error(errMsg);
		}
	}
} 
