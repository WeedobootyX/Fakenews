package se.bubbelbubbel.fakenews;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.bubbelbubbel.fakenews.model.Structure;

public class NewsflashStructureTest {

	@Test
	public void test() {
		Structure newsflashStructure = new Structure();
		
		newsflashStructure.setStructure("{A1}.{a2}, {hopplahej} {bokstäver} {slutkläm}");
		List<String> snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals(5, snippetKeys.size());
		assertTrue(snippetKeys.get(0).equals("A1"));
		assertTrue(snippetKeys.get(1).equals("a2"));
		assertTrue(snippetKeys.get(2).equals("hopplahej"));
		assertTrue(snippetKeys.get(3).equals("bokstäver"));
		assertTrue(snippetKeys.get(4).equals("slutkläm"));
		newsflashStructure.setStructure("");
		snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals(0, snippetKeys.size());
		newsflashStructure.setStructure("{snippet}        ");
		snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals(1, snippetKeys.size());
		newsflashStructure.setStructure("       {snippet}");
		snippetKeys = newsflashStructure.getSnippetKeys();
		assertEquals(1, snippetKeys.size());
	}

}

