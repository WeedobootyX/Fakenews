package se.bubbelbubbel.fakenews.model;

public class TrendingWord {
	private String word = "";
	private int wordCount = 0;
	
	public TrendingWord(String word, int wordCount) {
		this.word = word;
		this.wordCount = wordCount;
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getWordCount() {
		return wordCount;
	}
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}
}
