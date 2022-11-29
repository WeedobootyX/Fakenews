package se.bubbelbubbel.fakenews.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.bubbelbubbel.fakenews.service.TweetService;

@CrossOrigin
@RestController
public class TestController {
	Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired TweetService tweetService;

	@RequestMapping(value="/test", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public String test() {
		logger.debug("WE HAVE A CONNECTION");
		return "WE HAVE A CONNECTION";
	}
}
