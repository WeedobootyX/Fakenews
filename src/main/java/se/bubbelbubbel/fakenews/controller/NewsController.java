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
import se.bubbelbubbel.fakenews.exception.SnippetsNotFoundException;
import se.bubbelbubbel.fakenews.exception.StructureNotFoundException;
import se.bubbelbubbel.fakenews.model.Newsflash;
import se.bubbelbubbel.fakenews.model.SnippetList;
import se.bubbelbubbel.fakenews.service.NewsService;

@CrossOrigin
@RestController
public class NewsController {
	Logger logger = LoggerFactory.getLogger(NewsController.class);

	@Autowired NewsService newsService;
	
	@RequestMapping(value="/test/snippets/{snippetKey}", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public SnippetList getSnippets(@PathVariable("snippetKey") String snippetKey) throws DatabaseErrorException, SnippetsNotFoundException {
		logger.debug("in getSnippets");
		return newsService.getSnippetList(snippetKey);
	}

	@RequestMapping(value="/test/{structureKey}", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public String testStructure(@PathVariable("structureKey") String structureKey) throws DatabaseErrorException, SnippetsNotFoundException, StructureNotFoundException {
		return newsService.testStructure(structureKey);
	}

	@RequestMapping(value="/template/{structureKey}", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public String createTemplate(@PathVariable("structureKey") String structureKey) throws DatabaseErrorException, SnippetsNotFoundException, StructureNotFoundException {
		logger.debug("createTemplate for structureKey: " + structureKey);
		return newsService.createTemplate(structureKey);
	}

	@RequestMapping(value="/improve/{structureKey}", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public String improveStructure(@PathVariable("structureKey") String structureKey) throws DatabaseErrorException, SnippetsNotFoundException, StructureNotFoundException {
		return newsService.improveStructure(structureKey);
	}

	@RequestMapping(value="/analysis", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public String analysis() throws DatabaseErrorException, SnippetsNotFoundException, StructureNotFoundException {
		return newsService.analysis();
	}

	@RequestMapping(value="/upcoming", method=RequestMethod.GET,
			headers={"Accept=application/json"})
	public List<Newsflash> upcoming() throws DatabaseErrorException {
		return newsService.getUpcoming();
	}

	@RequestMapping(value="/newsflash/add", method=RequestMethod.POST,
			headers={"Accept=application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody String addNewsflash(@RequestBody String newsflashJson) throws IOException, DatabaseErrorException, IllegalNewsflashException {  
		logger.debug("Adding newsflash. newsflashJson: " + newsflashJson);
		return newsService.addNewsflash(newsflashJson);
	}
	
	@ExceptionHandler(IllegalNewsflashException.class)
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
