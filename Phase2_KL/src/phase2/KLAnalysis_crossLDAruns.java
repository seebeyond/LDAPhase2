package phase2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KLAnalysis_crossLDAruns {
	
	public static String PHIFILEPATH = "E:/temp/output/casestudy/";
	
	public static void main(String[] args) {
		
		List<String> phiFiles = KLAnalysis_crossLDAruns.getPhiFiles();
		
		
		
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
		
//		for (String s:targetFiles)
//			System.out.println(s);
		
		List<String> targetFilesWithFullPath = new ArrayList<String>();
		for (int i=0;i<targetFiles.size(); i++) {
			targetFilesWithFullPath.add(PHIFILEPATH+targetFiles.get(i));
		}
		return targetFilesWithFullPath;
	}

}
