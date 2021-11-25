package se.bubbelbubbel.fakenews;

import static org.junit.Assert.*;

import org.junit.Test;

import se.bubbelbubbel.fakenews.model.WordFilter;

public class WordFilterTest {

	@Test
	public void test() {
		WordFilter filter = new WordFilter();
		assertTrue("test 1 failed", filter.isFiltered("det"));
		assertTrue("test 2 failed", filter.isFiltered("DET"));
		assertFalse("test 3 failed", filter.isFiltered("tjottahejt"));
	}
}
