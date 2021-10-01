package benchmark.render.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.listeners.NullListener;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Test_proteinClass {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		pantherTodrugbank() ;
	}
	
	public static HashMap<String, HashSet<String>> convertToDrugBank(HashMap<String, HashSet<String>> x_uniprot,
			HashMap<String, HashSet<String>> uniprotToDrugbankHashMap,
			HashMap<String, HashSet<String>> objectToTripleHashMap){ 
		HashMap<String, HashSet<String>> map=new HashMap<>();
		
		for(Entry<String, HashSet<String>> entry:x_uniprot.entrySet()) {
			HashSet<String> set=new HashSet<>();
			map.put(entry.getKey(), set);
			for(String uniprot:entry.getValue()) {
				if(uniprotToDrugbankHashMap.containsKey(uniprot)) {
					for(String drugbank_target:uniprotToDrugbankHashMap.get(uniprot)) {
						if(objectToTripleHashMap.containsKey(drugbank_target)) {
							set.addAll(objectToTripleHashMap.get(drugbank_target));
						}
					}
				}
			}
		}
		return map;
	}
	
	
	public static ArrayList<Map.Entry<String,HashSet<String>>>  sortMap( HashMap<String, HashSet<String>> map, int topN) {
		ArrayList<Map.Entry<String,HashSet<String>>> list_2 = new ArrayList<>(map.entrySet());
	       Collections.sort(list_2, new Comparator<Map.Entry<String,HashSet<String>>>() {
	           public int compare(Map.Entry<String,HashSet<String>> o1, Map.Entry<String,HashSet<String>> o2) {
	               return Double.valueOf(o2.getValue().size()).compareTo(Double.valueOf(o1.getValue().size()));//升序，前边加负号变为降序
	           }
	       });
	       
	       for (int i = 0; i < topN; i++) {
			System.out.println(list_2.get(i).getKey()+" -> "+list_2.get(i).getValue().size());
	       }
	     return list_2;
	}
	
	public static void pantherTodrugbank() throws IOException {
		HashMap<String, HashSet<String>> uniprotToDrugbankHashMap=getUniprotToDrugbank();
		HashMap<String, HashSet<String>> objectToTripleHashMap=getTripleFromObject();
		
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/input/PTHR15.0_human")));
		String lineString=null;
		
		HashMap<String, HashSet<String>> pantherMolecular_uniprot=new HashMap<>();
		HashMap<String, HashSet<String>> pantherBiologicalProcess_uniprot=new HashMap<>();
		HashMap<String, HashSet<String>> pantherCellularComponents_uniprot=new HashMap<>();
		HashMap<String, HashSet<String>> pantherFamily_uniprot=new HashMap<>();
		HashMap<String, HashSet<String>> pantherSubFamily_uniprot=new HashMap<>();
		HashMap<String, HashSet<String>> pantherProteinClass_uniprot=new HashMap<>();
		
		while((lineString=br.readLine())!=null) {
			String[] elementStrings=lineString.toLowerCase().split("\t");
			
			String Gene_Identifier=null;
			String PANTHER_SF_ID=null;
			
			String PANTHER_Family_Name=null;
			String PANTHER_Subfamily_Name=null;
			
			String PANTHER_Molecular_function=null;
			String PANTHER_Biological_process=null;
			String Cellular_components=null;
			
			String Protein_class=null;
			String Pathway=null;
			String gene_id=null;
			String pthr_familyid=null;
			String pthr_subfamilyid=null;
			
			if(elementStrings.length>=1) {
				Gene_Identifier=elementStrings[0];
				gene_id=Gene_Identifier.substring(Gene_Identifier.lastIndexOf("=")+1,Gene_Identifier.length()).trim();
			}
			
			if(elementStrings.length>=3) {
				PANTHER_SF_ID=elementStrings[2];
				pthr_familyid=PANTHER_SF_ID.substring(0,PANTHER_SF_ID.lastIndexOf(":")).trim();
				pthr_subfamilyid=PANTHER_SF_ID.trim();			
			}
			if(elementStrings.length>=4) {
				PANTHER_Family_Name=elementStrings[3];
			}
			if(elementStrings.length>=5) {
				PANTHER_Subfamily_Name=elementStrings[4];
			}
			if(elementStrings.length>=6) {
				PANTHER_Molecular_function=elementStrings[5];
			}
			if(elementStrings.length>=7) {
				PANTHER_Biological_process=elementStrings[6];
			}
			if(elementStrings.length>=8) {
				Cellular_components=elementStrings[7];
			}
			if(elementStrings.length>=9) {
				Protein_class=elementStrings[8];
			}
			if(elementStrings.length>=10) {
				Pathway=elementStrings[9];
			}
			
			
			
			if(pthr_familyid!=null) {
				if(pthr_familyid.length()>1) {
					if(pantherFamily_uniprot.containsKey(pthr_familyid)) {
						pantherFamily_uniprot.get(pthr_familyid).add(gene_id);
					}else {
						HashSet<String> set=new HashSet<>();
						set.add(gene_id);
						pantherFamily_uniprot.put(pthr_familyid, set);
					}
				}
			}
			
			
			if(pthr_subfamilyid!=null) {
				if(pthr_subfamilyid.length()>1) {
					if(pantherSubFamily_uniprot.containsKey(pthr_subfamilyid)) {
						pantherSubFamily_uniprot.get(pthr_subfamilyid).add(gene_id);
					}else {
						HashSet<String> set=new HashSet<>();
						set.add(gene_id);
						pantherSubFamily_uniprot.put(pthr_subfamilyid, set);
					}
				}
			}
			
			
			if(PANTHER_Molecular_function!=null) {
				for(String string:PANTHER_Molecular_function.split(";")) {
					String  value=string.substring(string.lastIndexOf(":")+1,string.length()).trim();
					if(value.length()>1) {
						if(pantherMolecular_uniprot.containsKey(value)) {
							pantherMolecular_uniprot.get(value).add(gene_id);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(gene_id);
							pantherMolecular_uniprot.put(value, set);
						}
					}
				}
			}
			
			if(PANTHER_Biological_process!=null) {
				for(String string:PANTHER_Biological_process.split(";")) {
					String  value=string.substring(string.lastIndexOf(":")+1,string.length()).trim();
					if(value.length()>1) {
						if(pantherBiologicalProcess_uniprot.containsKey(value)) {
							pantherBiologicalProcess_uniprot.get(value).add(gene_id);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(gene_id);
							pantherBiologicalProcess_uniprot.put(value, set);
						}
					}
				}
			}
			
			if(Cellular_components!=null) {
				for(String string:Cellular_components.split(";")) {
					String  value=string.substring(string.lastIndexOf(":")+1,string.length()).trim();
					if(value.length()>1) {
						if(pantherCellularComponents_uniprot.containsKey(value)) {
							pantherCellularComponents_uniprot.get(value).add(gene_id);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(gene_id);
							pantherCellularComponents_uniprot.put(value, set);
						}
					}
				}	
			}
			
			if(Protein_class!=null) {
				if(Protein_class.length()>1) {
					for(String string:Protein_class.split(";")) {
						String  value=string.substring(string.lastIndexOf("#")+1,string.length()).trim();
						if(value.length()>1) {
							if(pantherProteinClass_uniprot.containsKey(value)) {
								pantherProteinClass_uniprot.get(value).add(gene_id);
							}else {
								HashSet<String> set=new HashSet<>();
								set.add(gene_id);
								pantherProteinClass_uniprot.put(value, set);
							}
						}	
					}
				}
			}
		}
		br.close();
		
		HashMap<String, HashSet<String>> pantherMolecular_map=convertToDrugBank(pantherMolecular_uniprot, 
				uniprotToDrugbankHashMap,
				objectToTripleHashMap);
		
		HashMap<String, HashSet<String>> pantherBiologicalProcess_map=convertToDrugBank(pantherBiologicalProcess_uniprot, 
				uniprotToDrugbankHashMap,
				objectToTripleHashMap);
		
		HashMap<String, HashSet<String>> pantherCellularComponents_map=convertToDrugBank(pantherCellularComponents_uniprot, 
				uniprotToDrugbankHashMap,
				objectToTripleHashMap);
		
		HashMap<String, HashSet<String>> pantherFamily_map=convertToDrugBank(pantherFamily_uniprot, 
				uniprotToDrugbankHashMap,
				objectToTripleHashMap);
		
		HashMap<String, HashSet<String>> pantherSubFamily_map=convertToDrugBank(pantherSubFamily_uniprot, 
				uniprotToDrugbankHashMap,
				objectToTripleHashMap);
		
		HashMap<String, HashSet<String>> pantherProteinClass_map=convertToDrugBank(pantherProteinClass_uniprot, 
				uniprotToDrugbankHashMap,
				objectToTripleHashMap);
		
		System.out.println(pantherMolecular_map.size());
		System.out.println(pantherBiologicalProcess_map.size());
		System.out.println(pantherCellularComponents_map.size());
		System.out.println(pantherFamily_map.size());
		System.out.println(pantherSubFamily_map.size());
		System.out.println(pantherProteinClass_map.size());
		
		
		System.out.println("==============pantherMolecular_map==============");
		sortMap( pantherMolecular_map, 10);
		System.out.println("==============pantherBiologicalProcess_map==============");
		sortMap( pantherBiologicalProcess_map, 10);
		System.out.println("==============pantherCellularComponents_map==============");
		sortMap( pantherCellularComponents_map, 10);
		System.out.println("==============pantherFamily_map==============");
		sortMap( pantherFamily_map, 10);
		System.out.println("==============pantherSubFamily_map==============");
		sortMap( pantherSubFamily_map, 10);
		System.out.println("==============pantherProteinClass_map==============");
		sortMap( pantherProteinClass_map, 10);
		
	}
	
	public static HashMap<String, HashSet<String>> getUniprotToDrugbank() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(dataDir+"/input/done/drugbank.nq")));
		String line=null;
		HashMap<String,HashSet<String>> valueHashMap=new HashMap<>();
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					String uniprotString=o.substring(o.lastIndexOf(":")+1,o.lastIndexOf(">"));
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")) {
						if(s.startsWith("<http://bio2rdf.org/drugbank:be")) {
							if(valueHashMap.containsKey(uniprotString)) {
								valueHashMap.get(uniprotString).add(s);
							}else {
								HashSet<String> set=new HashSet<>();
								set.add(s);
								valueHashMap.put(uniprotString, set);
							}	
						}
					}
				}
			}
		}
		return valueHashMap;
	}
	
	
	public static HashMap<String, HashSet<String>> getTripleFromObject() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/association_drugbank.nq")));
		String line=null;
		HashMap<String,HashSet<String>> valueHashMap=new HashMap<>();
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
						if(valueHashMap.containsKey(o)) {
							valueHashMap.get(o).add(line);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(line);
							valueHashMap.put(o, set);
						}
					}
				}
			}
		}
		return valueHashMap;
	}
}
