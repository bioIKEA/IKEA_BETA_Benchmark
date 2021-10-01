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

public class Sider {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
public static void extract(String input, String output) throws IOException {
		
		HashSet<String> ps=new HashSet<>();
		
		HashSet<String> drugsHashSet=new HashSet<>();
		HashSet<String> diseaseHashSet=new HashSet<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
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
					if(p.equals("<http://www4.wiwiss.fu-berlin.de/sider/resource/sider/sideeffect>")) {
						ps.add(s+" "+"<http://www4.wiwiss.fu-berlin.de/sider/resource/sider/drug-sideeffect>"+" "+o+" .");
						drugsHashSet.add(s);
						diseaseHashSet.add(o);
					}
				}
			}
		}
		br.close();
		
		for(String drug:drugsHashSet ) {
			ps.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/sider_resource:drugs>" + " .");
		}
		
		for(String disease:diseaseHashSet ) {
			ps.add(disease + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/sider_resource:side_effects>" + " .");
		}
		
		ps.add("<http://bio2rdf.org/sider_resource:drugs> <http://www4.wiwiss.fu-berlin.de/sider/resource/sider/drug-sideeffect> <http://bio2rdf.org/sider_resource:side_effects> .");
		
		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(output)));
		for(String string:ps){
			bw_1.write(string+"\n");
		}
		bw_1.flush();
		bw_1.close();
	}
}
