package phase2;

import java.io.*;
import java.util.*;

public class KLUtil_generateTraceMap implements Serializable {

	private static final long serialVersionUID = -3965922551823098111L;
	
	//public static String PHIFILEPATH = "E:/temp/output/casestudy/";
	//public static String TRACEMAPPATH = "C:/Users/Administrator/Desktop/mid-data/traceMap.txt";
	
//	public static String PHIFILEPATH = "C:/Users/zouc/Desktop/lda/output/";
//	public static String TRACEMAPPATH = "C:/Users/zouc/Desktop/lda/mid_data/traceMap.txt";
//	public static String BACKTRACEMAPPATH = "C:/Users/zouc/Desktop/lda/mid_data/backTraceMap.txt";
	
	public static String PHIFILEPATH = "C:/Users/nancy.quan/Desktop/lda/output/casestudy/";
	public static String TRACEMAPPATH =     "C:/Users/nancy.quan/Desktop/lda/mid_data/traceMap.txt";
	public static String BACKTRACEMAPPATH = "C:/Users/nancy.quan/Desktop/lda/mid_data/backTraceMap.txt";	
	
	public static double KLThreshold = 2.5;

	public static void main(String[] args) throws Exception {
		//KLAnalysis_crossLDAruns.constructAndSaveBackTraceMap();
		//KLAnalysis_crossLDAruns.constructAndSaveTraceMap();
		KLUtil_generateTraceMap.traceMapAdjacentLayerAnalysis(40);
		//KLUtil_generateTraceMap.backTraceMapAdjacentLayerAnalysis(4);
	}
	
	private static void backTraceMapAdjacentLayerAnalysis(int topicNumberOfLDARun) throws Exception {
		List<String> phiFiles = KLUtil_generateTraceMap.getPhiFiles();

		Hashtable<DictDistribution, List<Double>> traceMap = new Hashtable<DictDistribution, List<Double>>();

		

			String path1 = phiFiles.get(topicNumberOfLDARun-1);
			String path2 = phiFiles.get(topicNumberOfLDARun-2);

			List<double[]> list1 = new ArrayList<double[]>();
			List<double[]> list2 = new ArrayList<double[]>();
			// -------------file1 ----------------
			File file1 = new File(path1);
			BufferedReader br1 = new BufferedReader(new FileReader(file1));
			String str1 = "";
			while ((str1 = br1.readLine()) != null) {
				String[] arr = str1.split(" ");
				double[] darr1 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr1[j] = Double.valueOf(arr[j]);
				list1.add(darr1);
			}
			br1.close();
			// -------------file2 ----------------
			File file2 = new File(path2);
			BufferedReader br2 = new BufferedReader(new FileReader(file2));
			String str2 = "";
			while ((str2 = br2.readLine()) != null) {
				String[] arr = str2.split(" ");
				double[] darr2 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr2[j] = Double.valueOf(arr[j]);
				list2.add(darr2);
			}
			br2.close();
			// -------------------------------------

			// construct the traceMap
			for (int m = 0; m < list1.size(); m++) {

				double[] darr1 = list1.get(m);
				List<Double> al = new ArrayList<Double>();
				DictDistribution dd = new DictDistribution(topicNumberOfLDARun, m);

				for (int n = 0; n < list2.size(); n++) {

					double[] darr2 = list2.get(n);

					double kl = KLUtil_DivergenceCalculator
							.getKLDivergenceVectorSpaceDistance(darr1, darr2,
									darr1.length);
					al.add(kl);
				}

				traceMap.put(dd, al);
				System.out.println("Complete topicNumberOfLDARun="+dd.topicNumberOfLDARun+" whichTopic="+dd.whichTopic);

			}

		
			System.out.println("traceMap construction complete "+traceMap.size());
			System.out.println("layer="+topicNumberOfLDARun);
			for (int ii=0;ii<topicNumberOfLDARun;ii++) {
				
				DictDistribution dd = new DictDistribution(topicNumberOfLDARun,ii);
				List<Double> list = traceMap.get(dd);
				System.out.print(" "+ii+"(");
			
				for (int jj=0;jj<list.size();jj++) {
					if (list.get(jj)<KLThreshold){
						System.out.print(jj+" ");
						//System.out.println("connect whichTopic="+jj+" with KLDistance="+list.get(jj));
					}
				}
				System.out.print(")");
			}
	}
	
	private static void traceMapAdjacentLayerAnalysis(int topicNumberOfLDARun) throws Exception {
		List<String> phiFiles = KLUtil_generateTraceMap.getPhiFiles();

		Hashtable<DictDistribution, List<Double>> traceMap = new Hashtable<DictDistribution, List<Double>>();

		

			String path1 = phiFiles.get(topicNumberOfLDARun-1);
			String path2 = phiFiles.get(topicNumberOfLDARun);

			List<double[]> list1 = new ArrayList<double[]>();
			List<double[]> list2 = new ArrayList<double[]>();
			// -------------file1 ----------------
			File file1 = new File(path1);
			BufferedReader br1 = new BufferedReader(new FileReader(file1));
			String str1 = "";
			while ((str1 = br1.readLine()) != null) {
				String[] arr = str1.split(" ");
				double[] darr1 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr1[j] = Double.valueOf(arr[j]);
				list1.add(darr1);
			}
			br1.close();
			// -------------file2 ----------------
			File file2 = new File(path2);
			BufferedReader br2 = new BufferedReader(new FileReader(file2));
			String str2 = "";
			while ((str2 = br2.readLine()) != null) {
				String[] arr = str2.split(" ");
				double[] darr2 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr2[j] = Double.valueOf(arr[j]);
				list2.add(darr2);
			}
			br2.close();
			// -------------------------------------

			// construct the traceMap
			for (int m = 0; m < list1.size(); m++) {

				double[] darr1 = list1.get(m);
				List<Double> al = new ArrayList<Double>();
				DictDistribution dd = new DictDistribution(topicNumberOfLDARun, m);

				for (int n = 0; n < list2.size(); n++) {

					double[] darr2 = list2.get(n);

					double kl = KLUtil_DivergenceCalculator
							.getKLDivergenceVectorSpaceDistance(darr1, darr2,
									darr1.length);
					al.add(kl);
				}

				traceMap.put(dd, al);
				System.out.println("Complete topicNumberOfLDARun="+dd.topicNumberOfLDARun+" whichTopic="+dd.whichTopic);

			}

		
			System.out.println("traceMap construction complete "+traceMap.size());
			System.out.println("layer="+topicNumberOfLDARun);
			for (int ii=0;ii<topicNumberOfLDARun;ii++) {
				
				DictDistribution dd = new DictDistribution(topicNumberOfLDARun,ii);
				List<Double> list = traceMap.get(dd);
				System.out.print(" "+ii+"(");
			
				for (int jj=0;jj<list.size();jj++) {
					if (list.get(jj)<KLThreshold){
						System.out.print(jj+" ");
						System.out.print("connect whichTopic="+jj+" with KLDistance="+list.get(jj)+"\n");
					}
				}
				System.out.print(")");
			}
	}

	/* Construct back trace map */
	private static void constructAndSaveBackTraceMap() throws Exception {
		List<String> phiFiles = KLUtil_generateTraceMap.getPhiFiles();

		Hashtable<DictDistribution, List<Double>> backTraceMap = new Hashtable<DictDistribution, List<Double>>();
		
		for (int i = phiFiles.size()-1; i >0 ; i--) {

			String path1 = phiFiles.get(i);
			String path2 = phiFiles.get(i - 1);

			List<double[]> list1 = new ArrayList<double[]>();
			List<double[]> list2 = new ArrayList<double[]>();
			// -------------file1 ----------------
			File file1 = new File(path1);
			BufferedReader br1 = new BufferedReader(new FileReader(file1));
			String str1 = "";
			while ((str1 = br1.readLine()) != null) {
				String[] arr = str1.split(" ");
				double[] darr1 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr1[j] = Double.valueOf(arr[j]);
				list1.add(darr1);
			}
			br1.close();
			// -------------file2 ----------------
			File file2 = new File(path2);
			BufferedReader br2 = new BufferedReader(new FileReader(file2));
			String str2 = "";
			while ((str2 = br2.readLine()) != null) {
				String[] arr = str2.split(" ");
				double[] darr2 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr2[j] = Double.valueOf(arr[j]);
				list2.add(darr2);
			}
			br2.close();
			// -------------------------------------

			// construct the back trace Map
			for (int m = 0; m < list1.size(); m++) {

				double[] darr1 = list1.get(m);
				List<Double> al = new ArrayList<Double>();
				DictDistribution dd = new DictDistribution(i + 1, m);

				for (int n = 0; n < list2.size(); n++) {

					double[] darr2 = list2.get(n);

					double kl = KLUtil_DivergenceCalculator
							.getKLDivergenceVectorSpaceDistance(darr1, darr2,
									darr1.length);
					al.add(kl);
				}

				backTraceMap.put(dd, al);
				System.out.println("Complete topicNumberOfLDARun="+dd.topicNumberOfLDARun+" whichTopic="+dd.whichTopic);

			}

		}
		System.out.println("backTraceMap construction complete");

		FileOutputStream fs = new FileOutputStream(BACKTRACEMAPPATH);
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(backTraceMap);
		os.flush();
		os.close();
		fs.close();
	}
	
	/* Construct trace map */
	private static void constructAndSaveTraceMap() throws Exception {
		List<String> phiFiles = KLUtil_generateTraceMap.getPhiFiles();

		Hashtable<DictDistribution, List<Double>> traceMap = new Hashtable<DictDistribution, List<Double>>();

		for (int i = 0; i < phiFiles.size() - 1; i++) {

			String path1 = phiFiles.get(i);
			String path2 = phiFiles.get(i + 1);

			List<double[]> list1 = new ArrayList<double[]>();
			List<double[]> list2 = new ArrayList<double[]>();
			// -------------file1 ----------------
			File file1 = new File(path1);
			BufferedReader br1 = new BufferedReader(new FileReader(file1));
			String str1 = "";
			while ((str1 = br1.readLine()) != null) {
				String[] arr = str1.split(" ");
				double[] darr1 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr1[j] = Double.valueOf(arr[j]);
				list1.add(darr1);
			}
			br1.close();
			// -------------file2 ----------------
			File file2 = new File(path2);
			BufferedReader br2 = new BufferedReader(new FileReader(file2));
			String str2 = "";
			while ((str2 = br2.readLine()) != null) {
				String[] arr = str2.split(" ");
				double[] darr2 = new double[arr.length];
				for (int j = 0; j < arr.length; j++)
					darr2[j] = Double.valueOf(arr[j]);
				list2.add(darr2);
			}
			br2.close();
			// -------------------------------------

			// construct the traceMap
			for (int m = 0; m < list1.size(); m++) {

				double[] darr1 = list1.get(m);
				List<Double> al = new ArrayList<Double>();
				DictDistribution dd = new DictDistribution(i + 1, m);

				for (int n = 0; n < list2.size(); n++) {

					double[] darr2 = list2.get(n);

					double kl = KLUtil_DivergenceCalculator
							.getKLDivergenceVectorSpaceDistance(darr1, darr2,
									darr1.length);
					al.add(kl);
				}

				traceMap.put(dd, al);
				System.out.println("Complete topicNumberOfLDARun="+dd.topicNumberOfLDARun+" whichTopic="+dd.whichTopic);

			}

		}
		System.out.println("traceMap construction complete");

		//KLAnalysis_crossLDAruns.printTraceMap(traceMap);
		KLUtil_generateTraceMap.saveTraceMapToFile(traceMap);
	}

	// ------------------- Util Functions ------------------------------
	private static List<String> getPhiFiles() {
		File folder = new File(PHIFILEPATH);
		String[] fileList = folder.list();
		List<String> targetFiles = new ArrayList<String>();
		for (String s : fileList)
			if (s.contains(".phi"))
				targetFiles.add(s);
		Collections.sort(targetFiles, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return getTopicNum(s1) - getTopicNum(s2);
			}

			private int getTopicNum(String name) {
				int index1 = name.indexOf("ntopics-");
				int index2 = name.indexOf("niters-");
				return Integer.valueOf(name.substring(index1 + 8, index2 - 1));
			}
		});

		List<String> targetFilesWithFullPath = new ArrayList<String>();
		for (int i = 0; i < targetFiles.size(); i++) {
			targetFilesWithFullPath.add(PHIFILEPATH + targetFiles.get(i));
		}
		return targetFilesWithFullPath;
	}

	private static void saveTraceMapToFile(
			Hashtable<DictDistribution, List<Double>> traceMap)
			throws IOException {
		FileOutputStream fs = new FileOutputStream(TRACEMAPPATH);
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(traceMap);
		os.flush();
		os.close();
		fs.close();
	}

	public static Hashtable<DictDistribution, List<Double>> readTraceMapFromFile()
			throws Exception {
		FileInputStream fs = new FileInputStream(TRACEMAPPATH);
		ObjectInputStream ois = new ObjectInputStream(fs);
		Hashtable<DictDistribution, List<Double>> ht = (Hashtable<DictDistribution, List<Double>>) ois
				.readObject();
		ois.close();
		fs.close();
		return ht;
	}
	
	public static Hashtable<DictDistribution, List<Double>> readBackTraceMapFromFile()
			throws Exception {
		FileInputStream fs = new FileInputStream(BACKTRACEMAPPATH);
		ObjectInputStream ois = new ObjectInputStream(fs);
		Hashtable<DictDistribution, List<Double>> ht = (Hashtable<DictDistribution, List<Double>>) ois.readObject();
		ois.close();
		fs.close();
		return ht;
	}
	
	public static void printTraceMap(Hashtable<DictDistribution, List<Double>> tm) {
		
		Iterator<DictDistribution> iter = tm.keySet().iterator();
		while (iter.hasNext()) {
			DictDistribution dd = iter.next();
			System.out.println(dd.topicNumberOfLDARun+"--"+dd.whichTopic);
			List<Double> list = tm.get(dd);
			for (double d:list)
				System.out.print(d+"\t");
			
			System.out.println();
		}
		
	}

}

class DictDistribution implements Serializable {

	private static final long serialVersionUID = -2410260397392439812L;
	int topicNumberOfLDARun;
	int whichTopic; // the topic number (e.g. 8th. topic) in one LDA run

	public DictDistribution(int _topicNumberOfLDARun, int _whichTopic) {
		this.topicNumberOfLDARun = _topicNumberOfLDARun;
		this.whichTopic = _whichTopic;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		DictDistribution other = (DictDistribution) o;
		if (this.topicNumberOfLDARun == other.topicNumberOfLDARun
				&& this.whichTopic == other.whichTopic)
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		int i1 = this.topicNumberOfLDARun * 1000;
		int i2 = this.whichTopic;
		return i1 | i2;
	}
}
