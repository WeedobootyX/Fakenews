package se.bubbelbubbel.fakenews.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import se.bubbelbubbel.fakenews.exception.DatabaseErrorException;
import se.bubbelbubbel.fakenews.exception.NewsflashNotFoundException;
import se.bubbelbubbel.fakenews.exception.StructureNotFoundException;
import se.bubbelbubbel.fakenews.exception.SnippetsNotFoundException;
import se.bubbelbubbel.fakenews.model.Structure;
import se.bubbelbubbel.fakenews.model.Newsflash;
import se.bubbelbubbel.fakenews.model.Snippet;
import se.bubbelbubbel.fakenews.model.SnippetList;
import se.bubbelbubbel.fakenews.rowmapper.NewsflashRowMapper;
import se.bubbelbubbel.fakenews.rowmapper.SnippetRowMapper;
import se.bubbelbubbel.fakenews.rowmapper.StructureRowMapper;

@Component
public class NewsDAO {
	Logger logger = LoggerFactory.getLogger(NewsDAO.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	
	@Value("${fakenews.databaseName}")
	String DATABASE_NAME;

	public SnippetList getSnippetList(String snippetKey) throws DatabaseErrorException, SnippetsNotFoundException {
		logger.debug("getSnippetList, snippetKey: " + snippetKey);
		String SELECT_SNIPPETS_BY_SNIPPET_KEY =
				"SELECT " + SnippetRowMapper.SNIPPET_COLUMN_LIST +
				"FROM " + DATABASE_NAME + ".snippets " +
				"WHERE snippet_key = ? " +
				"AND status = ? " +
				"AND snippet_id NOT IN " +
				"(SELECT snippet_id FROM " + DATABASE_NAME + ".snippet_cooldowns) ";
		try {
			List<Snippet> snippets = jdbcTemplate.query(SELECT_SNIPPETS_BY_SNIPPET_KEY,
																new Object[] {snippetKey, Snippet.STATUS_ACTIVE},
																new SnippetRowMapper());
			SnippetList snippetList = new SnippetList();
			snippetList.setSnippets(snippets);
			return snippetList;
		}
		catch (EmptyResultDataAccessException ere) {
			String errorMsg = "No snippets with key: " + snippetKey + " were found";
			logger.error(errorMsg);
			throw new SnippetsNotFoundException(errorMsg);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getSnippetList: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}

	public SnippetList getAllSnippetList(String snippetKey) throws DatabaseErrorException, SnippetsNotFoundException {
		logger.debug("getAllSnippetList, snippetKey: " + snippetKey);
		String SELECT_SNIPPETS_BY_SNIPPET_KEY =
				"SELECT " + SnippetRowMapper.SNIPPET_COLUMN_LIST +
				"FROM " + DATABASE_NAME + ".snippets " +
				"WHERE snippet_key = ? " +
				"AND status = ? ";
		try {
			List<Snippet> snippets = jdbcTemplate.query(SELECT_SNIPPETS_BY_SNIPPET_KEY,
																new Object[] {snippetKey, Snippet.STATUS_ACTIVE},
																new SnippetRowMapper());
			SnippetList snippetList = new SnippetList();
			snippetList.setSnippets(snippets);
			return snippetList;
		}
		catch (EmptyResultDataAccessException ere) {
			String errorMsg = "No snippets with key: " + snippetKey + " were found";
			logger.error(errorMsg);
			throw new SnippetsNotFoundException(errorMsg);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getAllSnippetList: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}

	public List<Structure> getStructures(int weightLimit) throws DatabaseErrorException, StructureNotFoundException {
		String SELECT_STRUCTURES_BY_WEIGHT =
				"SELECT " + StructureRowMapper.STRUCTURE_COLUMN_LIST +
				"FROM " + DATABASE_NAME + ".structures " +
				"WHERE status = ? " +
				"AND weight >= ? " +
				"AND cooldown_counter = 0 ";
		try {
			List<Structure> structures = jdbcTemplate.query(SELECT_STRUCTURES_BY_WEIGHT,
																new Object[] {Structure.STATUS_ACTIVE,
																			  weightLimit},
																new StructureRowMapper());
			return structures;
		}
		catch (EmptyResultDataAccessException ere) {
			String errorMsg = "No structures were found";
			logger.error(errorMsg);
			throw new StructureNotFoundException(errorMsg);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getSnippetList: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}

	public List<Newsflash> getUnpublishedNewsflashes() throws DatabaseErrorException {
		String SELECT_UNPUBLISHED_NEWSFLASHES =
				"SELECT " + NewsflashRowMapper.NEWSFLASH_COLUMN_LIST +
				"FROM " + DATABASE_NAME + ".newsflashes " +
				"WHERE status = ? " +
				"AND send_time < now() " +
				"ORDER BY send_time ";
		try {
			List<Newsflash> newsflashes = jdbcTemplate.query(SELECT_UNPUBLISHED_NEWSFLASHES,
																new Object[] {Newsflash.STATUS_PENDING},
																new NewsflashRowMapper());
			return newsflashes;
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getNextNewsflash: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
		
	}

	public void saveNewsflash(Newsflash newsflash) throws DatabaseErrorException {
		logger.debug("saveNewsflash" );
		String UPDATE_NEWSFLASH =
				"UPDATE " + DATABASE_NAME + ".newsflashes " +
				"SET send_time=?, news_text=?, status=? " +
				"WHERE newsflash_id=? ";
		String INSERT_NEWSFLASH = 
				"INSERT INTO " + DATABASE_NAME + ".newsflashes " +
				"(send_time, news_text, status) " +
				"VALUES (?, ?, ?) ";

		try {
			if(newsflash.getNewsflashId() == 0) {
				jdbcTemplate.update(INSERT_NEWSFLASH,
						new Object[] {newsflash.getSendTime().getTime(),
								newsflash.getNewsText(),
								newsflash.getStatus()});
			}
			else {
				jdbcTemplate.update(UPDATE_NEWSFLASH,
						new Object[] {newsflash.getSendTime().getTime(),
								newsflash.getNewsText(),
								newsflash.getStatus(),
								newsflash.getNewsflashId()});
			}
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in saveNewsflash for id: " + newsflash.getNewsflashId() + " - " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}

		
	}

	public Structure getStructure(String structureKey) throws StructureNotFoundException, DatabaseErrorException {
		String SELECT_STRUCTURE_BY_KEY =
				"SELECT " + StructureRowMapper.STRUCTURE_COLUMN_LIST +
				"FROM " + DATABASE_NAME + ".structures " +
				"WHERE structure_key = ? ";
		try {
			Structure structure = jdbcTemplate.queryForObject(SELECT_STRUCTURE_BY_KEY,
																new Object[] {structureKey},
																new StructureRowMapper());
			return structure;
		}
		catch (EmptyResultDataAccessException ere) {
			String errorMsg = "No structure with key: " + structureKey + " was found";
			logger.error(errorMsg);
			throw new StructureNotFoundException(errorMsg);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getStructure: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}

	public int getPendingNewsflashCount() {
		String SELECT_COUNT_NEWSFLASHES =
				"SELECT COUNT(1) " +
						"FROM " + DATABASE_NAME + ".newsflashes " +
						"WHERE status = ? ";
		try {
			int numRows = jdbcTemplate.queryForObject(SELECT_COUNT_NEWSFLASHES,
					new Object[] {Newsflash.STATUS_PENDING},
					Integer.class);
			return numRows;
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getPendingNewsflashCount: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			return 0;
		}
	}
	
	public void logNewsflash(String structureKey, String newsText) {
		String INSERT_NEWSFLASH_LOG =
				"INSERT INTO " + DATABASE_NAME + ".newslash_logs " +
				"(sent_time, news_text, structure_key) VALUES (now(), ?, ?) ";
		try {
			jdbcTemplate.update(INSERT_NEWSFLASH_LOG,
								new Object[] {structureKey, newsText});
		}
		catch (Exception e) {
			String errorMsg = "Exception caught in logNewsflash: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}

	public void setStructureCooldown(String structureKey) {
		String SET_COOLDOWN =
				"UPDATE " + DATABASE_NAME + ".structures " + 
				"SET cooldown_counter = 3 " +
				"WHERE structure_key = ? ";
		try {
			jdbcTemplate.update(SET_COOLDOWN,
					new Object[] {structureKey});
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in setStructureCooldown for structureKey: " + structureKey + ": " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}
	
	public void decrementStructureCooldown() {
		String DECREMENT_COOLDOWN =
				"UPDATE " + DATABASE_NAME + ".structures " + 
				"SET cooldown_counter = cooldown_counter - 1 " +
				"WHERE cooldown_counter> 0 ";
		try {
			jdbcTemplate.update(DECREMENT_COOLDOWN);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in decrementStructureCooldown: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}
	public void setSnippetCooldown(String structureKey, int snippetId) {
		String SET_COOLDOWN =
				"INSERT INTO " + DATABASE_NAME + ".snippet_cooldowns " + 
				"(structure_key, snippet_id, cooldown_counter) VALUES (?, ?, 2) ";
		try {
			jdbcTemplate.update(SET_COOLDOWN,
					new Object[] {structureKey, 
								  snippetId});
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in setSnippetCooldown for snippetKey: " + snippetId + ": " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}
	
	public void decrementSnippetCooldown(String structureKey) {
		String DECREMENT_COOLDOWN =
				"UPDATE " + DATABASE_NAME + ".snippet_cooldowns " + 
				"SET cooldown_counter = cooldown_counter - 1 " +
				"WHERE structure_key = ? ";
		String CLEANUP_COOLDOWNS =
				"DELETE FROM " + DATABASE_NAME + ".snippet_cooldowns " +
				"WHERE cooldown_counter < 1 ";
		try {
			jdbcTemplate.update(DECREMENT_COOLDOWN,
					new Object[] {structureKey});
			jdbcTemplate.update(CLEANUP_COOLDOWNS);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in decrementSnippetCooldown for snippetKey: " + structureKey + ": " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
		}
	}

	public List<Structure> getActiveStuctures() throws StructureNotFoundException, DatabaseErrorException {
		String SELECT_STRUCTURES =
				"SELECT " + StructureRowMapper.STRUCTURE_COLUMN_LIST +
				"FROM " + DATABASE_NAME + ".structures " +
				"WHERE status = ? ";
		try {
			List<Structure> structures = jdbcTemplate.query(SELECT_STRUCTURES,
																new Object[] {Structure.STATUS_ACTIVE},
																new StructureRowMapper());
			return structures;
		}
		catch (EmptyResultDataAccessException ere) {
			String errorMsg = "No structures were found";
			logger.error(errorMsg);
			throw new StructureNotFoundException(errorMsg);
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getActiveStructures: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
	}

	public List<Newsflash> getUpcomingNewsflashes() throws DatabaseErrorException {
		String SELECT_UPCOMING_NEWSFLASHES =
				"SELECT " + NewsflashRowMapper.NEWSFLASH_COLUMN_LIST +
				"FROM " + DATABASE_NAME + ".newsflashes " +
				"WHERE status = ? " +
				"ORDER BY send_time ";
		try {
			List<Newsflash> newsflashes = jdbcTemplate.query(SELECT_UPCOMING_NEWSFLASHES,
															 new Object[] {Newsflash.STATUS_PENDING},
															 new NewsflashRowMapper());
			return newsflashes;
		}
		catch (Exception e) {
			String errorMsg = " Exception caught in getUpcomingNewsflashes: " + e.getClass() + " with message: " + e.getMessage();
			logger.error(errorMsg);
			throw new DatabaseErrorException(errorMsg);
		}
		
	}

}
