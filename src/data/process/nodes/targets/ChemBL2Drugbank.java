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

public class ChemBL2Drugbank {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String,String> chembl=ChemBL2Drugbank.getChemBL("D:/data/drug-taget-network/Databases/data/input/chembl_23.0_target.nt",
				"D:/data/drug-taget-network/Databases/data/input/chembl_uniprot_mapping.txt");
		HashMap<String,String> drugbank=ChemBL2Drugbank.getDrugBank("D:/data/drug-taget-network/Databases/data/input/drugbank.nq");
		writeMapping(chembl,drugbank,"D:/data/drug-taget-network/Databases/data/output/target_chembl_drugbank.nq");
	}
	
	public static HashMap<String,String> getChemBL(String input1, String input2) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input1)));
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
							&o.equals("<http://rdf.ebi.ac.uk/terms/chembl#SingleProtein>")){
						targets.add(s);
					}
				}
			}
		}
		
		br = new BufferedReader(new FileReader(new File(input2)));
		line=null;
		while((line=br.readLine())!=null){
			if(!line.contains("# chembl_23 target list")){
				String[] elements=line.split("	");
				String s="<http://bio2rdf.org/uniprot:"+elements[0]+">";
				String o="<http://rdf.ebi.ac.uk/resource/chembl/target/"+elements[0]+">";
				uniprots.put(s, o);
			}
		}
		
		
		System.out.println("ChemBL uniprots: "+uniprots.size());
		System.out.println("ChemBL targets: "+targets.size());
		br.close();
		return uniprots;
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
	
	
	public static void writeMapping(HashMap<String,String> chembl,HashMap<String,String> drugbank,String outfile) throws IOException{
		int i=0;
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(Entry<String,String> entry:chembl.entrySet()){
				if(drugbank.containsKey(entry.getKey())){
					bw.write(entry.getValue()+" <http://www.w3.org/2002/07/owl#sameAs> "+drugbank.get(entry.getKey())+" .\n");		
					i++;
				}	
		}
		System.out.println("pharmgkb 2 drugbank mapping: "+i);
		bw.flush();
		bw.close();
	}
}
