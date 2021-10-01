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

import jsat.linear.distancemetrics.PearsonDistance;

public class Target_Omim2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String, HashSet<String>> toOmim=getfromhgnc
				(dataDir+"/input/done/hgnc_complete_set.nq") ; //uniprot&hgnc, hgnc_symbol
		
		HashMap<String, HashSet<String>> toDrugbankHashMap=getfromDrugbank(
				dataDir+"/input/done/drugbank.nq"); //uniprot&hgnc, drugbank
		HashSet<String> symbolSet=getSymbolfromOmim(dataDir+"/input/done/omim.nq");
		writeMapping(symbolSet,toOmim,toDrugbankHashMap,output) ;
	}
	
	
	public static void writeMapping(HashSet<String> symbolSet, HashMap<String,HashSet<String>> omim,HashMap<String,HashSet<String>> drugbank,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> lines=new HashSet<>();
		HashSet<String> om=new HashSet<>();
		HashSet<String> drug=new HashSet<>();
		
		for(Entry<String,HashSet<String>> entry:omim.entrySet()){
				if(drugbank.containsKey(entry.getKey())){
					for(String string1:entry.getValue()){
						if(symbolSet.contains(string1)) {
							for(String string2:drugbank.get(entry.getKey())){
								lines.add(string1+" <http://www.w3.org/2002/07/owl#sameAs> "+string2+" .");
								om.add(string1);
								drug.add(string2);		
							}	
						}
					}
			}	
		}
		
		for(String string:lines){
			bw.write(string+"\n");
		}
		
		System.out.println("@@@ kegg 2 drugbank mapping: "+lines.size());
		System.out.println("@@@ mapped kegg: "+om.size());
		System.out.println("@@@ mapped drug: "+drug.size());
		
		bw.flush();
		bw.close();
	}
	
	
	public static HashMap<String, HashSet<String>> getfromhgnc(String input) throws IOException{
		
		HashMap<String, HashSet<String>> map=new HashMap<>();
		HashMap<String, HashSet<String>> hgnc2uniprot=new HashMap<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(
				input)));
		String line=null;
		HashMap<String,HashSet<String>> uniprots=new HashMap<>();
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
					
					if(p.equals("<http://bio2rdf.org/hgnc_vocabulary:has-approved-symbol>")) {
						if(map.containsKey(s)) {
							map.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							map.put(s, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/hgnc_vocabulary:x-uniprot>")) {
						if(hgnc2uniprot.containsKey(s)) {
							hgnc2uniprot.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							hgnc2uniprot.put(s, set);
						}
					}
				}
			}
		}
		
		HashMap<String, HashSet<String>> tmp=new HashMap<>();
		for(Entry<String, HashSet<String>> entry:map.entrySet()) {
			if(hgnc2uniprot.containsKey(entry.getKey())) {
				for(String uniprot:hgnc2uniprot.get(entry.getKey())) {
					for(String symbol:entry.getValue()) {
						if(tmp.containsKey(uniprot)) {
							tmp.get(uniprot).add(symbol);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(symbol);
							tmp.put(uniprot, set);
						}
					}
				}
			}
		}
		map.putAll(tmp);
		return map;
	}
	
	public static HashSet<String> getSymbolfromOmim(String input) throws IOException{
		
		HashSet<String> set=new HashSet<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> uniprots=new HashMap<>();
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
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:genesymbols>")||
							p.equals("<http://bio2rdf.org/omim_vocabulary:gene-symbol>")||
							p.equals("<http://bio2rdf.org/omim_vocabulary:x-symbol>")) {
						set.add(o);
					}
					
				}
			}
		}
		return set;
	}
	
public static HashMap<String, HashSet<String>> getfromDrugbank(String input) throws IOException{
		
		HashMap<String, HashSet<String>> map=new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> uniprots=new HashMap<>();
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
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")&&
							s.startsWith("<http://bio2rdf.org/drugbank:be")) {
						if(map.containsKey(o)) {
							map.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							map.put(o, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-hgnc>")&&
							s.startsWith("<http://bio2rdf.org/drugbank:be")) {
						
						if(map.containsKey(o)) {
							map.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							map.put(o, set);
						}
					}
				}
			}
		}
		return map;
	}

}
