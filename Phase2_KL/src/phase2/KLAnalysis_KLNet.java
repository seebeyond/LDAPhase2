package phase2;

import java.util.Hashtable;
import java.util.List;
import java.io.*;

public class KLAnalysis_KLNet {
	
	public static double connectThreshold=1E-4;
	public static String outputPath = "C:/Users/zouc/Desktop/lda/mid_data/layerCompare.txt";
	
	public static void main(String[] args) throws Exception {
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
					if (list.get(j)>KLAnalysis_KLNet.connectThreshold){
						System.out.println("\t\tconnect whichTopic="+j+" with KLDistance="+list.get(j));
						bw.write("\t\tconnect whichTopic="+j+" with KLDistance="+list.get(j)+"\n");
					}
				}
			}
		}
		bw.close();
	}

}
