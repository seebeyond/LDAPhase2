package phase2;

import java.util.Hashtable;
import java.util.List;

public class KLAnalysis_KLNet {
	
	public static double connectThreshold=1E-5;
	
	public static void main(String[] args) throws Exception {
		Hashtable<DictDistribution, List<Double>> newTraceMap = KLAnalysis_crossLDAruns.readTraceMapFromFile();
		//KLAnalysis_crossLDAruns.printTraceMap(newTraceMap);
		System.out.println(newTraceMap.size());
		
		DictDistribution dd1 = new DictDistribution(10,0);
		List<Double> list = newTraceMap.get(dd1);
		for (double d:list) {
			if (d>KLAnalysis_KLNet.connectThreshold)
				System.out.println(d);
		}
	}

}
