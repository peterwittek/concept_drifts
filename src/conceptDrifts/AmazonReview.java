package conceptDrifts;

import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class AmazonReview {

	private int cutoff;
	
	public AmazonReview(int cutoff) {
		this.cutoff = cutoff;
	}
	
	public Document Document(Scanner scanner) {
		String id = "";
		int timestamp = -1;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.contains("product/productId: ")) {
				id = line.substring(19);
			}
			if (line.contains("review/time: ")) {
				id += line.substring(13);
				timestamp = Integer.parseInt(line.substring(13)); 
			}
			if (line.contains("review/text: ")) {
				if (timestamp>-1 && timestamp<cutoff) {
					Document doc = new Document();
					doc.add(new Field("path", id, Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("contents", line.substring(13),
							Field.Store.NO, Field.Index.ANALYZED,
							Field.TermVector.YES));
					return doc;
				} else {
					id = "";
					timestamp = -1;
				}
			}
		}
		return null;
	}

}
