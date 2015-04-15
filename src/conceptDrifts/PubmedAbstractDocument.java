package conceptDrifts;

import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class PubmedAbstractDocument {

	
	public PubmedAbstractDocument() {
	}
	
	public Document Document(Scanner scanner) {
		String id = "";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			int startIndex = line.indexOf("\"");
			if (startIndex < 0) {
				startIndex = line.indexOf("	");
			}
			id = line.substring(0, startIndex);
			Document doc = new Document();
			doc.add(new Field("path", id, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc.add(new Field("contents", line.substring(startIndex),
					Field.Store.NO, Field.Index.ANALYZED,
					Field.TermVector.YES));
			return doc;
		}
		return null;
	}

}
