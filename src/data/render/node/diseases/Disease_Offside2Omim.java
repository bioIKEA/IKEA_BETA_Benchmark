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

public class Disease_Offside2Omim {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public static void writeMapping(String output) throws IOException {
		getOmim(dataDir+"/input/done/omim.nq",
				output);
	}
	public static void getOmim(String input, String output) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> umls=new HashMap<>();
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
							&o.equals("<http://bio2rdf.org/omim_vocabulary:characteristic>")){
						disease.add(s);
					}
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
								&o.equals("<http://bio2rdf.org/omim_vocabulary:phenotype>")){
							disease.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:x-umls>")
							&o.startsWith("<http://bio2rdf.org/umls")){
						if(umls.containsKey(o)){
							umls.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							umls.put(o, set);
						}
					}
				}
			}
		}
		
		br.close();
		System.out.println("Omim Umls: "+umls.size());
		System.out.println("Omim disease: "+disease.size());
		
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(output)));
		for(Entry<String,HashSet<String>> entry:umls.entrySet()){
				for(String string:entry.getValue()) {
					bw.write(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string+" .\n");			
				}
		}
		bw.flush();
		bw.close();
	}
	
	
}
