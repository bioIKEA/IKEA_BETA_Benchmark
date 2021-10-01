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

import org.bytedeco.javacpp.videoInputLib.videoDevice;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Irefindex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
public static void extract(String input, String outfile_1) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps=new HashSet<>();
		HashSet<String> ps_1=new HashSet<>();
		HashSet<String> ps_2=new HashSet<>();
		HashSet<String> ps_3=new HashSet<>();
		HashSet<String> ps_4=new HashSet<>();
		
		HashSet<String> instancehSet=new HashSet<>();
		HashSet<String> interactiongroupSet=new HashSet<>();
		HashSet<String> groupSet=new HashSet<>();
		
		HashMap<String, HashSet<String>> typesHashMap=new HashMap<>();
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
					if(p.equals("<http://bio2rdf.org/irefindex_vocabulary:interactor_a>")) {
						ps.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:interactor_a>"+" "+o+" .");
						ps_1.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:interactor_a>"+" "+o+" .");
						instancehSet.add(s);
						instancehSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/irefindex_vocabulary:interactor_b>")) {
						ps.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:interactor_b>"+" "+o+" .");
						ps_2.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:interactor_a>"+" "+o+" .");
						instancehSet.add(s);
						instancehSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-interaction-group>")) {
						ps.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-interaction-group>"+" "+o+" .");
						ps_3.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-interaction-group>"+" "+o+" .");
						instancehSet.add(s);
						interactiongroupSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-group>")) {
						ps.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-group>"+" "+o+" .");
						ps_4.add(s+" "+"<http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-group>"+" "+o+" .");
						instancehSet.add(s);
						groupSet.add(o);
					}
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")&&
							o.startsWith("<http://bio2rdf.org/mi:")) {
						if(typesHashMap.containsKey(s)) {
							typesHashMap.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							typesHashMap.put(s, set);
						}
					}
				}
			}
		}
		br.close();
		
		for(String instance:instancehSet) {
			if(typesHashMap.containsKey(instance)) {
				for(String type:typesHashMap.get(instance)) {
					ps.add(instance+" "+"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"+" "+type+" .");
				}
			}
		}
		
		for(String interactiongroup:interactiongroupSet ) {
			ps.add(interactiongroup + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/irefindex_vocabulary:sequence-identical-interaction-groups>" + " .");
		}
		for(String group:groupSet ) {
			ps.add(group + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/irefindex_vocabulary:sequence-identical-groups>" + " .");
		}
		
		for(String string:ps_1) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim().toLowerCase();
				String o = quard[2].toString().trim().toLowerCase();
				
				if(typesHashMap.containsKey(s)&&typesHashMap.containsKey(o)) {
					for(String type_1:typesHashMap.get(s)) {
						for(String type_2:typesHashMap.get(o)) {
							ps.add(type_1+" "+"<http://bio2rdf.org/irefindex_vocabulary:interactor_a>"+" "+type_2+" .");
						}
					}
				}
			}
		}
		
		
		for(String string:ps_2) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim().toLowerCase();
				String o = quard[2].toString().trim().toLowerCase();
				
				if(typesHashMap.containsKey(s)&&typesHashMap.containsKey(o)) {
					for(String type_1:typesHashMap.get(s)) {
						for(String type_2:typesHashMap.get(o)) {
							ps.add(type_1+" "+"<http://bio2rdf.org/irefindex_vocabulary:interactor_b>"+" "+type_2+" .");
						}
					}
				}
			}
		}
		
		
		for(String string:ps_3) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim().toLowerCase();
				String o = quard[2].toString().trim().toLowerCase();
				
				if(typesHashMap.containsKey(s)) {
					for(String type_1:typesHashMap.get(s)) {
							ps.add(type_1+" "+"<http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-interaction-group>"+" "+"<http://bio2rdf.org/irefindex_vocabulary:sequence-identical-interaction-groups>"+" .");
					}
				}
			}
		}
		
		for(String string:ps_4) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim().toLowerCase();
				String o = quard[2].toString().trim().toLowerCase();
				
				if(typesHashMap.containsKey(s)) {
					for(String type_1:typesHashMap.get(s)) {
							ps.add(type_1+" "+"http://bio2rdf.org/irefindex_vocabulary:taxon-sequence-identical-group>"+" "+"<http://bio2rdf.org/irefindex_vocabulary:sequence-identical-groups>"+" .");
					}
				}
			}
		}
		
		
		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(outfile_1)));
		for(String string:ps){
			bw_1.write(string+"\n");
		}
		bw_1.flush();
		bw_1.close();
	}

}
