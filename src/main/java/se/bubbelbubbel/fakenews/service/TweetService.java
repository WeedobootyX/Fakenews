package se.bubbelbubbel.fakenews.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.bubbelbubbel.fakenews.dao.NewsDAO;
import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.IllegalTweetRequestException;
import se.bubbelbubbel.fakenews.model.TweetRequest;

@EnableScheduling
@Component
public class TweetService {
	Logger logger = LoggerFactory.getLogger(TweetService.class);

	@Autowired NewsDAO  newsDAO;
		
	@Autowired SystemService systemService;
	
	public String addTweet(String tweetRequestJson) throws IOException, DatabaseErrorException, IllegalTweetRequestException {
		logger.debug("Adding tweet: " + tweetRequestJson);
		ObjectMapper jsonMapper = new ObjectMapper();
		TweetRequest tweetRequest;
		try {
			tweetRequest = jsonMapper.readValue(tweetRequestJson, TweetRequest.class);
			if(!tweetRequest.isValid()) {
				throw new IllegalTweetRequestException("Illegal tweet request rejected");
			}
			return "Tweet saved saved";
		} 
		catch (IOException e) {
			logger.error("IOException caught in addTweet: " + e.getMessage());
			throw e;
		} 
//		catch (DatabaseErrorException e) {
//			logger.error("DatabaseErrorException caught in addTweet: " + e.getMessage());
//			throw e;
//		}
	}
}
