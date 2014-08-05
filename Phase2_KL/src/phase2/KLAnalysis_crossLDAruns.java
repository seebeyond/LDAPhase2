package phase2;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;

public class KLAnalysis_crossLDAruns {
	
	public static String PHIFILEPATH = "E:/temp/output/casestudy/";
	
	public static void main(String[] args) throws IOException {
		
		List<String> phiFiles = KLAnalysis_crossLDAruns.getPhiFiles();
		
		for (int i=0;i<phiFiles.size()-1;i++) {
			
			String path1 = phiFiles.get(i);
			String path2 = phiFiles.get(i+1);
			
			List<Double[]> list1 = new ArrayList<Double[]>();
			List<Double[]> list2 = new ArrayList<Double[]>();
			//-------------file1 ----------------
			File file1 = new File(path1);
			BufferedReader br1  =new BufferedReader(new FileReader(file1));
			String str1 = "";
			while ( (str1=br1.readLine())!= null  ) {
				String[] arr = str1.split(" ");
				Double[] darr1 = new Double[arr.length];
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
				Double[] darr2 = new Double[arr.length];
				for (int j=0;j<arr.length; j++) 
					darr2[i] = Double.valueOf(arr[j]);
				list2.add(darr2);
			}
			br2.close();
			//-------------------------------------
			
			
			
		}
		
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

class KLMap {
	int topicNumberOfLDARun;
	int whichTopic; // the topic number (e.g. 8th. topic) in one LDA run
	Hashtable<Integer, Double> tracemap;
}
