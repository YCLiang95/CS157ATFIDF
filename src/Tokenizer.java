import java.io.File; 
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner; 

class Token{
	int TID;
	String token;
	int DID;
	
	Token(int TID, String token, int DID){
		this.TID = TID;
		this.token = token;
		this.DID = DID;
	}
}

class TFIDF{
	int DID;
	String token;
	double tfidf;
	
	TFIDF(int DID, String token, double tfidf){
		this.DID = DID;
		this.token = token;
		this.tfidf = tfidf;
	}
}

class TokenComparator implements  Comparator<Token>{
    public int compare(Token a, Token b) { 
        return a.token.compareTo(b.token); 
    } 
}

class TFIDFComparator implements  Comparator<TFIDF>{
    public int compare(TFIDF a, TFIDF b) {
    	if (a.tfidf > b.tfidf) return 1; else if (a.tfidf == b.tfidf) return 0; else return -1;
    }
}



public class Tokenizer {
	int DID = 0;
	int tidCounter = 0;
	public ArrayList<Token> list;
	String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String numbers = "0123456789";
	public ArrayList<Token> tokenTable;
	
	public Tokenizer() {
		list = new ArrayList<Token>();
		tokenTable = new ArrayList<Token>();
	}
	
	void LoadFile(String filename) throws FileNotFoundException{
		Scanner scanner = new Scanner (new File(filename));
		while (scanner.hasNext())
			splite(scanner.next());
		scanner.close();
	}
	
	void splite(String s) {
		String t = "";
		boolean number = false;;
		
		for (int i = 0; i < s.length(); i ++)
			if (alphabet.contains(s.substring(i, i + 1)))
				if (number) {
					list.add(new Token(list.size(), t, DID));
					t = s.substring(i, i + 1);
					number = false;
				} else {
					t = t + s.charAt(i);
				}
			else if (numbers.contains(s.substring(i, i + 1))) {
				if (number || t.length() == 0) {
					t = t +s.charAt(i);
					number = true;
				} else {
					list.add(new Token(list.size(), t, DID));
					t = s.substring(i, i + 1);
					number = true;
				}
			} else if (s.substring(i, i +1).equals(" ")  && t.equals("")) {
				//do nothing
			} else if (s.substring(i, i +1).equals(" ") ){
				list.add(new Token(list.size(), t, DID));
				t = "";
				number = false;
			} else {
				if (!t.equals("")) list.add(new Token(list.size(), t, DID));
				list.add(new Token(list.size(), s.substring(i, i + 1), DID));
				t = "";
				number = false;
			}
	}
	
	void TokenTable() {
		for (Token s : list) {
			if (alphabet.contains(s.token.substring(0, 1)))
				tokenTable.add(s);
		}
	}
	
	public static void main (String args[]) throws FileNotFoundException{
		Tokenizer t = new Tokenizer();
		for (int i = 1; i < 11; i ++) {
			t.DID = i;
			t.LoadFile("Data_" + i + ".txt");
		}
		
		//Debug output
		//for (int i = 0; i < t.list.size(); i ++) 
		//	System.out.println(i + " " + t.list.get(i).token + " " + t.list.get(i).DID);
		
		t.TokenTable();
		t.tokenTable.sort(new TokenComparator());
		
		//Debug output
		for (Token s : t.tokenTable)
			System.out.println(s.token + " " + s.DID);
		
		ArrayList<TFIDF> list2 = new ArrayList<TFIDF>();
		String last = "";
		int counter = 0;
		int did[] = new int[11];
		
		for (Token s : t.tokenTable)
			if (!(last.equals("") || s.token.equals(last))) {
				for (int i = 1; i < 11; i ++) {
					if (did[i] > 0) list2.add(new TFIDF(i, last, did[i] * Math.log(10.0f / (double) counter) / Math.log(2)));
					did[i] = 0;
				}
				last = s.token;

				did[s.DID] = 1;
				counter = 1;
			} else {
				if (last.equals("")) {
					last = s.token;
					did[s.DID] = 1;
				}
				
				if (did[s.DID] != 0) 
					counter ++;
				
				did[s.DID] += 1;
				
			}
		for (int i = 1; i < 11; i ++)
			list2.add(new TFIDF(i, last, did[i] * Math.log(10.0f / (double) counter) / Math.log(2)));
		list2.sort(new TFIDFComparator());
		
		for (TFIDF a : list2)
			System.out.println(a.DID + " " + a.token + " " + a.tfidf);
	}
}
