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
import java.util.Set;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Pharmgkb2Uniprot_NCBI {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String,String> mapping=Pharmgkb2Uniprot_NCBI.getPharmkbg("D:/data/drug-taget-network/Databases/data/input/pharmgkb_genes.nq");
		writeMapping(mapping,"D:/data/drug-taget-network/Databases/data/output/target_pharmgkb_uniprot_ncbi.nq");
	}
	
	public static HashMap<String,String> getPharmkbg(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> uniprots=new HashMap<>();
		HashMap<String,String> ncbi=new HashMap<>();
		HashSet<String> targets=new HashSet<>();
		HashSet<String> predicate=new HashSet<>();
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
					predicate.add(p);
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/pharmgkb_vocabulary:Gene>")){
						targets.add(s);
					}
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-uniprot>")
							&o.startsWith("<http://bio2rdf.org/uniprot")){
						uniprots.put(s, o);
					}
					
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-ncbigene>")
							&o.startsWith("<http://bio2rdf.org/ncbigene")){
						ncbi.put(s, o);
					}
				}
			}
			
			if(line.contains("\"> ")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					predicate.add(p);
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/pharmgkb_vocabulary:Gene>")){
						targets.add(s);
					}
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-uniprot>")
							&o.startsWith("<http://bio2rdf.org/uniprot")){
						String newo=o.substring(0, o.lastIndexOf("\""))+">";
						uniprots.put(s, newo);
					}
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-ncbigene>")
							&o.startsWith("<http://bio2rdf.org/ncbigene")){
						String newo=o.substring(0, o.lastIndexOf("\""))+">";
						ncbi.put(s, newo);
					}
				}
			}
			
		}
		
		int i=0;
		int j=0;
		HashMap<String,String> mappings=new HashMap<>();
		
		Set<String> set=new HashSet<>();
		set.addAll(uniprots.keySet());
		set.addAll(ncbi.keySet());
		
		for(String string:set){
			if(uniprots.containsKey(string)){
				mappings.put(uniprots.get(string), string);
			i++;
			}else if(ncbi.containsKey(string)){
				mappings.put(ncbi.get(string), string);
			j++;
			}
		}
		System.out.println("Pharmkbg ncbi: "+j);
		System.out.println("Pharmkbg uniprots: "+i);
		System.out.println("Pharmkbg targets: "+targets.size());
		
		br.close();
		return mappings;
	}
	
	
	
	public static void writeMapping(HashMap<String,String> mapping,String outfile) throws IOException{
		int i=0;
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(Entry<String,String> entry:mapping.entrySet()){
			bw.write(entry.getValue()+" <http://www.w3.org/2002/07/owl#sameAs> "+entry.getKey()+" .\n");
		}
		
		System.out.println("pharmgkb 2 uniprot mapping: "+mapping.size());
		bw.flush();
		bw.close();
	}
}
