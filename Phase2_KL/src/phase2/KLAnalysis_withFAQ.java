package phase2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class KLAnalysis_withFAQ {
	//public static String RESULTPATH = "C:\\Users\\zouc\\Desktop\\lda\\mid_data\\";
		public static String RESULTPATH = "C:\\Users\\Administrator\\Desktop\\mid-data\\";
		
		public static void main(String[] args) throws IOException {
			
			//KLAnalysis_withFAQ.klCompareResult_findSmallestLDARun();
			//KLAnalysis_withFAQ.klCompareResult_findAverageSmallestLDARun();
			KLAnalysis_withFAQ.klCompareResult_findAverage2();
		}
		
		/*
		 * Given a specific topicNumber LDA run, calculate the average of min KL over all articles.
		 * Then calculate above over all topicNumber LDA runs. And then find the smallest.
		 */
		public static void klCompareResult_findAverage2() throws IOException {
			
			List<String> klCompareResultFilesWithFullPath = KLAnalysis_withFAQ.getKLCompareResultFiles();
			
			double[] minKLArr = new double[200];
			
			for (String path:klCompareResultFilesWithFullPath) {
				
				File file = new File(path);
				BufferedReader br = new BufferedReader(new FileReader(file));
				String str = "";
				int ldaRunWithHowManyTopicNumber = 1;
				while ( (str=br.readLine())!=null ) {
					if (ldaRunWithHowManyTopicNumber==1) {
						minKLArr[ldaRunWithHowManyTopicNumber-1] += Double.valueOf(str);
						ldaRunWithHowManyTopicNumber++;
					} else {
						int i=0;
						for (;i<str.length();i++) {
							if (str.charAt(i)==' '){
								break;
							}
						}
						
						minKLArr[ldaRunWithHowManyTopicNumber-1] += Double.valueOf(str.substring(0,i));					
						ldaRunWithHowManyTopicNumber++;
					}
				}
				
				br.close();
			}
			
			for (int i=0;i<minKLArr.length;i++) {
				minKLArr[i] = minKLArr[i]/klCompareResultFilesWithFullPath.size();
				System.out.println(minKLArr[i]);
			}
			
			double min = Double.MAX_VALUE;
			int topicNumber = 0;
			for (int i=0;i<minKLArr.length; i++) {
				if (minKLArr[i]<min) {
					min = minKLArr[i];
					topicNumber = i+1;
				}
			}
			System.out.println(min);
			System.out.println(topicNumber);		
		} 
		
		/*
		 * Give an article, every 1-200 LDA run has a smallest KL distance. Calculate the 200 smallest distance average value
		 * This function calculates above thing for all the articles.
		 * Meanwhile find out the smallest average value
		 */
		public static void klCompareResult_findAverageSmallestLDARun() throws IOException {
			List<String> klCompareResultFilesWithFullPath = KLAnalysis_withFAQ.getKLCompareResultFiles();
			
			for (String path:klCompareResultFilesWithFullPath) {
				
				File file = new File(path);
				BufferedReader br = new BufferedReader(new FileReader(file));
				String str = "";
				double totalKLDistance = 0;
				int ldaRunWithHowManyTopicNumber = 1;
				while ( (str=br.readLine())!=null ) {
					if (ldaRunWithHowManyTopicNumber==1) {
						totalKLDistance += Double.valueOf(str);
						ldaRunWithHowManyTopicNumber++;
					} else {
						int i=0;
						for (;i<str.length();i++) {
							if (str.charAt(i)==' '){
								break;
							}
						}
						
						totalKLDistance += Double.valueOf(str.substring(0,i));					
						ldaRunWithHowManyTopicNumber++;
					}
				}
				ldaRunWithHowManyTopicNumber--;
				br.close();
				// output result:
				String fileName = "";
				String[] arr = path.split("\\\\");
				fileName = arr[arr.length-1];
				System.out.println("For comparison result \""+fileName+"\"\n\t\t the average of all smallest KL distances is "+totalKLDistance/ldaRunWithHowManyTopicNumber);

			}
			
		}
		
		/*
		 * Give an article, find out the smallest KL distance from the 1-200 LDA run
		 * This function calculates above thing for all the articles
		 */
		public static void klCompareResult_findSmallestLDARun() throws IOException{
			
			List<String> klCompareResultFilesWithFullPath = KLAnalysis_withFAQ.getKLCompareResultFiles();
			
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
				br.close();

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