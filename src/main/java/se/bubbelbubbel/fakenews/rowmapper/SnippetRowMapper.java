package se.bubbelbubbel.fakenews.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import se.bubbelbubbel.fakenews.model.Snippet;

public class SnippetRowMapper implements RowMapper <Snippet> {
	public static final String SNIPPET_COLUMN_LIST =
			" snippet_key, snippet_text, status, snippet_id ";

	public Snippet mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
		Snippet snippet = new Snippet();
		snippet.setSnippetKey(rs.getString(1));
		snippet.setSnippetText(rs.getString(2));
		snippet.setStatus(rs.getString(3));
		snippet.setSnippetId(rs.getInt(4));
		return snippet;
	}

}
