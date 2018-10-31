//Author Yecheng Liang 010010481
//Sept.20th, 2018
//CS157A database, TFIDF = TF * log2 (TD / DF)

import java.io.File; 
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
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
	boolean[] alphabetArray;
	String numbers = "0123456789";
	public ArrayList<Token> tokenTable;
	public ArrayList<Document> documentTable;
	
	public Tokenizer() {
		list = new ArrayList<Token>();
		tokenTable = new ArrayList<Token>();
		documentTable = new ArrayList<Document>();
		alphabetArray = new boolean[65536];
		for (int i = 0; i < alphabet.length(); i++)
			alphabetArray[Character.getNumericValue(alphabet.charAt(i))] = true;
	}
	
	//load a single file with scanner
	void LoadFile(String filename) throws FileNotFoundException{
		int LastTID = list.size();
		Scanner scanner = new Scanner (new File(filename));
		while (scanner.hasNext())
			splite(scanner.next());
		scanner.close();
		documentTable.add(new Document(DID, list.size() - LastTID));
		DID ++;
	}
	
	boolean isLetter(char c) {
		try {
			return alphabetArray[Character.getNumericValue(c)];
		} catch (Exception e) {
			return false;
		}
	}
	
	void splite(String s) {
		String t = "";
		//boolean number = false;
		s = s.toLowerCase();
		
		//token ID is equal to current size of the token list
		//Remove all the number logic in this iteration
		for (int i = 0; i < s.length(); i ++) {
			if (isLetter(s.charAt(i))) {
				t = t + s.charAt(i);
				//I actually don't need to consider " " because scanner already ignore all the space
				//But I'll leave it there
			//} else if (s.substring(i, i + 1).equals(" ") ){
				//if (!t.equals("")) list.add(new Token(list.size(), t, DID));
				//t = "";
			} else {
				if (!t.equals("")) list.add(new Token(list.size(), t, DID));
				list.add(new Token(list.size(), s.substring(i, i + 1), DID));
				t = "";
			}
		}
		if (!t.equals("")) list.add(new Token(list.size(), t, DID));
	}
	
	//Split the string into token
	void spliteOld(String s) {
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
	
	ArrayList<TFIDF> computeTFIDF(int DocumentCount){
		ArrayList<TFIDF> list2 = new ArrayList<TFIDF>();
		String last = "";
		//Document frequency counter
		int counter = 0;
		//Token Frequency counter
		int did[] = new int[DocumentCount + 1];
		int currentToken = 0;
		
		//calculate the TFIDF
		for (Token s : tokenTable) {
			currentToken ++;
			if (currentToken % 1000 == 0) System.out.println("Calculating" + currentToken + "/" + tokenTable.size());
			if (!(last.equals("") || s.token.equals(last))) {
				for (int i = 1; i < DocumentCount + 1; i ++) {
					if (did[i] > 0 && documentTable.get(i - 1).wordCount > 0) list2.add(new TFIDF(i, last, ((double)did[i] / (double)documentTable.get(i - 1).wordCount) * Math.log((double) DocumentCount / ((double) counter)) / Math.log(2)));
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
		}
		for (int i = 1; i < DocumentCount + 1; i ++)
			if (did[i] > 0 && documentTable.get(i - 1).wordCount > 0)	
				list2.add(new TFIDF(i, last, ((double)did[i] / (double)documentTable.get(i - 1).wordCount) * Math.log((double)DocumentCount / (double) counter) / Math.log(2)));
		list2.sort(new TFIDFComparator());
		
		return list2;
	}
	
	static void degbugRead(Tokenizer t) throws FileNotFoundException {
		t.DID = 1;
		t.LoadFile("t1.txt");
		t.DID = 2;
		t.LoadFile("t2.txt");
		
		t.TokenTable();
		t.tokenTable.sort(new TokenComparator());
	}
	
	static int Load(String path, Tokenizer t) throws FileNotFoundException {
		int counter = 0;
		File directory = new File (path);
		for (File file : directory.listFiles()) {
			//t.DID = counter;
			t.LoadFile(file.getAbsolutePath());
			System.out.println(t.DID + " " + file.getAbsolutePath());
			counter++;
		}
		return counter;
	}
	
	public static void main (String args[]) throws FileNotFoundException{
		int DocumentCount = 0;
		Tokenizer t = new Tokenizer();
		//for (int i = 1; i < 11; i ++) {
			//t.DID = i;
			//t.LoadFile("Data_" + i + ".txt");
		//}
		
		//Some smaller testing samples
		//degbugRead(t);
		PrintStream out = new PrintStream(new FileOutputStream("DocumentID1.txt"));
		System.setOut(out);
		for (int i = 1; i < 2; i ++) {
			System.out.println("Loading..." +  i);
			//DocumentCount += Load("E:\\CS157A\\CS157ATFIDF\\data\\tylin-" + i + "\\", t);
			DocumentCount += Load("E:\\CS157A\\CS157ATFIDF\\data\\" + i, t);
			//System.out.println("Compute" + DocumentCount + " " + t.list.size());
		}
		System.setOut(System.out);
		t.TokenTable();
		t.tokenTable.sort(new TokenComparator());
		for (int i = 0; i < t.tokenTable.size(); i ++) {
			System.out.println(i + " " + t.tokenTable.get(i).token);
		}
		ArrayList<TFIDF> list2 = t.computeTFIDF(DocumentCount);

		double max = 0f;
		double last = list2.get(0).tfidf;
		String lastToken = "";
		int lastTokenDID = 0;
		
		out = new PrintStream(new FileOutputStream("output1.txt"));
		System.setOut(out);
		//print out document ID / Token / TFIDF
		for (TFIDF a : list2) {
			if (a.tfidf - last > max) {
				max = a.tfidf - last;
				last = a.tfidf;
				lastToken = a.token;
				lastTokenDID = a.DID;
			}
			//max = Math.max(max, (a.tfidf - last));
			last = a.tfidf;
			System.out.println(a.DID + " " + a.token + " " + a.tfidf);
		}
		System.out.println("Largest gap: " + max + "Token:" + lastTokenDID + lastToken);
	}
}
