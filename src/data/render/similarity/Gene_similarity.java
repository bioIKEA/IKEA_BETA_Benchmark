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

public class Gene_similarity {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashSet<String> set_similarity=pull_gene_fromSimilarity();
		System.out.println(set_similarity.size());
	}
	
	public static HashSet<String> pull_gene_fromSimilarity() throws IOException {
		HashSet<String> gene_set=new HashSet<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/output/datasets/orignial/gene_similarity_matrix_cosine.txt")));
		String line=null;
		int i=0;
		while((line=br.readLine())!=null){
			i++;
			if(i==2) {
				String[] elements=line.split("	");
				for (int j = 3; j < elements.length; j++) {
					gene_set.add(elements[j]);
				}
			}
		}
		return gene_set;
	}
	
	public static HashSet<String> pull_gene_fromDrugBank() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/input/done/drugbank_v3.nq")));
		String line=null;
		HashSet<String> gene_set=new HashSet<>();
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:gene-sequence>")) {
						String value=o.substring(o.indexOf(" bp")+3,o.lastIndexOf("\""));
						value=value.replaceAll("\n", "");
						gene_set.add(s);
					}
			}
		}
		return gene_set;
	}

}
