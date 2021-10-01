package data.process.map;

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
public class Diseasome {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("data/input/drugCloud/test.nt"))) ;
//		new Diseasome().writeOthers("data/input/diseasome/diseasome_dump.nt", bw,false);
//		new Diseasome().writeDGeneProteinAssociation("data/input/diseasome/diseasome_dump.nt", 
//				"data/input/hgnc/hgnc.nq","data/input/drugbank/drugbank_dump.nt",bw,false);
		
		
		new Diseasome().writeProteinInteractions("data/input/hprd/BINARY_PROTEIN_PROTEIN_INTERACTIONS.txt",
				"data/input/hprd/HPRD_ID_MAPPINGS.txt",bw);
		
//		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("data/input/drugCloud/diseasome.nt"))) ;
//		new Diseasome().writeIntegration("data/input/diseasome/diseasome_dump.nt", bw);
	}
	
	public void writeDGeneProteinAssociation(String diseasomefile, String hgncfile, String drugbankfile,BufferedWriter bw, Boolean writeType) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(diseasomefile)));
		String line=null;
		HashMap<String,String> geneHgnc=new HashMap<>();
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
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/")&o.startsWith("<http://")
							&p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/hgncId>")){
							geneHgnc.put(s, o);
					}
				}
			}
		}
		HashMap<String,String> hgncUniprot=new Hgnc().getHgncUniprotMapping(hgncfile);
		HashSet<String> unassociatedProtein=new HashSet<>();
		HashMap<String,String> uniprotassociatedProtein=new HashMap<>();
		int i=0;
		int j=0;
		for(Entry<String,String> entry:geneHgnc.entrySet()){
			if(hgncUniprot.containsKey(entry.getValue())){
				bw.write(entry.getKey()+" <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/associatedProtein> "+hgncUniprot.get(entry.getValue())+" .\n");
			}
		}
		if(writeType){
			for(String string:unassociatedProtein){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/proteins> .\n");
			}	
		}
		System.out.println("hgncUniprot: "+hgncUniprot.size());
		System.out.println("unassociatedProtein: "+unassociatedProtein.size());
		System.out.println("uniprotassociatedProtein: "+uniprotassociatedProtein.size());
		System.out.println("targets: "+i);
		System.out.println("generated proteins: "+j);
		bw.flush();
	}
	
	public void writeProteinInteractions(String proteinInteractionFile ,String proteinMappings,BufferedWriter bw ) throws IOException{
		HashMap<String,HashSet<String>> interactions=
				new Hprd().getProteinInterations(proteinInteractionFile, proteinMappings);
		
		
		
		for(Entry<String,HashSet<String>> entry:interactions.entrySet()){
			for(String string:entry.getValue()){
				bw.write(entry.getKey()+" <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/proteinInteractProtein> "+string+" .\n");		
			}
		}
		
		bw.flush();
	}
	
	public void writeDiseaseDrugAssociation(String input, BufferedWriter bw) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		
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
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")
							&o.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/")
							&p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibleDrug>")){
						
						bw.write(line+"\n");
					}
					
				}
			}
		}
		bw.flush();
	}
	
	public void writeOthers(String input, BufferedWriter bw, Boolean writeType) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;

		HashSet<String> properties = new HashSet<String>();
		properties.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibleDrug>"); //daileymed and drugbank
//		properties.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/omimPage>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/associatedGene>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseaseSubtypeOf>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/chromosomalLocation>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/class>");
		
				
		HashSet<String> diseases= new HashSet<>();
		HashSet<String> genes= new HashSet<>();
		HashSet<String> chromosomalLocation= new HashSet<>();
		HashSet<String> diseaseClass= new HashSet<>();
		
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
					
					if(s.startsWith("<http://")&o.startsWith("<http://")
							&properties.contains(p)){
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibleDrug>")){
							diseases.add(s);
							if(o.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/")){
								bw.write(s+" <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibleDrugBankDrug> "+o+" .\n");	
							}else{
								bw.write(s+" <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibleDailymedDrug> "+o+" .\n");
							}
								
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/omimPage>")){
							diseases.add(s);
//							bw.write(line+"\n");
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/associatedGene>")){
							diseases.add(s);
							genes.add(o);
							bw.write(line+"\n");
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseaseSubtypeOf>")){
							diseases.add(s);
							diseases.add(s);
							bw.write(line+"\n");
						}

						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/chromosomalLocation>")){
							diseases.add(s);
							chromosomalLocation.add(o);
							bw.write(line+"\n");
						}
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/class>")){
							diseases.add(s);
							diseaseClass.add(o);
							bw.write(line+"\n");
						}
					}
					
				}
			}
		}
		
		if(writeType){
			for(String string:diseases){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases> .\n");
			}
			for(String string:genes){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/genes> .\n");
			}
			for(String string:chromosomalLocation){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/chromosomalLocations> .\n");
			}
			for(String string:diseaseClass){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseaseClasses> .\n");
			}	
		}
		System.out.println(diseases.size());
		System.out.println(genes.size());
		System.out.println(chromosomalLocation.size());
		System.out.println(diseaseClass.size());
		
		bw.flush();
	}
	
	public HashMap<String,HashSet<String>> getOmimDisease(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> map=new HashMap<>();
		
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
					
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")
							&o.startsWith("<http://bio2rdf.org/omim:")
							&p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/omim>")
							){
						if(map.containsKey(o)){
							map.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							map.put(o, set);
						}
					}
					
				}
			}
			
		}
		return map;
	}
	
	public HashMap<String,HashSet<String>> getDiseaseHgnc(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		
		
		HashMap<String,HashSet<String>> map=new HashMap<>();
		
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
							&o.startsWith("<http://bio2rdf.org/hgnc:")
							&p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/hgncId>")
							){
						if(map.containsKey(o)){
							map.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							map.put(o, set);
						}
					}
					
				}
			}
			
		}
		return map;
	}
	
}
