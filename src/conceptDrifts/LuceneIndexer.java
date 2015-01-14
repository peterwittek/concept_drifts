package conceptDrifts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * Creates a Lucene index from a directory in a given language.
 */
public class LuceneIndexer {

	private static Logger logger = Logger.getLogger("conceptDrifts");
	private static final Version LUCENE_VERSION = Version.LUCENE_4_10_3;

	/**
	 * The main method gives an example of invocation.
	 * 
	 * @param args
	 *            the arguments; not used
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		logger.setLevel(Level.OFF);
		String textCollectionDirectory = "data/sample"; //args[0];
		String luceneIndexDirectory = "data/sample_index"; //args[1];
		startIndexer(textCollectionDirectory, luceneIndexDirectory);
		getIndexTerms(luceneIndexDirectory);
	}

	/**
	 * Starts the indexer with a specific parameter setting
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 */
	public static void startIndexer(String textCollectionDirectory,
			String luceneIndexDirectory) throws InterruptedException,
			IOException, ParseException {
		//Set<String> stopwords = new HashSet<String>();

		Analyzer analyzer = new WordnetAnalyzer();
		//new WordnetAnalyzer(LUCENE_VERSION, stopwords, true);
		LuceneIndexer.indexDir(textCollectionDirectory, luceneIndexDirectory,
				analyzer);
	}

	/**
	 * Indexes a single directory of documents with a given parameter setting.
	 * 
	 * @param docDir
	 *            the directory containing the documents. Sub-directories will
	 *            also be indexed
	 * @param indexDir
	 *            the directory of the index files
	 * @param analyzer
	 *            the language-specific analyzer
	 * @throws ParseException
	 */
	public static void indexDir(String docDir, String indexDir,
			Analyzer analyzer) throws ParseException {
		try {
			IndexWriterConfig indexConfig = new IndexWriterConfig(
					LUCENE_VERSION, analyzer);
			IndexWriter writer = new IndexWriter(FSDirectory.open(new File(
					indexDir)), indexConfig);
			logger.info("Indexing to directory '" + indexDir + "'...");
			indexDocs(writer, new File(docDir));
			writer.commit();
			logger.info("Number of documents: " + writer.numDocs());
			writer.close();
		} catch (IOException e) {
			logger.severe("caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
		}

	}

	private static void indexDocs(IndexWriter writer, File file)
			throws IOException, ParseException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				Arrays.sort(files);
				// an IO error could occur
				if (files != null) {
					for (String file2 : files) {
						indexDocs(writer, new File(file, file2));
					}
				}
			} else {
				try {
					Document doc = AmazonReview.Document(file);
					if (doc != null) {
						logger.fine("adding " + file);
						writer.addDocument(doc);
					}
				} catch (FileNotFoundException fnfe) {
					;
				}
			}
		}
	}

	/**
	 * Gets the index terms and writes them in the index directory.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public static void getIndexTerms(String luceneIndexDirectory)
			throws Exception {
		File file = new File(luceneIndexDirectory);
		IndexReader indexReader = IndexReader.open(FSDirectory.open(file));
		Fields fields = MultiFields.getFields(indexReader);
        Terms terms = fields.terms("contents");
        TermsEnum termsEnum = terms.iterator(null);
        BytesRef text;
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(
				luceneIndexDirectory + "/indexTerms.txt")));
        while((text = termsEnum.next()) != null) {
        	out.write(text.utf8ToString()+"\n");
        }
		out.close();
		indexReader.close();
	}

	
}
