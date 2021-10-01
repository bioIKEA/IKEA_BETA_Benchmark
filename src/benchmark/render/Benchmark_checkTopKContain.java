package benchmark.render;

import java.io.File;
import java.util.HashSet;
import java.util.Map.Entry;

import jsat.classifiers.neuralnetwork.regularizers.Max2NormRegularizer;

public class Benchmark_checkTopKContain {
	public static String dataDir="data_sample";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		HashSet<String> top_10_drugs=getFiles(dataDir+"/output/datasets/experiment/top10/drugClass");
		HashSet<String> top_10_targets=getFiles(dataDir+"/output/datasets/experiment/top10/targetClass");
		
		HashSet<String> top_5_drugs=getFiles(dataDir+"/output/datasets/experiment/drugClass");
		HashSet<String> top_5_targets=getFiles(dataDir+"/output/datasets/experiment/targetClass");
		
		System.out.println("top_5_drugs: "+top_5_drugs.size());
		System.out.println("top_5_targets: "+top_5_targets.size());
		System.out.println("top_10_drugs: "+top_10_drugs.size());
		System.out.println("top_10_targets: "+top_10_targets.size());
		top_5_drugs.removeAll(top_10_drugs);
		top_5_targets.removeAll(top_10_targets);
		
		System.out.println("top_5_drugs: "+top_5_drugs.size());
		System.out.println("top_5_targets: "+top_5_targets.size());
	}
	
	
	public static HashSet<String> getFiles(String folder){
		HashSet<String> set=new HashSet<>();
		for(File dir:new File(folder).listFiles()) {
			for(File file:dir.listFiles()) {
				set.add(file.getName());
			}
		}
		return set;
	}
	

}
