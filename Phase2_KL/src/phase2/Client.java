package phase2;

import java.io.*;
import java.util.*;
import java.text.*;
public class Client {
	public static String PREPATH = "C:\\Users\\zouc\\Desktop\\lda\\output\\";
	public static String SAVEPATH = "C:\\Users\\zouc\\Desktop\\lda\\mid_data\\1.txt";
	public static String DICT = "C:\\Users\\zouc\\Desktop\\lda\\output\\wordmap.txt";
	
	public static void main(String[] args) throws IOException {
		
		Client t = new Client();
		List<String> phiFiles = t.getPhiFiles(PREPATH);
		
		double[] comparable = t.getComparable(phiFiles, 0, 0);
		
		//List<List<Double>> lists = t.compareDoubleArrWithSomePhiFiles(phiFiles, t.getManualArr(DICT), 0, 10);
		
		List<List<Double>> lists = t.compareDoubleArrWithAllPhiFiles(phiFiles, t.getManualArr(DICT));
		
		//List<List<Double>> lists = t.compareDoubleArrWithAllPhiFiles(phiFiles, comparable);
		
		//List<List<Double>> lists = t.compareDoubleArrWithSomePhiFiles(phiFiles, comparable, 0, 10);
		
		List<List<Double>> sortedLists = t.sortKLDistance(lists);
		
		t.saveToFile(SAVEPATH, sortedLists);
	}
	
	
	private double[] getManualArr(String dictPath) throws IOException {
		//String[] keywords = {"upload","fileupload","filename","post","fileoutputstream","utf","mime","size","path"};
		//String[] keywords = {"thread", "instanc", "multipl", "safe", "pool", "singleton", "simultan", "multi"};
		String[] keywords = {"session","scope","httpsession","setattribut","getattribut","sessionid"};
		List<String> keywordList = new ArrayList<String>();
		for (String kw:keywords)
			keywordList.add(kw);
		
		double averageProbability = 1.0/(keywords.length);
		
		BufferedReader br = new BufferedReader(new FileReader(new File(dictPath)));
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
		
		double[] ret = new double[wordmap.size()];
		for (int i=0;i<wordmap.size();i++) {
			if (keywordList.contains(wordmap.get(i).word))
				ret[i] = averageProbability;
			else
				ret[i] = 0.0;
				//ret[i] = 1.0e-250;
		}
		
		return ret;
	}
	
	private void saveToFile(String fileName, List<List<Double>> sortedLists) throws IOException {
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
	
	private List<List<Double>> sortKLDistance(List<List<Double>> lists) {
		List<List<Double>> sortedLists =  new ArrayList<List<Double>>();
		for (List<Double> al : lists) {
			Collections.sort(al);
			sortedLists.add(al);
		}
		return sortedLists;
	}
	
	private List<List<Double>> compareDoubleArrWithSomePhiFiles(List<String> phiFiles, double[] comparable, int start, int end) throws IOException {
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
	
	private List<List<Double>> compareDoubleArrWithAllPhiFiles(List<String> phiFiles, double[] comparable) throws IOException {
		List<List<Double>> lists = new ArrayList<List<Double>>();
		for (String fileName : phiFiles) {
		//for (int f=0;f<phiFiles.size(); f++) {
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
	
	
	
	private List<String> getPhiFiles(String prepath) {
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
