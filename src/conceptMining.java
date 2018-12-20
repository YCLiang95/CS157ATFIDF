import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class BooleanMap{
	HashMap<String, Boolean> Dfrequency;
	
	public BooleanMap() {
		Dfrequency = new HashMap<String, Boolean>();
	}
	
	public boolean get(String s) {
		if (Dfrequency.get(s) != null)
			return true;
		else
			return false;
	}
	
	public void put(String s) {
		Dfrequency.put(s, true);
	}
	
}

class entryComparator implements  Comparator<Map.Entry<String, Double>>{
    public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) { 
        return a.getValue().compareTo(b.getValue()); 
    } 
}

public class conceptMining{	
	//
	public static int threashold = 10;
	ArrayList<Token> tokenTable;
	
	public conceptMining(ArrayList<Token> t) {
		tokenTable = new ArrayList<Token>();
		for (Token i : t)
			tokenTable.add(new Token(i.TID, i.token, i.DID));
	}
	
	public void twoConcept(BooleanMap[] keywords, int dc) {
		 HashMap<String, Double> frequency = new HashMap<String, Double>();
		 BooleanMap[] df = new BooleanMap[dc];
		 for (int i = 0 ; i < df.length; i ++)
			 df[i] = new BooleanMap();
		 
		 for (int i = 0; i < tokenTable.size() - 1; i ++) {
			 if (i % 1000 == 0) System.out.println(i + "/" + tokenTable.size());
			 if (keywords[tokenTable.get(i).DID].get(tokenTable.get(i).token))
				 for (int j = i + 1; j < Math.min(tokenTable.size(), i + 7); j ++) {
					 if (tokenTable.get(i).DID != tokenTable.get(j).DID || tokenTable.get(i).token.equals(tokenTable.get(j).token))
							 break;
				 	 if (keywords[tokenTable.get(j).DID].get(tokenTable.get(j).token)){
						 String t = tokenTable.get(i).token + " " + tokenTable.get(j).token;
						 df[tokenTable.get(i).DID].put(t);
						 if (frequency.get(t) != null)
							 frequency.put(t, frequency.get(t) + 1.0f / (double) (j - i));
						 else 
							 frequency.put(t, 1.0f / (double) (j - i));
				 }
			}
		 }
		 
		 ArrayList<Entry<String, Double>> list = new  ArrayList<Entry<String, Double>>();
		 Iterator<Entry<String, Double>> it = frequency.entrySet().iterator();
		 while (it.hasNext()) {
			 Map.Entry<String, Double> entry = it.next();
			 int count = 0;
			 for (int i = 0; i < df.length; i ++) 
				 if (df[i].get(entry.getKey())) count ++;
			 
			 if (count >= threashold)
				 list.add(entry);
				 //System.out.println(entry.getKey() + " " + entry.getValue());
			 it.remove();
		 }
		 
			try {
				PrintStream out = new PrintStream(new FileOutputStream("output\\output_twoConcept.txt"));
				System.setOut(out);
			} catch (Exception e){}
			
		 list.sort(new entryComparator());
		 for (Map.Entry<String, Double> e : list)
			 System.out.println(e.getKey() + " " + e.getValue());
	}
}
	