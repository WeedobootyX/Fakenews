package se.bubbelbubbel.fakenews.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.bubbelbubbel.fakenews.dao.NewsDAO;
import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.SnippetsNotFoundException;
import se.bubbelbubbel.fakenews.exception.StructureNotFoundException;
import se.bubbelbubbel.fakenews.model.Newsflash;
import se.bubbelbubbel.fakenews.model.QueuedNewsflash;
import se.bubbelbubbel.fakenews.model.Snippet;
import se.bubbelbubbel.fakenews.model.SnippetList;
import se.bubbelbubbel.fakenews.model.Structure;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@EnableScheduling
@Component
public class NewsService {
	Logger logger = LoggerFactory.getLogger(NewsService.class);

	@Autowired NewsDAO  newsDAO;
	
	@Value("${twitter.access.token}")
	String TWITTER_ACCESS_TOKEN;

	@Value("${twitter.access.token.secret}")
	String TWITTER_ACCESS_TOKEN_SECRET;
	
	@Value("${twitter.oauth.consumer.key}")
	String TWITTER_OAUTH_CONSUMER_KEY;
	
	@Value("${twitter.oauth.consumer.secret}")
	String TWITTER_OAUTH_CONSUMER_SECRET;
	
	/*HERE IS THE fourth dev HOTFIX*/
	public SnippetList getSnippetList(String snippetKey) throws DatabaseErrorException, SnippetsNotFoundException {
		logger.debug("getSnippetList for key: " + snippetKey);
		return newsDAO.getSnippetList(snippetKey);
	}
	
	public String getNewsMessage() throws DatabaseErrorException, StructureNotFoundException, SnippetsNotFoundException {
		Random random = new Random();
		int weightLimit = random.nextInt(10) + 1;
		List<Structure> structureList = newsDAO.getStructures(weightLimit);
		int selectIndex = random.nextInt(structureList.size());
		Structure selectedStructure = structureList.get(selectIndex);
		handleStructureCooldown(selectedStructure.getStructureKey());
		return makeNewsMessage(selectedStructure);
	}
	
	private void handleStructureCooldown(String structureKey) {
		newsDAO.setStructureCooldown(structureKey);
		newsDAO.decrementStructureCooldown();
	}
	private String makeNewsMessage(Structure structure) throws DatabaseErrorException, SnippetsNotFoundException {
		List<String> snippetKeys = structure.getSnippetKeys();
		String newsMessage = structure.getStructure();
		newsDAO.decrementSnippetCooldown(structure.getStructureKey());
		for(String snippetKey : snippetKeys) {
			String keyRegex = "{" + snippetKey + "}";
			Snippet selectedSnippet = getSnippetList(snippetKey).getRandomSnippet();
			newsMessage = newsMessage.replace(keyRegex, selectedSnippet.getSnippetText());
			newsDAO.setSnippetCooldown(structure.getStructureKey(), selectedSnippet.getSnippetId());
		}
		return newsMessage;
	}

	private void createNewNewsflash() throws DatabaseErrorException, StructureNotFoundException, SnippetsNotFoundException {
		logger.debug("createNewNewsflash");
		Newsflash newNewsflash =  new Newsflash();
		newNewsflash.setNewsText(getNewsMessage());
		newNewsflash.setStatus(Newsflash.STATUS_PENDING);
		GregorianCalendar sendTime = new GregorianCalendar();
		Random random = new Random();
		int minuteInterval = 30 + random.nextInt(60);
		sendTime.add(GregorianCalendar.MINUTE, minuteInterval);
		newNewsflash.setSendTime(sendTime);
		newsDAO.saveNewsflash(newNewsflash);

	}
	private void tweetNewsflash(Newsflash newNewsflash) {
		AccessToken accessToken = new AccessToken(TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(TWITTER_OAUTH_CONSUMER_KEY, TWITTER_OAUTH_CONSUMER_SECRET);
		twitter.setOAuthAccessToken(accessToken);
		try {
			Status status = twitter.updateStatus(newNewsflash.getNewsText());
		} catch (TwitterException e) {
			logger.error("TwitterException caught: " + e.getMessage());
		}
	}

	@Scheduled(fixedRate=60000)
	public void publishNews() {
		try {
			logger.debug("publishNews");
			if(getPendingNewsflashCount() == 0) {
				createNewNewsflash();
			}
			List<Newsflash> newsflashes = newsDAO.getUnpublishedNewsflashes();
			if(newsflashes.size() > 0) {
				Newsflash nextNewsflash = newsflashes.get(0);
				tweetNewsflash(nextNewsflash);
				nextNewsflash.setStatus(Newsflash.STATUS_PUBLISHED);
				newsDAO.saveNewsflash(nextNewsflash);
			}
		} catch (DatabaseErrorException | StructureNotFoundException | SnippetsNotFoundException e) {
			logger.error("Error in publishNews: " + e.getMessage());
		}
	}
	
	private int getPendingNewsflashCount() {
		return newsDAO.getPendingNewsflashCount();
	}

	public Structure getStructure(String structureKey) throws StructureNotFoundException, DatabaseErrorException{
		return newsDAO.getStructure(structureKey);
	}

	public String testStructure(String structureKey) throws StructureNotFoundException, DatabaseErrorException, SnippetsNotFoundException {
		Structure structure = getStructure(structureKey);
		StringBuffer sb  = new StringBuffer();
		for(int i = 0; i <100; i++) {
			sb.append(makeNewsMessage(structure) + "\n");
		}
		return sb.toString();
	}
	
	public String createTemplate(String structureKey) throws StructureNotFoundException, DatabaseErrorException {
		Structure structure = this.getStructure(structureKey);
		List<String> snippetKeys = structure.getSnippetKeys();
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO `snippets` (`snippet_key`, `snippet_text`, `status`, `snippet_id`) VALUES ");
		for(String snippetKey : snippetKeys) {
			for(int i = 0; i < 5; i++) {
				sb.append("\n(\'" + snippetKey + "\', \'\', \'active\', null),");
			}
		}
		return sb.toString();
	}
	public String improveStructure(String structureKey) throws StructureNotFoundException, DatabaseErrorException, SnippetsNotFoundException {
		Structure structure = this.getStructure(structureKey);
		List<String> snippetKeys = structure.getSnippetKeys();
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		for(String snippetKey : snippetKeys) {
			SnippetList snippetList = this.getSnippetList(snippetKey);
			for(Snippet snippet : snippetList.getSnippets()) {
				sb.append("UPDATE snippets set snippet_text='" + snippet.getSnippetText() + "' WHERE snippet_id='" + snippet.getSnippetId() + "';\n");
			}
			sb2.append("INSERT INTO `snippets` (`snippet_key`, `snippet_text`, `status`, `snippet_id`) VALUES ");
			sb2.append("\n(\'" + snippetKey + "\', \'\', \'active\', null),\n");
		}
		sb.append(sb2.toString());
		return sb.toString();
	}

	public String analysis() throws StructureNotFoundException, DatabaseErrorException, SnippetsNotFoundException {
		List<Structure> structureList = newsDAO.getActiveStuctures();
		StringBuffer sb = new StringBuffer();
		int structureCounter= 0;
		int comboCounter = 0;
		sb.append("STRUCTURE ANALYSIS \n");
		for(Structure structure : structureList) {
			List<Float> snippetNumbers = new ArrayList<Float>();
			StringBuffer snipCounts = new StringBuffer();
			snipCounts.append(" (");
			String comma = "";
			for(String snippetKey : structure.getSnippetKeys()) {
				SnippetList snippetList = newsDAO.getSnippetList(snippetKey);
				snippetNumbers.add(new Float(snippetList.getSnippets().size()));
				snipCounts.append(comma + snippetList.getSnippets().size());
				comma = ", ";
			}
			snipCounts.append(")");
			Float combos = new Float(1);
			for(Float fl : snippetNumbers) {
				combos = combos * fl.floatValue();
			}
			sb.append("Structure: " + structure.getStructureKey() + " has " + combos.intValue() + " combinations. " + snipCounts.toString() + "\n");
			structureCounter++;
			comboCounter = comboCounter + combos.intValue();
		}
		sb.append("Structures: " + structureCounter + "\n");
		sb.append("Combinations; " + comboCounter + "\n");
		return sb.toString();
	}

	public List<Newsflash> getUpcoming() throws DatabaseErrorException {
		return newsDAO.getUnpublishedNewsflashes();
	}

	public String addNewsflash(String queuedNewsflashJson) throws IOException, DatabaseErrorException {
		ObjectMapper jsonMapper = new ObjectMapper();
		QueuedNewsflash queuedNewsflash;
		try {
			queuedNewsflash = jsonMapper.readValue(queuedNewsflashJson, QueuedNewsflash.class);
			logger.debug("queuedNewsflash is: " + queuedNewsflash.toString());
			Newsflash newsflash = new Newsflash();
			newsflash.setNewsText(queuedNewsflash.getNewsText());
			newsflash.setStatus(Newsflash.STATUS_PENDING);
			GregorianCalendar nfDate = new GregorianCalendar();
			nfDate.add(GregorianCalendar.MINUTE, queuedNewsflash.getSendMinutes());
			newsflash.setSendTime(nfDate);
			newsDAO.saveNewsflash(newsflash);
			return "OK";
		} 
		catch (IOException e) {
			logger.error("IOException caught in addNewsflash: " + e.getMessage());
			throw e;
		} catch (DatabaseErrorException e) {
			logger.error("DatabaseErrorException caught in addNewsflash: " + e.getMessage());
			throw e;
		}
	}
	
}
