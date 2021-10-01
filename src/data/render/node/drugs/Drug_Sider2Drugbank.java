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

public class Drug_Sider2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
	}
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String,HashSet<String>> mapping_1=Drug_Sider2Drugbank.getSider
				(dataDir+"/input/done/sider_dump.nt"); //drugbank & dbpedia , sider
		HashMap<String,HashSet<String>> mapping_2=Drug_Sider2Drugbank.getDrugBank
				(dataDir+"/input/done/drugbank.nq");
		writeMapping(mapping_1,mapping_2,output);
	}
	
	public static HashMap<String,HashSet<String>> getDrugBank(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> mappings=new HashMap<>();
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
						String news=s.substring(s.lastIndexOf(":")+1, s.lastIndexOf(">"));
						if(mappings.containsKey(news)){
							mappings.get(news).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							mappings.put(news, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-wikipedia>")
							&o.startsWith("<http://bio2rdf.org/wikipedia:")){
						String newo=o.substring(o.lastIndexOf(":")+1, o.lastIndexOf(">")).toLowerCase();
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
		System.out.println("DrugBank potential mappings: "+mappings.size());
		System.out.println("DrugBank drugs: "+drugs.size());
		br.close();
		return mappings;
	}
	
	public static HashMap<String,HashSet<String>> getSider(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> mappings=new HashMap<>();
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
					
					if(p.equals("<http://www.w3.org/2002/07/owl#sameas>")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/sider/resource/drugs/")
							&o.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/")){
						drugs.add(s);
						
						String newo=o.substring(o.lastIndexOf("/")+1, o.lastIndexOf(">"));
						if(mappings.containsKey(newo)){
							mappings.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							mappings.put(newo, set);
						}
					}
					
					if(p.equals("<http://www.w3.org/2002/07/owl#sameas>")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/sider/resource/drugs/")
							&o.startsWith("<http://www.dbpedia.org/resource/")){
						drugs.add(s);
						String newo=o.substring(o.lastIndexOf("/")+1, o.lastIndexOf(">")).toLowerCase();
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
		System.out.println("Sider Compound: "+mappings.size());
		System.out.println("Sider drugs: "+drugs.size());
		br.close();
		return mappings;
	}
	
	public static void writeMapping(HashMap<String,HashSet<String>> mapping_1,HashMap<String,HashSet<String>> mapping_2,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> mappings=new HashSet<>();
		HashSet<String> nodes1=new HashSet<>();
		HashSet<String> nodes2=new HashSet<>();
		for(Entry<String,HashSet<String>> entry:mapping_1.entrySet()){
			if(mapping_2.containsKey(entry.getKey())){
				for(String value_1:entry.getValue()){
					for(String value_2:mapping_2.get(entry.getKey())){
						mappings.add(value_1+" <http://www.w3.org/2002/07/owl#sameAs> "+value_2);
						nodes1.add(value_1);
						nodes2.add(value_2);
					}
				}
			}
		}
		for(String mapping:mappings){
			bw.write(mapping+" .\n");		
		}
		System.out.println("@@@ Sider2Drugbank mapping: "+mappings.size());
		System.out.println("@@@ mapped sider: "+nodes1.size());
		System.out.println("@@@ mapped drugbank: "+nodes2.size());
		
		bw.flush();
		bw.close();
	}
}
