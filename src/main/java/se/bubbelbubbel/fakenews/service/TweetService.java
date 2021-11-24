package se.bubbelbubbel.fakenews.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.bubbelbubbel.fakenews.dao.TweetDAO;
import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.IllegalTweetRequestException;
import se.bubbelbubbel.fakenews.exception.SnippetsNotFoundException;
import se.bubbelbubbel.fakenews.exception.StructureNotFoundException;
import se.bubbelbubbel.fakenews.exception.SystemParameterNotFoundException;
import se.bubbelbubbel.fakenews.model.MonitoredAccount;
import se.bubbelbubbel.fakenews.model.Monitorer;
import se.bubbelbubbel.fakenews.model.Newsflash;
import se.bubbelbubbel.fakenews.model.StatusUpdate;
import se.bubbelbubbel.fakenews.model.Tweet;
import se.bubbelbubbel.fakenews.model.TweetRequest;
import se.bubbelbubbel.fakenews.model.TweetScheduleEntry;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

@EnableScheduling
@Component
public class TweetService {
	Logger logger = LoggerFactory.getLogger(TweetService.class);

	@Autowired TweetDAO tweetDAO;
		
	@Autowired SystemService systemService;
	
	public String addTweet(String tweetRequestJson) throws IOException, DatabaseErrorException, IllegalTweetRequestException {
		logger.debug("Adding tweet: " + tweetRequestJson);
		ObjectMapper jsonMapper = new ObjectMapper();
		TweetRequest tweetRequest;
		try {
			tweetRequest = jsonMapper.readValue(tweetRequestJson, TweetRequest.class);
			if(tweetRequest.getText().length() > 280 || tweetRequest.getText().length() == 0) {
				throw new IllegalTweetRequestException("Illegal tweet request rejected");
			}
			tweetRequest = tweetDAO.saveTweetRequest(tweetRequest);
			List<TweetScheduleEntry> scheduleEntries = tweetRequest.getScheduleEntries();
			for(TweetScheduleEntry scheduledEntry : scheduleEntries) {
				saveTweet(new Tweet(tweetRequest.getText(), scheduledEntry, tweetRequest.getId()));
			}
//			scheduleEntries.stream() {
//						   .filter(entry -> entry.getTimeValue() > 0)
//						   .forEach(entry -> saveTweet(new Tweet(tweetRequest.getText(), entry, tweetRequest.getId())));
			return "Tweet saved and scheduled";
		} 
		catch (IOException e) {
			logger.error("IOException caught in addTweet: " + e.getMessage());
			throw e;
		} 
	}

	private void saveTweet(Tweet tweet) throws DatabaseErrorException {
		logger.debug("saveTweet");
		tweetDAO.saveTweet(tweet);
	}

	@Scheduled(fixedRate=60000)
	public void publishTweets() {
		logger.debug("publishTweets");
		List<Tweet> tweets = tweetDAO.getUnpublishedTweets();
		tweets.forEach(tweet -> sendTweet(tweet));
	}

	private void sendTweet(Tweet tweet) {
		logger.debug("sendTweet: " + tweet.getId());
		tweet.setSentTime(LocalDateTime.now());
		tweet.setStatus(Tweet.SENT);
		try {
			tweetText(tweet.getText(), "SCHEDULER");
			saveTweet(tweet);
		} catch (DatabaseErrorException e) {
			logger.error("Error in sendTweet: " + e.getMessage());
		} catch (SystemParameterNotFoundException e) {
			logger.error("Error in sendTweet: " + e.getMessage());
		}
	}

	public void tweetText(String text, String prefix) throws DatabaseErrorException, SystemParameterNotFoundException {
		logger.debug("tweetText: " + text + ", prefix: " + prefix);
		String twitterAccessToken = systemService.getSystemParameter(prefix + "_TWITTER_ACCESS_TOKEN");
		String twitterAccessTokenSecret = systemService.getSystemParameter(prefix + "_TWITTER_ACCESS_TOKEN_SECRET");
		String twitterOauthConsumerKey = systemService.getSystemParameter(prefix + "_TWITTER_OAUTH_CONSUMER_KEY");
		String twitterOauthConsumerSecret = systemService.getSystemParameter(prefix + "_TWITTER_OAUTH_CONSUMER_SECRET");
		AccessToken accessToken = new AccessToken(twitterAccessToken, twitterAccessTokenSecret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(twitterOauthConsumerKey, twitterOauthConsumerSecret);
		twitter.setOAuthAccessToken(accessToken);
		try {
			Status status = twitter.updateStatus(text);
		} 
		catch (TwitterException e) {
			logger.error("TwitterException caught: " + e.getMessage());
		}
		catch (Exception e) {
			logger.error("Exception caught: " + e.getClass() + " - " + e.getMessage());
		}
	}
	
	@Scheduled(fixedRate=3600000)
	public void monitorTweets() {
		logger.info("Tweet monitoring begins");
		List<Monitorer> monitorers = tweetDAO.getMonitorers();
		cleanupOldStatuses();
		cleanupWords();
		monitorers.forEach(monitorer -> getStatusUpdates(monitorer));
		logger.info("building word analysis");
		monitorers.forEach(monitorer -> buildTrendingWords(monitorer));
		logger.info("Tweet monitoring completed");
	}
	
	private void getStatusUpdates(Monitorer monitorer) {
		String prefix = "SCHEDULER";
		try {
			List<MonitoredAccount> monitoredAccounts = tweetDAO.getMonitoredAccounts(monitorer);
			String twitterAccessToken = systemService.getSystemParameter(prefix + "_TWITTER_ACCESS_TOKEN");
			String twitterAccessTokenSecret = systemService.getSystemParameter(prefix + "_TWITTER_ACCESS_TOKEN_SECRET");
			String twitterOauthConsumerKey = systemService.getSystemParameter(prefix + "_TWITTER_OAUTH_CONSUMER_KEY");
			String twitterOauthConsumerSecret = systemService.getSystemParameter(prefix + "_TWITTER_OAUTH_CONSUMER_SECRET");
			AccessToken accessToken = new AccessToken(twitterAccessToken, twitterAccessTokenSecret);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(twitterOauthConsumerKey, twitterOauthConsumerSecret);
			twitter.setOAuthAccessToken(accessToken);
			monitoredAccounts.forEach(monitoredAccount -> processStatusUpdates(monitoredAccount, twitter, monitorer));
		}
		catch (Exception e) {
			logger.error("Exception caught in getStatusUpdates: " + e.getClass() + " - " + e.getMessage());
		}
	}

	private void processStatusUpdates(MonitoredAccount monitoredAccount, Twitter twitter, Monitorer monitorer) {
		logger.debug("processStatusUpdates for: " + monitoredAccount.getUserName());
		try {
			if(monitoredAccount.getUserId() == 0 || monitoredAccount.getName() == null) {
				setupMonitoredAccount(monitoredAccount, twitter);
			}
			ResponseList<Status> statusList;
			statusList = twitter.getUserTimeline(monitoredAccount.getUserId());
			statusList.forEach(status -> saveStatus(status, monitorer));
		} catch (TwitterException e) {
			logger.error("Exception caught in processStatusUpdates: " + e.getClass() + " - " + e.getMessage());
		} catch (DatabaseErrorException e) {
		}
	}

	private void saveStatus(Status status, Monitorer monitorer) {
		tweetDAO.saveStatus(status,  monitorer);
	}

	private void cleanupOldStatuses() {
		tweetDAO.statusCleanup();
	}
	
	private void cleanupWords() {
		tweetDAO.cleanupWords();
	}
	
	private void setupMonitoredAccount(MonitoredAccount monitoredAccount, Twitter twitter) throws DatabaseErrorException {
		logger.debug("setupMonitoredAccount for: " + monitoredAccount.getUserName());
		User user;
		try {
			user = twitter.showUser(monitoredAccount.getUserName());
			monitoredAccount.setName(user.getName());
			monitoredAccount.setUserId(user.getId());
			monitoredAccount.setImageURL(user.get400x400ProfileImageURLHttps());
			tweetDAO.saveMonitoredAccount(monitoredAccount);
		} catch (TwitterException e) {
			logger.error("Exception caught in getStatusUpdates: " + e.getClass() + " - " + e.getMessage());
		}
	}

	private void buildTrendingWords(Monitorer monitorer) {
		List<StatusUpdate> statusUpdates = tweetDAO.getMonitoredStatusUpdates(monitorer);
		statusUpdates.forEach(statusUpdate -> processWords(monitorer, statusUpdate));
	}

	private void processWords(Monitorer monitorer, StatusUpdate statusUpdate) {
		List<String> words = Arrays.asList(statusUpdate.getText().split(" "));
		words.forEach(word -> processWord(monitorer, word));
	}

	private void processWord(Monitorer monitorer, String word) {
		String cleanWord = word.replace(".", "")
							   .replace(",", "")
							   .replace("!", "")
							   .replace("?", "")
							   .replace("\"", "");
		tweetDAO.incrementWord(monitorer, cleanWord);
	}
}
