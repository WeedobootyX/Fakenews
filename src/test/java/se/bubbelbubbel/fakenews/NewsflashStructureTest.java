package se.bubbelbubbel.fakenews;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import se.bubbelbubbel.fakenews.model.Structure;

public class NewsflashStructureTest {

	@Test
	public void test() {
		Structure newsflashStructure = new Structure();
		
		newsflashStructure.setStructure("{A1}.{a2}, {hopplahej} {bokstäver} {slutkläm}");
		List<String> snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals("test 1 failed", 5, snippetKeys.size());
		assertTrue("test 1a failed", snippetKeys.get(0).equals("A1"));
		assertTrue("test 1b failed", snippetKeys.get(1).equals("a2"));
		assertTrue("test 1a failed", snippetKeys.get(2).equals("hopplahej"));
		assertTrue("test 1a failed", snippetKeys.get(3).equals("bokstäver"));
		assertTrue("test 1a failed", snippetKeys.get(4).equals("slutkläm"));
		newsflashStructure.setStructure("");
		snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals("test 2 failed", 0, snippetKeys.size());
		newsflashStructure.setStructure("{snippet}        ");
		snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals("test 3 failed", 1, snippetKeys.size());
		newsflashStructure.setStructure("       {snippet}");
		snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals("test 4 failed", 1, snippetKeys.size());
	}

}

