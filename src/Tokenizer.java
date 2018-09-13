import java.io.File; 
import java.io.FileNotFoundException;
import java.util.ArrayList;
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


public class Tokenizer {
	int DID = 0;
	int tidCounter = 0;
	public ArrayList<Token> list;
	String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String numbers = "0123456789";
	
	public Tokenizer() {
		list = new ArrayList<Token>();
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
			} else if (s.substring(i, i +1).equals(" ")){
				list.add(new Token(list.size(), t, DID));
				t = "";
				number = false;
			} else {
				list.add(new Token(list.size(), t, DID));
				list.add(new Token(list.size(), s.substring(i, i + 1), DID));
				t = "";
				number = false;
			}
	}
	
	public static void main (String args[]) throws FileNotFoundException{
		Tokenizer t = new Tokenizer();
		t.LoadFile("Data_1.txt");
		for (int i = 0; i < t.list.size(); i ++) 
			System.out.println(i + " " + t.list.get(i).token + " " + t.list.get(i).DID);
	}
}
