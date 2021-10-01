package benchmark.render.external;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import benchmark.render.Benchmark_checker;
import benchmark.render.internal.NegativeGenerator_drugClass;
import benchmark.render.internal.NegativeGenerator_targetClass;
import benchmark.render.internal.NegativeSetBean;
import java_cup.internal_error;

public class External_Testgenerator {
	public static String dataDir="data_sample";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static ArrayList<Map.Entry<String,HashSet<String>>>  sortMap( HashMap<String, HashSet<String>> map) {
		ArrayList<Map.Entry<String,HashSet<String>>> list_2 = new ArrayList<>(map.entrySet());
	       Collections.sort(list_2, new Comparator<Map.Entry<String,HashSet<String>>>() {
	           public int compare(Map.Entry<String,HashSet<String>> o1, Map.Entry<String,HashSet<String>> o2) {
	               return Double.valueOf(o2.getValue().size()).compareTo(Double.valueOf(o1.getValue().size()));//升序，前边加负号变为降序
	           }
	       });
	       
	     return list_2;
	}
	
	public static HashMap<String,String> readLabel() throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/input/done/diseasome_dump.nt")));
		String line=null;
		HashMap<String,String> lablesHashMap=new HashMap<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://www.w3.org/2000/01/rdf-schema#label>")) {
					String valueString=o.substring(o.indexOf("\"")+1,o.lastIndexOf("\""));
					lablesHashMap.put(s, valueString);
				} 
			}	
		}
		br.close();
		return lablesHashMap;
	}
	
	
	public static HashMap<String, HashSet<String>> diseasomeTargetToDrugBankTarget() throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/target_diseasome_drugbank.nq")));
		String line=null;
		HashMap<String, HashSet<String>> mappingHashMap=new HashMap<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://www.w3.org/2002/07/owl#sameAs>")) {
					if(mappingHashMap.containsKey(s)) {
						mappingHashMap.get(s).add(o);
					}else {
						HashSet<String> set=new HashSet<>();
						set.add(o);
						mappingHashMap.put(s, set);
					}
				} 
			}	
		}
		br.close();
		return mappingHashMap;
	}
	
	
	public static HashMap<String, HashSet<String>> diseaseToGenes_pharmgkb() throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/association_pharmgkb.nq")));
		String line=null;
		HashMap<String, HashSet<String>> mappingHashMap=new HashMap<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:disease-gene-Association>")) {
					if(mappingHashMap.containsKey(s)) {
						mappingHashMap.get(s).add(o);
					}else {
						HashSet<String> set=new HashSet<>();
						set.add(o);
						mappingHashMap.put(s, set);
					}
				} 
			}	
		}
		br.close();
		return mappingHashMap;
	}
	
	
		
	public static HashMap<String, HashSet<String>> diseaseToGenes_diseasome() throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/association_diseasome.nq")));
		String line=null;
		HashMap<String, HashSet<String>> mappingHashMap=new HashMap<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease-target>")) {
					if(mappingHashMap.containsKey(s)) {
						mappingHashMap.get(s).add(o);
					}else {
						HashSet<String> set=new HashSet<>();
						set.add(o);
						mappingHashMap.put(s, set);
					}
				} 
			}	
		}
		br.close();
		return mappingHashMap;
	} 
	
	
	
	public static void writeDataToFolders_disease(String outDir, HashSet<String> triples,  
			ArrayList<Map.Entry<String,HashSet<String>>>  class_testTriples, HashMap<String, String> labels, HashMap<String, HashSet<String>> disease_target,
			int topK)
			throws IOException {
		
		HashSet<String> diseases=new HashSet<>();
		
		for(Map.Entry<String,HashSet<String>> entry:class_testTriples) {
			if(diseases.size()>topK-1) {
				break;
			}
			String name=labels.get(entry.getKey());
			HashSet<String> similar_targets= disease_target.get(entry.getKey());
			HashSet<String> positive_triples= entry.getValue();
			
			HashSet<String> triples_1=new HashSet<>();
			
			for (String triple:triples) {
				if(!positive_triples.contains(triple)){
					triples_1.add(triple);
				}
			}
			
			HashSet<String> positive_train=triples_1;
			HashSet<String> positive_test=entry.getValue();
			
			boolean repeat=generateNegative_disease(name, similar_targets, positive_train, positive_test, 1.0,outDir,topK);
			System.out.println("@@@ diseases: "+diseases.size());
			if(!repeat) {
				diseases.add(name);
			}
		}
	}
	
	
	
	public static void writeDataToFolders_clinicCT(String outDir, HashSet<String> triples,  
			ArrayList<Map.Entry<String,HashSet<String>>>  class_testTriples, HashMap<String, HashSet<String>> disease_target)
			throws IOException {
		
		HashSet<String> diseases=new HashSet<>();
		
		for(Map.Entry<String,HashSet<String>> entry:class_testTriples) {
			HashSet<String> similar_targets= disease_target.get(entry.getKey());
			HashSet<String> positive_triples= entry.getValue();
			
			HashSet<String> triples_1=new HashSet<>();
			
			for (String triple:triples) {
				if(!positive_triples.contains(triple)){
					triples_1.add(triple);
				}
			}
			
			HashSet<String> positive_train=triples_1;
			HashSet<String> positive_test=entry.getValue();
			
			boolean repeat=generateNegative_disease(entry.getKey(), similar_targets, positive_train, positive_test, 1.0,outDir, 10);
			System.out.println("@@@ diseases: "+diseases.size());
			System.err.println(entry.getKey()+" -> "+positive_test.size());
			if(!repeat) {
				diseases.add(entry.getKey());
			}
		}
			
	}
	public static void writeDataToFolders_disease(String outDir, HashSet<String> triples,  
			HashMap<String,HashSet<String>> class_testTriples, HashMap<String, String> labels, HashMap<String, HashSet<String>> disease_target)
			throws IOException {
		
		for(Entry<String,HashSet<String>> entry:class_testTriples.entrySet()) {
			String name=labels.get(entry.getKey());
			HashSet<String> similar_targets= disease_target.get(entry.getKey());
			HashSet<String> positive_triples= class_testTriples.get(entry.getKey());
			
			HashSet<String> triples_1=new HashSet<>();
			
			for (String triple:triples) {
				if(!positive_triples.contains(triple)){
					triples_1.add(triple);
				}
			}
			
			HashSet<String> positive_train=triples_1;
			HashSet<String> positive_test=entry.getValue();
			
			generateNegative_disease(name, similar_targets, positive_train, positive_test, 1.0,outDir);
		}
			
	}
	
	
	public static Boolean generateNegative_disease(String class_name,
			HashSet<String> similar_drugs,HashSet<String> positive_train,HashSet<String> positive_test, Double negative_ratio,
	String outDir, int topK) throws IOException {
		
		NegativeGenerator_disease g=new NegativeGenerator_disease(
				positive_train,
				positive_test);
		NegativeSetBean bean_tt=g.generate(1024, negative_ratio,"tt", similar_drugs,positive_train, positive_test );
		NegativeSetBean bean_trt=g.generate(1024, negative_ratio,"trt", similar_drugs,positive_train, positive_test );
		NegativeSetBean bean_te=g.generate(1024, negative_ratio,"te", similar_drugs,positive_train, positive_test );
		NegativeSetBean bean_tre=g.generate(1024, negative_ratio,"tre", similar_drugs,positive_train, positive_test );
		
		
		String name="";
		
		name=class_name.replaceAll(" ", "_");
		
		writeToFile( outDir,  name+"_tt", positive_train,positive_test, bean_tt.getCandidateSet());
		writeToFile( outDir,  name+"_trt", positive_train,positive_test, bean_trt.getCandidateSet());
		writeToFile( outDir,  name+"_te", positive_train,positive_test, bean_te.getCandidateSet());
		writeToFile( outDir,  name+"_tre", positive_train,positive_test, bean_tre.getCandidateSet());
		
		String type_tt = name+"_tt";  
		String type_trt = name+"_trt";
		String type_te = name+"_te";
		String type_tre = name+"_tre";

		HashSet<String> drugs = getDrugs();
		HashSet<String> targets = getTargets();
		String file1 = outDir + "/train" +"_"+type_tt +".nt";
		String file2 = outDir + "/test" +"_"+type_tt +".nt";
		String file3 = outDir + "/train" +"_"+type_trt +".nt";
		String file4 = outDir + "/test" +"_"+type_trt +".nt";
		String file5 = outDir + "/train" +"_"+type_te +".nt";
		String file6 = outDir + "/test" +"_"+type_te +".nt";
		String file7 = outDir + "/train" +"_"+type_tre +".nt";
		String file8 = outDir + "/test" +"_"+type_tre +".nt";

		Boolean check_tt_train = Benchmark_checker.check(file1, drugs, targets);
		Boolean check_tt_test = Benchmark_checker.check(file2, drugs, targets);

		Boolean check_trt_train = Benchmark_checker.check(file3, drugs, targets);
		Boolean check_trt_test = Benchmark_checker.check(file4, drugs, targets);

		Boolean check_te_train = Benchmark_checker.check(file5, drugs, targets);
		Boolean check_te_test = Benchmark_checker.check(file6, drugs, targets);

		Boolean check_tre_train = Benchmark_checker.check(file7, drugs, targets);
		Boolean check_tre_test = Benchmark_checker.check(file8, drugs, targets);

		boolean repeat = false;
		if (check_tt_train || check_tt_test || check_trt_train || check_trt_test || check_te_train || check_te_test
				|| check_tre_train || check_tre_test) {
			
				new File(file1).deleteOnExit();
				new File(file2).deleteOnExit();
				new File(file3).deleteOnExit();
				new File(file4).deleteOnExit();
				new File(file5).deleteOnExit();
				new File(file6).deleteOnExit();
				new File(file7).deleteOnExit();
				new File(file8).deleteOnExit();	
			
			repeat = true;
		}
		return repeat;
	}
	
	
	
	public static void generateNegative_disease(String class_name,
			HashSet<String> similar_drugs,HashSet<String> positive_train,HashSet<String> positive_test, Double negative_ratio,
	String outDir) throws IOException {
		NegativeGenerator_disease g=new NegativeGenerator_disease(
				positive_train,
				positive_test);
		NegativeSetBean bean_tt=g.generate(1024, negative_ratio,"tt", similar_drugs,positive_train, positive_test );
		NegativeSetBean bean_trt=g.generate(1024, negative_ratio,"trt", similar_drugs,positive_train, positive_test );
		NegativeSetBean bean_te=g.generate(1024, negative_ratio,"te", similar_drugs,positive_train, positive_test );
		NegativeSetBean bean_tre=g.generate(1024, negative_ratio,"tre", similar_drugs,positive_train, positive_test );
		
		
		String name="";
		
		name=class_name.replaceAll(" ", "_");
		
		writeToFile( outDir,  name+"_tt", positive_train,positive_test, bean_tt.getCandidateSet());
		writeToFile( outDir,  name+"_trt", positive_train,positive_test, bean_trt.getCandidateSet());
		writeToFile( outDir,  name+"_te", positive_train,positive_test, bean_te.getCandidateSet());
		writeToFile( outDir,  name+"_tre", positive_train,positive_test, bean_tre.getCandidateSet());
		
	}
	public static HashSet<String> getTargets() throws IOException {
		HashSet<String> targetSet = new HashSet<>();
		BufferedReader bReader = new BufferedReader(new FileReader(
				new File(dataDir+"/output/datasets/orignial/sequence.txt")));
		String lineString = null;
		while ((lineString = bReader.readLine()) != null) {
			String[] elementStrings = lineString.split("\t");
			targetSet.add(elementStrings[0]);
		}
		return targetSet;
	}

	public static HashSet<String> getDrugs() throws IOException {
		HashSet<String> drugSet = new HashSet<>();
		BufferedReader bReader = new BufferedReader(new FileReader(
				new File(dataDir+"/output/datasets/orignial/smile.txt")));
		String lineString = null;
		while ((lineString = bReader.readLine()) != null) {
			String[] elementStrings = lineString.split("\t");
			drugSet.add(elementStrings[0]);
		}
		return drugSet;
	}
	
	public static void writeToFile(String outDir, String type, HashSet<String> positive_train,HashSet<String> positive_test, HashSet<String> nateive_test ) throws IOException {
		
		HashSet<String> drugs = getDrugs();
		HashSet<String> targets = getTargets();

		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(outDir + "/train" +"_"+type +".nt")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(outDir + "/test" +"_"+type +".nt")));
		for(String string:positive_train) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (!drugs.contains(s)) {
					System.err.println(type+" @ warning positive_train, not SMILE or sequence for the pair " + s);
				}
				if (!targets.contains(o)) {
					System.err.println(type+" @ warning positive_train, not SMILE or sequence for the pair " + o);
				}
				
				bw1.write(s+" "+o+" "+"true \n");
			}	
		}
		for(String string:positive_test) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				bw2.write(s+" "+o+" "+"true \n");
				if (!drugs.contains(s)) {
					System.err.println(type+" @ warning positive_test, not SMILE or sequence for the pair " + s);
				}
				if (!targets.contains(o)) {
					System.err.println(type+" @ warning positive_test, not SMILE or sequence for the pair " + o);
				}
			}	
		}
		for(String string:nateive_test) {
			String[] elementStrings=string.split(" ");
			bw2.write(elementStrings[0]+" "+elementStrings[1]+" "+"false \n");
			
			if (!drugs.contains(elementStrings[0])) {
				System.err.println(type+" @ warning nateive_test, not SMILE or sequence for the pair " + elementStrings[0]);
			}
			if (!targets.contains(elementStrings[1])) {
				System.err.println(type+" @ warning nateive_test, not SMILE or sequence for the pair " + elementStrings[1]);
			}
		}
		bw1.flush();
		bw1.close();
		bw2.flush();
		bw2.close();
	}

}
