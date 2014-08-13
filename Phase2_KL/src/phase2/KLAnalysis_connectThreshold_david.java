package phase2;

import java.text.DecimalFormat;
import java.util.*;

public class KLAnalysis_connectThreshold_david {

	private static int similarTopicNum = 2;
	private static int topN = 2;

	public static void main(String[] args) throws Exception {

		Hashtable<DictDistribution, List<Double>> backTraceMap = KLUtil_generateTraceMap.readBackTraceMapFromFile();
		Hashtable<DictDistribution, List<Double>> traceMap = KLUtil_generateTraceMap.readTraceMapFromFile();
		
		KLAnalysis_connectThreshold_david.getBranchSaveBranchSaveKeywords(backTraceMap, traceMap);
	}

	public static void getBranchSaveBranchSaveKeywords(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		// get branch 
		List<String> branch = KLAnalysis_connectThreshold_david.getFullBranchUpAndDown(Constant.topicNumberOfLDARun, Constant.whichTopic, backTraceMap, traceMap);
				
		// save branch into local file
		KLAnalysis_KLNet.printAndSaveBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
				
		//get topic keywords for the topics in branch, and then save it into local file	
		KLAnalysis_KLNet.saveAllTopicKeywordsInBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
	}
	public static List<String> getFullBranchUpAndDown(int topicNumberOfLDARun, int whichTopic, Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		List<String> branch = new ArrayList<String>();		
		KLAnalysis_connectThreshold_david.getFullBranchUp(topicNumberOfLDARun, whichTopic, branch,traceMap, backTraceMap);
		branch = KLAnalysis_KLNet.reverse(branch);
		KLAnalysis_connectThreshold_david.getFullBranchDown(topicNumberOfLDARun, whichTopic, branch, traceMap, backTraceMap);
		return branch;
	}
	
	public static void getFullBranchUp(int layer, int whichTopic, List<String> branch,Hashtable<DictDistribution, List<Double>> traceMap, Hashtable<DictDistribution, List<Double>> backTraceMap) throws Exception {
		
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
				
				List<Node> nodes = new ArrayList<Node>();
				for (int i=0; i<dlist.size(); i++) {
					Node anode = new Node(i, dlist.get(i));
					nodes.add(anode);
				}
				Collections.sort(nodes, new Comparator<Node>(){
					@Override 
					public int compare(Node n1, Node n2){
						if (n1.val-n2.val <0 )
							return -1;
						else if (n1.val == n2.val)
							return 0;
						else
							return 1;
					}
				});
				
				for (int i=0;i<topN && i<nodes.size(); i++) {
					DictDistribution nextdd = new DictDistribution(cur.topicNumberOfLDARun-1, nodes.get(i).topicNum);
					List<Double> uplist = traceMap.get(nextdd);
					
					int minUpWhichTopic = 0;
					double minUpKLVal = uplist.get(0);
					for (int j=1;j<uplist.size();j++) {
						if (uplist.get(j)<minUpKLVal) {
							minUpKLVal = uplist.get(j);
							minUpWhichTopic = j;
						}
					}
					if (minUpWhichTopic == cur.whichTopic) {
						curLayerBranch += "("+nextdd.topicNumberOfLDARun+" "+nextdd.whichTopic+")";
						if (!ll.contains(nextdd)) {
							ll.add(nextdd);
							newcount++;
						}
					}
				}
								
				curLayerBranch += "] ";				
			}
			
			count = newcount;
			fullBranchUp.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	public static void getFullBranchDown(int layer, int whichTopic, List<String> branch, Hashtable<DictDistribution, List<Double>> traceMap, Hashtable<DictDistribution, List<Double>> backTraceMap) throws Exception {
		
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
				
				List<Node> nodes = new ArrayList<Node>();
				for (int i=0; i<dlist.size(); i++) {
					Node anode = new Node(i, dlist.get(i));
					nodes.add(anode);
				}
				Collections.sort(nodes, new Comparator<Node>(){
					@Override 
					public int compare(Node n1, Node n2){
						if (n1.val-n2.val <0 )
							return -1;
						else if (n1.val == n2.val)
							return 0;
						else
							return 1;
					}
				});
				
				for (int i=0;i<topN && i<nodes.size(); i++) {
					DictDistribution nextdd = new DictDistribution(cur.topicNumberOfLDARun+1, nodes.get(i).topicNum);
					List<Double> uplist = backTraceMap.get(nextdd);
					
					int minUpWhichTopic = 0;
					double minUpKLVal = uplist.get(0);
					for (int j=1;j<uplist.size();j++) {
						if (uplist.get(j)<minUpKLVal) {
							minUpKLVal = uplist.get(j);
							minUpWhichTopic = j;
						}
					}
					if (minUpWhichTopic == cur.whichTopic) {
						curLayerBranch += "("+nextdd.topicNumberOfLDARun+" "+nextdd.whichTopic+")";
						if (!ll.contains(nextdd)) {
							ll.add(nextdd);
							newcount++;
						}
					}
				}
				
				curLayerBranch += "] ";
			}
			
			count = newcount;
			fullBranch.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
}
class Node{
	int topicNum;
	double val;
	public Node(int _topicNum, double _val){
		topicNum = _topicNum;
		val = _val;
	}
}
