package data.render.node.drugs;

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

public class Drug_Offside2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String,HashSet<String>> pubchem_compound2drugbank_0 =offside2drugank
				(dataDir+"/input/done/drugbank.nq"); //offside2drugbank
		
		HashMap<String,HashSet<String>> pubchem_compound2drugbank_2=offside2drugank_viaKegg(dataDir+"/input/done/kegg-drug.nq", 
				dataDir+"/input/done/drugbank.nq");
		
		HashMap<String,HashSet<String>> pubchem_compound2drugbank_1=offside2drugank_viaPharGKB(dataDir+"/input/done/pharmgkb_drugs.nq", 
				dataDir+"/input/done/drugbank.nq");
		
		HashMap<String,HashSet<String>> pubchem_compound2drugbank=new HashMap<>();
		genrateMap(pubchem_compound2drugbank, pubchem_compound2drugbank_0);
		genrateMap(pubchem_compound2drugbank, pubchem_compound2drugbank_1);
		genrateMap(pubchem_compound2drugbank, pubchem_compound2drugbank_2);
		
		
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(output)));
		for(Entry<String,HashSet<String>> entry:pubchem_compound2drugbank.entrySet()){
				for(String string:entry.getValue()){
					bw.write(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string+" .\n");
			}
		}
		
		bw.flush();
		bw.close();
	}
	
	public static void genrateMap(HashMap<String,HashSet<String>> base, HashMap<String,HashSet<String>> input) {
		
		for(Entry<String, HashSet<String>> entry:input.entrySet()) {
			if(base.containsKey(entry.getKey())) {
				base.get(entry.getKey()).addAll(entry.getValue());
			}else {
				HashSet<String> set=new HashSet<>();
				for(String string:entry.getValue()) {
					set.add(string);
				}
				base.put(entry.getKey(), set);
			}
		}
	}
	
	public static HashMap<String,HashSet<String>> offside2drugank(String drugbankfile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(drugbankfile)));
		String line=null;
		HashMap<String,HashSet<String>> todrugBank=new HashMap<>();
		HashSet<String> drugs=new HashSet<>();
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
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-pubchemcompound>")
							&s.startsWith("<http://bio2rdf.org/drugbank:db")){
						if(todrugBank.containsKey(o)){
							todrugBank.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							todrugBank.put(o, set);
						}
					}
					
				}
			}
		}
		System.out.println("DrugBank drugbank: "+todrugBank.size());
		System.out.println("DrugBank drugs: "+drugs.size());
		br.close();
		return todrugBank;
	}

	
	public static HashMap<String,HashSet<String>> offside2drugank_viaKegg(String keggfile, String durgbankfile) throws IOException {
		
		HashMap<String,HashSet<String>> pubchemtokegg=new HashMap<>();
		HashMap<String,HashSet<String>> keggtodrugbank=new HashMap<>();
		HashMap<String,HashSet<String>> pubchemtodrugbank=new HashMap<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(keggfile)));
		String line=null;
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
					
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:x-pubchem.compound>")
							&s.startsWith("<http://bio2rdf.org/kegg:d")){
						if(pubchemtokegg.containsKey(o)){
							pubchemtokegg.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							pubchemtokegg.put(o, set);
						}
					}
					
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(new File(durgbankfile)));
		line=null;
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
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-kegg>")
							&o.startsWith("<http://bio2rdf.org/kegg:d")&&
							s.startsWith("<http://bio2rdf.org/drugbank:db")){
						if(keggtodrugbank.containsKey(o)){
							keggtodrugbank.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							keggtodrugbank.put(o, set);
						}
					}
					
				}
			}
		}
		br.close();
		
		
		for(Entry<String, HashSet<String>> entry_1:pubchemtokegg.entrySet()) {
			for(String kegg:entry_1.getValue()) {
				if(keggtodrugbank.containsKey(kegg)) {
					for(String drugbank:keggtodrugbank.get(kegg)) {
						if(pubchemtodrugbank.containsKey(entry_1.getKey())){
							pubchemtodrugbank.get(entry_1.getKey()).add(drugbank);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(drugbank);
							pubchemtodrugbank.put(entry_1.getKey(), set);
						}
					}
				}
			}
		}
		return pubchemtodrugbank;
	}
	
	
	
	public static HashMap<String,HashSet<String>> offside2drugank_viaPharGKB(String pharmgkbfile, String durgbankfile) throws IOException {
		
		HashMap<String,HashSet<String>> pubchemtopharmgkb=new HashMap<>();
		HashMap<String,HashSet<String>> pharmgkbtodrugbank=new HashMap<>();
		HashMap<String,HashSet<String>> pubchemtodrugbank=new HashMap<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(pharmgkbfile)));
		String line=null;
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
					
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-pubchemcompound>")
							&s.startsWith("<http://bio2rdf.org/pharmgkb:pa")){
						if(pubchemtopharmgkb.containsKey(o)){
							pubchemtopharmgkb.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							pubchemtopharmgkb.put(o, set);
						}
					}
					
					
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-drugbank>")
							&o.startsWith("http://bio2rdf.org/drugbank:db")){
						if(pharmgkbtodrugbank.containsKey(s)){
							pharmgkbtodrugbank.get(s).add(o);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(o);
							pharmgkbtodrugbank.put(s, set);
						}
					}
					
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(new File(durgbankfile)));
		line=null;
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
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-pharmgkb>")
							&o.startsWith("<http://bio2rdf.org/pharmgkb")){
						if(pharmgkbtodrugbank.containsKey(o)){
							pharmgkbtodrugbank.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							pharmgkbtodrugbank.put(o, set);
						}
					}
					
				}
			}
		}
		br.close();
		
		
		for(Entry<String, HashSet<String>> entry_1:pubchemtopharmgkb.entrySet()) {
			for(String pharmkgb:entry_1.getValue()) {
				if(pharmgkbtodrugbank.containsKey(pharmkgb)) {
					for(String drugbank:pharmgkbtodrugbank.get(pharmkgb)) {
						if(pubchemtodrugbank.containsKey(entry_1.getKey())){
							pubchemtodrugbank.get(entry_1.getKey()).add(drugbank);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(drugbank);
							pubchemtodrugbank.put(entry_1.getKey(), set);
						}
					}
				}
			}
		}
		return pubchemtodrugbank;
	}
}
