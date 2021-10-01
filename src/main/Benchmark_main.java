package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.listeners.NullListener;
import org.apache.xerces.impl.xs.identity.Field;
import org.bounce.message.Publisher;
import org.bytedeco.javacpp.annotation.NoDeallocator;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.tukaani.xz.check.Check;
import org.xml.sax.SAXException;

import com.twelvemonkeys.io.FileSeekableStream;

import benchmark.render.external.External_Testgenerator;
import benchmark.render.external.Generate_Annotation_Cancer;
import benchmark.render.external.VersioningbasedDrugBankXML;
import benchmark.render.internal.Internal_Testgenerator;
import java_cup.internal_error;

public class Benchmark_main {

	public static String dataDir="data_sample";
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub
		
		task_0();
		task_1();
		task_2();
		task_3();
		task_4();
		task_5();
	}
	
	
	public static void task_0() throws IOException {
		generate_genral();
		for (int i = 0; i < 10; i++) {
		String path=dataDir+"/output/datasets/experiment/internal_general";
		String train=path+"/train_"+i+"_general.nt";
		String test=path+"/test_"+i+"_general.nt";
		String test_isolateFree=path+"/test_"+i+"_general_isolateFree.nt";
		removeIsolate( train,  test,  test_isolateFree) ;
		}
	}
	
	public static void task_1() throws IOException {
		generate_internal();
	}
	
	public static void task_2() throws IOException {
		generate_drugClass();
	}
	
	public static void task_3() throws IOException {
		generate_targetClass();
	}
	
	public static void task_4() throws IOException, ParserConfigurationException, SAXException {
		generate_diseaseClass();
	}
	
	public static void task_5() throws IOException, ParserConfigurationException, SAXException {
		String annotation_file=dataDir+"/output/datasets/orignial/annotation_disease_results.csv";
		
//		generate_clinicalCT_genral(annotation_file);
//		generate_internal_clinicalCT(annotation_file);
		generate_ClinicCTClass(annotation_file) ;
	}
	
	public static HashSet<String> getDrugTarget() throws IOException {
		String drugbank_file=dataDir+"/output/association_drugbank.nq";
		
		String line=null;
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		BufferedReader br=new BufferedReader(new FileReader(new File(drugbank_file)));
		HashSet<String> pair=new HashSet<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						pair.add(s+" "+o);	
					}
				}
			}
		}
		return pair;
	}
	
	public static void updateDisease_target(String file, HashSet<String> targets, HashMap<String,HashSet<String>> disease_targets) throws IOException {
		String name=new File(file).getName();
		name=name.substring(0,name.indexOf("."));
		HashSet<String> set=new HashSet<>();
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null) {
			String[] elements=line.split("\t");
			String dbpedia_name="<http://bio2rdf.org/drugbank:"+elements[0]+">";
			if(targets.contains(dbpedia_name)) {
				set.add(dbpedia_name);	
			}
		}
		disease_targets.put(name, set);
	}
	public static void check_clinicalCT(String annotationFile) throws IOException {
		  HashSet<String> drugs=getDrugs();
	        HashSet<String> targets=getTargets();
	        
		HashMap<String,HashSet<String>> disease_allTargets=new HashMap<>();
		for(File file:new File(dataDir+"/input/disease_annotation/diseases").listFiles()){
			updateDisease_target(file.getAbsolutePath(),  targets,  disease_allTargets);
		}
		System.out.println(disease_allTargets);
		Reader reader = Files.newBufferedReader(Paths.get(annotationFile),StandardCharsets.ISO_8859_1);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withIgnoreHeaderCase());
        
        HashMap<String,HashSet<String>> disease_drugs=new HashMap<>();
        HashMap<String,HashSet<String>> disease_targets=new HashMap<>();
        HashMap<String,HashSet<String>> disease_newPair=new HashMap<>();
        
      
        
        HashSet<String> exisited_drugTarget=getDrugTarget();
        
        for (CSVRecord csvRecord : csvParser) {
      	  String disease=csvRecord.get(1).toLowerCase();
      	  String target_id=csvRecord.get(2).toLowerCase();
      	  String drug_id=csvRecord.get(4).toLowerCase();
      	  String target="<http://bio2rdf.org/drugbank:"+target_id+">";
      	  String drug="<http://bio2rdf.org/drugbank:"+drug_id+">";
      	  
      	  if(drugs.contains(drug)&&targets.contains(target)) {
      		  if(!exisited_drugTarget.contains(drug+" "+target)) {
      			if(disease_drugs.containsKey(disease)) {
          			disease_drugs.get(disease).add(drug);
          		  }else {
          			  HashSet<String> set=new HashSet<>();
          			  set.add(drug);
          			  disease_drugs.put(disease, set);
          		  }
          		  
          		 if(disease_targets.containsKey(disease)) {
          			disease_targets.get(disease).add(target);
           		  }else {
           			  HashSet<String> set=new HashSet<>();
           			  set.add(target);
           			disease_targets.put(disease, set);
           		  }
          		 
          		 if(disease_newPair.containsKey(disease)) {
          			disease_newPair.get(disease).add(drug+" "+target);
          		 }else {
          			 HashSet<String> set=new HashSet<>();
          			 set.add(drug+" "+target);
          			disease_newPair.put(disease, set);
          		 }
      		  }
      	  }
        }
        
        for(Entry<String, HashSet<String>> entry:disease_drugs.entrySet()) {
        	String disease=entry.getKey();
        	HashSet<String> drugs_set=entry.getValue();
        	HashSet<String> targets_set=disease_targets.get(entry.getKey());
        	HashSet<String> newPair_set=disease_newPair.get(entry.getKey());
        	if(drugs_set.size()>1) {
        		HashSet<String> all_targets_set=disease_allTargets.get(entry.getKey());
        		if(drugs_set.size()*all_targets_set.size()>12) {
            		System.out.println(disease+"\t"+"drugs: "+drugs_set.size()+"\t"+"targets: "+all_targets_set.size()+"\t"+" new-pair: "+newPair_set.size()+"\t"+" drug-target: "+
            	        	drugs_set.size()*all_targets_set.size());		
        		}
        	}
        }
        
        
	}
	
	
	
	public static void changeNames() {
		HashSet<String> set=new HashSet<>();
		for(File dir:new File(dataDir+"/output/datasets/experiment/targetClass").listFiles()) {
			if(dir.getName().equals("family")||dir.getName().equals("proteinClass")) {
				for(File file:dir.listFiles()) {
					if(file.getName().contains("_trt.nt")) {
						file.renameTo(new File(file.getAbsolutePath().replace("_trt", "_trt_")));
					}
					if(file.getName().contains("_tre.nt")) {
						file.renameTo(new File(file.getAbsolutePath().replace("_tre", "_tre_")));
					}
					set.add(file.getName());
				}	
			}
		}
	}
	
	public static void generate_diseaseClass(HashSet<String> diseases) throws IOException, ParserConfigurationException, SAXException {
		
		String drugbank_file=dataDir+"/output/datasets/orignial/network/association_drugbank.nq";
		String drugbank_newfile=dataDir+"/input/full database_202011.xml";
		
		String line=null;
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		BufferedReader br=new BufferedReader(new FileReader(new File(drugbank_file)));
		HashSet<String> tripleSet=new HashSet<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
				}
			}
		}
		
		HashMap<String,String> xml_relations=VersioningbasedDrugBankXML.extract_newDrugBankRelation
				(drugbank_newfile);
		HashMap<String,HashSet<String>> retained_relations=VersioningbasedDrugBankXML.Retain(tripleSet, 
				xml_relations);
		
		System.out.println("xml_relations: "+xml_relations.size());
		System.out.println("retained_relationSet: "+retained_relations.size());
		
		HashMap<String, HashSet<String>> diseaseToGenes_diseasome=External_Testgenerator.diseaseToGenes_diseasome();
		System.out.println("diseaseToGenes_diseasome: "+diseaseToGenes_diseasome.size());
		
		HashMap<String,String> labels=External_Testgenerator.readLabel();
		HashMap<String, HashSet<String>> diseasomeTargetToDrugBankTarget=External_Testgenerator.diseasomeTargetToDrugBankTarget();
		
		HashMap<String, HashSet<String>> diseaseToDrugBankTarget=new HashMap<>();
		HashSet<String> drugBank_targets=getTargets();
		for(Entry<String, HashSet<String>> entry:diseaseToGenes_diseasome.entrySet()) {
			for(String string:entry.getValue()) {
				if(diseasomeTargetToDrugBankTarget.containsKey(string)) {
					for(String drugBank_gene:diseasomeTargetToDrugBankTarget.get(string)) {
						
						if(drugBank_targets.contains(drugBank_gene)) {
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
		}
		System.out.println("labels: "+labels.size());
		System.out.println("diseasomeTargetToDrugBankTarget: "+diseasomeTargetToDrugBankTarget.size());
		System.out.println("diseaseToDrugBankTarget: "+diseaseToDrugBankTarget.size());
		
		
		HashMap<String, HashSet<String>> diseaseToDrugBankTriple=new HashMap<>();
		
		for(Entry<String,HashSet<String>> entry:diseaseToDrugBankTarget.entrySet()) {
			HashSet<String> set=new HashSet<>();
			for(String target:entry.getValue()) {
				if(retained_relations.containsKey(target)) {
					HashSet<String> new_triples=retained_relations.get(target);
					set.addAll(new_triples);
				}
			}
			diseaseToDrugBankTriple.put(entry.getKey(), set);
		}
		
		
		HashMap<String, String> old_mergeTo=new HashMap<>();
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/2652>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/535>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/382>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/2347>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1983>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/261>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/2652>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/535>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/382>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/2347>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1313>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/24>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1983>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/261>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/3664>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/3658>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/960>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/3658>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/84>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1439>");
		
		old_mergeTo.put("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/3165>", 
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/764>");
		
		
		updateMap(old_mergeTo,  diseaseToDrugBankTriple);
		
		
		HashMap<String, HashSet<String>> diseaseToDrugBankTriple_filgered=filter(diseaseToDrugBankTriple,
				diseases);
		new File(dataDir+"/output/datasets/experiment/disease").mkdirs();
		External_Testgenerator.writeDataToFolders_disease(
				dataDir+"/output/datasets/experiment/disease",
				tripleSet,  
				diseaseToDrugBankTriple_filgered, 
				labels, 
				diseaseToDrugBankTarget);
}
	
	

	
public static void generate_diseaseClass() throws IOException, ParserConfigurationException, SAXException {
		
		String drugbank_file=dataDir+"/output/association_drugbank.nq";
		String drugbank_newfile=dataDir+"/output/datasets/orignial/full database_202011.xml";
		
		String line=null;
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		BufferedReader br=new BufferedReader(new FileReader(new File(drugbank_file)));
		HashSet<String> tripleSet=new HashSet<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
				}
			}
		}
		
		HashMap<String,String> xml_relations=VersioningbasedDrugBankXML.extract_newDrugBankRelation
				(drugbank_newfile);
		HashMap<String,HashSet<String>> retained_relations=VersioningbasedDrugBankXML.Retain(tripleSet, 
				xml_relations);
		
		System.out.println("xml_relations: "+xml_relations.size());
		System.out.println("retained_relationSet: "+retained_relations.size());
		
		HashMap<String, HashSet<String>> diseaseToGenes_diseasome=External_Testgenerator.diseaseToGenes_diseasome();
		System.out.println("diseaseToGenes_diseasome: "+diseaseToGenes_diseasome.size());
		
		HashMap<String,String> labels=External_Testgenerator.readLabel();
		HashMap<String, HashSet<String>> diseasomeTargetToDrugBankTarget=External_Testgenerator.diseasomeTargetToDrugBankTarget();
		
		HashMap<String, HashSet<String>> diseaseToDrugBankTarget=new HashMap<>();
		HashSet<String> drugBank_targets=getTargets();
		for(Entry<String, HashSet<String>> entry:diseaseToGenes_diseasome.entrySet()) {
			for(String string:entry.getValue()) {
				if(diseasomeTargetToDrugBankTarget.containsKey(string)) {
					for(String drugBank_gene:diseasomeTargetToDrugBankTarget.get(string)) {
						
						if(drugBank_targets.contains(drugBank_gene)) {
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
		}
		System.out.println("labels: "+labels.size());
		System.out.println("diseasomeTargetToDrugBankTarget: "+diseasomeTargetToDrugBankTarget.size());
		System.out.println("diseaseToDrugBankTarget: "+diseaseToDrugBankTarget.size());
		
		
		HashMap<String, HashSet<String>> diseaseToDrugBankTriple=new HashMap<>();
		
		for(Entry<String,HashSet<String>> entry:diseaseToDrugBankTarget.entrySet()) {
			HashSet<String> set=new HashSet<>();
			for(String target:entry.getValue()) {
				if(retained_relations.containsKey(target)) {
					HashSet<String> new_triples=retained_relations.get(target);
					set.addAll(new_triples);
				}
			}
			diseaseToDrugBankTriple.put(entry.getKey(), set);
		}
		
		HashSet<HashSet<String>> disease_cluster=read_disease_cluter();
		updateMap(disease_cluster,  diseaseToDrugBankTriple);
		
		ArrayList<Map.Entry<String,HashSet<String>>>  sortedList=External_Testgenerator.sortMap( diseaseToDrugBankTriple);
		
		
//		HashMap<String, HashSet<String>> diseaseToDrugBankTriple_filgered=filter(diseaseToDrugBankTriple,
//				diseases);
		new File(dataDir+"/output/datasets/experiment/disease").mkdirs();
		External_Testgenerator.writeDataToFolders_disease(
				dataDir+"/output/datasets/experiment/disease",
				tripleSet,  
				sortedList, 
				labels, 
				diseaseToDrugBankTarget,
				5);
}




public static void generate_ClinicCTClass(String annotation_file) throws IOException, ParserConfigurationException, SAXException {
	
	String drugbank_file=dataDir+"/output/association_drugbank.nq";
	String line=null;
	HashSet<String> targets=getTargets() ;
	HashSet<String> drugs=getDrugs();
	BufferedReader br=new BufferedReader(new FileReader(new File(drugbank_file)));
	HashSet<String> tripleSet=new HashSet<>();
	while((line=br.readLine())!=null) {
		InputStream inputStream = new ByteArrayInputStream(line.getBytes());
		NxParser nxp = new NxParser();
		nxp.parse(inputStream);
		while (nxp.hasNext()) {
			Node[] quard = nxp.next();
			String s = quard[0].toString().trim();
			String p = quard[1].toString().trim();
			String o = quard[2].toString().trim();
			if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
					s.startsWith("<http://bio2rdf.org/drugbank:db")&&
					o.startsWith("<http://bio2rdf.org/drugbank:be")) {
				if(drugs.contains(s)&&targets.contains(o)) {
					tripleSet.add(line);	
				}
			}
		}
	}
	
	HashMap<String,HashSet<String>> disease_allTargets=new HashMap<>();
	for(File file:new File(dataDir+"/output/datasets/orignial/diseases").listFiles()){
		updateDisease_target(file.getAbsolutePath(),  targets, disease_allTargets);
	}
	System.out.println(disease_allTargets);
	
	Reader reader = Files.newBufferedReader(Paths.get(annotation_file),StandardCharsets.ISO_8859_1);
    CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withIgnoreHeaderCase());
    
    HashMap<String,HashSet<String>> disease_drugs=new HashMap<>();
    HashMap<String,HashSet<String>> disease_targets=new HashMap<>();
    HashMap<String,HashSet<String>> disease_newPair=new HashMap<>();
    
    HashSet<String> exisited_drugTarget=getDrugTarget();
    
    for (CSVRecord csvRecord : csvParser) {
  	  String disease=csvRecord.get(1).toLowerCase();
  	  String target_id=csvRecord.get(2).toLowerCase();
  	  String drug_id=csvRecord.get(4).toLowerCase();
  	  String target="<http://bio2rdf.org/drugbank:"+target_id+">";
  	  String drug="<http://bio2rdf.org/drugbank:"+drug_id+">";
  	  
  	  if(drugs.contains(drug)&&targets.contains(target)) {
  		  if(!exisited_drugTarget.contains(drug+" "+target)) {
  			if(disease_drugs.containsKey(disease)) {
      			disease_drugs.get(disease).add(drug);
      		  }else {
      			  HashSet<String> set=new HashSet<>();
      			  set.add(drug);
      			  disease_drugs.put(disease, set);
      		  }
      		  
      		 if(disease_targets.containsKey(disease)) {
      			disease_targets.get(disease).add(target);
       		  }else {
       			  HashSet<String> set=new HashSet<>();
       			  set.add(target);
       			disease_targets.put(disease, set);
       		  }
      		 
      		 if(disease_newPair.containsKey(disease)) {
      			disease_newPair.get(disease).add(drug+" <http://bio2rdf.org/drugbank_vocabulary:drug-target> "+target +" .");
      		 }else {
      			 HashSet<String> set=new HashSet<>();
      			 set.add(drug+" <http://bio2rdf.org/drugbank_vocabulary:drug-target> "+target +" .");
      			disease_newPair.put(disease, set);
      		 }
  		  }
  	  }
    }
   
    HashMap<String,HashSet<String>>  selected_disease_newPair=new HashMap<>();
    HashMap<String, HashSet<String>> selected_diseaseToDrugBankTarget=new HashMap<>();
    
    for(Entry<String, HashSet<String>> entry:disease_drugs.entrySet()) {
    	String disease=entry.getKey();
    	HashSet<String> drugs_set=entry.getValue();
    	HashSet<String> targets_set=disease_targets.get(entry.getKey());
    	HashSet<String> newPair_set=disease_newPair.get(entry.getKey());
    	if(drugs_set.size()>1) {
    		HashSet<String> all_targets_set=disease_allTargets.get(entry.getKey());
    		if(drugs_set.size()*all_targets_set.size()>12) {
        		System.out.println(disease+"\t"+"drugs: "+drugs_set.size()+"\t"+"targets: "+all_targets_set.size()+"\t"+" new-pair: "+newPair_set.size()+"\t"+" drug-target: "+
        	        	drugs_set.size()*all_targets_set.size());		
        		
        		selected_disease_newPair.put(disease, newPair_set);
        		selected_diseaseToDrugBankTarget.put(disease, all_targets_set);
    		}
    	}
    }
    
    ArrayList<Map.Entry<String,HashSet<String>>>  sortedList=External_Testgenerator.sortMap(selected_disease_newPair); // disease, drugbank new triple
    
	new File(dataDir+"/output/datasets/experiment/clinicalCT").mkdirs();
	External_Testgenerator.writeDataToFolders_clinicCT(
			dataDir+"/output/datasets/experiment/clinicalCT",
			tripleSet,  
			sortedList,  // disease, drugbank new triple
			selected_diseaseToDrugBankTarget);
}
	
public static HashMap<String, HashSet<String>> filter(HashMap<String, HashSet<String>> diseaseToDrugBankTriple,
		HashSet<String> diseases){
	
	
	HashMap<String, HashSet<String>> diseaseToDrugBankTriple_filgered=new HashMap<>();
	
	for(String disease:diseases) {
		diseaseToDrugBankTriple_filgered.put(disease, diseaseToDrugBankTriple.get(disease));	
	}
	
	return diseaseToDrugBankTriple_filgered; 
}
	
public static void check_diseaseClass() throws IOException, ParserConfigurationException, SAXException {
		
		String drugbank_file=dataDir+"/output/datasets/orignial/network/association_drugbank.nq";
		String drugbank_newfile=dataDir+"/input/full database_202011.xml";
//		String drugbank_newfile=dataDir+"/input/full database.xml";
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		String line=null;
		BufferedReader br=new BufferedReader(new FileReader(new File(drugbank_file)));
		HashSet<String> tripleSet=new HashSet<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
				}
			}
		}
		
		HashMap<String,String> xml_relations=VersioningbasedDrugBankXML.extract_newDrugBankRelation
				(drugbank_newfile);
		HashMap<String,HashSet<String>> retained_relations=VersioningbasedDrugBankXML.Retain(tripleSet, 
				xml_relations);
		
		System.out.println("xml_relations: "+xml_relations.size());
		System.out.println("retained_relationSet: "+retained_relations.size());
		
		HashMap<String, HashSet<String>> diseaseToGenes_diseasome=External_Testgenerator.diseaseToGenes_diseasome();
		System.out.println("diseaseToGenes_diseasome: "+diseaseToGenes_diseasome.size());
		
		HashMap<String,String> labels=External_Testgenerator.readLabel();
		HashMap<String, HashSet<String>> diseasomeTargetToDrugBankTarget=External_Testgenerator.diseasomeTargetToDrugBankTarget();
		
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
		
		
		HashMap<String, HashSet<String>> diseaseToDrugBankTriple=new HashMap<>();
		
		for(Entry<String,HashSet<String>> entry:diseaseToDrugBankTarget.entrySet()) {
			HashSet<String> set=new HashSet<>();
			for(String target:entry.getValue()) {
				if(retained_relations.containsKey(target)) {
					HashSet<String> new_triples=retained_relations.get(target);
					set.addAll(new_triples);
				}
			}
			diseaseToDrugBankTriple.put(entry.getKey(), set);
		}
		
		
		HashSet<HashSet<String>> disease_cluster=read_disease_cluter();
		for(HashSet<String> cluster:disease_cluster) {
			System.err.println(cluster);
		}
		updateMap(disease_cluster,  diseaseToDrugBankTriple);
		
		ArrayList<Map.Entry<String,HashSet<String>>>  sortedList=External_Testgenerator.sortMap( diseaseToDrugBankTriple);
		
		for (int i = 0; i <50; i++) {
			Map.Entry<String,HashSet<String>> entry=sortedList.get(i);
			System.out.println(entry.getKey()+"\t" + labels.get(entry.getKey())+"\t"+entry.getValue().size());
		}
	}
	
	public static void updateMap(HashMap<String, String> old_mergeTo, HashMap<String, HashSet<String>> updatedMap) {
		for(Entry<String, String> entry:old_mergeTo.entrySet()) {
			HashSet<String> set_1=updatedMap.get(entry.getKey());
			updatedMap.get(entry.getValue()).addAll(set_1);
			updatedMap.remove(entry.getKey());
		}
	}
	
	public static HashSet<HashSet<String>> read_disease_cluter() throws IOException {
		HashSet<HashSet<String>> return_set=new HashSet<>();
		HashMap<String,HashSet<String>> clusterHashSet=new HashMap<>();
		BufferedReader br=new BufferedReader(new FileReader(new File(
				dataDir+"/output/datasets/orignial/diseaseSome_cluster.csv")));
		String line=null;
		while((line=br.readLine())!=null) {
			String[] elements=line.split(",");
			
			if(clusterHashSet.containsKey(elements[1])) {
				clusterHashSet.get(elements[1]).add(elements[0]);
			}else {
				HashSet<String> set=new HashSet<>();
				set.add(elements[0]);
				clusterHashSet.put(elements[1], set);
			}
		}
		
		for(Entry<String, HashSet<String>> entry:clusterHashSet.entrySet()) {
			return_set.add(entry.getValue());	
		}
		return return_set;
	}
	
	public static void updateMap(HashSet<HashSet<String>> disease_cluster, HashMap<String, HashSet<String>> updatedMap) {
		
		HashSet<String> _remove=new HashSet<>();
		HashMap<String, HashSet<String>> add=new HashMap<>();
		for(HashSet<String> cluster:disease_cluster) {
			String nameString="";
			HashSet<String> values=new HashSet<>();
			for(String disease:cluster) {
				if(nameString.equals("")) {
					nameString=disease;
				}
				HashSet<String> local_value=updatedMap.get(disease);
				_remove.add(disease);
				values.addAll(local_value);
			}
			add.put(nameString, values);
		}
		
		for(String string:_remove) {
			updatedMap.remove(string);
		}
		
		updatedMap.putAll(add);
		
	}
	
	
	public static void generate_targetClass() throws IOException {
		String line=null;
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/association_drugbank.nq")));
		HashSet<String> tripleSet=new HashSet<>();
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
				}
			}
		}
		
		Internal_Testgenerator generator=new Internal_Testgenerator();
		generator.generate_targetClass(tripleSet, dataDir+"/output/datasets/experiment/targetClass");
	}
	
	
	public static void generate_drugClass() throws IOException {
		String line=null;
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/association_drugbank.nq")));
		HashSet<String> tripleSet=new HashSet<>();
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
				}
			}
		}
		Internal_Testgenerator generator=new Internal_Testgenerator();
		generator.generate_drugClass(tripleSet, dataDir+"/output/datasets/experiment/drugClass");
	}
	
	
	public static HashSet<String> getTargets() throws IOException{
		HashSet<String> targetSet=new HashSet<>();
		BufferedReader bReader =new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/sequence.txt")));
		String lineString=null;
		while((lineString=bReader.readLine())!=null) {
			String[] elementStrings=lineString.split("\t");
			targetSet.add(elementStrings[0]);
		}
		return targetSet;
	}
	
	public static HashSet<String> getDrugs() throws IOException{
		HashSet<String> drugSet=new HashSet<>();
		BufferedReader bReader =new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/smile.txt")));
		String lineString=null;
		while((lineString=bReader.readLine())!=null) {
			String[] elementStrings=lineString.split("\t");
			drugSet.add(elementStrings[0]);
		}
		return drugSet;
	}
	
	
	public static void generate_clinicalCT_genral(String annotation_file) throws IOException {
		String line=null;
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/association_drugbank.nq")));
		HashSet<String> tripleSet=new HashSet<>();
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim().toLowerCase();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
					
				}
			}
		}
		Internal_Testgenerator generator=new Internal_Testgenerator();
		
		generator.generate_clinicalCT_general(annotation_file, tripleSet, 
				dataDir+"/output/datasets/experiment/clinicalCT");
	}
	
	
	public static void generate_genral() throws IOException {
		String line=null;
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/association_drugbank.nq")));
		HashSet<String> tripleSet=new HashSet<>();
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim().toLowerCase();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
					
				}
			}
		}
		
		Internal_Testgenerator generator=new Internal_Testgenerator();
		
		generator.generate_general(tripleSet, 
				dataDir+"/output/datasets/experiment/internal_general");
	}
	
	public static void generate_internal() throws IOException {
		String line=null;
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/association_drugbank.nq")));
		HashSet<String> tripleSet=new HashSet<>();
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim().toLowerCase();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
					
				}
			}
		}
		Internal_Testgenerator generator=new Internal_Testgenerator();
		
		generator.generate_nonisolate(tripleSet, 
				dataDir+"/output/datasets/experiment/internal");
		
		generator.generate_semiisolate(tripleSet, 
				dataDir+"/output/datasets/experiment/internal");
		
		generator.generate_allisolate(tripleSet, 
				dataDir+"/output/datasets/experiment/internal");
	}
	
	
	
	public static void generate_internal_clinicalCT(String annotation_file) throws IOException {
		String line=null;
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/network/association_drugbank.nq")));
		HashSet<String> tripleSet=new HashSet<>();
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim().toLowerCase();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						tripleSet.add(line);	
					}
					
				}
			}
		}
		
		
		Internal_Testgenerator generator=new Internal_Testgenerator();
		
		generator.generate_clinicalCT_nonisolate(annotation_file, tripleSet, 
				dataDir+"/output/datasets/experiment/clinicalCT");
		
		generator.generate_clinicalCT_semiisolate(annotation_file, tripleSet, 
				dataDir+"/output/datasets/experiment/clinicalCT");
		
		generator.generate_clinicalCT_allisolate(annotation_file, tripleSet, 
				dataDir+"/output/datasets/experiment/clinicalCT");
	}
	
	public static void removeIsolate(String train, String test, String isolate_free) throws IOException {
		HashSet<String> nodes_train=readFile(train);
		HashSet<String> nodes_test=readFile(test);
		System.out.println(train+" -> all test node: "+nodes_test.size());
		nodes_test.removeAll(nodes_train);
		System.out.println(train+" -> isolate test node: "+nodes_test.size());
		removeTriples(nodes_train,  test,  isolate_free) ;
	}
	
	public static void removeTriples(HashSet<String> nodes, String test, String isolate_free ) throws IOException {
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(isolate_free)));
		BufferedReader br=new BufferedReader(new FileReader(new File(test)));
		String line=null;
		int all=0;
		int remain=0;
		while((line=br.readLine())!=null) {
			String[] elements=line.split(" ");
			all++;
			if(nodes.contains(elements[0])&&nodes.contains(elements[1])) {
				bw.write(line+"\n");
				remain++;
			}
		}
		System.out.println("remain: "+remain+" all: "+all);
		bw.flush();
		bw.close();
	}
	
	public static HashSet<String> readFile(String file) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashSet<String> nodes=new HashSet<>();
		while((line=br.readLine())!=null) {
			String[] elements=line.split(" ");
			nodes.add(elements[0]);
			nodes.add(elements[1]);
		}
		return nodes;
	}
	
}
