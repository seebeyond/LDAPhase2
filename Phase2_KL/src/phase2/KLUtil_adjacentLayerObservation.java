package phase2;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.*;

public class KLUtil_adjacentLayerObservation {

	public static void main(String[] args) throws Exception {
		
		int layer = 49;
		int whichTopic = 4;
		
		//KLUtil_adjacentLayerObservation.lookUp(layer, whichTopic);
		
		KLUtil_adjacentLayerObservation.lookDown(layer,whichTopic);


		
	}
	
	private static void lookUp(int layer, int whichTopic) throws Exception {
		Hashtable<DictDistribution, List<Double>> backTraceMap = KLUtil_generateTraceMap.readBackTraceMapFromFile();
		DecimalFormat df = new DecimalFormat("#.0000");
		List<Double> dlist = backTraceMap.get(new DictDistribution(layer, whichTopic));
		List<Node> nodes = new ArrayList<Node>();
		for (int i=0;i<dlist.size();i++) {
			nodes.add(new Node(i, dlist.get(i)));
		}
		Collections.sort(nodes, new Comparator<Node>(){
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.val-n2.val <0)
					return -1;
				else if (n1.val == n2.val)
					return 0;
				else
					return 1;
			}
		});
		for (Node n:nodes)
			System.out.println("("+(layer-1)+" "+n.topicNum +") \t"+df.format(n.val));
	}
	
	private static void lookDown(int layer, int whichTopic) throws Exception {
		Hashtable<DictDistribution, List<Double>> traceMap = KLUtil_generateTraceMap.readTraceMapFromFile();
		DecimalFormat df = new DecimalFormat("#.0000");
		List<Double> dlist = traceMap.get(new DictDistribution(layer, whichTopic));
		List<Node> nodes = new ArrayList<Node>();
		for (int i=0;i<dlist.size();i++) {
			nodes.add(new Node(i, dlist.get(i)));
		}
		Collections.sort(nodes, new Comparator<Node>(){
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.val-n2.val <0)
					return -1;
				else if (n1.val == n2.val)
					return 0;
				else
					return 1;
			}
		});
		for (Node n:nodes)
			System.out.println("("+(layer+1)+" "+n.topicNum +") \t"+df.format(n.val));
	}

}
