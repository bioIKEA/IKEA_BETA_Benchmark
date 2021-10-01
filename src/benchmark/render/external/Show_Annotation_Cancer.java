package benchmark.render.external;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

import org.nd4j.shade.jackson.dataformat.yaml.snakeyaml.Yaml;
import org.netlib.util.booleanW;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Show_Annotation_Cancer {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, HashSet<String>> diseaseToGenes_diseasome=diseaseToGenes_diseasome();
		System.out.println("diseaseToGenes_diseasome: "+diseaseToGenes_diseasome.size());
		
		HashMap<String,String> labels=readLabel();
		HashMap<String, HashSet<String>> diseasomeTargetToDrugBankTarget=diseasomeTargetToDrugBankTarget();
		
		HashMap<String, HashSet<String>> diseaseToDrugBankTarget=new HashMap<>();
		
		for(Entry<String, HashSet<String>> entry:diseaseToGenes_diseasome.entrySet()) {
			for(String string:entry.getValue()) {
				if(diseasomeTargetToDrugBankTarget.containsKey(string)) {
					for(String drugBank_gene:diseasomeTargetToDrugBankTarget.get(string)) {
						if(diseaseToDrugBankTarget.containsKey(entry.getKey())) {
							diseaseToDrugBankTarget.get(entry.getKey()).add(drugBank_gene);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(drugBank_gene);
							diseaseToDrugBankTarget.put(entry.getKey(), set);
						}
					}
				}
			}
		}
		System.out.println("labels: "+labels.size());
		System.out.println("diseasomeTargetToDrugBankTarget: "+diseasomeTargetToDrugBankTarget.size());
		System.out.println("diseaseToDrugBankTarget: "+diseaseToDrugBankTarget.size());
		ArrayList<Map.Entry<String,HashSet<String>>>  sortedList=sortMap( diseaseToDrugBankTarget);
		
		for (int i = 0; i < sortedList.size(); i++) {
			Map.Entry<String,HashSet<String>> entry=sortedList.get(i);
			String nameString=labels.get(entry.getKey()).toLowerCase().trim();
			if(nameString.contains("cancer")) {
				System.out.println(nameString+"\t"+entry.getValue().size()+"\t");
			}
		}
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
	
	public static HashMap<String,String> readLabel_drugbank() throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/input/done/drugbank.nq")));
		String line=null;
		HashMap<String,String> lablesHashMap=new HashMap<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim().toLowerCase();
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
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/network/target_diseasome_drugbank.nq")));
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
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/network/association_diseasome.nq")));
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
}
