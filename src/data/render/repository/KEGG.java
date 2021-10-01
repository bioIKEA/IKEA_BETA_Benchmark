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

import jsat.clustering.OPTICS.ExtractionMethod;

public class KEGG {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	String drug_file=dataDir+"/input/done/kegg-drug.nq";
	String disease_file=dataDir+"/input/done/kegg-disease.nq";
	String gene_file=dataDir+"/input/done/kegg-genes.nq";
	String module_file=dataDir+"/input/done/kegg-module.nq";
	String pathway_file=dataDir+"/input/done/kegg-pathway.nq";
	HashSet<String> printSet;
	public KEGG() {
		printSet=new HashSet<>();
	}
	
	public void extract(String associationOut) throws IOException {
		HashMap<String, HashSet<String>> compound_drug= extract_drug();
		extract_disease();
		extract_gene();
		extract_module(compound_drug);
		extract_pathway(compound_drug);
		
		BufferedWriter bw_2 =new BufferedWriter(new FileWriter(new File(associationOut)));
		HashSet<String> check_set=new HashSet<String>();
		for(String string:printSet) {
			bw_2.write(string+"\n");
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim().toLowerCase();
				String o = quard[2].toString().trim().toLowerCase();
				String standard_s=s.substring(0,s.lastIndexOf(":"));
				String standard_o=o.substring(0,o.lastIndexOf(":"));
				check_set.add(standard_s+" "+p+" "+standard_o);
			}
		}
		bw_2.flush();
		bw_2.close();
	}
	public HashMap<String, HashSet<String>> extract_drug() throws IOException {
			HashMap<String, HashSet<String>> compound_drug=new HashMap<String, HashSet<String>>();
			BufferedReader br = new BufferedReader(new FileReader(new File(drug_file)));
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
						if(p.equals("<http://bio2rdf.org/kegg_vocabulary:str_map>")
								&&s.startsWith("<http://bio2rdf.org/kegg:d")
								&&o.startsWith("<http://bio2rdf.org/kegg:map")) {
							printSet.add(s+" "+"<http://bio2rdf.org/kegg_vocabulary:drug-str_map>"+" "+o+" .");
						}
						if(p.equals("<http://bio2rdf.org/kegg_vocabulary:pathway>")
								&&s.startsWith("<http://bio2rdf.org/kegg:d")
								&&o.startsWith("<http://bio2rdf.org/kegg:map")) {
							printSet.add(s+" "+"<http://bio2rdf.org/kegg_vocabulary:drug-pathway>"+" "+o+" .");
						}
						if(p.equals("<http://bio2rdf.org/kegg_vocabulary:drug-group>")
								&&s.startsWith("<http://bio2rdf.org/kegg:d")
								&&o.startsWith("<http://bio2rdf.org/kegg:dg")) {
							printSet.add(s+" "+"<http://bio2rdf.org/kegg_vocabulary:drug-drug_group>"+" "+o+" .");
						}
						if(p.equals("<http://bio2rdf.org/kegg_vocabulary:same-as>")
								&&s.startsWith("<http://bio2rdf.org/kegg:d")
								&&o.startsWith("<http://bio2rdf.org/kegg:c")) {
							if(compound_drug.containsKey(o)) {
								compound_drug.get(o).add(s);
							}else {
								HashSet<String> set=new HashSet<>();
								set.add(s);
								compound_drug.put(o, set);
							}
						}
					}
				}
			}
			br.close();
			
			return compound_drug;
	}

	
	
	public void  extract_disease() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(new File(disease_file)));
		String line=null;
		HashSet<String> diseasesHashSet=new HashSet<>();
		HashSet<String> drugsHashSet=new HashSet<>();
		HashSet<String> genesHashSet=new HashSet<>();
		HashSet<String> pathwaysHashSet=new HashSet<>();
		
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
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:drug>")
							&&s.startsWith("<http://bio2rdf.org/kegg:h")
							&&o.startsWith("<http://bio2rdf.org/kegg:d")) {
						printSet.add(o+" "+"<http://bio2rdf.org/kegg_vocabulary:drug-disease>"+" "+s+" .");
						diseasesHashSet.add(s);
						drugsHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:gene>")
							&&s.startsWith("<http://bio2rdf.org/kegg:h")
							&&o.startsWith("<http://bio2rdf.org/kegg:hsa_")) {
						printSet.add(o+" "+"<http://bio2rdf.org/kegg_vocabulary:disease-target>"+" "+s+" .");
						diseasesHashSet.add(s);
						genesHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:pathway>")
							&&s.startsWith("<http://bio2rdf.org/kegg:h")
							&&o.startsWith("<http://bio2rdf.org/kegg:map")) {
						printSet.add(s+" "+"<http://bio2rdf.org/kegg_vocabulary:disease-pathway>"+" "+o+" .");
						diseasesHashSet.add(s);
						pathwaysHashSet.add(o);
					}
				}
			}
		}
		br.close();
		
		for(String disease:diseasesHashSet ) {
			printSet.add(disease + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:diseases>" + " .");
		}
		for(String drug:drugsHashSet ) {
			printSet.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:drugs>" + " .");
		}
		
		for(String gene:genesHashSet ) {
			printSet.add(gene + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:genes>" + " .");
		}
		
		for(String pathway:pathwaysHashSet ) {
			printSet.add(pathway + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:pathways>" + " .");
		}

		printSet.add("<http://bio2rdf.org/kegg_vocabulary:drugs> <http://bio2rdf.org/kegg_vocabulary:drug-disease> <http://bio2rdf.org/kegg_vocabulary:diseases> .");
		printSet.add("<http://bio2rdf.org/kegg_vocabulary:diseases> <http://bio2rdf.org/kegg_vocabulary:disease-target> <http://bio2rdf.org/kegg_vocabulary:genes> .");
		printSet.add("<http://bio2rdf.org/kegg_vocabulary:diseases> <http://bio2rdf.org/kegg_vocabulary:disease-pathway> <http://bio2rdf.org/kegg_vocabulary:pathways> .");
	}

	
			
public void  extract_gene() throws IOException {
		
	HashSet<String> diseasesHashSet=new HashSet<>();
	HashSet<String> drugsHashSet=new HashSet<>();
	HashSet<String> genesHashSet=new HashSet<>();
	HashSet<String> pathwaysHashSet=new HashSet<>();
	HashSet<String> modulesHashSet=new HashSet<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(gene_file)));
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
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:drug-target>")
							&&s.startsWith("<http://bio2rdf.org/kegg:hsa_")
							&&o.startsWith("<http://bio2rdf.org/kegg:d")) {
						printSet.add(o+" "+"<http://bio2rdf.org/kegg_vocabulary:drug-target>"+" "+s+" .");
						genesHashSet.add(s);
						drugsHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:disease>")
							&&s.startsWith("<http://bio2rdf.org/kegg:hsa")
							&&o.startsWith("<http://bio2rdf.org/kegg:h")) {
						printSet.add(o+" "+"<http://bio2rdf.org/kegg_vocabulary:disease-target>"+" "+s+" .");
						genesHashSet.add(s);
						diseasesHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:module>")
							&&s.startsWith("<http://bio2rdf.org/kegg:hsa")
							&&o.startsWith("<http://bio2rdf.org/kegg:m")) {
						printSet.add(s+" "+"<http://bio2rdf.org/kegg_vocabulary:target-module>"+" "+o+" .");
						genesHashSet.add(s);
						modulesHashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/kegg_vocabulary:pathway>")
							&&s.startsWith("<http://bio2rdf.org/kegg:hsa")
							&&o.startsWith("<http://bio2rdf.org/kegg:map")) {
						printSet.add(s+" "+"<http://bio2rdf.org/kegg_vocabulary:target-pathway>"+" "+o+" .");
						genesHashSet.add(s);
						pathwaysHashSet.add(o);
					}
				}
			}
		}
		br.close();
		
		for(String disease:diseasesHashSet ) {
			printSet.add(disease + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:diseases>" + " .");
		}
		for(String drug:drugsHashSet ) {
			printSet.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:drugs>" + " .");
		}
		
		for(String gene:genesHashSet ) {
			printSet.add(gene + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:genes>" + " .");
		}
		
		for(String pathway:pathwaysHashSet ) {
			printSet.add(pathway + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:pathways>" + " .");
		}
		
		for(String module:modulesHashSet ) {
			printSet.add(module + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:modules>" + " .");
		}

		printSet.add("<http://bio2rdf.org/kegg_vocabulary:drugs> <http://bio2rdf.org/kegg_vocabulary:drug-target> <http://bio2rdf.org/kegg_vocabulary:genes> .");
		printSet.add("<http://bio2rdf.org/kegg_vocabulary:diseases> <http://bio2rdf.org/kegg_vocabulary:disease-target> <http://bio2rdf.org/kegg_vocabulary:genes> .");
		printSet.add("<http://bio2rdf.org/kegg_vocabulary:genes> <http://bio2rdf.org/kegg_vocabulary:target-module> <http://bio2rdf.org/kegg_vocabulary:modules> .");
		printSet.add("<http://bio2rdf.org/kegg_vocabulary:genes> <http://bio2rdf.org/kegg_vocabulary:target-pathway> <http://bio2rdf.org/kegg_vocabulary:pathways> .");
		
	}

	
public void  extract_module(HashMap<String, HashSet<String>> compound_drug) throws IOException {
	
	HashSet<String> drugsHashSet=new HashSet<>();
	HashSet<String> pathwaysHashSet=new HashSet<>();
	HashSet<String> modulesHashSet=new HashSet<>();
	
	BufferedReader br = new BufferedReader(new FileReader(new File(module_file)));
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
				if(p.equals("<http://bio2rdf.org/kegg_vocabulary:pathway>")
						&&s.startsWith("<http://bio2rdf.org/kegg:m")
						&&o.startsWith("<http://bio2rdf.org/kegg:map")) {
					printSet.add(s+" "+"<http://bio2rdf.org/kegg_vocabulary:module-pathway>"+" "+o+" .");
					pathwaysHashSet.add(o);
					modulesHashSet.add(s);
				}
				if(p.equals("<http://bio2rdf.org/kegg_vocabulary:compound>")
						&&s.startsWith("<http://bio2rdf.org/kegg:m")
						&&o.startsWith("<http://bio2rdf.org/kegg:c")) {
					if(compound_drug.containsKey(o)) {
						for(String drug:compound_drug.get(o)) {
							printSet.add(drug+" "+"<http://bio2rdf.org/kegg_vocabulary:drug-module>"+" "+s+" .");
							drugsHashSet.add(drug);
							modulesHashSet.add(s);
						}
					}
				}
			}
		}
	}
	br.close();
	
	for(String drug:drugsHashSet ) {
		printSet.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:drugs>" + " .");
	}
	
	for(String pathway:pathwaysHashSet ) {
		printSet.add(pathway + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:pathways>" + " .");
	}
	
	for(String module:modulesHashSet ) {
		printSet.add(module + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:modules>" + " .");
	}

	printSet.add("<http://bio2rdf.org/kegg_vocabulary:modules> <http://bio2rdf.org/kegg_vocabulary:module-pathway> <http://bio2rdf.org/kegg_vocabulary:pathways> .");
	printSet.add("<http://bio2rdf.org/kegg_vocabulary:drugs> <http://bio2rdf.org/kegg_vocabulary:drug-module> <http://bio2rdf.org/kegg_vocabulary:modules> .");
}

public void  extract_pathway(HashMap<String, HashSet<String>> compound_drug) throws IOException {
	
	HashSet<String> diseasesHashSet=new HashSet<>();
	HashSet<String> drugsHashSet=new HashSet<>();
	HashSet<String> pathwaysHashSet=new HashSet<>();
	HashSet<String> modulesHashSet=new HashSet<>();
	
	BufferedReader br = new BufferedReader(new FileReader(new File(module_file)));
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
				if(p.equals("<http://bio2rdf.org/kegg_vocabulary:disease>")
						&&s.startsWith("<http://bio2rdf.org/kegg:map")
						&&o.startsWith("<http://bio2rdf.org/kegg:h")) {
					printSet.add(o+" "+"<http://bio2rdf.org/kegg_vocabulary:disease-pathway>"+" "+s+" .");
					pathwaysHashSet.add(s);
					diseasesHashSet.add(o);
				}
				if(p.equals("<http://bio2rdf.org/kegg_vocabulary:module>")
						&&s.startsWith("<http://bio2rdf.org/kegg:map")
						&&o.startsWith("<http://bio2rdf.org/kegg:m")) {
					printSet.add(o+" "+"<http://bio2rdf.org/kegg_vocabulary:module-pathway>"+" "+s+" .");
					pathwaysHashSet.add(s);
					modulesHashSet.add(o);
				}
				if(p.equals("<http://bio2rdf.org/kegg_vocabulary:compound>")
						&&s.startsWith("<http://bio2rdf.org/kegg:map")
						&&o.startsWith("<http://bio2rdf.org/kegg:c")) {
					
					if(compound_drug.containsKey(o)) {
						for(String drug:compound_drug.get(o)) {
							printSet.add(drug+" "+"<http://bio2rdf.org/kegg_vocabulary:drug-pathway>"+" "+s+" .");
							pathwaysHashSet.add(s);
							drugsHashSet.add(o);
						}
					}
				}
			}
		}
	}
	br.close();
	
	for(String disease:diseasesHashSet ) {
		printSet.add(disease + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:diseases>" + " .");
	}
	for(String drug:drugsHashSet ) {
		printSet.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:drugs>" + " .");
	}
	
	for(String pathway:pathwaysHashSet ) {
		printSet.add(pathway + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:pathways>" + " .");
	}
	
	for(String module:modulesHashSet ) {
		printSet.add(module + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/kegg_vocabulary:modules>" + " .");
	}

	printSet.add("<http://bio2rdf.org/kegg_vocabulary:diseases> <http://bio2rdf.org/kegg_vocabulary:disease-pathway> <http://bio2rdf.org/kegg_vocabulary:pathways> .");
	printSet.add("<http://bio2rdf.org/kegg_vocabulary:modules> <http://bio2rdf.org/kegg_vocabulary:module-pathway> <http://bio2rdf.org/kegg_vocabulary:pathways> .");
	printSet.add("<http://bio2rdf.org/kegg_vocabulary:drugs> <http://bio2rdf.org/kegg_vocabulary:drug-pathway> <http://bio2rdf.org/kegg_vocabulary:pathways> .");
	
}















}
