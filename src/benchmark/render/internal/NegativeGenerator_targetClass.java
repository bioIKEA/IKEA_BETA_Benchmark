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

import java_cup.internal_error;
import jsat.classifiers.DDAG;

public class NegativeGenerator_targetClass {
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
	
	
	
	
public NegativeSetBean generate(long seed, Double ratio,String type, HashSet<String> similarTargets,HashSet<String> positive_train, HashSet<String> positive_test){
		
		/**
		 *  marker
		 */
			candidateDrugTargets=new HashMap<>();
			candidateTargetDrugs=new HashMap<>();
			candidateSet=new HashSet<>();
			
			HashSet<String> local_drugSet_1 = new HashSet<>();
			HashSet<String> local_targetSet_1 = new HashSet<>();;
			
			
			if(type.equals("tt")) {
				local_targetSet_1 = similarTargets;
				
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
							local_drugSet_1.add(s);
						} 
					}	
				}
			}
			
			if(type.equals("trt")) {
				local_targetSet_1 = similarTargets;
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
							local_drugSet_1.add(s);
						} 
					}	
				}
				
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
							if(local_drugSet_1.contains(s)) {
								local_drugSet_1.remove(s);								
							}
						} 
					}	
				}
			}
			
			
			if(type.equals("te")) {
				
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
							local_drugSet_1.add(s);
						} 
					}	
				}
				
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
				
				local_targetSet_1.removeAll(similarTargets);
			}
			
			if(type.equals("tre")) {
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
							local_drugSet_1.add(s);
						} 
					}	
				}
				
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
							if(local_drugSet_1.contains(s)) {
								local_drugSet_1.remove(s);
							}
						} 
					}	
				}
				local_targetSet_1.removeAll(similarTargets);
			}
			
			
			List<String> drug_list=new ArrayList<String>();
			for(String string:local_drugSet_1){
				drug_list.add(string);
			}
			
			List<String> target_list=new ArrayList<String>();
			for(String string:local_targetSet_1){
				target_list.add(string);
			}
			
			Random random = new Random(seed);
			
			Collections.shuffle(drug_list,random); 
			Collections.shuffle(target_list,random); 
			HashSet<String> set1=new HashSet<>();
			
				for(String drug:drug_list){
					for(String target:target_list){
						if(!testSet.containsKey(drug+" "+target)){
							if(!trainSet.containsKey(drug+" "+target)) {
								if(set1.size()<testSet.size()*ratio) {
									String string=drug+" "+target;
									set1.add(string);
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
								}else {
									break;
								}
							}
						}
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


//public NegativeSetBean generate(long seed, Double ratio,String type, HashSet<String> similarTargets,HashSet<String> positive_train, HashSet<String> positive_test ){
//		
//		/**
//		 *  marker
//		 */
//			candidateDrugTargets=new HashMap<>();
//			candidateTargetDrugs=new HashMap<>();
//			candidateSet=new HashSet<>();
//			HashSet<String> set1=new HashSet<>();
//			
//			HashSet<String> local_drugSet_1 = new HashSet<>();
//			HashSet<String> local_targetSet_1 = new HashSet<>();;
//			
//			
//			if(type.equals("tt")) {
//				local_targetSet_1 = similarTargets;
//				for(String triple:positive_test) {
//					InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
//					NxParser nxp = new NxParser();
//					nxp.parse(inputStream);
//					while (nxp.hasNext()) {
//						Node[] quard = nxp.next();
//						String s = quard[0].toString().trim();
//						String p = quard[1].toString().trim();
//						String o = quard[2].toString().trim();
//						if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
//							local_drugSet_1.add(s);
//						} 
//					}	
//				}
//			}
//			
//			if(type.equals("trt")) {
//				local_targetSet_1 = similarTargets;
//				for(String triple:positive_train) {
//					InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
//					NxParser nxp = new NxParser();
//					nxp.parse(inputStream);
//					while (nxp.hasNext()) {
//						Node[] quard = nxp.next();
//						String s = quard[0].toString().trim();
//						String p = quard[1].toString().trim();
//						String o = quard[2].toString().trim();
//						if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
//							local_drugSet_1.add(s);
//						} 
//					}	
//				}
//			}
//			
//			if(type.equals("te")) {
//				for(String triple:positive_test) {
//					InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
//					NxParser nxp = new NxParser();
//					nxp.parse(inputStream);
//					while (nxp.hasNext()) {
//						Node[] quard = nxp.next();
//						String s = quard[0].toString().trim();
//						String p = quard[1].toString().trim();
//						String o = quard[2].toString().trim();
//						if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
//							local_drugSet_1.add(s);
//							local_targetSet_1.add(o);
//						} 
//					}	
//				}
//				local_targetSet_1.removeAll(similarTargets);
//			}
//			
//			if(type.equals("tre")) {
//				for(String triple:positive_train) {
//					InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
//					NxParser nxp = new NxParser();
//					nxp.parse(inputStream);
//					while (nxp.hasNext()) {
//						Node[] quard = nxp.next();
//						String s = quard[0].toString().trim();
//						String p = quard[1].toString().trim();
//						String o = quard[2].toString().trim();
//						if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
//							local_drugSet_1.add(s);
//							local_targetSet_1.add(o);
//						} 
//					}	
//				}
//				local_targetSet_1.removeAll(similarTargets);
//			}
//			
//			
//			if((local_drugSet_1!=null)&&(local_targetSet_1!=null) ) {
//				for(String drug:local_drugSet_1){
//					for(String target:local_targetSet_1){
//						if(!testSet.containsKey(drug+" "+target)){
//							if(!trainSet.containsKey(drug+" "+target)) {
//								set1.add(drug+" "+target);
//							}
//						}
//					}
//				}
//			}
//			
//			
//			set1.removeAll(trainSet.keySet());
//			set1.removeAll(testSet.keySet());
//			
//			/**
//			 *  all negatives, no positives
//			 */
//			
//			Random random = new Random(seed);
//			
//			List<String> list=new ArrayList<String>();
//			for(String string:set1){
//				list.add(string);
//			}
//			
//			Collections.shuffle(list,random); 
//			
////			System.out.println("testSet negatives: "+set1.size());
//			for (int i = 0; (i < list.size())&&(i<testSet.size()*ratio); i++) {
//				candidateSet.add(list.get(i));
//				String[] e=list.get(i).split(" ");
//				if(candidateDrugTargets.containsKey(e[0])){
//					candidateDrugTargets.get(e[0]).add(e[1]);
//				}else{
//					HashSet<String> set=new HashSet<>();
//					set.add(e[1]);
//					candidateDrugTargets.put(e[0], set);
//				}
//				
//				if(candidateTargetDrugs.containsKey(e[1])){
//					candidateTargetDrugs.get(e[1]).add(e[0]);
//				}else{
//					HashSet<String> set=new HashSet<>();
//					set.add(e[0]);
//					candidateTargetDrugs.put(e[1], set);
//				}
//			}
//			
//			System.out.println(type+" "+"testSet negatives: "+candidateSet.size());
//			
//			NegativeSetBean bean=new NegativeSetBean();
//			bean.setCandidateDrugTargets(candidateDrugTargets);
//			bean.setCandidateSet(candidateSet);
//			bean.setCandidateTargetDrugs(candidateTargetDrugs);
//			set1.clear();
//			return bean;
//		}




	public NegativeGenerator_targetClass (HashSet<String> positive_train, HashSet<String> positive_test) throws IOException{
		
		test_drugs=new HashSet<>();
		test_targets=new HashSet<>();
		connected_drugs=new HashSet<>();
		connected_targets=new HashSet<>();
		all_drugs=new HashSet<>();
		all_targets=new HashSet<>();
		
		readAllNode();
		testSet=readTestAssociation(positive_test);
		trainSet=readTrainAssociation(positive_train);
		
		connected_drugs.removeAll(test_drugs);
		connected_targets.removeAll(test_targets);
		
		all_drugs.removeAll(test_drugs);
		all_drugs.removeAll(connected_drugs);
		
		all_targets.removeAll(test_targets);
		all_targets.removeAll(connected_targets);
		
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
	
	
	public void readAllNode() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/input/done/drugbank.nq")));
		String line=null;
		HashSet<String> targets=geTargetsWithSequence();
		HashSet<String> drugs=getDrugsWithSmile();
		
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
