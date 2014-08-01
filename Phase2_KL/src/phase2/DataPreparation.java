package phase2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class DataPreparation {

	private static String stopWords = Constants.PATH_STOPWORDS;
	private static String dataFiles = Constants.PATH_DATAFILES;
	
	private static EnglishAnalyzer ea = new EnglishAnalyzer(Version.LUCENE_40);
	private static StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_40);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println("********* LDA Input File Preparation started *********");
		
		Map<String, Integer> tmap = new HashMap<String, Integer>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		Map<String, Integer> mapIndex = new HashMap<String, Integer>();
		Map<String, Integer> stopwordmap = new HashMap<String, Integer>();
		
		
		
		File dir = new File(dataFiles);
		File[] files = dir.listFiles();
		if (files == null) {
			System.out.println("No files");
			return;
		} else {
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isHidden()) {
					String add = files[i].getAbsolutePath();
					readFileByChars(add, tmap);
				}
			}
			readFileByChars(stopWords, stopwordmap); // read stopword in
														// stopwordmap
			num(tmap, map, mapIndex, stopwordmap);
			
			System.out.println();
		}
			
	}

	// generate Map
	public static void readFileByChars(String fileName, Map map)
			throws IOException {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String str = null;
			String[] s = null;
			while ((str = reader.readLine()) != null) {
				// ----Lucene stemmer added here
				//System.out.println("Before Stemming: " + str);
				str = LuceneUtil.tokenizeStringReturnString(ea, str);
				//System.out.println("After Stemming: " + str);
				s = str.split("\\W");
				//System.out.print("After splitting: ");
				for (int u=0;u<s.length;u++) {
					//System.out.print(s[u] + " ");
				}
				//System.out.println();
				// ----lucene stemmer ends
				//s = str.split("\\W");
				for (int i = 0; i < s.length; i++) {
					String key = s[i].toLowerCase();
					if (s[i].length() > 1) {
						if (map.get(key) == null) {
							map.put(key, 1);
						} else {
							int value = ((Integer) map.get(key)).intValue();
							value++;
							map.put(key, value);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	// update tmap to map, remove stopwords, generate mapindex
	// /map(term,frequency) /mapIndex(term,Index)
	public static void num(Map tmap, Map map, Map mapIndex, Map stopwordmap) {
		int num = 0;
		Set set = tmap.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry mapentry = (Map.Entry) iterator.next();
			int u = ((Integer) mapentry.getValue()).intValue();// value
			String m = ((String) mapentry.getKey()).toString();// key
			if (stopwordmap.get(mapentry.getKey()) == null) {
				map.put(m, u);
				mapIndex.put(m, num);
				num++;
			}
		}
	}

	public static void readFileByChars(Map filemap, Map mapIndex, Map newmap)
			throws IOException {
		Set set = filemap.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry mapentry = (Map.Entry) iterator.next();
			if (mapIndex.get(mapentry.getKey()) == null)
				;
			else {
				int u = ((Integer) mapentry.getValue()).intValue();// value
				String m = ((String) mapentry.getKey()).toString();// key
				newmap.put(m, u);
			}
		}
	}

	
}

class Constants {
	public static final long BUFFER_SIZE_LONG = 10000;
	public static final short BUFFER_SIZE_SHORT = 512;
	
	public static final int MODEL_STATUS_UNKNOWN = 0;
	public static final int MODEL_STATUS_EST = 1;
	public static final int MODEL_STATUS_ESTC = 2;
	public static final int MODEL_STATUS_INF = 3;
	
	//-----------------------------------------------------------------
	
	/***** used in LDADataPreparation。java *****/
		// Input
		public static final String PATH_DATAFILES = "C:/Users/zouc/Desktop/lda/corpus/CorpusJavaRanchFAQ";
		
		public static final String PATH_STOPWORDS = "C:/Users/zouc/Desktop/lda/corpus/stopwords.txt";  
		
		}

class LuceneUtil {
	
	public static List<String> tokenizeStringReturnList(Analyzer analyzer, String string) {
	    List<String> result = new ArrayList<String>();
	    try {
	      TokenStream stream  = analyzer.tokenStream(null, new StringReader(string));
	      stream.reset();
	      while (stream.incrementToken()) {
	        result.add(stream.getAttribute(CharTermAttribute.class).toString());
	      }
	    } catch (IOException e) {
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
	    return result;
	  }
	
	public static String tokenizeStringReturnString(Analyzer analyzer, String string) {
	    String str = "";
	    try {
	      TokenStream stream  = analyzer.tokenStream(null, new StringReader(string));
	      stream.reset();
	      while (stream.incrementToken()) {
	        str = str + " " +stream.getAttribute(CharTermAttribute.class).toString();
	      }
	    } catch (IOException e) {
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
	    return str;
	  }
}
