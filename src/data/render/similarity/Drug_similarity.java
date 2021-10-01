package data.render.similarity;

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

import java_cup.internal_error;

public class Drug_similarity {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashSet<String> set_exisiting=pull_chemical_from_SimilarityFile() ;
		HashSet<String> set_all=pull_chemical_from_Drugbank();
		for(String string:set_all) {
			System.out.println(string);
			break;
		}
		for(String string:set_exisiting) {
			System.out.println(string);
			break;
		}
		
		System.out.println("set_all: "+set_all.size());
		System.out.println("set_exisiting: "+set_exisiting.size());
		set_all.retainAll(set_exisiting);
		System.out.println("set_remain: "+set_all.size());
	}

	public static HashSet<String> pull_chemical_from_SimilarityFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/output/datasets/orignial/drug_similarity.tsv")));
		String line=null;
		HashSet<String> drugSet=new HashSet<>();
		int i=0;
		while((line=br.readLine())!=null){
			i++;
			if(i>1) {
				String[] elementStrings=line.toLowerCase().split("\t");
				drugSet.add("<http://bio2rdf.org/drugbank:"+elementStrings[0]+">");
				drugSet.add("<http://bio2rdf.org/drugbank:"+elementStrings[1]+">");
			}
		}
		return drugSet;
	}
	
	
	public static HashSet<String> pull_chemical_from_Drugbank() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/input/done/drugbank.nq")));
		String line=null;
		HashSet<String> drugSet=new HashSet<>();
		
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:calculated-properties>")){
						drugSet.add(s);
					}
			}
		}
		return drugSet;
	}
}
