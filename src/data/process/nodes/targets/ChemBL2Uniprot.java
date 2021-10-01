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

public class ChemBL2Uniprot {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String,String> uniprot_1=ChemBL2Uniprot.getChemBL("D:/data/drug-taget-network/Databases/data/input/chembl_23.0_target.nt",
				"D:/data/drug-taget-network/Databases/data/input/chembl_uniprot_mapping.txt");
		writeMapping(uniprot_1,"D:/data/drug-taget-network/Databases/data/output/target_chembl_uniprot.nq");
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
	
	public static void writeMapping(HashMap<String,String> uniprot_1,String outfile) throws IOException{
		int i=0;
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(Entry<String,String> entry:uniprot_1.entrySet()){
			bw.write(entry.getValue()+" <http://www.w3.org/2002/07/owl#sameAs> "+entry.getKey()+" .\n");
		}
		
		System.out.println("chembl 2 uniprot mapping: "+uniprot_1.size());
		bw.flush();
		bw.close();
	}
}
