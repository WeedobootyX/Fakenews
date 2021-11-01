package se.bubbelbubbel.fakenews.service;

import java.io.IOException;
import java.time.LocalDateTime;
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
import se.bubbelbubbel.fakenews.model.Newsflash;
import se.bubbelbubbel.fakenews.model.Tweet;
import se.bubbelbubbel.fakenews.model.TweetRequest;
import se.bubbelbubbel.fakenews.model.TweetScheduleEntry;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
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
			if(tweetRequest.getText().length() > 280) {
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
}
