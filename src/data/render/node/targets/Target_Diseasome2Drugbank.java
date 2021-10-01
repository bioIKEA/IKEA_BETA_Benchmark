package data.render.node.targets;

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

import data.process.map.Hgnc;
import data.process.map.Omim;

public class Target_Diseasome2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

	}
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String, HashSet<String>> diseasome = Target_Diseasome2Drugbank
				.getDisease(dataDir+"/input/done/diseasome_dump.nt"); // Hgnc & bio2rdfSymbol, diseasome
		HashMap<String, HashSet<String>> drugbank = Target_Diseasome2Drugbank
				.getDrugBank(dataDir+"/input/done/drugbank.nq"); // uniprot& hngc& genatlas, drugbank
		Target_Diseasome2Drugbank.writeMapping(diseasome, drugbank,
				output);
	}
	public static HashMap<String,HashSet<String>> getDisease(String input1) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input1)));
		String line=null;
		HashMap<String,HashSet<String>> mappings=new HashMap<>();
		HashSet<String> targets=new HashSet<>();
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
							&o.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/genes>")){
						targets.add(s);
					}
//					if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/omim>")
//							&o.startsWith("<http://bio2rdf.org/omim:")
//							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")){
//						if(mappings.containsKey(o)){
//							mappings.get(o).add(s);
//						}else{
//							HashSet<String> set=new HashSet<>();
//							set.add(s);
//							mappings.put(o, set);
//						}
//					}
					if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/hgncid>")
							&o.startsWith("<http://bio2rdf.org/hgnc:")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/")){
						if(mappings.containsKey(o)){
							mappings.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							mappings.put(o, set);
						}
					}
					if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/bio2rdfsymbol>")
							&o.startsWith("<http://symbol.bio2rdf.org/symbol:")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/")){
						String newo=o.substring(o.lastIndexOf(":")+1, o.lastIndexOf(">"));
						
						if(mappings.containsKey(newo)){
							mappings.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							mappings.put(newo, set);
						}
					}
					
				}
			}
		}
		
		System.out.println("diseasome mappings: "+mappings.size());
		System.out.println("diseasome targets: "+targets.size());
		br.close();
		return mappings;
	}
	
	public static HashMap<String,HashSet<String>> getDrugBank(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		Omim omim= new Omim();
		HashMap<String,HashSet<String>> uniprotOmim=omim.getUniprotOmim(dataDir+"/input/done/omim.nq");
		
		HashMap<String,HashSet<String>> mappings=new HashMap<>();
		HashSet<String> targets=new HashSet<>();
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
							&o.equals("<http://bio2rdf.org/drugbank_vocabulary:target>")){
						targets.add(s);
					}
					
//					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")
//							&o.startsWith("<http://bio2rdf.org/uniprot:")
//							&s.startsWith("<http://bio2rdf.org/drugbank:be")){
//						if(uniprotOmim.containsKey(o)){
//							for(String string:uniprotOmim.get(o)){
//								if(mappings.containsKey(string)){
//									mappings.get(string).add(s);
//								}else{
//									HashSet<String> set=new HashSet<>();
//									set.add(s);
//									mappings.put(string,set);
//								}
//							}
//						}
//					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-hgnc>")
							&o.startsWith("<http://bio2rdf.org/hgnc:")
							&s.startsWith("<http://bio2rdf.org/drugbank:be")){
						
						if(mappings.containsKey(o)){
							mappings.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							mappings.put(o,set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-genatlas>")
							&o.startsWith("<http://bio2rdf.org/genatlas:")
							&s.startsWith("<http://bio2rdf.org/drugbank:be")){
						String newo=o.substring(o.lastIndexOf(":")+1, o.lastIndexOf(">"));
						
						if(mappings.containsKey(newo)){
							mappings.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							mappings.put(newo,set);
						}
					}
				}
			}
		}
		System.out.println("DrugBank 3rd party: "+mappings.size());
		System.out.println("DrugBank targets: "+targets.size());
		br.close();
		return mappings;
	}
	
	
	
	public static void writeMapping(HashMap<String,HashSet<String>> diseasome,HashMap<String,HashSet<String>> drugbank,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> set=new HashSet<>();
		HashSet<String> genes=new HashSet<>();
		
		HashSet<String> dis=new HashSet<>();
		HashSet<String> drug=new HashSet<>();
		
		for(Entry<String,HashSet<String>> entry:diseasome.entrySet()){
				if(drugbank.containsKey(entry.getKey())){
					for(String string1:entry.getValue()){
						for(String string2:drugbank.get(entry.getKey())){
							set.add(string1+" <http://www.w3.org/2002/07/owl#sameAs> "+string2);
							genes.add(string1);
							dis.add(string1);
							drug.add(string2);		  
						}
					}
				}	
		}
		
		for(String string:set){
			bw.write(string+" .\n");
		}
		
		System.out.println("@@@ diseasome drugbank mapping: "+set.size());
		System.out.println("@@@ mapped diseasome: "+dis.size());
		System.out.println("@@@ mapped drugbank: "+drug.size());
		
		bw.flush();
		bw.close();
	}
}
