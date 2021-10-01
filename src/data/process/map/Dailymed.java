package data.process.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Dailymed {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("data/input/drugCloud/dailymed.nt"))) ;
		new Dailymed().writeIntegration("data/input/dailymed/dailymed_dump.nt",bw,false);
	}
	
	public void writeIntegration(String input, BufferedWriter bw,Boolean writeType) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		
		HashSet<String> properties = new HashSet<String>();
		properties.add("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/routeOfAdministration>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/activeIngredient>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/activeMoiety>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/possibleDiseaseTarget>");
		properties.add("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/genericDrug>");
				
		HashSet<String> drugs= new HashSet<>();
		HashSet<String> ingredient= new HashSet<>();
		HashSet<String> administration= new HashSet<>();
		
		
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
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/drugs/")&o.startsWith("<http://")
							&properties.contains(p)){
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/routeOfAdministration>")){
							drugs.add(s);
							administration.add(o);
							bw.write(line+"\n");
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/activeIngredient>")){
							drugs.add(s);
							ingredient.add(o);
							bw.write(line+"\n");
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/activeMoiety>")){
							drugs.add(s);
							ingredient.add(o);
							bw.write(line+"\n");
						}
						
						if(p.equals("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/possibleDiseaseTarget>")){
							drugs.add(s);
//							bw.write(line+"\n"); 这段在diseasome中控制
						}

						if(p.equals("<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/genericDrug>")){
							drugs.add(s);
							bw.write(line+"\n");
						}
					}
					
				}
			}
		}
		
		if(writeType){
			for(String string:drugs){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/drugs> .\n");
			}
			for(String string:ingredient){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/ingredients> .\n");
			}
			for(String string:administration){
				bw.write(string+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/routeOfAdministrations> .\n");
			}	
		}
		
		System.out.println(drugs.size());
		System.out.println(ingredient.size());
		System.out.println(administration.size());
		
		bw.flush();
	}
	
}
