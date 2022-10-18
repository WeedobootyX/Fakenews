package se.bubbelbubbel.fakenews.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import se.bubbelbubbel.fakenews.model.test.AppVersion;
import se.bubbelbubbel.fakenews.service.TweetService;

@CrossOrigin
@RestController
public class TestController {
	Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired TweetService tweetService;

	@RequestMapping(value="/apps", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public List<AppVersion> getAppVersions() {
		logger.debug("getAppVersions");
		List<AppVersion> appVersions = new ArrayList();
		appVersions.add(new AppVersion("2021.09", "Android", "retired"));
		appVersions.add(new AppVersion("2022.01", "Android", "deprecated"));
		appVersions.add(new AppVersion("2022.02", "Android", "deprecated"));
		appVersions.add(new AppVersion("2022.03", "Android", "retired"));
		appVersions.add(new AppVersion("2022.04", "Android", "retired"));
		appVersions.add(new AppVersion("2022.05", "Android", "retired"));
		appVersions.add(new AppVersion("2021.09", "iOS", "retired"));
		appVersions.add(new AppVersion("2022.01", "iOS", "retired"));
		appVersions.add(new AppVersion("2022.02", "iOS", "deprecated"));
		appVersions.add(new AppVersion("2022.03", "iOS", "active"));
		appVersions.add(new AppVersion("2022.04", "iOS", "active"));
		appVersions.add(new AppVersion("2022.05", "iOS", "active"));
		
		return appVersions;
	}
}
