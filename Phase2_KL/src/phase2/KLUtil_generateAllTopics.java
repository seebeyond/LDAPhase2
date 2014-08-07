package phase2;

import java.io.*;
import java.util.*;

/*
 * This util generates all topics (keywords=20) from LDA run 1-200
 * 
 * */
public class KLUtil_generateAllTopics {
	
	public static String prepath = "C:\\Users\\zouc\\Desktop\\lda\\output\\";
	public static String allTopicPath = "C:/Users/zouc/Desktop/lda/mid_data/allTopicKeywords.txt";
	
	public static void main(String[] args) throws Exception {
		
		KLUtil_generateAllTopics.generateAllTopics();
	}
	
	private static void generateAllTopics() throws Exception {
		List<String> twordsFiles = KLUtil_generateAllTopics.getTwordsFiles();
		
		File file = new File(allTopicPath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		for (int i=0;i<twordsFiles.size(); i++) {
			
			File twordsFile = new File(twordsFiles.get(i));
			BufferedReader br = new BufferedReader(new FileReader(twordsFile));
			String str = "";
			String atopic = "";
			int topicNum = 0;
			while ( (str=br.readLine()) != null ) {
				
				if (str.contains("Topic")) {
					if (atopic.length() > 10) {
						bw.write(atopic+"\n");
					}
					atopic = "("+(i+1)+" "+ topicNum + ") ";
					topicNum++;
				} else {
					
					String[] arr = str.split(" ");
					atopic += arr[1]+" ";					
				}				
			}
			bw.write(atopic+"\n");
			br.close();
		}
		
		bw.close();
	}
	
	
	private static List<String> getTwordsFiles() {
		File folder = new File(prepath);		
		String[] fileList = folder.list();  
		List<String> targetFiles = new ArrayList<String>();
		for (String s:fileList)
			if (s.contains(".twords"))
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

