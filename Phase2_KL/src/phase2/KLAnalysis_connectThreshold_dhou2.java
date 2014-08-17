package phase2;

import java.util.*;

/* always take top 5 KL values for the new layers */
public class KLAnalysis_connectThreshold_dhou2 {
	

	private static int topN = 5;

	public static void main(String[] args) throws Exception {

		Hashtable<DictDistribution, List<Double>> backTraceMap = KLUtil_generateTraceMap.readBackTraceMapFromFile();
		Hashtable<DictDistribution, List<Double>> traceMap = KLUtil_generateTraceMap.readTraceMapFromFile();
		
		KLAnalysis_connectThreshold_dhou2.getBranchSaveBranchSaveKeywords(backTraceMap, traceMap);
	}

	public static void getBranchSaveBranchSaveKeywords(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		// get branch 
		List<String> branch = KLAnalysis_connectThreshold_dhou2.getFullBranchUpAndDown(Constant.topicNumberOfLDARun, Constant.whichTopic, backTraceMap, traceMap);
				
		// save branch into local file
		KLAnalysis_KLNet.printAndSaveBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
				
		//get topic keywords for the topics in branch, and then save it into local file	
		KLAnalysis_KLNet.saveAllTopicKeywordsInBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
	}
	public static List<String> getFullBranchUpAndDown(int topicNumberOfLDARun, int whichTopic, Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap) throws Exception {
		List<String> branch = new ArrayList<String>();		
		KLAnalysis_connectThreshold_dhou2.getFullBranchUp(topicNumberOfLDARun, whichTopic, branch,traceMap, backTraceMap);
		branch = KLAnalysis_KLNet.reverse(branch);
		KLAnalysis_connectThreshold_dhou2.getFullBranchDown(topicNumberOfLDARun, whichTopic, branch, traceMap, backTraceMap);
		return branch;
	}
	
	public static void getFullBranchUp(int layer, int whichTopic, List<String> branch,Hashtable<DictDistribution, List<Double>> traceMap, Hashtable<DictDistribution, List<Double>> backTraceMap) throws Exception {
		
		DictDistribution dd = new DictDistribution(layer, whichTopic);
		LinkedList<DictDistribution> ll= new LinkedList<DictDistribution>();
		ll.add(dd);
		int count = 1;
		
		List<Node> topNodes = new ArrayList<Node>();
		Hashtable<DictDistribution, List<Node>> ht = new Hashtable<DictDistribution, List<Node>>();
		
		while (!ll.isEmpty()) {
			
			int newcount=0;
			String curLayerBranch = count + " ";
			topNodes.clear();
			ht.clear();
			
			while (count>0) {
				
				DictDistribution cur = ll.pop();
				count--;
				
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
				
				List<Node> topNodesForCur = new ArrayList<Node>();
				for (int i=0;i<topN && i<nodes.size(); i++) {
					topNodesForCur.add(nodes.get(i));
					if (topNodes.contains(nodes.get(i))==false) {
						topNodes.add(nodes.get(i));						
					}
				}
				ht.put(cur, topNodesForCur);		
			}
			
			Collections.sort(topNodes, new Comparator<Node>(){
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
			List<Node> leftTopNodes = new ArrayList<Node>();
			for (int i=0;i<topN&&i<topNodes.size(); i++)
				leftTopNodes.add(topNodes.get(i));
			
			Iterator<DictDistribution> iter = ht.keySet().iterator();
			while (iter.hasNext()) {
				DictDistribution dd1 = iter.next();
				curLayerBranch += "[("+dd1.topicNumberOfLDARun+" "+dd1.whichTopic+")<-";
				List<Node> ln = ht.get(dd1);
				for (Node n:leftTopNodes) {
					if (ln.contains(n)) {
						curLayerBranch += "("+(dd1.topicNumberOfLDARun-1)+" "+n.topicNum+")";
						if (!ll.contains(new DictDistribution((dd1.topicNumberOfLDARun-1), n.topicNum))) {
							ll.add(new DictDistribution((dd1.topicNumberOfLDARun-1), n.topicNum));
							newcount++;
						}
					}
				}
				curLayerBranch += "] ";
			}
			
			count = newcount;
			branch.add(curLayerBranch);
		}
	}
	
	public static void getFullBranchDown(int layer, int whichTopic, List<String> branch, Hashtable<DictDistribution, List<Double>> traceMap, Hashtable<DictDistribution, List<Double>> backTraceMap) throws Exception {
		
		DictDistribution dd = new DictDistribution(layer, whichTopic);
		LinkedList<DictDistribution> ll= new LinkedList<DictDistribution>();
		ll.add(dd);
		int count = 1;
		
		List<Node> topNodes = new ArrayList<Node>();
		Hashtable<DictDistribution, List<Node>> ht = new Hashtable<DictDistribution, List<Node>>();
		
		while (!ll.isEmpty()) {
			
			int newcount=0;
			String curLayerBranch = count+ " ";
			topNodes.clear();
			ht.clear();
			
			while (count>0) {
				
				DictDistribution cur = ll.pop();
				count--;
				
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
				
				
				List<Node> topNodesForCur = new ArrayList<Node>();
				for (int i=0;i<topN && i<nodes.size(); i++) {
					topNodesForCur.add(nodes.get(i));
					if (topNodes.contains(nodes.get(i))==false) {
						topNodes.add(nodes.get(i));						
					}

				}
				ht.put(cur, topNodesForCur);
			}
			
			Collections.sort(topNodes, new Comparator<Node>(){
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
			List<Node> leftTopNodes = new ArrayList<Node>();
			for (int i=0;i<topN&&i<topNodes.size(); i++)
				leftTopNodes.add(topNodes.get(i));
			
			Iterator<DictDistribution> iter = ht.keySet().iterator();
			while (iter.hasNext()) {
				DictDistribution dd1 = iter.next();
				if (dd1.topicNumberOfLDARun==41)
					System.out.println();
				curLayerBranch += "[("+dd1.topicNumberOfLDARun+" "+dd1.whichTopic+")->";
				List<Node> ln = ht.get(dd1);
				for (Node n:leftTopNodes) {
					if (ln.contains(n)) {
						curLayerBranch += "("+(dd1.topicNumberOfLDARun+1)+" "+n.topicNum+")";
						if (!ll.contains(new DictDistribution((dd1.topicNumberOfLDARun+1), n.topicNum))) {
							ll.add(new DictDistribution((dd1.topicNumberOfLDARun+1), n.topicNum));
							newcount++;
						}
					}
				}
				curLayerBranch += "] ";
			}
			
			count = newcount;
			branch.add(curLayerBranch);
		}
	}
}

