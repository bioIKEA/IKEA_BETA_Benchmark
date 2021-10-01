package data.process.node.diseases;

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

public class Sider2pharmgkb {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String,String> umls_1=Sider2pharmgkb.getPharmgkb("D:/data/drug-taget-network/Databases/data/input/pharmgkb_diseases.nq");
		HashMap<String,String> umls_2=Sider2pharmgkb.getSider("D:/data/drug-taget-network/Databases/data/input/sider-indications.nq");
		writeMapping(umls_1,umls_2,"D:/data/drug-taget-network/Databases/data/output/disease_sider_pharmgkb.nq");
	}
	
	public static HashMap<String,String> getPharmgkb(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> umls=new HashMap<>();
		HashSet<String> disease=new HashSet<>();
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
							&o.equals("<http://bio2rdf.org/pharmgkb_vocabulary:Disease>")){
						disease.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:x-umls>")
							&o.startsWith("<http://bio2rdf.org/umls")){
						String newo=o.substring(o.lastIndexOf(":")+1, o.length());
						umls.put(newo, s);
					}
				}
			}
		}
		System.out.println("Pharmgkb Umls: "+umls.size());
		System.out.println("Pharmgkb disease: "+disease.size());
		br.close();
		return umls;
	}
	
	public static HashMap<String,String> getSider(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> umls=new HashMap<>();
		HashSet<String> disease=new HashSet<>();
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
							&o.equals("<http://bio2rdf.org/sider_vocabulary:Drug-Indication-Association>")){
						disease.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/sider_vocabulary:indication>")
							&o.startsWith("<http://bio2rdf.org/meddra")){
						String newo=o.substring(o.lastIndexOf(":")+1, o.length());
						umls.put(newo, s);
					}
				}
			}
		}
		System.out.println("Sider umls: "+umls.size());
		System.out.println("Sider disease: "+disease.size());
		br.close();
		return umls;
	}
	
	public static void writeMapping(HashMap<String,String> uniprot_1,HashMap<String,String> uniprot_2,String outfile) throws IOException{
		int i=0;
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(Entry<String,String> entry:uniprot_1.entrySet()){
			if(uniprot_2.containsKey(entry.getKey())){
				bw.write(entry.getValue()+" <http://www.w3.org/2002/07/owl#sameAs> "+uniprot_2.get(entry.getKey())+" .\n");
			i++;
			}
		}
		System.out.println("Drugbank2sider mapping: "+i);
		bw.flush();
		bw.close();
	}
}
