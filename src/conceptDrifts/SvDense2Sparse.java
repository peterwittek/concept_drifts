package conceptDrifts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class SvDense2Sparse {

	private static List<String> indexWords = new ArrayList<String>();
	private static List<String> keywordList = new ArrayList<String>();
		
	public static void main(String[] args) throws IOException {
		String keywords = null; 
		if (args.length==4) {
			keywords = args[3];
		}
		transform(args[0], args[1], args[2], keywords);
	}

	static String parseToSVMVector(String s) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(s, "|");
		int j = 0;
		String indexWord = st.nextToken(); 
		if (keywordList.size() == 0 || keywordList.contains(indexWord)) {
			indexWords.add(indexWord);
			while (st.hasMoreTokens()) {
				double tmp = Double.valueOf(st.nextToken()).doubleValue();
				if (tmp != 0) {
					result += j + ":" + tmp + " ";
				}
				j++;
			}
			if (result.length() == 0) {
				result = " ";
			}
			result += "\n";
		}
		return result;
	}
	
	private static void readKeywords(String keywords) throws FileNotFoundException {
		Scanner scn = new Scanner(new BufferedReader(new FileReader(keywords)))
		.useDelimiter("\n");
		scn.next();
		while (scn.hasNext()) {
			StringTokenizer st = new StringTokenizer(scn.next(), "\t");
			st.nextToken();
			String word = st.nextToken();
			keywordList.add(word);
		}
		scn.close();
	}

	public static void transform(String src, String dest, String keywordFile, String keywords)
			throws IOException {
		if (keywords!=null) {
			readKeywords(keywords);
		}
		Scanner scn = new Scanner(new BufferedReader(new FileReader(src)))
				.useDelimiter("\n");
		FileWriter out = new FileWriter(new File(dest));
		scn.nextLine();
		while (scn.hasNext()) {
			out.write(parseToSVMVector(scn.next()));
		}
		scn.close();
		out.close();
		out = new FileWriter(new File(keywordFile));
		out.write("% " + indexWords.size() + "\n");
		for (int i=0; i<indexWords.size(); ++i) {
			out.write(i + "\t" + indexWords.get(i) + "\t" + indexWords.get(i) + "\n");
		}
		out.close();
	}
}
