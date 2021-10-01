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

import org.apache.jena.sparql.function.library.leviathan.tan;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Disease_Pharmgkb2Omim {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String,HashSet<String>> umls_1=Disease_Pharmgkb2Omim.
				getPharmgkb(dataDir+"/input/done/pharmgkb_diseases.nq"); //umls,pharmgkb
		
		HashMap<String,HashSet<String>> umls_2=Disease_Pharmgkb2Omim.
				getOmim(dataDir+"/input/done/omim.nq");
		
		writeMapping(umls_1,umls_2,output);
	}
	
	public static HashMap<String,HashSet<String>> getPharmgkb(String input) throws IOException{
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
							&o.equals("<http://bio2rdf.org/pharmgkb_vocabulary:disease>")){
						disease.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-umls>")
							&o.startsWith("<http://bio2rdf.org/umls")){
						String newo=o.substring(o.lastIndexOf(":")+1, o.length());
						if(umls.containsKey(newo)){
							umls.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							umls.put(newo, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-snomedct>")
							&o.startsWith("<http://bio2rdf.org/snomedct:")){
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
		System.out.println("Pharmgkb Umls: "+umls.size());
		System.out.println("Pharmgkb disease: "+disease.size());
		br.close();
		return umls;
	}
	
	public static HashMap<String,HashSet<String>> getOmim(String input) throws IOException{
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
						String newo=o.substring(o.lastIndexOf(":")+1, o.length());
						if(umls.containsKey(newo)){
							umls.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							umls.put(newo, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:x-snomed>")
							&o.startsWith("<http://bio2rdf.org/snomedct:")){
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
		System.out.println("Omim Umls: "+umls.size());
		System.out.println("Omim disease: "+disease.size());
		br.close();
		return umls;
	}
	
	
	public static void writeMapping(HashMap<String,HashSet<String>> umls_1,HashMap<String,HashSet<String>> umls_2,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> lines=new HashSet<>();
		HashSet<String> pharggkb=new HashSet<>();
		HashSet<String> omim=new HashSet<>();
		
		for(Entry<String,HashSet<String>> entry:umls_1.entrySet()){
				if(umls_2.containsKey(entry.getKey())){
					for(String string_1:entry.getValue()){
						for(String string_2:umls_2.get(entry.getKey())){
							lines.add(string_1+" <http://www.w3.org/2002/07/owl#sameAs> "+string_2+" .");
							pharggkb.add(string_1);
							omim.add(string_2);
						}
					}
					
				}	
		}
		for(String string:lines){
        	bw.write(string+"\n");
        }
	
        System.out.println("@@@@ pharggkb to Omim mapping: "+lines.size());
		System.out.println("@@@@ pharggkb mapped: "+pharggkb.size());
		System.out.println("@@@@ Omim mapped: "+omim.size());
		
		bw.flush();
		bw.close();
	}
}
