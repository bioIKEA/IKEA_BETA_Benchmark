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

public class Disease_Sider2Omim {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public static void writeMapping(String output) throws IOException {
		HashMap<String,HashSet<String>> umls_2=Disease_Sider2Omim.getOmim
				(dataDir+"/input/done/omim.nq"); // umls, omim
		HashMap<String,HashSet<String>> umls_1=Disease_Sider2Omim.getSider
		(dataDir+"/input/done/sider_dump.nt");
		writeMapping(umls_2,umls_1, output);
	}
	public static HashMap<String,HashSet<String>> getOmim(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> umls=new HashMap<>();
		HashSet<String> disease=new HashSet<>();
		HashSet<String> containUmls=new HashSet<>();
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
						containUmls.add(s);
						if(umls.containsKey(newo)){
							umls.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							umls.put(newo, set);
						}
					}
				}
			}
		}
		
		br.close();
		System.out.println("Omim Umls: "+umls.size());
		System.out.println("Omim disease: "+disease.size());

		
		br = new BufferedReader(new FileReader(new File(input)));
		line=null;
		HashMap<String,String> types=new HashMap<>();
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
							&containUmls.contains(s)){
						
						types.put(o, s);
					}
				}
			}
		}

		System.err.println(types);
		return umls;
	}
	
	public static HashMap<String,HashSet<String>> getSider(String input) throws IOException{
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
							&o.equals("<http://www4.wiwiss.fu-berlin.de/sider/resource/sider/side_effects>")){
						disease.add(s);
					}
				}
			}
		}
		for(String string:disease){
			String newo=string.substring(string.lastIndexOf("/")+1, string.length());
			if(umls.containsKey(newo)){
				umls.get(newo).add(string);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(string);
				umls.put(newo, set);
			}
		}
		
		System.out.println("Sider umls: "+umls.size());
		System.out.println("Sider disease: "+disease.size());
		br.close();
		return umls;
	}
	
	public static void writeMapping(HashMap<String,HashSet<String>> umls_1,HashMap<String,HashSet<String>> umls_2,String outfile) throws IOException{
		int i=0;
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(Entry<String,HashSet<String>> entry:umls_1.entrySet()){
				if(umls_2.containsKey(entry.getKey())){
					for(String string_1:entry.getValue()){
						for(String string_2:umls_2.get(entry.getKey())){
							bw.write(string_2+" <http://www.w3.org/2002/07/owl#sameAs> "+string_1+" .\n");		
							i++;
						}
					}
					
				}	
		}
		System.out.println("Sider2omim mapping: "+i);
		bw.flush();
		bw.close();
	}
	
	public static HashMap<String,HashSet<String>> getSiderToOmim(HashMap<String,HashSet<String>> umls_1,HashMap<String,HashSet<String>> umls_2) throws IOException{
		HashMap<String,HashSet<String>> mapping=new HashMap<>();
		int i=0;
		for(Entry<String,HashSet<String>> entry:umls_1.entrySet()){
				if(umls_2.containsKey(entry.getKey())){
					for(String string_1:entry.getValue()){
						for(String string_2:umls_2.get(entry.getKey())){
							if(mapping.containsKey(string_1)){
								mapping.get(string_1).add(string_2);
							}else{
								HashSet<String> set=new HashSet<>();
								set.add(string_2);
								mapping.put(string_1, set);
							}
							i++;
						}
					}
					
				}	
		}
		System.out.println("Sider2omim mapping: "+i);
		return mapping;
	}
}
