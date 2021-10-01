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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.netlib.util.booleanW;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import data.render.node.features.Generate_feature;
import java_cup.internal_error;
import jsat.classifiers.DDAG;

public class NegativeGenerator {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	}
	
	public HashSet<String> test_drugs;
	public HashSet<String> test_targets;
	public HashSet<String> connected_drugs; //exclude testNode
	public HashSet<String> connected_targets; //exclude testNode
	public HashSet<String> all_drugs; //exclude testNode,connectedNode
	public HashSet<String> all_targets; //exclude testNode,connectedNode
	
	public HashSet<String> universe_drugs;
	public HashSet<String> universe_targets;
	
	
	public HashMap<String,Boolean> trainSet;
	public HashMap<String,Boolean> testSet;
	
	
	public HashMap<String,HashSet<String>> drugTargets;
	public HashMap<String,HashSet<String>> targetDrugs;
	
	public HashMap<String,HashSet<String>> candidateDrugTargets;
	
	public HashMap<String, Boolean> getTestSet() {
		return testSet;
	}

	public void setTestSet(HashMap<String, Boolean> testSet) {
		this.testSet = testSet;
	}

	
	public HashMap<String, HashSet<String>> getCandidateDrugTargets() {
		return candidateDrugTargets;
	}

	public void setCandidateDrugTargets(HashMap<String, HashSet<String>> candidateDrugTargets) {
		this.candidateDrugTargets = candidateDrugTargets;
	}

	public HashMap<String, HashSet<String>> getCandidateTargetDrugs() {
		return candidateTargetDrugs;
	}

	public void setCandidateTargetDrugs(HashMap<String, HashSet<String>> candidateTargetDrugs) {
		this.candidateTargetDrugs = candidateTargetDrugs;
	}

	public HashSet<String> getCandidateSet() {
		return candidateSet;
	}

	public void setCandidateSet(HashSet<String> candidateSet) {
		this.candidateSet = candidateSet;
	}

	public HashMap<String,HashSet<String>> candidateTargetDrugs;
	public HashSet<String> candidateSet;
	
	
	
public NegativeSetBean generate(long seed, Double ratio,String type, HashSet<String> similarDrugs,HashSet<String> positive_train, HashSet<String> positive_test ){
		
		/**
		 *  marker
		 */
			candidateDrugTargets=new HashMap<>();
			candidateTargetDrugs=new HashMap<>();
			candidateSet=new HashSet<>();
			HashSet<String> set1=new HashSet<>();
			
			HashSet<String> local_drugSet_1 = similarDrugs;
			HashSet<String> local_targetSet_1 = new HashSet<>();;
			
			
			if(type.equals("tt")) {
				for(String triple:positive_test) {
					InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
					NxParser nxp = new NxParser();
					nxp.parse(inputStream);
					while (nxp.hasNext()) {
						Node[] quard = nxp.next();
						String s = quard[0].toString().trim();
						String p = quard[1].toString().trim();
						String o = quard[2].toString().trim();
						if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
							local_targetSet_1.add(o);
						} 
					}	
				}
			}
			
			if(type.equals("trt")) {
				for(String triple:positive_train) {
					InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
					NxParser nxp = new NxParser();
					nxp.parse(inputStream);
					while (nxp.hasNext()) {
						Node[] quard = nxp.next();
						String s = quard[0].toString().trim();
						String p = quard[1].toString().trim();
						String o = quard[2].toString().trim();
						if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
							local_targetSet_1.add(o);
						} 
					}	
				}
			}
			
			
			if((local_drugSet_1!=null)&&(local_targetSet_1!=null) ) {
				for(String drug:local_drugSet_1){
					for(String target:local_targetSet_1){
						if(!testSet.containsKey(drug+" "+target)){
							if(!trainSet.containsKey(drug+" "+target)) {
								set1.add(drug+" "+target);
							}
						}
					}
				}
			}
			
			
			set1.removeAll(trainSet.keySet());
			set1.removeAll(testSet.keySet());
			
			/**
			 *  all negatives, no positives
			 */
			
			Random random = new Random(seed);
			
			List<String> list=new ArrayList<String>();
			for(String string:set1){
				list.add(string);
			}
			
			Collections.shuffle(list,random); 
			
//			System.out.println("testSet negatives: "+set1.size());
			for (int i = 0; (i < list.size())&&(i<testSet.size()*ratio); i++) {
				candidateSet.add(list.get(i));
				String[] e=list.get(i).split(" ");
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
			
			System.out.println(type+" "+"testSet negatives: "+candidateSet.size());
			
			NegativeSetBean bean=new NegativeSetBean();
			bean.setCandidateDrugTargets(candidateDrugTargets);
			bean.setCandidateSet(candidateSet);
			bean.setCandidateTargetDrugs(candidateTargetDrugs);
			set1.clear();
			return bean;
		}

	public HashSet<String> getDrugsWithSmile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/smile.txt")));
		String line=null;
		HashSet<String> drugs=new HashSet<>();
		while((line=br.readLine())!=null){
			String[] elements=line.split("\t");
			drugs.add(elements[0].toLowerCase());
		}
		return drugs;
	}

	public HashSet<String> geTargetsWithSequence() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/sequence.txt")));
		String line=null;
		HashSet<String> targets=new HashSet<>();
		while((line=br.readLine())!=null){
			String[] elements=line.split("\t");
			targets.add(elements[0].toLowerCase());
		}
		return targets;
	}


	public NegativeGenerator (HashSet<String> positive_train, HashSet<String> positive_test) throws IOException{
		
		test_drugs=new HashSet<>();
		test_targets=new HashSet<>();
		connected_drugs=new HashSet<>();
		connected_targets=new HashSet<>();
		all_drugs=new HashSet<>();
		all_targets=new HashSet<>();
		universe_drugs=new HashSet<>();
		universe_targets=new HashSet<>();
		
		readAllNode();
		testSet=readTestAssociation(positive_test);
		trainSet=readTrainAssociation(positive_train);
		
		connected_drugs.removeAll(test_drugs);
		connected_targets.removeAll(test_targets);
		
		all_drugs.removeAll(test_drugs);
		all_drugs.removeAll(connected_drugs);
		
		all_targets.removeAll(test_targets);
		all_targets.removeAll(connected_targets);
		
		universe_drugs.addAll(test_drugs);
		universe_drugs.addAll(connected_drugs);
		universe_drugs.addAll(all_drugs);
		
		
		universe_targets.addAll(test_targets);
		universe_targets.addAll(connected_targets);
		universe_targets.addAll(all_targets);
		
		
		HashSet<String> drugs=getDrugsWithSmile();
		HashSet<String> targets=geTargetsWithSequence();
		
		universe_drugs.retainAll(drugs);
		universe_targets.retainAll(targets);
		
		System.out.println("train postive: "+trainSet.size());
		System.out.println("test positive: "+testSet.size());
		
		System.out.println("all_drugs: "+all_drugs.size());
		System.out.println("connected_drugs: "+connected_drugs.size());
		System.out.println("test_drugs: "+test_drugs.size());
		
		System.out.println("all_targets: "+all_drugs.size());
		System.out.println("connected_targets: "+connected_targets.size());
		System.out.println("test_targets: "+test_targets.size());
		/**
		 * 	tt
		 * 	tc
		 * 	ta
		 * 	cc
		 * 	ca
		 * 	aa
		 * 
		 */
		
	}
	
	
	
public NegativeSetBean generate_general(long seed, Double ratio){
		
		/**
		 *  marker
		 */
			candidateDrugTargets=new HashMap<>();
			candidateTargetDrugs=new HashMap<>();
			candidateSet=new HashSet<>();
			HashSet<String> set1=new HashSet<>();
			
			ArrayList<String> local_drugSet= new ArrayList<>();
			ArrayList<String> local_targetSet = new ArrayList<>();
			
			for(String string:universe_drugs) {
				local_drugSet.add(string);
			}
			
			for(String string:universe_targets) {
				local_targetSet.add(string);
			}
			
			Random random = new Random(seed);
			Collections.shuffle(local_drugSet,random); 
			Collections.shuffle(local_targetSet,random); 
			
			while (candidateSet.size()<(testSet.size()*ratio)) {
				int drug_idx=random.nextInt(local_drugSet.size());
				int target_idx=random.nextInt(local_targetSet.size());
				String drug=local_drugSet.get(drug_idx);
				String target=local_targetSet.get(target_idx);
				if(!testSet.containsKey(drug+" "+target)){
					if(!trainSet.containsKey(drug+" "+target)) {
						candidateSet.add(drug+" "+target);
					}
					if(candidateDrugTargets.containsKey(drug)){
						candidateDrugTargets.get(drug).add(target);
					}else{
						HashSet<String> set=new HashSet<>();
						set.add(target);
						candidateDrugTargets.put(drug, set);
					}
					
					if(candidateTargetDrugs.containsKey(target)){
						candidateTargetDrugs.get(target).add(drug);
					}else{
						HashSet<String> set=new HashSet<>();
						set.add(drug);
						candidateTargetDrugs.put(target, set);
					}
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

	public NegativeSetBean generate(long seed, Double ratio, String type ){
		
		/**
		 *  marker
		 */
			candidateDrugTargets=new HashMap<>();
			candidateTargetDrugs=new HashMap<>();
			candidateSet=new HashSet<>();
			HashSet<String> set1=new HashSet<>();
			
			HashSet<String> local_drugSet_1 = null;
			HashSet<String> local_targetSet_1 = null;
			
			HashSet<String> local_drugSet_2 = null;
			HashSet<String> local_targetSet_2 = null;
			
			if(type.equals("tt")) {
				local_drugSet_1=test_drugs;
				local_targetSet_1=test_targets;
			}
			if(type.equals("tc")) {
				local_drugSet_1=test_drugs;
				local_targetSet_1=connected_targets;
				
				local_drugSet_2=connected_drugs;
				local_targetSet_2=test_targets;
			}
			if(type.equals("ta")) {
				local_drugSet_1=test_drugs;
				local_targetSet_1=all_targets;
				
				local_drugSet_2=all_drugs;
				local_targetSet_2=test_targets;
			}
			
			
			if(type.equals("cc")) {
				local_drugSet_1=connected_drugs;
				local_targetSet_1=connected_targets;
			}
			
			if(type.equals("ca")) {
				local_drugSet_1=connected_drugs;
				local_targetSet_1=all_targets;
				
				local_drugSet_2=all_drugs;
				local_targetSet_2=connected_targets;
			}
			
			if(type.equals("aa")) {
				local_drugSet_1=all_drugs;
				local_targetSet_1=all_targets;
			}
			
			if((local_drugSet_1!=null)&&(local_targetSet_1!=null) ) {
				for(String drug:local_drugSet_1){
					for(String target:local_targetSet_1){
						if(!testSet.containsKey(drug+" "+target)){
							if(!trainSet.containsKey(drug+" "+target)) {
								set1.add(drug+" "+target);
							}
						}
					}
				}
			}
			
			if((local_drugSet_2!=null)&&(local_targetSet_2!=null) ) {
				for(String drug:local_drugSet_2){
					for(String target:local_targetSet_2){
						if(!testSet.containsKey(drug+" "+target)){
							if(!trainSet.containsKey(drug+" "+target)) {
								set1.add(drug+" "+target);
							}
						}
					}
				}
			}
			
			set1.removeAll(trainSet.keySet());
			set1.removeAll(testSet.keySet());
			
			/**
			 *  all negatives, no positives
			 */
			
			Random random = new Random(seed);
			
			List<String> list=new ArrayList<String>();
			for(String string:set1){
				list.add(string);
			}
			
			Collections.shuffle(list,random); 
			
//			System.out.println("testSet negatives: "+set1.size());
			for (int i = 0; (i < list.size())&&(i<testSet.size()*ratio); i++) {
				candidateSet.add(list.get(i));
				String[] e=list.get(i).split(" ");
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
			
			System.out.println(type+" "+"testSet negatives: "+candidateSet.size());
			
			NegativeSetBean bean=new NegativeSetBean();
			bean.setCandidateDrugTargets(candidateDrugTargets);
			bean.setCandidateSet(candidateSet);
			bean.setCandidateTargetDrugs(candidateTargetDrugs);
			set1.clear();
			return bean;
		}
	
	public ArrayList<String> toArrayList(HashSet<String> set){
		ArrayList<String> list=new ArrayList<>();
		for(String string:set) {
			list.add(string);
		}
		return list;
	}
	public NegativeSetBean generate_bigdata(long seed, Double ratio, String type ){
		
		/**
		 *  marker
		 */
			candidateDrugTargets=new HashMap<>();
			candidateTargetDrugs=new HashMap<>();
			candidateSet=new HashSet<>();
			HashSet<String> set1=new HashSet<>();
			
			HashSet<String> local_drugSet_1 = new HashSet<>();
			HashSet<String> local_targetSet_1 = new HashSet<>();
			
			HashSet<String> local_drugSet_2 = new HashSet<>();
			HashSet<String> local_targetSet_2 = new HashSet<>();
			
			
			if(type.equals("tt")) {
				local_drugSet_1=test_drugs;
				local_targetSet_1=test_targets;
			}
			if(type.equals("tc")) {
				local_drugSet_1=test_drugs;
				local_targetSet_1=connected_targets;
				
				local_drugSet_2=connected_drugs;
				local_targetSet_2=test_targets;
			}
			if(type.equals("ta")) {
				local_drugSet_1=test_drugs;
				local_targetSet_1=all_targets;
				
				local_drugSet_2=all_drugs;
				local_targetSet_2=test_targets;
			}
			
			
			if(type.equals("cc")) {
				local_drugSet_1=connected_drugs;
				local_targetSet_1=connected_targets;
			}
			
			if(type.equals("ca")) {
				local_drugSet_1=connected_drugs;
				local_targetSet_1=all_targets;
				
				local_drugSet_2=all_drugs;
				local_targetSet_2=connected_targets;
			}
			
			if(type.equals("aa")) {
				local_drugSet_1=all_drugs;
				local_targetSet_1=all_targets;
			}
			
			ArrayList<String> local_drugList_1= toArrayList(local_drugSet_1);
			ArrayList<String> local_targetList_1= toArrayList(local_targetSet_1);	
			
			ArrayList<String> local_drugList_2= toArrayList(local_drugSet_2);
			ArrayList<String> local_targetList_2= toArrayList(local_targetSet_2);	
			
			
			int data_size=(int) (testSet.size()*ratio);
			Random random = new Random(seed);
			Collections.shuffle(local_drugList_1,random); 
			Collections.shuffle(local_drugList_2,random); 
			Collections.shuffle(local_targetList_1,random); 
			Collections.shuffle(local_targetList_2,random); 
			
			while(set1.size()<data_size) {
				if((local_drugSet_1.size()>0)&&(local_targetSet_1.size()>0) ) {
					boolean run_1=true;
					while(run_1) {
						int idx_1=random.nextInt(local_drugList_1.size());
						int idx_2=random.nextInt(local_targetList_1.size());
						String drug=local_drugList_1.get(idx_1);
						String target=local_targetList_1.get(idx_2);
						if(!testSet.containsKey(drug+" "+target)){
							if(!trainSet.containsKey(drug+" "+target)) {
								set1.add(drug+" "+target);
								run_1=false;
							}
						}	
					}
					if((local_drugSet_2.size()>0)&&(local_targetSet_2.size()>0) ) {
						boolean run_2=true;
						while(run_2) {
							int idx_3=random.nextInt(local_drugList_2.size());
							int idx_4=random.nextInt(local_targetList_2.size());
							String drug=local_drugList_2.get(idx_3);
							String target=local_targetList_2.get(idx_4);
							if(!testSet.containsKey(drug+" "+target)){
								if(!trainSet.containsKey(drug+" "+target)) {
									set1.add(drug+" "+target);
									run_2=false;
								}
							}	
						}	
					}
				}
			}
			/**
			 *  all negatives, no positives
			 */
			
			for (String string: set1) {
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
			
			System.out.println(type+" "+"testSet negatives: "+candidateSet.size());
			
			NegativeSetBean bean=new NegativeSetBean();
			bean.setCandidateDrugTargets(candidateDrugTargets);
			bean.setCandidateSet(candidateSet);
			bean.setCandidateTargetDrugs(candidateTargetDrugs);
			set1.clear();
			return bean;
		}
	
	public void readAllNode() throws IOException {
		
		HashSet<String> drugs=getDrugsWithSmile();
		HashSet<String> targets=geTargetsWithSequence();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/input/done/drugbank.nq")));
		String line=null;
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
					
					if(s.startsWith("<http://bio2rdf.org/drugbank:db")&&drugs.contains(s)) {
						all_drugs.add(s);
					}
					if(o.startsWith("<http://bio2rdf.org/drugbank:db")&&drugs.contains(o)) {
						all_drugs.add(o);
					}
					if(s.startsWith("<http://bio2rdf.org/drugbank:be")&&targets.contains(s)) {
						all_targets.add(s);
					}
					if(o.startsWith("<http://bio2rdf.org/drugbank:be")&&targets.contains(o)) {
						all_targets.add(o);
					}
					
				}
			}
		}
		br.close();
	}


	public HashMap<String,Boolean> readTrainAssociation(HashSet<String> positive_test) throws IOException {
		HashMap<String,Boolean> map = new HashMap<>();
		for(String line:positive_test) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					map.put(s + " " + o,true);
					connected_drugs.add(s);
					connected_targets.add(o);
				}
			}	
		}
		return map;
	}
	
	public HashMap<String,Boolean> readTestAssociation(HashSet<String> positive_test) throws IOException {
		HashMap<String,Boolean> map = new HashMap<>();
		for(String line:positive_test) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					map.put(s + " " + o,true);
					test_drugs.add(s);
					test_targets.add(o);
				}
			}	
		}
		return map;
	}
	
	public void feedDrugTargetAssociation(HashSet<String> positive_test) throws IOException {
		drugTargets=new HashMap<>();
		targetDrugs=new HashMap<>();
		for(String line:positive_test) {
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

}
