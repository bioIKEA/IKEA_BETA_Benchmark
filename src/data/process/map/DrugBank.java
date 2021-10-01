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
public class DrugBank {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("data/input/drugCloud/drugbank.nt"))) ;
		new DrugBank().writeDrugOthers("data/input/drugbank/drugbank_dump.nt",bw,false);
	}

	public HashMap<String,String> getDrugBankUniprotMapping(String drugbankfile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(drugbankfile)));
		String line=null;
		HashMap<String,String> targetUniprot=new HashMap<>();
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
					
					if(p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotId>")&
							s.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/targets/")){
						targetUniprot.put(s, o);
					}
				}
			}
		}
		return targetUniprot;
	}
	public void writeDrugOthers(String input, BufferedWriter bw, Boolean writeType) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		
		HashSet<String> properties = new HashSet<String>();
		properties.add("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/enzyme>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugType>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotId>");
				
		HashMap<String,String> interaction1= new HashMap<>();
		HashMap<String,String> interaction2= new HashMap<>();
		HashMap<String,String> enzymeProtein=new HashMap<>();
		HashSet<String> drugs=new HashSet<>();
		HashSet<String> enzymes=new HashSet<>();
		HashSet<String> drugCategorys=new HashSet<>();
		HashSet<String> drugTypes=new HashSet<>();
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
					
					if(s.startsWith("<http://")&o.startsWith("<http://")
							&properties.contains(p)){
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1>")){
							drugs.add(o);
							interaction1.put(s, o);
						}
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2>")){
							drugs.add(o);
							interaction2.put(s, o);
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/enzyme>")){
							drugs.add(s);
							enzymes.add(o);
							bw.write(line+"\n");
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory>")){
							drugs.add(s);
							drugCategorys.add(o);
							bw.write(line+"\n");
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugType>")){
							drugs.add(s);
							drugTypes.add(o);
							bw.write(line+"\n");
						}

						if(p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>")){
							drugs.add(s);
							targets.add(o);
							bw.write(line+"\n");
						}
						if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/enzymes/")&
								p.equals("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotId>")){
						}
						bw.write(s+" <http://www.w3.org/2002/07/owl#sameAs> "+o+" .\n");
					}
					
				}
			}
		}
		
		
		
		for(Entry<String,String> entry:interaction1.entrySet()){
			if(interaction2.containsKey(entry.getKey())){
				bw.write(entry.getValue()+" <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interact> "+interaction2.get(entry.getKey())+" .\n");
			}
		}
	
		if(writeType){
			for(String string:drugs){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugs> .\n");
			}
			for(String string:enzymes){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/enzyms> .\n");
			}
			for(String string:drugCategorys){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategorys> .\n");
			}
			for(String string:drugTypes){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugTypes> .\n");
			}
			for(String string:targets){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/targets> .\n");
			}	
		}
		
		System.out.println(drugs.size());
		System.out.println(enzymes.size());
		System.out.println(drugCategorys.size());
		System.out.println(drugTypes.size());
		System.out.println(targets.size());
		
		bw.flush();
	}
}
