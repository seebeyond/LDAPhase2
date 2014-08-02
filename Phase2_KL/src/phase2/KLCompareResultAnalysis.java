package phase2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class KLCompareResultAnalysis {

	public static String RESULTPATH = "C:\\Users\\zouc\\Desktop\\lda\\mid_data\\";
	
	public static void main(String[] args) throws IOException {
		
		KLCompareResultAnalysis.klCompareResultAnalysis();
	}
	
	public static void klCompareResultAnalysis() throws IOException{
		List<String> klCompareResultFilesWithFullPath = KLCompareResultAnalysis.getKLCompareResultFiles();
		
		List<Integer> minvalTopicNumberList = new ArrayList<Integer>();
		
		for (String path:klCompareResultFilesWithFullPath) {
			
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";
			double minval = Double.MAX_VALUE;
			int preparameter_topicNumber_whenMinVal = 0;
			int ldaRunWithHowManyTopicNumber = 1;
			while ( (str=br.readLine())!=null ) {
				if (ldaRunWithHowManyTopicNumber==1) {
					if (Double.valueOf(str) < minval) {
						minval = Double.valueOf(str);
						preparameter_topicNumber_whenMinVal = ldaRunWithHowManyTopicNumber;
					}
					ldaRunWithHowManyTopicNumber++;
				} else {
					int i=0;
					for (;i<str.length();i++) {
						if (str.charAt(i)==' '){
							break;
						}
					}
					if (Double.valueOf(str.substring(0,i)) < minval) {
						minval = Double.valueOf(str.substring(0,i));
						preparameter_topicNumber_whenMinVal = ldaRunWithHowManyTopicNumber;
					}
					ldaRunWithHowManyTopicNumber++;
				}
			}
			
			// output result:
			String fileName = "";
			String[] arr = path.split("\\\\");
			fileName = arr[arr.length-1];
			System.out.println("For comparison result \""+fileName+"\"\n\t\t the LDA run with pre-parameter topicNumber="+preparameter_topicNumber_whenMinVal+" has the minimum KL distance which is "+minval);
			minvalTopicNumberList.add(preparameter_topicNumber_whenMinVal);
		}
		
		int total = 0;
		for (int i: minvalTopicNumberList)
			total +=i;
		System.out.println("The average value of all the above topicNumber is " + total/minvalTopicNumberList.size());		
		
	}
	
	private static List<String> getKLCompareResultFiles() {
		File folder = new File(RESULTPATH);		
		String[] fileList = folder.list(); 
		List<String> klCompareResultFilesWithFullPath = new ArrayList<String>();
		for (String s: fileList) {
			klCompareResultFilesWithFullPath.add(RESULTPATH+s);
		}
		return klCompareResultFilesWithFullPath;
	}
	

}
