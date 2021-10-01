package data.render.node.targets;

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

import org.bounce.message.Publisher;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Target_Kegg2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String,HashSet<String>> kegg=Target_Kegg2Drugbank.getKegg
				(dataDir+"/input/done/kegg-genes.nq"); // uniprot, kegg
		HashMap<String,HashSet<String>> drugbank=Target_Kegg2Drugbank.getDrugBank
				(dataDir+"/input/done/drugbank.nq"); // uniprot, drugbank
		writeMapping(kegg,drugbank,output);
	}
	
	public static HashMap<String,HashSet<String>> getKegg(String input) throws IOException{
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
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/kegg_vocabulary:gene>")){
						targets.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:x-uniprot>")
							&o.startsWith("<http://bio2rdf.org/uniprot")){
						if(uniprots.containsKey(o)){
							uniprots.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							uniprots.put(o,set);
						}
					}
				}
			}
		}
		
		System.out.println("kegg uniprots: "+uniprots.size());
		System.out.println("kegg targets: "+targets.size());
		
		br.close();
		return uniprots;
	}
	
	public static HashMap<String,HashSet<String>> getDrugBank(String input) throws IOException{
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
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://bio2rdf.org/drugbank_vocabulary:target>")){
						targets.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")
							&o.startsWith("<http://bio2rdf.org/uniprot")&
							s.startsWith("<http://bio2rdf.org/drugbank:be")
							){
						if(uniprots.containsKey(o)){
							uniprots.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							uniprots.put(o,set);
						}
					}
				}
			}
		}
		System.out.println("DrugBank uniprots: "+uniprots.size());
		System.out.println("DrugBank targets: "+targets.size());
		br.close();
		return uniprots;
	}
	
	
	public static void writeMapping(HashMap<String,HashSet<String>> kegg,HashMap<String,HashSet<String>> drugbank,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> lines=new HashSet<>();
		HashSet<String> ke=new HashSet<>();
		HashSet<String> drug=new HashSet<>();
		
		for(Entry<String,HashSet<String>> entry:kegg.entrySet()){
				if(drugbank.containsKey(entry.getKey())){
					for(String string1:entry.getValue()){
						for(String string2:drugbank.get(entry.getKey())){
							lines.add(string1+" <http://www.w3.org/2002/07/owl#sameAs> "+string2+" .");
							ke.add(string1);
							drug.add(string2);		
						}
					}
				}	
		}
		
		for(String string:lines){
			bw.write(string+"\n");
		}
		
		System.out.println("@@@ kegg 2 drugbank mapping: "+lines.size());
		System.out.println("@@@ mapped kegg: "+ke.size());
		System.out.println("@@@ mapped drug: "+drug.size());
		
		bw.flush();
		bw.close();
	}
}
