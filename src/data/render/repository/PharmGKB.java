package data.render.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class PharmGKB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void extract(String relation_file,String drug_file, String disease_file, String gene_file, 
			String associationOut) throws IOException{
	
	BufferedReader br = new BufferedReader(new FileReader(new File(relation_file)));
	String line=null;
	HashSet<String> include=new HashSet<>();
	include.add("<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	include.add("<http://bio2rdf.org/pharmgkb_vocabulary:variantlocation>");
	include.add("<http://bio2rdf.org/pharmgkb_vocabulary:gene>");
	include.add("<http://bio2rdf.org/pharmgkb_vocabulary:haplotype>");
	include.add("<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	
	
	HashMap<String,HashSet<String>> resouce_type_map= new HashMap<>();
	HashMap<String,HashSet<String>> resouce_association_map= new HashMap<>();
	
	HashSet<String> printset=new HashSet<>();
	
	while((line=br.readLine())!=null){
		if(!line.contains("\"")){
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim().toLowerCase();
				String o = quard[2].toString().trim().toLowerCase();
				
				if(include.contains(p)) {
					if(resouce_association_map.containsKey(s)) {
						resouce_association_map.get(s).add(line);
					}else {
						HashSet<String> set=new HashSet<>();
						set.add(line);
						resouce_association_map.put(s, set);
					}
				}
				
				if(s.startsWith("<http://bio2rdf.org/pharmgkb_resource:")&& p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
						) {
					if(resouce_type_map.containsKey(o)) {
						resouce_type_map.get(o).add(s);
					}else {
						HashSet<String> set=new HashSet<>();
						set.add(s);
						resouce_type_map.put(o, set);
					}
				}
				
			}
		}
	}
	br.close();
	
	
	
	HashMap<String, HashMap<String,String>> control_map=new HashMap<>();
	HashMap<String,String> a_1=new HashMap<>();
	a_1.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_1.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:gene>");
	a_1.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:disease-gene-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:disease-gene-association>", a_1);
	
	HashMap<String,String> a_2=new HashMap<>();
	a_2.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_2.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:gene>");
	a_2.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:disease-gene-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:gene-disease-association>", a_2);
	
	HashMap<String,String> a_3=new HashMap<>();
	a_3.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_3.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:haplotype>");
	a_3.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:disease-haplotype-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:disease-haplotype-association>", a_3);
	
	HashMap<String,String> a_4=new HashMap<>();
	a_4.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_4.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:haplotype>");
	a_4.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:disease-haplotype-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:haplotype-disease-association>", a_4);
	
	HashMap<String,String> a_5=new HashMap<>();
	a_5.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_5.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:variantlocation>");
	a_5.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:drug-variantlocation-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:drug-variantlocation-association>", a_5);
	
	HashMap<String,String> a_6=new HashMap<>();
	a_6.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_6.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:variantlocation>");
	a_6.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:drug-variantlocation-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:variantlocation-drug-association>", a_6);
	
	HashMap<String,String> a_7=new HashMap<>();
	a_7.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_7.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:variantlocation>");
	a_7.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:disease-variantlocation-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:disease-variantlocation-association>", a_7);
	
	HashMap<String,String> a_8=new HashMap<>();
	a_8.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_8.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:variantlocation>");
	a_8.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:disease-variantlocation-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:variantlocation-disease-association>", a_8);
	
	HashMap<String,String> a_9=new HashMap<>();
	a_9.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_9.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:haplotype>");
	a_9.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:drug-haplotype-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:drug-haplotype-association>", a_9);
	
	HashMap<String,String> a_10=new HashMap<>();
	a_10.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_10.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:haplotype>");
	a_10.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:drug-haplotype-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:haplotype-drug-association>", a_10);
	
	HashMap<String,String> a_11=new HashMap<>();
	a_11.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_11.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:gene>");
	a_11.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:drug-gene-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:drug-gene-association>", a_11);
	
	HashMap<String,String> a_12=new HashMap<>();
	a_12.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_12.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:gene>");
	a_12.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:drug-gene-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:gene-drug-association>", a_12);
	
	
	HashMap<String,String> a_13=new HashMap<>();
	a_13.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_13.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:drug>");
	a_13.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:drug-drug-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:drug-drug-association>", a_13);
	
	HashMap<String,String> a_14=new HashMap<>();
	a_14.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_14.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:disease>");
	a_14.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:disease-disease-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:disease-disease-association>", a_14);
	
	HashMap<String,String> a_15=new HashMap<>();
	a_15.put("obj_1", "<http://bio2rdf.org/pharmgkb_vocabulary:gene>");
	a_15.put("obj_2", "<http://bio2rdf.org/pharmgkb_vocabulary:gene>");
	a_15.put("out", "<http://bio2rdf.org/pharmgkb_vocabulary:gene-gene-Association>");
	control_map.put("<http://bio2rdf.org/pharmgkb_vocabulary:gene-gene-association>", a_15);
	
	for(Entry<String, HashMap<String, String>> entry:control_map.entrySet()) {
		String associationType=entry.getKey();
		String obj_1_association=entry.getValue().get("obj_1");
		String obj_2_association=entry.getValue().get("obj_2");
		String outputassociationType=entry.getValue().get("out");
		
		System.out.println("associationType: "+ associationType);
		System.out.println("obj_1_association: "+ obj_1_association);
		System.out.println("obj_2_association: "+ obj_2_association);
		System.out.println("outputassociationType: "+ outputassociationType);
		System.out.println("resouce_type_map: "+ resouce_type_map.keySet());
		
		generatePrint(resouce_type_map,
				resouce_association_map,
				 associationType,
				 obj_1_association,
				 obj_2_association,
				 outputassociationType,
				printset);
	}
	
	
	BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(associationOut)));
	for(String string:printset){
		bw_1.write(string+"\n");
	}
	bw_1.flush();
	bw_1.close();
	
	}
	
	public static void generatePrint(HashMap<String,HashSet<String>> resouce_type_map,
			HashMap<String,HashSet<String>> resouce_association_map,
			String associationType,
			String obj_1_association,
			String obj_2_association,
			String outputassociationType,
			HashSet<String> printset) {
		/**
		 *  bug fixed 09/02/2021
		 */
		
		if(resouce_type_map.containsKey(associationType)) {
			for(String association:resouce_type_map.get(associationType)) {
				if(resouce_association_map.containsKey(association)) {
					String obj_1="";
					String obj_2="";
					for(String triple:resouce_association_map.get(association)) {
						InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
						NxParser nxp = new NxParser();
						nxp.parse(inputStream);
						while (nxp.hasNext()) {
							Node[] quard = nxp.next();
							String s = quard[0].toString().trim().toLowerCase();
							String p = quard[1].toString().trim().toLowerCase();
							String o = quard[2].toString().trim().toLowerCase();
						
							if(obj_1_association.equals(obj_2_association)) {
								if(p.equals(obj_1_association)){
									if(obj_1.equals("")) {
										obj_1=o;
									}else {
										obj_2=o;
									}
								}
							}else {
								if(p.equals(obj_1_association)) {
									obj_1=o;
								}
								if(p.equals(obj_2_association)) {
									obj_2=o;
								}
							}
						}
					}	
					
					printset.add(obj_1 + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + obj_1_association + " .");
					printset.add(obj_2 + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + obj_2_association + " .");
					
					if(obj_1_association.equals(obj_2_association)) {
						printset.add(association+" "+outputassociationType+" "+obj_1+" .");
						printset.add(association+" "+outputassociationType+" "+obj_2+" .");
						printset.add(associationType + " " + outputassociationType + " " + obj_1_association + " .");
					}else {
						printset.add(obj_1+" "+outputassociationType+" "+obj_2+" .");
						printset.add(obj_1_association + " " + outputassociationType + " " + obj_2_association + " .");
					}
					
				}
				
			}
		}
	
	}

}
