package se.bubbelbubbel.fakenews.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WordFilter {
	private String filteredWordsStr = "to,rt,har,ett,för,att,de,som,på,du,rätt,det,är,inte,med,där,bara,och,lite,om,jag,man,sig,i,går,utan,vill,här,eller,upp,men,mot,måste,samma,någon,ska,dem,få,den,sedan,alltså,ta,över,in,till,hela,ha,kan,vara,så,sitt,kommer,en,tid,då,folk,allt,vi,just,igenom,var,av,blir,alla,vem,håller,efter,vad,&,när,nu,tack,hade,inget,göra,gör,ingen,ju,dig,mer,ge,under,-,får,än,gång,annat,väl,tar,första,andra,finns,detta,skulle,se,fram,igen,även,idag,år,från,två,nya,bli,borde,denna,säger,hur,the,ni,gjort,er,ändå,inom,han,många,nej,verkar,ja,trots,din,något,ut,ser,gå,sin,varit,hon,tror,oss,min,vid,både,också,varför,vet,helt,ner,bra,mig,mycket,jodå,tycker,bättre,skriver,ny,ggr,varje,fler,gånger";
	private List<String> filteredWords;
	
	public WordFilter() {
		String[] wordsArr = filteredWordsStr.split(",");
		filteredWords = Arrays.asList(wordsArr);
	}
	
	public boolean isFiltered(String word) {
		if(filteredWords.contains(word.toLowerCase())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String file ="c:/slask/words.txt";
		try {
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(file));
			String currentLine;
			StringBuffer allWords = new StringBuffer();
			String comma = "";
			while((currentLine = reader.readLine()) != null) {
				allWords.append(comma + currentLine.toLowerCase());
				comma = ",";
			}
			System.out.println(allWords.toString());
			reader.close();		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

