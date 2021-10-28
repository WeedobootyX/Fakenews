package se.bubbelbubbel.fakenews.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.IllegalNewsflashException;
import se.bubbelbubbel.fakenews.exception.IllegalTweetRequestException;
import se.bubbelbubbel.fakenews.service.TweetService;

@CrossOrigin
@RestController
public class TweetController {
	Logger logger = LoggerFactory.getLogger(TweetController.class);

	@Autowired TweetService tweetService;
	@RequestMapping(value="/tweet/add", method=RequestMethod.POST,
			headers={"Accept=application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody String addTweet(@RequestBody String tweetRequestJson) throws IOException, DatabaseErrorException, IllegalTweetRequestException {  
		logger.debug("Adding tweet. tweetRequestJson: " + tweetRequestJson);
		return tweetService.addTweet(tweetRequestJson);
	}
	
	@ExceptionHandler(IllegalTweetRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleIllegalNewsflashException(IllegalNewsflashException ex) {
		return ex.getMessage();
	}

	@ExceptionHandler(DatabaseErrorException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleDatabaseErrorException(DatabaseErrorException ex) {
		return ex.getMessage();
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleIOException(IOException ex) {
		return ex.getMessage();
	}
}
