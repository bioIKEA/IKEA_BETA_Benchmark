package data.process.map;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;


public class KeggGeneMapping {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public HashMap<String,String> getKeggGeneToHGNCMapping(String keggGene) throws IOException{
		BufferedReader br= new BufferedReader(new FileReader(new File(keggGene)));
		String line=null;
		
		HashMap<String,String> keggToHgnc = new HashMap<String,String>();
		
		while((line=br.readLine())!=null){
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				
				if(s.startsWith("<http://bio2rdf.org/kegg:")&o.startsWith("<http://bio2rdf.org/hgnc:")&p.startsWith("<http://bio2rdf.org/kegg_vocabulary:x-hgnc>"))	
				{
					keggToHgnc.put(s, o);
				}
				
			}
		}
		return keggToHgnc;
	}
	
	public HashMap<String,String> getDiseasomeGeneToHGNCMapping(String diseasome) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(diseasome)));
		String line=null;
		HashMap<String,String> hgncDisease = new HashMap<String,String>();
		HashSet<String> genes=new HashSet<>();
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
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/")
							&o.startsWith("<http://bio2rdf.org/hgnc:")&p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/hgncId>")){
						hgncDisease.put(o, s);
					}
					
				}
			}
			
		}
		return hgncDisease;
	}
	
}
