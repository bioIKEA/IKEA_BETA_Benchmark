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

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import jsat.regression.StochasticGradientBoosting;

public class Target_GOA2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, HashSet<String>> uniprot2drugbank=getUniprot2Drugbank(
				dataDir+"/input/done/drugbank.nq");
		Target_GOA2Drugbank.extract(dataDir+"/input/done/goa_human.nq",
				uniprot2drugbank,
				dataDir+"/output/target_mapping_goa.nq");
	}
	
	
public static void writeMapping(String output) throws IOException {
	HashMap<String, HashSet<String>> uniprot2drugbank=getUniprot2Drugbank(
			dataDir+"/input/done/drugbank.nq");
	Target_GOA2Drugbank.extract(dataDir+"/input/done/goa_human.nq",
			uniprot2drugbank,
			output);
}

public static HashMap<String, HashSet<String>> getUniprot2Drugbank(String drugbank_file) throws IOException{
	HashMap<String, HashSet<String>> map=new HashMap<>();
	
	BufferedReader br = new BufferedReader(new FileReader(new File(drugbank_file)));
	String line=null;
	HashSet<String> sameAs=new HashSet<String>();
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
				if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")
						&&s.startsWith("<http://bio2rdf.org/drugbank:be")
						&&o.startsWith("<http://bio2rdf.org/uniprot:")) {
					
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
	br.close();
	return map;
}
	
public static void extract(String input, HashMap<String, HashSet<String>> uniprot2drugbank, String outfile_2) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> sameAs=new HashSet<String>();
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
					if(p.equals("<http://bio2rdf.org/goa_vocabulary:function>")
							&&s.startsWith("<http://bio2rdf.org/uniprot:")
							&&o.startsWith("<http://bio2rdf.org/go:")) {
						if(uniprot2drugbank.containsKey(s)) {
							for(String string:uniprot2drugbank.get(s)) {
								sameAs.add(s+" "+"<http://www.w3.org/2002/07/owl#sameAs>"+" "+string+" .");
							}
						}
						
					}
					if(p.equals("<http://bio2rdf.org/goa_vocabulary:component>")
							&&s.startsWith("<http://bio2rdf.org/uniprot:")
							&&o.startsWith("<http://bio2rdf.org/go:")) {
						if(uniprot2drugbank.containsKey(s)) {
							for(String string:uniprot2drugbank.get(s)) {
								sameAs.add(s+" "+"<http://www.w3.org/2002/07/owl#sameAs>"+" "+string+" .");
							}
						}
					}
				}
			}
		}
		br.close();
		
		BufferedWriter bw_2 =new BufferedWriter(new FileWriter(new File(outfile_2)));
		for(String string:sameAs){
			bw_2.write(string+"\n");
		}
		bw_2.flush();
		bw_2.close();
	}
}
