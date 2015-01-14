package conceptDrifts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Generates a Lucene Document for indexing from a given filename
 */
public class AmazonReview {
	static char dirSep = System.getProperty("file.separator").charAt(0);

	/**
	 * Document.
	 * 
	 * @param f
	 *            the file to be processed
	 * @param lang
	 *            the language being indexed;
	 * @return the document; if f is not in the given language, it returns null
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException 
	 */
	public static Document Document(File file) throws IOException, ParseException {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			// at least on windows, some temporary files raise this exception
			// with an "access denied" message
			// checking if the file can be read doesn't help
			return null;
		}
		BufferedReader bis=new BufferedReader(new InputStreamReader(
				fis, "UTF-8"));


		Document doc = new Document();
        String filename=file.getName();
		doc.add(new Field("path", filename,
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("contents", bis, Field.TermVector.YES));
		return doc;
	}

	private AmazonReview() {
	}
}
