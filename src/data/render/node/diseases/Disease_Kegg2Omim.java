package data.render.node.diseases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Disease_Kegg2Omim {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	}
	
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String,HashSet<String>> mapping=Disease_Kegg2Omim.getKegg(dataDir+"/input/done/kegg-disease.nq");
		
		writeMapping(mapping,output);
	}
	
	public static void getOmim(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> disease=new HashSet<>();
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
					
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/omim_vocabulary:phenotype>")){
						disease.add(s);
					}
				}
			}
		}
		System.out.println("Omim disease: "+disease.size());
		br.close();
	}
	
	
	public static HashMap<String,HashSet<String>> getKegg(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> mappings=new HashMap<>();
		HashSet<String> disease=new HashSet<>();
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
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/kegg_vocabulary:disease>")){
						disease.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:x-omim>")
							&o.startsWith("<http://bio2rdf.org/omim")
							&s.startsWith("<http://bio2rdf.org/kegg:h")){
						if(mappings.containsKey(s)){
							mappings.get(s).add(o);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(o);
							mappings.put(s, set);
						}
					}
				}
			}
		}
		System.out.println("mappings: "+mappings.size());
		System.out.println("Omim disease: "+disease.size());
		br.close();
		return mappings;
	}
	
	
	public static void writeMapping(HashMap<String,HashSet<String>> mappings,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> lines=new HashSet<>();
		HashSet<String> kegg=new HashSet<>();
		HashSet<String> omim=new HashSet<>();
		for(Entry<String,HashSet<String>> entry:mappings.entrySet()){
			for(String string:entry.getValue()){
				lines.add(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string+" .");
				kegg.add(entry.getKey());
				omim.add(string);
			}
		}
		
	        for(String string:lines){
	        	bw.write(string+"\n");
	        }
		
		
	        System.out.println("@@@@ kegg to Omim mapping: "+lines.size());
			System.out.println("@@@@ kegg mapped: "+kegg.size());
			System.out.println("@@@@ Omim mapped: "+omim.size());
		bw.flush();
		bw.close();
	}
}
