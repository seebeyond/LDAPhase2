package phase2;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;

public class KLAnalysis_crossLDAruns {
	
	public static String PHIFILEPATH = "C:/Users/zouc/Desktop/lda/output/";
	
	public static void main(String[] args) throws IOException {
		
		List<String> phiFiles = KLAnalysis_crossLDAruns.getPhiFiles();
		
		Hashtable<DictDistribution, List<Double>> traceMap = new Hashtable<DictDistribution, List<Double>>();
		
		for (int i=0;i<phiFiles.size()-1;i++) {
			
			String path1 = phiFiles.get(i);
			String path2 = phiFiles.get(i+1);
			
			List<double[]> list1 = new ArrayList<double[]>();
			List<double[]> list2 = new ArrayList<double[]>();
			//-------------file1 ----------------
			File file1 = new File(path1);
			BufferedReader br1  =new BufferedReader(new FileReader(file1));
			String str1 = "";
			while ( (str1=br1.readLine())!= null  ) {
				String[] arr = str1.split(" ");
				double[] darr1 = new double[arr.length];
				for (int j=0;j<arr.length; j++) 
					darr1[i] = Double.valueOf(arr[j]);
				list1.add(darr1);
			}
			br1.close();
			//-------------file2 ----------------
			File file2 = new File(path2);
			BufferedReader br2  =new BufferedReader(new FileReader(file2));
			String str2 = "";
			while ( (str2=br2.readLine())!= null  ) {
				String[] arr = str2.split(" ");
				double[] darr2 = new double[arr.length];
				for (int j=0;j<arr.length; j++) 
					darr2[i] = Double.valueOf(arr[j]);
				list2.add(darr2);
			}
			br2.close();
			//-------------------------------------
			
			// construct the traceMap
			for (int m=0; m<list1.size(); m++) {
				
				double[] darr1 = list1.get(m);
				List<Double> al = new ArrayList<Double>();
				DictDistribution dd = new DictDistribution(i+1, m);
				
				
				for (int n=0;n<list2.size(); n++) {	
					
					double[] darr2 = list2.get(n);
					
					double kl = KLDivergenceCalculator.getKLDivergenceVectorSpaceDistance(darr1, darr2, darr1.length);
					al.add(kl);
				}
				
				traceMap.put(dd, al);
				System.out.println("Complete topicNumberOfLDARun="+dd.topicNumberOfLDARun+" whichTopic="+dd.whichTopic);
				
			}
			
		}
		System.out.println();
	}
	
	
	private static List<String> getPhiFiles() {
		File folder = new File(PHIFILEPATH);		
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
		
		List<String> targetFilesWithFullPath = new ArrayList<String>();
		for (int i=0;i<targetFiles.size(); i++) {
			targetFilesWithFullPath.add(PHIFILEPATH+targetFiles.get(i));
		}
		return targetFilesWithFullPath;
	}

}

class DictDistribution {
	int topicNumberOfLDARun;
	int whichTopic; // the topic number (e.g. 8th. topic) in one LDA run
	public DictDistribution(int _topicNumberOfLDARun, int _whichTopic) {
		this.topicNumberOfLDARun = _topicNumberOfLDARun;
		this.whichTopic = _whichTopic;
	}
	@Override 
	public boolean equals(Object o) {
		if (o==null)
			return false;
		DictDistribution other = (DictDistribution)o;
		if (this.topicNumberOfLDARun==other.topicNumberOfLDARun && this.whichTopic==other.whichTopic)
				return true;
		else
				return false;
	}
	@Override
	public int hashCode() {
		int i1 = this.topicNumberOfLDARun*1000;
		int i2 = this.whichTopic;
		return i1|i2;
	}
}
