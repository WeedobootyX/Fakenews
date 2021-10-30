package se.bubbelbubbel.fakenews.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.bubbelbubbel.fakenews.dao.TweetDAO;
import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.IllegalTweetRequestException;
import se.bubbelbubbel.fakenews.model.Tweet;
import se.bubbelbubbel.fakenews.model.TweetRequest;
import se.bubbelbubbel.fakenews.model.TweetScheduleEntry;

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
}
