package data.process.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

public class Hprd {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		new Hprd().getProteinInterations("data/input/hprd/BINARY_PROTEIN_PROTEIN_INTERACTIONS.txt", "data/input/hprd/HPRD_ID_MAPPINGS.txt");
	}
	
	
	public HashMap<String,HashSet<String>> getProteinInterations(String interationfiles, String idsfiles) throws IOException{
		HashMap<String,HashSet<String>> mappings=getMappings(idsfiles);
		
		 HashMap<String,HashSet<String>> interations = new HashMap<>();
		 BufferedReader br = new BufferedReader(new FileReader(new File(interationfiles)));
		 String line=null;
		 while((line=br.readLine())!=null){
			 String[] elements=line.split("\t");
			 HashSet<String> sources=mappings.get(elements[1]);
			 HashSet<String> targets=mappings.get(elements[4]);
			 if(sources!=null&targets!=null){
				 for(String srouce:sources){
					 for(String target:targets){
						 
						 if(interations.containsKey(srouce)){
							 interations.get(srouce).add(target);
						 }else{
						 HashSet<String> set= new HashSet<>();
						 set.add(target);
						 interations.put(srouce, set);
						 }	 	 
						 
					 }
				 }
			 }
		 }
//		 for(Entry<String,HashSet<String>>entry:interations.entrySet()){
//			 System.out.println(entry);
//		 }
		 
		return interations;
	}
	
	
	public HashMap<String,HashSet<String>> getMappings( String idsfiles) throws IOException{
		 HashMap<String,HashSet<String>> mappings = new HashMap<>();
		 BufferedReader br = new BufferedReader(new FileReader(new File(idsfiles)));
		 String line=null;
		 while((line=br.readLine())!=null){
			 String[] elements=line.split("\t");
			 String[] uniprotids=elements[6].split(",");
			 for(String i:uniprotids){
				 String id="<http://bio2rdf.org/uniprot:"+i+">";
				 if(mappings.containsKey(elements[0])){
					 mappings.get(elements[0]).add(id);
				 }else{
					 HashSet<String> set= new HashSet<>();
					 set.add(id);
					 mappings.put(elements[0], set);
				 }	 
			 }
		 }
		return mappings;
	}
	
}
