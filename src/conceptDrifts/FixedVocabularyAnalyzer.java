package conceptDrifts;

import java.util.HashSet;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

class FixedVocabularyAnalyzer extends Analyzer {

	private HashSet<String> keywordList;

	public FixedVocabularyAnalyzer(HashSet<String> keywordList) {
		    super();
		    this.keywordList = keywordList;
	 }	
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer source = new LetterDigitDashTokenizer(reader);
		return new TokenStreamComponents(source, new KeywordFilter(keywordList, new LowerCaseFilter(source)));
	}

	public final class KeywordFilter extends TokenFilter {
		PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
		private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
		private HashSet<String> keywordList;
		
		public KeywordFilter(HashSet<String> keywordList, TokenStream in) {
			super(in);
			this.keywordList = keywordList;
		}

	    @Override
        public boolean incrementToken() throws IOException {
           int extraIncrement = 0;
           while (true) {
             boolean hasNext = input.incrementToken();
             if (hasNext) {
               if (!keywordList.contains(termAtt.toString())) {
                 extraIncrement += posIncrAtt.getPositionIncrement(); // filter this word
                 continue;
               } 
               if (extraIncrement > 0) {
                 posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement()+extraIncrement);
               }
             }
             return hasNext;
           }
         }
		
	}

	public class LetterDigitDashTokenizer extends CharTokenizer {
		  
		  /**
		   * Construct a new LetterTokenizer.
		   * @param in
		   *          the input to split up into tokens
		   */
		  public LetterDigitDashTokenizer(Reader in) {
		    super(in);
		  }
		  
		  /** Collects only characters which satisfy
		   * {@link Character#isLetter(int)}.*/
		  @Override
		  protected boolean isTokenChar(int c) {
		    return (Character.isLetterOrDigit(c)|| c == 45);
		  }
		}

}
