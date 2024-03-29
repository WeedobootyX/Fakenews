package se.bubbelbubbel.fakenews.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.bubbelbubbel.fakenews.dao.NewsDAO;
import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.IllegalNewsflashException;
import se.bubbelbubbel.fakenews.exception.SnippetsNotFoundException;
import se.bubbelbubbel.fakenews.exception.StructureNotFoundException;
import se.bubbelbubbel.fakenews.exception.SystemParameterNotFoundException;
import se.bubbelbubbel.fakenews.model.Newsflash;
import se.bubbelbubbel.fakenews.model.QueuedNewsflash;
import se.bubbelbubbel.fakenews.model.Snippet;
import se.bubbelbubbel.fakenews.model.SnippetList;
import se.bubbelbubbel.fakenews.model.Structure;

@EnableScheduling
@Component
public class NewsService {
	Logger logger = LoggerFactory.getLogger(NewsService.class);

	@Autowired NewsDAO  newsDAO;
		
	@Autowired SystemService systemService;

	@Autowired TweetService tweetService;
	
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
		Random random = new Random();
		List<Newsflash> recycledNewsflashes = newsDAO.getRecycledNewsflashes();
		Newsflash newNewsflash =  new Newsflash();
		if(recycledNewsflashes.size() > 10 && random.nextInt(100) > 20) { // 20% recycled if there are any
			Newsflash recycledNewsflash = recycledNewsflashes.get(random.nextInt(recycledNewsflashes.size()));
			newNewsflash.setNewsText(recycledNewsflash.getNewsText());
			newsDAO.lockManualNewsflash(recycledNewsflash.getNewsflashId());
		}
		else {
			newNewsflash.setNewsText(getNewsMessage());
		}
		newNewsflash.setStatus(Newsflash.STATUS_PENDING);
		GregorianCalendar sendTime = new GregorianCalendar();
		int minuteInterval = 30 + random.nextInt(60);
		sendTime.add(GregorianCalendar.MINUTE, minuteInterval);
		newNewsflash.setSendTime(sendTime);
		newsDAO.saveNewsflash(newNewsflash);
	}
	
//	private void tweetNewsflash(Newsflash newNewsflash) throws DatabaseErrorException, SystemParameterNotFoundException {
//		logger.debug("tweetNewsflash");
//		String twitterAccessToken = systemService.getSystemParameter("TWITTER_ACCESS_TOKEN");
//
//		String twitterAccessTokenSecret = systemService.getSystemParameter("TWITTER_ACCESS_TOKEN_SECRET");
//		
//		String twitterOauthConsumerKey = systemService.getSystemParameter("TWITTER_OAUTH_CONSUMER_KEY");
//		
//		String twitterOauthConsumerSecret = systemService.getSystemParameter("TWITTER_OAUTH_CONSUMER_SECRET");
//
//		AccessToken accessToken = new AccessToken(twitterAccessToken, twitterAccessTokenSecret);
//		Twitter twitter = new TwitterFactory().getInstance();
//		twitter.setOAuthConsumer(twitterOauthConsumerKey, twitterOauthConsumerSecret);
//		twitter.setOAuthAccessToken(accessToken);
//		try {
//			Status status = twitter.updateStatus(newNewsflash.getNewsText());
//		} 
//		catch (TwitterException e) {
//			logger.error("TwitterException caught: " + e.getMessage());
//		}
//		catch (Exception e) {
//			logger.error("Exception caught: " + e.getClass() + " - " + e.getMessage());
//		}
//	}
//
	@Scheduled(fixedRate=60000)
	public void publishNews() {
		try {
			logger.debug("publishNews. Ja, ny version");
			if(getPendingNewsflashCount() == 0) {
				logger.debug("no pending newsflashes. Creating new");
				createNewNewsflash();
			}
			List<Newsflash> newsflashes = newsDAO.getUnpublishedNewsflashes();
			if(newsflashes.size() > 0) {
				Newsflash nextNewsflash = newsflashes.get(0);
				tweetService.tweetText(nextNewsflash.getNewsText(), "FAKENEWS");
				nextNewsflash.setStatus(Newsflash.STATUS_PUBLISHED);
				newsDAO.saveNewsflash(nextNewsflash);
			}
		} catch (DatabaseErrorException | StructureNotFoundException | SnippetsNotFoundException e) {
			logger.error("Error in publishNews: " + e.getMessage());
		} catch (SystemParameterNotFoundException e) {
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
		return newsDAO.getUpcomingNewsflashes();
	}

	public String addNewsflash(String queuedNewsflashJson) throws IOException, DatabaseErrorException, IllegalNewsflashException {
		ObjectMapper jsonMapper = new ObjectMapper();
		QueuedNewsflash queuedNewsflash;
		try {
			queuedNewsflash = jsonMapper.readValue(queuedNewsflashJson, QueuedNewsflash.class);
			if(queuedNewsflash.getNewsText().isEmpty() || queuedNewsflash.getSendMinutes() == 0) {
				throw new IllegalNewsflashException("Illegal newsflash rejected");
			}
			logger.debug("queuedNewsflash is: " + queuedNewsflash.toString());
			Newsflash newsflash = new Newsflash();
			newsflash.setNewsText(queuedNewsflash.getNewsText());
			newsflash.setStatus(Newsflash.STATUS_PENDING);
			GregorianCalendar nfDate = new GregorianCalendar();
			nfDate.add(GregorianCalendar.MINUTE, queuedNewsflash.getSendMinutes());
			newsflash.setSendTime(nfDate);
			newsDAO.saveNewsflash(newsflash);
			if(queuedNewsflash.isRecycle()) {
				newsDAO.saveManualNewsflash(newsflash);
			}
			return "Newsflash saved";
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
