//Author Yecheng Liang 010010481
//Sept.20th, 2018
//CS157A database, TFIDF = TF * log2 (TD / DF)

import java.io.File; 
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner; 

//A class to store token info
class Token{
	//Token ID
	int TID;
	//Token
	String token;
	//Document ID
	int DID;
	
	Token(int TID, String token, int DID){
		this.TID = TID;
		this.token = token;
		this.DID = DID;
	}
}

//A class to store calculated TFIDF
class TFIDF{
	//Document ID
	int DID;
	//Token
	String token;
	//TFIDF
	double tfidf;
	
	TFIDF(int DID, String token, double tfidf){
		this.DID = DID;
		this.token = token;
		this.tfidf = tfidf;
	}
}

class Document{
	int wordCount;
	int DID;
	
	Document (int DID, int wordCount){
		this.DID = DID;
		this.wordCount = wordCount;
	}
}

//For list sorting
class TokenComparator implements  Comparator<Token>{
    public int compare(Token a, Token b) { 
        return a.token.compareTo(b.token); 
    } 
}

//For list sorting
class TFIDFComparator implements  Comparator<TFIDF>{
    public int compare(TFIDF a, TFIDF b) {
    	if (a.tfidf > b.tfidf) return 1; else if (a.tfidf == b.tfidf) return 0; else return -1;
    }
}

public class Tokenizer {
	//Document ID
	int DID = 0;
	public ArrayList<Token> list;
	String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String numbers = "0123456789";
	public ArrayList<Token> tokenTable;
	public ArrayList<Document> documentTable;
	
	public Tokenizer() {
		list = new ArrayList<Token>();
		tokenTable = new ArrayList<Token>();
		documentTable = new ArrayList<Document>();
	}
	
	//load a single file with scanner
	void LoadFile(String filename) throws FileNotFoundException{
		int LastTID = list.size();
		Scanner scanner = new Scanner (new File(filename));
		while (scanner.hasNext())
			splite(scanner.next());
		scanner.close();
		documentTable.add(new Document(DID, list.size() - LastTID));
	}
	
	//Split the string into token
	void splite(String s) {
		String t = "";
		//boolean number = false;
		s = s.toLowerCase();
		
		//token ID is equal to current size of the token list
		//Remove all the number logic in this iteration
		//I used to keeps numbers as a whole as a word
		//But professor only requires us to treat single digit as a word
		for (int i = 0; i < s.length(); i ++) {
			//if (DID == 2) 
			//	System.out.println(s.substring(i, i + 1));
			if (alphabet.contains(s.substring(i, i + 1))) {
				//if (number) {
					//list.add(new Token(list.size(), t, DID));
					//t = s.substring(i, i + 1);
					//number = false;
				//} else {
					t = t + s.charAt(i);
				//}
			//We don't allows words that start with number end of alphabet like 123abc
			//Those will be treated as 2 token, "123" and "abc"
			//} else if (numbers.contains(s.substring(i, i + 1))) {
				//if (number || t.length() == 0) {
					//t = t  + s.charAt(i);
					//number = true;
				//} else {
				//	list.add(new Token(list.size(), t, DID));
				//	t = s.substring(i, i + 1);
				//	number = true;
				//}
			//I actually don't need to consider " " because scanner already ignore all the space
			//But I'll leave it there
			} else if (s.substring(i, i + 1).equals(" ") ){
				if (!t.equals("")) list.add(new Token(list.size(), t, DID));
				t = "";
				//number = false;
			} else {
				if (!t.equals("")) list.add(new Token(list.size(), t, DID));
				list.add(new Token(list.size(), s.substring(i, i + 1), DID));
				t = "";
				//number = false;
			}
		}
		if (!t.equals("")) list.add(new Token(list.size(), t, DID));
	}
	
	//The intial table that contains all the token and their ID
	void TokenTable() {
		for (Token s : list) {
			if (alphabet.contains(s.token.substring(0, 1)))
				tokenTable.add(s);
		}
	}
	
	//An seperate table for documents that contains their word counts
	void documentTable() {
		
	}
	
	public static void main (String args[]) throws FileNotFoundException{
		int DocumentCount = 2;
		Tokenizer t = new Tokenizer();
		//for (int i = 1; i < 11; i ++) {
			//t.DID = i;
			//t.LoadFile("Data_" + i + ".txt");
		//}
		
		//Some smaller testing samples
		//t.DID = 1;
		//t.LoadFile("t1.txt");
		//t.DID = 2;
		//t.LoadFile("t2.txt");
		
		//Debug output
		//for (int i = 0; i < t.list.size(); i ++) 
			//System.out.println(i + " " + t.list.get(i).token + " " + t.list.get(i).DID);
		
		t.TokenTable();
		t.tokenTable.sort(new TokenComparator());
		
		//Debug output
		//for (Token s : t.tokenTable)
			//System.out.println(s.token + " " + s.DID);
		
		ArrayList<TFIDF> list2 = new ArrayList<TFIDF>();
		String last = "";
		//Document frequency counter
		int counter = 0;
		//Token Frequency counter
		int did[] = new int[DocumentCount + 1];
		
		//calculate the TFIDF
		for (Token s : t.tokenTable)
			if (!(last.equals("") || s.token.equals(last))) {
				for (int i = 1; i < DocumentCount + 1; i ++) {
					if (did[i] > 0) list2.add(new TFIDF(i, last, ((double)did[i] / (double)t.documentTable.get(i - 1).wordCount) * Math.log((double) DocumentCount / (double) counter) / Math.log(2)));
					did[i] = 0;
				}
				last = s.token;

				did[s.DID] = 1;
				counter = 1;
			} else {
				if (last.equals("")) {
					last = s.token;
					did[s.DID] = 1;
					counter++;
				}
				
				if (did[s.DID] == 0) 
					counter ++;
				
				did[s.DID] += 1;
				
			}
		for (int i = 1; i < DocumentCount + 1; i ++)
			list2.add(new TFIDF(i, last, (did[i] / t.documentTable.get(i - 1).wordCount) * Math.log((double)DocumentCount / (double) counter) / Math.log(2)));
		list2.sort(new TFIDFComparator());
		
		//print out document ID / Token / TFIDF
		for (TFIDF a : list2)
			System.out.println(a.DID + " " + a.token + " " + a.tfidf);
	}
}
