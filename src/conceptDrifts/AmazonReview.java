package conceptDrifts;

import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class AmazonReview {

	public static Document Document(Scanner scanner) {
		String id = "";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.contains("product/productId: ")) {
				id = line.substring(19);
			}
			if (line.contains("review/time: ")) {
				id += line.substring(13);
			}
			if (line.contains("review/text: ")) {
				Document doc = new Document();
				doc.add(new Field("path", id, Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("contents", line.substring(13),
						Field.Store.NO, Field.Index.ANALYZED,
						Field.TermVector.YES));
				return doc;
			}
		}
		return null;
	}

}
