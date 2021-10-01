package data.render.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Drugbank {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void extract(String input, String output) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps=new HashSet<>();
		
		HashSet<String> drugsHashSet=new HashSet<>();
		HashSet<String> geneHashSet=new HashSet<>();
		HashSet<String> foointeractionHashSet=new HashSet<>();
		HashSet<String> bindingHashSet=new HashSet<>();
		HashSet<String> categoryHashSet=new HashSet<>();
		HashSet<String> druginteractionHashSet=new HashSet<>();
		HashSet<String> substructureHashSet=new HashSet<>();
		
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
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:target>")&&
							s.startsWith("<http://bio2rdf.org/drugbank:db")&&
							o.startsWith("<http://bio2rdf.org/drugbank:be")) {
						ps.add(s+" "+"<http://bio2rdf.org/drugbank_vocabulary:drug-target>"+" "+o+" .");
						drugsHashSet.add(s);
						geneHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:ddi-interactor-in>")) {
						ps.add(s+" "+"<http://bio2rdf.org/drugbank_vocabulary:drug-drug>"+" "+o+" .");
						drugsHashSet.add(s);
						druginteractionHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:food-interaction>")) {
						ps.add(s+" "+"<http://bio2rdf.org/drugbank_vocabulary:drug-food>"+" "+o+" .");
						drugsHashSet.add(s);
						foointeractionHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:protein-binding>")) {
						ps.add(s+" "+"<http://bio2rdf.org/drugbank_vocabulary:drug-binding>"+" "+o+" .");
						drugsHashSet.add(s);
						bindingHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:substructure>")) {
						ps.add(s+" "+"<http://bio2rdf.org/drugbank_vocabulary:drug-substructure>"+" "+o+" .");
						drugsHashSet.add(s);
						substructureHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:category>")) {
						ps.add(s+" "+"<http://bio2rdf.org/drugbank_vocabulary:drug-category>"+" "+o+" .");
						drugsHashSet.add(s);
						categoryHashSet.add(o);
					}
				}
			}
		}
		br.close();
		
		
		for(String drug:drugsHashSet ) {
			ps.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/drugbank_resource:drugs>" + " .");
		}
		
		for(String gene:geneHashSet ) {
			ps.add(gene + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/drugbank_resource:targets>" + " .");
		}
		
		for(String foointeraction:foointeractionHashSet ) {
			ps.add(foointeraction + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/drugbank_resource:food_interactions>" + " .");
		}
		
		for(String binding:bindingHashSet ) {
			ps.add(binding + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/drugbank_resource:bindings>" + " .");
		}
		
		for(String category:categoryHashSet ) {
			ps.add(category + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/drugbank_resource:drug_categories>" + " .");
		}
		
		for(String druginteraction:druginteractionHashSet ) {
			ps.add(druginteraction + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/drugbank_resource:drug_interactions>" + " .");
		}
		
		for(String substructure:substructureHashSet ) {
			ps.add(substructure + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/drugbank_resource:drug_substructures>" + " .");
		}
		
		ps.add("<http://bio2rdf.org/drugbank_resource:drugs> <http://bio2rdf.org/drugbank_vocabulary:drug-target> <http://bio2rdf.org/drugbank_resource:targets> .");
		ps.add("<http://bio2rdf.org/drugbank_resource:drugs> <http://bio2rdf.org/drugbank_vocabulary:drug-food> <http://bio2rdf.org/drugbank_resource:food_interactions> .");
		ps.add("<http://bio2rdf.org/drugbank_resource:drugs> <http://bio2rdf.org/drugbank_vocabulary:drug-binding> <http://bio2rdf.org/drugbank_resource:bindings> .");
		ps.add("<http://bio2rdf.org/drugbank_resource:drugs> <http://bio2rdf.org/drugbank_vocabulary:drug-category> <http://bio2rdf.org/drugbank_resource:drug_categories> .");
		ps.add("<http://bio2rdf.org/drugbank_resource:drugs> <http://bio2rdf.org/drugbank_vocabulary:drug-drug> <http://bio2rdf.org/drugbank_resource:drug_interactions> .");
		ps.add("<http://bio2rdf.org/drugbank_resource:drugs> <http://bio2rdf.org/drugbank_vocabulary:drug-substructure> <http://bio2rdf.org/drugbank_resource:drug_substructures> .");
		
		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(output)));
		for(String string:ps){
			bw_1.write(string+"\n");
		}
		bw_1.flush();
		bw_1.close();
	}

}
