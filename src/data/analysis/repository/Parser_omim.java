package data.analysis.repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Parser_omim {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		getproterty_advance(dataDir+"/input/omim.nq") ;
//		checkProperty_disease(dataDir+"/input/omim.nq");
//		checkProperty_gene(dataDir+"/input/done/omim.nq");
		checkProperty_directGene(dataDir+"/input/done/omim.nq") ;
	}
	
	
public static void checkProperty_directGene(String input) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> ps_2=new HashMap<>();
		HashSet<String> exclude=new HashSet<>();
		exclude.add("<http://rdfs.org/ns/void#inDataset>");
		exclude.add("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
		exclude.add("<http://bio2rdf.org/bio2rdf_vocabulary:x-identifiers.org>");
		exclude.add("<http://bio2rdf.org/sider_vocabulary:pdf-url>");
		
		
		HashMap<String,HashSet<String>> disease_map=new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> map_gene=new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> disease_gene=new HashMap<String,HashSet<String>>();
		
		HashMap<String,HashSet<String>> x_symbol=new HashMap<String,HashSet<String>>();
		
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					
//					?? single internal: -> <http://bio2rdf.org/omim:100100> <http://bio2rdf.org/omim_vocabulary:phenotype-map> <http://bio2rdf.org/omim_resource:100100_pm_1>
//					?? meaningful internal: -> <http://bio2rdf.org/omim_resource:100100_pm_1> <http://bio2rdf.org/omim_vocabulary:geneSymbols> <http://bio2rdf.org/hgnc.symbol:CHRM3>
//					?? meaningful internal: -> <http://bio2rdf.org/omim:100070> <http://bio2rdf.org/omim_vocabulary:gene-symbol> <http://bio2rdf.org/hgnc.symbol:AAA1>
//					external: -> <http://bio2rdf.org/omim:100640> <http://bio2rdf.org/omim_vocabulary:x-symbol> <http://bio2rdf.org/hgnc.symbol:ALDH1A1>
						
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:x-symbol>")) {
						
						if(x_symbol.containsKey(s)) {
							x_symbol.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(o);
							x_symbol.put(s, set);
						}
					}
					
					
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:phenotype-map>")) {
						
						if(disease_map.containsKey(s)) {
							disease_map.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(o);
							disease_map.put(s, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:geneSymbols>")) {
						if(map_gene.containsKey(s)) {
							map_gene.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(o);
							map_gene.put(s, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:gene-symbol>")) {
						if(disease_gene.containsKey(s)) {
							disease_gene.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(o);
							disease_gene.put(s, set);
						}
					}
				}
			}
		}
		
		
		HashMap<String,HashSet<String>> disease_gene_compared=new HashMap<String,HashSet<String>>();
		
		for(Entry<String,HashSet<String>> entry_1:disease_map.entrySet()) {
			for(String string:entry_1.getValue()) {
				if(map_gene.containsKey(string)) {
					for(String gene:map_gene.get(string)) {
						if(disease_gene_compared.containsKey(entry_1.getKey())) {
							disease_gene_compared.get(entry_1.getKey()).add(gene);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(gene);
							disease_gene_compared.put(entry_1.getKey(), set);
						}
					}
				}
			}
		}
		
		
		System.out.println("disease_gene_compared <http://bio2rdf.org/omim:100100> -> "+disease_gene_compared.get("<http://bio2rdf.org/omim:100100>"));
		System.out.println("disease_gene_compared <http://bio2rdf.org/omim:164280> -> "+disease_gene_compared.get("<http://bio2rdf.org/omim:164280>"));
		System.out.println("disease_gene_compared <http://bio2rdf.org/omim:300314> -> "+disease_gene_compared.get("<http://bio2rdf.org/omim:300314>"));
		
		System.err.println("disease_gene <http://bio2rdf.org/omim:100100> -> "+disease_gene.get("<http://bio2rdf.org/omim:100100>"));
		System.err.println("disease_gene <http://bio2rdf.org/omim:164280> -> "+disease_gene.get("<http://bio2rdf.org/omim:164280>"));
		System.err.println("disease_gene <http://bio2rdf.org/omim:300314> -> "+disease_gene.get("<http://bio2rdf.org/omim:300314>"));
		
		System.err.println("x_symbol <http://bio2rdf.org/omim:100100> -> "+x_symbol.get("<http://bio2rdf.org/omim:100100>"));
		System.err.println("x_symbol <http://bio2rdf.org/omim:164280> -> "+x_symbol.get("<http://bio2rdf.org/omim:164280>"));
		System.err.println("x_symbol <http://bio2rdf.org/omim:300314> -> "+x_symbol.get("<http://bio2rdf.org/omim:300314>"));
		
		int counter_1=0;
		int counter_1_match=0;
		for(Entry<String,HashSet<String>> entry:disease_gene_compared.entrySet()) {
			counter_1+=entry.getValue().size();
			for(String string:entry.getValue()) {
				if(x_symbol.containsKey(entry.getKey())) {
					if(x_symbol.get(entry.getKey()).contains(string)) {
						counter_1_match++;
					}
				}
			}
		}
		int counter_2=0;
		int counter_2_match=0;
		for(Entry<String,HashSet<String>> entry:disease_gene.entrySet()) {
			counter_2+=entry.getValue().size();
			
			for(String string:entry.getValue()) {
				if(x_symbol.containsKey(entry.getKey())) {
					if(x_symbol.get(entry.getKey()).contains(string)) {
						counter_2_match++;
					}
				}
			}
		}
		System.out.println("disease_gene_compared: "+disease_gene_compared.size());
		System.out.println("disease_gene_compared match: "+(double)counter_1_match/counter_1);
		
		System.out.println("disease_gene: "+disease_gene.size());
		System.out.println("disease_gene match: "+(double)counter_2_match/counter_2);
	}

	
	public static void checkProperty_gene(String input) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> ps_2=new HashMap<>();
		HashSet<String> exclude=new HashSet<>();
		exclude.add("<http://rdfs.org/ns/void#inDataset>");
		exclude.add("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
		exclude.add("<http://bio2rdf.org/bio2rdf_vocabulary:x-identifiers.org>");
		exclude.add("<http://bio2rdf.org/sider_vocabulary:pdf-url>");
		
		
		HashMap<String,HashSet<String>> disease_map=new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> map_gene=new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> disease_gene=new HashMap<String,HashSet<String>>();
		
		HashSet<String> external_1=new HashSet<String> ();
		HashSet<String> external_2=new HashSet<String> ();
		
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					
//					?? single internal: -> <http://bio2rdf.org/omim:100100> <http://bio2rdf.org/omim_vocabulary:phenotype-map> <http://bio2rdf.org/omim_resource:100100_pm_1>
//					?? meaningful internal: -> <http://bio2rdf.org/omim_resource:100100_pm_1> <http://bio2rdf.org/omim_vocabulary:geneSymbols> <http://bio2rdf.org/hgnc.symbol:CHRM3>
//					?? meaningful internal: -> <http://bio2rdf.org/omim:100070> <http://bio2rdf.org/omim_vocabulary:gene-symbol> <http://bio2rdf.org/hgnc.symbol:AAA1>
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:x-uniprot>")){
						external_1.add(s);
						external_2.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:phenotype-map>")) {
						
						if(disease_map.containsKey(s)) {
							disease_map.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(o);
							disease_map.put(s, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:geneSymbols>")) {
						if(map_gene.containsKey(s)) {
							map_gene.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(o);
							map_gene.put(s, set);
						}
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:gene-symbol>")) {
						if(disease_gene.containsKey(s)) {
							disease_gene.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(o);
							disease_gene.put(s, set);
						}
					}
				}
			}
		}
		
		
		HashMap<String,HashSet<String>> disease_gene_compared=new HashMap<String,HashSet<String>>();
		
		for(Entry<String,HashSet<String>> entry_1:disease_map.entrySet()) {
			for(String string:entry_1.getValue()) {
				if(map_gene.containsKey(string)) {
					for(String gene:map_gene.get(string)) {
						if(disease_gene_compared.containsKey(entry_1.getKey())) {
							disease_gene_compared.get(entry_1.getKey()).add(gene);
						}else {
							HashSet<String> set=new HashSet<String> ();
							set.add(gene);
							disease_gene_compared.put(entry_1.getKey(), set);
						}
					}
				}
			}
		}
		
		
		System.out.println("disease_gene_compared <http://bio2rdf.org/omim:100100> -> "+disease_gene_compared.get("<http://bio2rdf.org/omim:100100>"));
		System.out.println("disease_gene_compared <http://bio2rdf.org/omim:164280> -> "+disease_gene_compared.get("<http://bio2rdf.org/omim:164280>"));
		System.out.println("disease_gene_compared <http://bio2rdf.org/omim:300314> -> "+disease_gene_compared.get("<http://bio2rdf.org/omim:300314>"));
		
		System.err.println("disease_gene <http://bio2rdf.org/omim:100100> -> "+disease_gene.get("<http://bio2rdf.org/omim:100100>"));
		System.err.println("disease_gene <http://bio2rdf.org/omim:164280> -> "+disease_gene.get("<http://bio2rdf.org/omim:164280>"));
		System.err.println("disease_gene <http://bio2rdf.org/omim:300314> -> "+disease_gene.get("<http://bio2rdf.org/omim:300314>"));
		
		
		int counter_1=0;
		int counter_1_match=0;
		for(Entry<String,HashSet<String>> entry:disease_gene_compared.entrySet()) {
			counter_1+=entry.getValue().size();
			
			for(String string:entry.getValue()) {
				if(disease_gene.containsKey(entry.getKey())) {
					if(disease_gene.get(entry.getKey()).contains(string)) {
						counter_1_match++;
					}
				}
			}
			
		}
		int counter_2=0;
		int counter_2_match=0;
		for(Entry<String,HashSet<String>> entry:disease_gene.entrySet()) {
			counter_2+=entry.getValue().size();
			
			for(String string:entry.getValue()) {
				if(disease_gene_compared.containsKey(entry.getKey())) {
					if(disease_gene_compared.get(entry.getKey()).contains(string)) {
						counter_2_match++;
					}
				}
			}
		}
		System.out.println("disease_gene_compared: "+disease_gene_compared.size());
		System.out.println("disease_gene_compared match: "+(double)counter_1_match/counter_1);
		
		System.out.println("disease_gene: "+disease_gene.size());
		System.out.println("disease_gene match: "+(double)counter_2_match/counter_2);
		
		
		Set<String> set_1=disease_gene_compared.keySet();
		Set<String> set_2=disease_gene.keySet();
		
		System.out.println("external_1: "+external_1.size());
		
		external_1.retainAll(set_1);
		external_2.retainAll(set_2);
		
		
		System.out.println("& 1: "+external_1.size());
		System.out.println("& 2: "+external_2.size());
	}
	
	


	public static void checkProperty_disease(String input) throws IOException{
	
//		?? single internal: -> <http://bio2rdf.org/omim:100100> <http://bio2rdf.org/omim_vocabulary:phenotype-map> <http://bio2rdf.org/omim_resource:100100_pm_1>
//		?? meaningful internal: -> <http://bio2rdf.org/omim_resource:100100_pm_1> <http://bio2rdf.org/omim_vocabulary:geneSymbols> <http://bio2rdf.org/hgnc.symbol:CHRM3>
//		?? meaningful internal: -> <http://bio2rdf.org/omim:100070> <http://bio2rdf.org/omim_vocabulary:gene-symbol> <http://bio2rdf.org/hgnc.symbol:AAA1>
//
//		?? single internal: -> <http://bio2rdf.org/omim:100050> <http://bio2rdf.org/omim_vocabulary:clinical-synopsis> <http://bio2rdf.org/omim_resource:100050_cs>
//		?? meaningful internal: -> <http://bio2rdf.org/omim_resource:100050_cs> <http://bio2rdf.org/omim_vocabulary:feature> <http://bio2rdf.org/omim_resource:13913bc11fb724b3199858a81020af0c>
//		external: -> <http://bio2rdf.org/omim_resource:2a8b9baf22ef3ccfe7eb3d643adc3b6f> <http://bio2rdf.org/omim_vocabulary:x-omim> <http://bio2rdf.org/omim:118494>
//		external: -> <http://bio2rdf.org/omim_resource:ffe2638260c8f14cc95cb8023edc3d0f> <http://bio2rdf.org/omim_vocabulary:x-umls> <http://bio2rdf.org/umls:C1855201>
				
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> ps_2=new HashMap<>();
		HashSet<String> exclude=new HashSet<>();
		exclude.add("<http://rdfs.org/ns/void#inDataset>");
		exclude.add("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
		exclude.add("<http://bio2rdf.org/bio2rdf_vocabulary:x-identifiers.org>");
		exclude.add("<http://bio2rdf.org/sider_vocabulary:pdf-url>");
		
		
		HashSet<String> reousces=new HashSet<String>();
		HashSet<String> features=new HashSet<String>();
		
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
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:x-omim>")
							&&s.startsWith("<http://bio2rdf.org/omim_resource:")) {
						reousces.add(s);
					}
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:feature>")
							&&o.startsWith("<http://bio2rdf.org/omim_resource:")) {
						features.add(o);
					}
				}
			}
		}
		
		System.out.println("reousces: "+reousces.size());
		System.out.println("features: "+features.size());
		
		reousces.retainAll(features);
		System.out.println("reousces & features: "+reousces.size());
		
	}
	
	
	public static void getproterty_advance(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,String> ps_2=new HashMap<>();
		HashSet<String> exclude=new HashSet<>();
		exclude.add("<http://rdfs.org/ns/void#inDataset>");
		exclude.add("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
		exclude.add("<http://bio2rdf.org/bio2rdf_vocabulary:x-identifiers.org>");
		exclude.add("<http://bio2rdf.org/sider_vocabulary:pdf-url>");
		
		HashMap<String,HashMap<String,HashSet<String>>> ps_1=new HashMap<>();
		
		
		HashSet<String> triples=new HashSet();
			
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
				
					if(!exclude.contains(p)) {
						String standard_s=s.substring(0,s.lastIndexOf(":"));
						String standard_o=o.substring(0,o.lastIndexOf(":"));
						triples.add(line);
						if(p.contains(":x-")) {
							ps_2.put(p,standard_s+" "+p+" "+standard_o);	
						}else {
							
							if(ps_1.containsKey(p)) {
								
								if(ps_1.get(p).containsKey(o)) {
									ps_1.get(p).get(o).add(s);
								}else {
									HashSet<String> set=new HashSet();
									set.add(s);
									ps_1.get(p).put(o, set);
								}
							}else {
								
								HashSet<String> set=new HashSet();
								set.add(s);
								HashMap<String,HashSet<String>> map=new HashMap();
								map.put(o, set);
								ps_1.put(p, map);
							}
							
						}
					}
				}
			}
		}
		br.close();
		
		for(Entry<String,HashMap<String,HashSet<String>>> entry_1:ps_1.entrySet()) {
			boolean meaningful=false;
			for(Entry<String,HashSet<String>> entry_2:entry_1.getValue().entrySet()) {
				if(entry_2.getValue().size()>1) {
					meaningful=true;
					break;
				}
			}
			if(meaningful) {
				System.out.println("meaningful internal: -> "+entry_1.getKey());
			}else {
				System.out.println("single internal: -> "+entry_1.getKey());
			}
		}
		
		
		for(Entry<String,String> entry:ps_2.entrySet()){
			System.out.println("external: -> "+entry.getValue());
		}
	}
	
	
	public static void getinstance(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps=new HashSet<>();
		ps.add("<http://bio2rdf.org/goa_vocabulary:function>");
		ps.add("<http://bio2rdf.org/goa_vocabulary:process>");
		ps.add("<http://bio2rdf.org/goa_vocabulary:component>");
		ps.add("<http://bio2rdf.org/goa_vocabulary:x-taxonomy>");
		HashSet<String> ins=new HashSet<>(); 
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
					
					if(ps.contains(p)&&!o.contains("|")) {
						String standard_s=s.substring(0,s.lastIndexOf(":"));
						String standard_o=o.substring(0,o.lastIndexOf(":"));
						ins.add(standard_s+" "+p+" "+standard_o);
					}
					
				}
			}
		}
		br.close();
		
		for(String string:ins){
			System.out.println(string);
		}
	}
	
	public static void getObjects(String input, String subject) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> strings=new HashSet<>();
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
					
					if(s.contains(subject)) {
						strings.add(line);
						System.out.println(line);
					}
					
				}
			}
		}
		br.close();
		
		for(String string:strings){
			System.err.println(string);
		}
	}
	
	
	public static void exploreAllObjects(String input) throws IOException{
		
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps=new HashSet<>();
		ps.add("<http://bio2rdf.org/goa_vocabulary:function>");
		ps.add("<http://bio2rdf.org/goa_vocabulary:process>");
		ps.add("<http://bio2rdf.org/goa_vocabulary:component>");
		ps.add("<http://bio2rdf.org/goa_vocabulary:x-taxonomy>");
		HashSet<String> ins=new HashSet<>(); 
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
					
					if(ps.contains(p)&&!o.contains("|")) {
						String standard_s=s.substring(0,s.lastIndexOf(":"));
						String standard_o=o.substring(0,o.lastIndexOf(":"));
						ins.add(o);
					}
					
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(new File(input)));
		line=null;
		HashSet<String> preticates=new HashSet<>();
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
					
					if(ins.contains(s)) {
						preticates.add(p);
					}
					
				}
			}
		}
		br.close();
		
		for(String string:preticates){
			System.err.println(string);
		}
	}
	
}
