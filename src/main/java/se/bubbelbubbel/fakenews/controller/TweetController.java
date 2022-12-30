package se.bubbelbubbel.fakenews.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.IllegalNewsflashException;
import se.bubbelbubbel.fakenews.exception.IllegalTweetRequestException;
import se.bubbelbubbel.fakenews.model.MonitoredTweet;
import se.bubbelbubbel.fakenews.model.TrendingWord;
import se.bubbelbubbel.fakenews.service.TweetService;

@CrossOrigin
@RestController
public class TweetController {
	Logger logger = LoggerFactory.getLogger(TweetController.class);
/*test git*/
	@Autowired TweetService tweetService;
	
	@RequestMapping(value="/tweet/add", method=RequestMethod.POST,
			headers={"Accept=application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody String addTweet(@RequestBody String tweetRequestJson) throws IOException, DatabaseErrorException, IllegalTweetRequestException {  
		logger.debug("Adding tweet. tweetRequestJson: " + tweetRequestJson);
		return tweetService.addTweet(tweetRequestJson);
	}
	
	@RequestMapping(value="/monitoredaccounts/add", method=RequestMethod.POST,
			headers={"Accept=application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody String addMonitoredAccounts(@RequestBody String addAccountsRequestJson) throws IOException, DatabaseErrorException, IllegalTweetRequestException {  
		logger.debug("addMonitoredAccounts. addAccountsRequestJson: " + addAccountsRequestJson);
		tweetService.addMonitoredAccounts(addAccountsRequestJson);
		return "Done";
	}

	@RequestMapping(value="/trending/{monitorerUserName}", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public List<TrendingWord> trending(@PathVariable("monitorerUserName") String monitorerUserName) throws DatabaseErrorException {
		logger.debug("trending for monitorerUserName: " + monitorerUserName);
		return tweetService.getTrendingWords(monitorerUserName);
	}

	@RequestMapping(value="/word/{monitorerUserName}/{word}", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public List<MonitoredTweet> getTweetsByWord(@PathVariable("monitorerUserName") String monitorerUserName,
									 			@PathVariable("word") String word) throws DatabaseErrorException {
		logger.debug("getTweetsByWord for monitorerUserName: " + monitorerUserName +" and word: " + word);
		return tweetService.getTweetsByWord(monitorerUserName, word);
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
