package phase2;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class KLAnalysis_KLNet {
	
	
	
	public static void main(String[] args) throws Exception {
		
		Hashtable<DictDistribution, List<Double>> backTraceMap = KLUtil_generateTraceMap.readBackTraceMapFromFile();
		Hashtable<DictDistribution, List<Double>> traceMap = KLUtil_generateTraceMap.readTraceMapFromFile();		
		
		/* DON'T Run below two functions at the same time */		
		// this is for auto select the best connection parameter.
		//KLAnalysis_KLNet.autoScanConnectionThreshold(backTraceMap, traceMap);		
		// this is the core function of this class.
		//KLAnalysis_KLNet.getBranchSaveBranchSaveKeywords(backTraceMap, traceMap);
		
		// DHou's requirement. Probably not use it anymore
		//KLAnalysis_KLNet.getAllKLDistance(backTraceMap, traceMap);
		KLAnalysis_KLNet.getAllMinKLDistance(backTraceMap, traceMap);
		
	}
	
	
	// --------------------------- CORE Procedure of this class >>>>> start ------------------------------------------------
	public static void getBranchSaveBranchSaveKeywords(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		// get branch 
		List<String> branch = KLAnalysis_KLNet.getFullBranchUpAndDown(Constant.topicNumberOfLDARun, Constant.whichTopic, backTraceMap, traceMap);
				
		// save branch into local file
		KLAnalysis_KLNet.printAndSaveBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
				
		//get topic keywords for the topics in branch, and then save it into local file	
		KLAnalysis_KLNet.saveAllTopicKeywordsInBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
	}
	
	public static List<String> getFullBranchUpAndDown(int topicNumberOfLDARun, int whichTopic, Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		List<String> branch = new ArrayList<String>();		
		KLAnalysis_KLNet.getFullBranchUp(topicNumberOfLDARun, whichTopic, branch, backTraceMap);
		branch = KLAnalysis_KLNet.reverse(branch);
		KLAnalysis_KLNet.getFullBranchDown(topicNumberOfLDARun, whichTopic, branch, traceMap);
		return branch;
	}
	// --------------------------- CORE Procedure of this class <<<<< end ------------------------------------------------
	
	// --------------------------- Util - auto find the best branch >>>>> start -------------------------------------------------------------------
	public static void autoScanConnectionThreshold(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Constant.autoScanConnection+"autoScanConnection"+Constant.topicNumberOfLDARun+".txt")));
		
		int[] topicNumberArr = {26,2,33,17,20,21};
		
		for (int topicNum:topicNumberArr) {
			
			Constant.whichTopic = topicNum;
			System.out.println("============== This is topic "+ topicNum +" ================");
			bw.write("============== This is topic "+ topicNum +" ================"+"\n");
			
			DecimalFormat df=new DecimalFormat("#.000"); 
			
			//for (double d1=2.0; d1<=3.5; d1+=0.2) {
			for (double d1=1.0; d1<=4.0; d1+=0.1) {
			
				//for (double d2=0; d2<0.08; d2+=0.002) {
				for (double d2=0; d2<=0; d2+=1){
					Constant.connectThreshold = d1;
					Constant.connectThresholdMinus = d2;
					StringBuilder info = new StringBuilder();
				
					List<String> branch = KLAnalysis_KLNet.getFullBranchUpAndDown(Constant.topicNumberOfLDARun, Constant.whichTopic, backTraceMap, traceMap);
//					if (branch.get(branch.size()-1).contains("200") == false)
//						continue;
				
					info.append(df.format(d1)+" "+df.format(d2)+": ");
					//info.append(d1+" "+d2+": ");
				
					for (String s: branch) {
						StringBuilder num = new StringBuilder();
						for (int i=0;i<s.length();i++) {
							if (s.charAt(i)!=' ')
								num.append(s.charAt(i));
							else 
								break;
						}
						info.append(num.toString()+"\t");
					}
				System.out.println(info);
				bw.write(info+"\n");
				}
			
			}
		}
		bw.close();
	}
	// --------------------------- Util - auto find the best branch <<<<< end -------------------------------------------------------------------
	
	
	//-----------------"save all topic keywords in a specific branch" >>>>> START ----------------------
	public static void saveAllTopicKeywordsInBranch(List<String> branch, int topicNumberOfLDARun, int whichTopic, double connectThreshold, double thresholdMinus) throws Exception{
		
		// calculat a line number in alltopicKeywords file		
		List<Integer> lines = KLAnalysis_KLNet.getSeq(branch);
		// save lines into stack would be great for further process
		Stack<Integer> slines = new Stack<Integer>();
		for (int i=lines.size()-1; i>=0;i--)
			slines.push(lines.get(i));
		
		File file = new File(Constant.branchPath+"branch "+topicNumberOfLDARun+" "+whichTopic+" "+connectThreshold+" "+thresholdMinus+" topicKeyWords.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		File rfile = new File(Constant.allTopicKeywordsPath);
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
	public static List<Integer> getSeq(List<String> branch) {
		
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
	public static int getSeqNum(String str) {
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
			String curLayerBranch = count + " ";
			
			while (count>0) {
				
				DictDistribution cur = ll.pop();
				count--;
				curLayerBranch +=  "[("+cur.topicNumberOfLDARun+" "+cur.whichTopic+")<-";
				
				List<Double> dlist = backTraceMap.get(cur);
				if (dlist==null)
					return ;
				
				for (int i=0;i<dlist.size();i++) {
					if (dlist.get(i)<Constant.connectThreshold) {
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
			
			//connection threshold add
			Constant.connectThreshold = Constant.connectThreshold+Constant.connectThresholdMinus;
			
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
			String curLayerBranch = count+ " ";
			
			while (count>0) {
				
				DictDistribution cur = ll.pop();
				count--;
				curLayerBranch += "[("+cur.topicNumberOfLDARun+" "+cur.whichTopic+")->";
				
				List<Double> dlist = traceMap.get(cur);
				if (dlist==null)
					return ;
				
				for (int i=0;i<dlist.size();i++) {
					if (dlist.get(i)<Constant.connectThreshold) {
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
			Constant.connectThreshold = Constant.connectThreshold-Constant.connectThresholdMinus;
			
			count = newcount;
			fullBranch.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	// util method
	public static List<String> reverse(List<String> branch) {
		
		List<String> al = new ArrayList<String>();
		for (int i=branch.size()-1; i>=0; i--)
			al.add(branch.get(i));
		return al;
	}
	// util method
	public static void printAndSaveBranch(List<String> branch, int topicNumberOfLDARun, int whichTopic, double connectThreshold, double thresholdMinus) throws Exception{
		File file = new File(Constant.branchPath+"branch "+topicNumberOfLDARun+" "+whichTopic+" "+connectThreshold+" "+thresholdMinus+".txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				
		for (String s:branch) {
			//System.out.println(s);
			bw.write(s+"\n");
		}
		bw.close();
	}
	
	// --------------------------------- Util of DHou >>>>> start  ------------------------------------------------
	// util method. DHou wants to see all topics KL distances between each adjacent layer 
	public static void getAllKLDistance(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		String outputpath = "C:/Users/nancy.quan/Desktop/lda/mid_data/allkl.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputpath)));
		for (int layer=1;layer<200; layer++) {
			bw.write("layer "+layer+" to layer "+(layer+1)+" average KL distance is : ");
			System.out.println("layer "+layer+" to layer "+(layer+1));
			int count = 0;
			double total=0;
			for (int topicNum = 0; topicNum<layer; topicNum++) {
				DictDistribution dd = new DictDistribution(layer, topicNum);
				List<Double> dlist = traceMap.get(dd);
				for (double d:dlist) {
					count++;
					total += d;
				}
					
			}
			bw.write(String.valueOf(total/count));
			bw.write("\n");
		}
		bw.close();
	}
	public static void getAllMinKLDistance(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		DecimalFormat df = new DecimalFormat("#.0000");
		String outputpath = "C:/Users/nancy.quan/Desktop/lda/mid_data/allminkl.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputpath)));
		for (int layer=1;layer<200; layer++) {
			bw.write("layer "+layer+" to layer "+(layer+1) +" ");
			List<Double> minlist = new ArrayList<Double>();
			for (int topicNum = 0; topicNum<layer; topicNum++) {
				DictDistribution dd = new DictDistribution(layer, topicNum);
				List<Double> dlist = traceMap.get(dd);
				Collections.sort(dlist);
				minlist.add(dlist.get(0));
			}
			Collections.sort(minlist);
			for (double d:minlist)
				bw.write(df.format(d)+" ");
			bw.write("\n");
		}
		bw.close();
	}
	
	
	// --------------------------------- Util of DHou <<<<< end  ------------------------------------------------
}
