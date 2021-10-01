package benchmark.render.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import java_cup.internal_error;

public class NegativeGenerator_isolate {

	HashSet<String> isolated_nodes;
	HashSet<String> connected_nodes;
	HashSet<String> connected_drugs;
	HashSet<String> isolated_drugs;
	HashSet<String> isolated_targets;
	HashSet<String> connected_targets;
	HashSet<String> trainSet;
	HashSet<String> testSet;
	
	public HashMap<String,HashSet<String>> drugTargets;
	public HashMap<String,HashSet<String>> targetDrugs;
	
	public HashMap<String,HashSet<String>> candidateDrugTargets;
	public HashMap<String,HashSet<String>> candidateTargetDrugs;
	public HashSet<String> candidateSet;
	
	public NegativeGenerator_isolate(HashSet<String> positive_train, HashSet<String> positive_test) throws IOException {

		isolated_nodes = new HashSet<>();
		connected_nodes = new HashSet<>();
		
		isolated_drugs=new HashSet<>();
		isolated_targets=new HashSet<>();
		
		connected_drugs=new HashSet<>();
		connected_targets=new HashSet<>();
		
		testSet = readTestAssociation(positive_test);
		trainSet = readTrainAssociation(positive_train);
		feedDrugTargetAssociation(positive_train);
		
		isolated_nodes.removeAll(connected_nodes);
		isolated_drugs.removeAll(connected_drugs);
		isolated_targets.removeAll(connected_targets);
		
		System.out.println("train postive: " + trainSet.size());
		System.out.println("test positive: " + testSet.size());

		System.out.println("isolated_nodes: " + isolated_nodes.size());
		System.out.println("connected_nodes: " + connected_nodes.size());

		/**
		 * ss
		 * su
		 * di
		 */

	}

	
	/**
	 * ss
	 * su
	 * di
	 */
	
public NegativeSetBean generate_isolate_ss(long seed, int ratio){
		
		/**
		 *  marker
		 */
			candidateDrugTargets=new HashMap<>();
			candidateTargetDrugs=new HashMap<>();
			candidateSet=new HashSet<>();
			HashSet<String> set1=new HashSet<>();
			Random random = new Random(seed);
			
			for(String test:testSet) {
				String[] elementStrings=test.split(" ");
				String drugString=elementStrings[0];
				String targetString=elementStrings[1];
				
				if(connected_nodes.contains(drugString)) {
					ArrayList<String> local_selectArrayList=new ArrayList<>();
					for(String local_target:drugTargets.get(drugString)) {
						for(String local_drug:targetDrugs.get(local_target)) {
							if(!local_selectArrayList.contains(local_drug)) {
								local_selectArrayList.add(local_drug);
							}
						}
					}
					Collections.shuffle(local_selectArrayList,random); 
					for (int i = 0; (i<local_selectArrayList.size())&&(i<ratio); i++) {
						set1.add(local_selectArrayList.get(i)+" "+targetString);
					}
				}else if (connected_nodes.contains(targetString)){
					ArrayList<String> local_selectArrayList=new ArrayList<>();
					for(String local_drug:targetDrugs.get(targetString)) {
						for(String local_target:drugTargets.get(local_drug)) {
							if(!local_selectArrayList.contains(local_target)) {
								local_selectArrayList.add(local_target);
							}
						}
					}
					Collections.shuffle(local_selectArrayList,random); 
					for (int i = 0; (i<local_selectArrayList.size())&&(i<ratio); i++) {
						set1.add(drugString+" "+local_selectArrayList.get(i));
					}
				}
			}
			
			set1.removeAll(trainSet);
			set1.removeAll(testSet);
			
			/**
			 *  all negatives, no positives
			 */
			
			
			
			for (String string:set1) {
				candidateSet.add(string);
				String[] e=string.split(" ");
				if(candidateDrugTargets.containsKey(e[0])){
					candidateDrugTargets.get(e[0]).add(e[1]);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(e[1]);
					candidateDrugTargets.put(e[0], set);
				}
				
				if(candidateTargetDrugs.containsKey(e[1])){
					candidateTargetDrugs.get(e[1]).add(e[0]);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(e[0]);
					candidateTargetDrugs.put(e[1], set);
				}
			}
			
			System.out.println("testSet negatives: "+candidateSet.size());
			
			NegativeSetBean bean=new NegativeSetBean();
			bean.setCandidateDrugTargets(candidateDrugTargets);
			bean.setCandidateSet(candidateSet);
			bean.setCandidateTargetDrugs(candidateTargetDrugs);
			set1.clear();
			return bean;
		}


public NegativeSetBean generate_isolate_su(long seed, int ratio ){
	
	/**
	 *  marker
	 */
		candidateDrugTargets=new HashMap<>();
		candidateTargetDrugs=new HashMap<>();
		candidateSet=new HashSet<>();
		HashSet<String> set1=new HashSet<>();
		Random random = new Random(seed);
		
		for(String test:testSet) {
			String[] elementStrings=test.split(" ");
			String drugString=elementStrings[0];
			String targetString=elementStrings[1];
			
			if(connected_nodes.contains(drugString)) {
				HashSet<String> sibling=new HashSet<>();
				sibling.add(drugString);
				for(String local_target:drugTargets.get(drugString)) {
					for(String local_drug:targetDrugs.get(local_target)) {
							sibling.add(local_drug);
					}
				}
				HashSet<String> local_drugSet=new HashSet<>(); 
				for(String drug:connected_drugs) {
					local_drugSet.add(drug);
				}
				local_drugSet.removeAll(sibling);
				ArrayList<String> local_selectArrayList=new ArrayList<>();
				for(String string:local_drugSet) {
					local_selectArrayList.add(string);
				}
				
				Collections.shuffle(local_selectArrayList,random); 
				for (int i = 0; (i<local_selectArrayList.size())&&(i<ratio); i++) {
					set1.add(local_selectArrayList.get(i)+" "+targetString);
				}
			}else if (connected_nodes.contains(targetString)){
				HashSet<String> sibling=new HashSet<>();
				sibling.add(targetString);
				for(String local_drug:targetDrugs.get(targetString)) {
					for(String local_target:drugTargets.get(local_drug)) {
							sibling.add(local_target);
					}
				}
				HashSet<String> local_targetSet=new HashSet<>(); 
				for(String target:connected_targets) {
					local_targetSet.add(target);
				}
				local_targetSet.removeAll(sibling);
				ArrayList<String> local_selectArrayList=new ArrayList<>();
				for(String string:local_targetSet) {
					local_selectArrayList.add(string);
				}
				
				Collections.shuffle(local_selectArrayList,random); 
				for (int i = 0; (i<local_selectArrayList.size())&&(i<ratio); i++) {
					set1.add(drugString+" "+local_selectArrayList.get(i));
				}
			}
		}
		
		set1.removeAll(trainSet);
		set1.removeAll(testSet);
		
		/**
		 *  all negatives, no positives
		 */
		
		for (String string:set1) {
			candidateSet.add(string);
			String[] e=string.split(" ");
			if(candidateDrugTargets.containsKey(e[0])){
				candidateDrugTargets.get(e[0]).add(e[1]);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(e[1]);
				candidateDrugTargets.put(e[0], set);
			}
			
			if(candidateTargetDrugs.containsKey(e[1])){
				candidateTargetDrugs.get(e[1]).add(e[0]);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(e[0]);
				candidateTargetDrugs.put(e[1], set);
			}
		}
		
		System.out.println("testSet negatives: "+candidateSet.size());
		
		NegativeSetBean bean=new NegativeSetBean();
		bean.setCandidateDrugTargets(candidateDrugTargets);
		bean.setCandidateSet(candidateSet);
		bean.setCandidateTargetDrugs(candidateTargetDrugs);
		set1.clear();
		return bean;
	}


public NegativeSetBean generate_isolate_di(long seed, int ratio ){
	
	/**
	 *  marker
	 */
		candidateDrugTargets=new HashMap<>();
		candidateTargetDrugs=new HashMap<>();
		candidateSet=new HashSet<>();
		HashSet<String> set1=new HashSet<>();
		Random random = new Random(seed);
		
		for(String test:testSet) {
			String[] elementStrings=test.split(" ");
			String drugString=elementStrings[0];
			String targetString=elementStrings[1];
			
			int idx=random.nextInt(2);
			
			if(idx==0) {
				ArrayList<String> local_targets=new ArrayList<>();
				for(String target:isolated_targets) {
					local_targets.add(target);
				}
				Collections.shuffle(local_targets,random); 
				
				for (int i = 0; (i<local_targets.size())&&(i<ratio); i++) {
					set1.add(drugString+" "+local_targets.get(i));
				}
				
			}else {
				ArrayList<String> local_drugs=new ArrayList<>();
				for(String drug:isolated_drugs) {
					local_drugs.add(drug);
				}
				Collections.shuffle(local_drugs,random); 
				
				for (int i = 0; (i<local_drugs.size())&&(i<ratio); i++) {
					set1.add(local_drugs.get(i)+" "+targetString);
				}
			}
		}
		
		set1.removeAll(trainSet);
		set1.removeAll(testSet);
		
		/**
		 *  all negatives, no positives
		 */
		
		for (String string:set1) {
			candidateSet.add(string);
			String[] e=string.split(" ");
			if(candidateDrugTargets.containsKey(e[0])){
				candidateDrugTargets.get(e[0]).add(e[1]);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(e[1]);
				candidateDrugTargets.put(e[0], set);
			}
			
			if(candidateTargetDrugs.containsKey(e[1])){
				candidateTargetDrugs.get(e[1]).add(e[0]);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(e[0]);
				candidateTargetDrugs.put(e[1], set);
			}
		}
		
		System.out.println("testSet negatives: "+candidateSet.size());
		
		NegativeSetBean bean=new NegativeSetBean();
		bean.setCandidateDrugTargets(candidateDrugTargets);
		bean.setCandidateSet(candidateSet);
		bean.setCandidateTargetDrugs(candidateTargetDrugs);
		set1.clear();
		return bean;
	}

	public HashSet<String> readTrainAssociation(HashSet<String> positive_test) throws IOException {
		HashSet<String> map = new HashSet<>();
		for (String line : positive_test) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					map.add(s + " " + o);
					connected_nodes.add(s);
					connected_nodes.add(o);
					connected_drugs.add(s);
					connected_targets.add(o);
				}
			}
		}
		return map;
	}

	public void feedDrugTargetAssociation(HashSet<String> positive_train) throws IOException {
		drugTargets=new HashMap<>();
		targetDrugs=new HashMap<>();
		for(String line:positive_train) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					if(drugTargets.containsKey(s)){
						drugTargets.get(s).add(o);
					}else{
						HashSet<String> set=new HashSet<>();
						set.add(o);
						drugTargets.put(s, set);
					}
					
					if(targetDrugs.containsKey(o)){
						targetDrugs.get(o).add(s);
					}else{
						HashSet<String> set=new HashSet<>();
						set.add(s);
						targetDrugs.put(o, set);
					}
				}
			}	
		}
	}
	
	public HashSet<String> readTestAssociation(HashSet<String> positive_test) throws IOException {
		HashSet<String> map = new HashSet<>();
		for (String line : positive_test) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					map.add(s + " " + o);
					isolated_nodes.add(s);
					isolated_nodes.add(o);
					isolated_drugs.add(s);
					isolated_targets.add(o);
				}
			}
		}
		return map;
	}
}
