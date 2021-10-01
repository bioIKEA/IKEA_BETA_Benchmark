package data.process.nodes.targets;

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

public class DrugBank2Uniprot {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String,String> uniprot_2=DrugBank2Uniprot.getDrugBank("D:/data/drug-taget-network/Databases/data/input/drugbank.nq");
		writeMapping(uniprot_2,"D:/data/drug-taget-network/Databases/data/output/target_drugbank_uniprot.nq");
	}
	
	public static HashMap<String,String> getDrugBank(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> uniprots=new HashMap<>();
		HashSet<String> targets=new HashSet<>();
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/drugbank_vocabulary:Target>")){
						targets.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")
							&o.startsWith("<http://bio2rdf.org/uniprot")){
						uniprots.put(o, s);
					}
				}
			}
		}
		System.out.println("DrugBank uniprots: "+uniprots.size());
		System.out.println("DrugBank targets: "+targets.size());
		br.close();
		return uniprots;
	}
	
	public static void writeMapping(HashMap<String,String> uniprot_1,String outfile) throws IOException{
		int i=0;
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(Entry<String,String> entry:uniprot_1.entrySet()){
			bw.write(entry.getValue()+" <http://www.w3.org/2002/07/owl#sameAs> "+entry.getKey()+" .\n");
		}
		
		System.out.println("drugbank 2 uniprot mapping: "+uniprot_1.size());
		bw.flush();
		bw.close();
	}
}
