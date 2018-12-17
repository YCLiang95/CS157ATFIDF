import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class Writer {
    private DataOutputStream out;
    private static String newLine = System.getProperty("line.separator");

    public Writer(String fname) throws FileNotFoundException {
        out = new DataOutputStream(new FileOutputStream(fname + ".txt"));
    }

    public void writeToken(String token, int TID, int DID) throws IOException {
        String s = "INSERT INTO TOKEN VALUES ('" + token + "', '" + TID + "', '" + DID + "');";
        out.writeBytes(s);
        out.writeBytes(newLine);
    }

    public void writeDoc(int wordCount, int DID) throws IOException {
        String s = "INSERT INTO DOCUMENT VALUES ('" + wordCount + "', '" + DID + "');";
        out.writeBytes(s);
        out.writeBytes(newLine);
    }

    public void writeTFIDF(String token, double TFIDF, int DID) throws IOException {
        String s = "INSERT INTO TOKEN VALUES ('" + token + "', '" + TFIDF + "', '" + DID + "');";
        out.writeBytes(s);
        out.writeBytes(newLine);
    }

}
