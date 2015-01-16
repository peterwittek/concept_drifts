package conceptDrifts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class SvDense2Sparse {

	private static List<String> indexWords = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		transform(args[0], args[1], args[2]);
	}

	static String parseToSVMVector(String s) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(s, "|");
		int j = 0;
		indexWords.add(st.nextToken());
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
		return result;
	}

	public static void transform(String src, String dest, String keywordFile)
			throws IOException {
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
