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

public class Drug_PharmGKB2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
	}
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String,HashSet<String>> drugbank_1=Drug_PharmGKB2Drugbank.getPharmGKB
				(dataDir+"/input/done/pharmgkb_drugs.nq"); //pharmgkb, drugbank
		HashMap<String,HashSet<String>> drugbank_2=Drug_PharmGKB2Drugbank.getDrugBank
				(dataDir+"/input/done/drugbank.nq"); //pharmgkb, drugbank
		writeMapping(drugbank_1,drugbank_2,output);
	}
	
	public static HashMap<String,HashSet<String>> getDrugBank(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
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
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/drugbank_vocabulary:drug>")){
						drugs.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-pharmgkb>")
							&o.startsWith("<http://bio2rdf.org/pharmgkb")){
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
	
	public static HashMap<String,HashSet<String>> getPharmGKB(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
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
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/pharmgkb_vocabulary:drug>")){
						drugs.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-drugbank>")
							&o.startsWith("<http://bio2rdf.org/drugbank")){
						if(todrugBank.containsKey(s)){
							todrugBank.get(s).add(o);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(o);
							todrugBank.put(s, set);
						}
					}
				}
			}
		}
		System.out.println("PharmGKB drugbank: "+todrugBank.size());
		System.out.println("PharmGKB drugs: "+drugs.size());
		br.close();
		return todrugBank;
	}
	
	public static void writeMapping(HashMap<String,HashSet<String>> drugbank_1,HashMap<String,HashSet<String>> drugbank_2,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> mappings=new HashSet<>();
		HashSet<String> nodes1=new HashSet<>();
		HashSet<String> nodes2=new HashSet<>();
		
		for(Entry<String,HashSet<String>> entry:drugbank_1.entrySet()){
			HashSet<String> tmp=new HashSet<>();
			for(String string:entry.getValue()){
				tmp.add(string);
			}
			if(drugbank_2.containsKey(entry.getKey())){
				for(String string:drugbank_2.get(entry.getKey())){
					tmp.add(string);
				}
			}
			for(String string:tmp){
				mappings.add(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string);
				nodes1.add(entry.getKey());
				nodes1.add(string);
			}
		}
		
		for(Entry<String,HashSet<String>> entry:drugbank_2.entrySet()){
			HashSet<String> tmp=new HashSet<>();
			for(String string:entry.getValue()){
				tmp.add(string);
			}
			if(drugbank_1.containsKey(entry.getKey())){
				for(String string:drugbank_2.get(entry.getKey())){
					tmp.add(string);
				}
			}
			for(String string:tmp){
				mappings.add(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string);
				nodes2.add(entry.getKey());
				nodes2.add(string);
			}
		}
		
		for(String mapping:mappings){
			bw.write(mapping+" .\n");		
		}
		System.out.println("@@@ Drugbank2PharmGKB mapping: "+mappings.size());
		System.out.println("@@@ mapped pharm: "+nodes1.size());
		System.out.println("@@@ mapped drugbank: "+nodes2.size());
		bw.flush();
		bw.close();
	}
}
