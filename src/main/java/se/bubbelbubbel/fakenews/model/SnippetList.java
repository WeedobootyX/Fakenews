package se.bubbelbubbel.fakenews.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bubbelbubbel.fakenews.service.NewsService;

public class SnippetList {
	Logger logger = LoggerFactory.getLogger(NewsService.class);

	private List<Snippet> snippets  = null;

	public List<Snippet> getSnippets() {
		if(this.snippets == null) {
			this.snippets = new ArrayList<Snippet>();
		}
		return snippets;
	}

	public void setSnippets(List<Snippet> snippets) {
		this.snippets = snippets;
	}
	
	public Snippet getRandomSnippet() {
		logger.debug("listan är: " + this.getSnippets().size() + " rader" );
		Random rn = new Random();
		int selectedIndex = rn.nextInt(this.getSnippets().size());
		return (Snippet)snippets.get(selectedIndex);
	}

}
