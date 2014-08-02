package phase2;

import java.io.*;
import java.util.*;
import java.text.*;
public class Client {
	public static String CORPUS = "C:\\Users\\zouc\\Desktop\\lda\\corpus\\CorpusJavaRanchFAQ\\";
	public static String PREPATH = "C:\\Users\\zouc\\Desktop\\lda\\output\\";
	public static String SAVEPATH = "C:\\Users\\zouc\\Desktop\\lda\\mid_data\\";
	public static String DICT = "C:\\Users\\zouc\\Desktop\\lda\\output\\wordmap.txt";
	
	public static void main(String[] args) throws IOException {
		
		Client t = new Client();
		
		/*
		List<String> phiFiles = t.getPhiFiles(PREPATH);
		
		double[] comparable = t.getComparable(phiFiles, 0, 0);
		
		//List<List<Double>> lists = t.compareDoubleArrWithSomePhiFiles(phiFiles, t.getManualArr(DICT), 0, 10);
		List<List<Double>> lists = t.compareDoubleArrWithAllPhiFiles(phiFiles, t.getManualArr());
		//List<List<Double>> lists = t.compareDoubleArrWithAllPhiFiles(phiFiles, comparable);
		//List<List<Double>> lists = t.compareDoubleArrWithSomePhiFiles(phiFiles, comparable, 0, 10);
		
		List<List<Double>> sortedLists = t.sortKLDistance(lists);
		
		t.saveToFile(SAVEPATH+"1.txt", sortedLists);
		*/
		
		t.solution_comparing_articles_and_LDAoutput();
	}
	
	//---------------------------------------------------------------------------------------
	// This is part is added for the discussion on 07.31. 
	// Analyze the FAQ articles as input.
	//---------------------------------------------------------------------------------------
	public void solution_comparing_articles_and_LDAoutput() throws IOException {
		List<String> phiFiles = Client.getPhiFiles(PREPATH);
		List<WordMap> dict = Client.getWordMapList(); // get words in dict
		List<String> corpusFilesWithFullPath = Client.getCorpusFiles();
		int counter = 0;
		for (String path:corpusFilesWithFullPath) {
			Map<String, Integer> articleWords = DataPreparation.getArticleWordMap(path); // 
			double[] myCreatedWordDistribution = Client.calArticleWordProbabilityDistribution(dict, articleWords);
			//List<List<Double>> klDistanceResult = Client.compareDoubleArrWithSomePhiFiles(phiFiles, myCreatedWordDistribution, 0,9);
			List<List<Double>> klDistanceResult = Client.compareDoubleArrWithAllPhiFiles(phiFiles, myCreatedWordDistribution);
			List<List<Double>> sortedklDistanceResult = Client.sortKLDistance(klDistanceResult);
						
			Client.saveToFile(SAVEPATH+"compare_result_"+Client.getCorpusFileName(path), sortedklDistanceResult);
			System.out.println("complete "+ counter);
			counter++;
		}
	}
	private static String getCorpusFileName(String filePath) {
		String[] arr = filePath.split("\\\\");
		return arr[arr.length-1];
	}
	private static List<String> getCorpusFiles() {
		File folder = new File(CORPUS);		
		String[] fileList = folder.list(); 
		List<String> corpusFilesWithFullPath = new ArrayList<String>();
		for (String s: fileList) {
			corpusFilesWithFullPath.add(CORPUS+s);
		}
		return corpusFilesWithFullPath;
	}
	private static List<WordMap> getWordMapList() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(Client.DICT)));
		br.readLine();
		List<WordMap> wordmap = new ArrayList<WordMap>();
		String str = "";
		while ((str=br.readLine())!=null) {
			String[] strs = str.split(" ");
			wordmap.add(new WordMap(strs[0], Integer.valueOf(strs[1])));
		}
		br.close();
		
		Collections.sort(wordmap, new Comparator<WordMap>(){
			@Override
			public int compare(WordMap wm1, WordMap wm2) {
				return wm1.seq-wm2.seq;
			}
		});
		return wordmap;
	}
	private static double[] calArticleWordProbabilityDistribution(List<WordMap> dict, Map<String, Integer> articleWords) throws IOException {
		
		Iterator<String> iter = articleWords.keySet().iterator();
		int count = 0;
		while (iter.hasNext()) {
			count += articleWords.get(iter.next());
		}
		
		double averageProbability = 1.0/count;
		
		double[] ret = new double[dict.size()];
		for (int i=0;i<dict.size();i++) {
			if (articleWords.containsKey((dict.get(i).word)))
				ret[i] = averageProbability * articleWords.get(dict.get(i).word);
			else
				ret[i] = 0.0;
		}		
		return ret;
	}
	
	//------------------------- END of 0731 ---------------------------------------------------------------
	
	
	private double[] getManualArr() throws IOException {
		//String[] keywords = {"upload","fileupload","filename","post","fileoutputstream","utf","mime","size","path"};
		//String[] keywords = {"thread", "instanc", "multipl", "safe", "pool", "singleton", "simultan", "multi"};
		String[] keywords = {"session","scope","httpsession","setattribut","getattribut","sessionid"};
		List<String> keywordList = new ArrayList<String>();
		for (String kw:keywords)
			keywordList.add(kw);
		
		double averageProbability = 1.0/(keywords.length);
		
		List<WordMap> wordmap = Client.getWordMapList();		
		
		double[] ret = new double[wordmap.size()];
		for (int i=0;i<wordmap.size();i++) {
			if (keywordList.contains(wordmap.get(i).word))
				ret[i] = averageProbability;
			else
				ret[i] = 0.0;
		}		
		return ret;
	}
	
	
	
	private static void saveToFile(String fileName, List<List<Double>> sortedLists) throws IOException {
		File file = new File(fileName);
		DecimalFormat df=new DecimalFormat("#.00000"); 
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		for (List<Double> al : sortedLists) {
			for (double d:al) {
				bw.write(df.format(d) + " ");
			}
			bw.write("\n");
		}
		bw.close();
	}
	
	private static List<List<Double>> sortKLDistance(List<List<Double>> lists) {
		List<List<Double>> sortedLists =  new ArrayList<List<Double>>();
		for (List<Double> al : lists) {
			Collections.sort(al);
			sortedLists.add(al);
		}
		return sortedLists;
	}
	
	private static List<List<Double>> compareDoubleArrWithSomePhiFiles(List<String> phiFiles, double[] comparable, int start, int end) throws IOException {
		List<List<Double>> lists = new ArrayList<List<Double>>();
		for (int f=start;f<end; f++) {
			List<Double> al = new ArrayList<Double>();
			File file = new File(phiFiles.get(f));
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";
			double[] curArr = null;
			while ( (str=br.readLine())!= null ) {
				String[] arr = str.split(" ");
				curArr = new double[arr.length];
				for (int i=0;i<arr.length; i++) {
					curArr[i] = Double.valueOf(arr[i]);
				}				
				double klDistance = KLDivergenceCalculator.getKLDivergenceVectorSpaceDistance(comparable, curArr, comparable.length);
				al.add(klDistance);
			}
			lists.add(al);
			br.close();
		}
		return lists;
	}
	
	private static List<List<Double>> compareDoubleArrWithAllPhiFiles(List<String> phiFiles, double[] comparable) throws IOException {
		List<List<Double>> lists = new ArrayList<List<Double>>();
		int counter=0;
		for (String fileName : phiFiles) {
			System.out.println("comparing "+fileName+" topic number "+ counter);
			counter++;
			List<Double> al = new ArrayList<Double>();
			File file = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";
			double[] curArr = null;
			while ( (str=br.readLine())!= null ) {
				String[] arr = str.split(" ");
				curArr = new double[arr.length];
				for (int i=0;i<arr.length; i++) {
					curArr[i] = Double.valueOf(arr[i]);
				}				
				double klDistance = KLDivergenceCalculator.getKLDivergenceVectorSpaceDistance(comparable, curArr, comparable.length);
				al.add(klDistance);
			}
			lists.add(al);
			br.close();
		}
		return lists;
	}
	
	// get which phiFile ? -- phiFileNum
	// get which topic ? -- topicNum
	private double[] getComparable(List<String> phiFiles, int phiFileNum, int topicNum) throws IOException {
		String fileName = phiFiles.get(phiFileNum);
		File file = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = "";
		int counter = 0;
		double[] ret = null;
		while ( (str=br.readLine())!= null ) {
			if (counter == topicNum) {
				String[] arr = str.split(" ");
				ret = new double[arr.length];
				for (int i=0;i<arr.length; i++) {
					ret[i] = Double.valueOf(arr[i]);
				}
				break;
			} else {
				counter++;
			}
		}
		br.close();
		return ret;
	}
	
	
	
	private static List<String> getPhiFiles(String prepath) {
		File folder = new File(prepath);		
		String[] fileList = folder.list();  
		List<String> targetFiles = new ArrayList<String>();
		for (String s:fileList)
			if (s.contains(".phi"))
				targetFiles.add(s);
		Collections.sort(targetFiles, new Comparator<String>(){
			@Override
			public int compare(String s1, String s2) {
				return getTopicNum(s1)-getTopicNum(s2);
			}
			private int getTopicNum(String name) {
				int index1 = name.indexOf("ntopics-");
				int index2 = name.indexOf("niters-");
				return Integer.valueOf(name.substring(index1+8, index2-1));
			}
		});
		
//		for (String s:targetFiles)
//			System.out.println(s);
		
		List<String> targetFilesWithFullPath = new ArrayList<String>();
		for (int i=0;i<targetFiles.size(); i++) {
			targetFilesWithFullPath.add(prepath+targetFiles.get(i));
		}
		return targetFilesWithFullPath;
	}
	
}
