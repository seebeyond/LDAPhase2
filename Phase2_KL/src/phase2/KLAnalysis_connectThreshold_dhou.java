package phase2;

import java.text.DecimalFormat;
import java.util.*;

// pre-set a similar topic number value; calculate double[] threshold for all the thresholds for each two layers.
// then construct the branch
public class KLAnalysis_connectThreshold_dhou {

	private static int similarTopicNum = 2;

	public static void main(String[] args) throws Exception {

		Hashtable<DictDistribution, List<Double>> backTraceMap = KLUtil_generateTraceMap.readBackTraceMapFromFile();
		Hashtable<DictDistribution, List<Double>> traceMap = KLUtil_generateTraceMap.readTraceMapFromFile();

		// thresholdArr[0] is not in use. threshold[1] saves the threshold between layer 1 and 2. [199] saves the threshold 199-200
		//double[] thresholdArr = KLAnalysis_connectThreshold.getThresholdArr(backTraceMap, traceMap, similarTopicNum);
		
		// advanced approach for deciding thresholdArr[]
		//double[] thresholdArr = KLAnalysis_connectThreshold.getThresholdArr(backTraceMap, traceMap);
		
		double[] thresholdArr = KLAnalysis_connectThreshold_dhou.getThresholdArrViaAverge(backTraceMap, traceMap);
		
		KLAnalysis_connectThreshold_dhou.getBranchSaveBranchSaveKeywords(backTraceMap, traceMap, thresholdArr);
	}

	public static void getBranchSaveBranchSaveKeywords(Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap, double[] thresholdArr) throws Exception {
		// get branch 
		List<String> branch = KLAnalysis_connectThreshold_dhou.getFullBranchUpAndDown(Constant.topicNumberOfLDARun, Constant.whichTopic, backTraceMap, traceMap, thresholdArr);
				
		// save branch into local file
		KLAnalysis_KLNet.printAndSaveBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
				
		//get topic keywords for the topics in branch, and then save it into local file	
		KLAnalysis_KLNet.saveAllTopicKeywordsInBranch(branch, Constant.topicNumberOfLDARun, Constant.whichTopic, Constant.fixedconnectThreshold, Constant.connectThresholdMinus);
	}
	public static List<String> getFullBranchUpAndDown(int topicNumberOfLDARun, int whichTopic, Hashtable<DictDistribution, List<Double>> backTraceMap, Hashtable<DictDistribution, List<Double>> traceMap, double[] thresholdArr) throws Exception {
		List<String> branch = new ArrayList<String>();		
		KLAnalysis_connectThreshold_dhou.getFullBranchUp(topicNumberOfLDARun, whichTopic, branch, backTraceMap, thresholdArr);
		branch = KLAnalysis_KLNet.reverse(branch);
		KLAnalysis_connectThreshold_dhou.getFullBranchDown(topicNumberOfLDARun, whichTopic, branch, traceMap, thresholdArr);
		return branch;
	}
	
	public static void getFullBranchUp(int layer, int whichTopic, List<String> branch, Hashtable<DictDistribution, List<Double>> backTraceMap, double[] thresholdArr) throws Exception {
		
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
					if (dlist.get(i)<=thresholdArr[cur.topicNumberOfLDARun-1]) {
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
			
			count = newcount;
			fullBranchUp.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	public static void getFullBranchDown(int layer, int whichTopic, List<String> branch, Hashtable<DictDistribution, List<Double>> traceMap, double[] thresholdArr) throws Exception {
		
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
					if (dlist.get(i)<=thresholdArr[cur.topicNumberOfLDARun]) {
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
			
			count = newcount;
			fullBranch.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	//---------------------------------------------------------------
	
	private static double[] getThresholdArrViaAverge(Hashtable<DictDistribution, List<Double>> backTraceMap,Hashtable<DictDistribution, List<Double>> traceMap) {
		DecimalFormat df = new DecimalFormat("#.0000");
		
		double[] thresholdArr = new double[200];
		for (int layer = 1; layer < 200; layer++) {

			
			int count = 0;
			double total=0;
			for (int topicNum = 0; topicNum < layer; topicNum++) {
				DictDistribution dd = new DictDistribution(layer, topicNum);
				List<Double> dlist = traceMap.get(dd);
				for (double d : dlist) {
					count++;
					total += d;
				}

			}
			thresholdArr[layer] = (total/count)/3;
			
		}
		return thresholdArr;
	}
	
	// get connect threshold values (given a similar topic number) and return it as double[]
	private static double[] getThresholdArr(Hashtable<DictDistribution, List<Double>> backTraceMap,Hashtable<DictDistribution, List<Double>> traceMap, int simTopicNum) {
		DecimalFormat df = new DecimalFormat("#.0000");
		List<Double> klArr = new ArrayList<Double>();
		double[] thresholdArr = new double[200];
		for (int layer = 1; layer < 200; layer++) {

			klArr.clear();
			for (int topicNum = 0; topicNum < layer; topicNum++) {
				DictDistribution dd = new DictDistribution(layer, topicNum);
				List<Double> dlist = traceMap.get(dd);
				for (double d : dlist) {
					klArr.add(d);
				}

			}
			Collections.sort(klArr);
			thresholdArr[layer] = KLAnalysis_connectThreshold_dhou.percentile(klArr, ((double) simTopicNum) / layer);
			System.out.println("layer " + layer + " to " + (layer + 1)+ " percentile "+ df.format(((double) simTopicNum) / layer) + " is "+ df.format(thresholdArr[layer]));
		}
		return thresholdArr;
	}
	// Try 1 similar topic for layers 1-100, and 2 similar topics for layers between 101-150, and 4 topics for layers between 151-200.
	private static double[] getThresholdArr(Hashtable<DictDistribution, List<Double>> backTraceMap,Hashtable<DictDistribution, List<Double>> traceMap){
		DecimalFormat df = new DecimalFormat("#.0000");
		List<Double> klArr = new ArrayList<Double>();
		double[] thresholdArr = new double[200];
		
		for (int layer = 1; layer < 100; layer++) {

			klArr.clear();
			for (int topicNum = 0; topicNum < layer; topicNum++) {
				DictDistribution dd = new DictDistribution(layer, topicNum);
				List<Double> dlist = traceMap.get(dd);
				for (double d : dlist) {
					klArr.add(d);
				}

			}
			Collections.sort(klArr);
			thresholdArr[layer] = KLAnalysis_connectThreshold_dhou.percentile(klArr, ((double) 1) / layer);
			System.out.println("layer " + layer + " to " + (layer + 1)+ " percentile "+ df.format(((double) 1) / layer) + " is "+ df.format(thresholdArr[layer]));
		}
		for (int layer = 100; layer < 150; layer++) {

			klArr.clear();
			for (int topicNum = 0; topicNum < layer; topicNum++) {
				DictDistribution dd = new DictDistribution(layer, topicNum);
				List<Double> dlist = traceMap.get(dd);
				for (double d : dlist) {
					klArr.add(d);
				}

			}
			Collections.sort(klArr);
			thresholdArr[layer] = KLAnalysis_connectThreshold_dhou.percentile(klArr, ((double)2) / layer);
			System.out.println("layer " + layer + " to " + (layer + 1)+ " percentile "+ df.format(((double)2) / layer) + " is "+ df.format(thresholdArr[layer]));
		}
		for (int layer = 150; layer < 200; layer++) {

			klArr.clear();
			for (int topicNum = 0; topicNum < layer; topicNum++) {
				DictDistribution dd = new DictDistribution(layer, topicNum);
				List<Double> dlist = traceMap.get(dd);
				for (double d : dlist) {
					klArr.add(d);
				}

			}
			Collections.sort(klArr);
			thresholdArr[layer] = KLAnalysis_connectThreshold_dhou.percentile(klArr, ((double)4) / layer);
			System.out.println("layer " + layer + " to " + (layer + 1)+ " percentile "+ df.format(((double)4) / layer) + " is "+ df.format(thresholdArr[layer]));
		}
		return thresholdArr;
	}

	// util : calculate excel percentile value
	private static double percentile(List<Double> arr, double percentile_value) {

		if (percentile_value > 1)
			percentile_value = 1;
		double percent = arr.size() * percentile_value;
		int index = (int) percent;

		double d1 = percent - index;
		double d2 = 1 - d1;
		if (d1 == 0)
			return arr.get(index - 1);
		else
			return d1 * arr.get(index - 1) + d2 * arr.get(index);
	}

	// util : calculate excel percentile value
	private static double percentile(double[] arr, double percentile_value) {
		double percent = arr.length * percentile_value;
		int index = (int) percent;
		double d1 = percent - index;
		double d2 = 1 - d1;
		return d1 * arr[index - 1] + d2 * arr[index];
	}

}
