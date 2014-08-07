package phase2;

import java.util.*;
import java.io.*;

public class KLAnalysis_KLNet {
	
	public static double connectThreshold=2.8;
	public static String outputPath = "C:/Users/zouc/Desktop/lda/mid_data/layerCompare.txt";
	
	public static void main(String[] args) throws Exception {
		
		int topicNumberOfLDARun = 80;
		int whichTopic = 26;
		
		List<String> branch = new ArrayList<String>();		
		KLAnalysis_KLNet.getFullBranchUp(topicNumberOfLDARun, whichTopic, branch);
		branch = KLAnalysis_KLNet.delFirstAndReverse(branch);
		KLAnalysis_KLNet.getFullBranchDown(topicNumberOfLDARun, whichTopic, branch);
		
		for (String s:branch)
			System.out.println(s);
	}
	
	public static void getFullBranchUp(int layer, int whichTopic, List<String> branch) throws Exception {
		Hashtable<DictDistribution, List<Double>> backTraceMap = KLAnalysis_crossLDAruns.readBackTraceMapFromFile();
		//KLAnalysis_crossLDAruns.printTraceMap(newTraceMap);
		//System.out.println(backTraceMap.size());
		
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
				curLayerBranch += "("+cur.topicNumberOfLDARun+" "+cur.whichTopic+") ";
				
				List<Double> dlist = backTraceMap.get(cur);
				if (dlist==null)
					return ;
				
				for (int i=0;i<dlist.size();i++) {
					if (dlist.get(i)<connectThreshold) {
						DictDistribution nextdd = new DictDistribution(cur.topicNumberOfLDARun-1, i);
						if (!ll.contains(nextdd)) {
							ll.add(nextdd);
							newcount++;
						}						
					}
				}
				
				
			}
			
			count = newcount;
			fullBranchUp.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	public static void getFullBranchDown(int layer, int whichTopic, List<String> branch) throws Exception {
		Hashtable<DictDistribution, List<Double>> newTraceMap = KLAnalysis_crossLDAruns.readTraceMapFromFile();
		//KLAnalysis_crossLDAruns.printTraceMap(newTraceMap);
		//System.out.println(newTraceMap.size());
		
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
				curLayerBranch += "("+cur.topicNumberOfLDARun+" "+cur.whichTopic+") ";
				
				List<Double> dlist = newTraceMap.get(cur);
				if (dlist==null)
					return ;
				
				for (int i=0;i<dlist.size();i++) {
					if (dlist.get(i)<connectThreshold) {
						DictDistribution nextdd = new DictDistribution(cur.topicNumberOfLDARun+1, i);
						if (!ll.contains(nextdd)) {
							ll.add(nextdd);
							newcount++;
						}						
					}
				}
			}
			
			count = newcount;
			fullBranch.add(curLayerBranch);
			branch.add(curLayerBranch);
		}
	}
	
	public static void allLayerCompare() throws Exception {
		Hashtable<DictDistribution, List<Double>> newTraceMap = KLAnalysis_crossLDAruns.readTraceMapFromFile();
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
	
	private static List<String> delFirstAndReverse(List<String> branch) {
		branch.remove(0);
		List<String> al = new ArrayList<String>();
		for (int i=branch.size()-1; i>=0; i--)
			al.add(branch.get(i));
		return al;
	}
}
