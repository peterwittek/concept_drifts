package conceptDrifts;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

class WordnetAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer source = new LowerCaseTokenizer(reader);
		return new TokenStreamComponents(source, new LengthFilter(true, new WordnetLemmaFilter(source), 3, 1000));
	}

	public final class WordnetLemmaFilter extends TokenFilter {
		private final MyWordnetStemmer stemmer = new MyWordnetStemmer();
		private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
		private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

		public WordnetLemmaFilter(TokenStream in) {
			super(in);
		}

		@Override
		public final boolean incrementToken() throws IOException {
			if (!input.incrementToken()) {
				return false;
			}
			if ((!keywordAttr.isKeyword())
					&& stemmer.stem(termAtt.buffer(), 0, termAtt.length())) {
				termAtt.copyBuffer(stemmer.getResultBuffer(), 0, stemmer.getResultLength());
			}
			return true;
		}
		
	}

	public final class LengthFilter extends FilteringTokenFilter {

		  private final int min;
		  private final int max;
		  
		  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

		  /**
		   * Build a filter that removes words that are too long or too
		   * short from the text.
		   */
		  public LengthFilter(boolean enablePositionIncrements, TokenStream in, int min, int max) {
		    super(in);
		    this.min = min;
		    this.max = max;
		  }
		  
		  @Override
		  public boolean accept() throws IOException {
		    final int len = termAtt.length();
		    return (len >= min && len <= max);
		  }
		}
	
}
