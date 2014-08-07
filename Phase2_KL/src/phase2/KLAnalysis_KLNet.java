package phase2;

import java.util.*;
import java.io.*;

public class KLAnalysis_KLNet {
	
	public static int 		topicNumberOfLDARun = 40;
	public static int 		whichTopic = 11;
	public static double 	connectThreshold=2.8;
	public static double 	fixedconnectThreshold=2.8;
	public static double    connectThresholdMinus = 0.0000; //initial 0.005
	
	public static String outputPath = "C:/Users/zouc/Desktop/lda/mid_data/layerCompare.txt";  // --> obsolete
	public static String branchPath ="C:/Users/zouc/Desktop/lda/mid_data/";
	public static String allTopicKeywordsPath = "C:/Users/zouc/Desktop/lda/mid_data/allTopicKeywords.txt";
	
	public static void main(String[] args) throws Exception {
		
		Hashtable<DictDistribution, List<Double>> backTraceMap = KLUtil_generateTraceMap.readBackTraceMapFromFile();
		Hashtable<DictDistribution, List<Double>> traceMap = KLUtil_generateTraceMap.readTraceMapFromFile();		
		
		
		
		
	}
	
	
	// --------------------------- CORE FUnction of this class >>>>> start ------------------------------------------------
	private static void getBranchSaveBranchSaveKeywords(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		// get branch 
		List<String> branch = KLAnalysis_KLNet.getFullBranchUpAndDown(topicNumberOfLDARun, whichTopic, backTraceMap, traceMap);
				
		// save branch into local file
		KLAnalysis_KLNet.printAndSaveBranch(branch, topicNumberOfLDARun, whichTopic, fixedconnectThreshold);
				
		//get topic keywords for the topics in branch, and then save it into local file	
		KLAnalysis_KLNet.saveAllTopicKeywordsInBranch(branch, topicNumberOfLDARun, whichTopic, fixedconnectThreshold);
	}
	
	private static List<String> getFullBranchUpAndDown(int topicNumberOfLDARun, int whichTopic, Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		List<String> branch = new ArrayList<String>();		
		KLAnalysis_KLNet.getFullBranchUp(topicNumberOfLDARun, whichTopic, branch, backTraceMap);
		branch = KLAnalysis_KLNet.reverse(branch);
		KLAnalysis_KLNet.getFullBranchDown(topicNumberOfLDARun, whichTopic, branch, traceMap);
		return branch;
	}
	// --------------------------- CORE FUnction of this class <<<<< end ------------------------------------------------
	
	
	//-----------------"save all topic keywords in a specific branch" >>>>> START ----------------------
	public static void saveAllTopicKeywordsInBranch(List<String> branch, int topicNumberOfLDARun, int whichTopic, double connectThreshold) throws Exception{
		
		// calculat a line number in alltopicKeywords file		
		List<Integer> lines = KLAnalysis_KLNet.getSeq(branch);
		// save lines into stack would be great for further process
		Stack<Integer> slines = new Stack<Integer>();
		for (int i=lines.size()-1; i>=0;i--)
			slines.push(lines.get(i));
		
		File file = new File(branchPath+"branch "+topicNumberOfLDARun+" "+whichTopic+" "+connectThreshold+" topicKeyWords.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		File rfile = new File(allTopicKeywordsPath);
		BufferedReader br = new BufferedReader(new FileReader(rfile));
		String str = "";
		int lineNum = 1;
		while ( (str=br.readLine())!=null ) {
			if (slines.peek() == lineNum) {
				bw.write(str+"\n");
				lineNum++;
				slines.pop();
				if (slines.isEmpty()==true)
					break;
			} else {
				lineNum++;
			}
		}
		br.close();
		bw.close();
	}
	private static List<Integer> getSeq(List<String> branch) {
		
		List<Integer> seq = new ArrayList<Integer>();		
//		branch.add("[(1 0)->(3 2)] ");
//		branch.add("[(108 95)->(109 50)(109 83)] ");
//		branch.add("[(109 50)->(110 21)] [(109 83)->(110 21)] ");
		for (String  s:branch) {
			
			String[] arr1 = s.split("\\)");
			
			for (String ss:arr1) {
				
				if (ss.contains("(")) {
					String[] arr2 = ss.split("\\(");
					int seqNum = getSeqNum(arr2[1]);
					seq.add(seqNum);
				}
			}
			
		}
		Collections.sort(seq);
		List<Integer> noDupSeq = new ArrayList<Integer>();
		for (int i:seq) {
			if (noDupSeq.size()==0 || noDupSeq.get(noDupSeq.size()-1) != i )
				noDupSeq.add(i);
		}
		return noDupSeq;
	}
	// the formula is:  (1 0)=>line 1;  (3 2)=>line6; (m n)=> sum of 1 to (m-1), then add n+1
	private static int getSeqNum(String str) {
		String[] arr = str.split(" ");
		int layer = Integer.valueOf(arr[0]);
		int topic = Integer.valueOf(arr[1]);
		int seqNum = 0;
		for (int i=1;i<layer; i++) {
			seqNum += i;
		}
		seqNum += topic + 1;
		return seqNum;
	}
	//------------------"save all topic keywords in a specific branch" <<<<< END ------------------
	
	public static void getFullBranchUp(int layer, int whichTopic, List<String> branch, Hashtable<DictDistribution, List<Double>> backTraceMap) throws Exception {
		
		List<String> fullBranchUp = new ArrayList<String>();
		DictDistribution dd = new DictDistribution(layer, whichTopic);
		LinkedList<DictDistribution> ll= new LinkedList<DictDistribution>();
		ll.add(dd);
		int count = 1;
		
		while (!ll.isEmpty()) {
			
			int newcount=0;
			String curLayerBranch = "";
			
			while (count>0) {
				
				DictDistribution cur = ll.pop();
				count--;
				curLayerBranch +=  "[("+cur.topicNumberOfLDARun+" "+cur.whichTopic+")<-";
				
				List<Double> dlist = backTraceMap.get(cur);
				if (dlist==null)
					return ;
				
				for (int i=0;i<dlist.size();i++) {
					if (dlist.get(i)<connectThreshold) {
						DictDistribution nextdd = new DictDistribution(cur.topicNumberOfLDARun-1, i);
						curLayerBranch += "("+nextdd.topicNumberOfLDARun+" "+nextdd.whichTopic+")";
						if (!ll.contains(nextdd)) {
							ll.add(nextdd);
							newcount++;
						}						
					}
				}
				curLayerBranch += "] ";				
			}
			
			//connection threshold minus
			connectThreshold = connectThreshold-connectThresholdMinus;
			
			count = newcount;
			fullBranchUp.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	public static void getFullBranchDown(int layer, int whichTopic, List<String> branch, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		
		List<String> fullBranch = new ArrayList<String>();
		
		DictDistribution dd = new DictDistribution(layer, whichTopic);
		LinkedList<DictDistribution> ll= new LinkedList<DictDistribution>();
		ll.add(dd);
		int count = 1;
		
		while (!ll.isEmpty()) {
			
			int newcount=0;
			String curLayerBranch = "";
			
			while (count>0) {
				
				DictDistribution cur = ll.pop();
				count--;
				curLayerBranch += "[("+cur.topicNumberOfLDARun+" "+cur.whichTopic+")->";
				
				List<Double> dlist = traceMap.get(cur);
				if (dlist==null)
					return ;
				
				for (int i=0;i<dlist.size();i++) {
					if (dlist.get(i)<connectThreshold) {
						DictDistribution nextdd = new DictDistribution(cur.topicNumberOfLDARun+1, i);
						curLayerBranch += "("+nextdd.topicNumberOfLDARun+" "+nextdd.whichTopic+")";
						if (!ll.contains(nextdd)) {
							ll.add(nextdd);
							newcount++;
						}						
					}
				}
				curLayerBranch += "] ";
			}
			
			//connection threshold minus
			connectThreshold = connectThreshold-connectThresholdMinus;
			
			count = newcount;
			fullBranch.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	public static void allLayerCompare() throws Exception {
		Hashtable<DictDistribution, List<Double>> newTraceMap = KLUtil_generateTraceMap.readTraceMapFromFile();
		//KLAnalysis_crossLDAruns.printTraceMap(newTraceMap);
		System.out.println(newTraceMap.size());
		
		File file = new File(outputPath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		for (int tn=1; tn<200; tn++) {
		
			System.out.println("=========== CURRENT LAYER IS topicNumberOfLDARun="+ tn +"==========");
			bw.write("=========== CURRENT LAYER IS topicNumberOfLDARun="+ tn +"==========\n");
			
			for (int i=0;i<tn;i++) {
			
				DictDistribution dd = new DictDistribution(tn,i);
				List<Double> list = newTraceMap.get(dd);
				System.out.println("\tcurrent layer topicNumberOfLDARun="+tn+" whichTopic="+i);
				bw.write("\tcurrent layer topicNumberOfLDARun="+tn+" whichTopic="+i+"\n");
			
				for (int j=0;j<list.size();j++) {
					if (list.get(j)<KLAnalysis_KLNet.connectThreshold){
						System.out.println("\t\tconnect whichTopic="+j+" with KLDistance="+list.get(j));
						bw.write("\t\tconnect whichTopic="+j+" with KLDistance="+list.get(j)+"\n");
					}
				}
			}
		}
		bw.close();
	}
	
	private static List<String> reverse(List<String> branch) {
		
		List<String> al = new ArrayList<String>();
		for (int i=branch.size()-1; i>=0; i--)
			al.add(branch.get(i));
		return al;
	}
	
	private static void printAndSaveBranch(List<String> branch, int topicNumberOfLDARun, int whichTopic, double connectThreshold) throws Exception{
		File file = new File(branchPath+"branch "+topicNumberOfLDARun+" "+whichTopic+" "+connectThreshold+".txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				
		for (String s:branch) {
			System.out.println(s);
			bw.write(s+"\n");
		}
		bw.close();
	}
}
