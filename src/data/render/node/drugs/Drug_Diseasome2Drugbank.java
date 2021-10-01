package data.render.node.drugs;

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

public class Drug_Diseasome2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		
	}
	
	
	public static void writeMapping(String output) throws IOException {
		Drug_Diseasome2Drugbank.diseasome(dataDir+"/input/done/diseasome_dump.nt",
				output);
	}
	public static void diseasome(String input, String output) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> drugs=new HashSet<>();
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
					
					if(p.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/possibledrug>")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")
							&o.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/")){
						drugs.add(o);
					}
				}
			}
		}
		System.out.println("diseasome drugbank: "+drugs.size());
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(output)));
		HashSet<String> lines=new HashSet<>();
		HashSet<String> nodes1=new HashSet<>();
		HashSet<String> nodes2=new HashSet<>();
		for(String drug:drugs){
			String string="<http://bio2rdf.org/drugbank:"+drug.substring(drug.lastIndexOf("/")+1,drug.length());
			lines.add(drug+" <http://www.w3.org/2002/07/owl#sameAs> "+string+" .");
			nodes1.add(drug);
			nodes2.add(string);
		}
		
		for(String string:lines){
			bw.write(string+"\n");
		}
		
		System.out.println("@@@ diseasome 2 drugbank mapping: "+lines.size());
		System.out.println("@@@ mapped diseasome: "+nodes1.size());
		System.out.println("@@@ mapped drugbank: "+nodes2.size());
		
		
		bw.flush();
		bw.close();
		br.close();
	}
	
}
