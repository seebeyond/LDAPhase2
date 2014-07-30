package phase2;
import java.io.*;
import java.util.*;

// this program proves below fact---------------------------------------------------

// in WordMap.txt file, after every word, there is an int number (from 0 to XXXXX)
// in .phi file, a line is the probability distribution of all words in dict. The sequence in .phi file is according with 
// the int number sequence mentioned above.
//---------------------------------------------------------------------------------
public class TestPhiDictMatching {
	
	public static String PATH = "C:\\Users\\zouc\\Desktop\\lda\\output\\model ---- alpha-0.5 beta-0.1 ntopics-11 niters-02000 twords-20.phi";
	public static String DICT = "C:\\Users\\zouc\\Desktop\\lda\\output\\wordmap.txt";
	
	

	public static void main(String[] args) throws IOException {
		
		File file1 = new File(PATH);
		File file2 = new File(DICT);
		
		BufferedReader br1 = new BufferedReader(new FileReader(file1));
		String str = "";
		String[] arr = null;
		while ((str=br1.readLine())!=null) {
			arr = str.split(" ");
			break;
		}
		
		BufferedReader br2 = new BufferedReader(new FileReader(file2));
		br2.readLine();
		List<WordMap> map = new ArrayList<WordMap>();
		while ((str=br2.readLine())!=null) {
			String[] strs = str.split(" ");
			map.add(new WordMap(strs[0], Integer.valueOf(strs[1])));
		}
		Collections.sort(map, new Comparator<WordMap>(){
			@Override
			public int compare(WordMap wm1, WordMap wm2) {
				return wm1.seq-wm2.seq;
			}			
		});
	
		List<String> dict = new ArrayList<String>();
		
		for (WordMap wm : map) {
			dict.add(wm.word);
		}
		
		br1.close();
		br2.close();
		
		
		List<Pair> al = new ArrayList<Pair>();
		for (int i=0;i<arr.length;i++) {
			String a_word = dict.get(i);
			double d = Double.valueOf(arr[i]);
			al.add(new Pair(a_word, d));
		}
		
		Collections.sort(al, new Comparator<Pair>(){
			@Override
			public int compare(Pair p1, Pair p2) {
				if (p2.p > p1.p)
					return 1;
				if (p2.p == p1.p)
					return 0;
				return -1;
			}
		});
		
		for (int i=0;i<20;i++) {
			System.out.println(al.get(i).word);
		}
	}
}

class Pair{
	public String word;
	public double p;
	public Pair(String w, double d) {
		this.word = w;
		this.p = d;
	}
}

class WordMap {
	public String word;
	public int seq;
	public WordMap(String w, int s) {
		word = w;
		seq = s;
	}
}

