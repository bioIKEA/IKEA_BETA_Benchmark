package data.render.repository;

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

public class Diseasome {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		extract(dataDir+"/input/done/diseasome_dump.nt",
				dataDir+"/output/association_diseasome.nq") ;
		
	}
	
			public static void extract(String input,String associationOut) throws IOException{
			
			HashMap<String, HashSet<String>> hgnc_uniprot=	HGNC.hgncToUniprot(dataDir+"/input/done/hgnc_complete_set.nq");
			
			HashSet<String> diseasesHashSet=new HashSet<>();
			HashSet<String> drugHashSet=new HashSet<>();
			HashSet<String> geneHashSet=new HashSet<>();
			HashSet<String> diseasesClassHashSet=new HashSet<>();
			HashSet<String> chromosomallocationHashSet=new HashSet<>();
			
			BufferedReader br = new BufferedReader(new FileReader(new File(input)));
			String line=null;
			HashSet<String> ps=new HashSet<>();
			
			
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
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasesubtypeof>")
								&&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases")
								&&o.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases")) {
							ps.add(s+" "+"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease_subtype-disease>"+" "+o+" .");
							diseasesHashSet.add(s);
							diseasesHashSet.add(o);
						}
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/chromosomallocation>")
								&&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases")
								&&o.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/chromosomalLocation")) {
							ps.add(s+" "+"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease-chromosomal_location>"+" "+o+" .");
							diseasesHashSet.add(s);
							chromosomallocationHashSet.add(o);
						}
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/associatedgene>")
								&&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases")
								&&o.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes")) {
							ps.add(s+" "+"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease-target>"+" "+o+" .");
							diseasesHashSet.add(s);
							geneHashSet.add(o);
						}
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/class>")
								&&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases")
								&&o.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseaseclass")) {
							ps.add(s+" "+"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease-class>"+" "+o+" .");
							diseasesHashSet.add(s);
							diseasesClassHashSet.add(o);
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibledrug>")
								&&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases")
								&&o.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs")) {
							ps.add(o+" "+"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:drug-disease>"+" "+s+" .");
							drugHashSet.add(o);
							diseasesHashSet.add(s);
						}
					}
				}
			}
			br.close();
			
			
		for(String chromosomallocation:chromosomallocationHashSet ) {
			ps.add(chromosomallocation + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:chromosomal_locations>" + " .");
		}
		for(String disease:diseasesHashSet ) {
			ps.add(disease + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:diseases>" + " .");
		}
		for(String gene:geneHashSet ) {
			ps.add(gene + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:genes>" + " .");
		}
		for(String disease_class:diseasesClassHashSet ) {
			ps.add(disease_class + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease_classes>" + " .");
		}
		for(String drug:drugHashSet ) {
			ps.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:drugs>" + " .");
		}
		
		ps.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:diseases> "
				+ "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease-chromosomal_location> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:chromosomal_locations> .");
		ps.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:diseases> "
				+ "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease_subtype-disease> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:diseases> .");
		ps.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:diseases> "
				+ "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease-target> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:genes> .");
		ps.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:diseases> "
				+ "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease-class> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:disease_classes> .");

		ps.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:drugs> "
				+ "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:drug-disease> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:diseases> .");

		
		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(associationOut)));
		for(String string:ps){
			bw_1.write(string+"\n");
			if(string.contains("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseasome_vocabulary:drug-disease>")) {
				System.err.println(string);
			}
		}
		bw_1.flush();
		bw_1.close();
			
		}
		
}
