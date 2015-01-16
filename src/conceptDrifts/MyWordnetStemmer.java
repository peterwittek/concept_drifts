package conceptDrifts;

import static org.apache.lucene.util.RamUsageEstimator.NUM_BYTES_CHAR;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.util.ArrayUtil;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.morph.WordnetStemmer;

public class MyWordnetStemmer {

	private IRAMDictionary dict;
	private WordnetStemmer wordnetStemmer;
	private char[] b;
	private int i; /* offset into b */
	private static final int INITIAL_SIZE = 50;
	  
	  
	public MyWordnetStemmer() {
		String path = "/usr/share/wordnet/dict";
		dict = new RAMDictionary(new File(path), ILoadPolicy.IMMEDIATE_LOAD);
		try {
			dict.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordnetStemmer = new WordnetStemmer(dict);
	    b = new char[INITIAL_SIZE];
	    i = 0;
	}

	/**
	 * Stem a word contained in a portion of a char[] array. Returns true if the
	 * stemming process resulted in a word different from the input. You can
	 * retrieve the result with getResultLength()/getResultBuffer() or
	 * toString().
	 */
	public boolean stem(char[] wordBuffer, int offset, int wordLen) {
		if (b.length < wordLen) {
			b = new char[ArrayUtil.oversize(wordLen, NUM_BYTES_CHAR)];
		}
		List<String> stems = wordnetStemmer.findStems(new String(Arrays.copyOfRange(wordBuffer, offset,
											   wordLen)), null);
		if (stems.size()>0) {
			b = stems.get(0).toCharArray();
			i = stems.get(0).length();

		} else {
			i = 0;
		}
		return true;
	}
	
	public int getResultLength() { return i; }
	
	public char[] getResultBuffer() { return b; }

}
