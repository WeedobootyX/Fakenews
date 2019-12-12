package se.bubbelbubbel.fakenews.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import se.bubbelbubbel.fakenews.model.Structure;

public class StructureRowMapper implements RowMapper <Structure>{

	public static final String STRUCTURE_COLUMN_LIST =
			" structure_key, structure, weight, status ";

	public Structure mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
		Structure structure =  new Structure();
		structure.setStructureKey(rs.getString(1));
		structure.setStructure(rs.getString(2));
		structure.setWeight(rs.getInt(3));
		structure.setStatus(rs.getString(4));
		return structure;
	}
}
