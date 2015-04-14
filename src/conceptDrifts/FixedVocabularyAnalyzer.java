package conceptDrifts;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
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
		Tokenizer source = new LowerCaseTokenizer(reader);
		return new TokenStreamComponents(source, new KeywordFilter(keywordList, source));
	}

	public final class KeywordFilter extends TokenFilter {
		//private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);
		private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
		public KeywordFilter(HashSet<String> keywordList, TokenStream in) {
			super(in);
		}

		@Override
		public final boolean incrementToken() throws IOException {
			if (!input.incrementToken()) {
				return false;
			}
		    //int startOffset = offsetAttribute.startOffset();
		    //int endOffset = offsetAttribute.endOffset();
			if (!keywordList.contains(termAttribute.toString())) {
				return false;
			}
			return true;
		}
		
	}	
}
