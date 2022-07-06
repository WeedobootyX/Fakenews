package se.bubbelbubbel.fakenews;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import se.bubbelbubbel.fakenews.model.WordFilter;

public class WordFilterTest {

	@Test
	public void test() {
		WordFilter filter = new WordFilter();
		assertTrue(filter.isFiltered("det"));
		assertTrue(filter.isFiltered("DET"));
		assertFalse(filter.isFiltered("tjottahejt"));
	}
}
