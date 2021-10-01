package data.analysis.repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Parser_kegg_genes {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		getproterty(dataDir+"/input/done/kegg-genes.nq") ;
//		checkSameValue(dataDir+"/input/kegg-genes.nq");
	}
	
	
	public static HashSet<String> getDiseaseToGene() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/input/kegg-disease.nq")));
		String line=null;
		HashSet<String> ps_1=new HashSet<>();
		HashSet<String> ps_2=new HashSet<>();
		HashSet<String> ps_3=new HashSet<>();
		HashSet<String> ps_4=new HashSet<>();
				
		String string_1="<http://bio2rdf.org/kegg_vocabulary:gene>";
		String string_2="<http://bio2rdf.org/kegg_vocabulary:marker>";
		
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
				
					
					if(p.equals(string_1)) {
						ps_1.add(o);
					}
					if(p.equals(string_2)) {
						ps_2.add(o);
					}
				}
			}
		}
		br.close();
		
		System.out.println("stirng-1 "+ps_1.size());
		System.out.println("stirng-2 "+ps_2.size());
		ps_1.addAll(ps_2);
		return ps_1;
	}
	
	public static void getproterty(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps_1=new HashSet<>();
		HashSet<String> ps_2=new HashSet<>();
		HashSet<String> exclude=new HashSet<>();
		exclude.add("<http://rdfs.org/ns/void#inDataset>");
		exclude.add("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
		exclude.add("<http://bio2rdf.org/bio2rdf_vocabulary:x-identifiers.org>");
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
				
					if(!o.contains("|")&&!exclude.contains(p)) {
						String standard_s=s.substring(0,s.lastIndexOf(":"));
						String standard_o=o.substring(0,o.lastIndexOf(":"));
						if(p.contains(":x-")) {
							ps_1.add(standard_s+" "+p+" "+standard_o);	
						}else {
							ps_2.add(standard_s+" "+p+" "+standard_o);
						}
						
					}
				}
			}
		}
		br.close();
		
		for(String string:ps_2){
			System.out.println("internal: -> "+string);
		}
		
		for(String string:ps_1){
			System.out.println("external: -> "+string);
		}
	}

	
			public static void checkSameValue(String input) throws IOException{
				
				HashSet<String> fromDisease=getDiseaseToGene();
			BufferedReader br = new BufferedReader(new FileReader(new File(input)));
			String line=null;
			HashSet<String> ps_1=new HashSet<>();
			HashSet<String> fromGene=new HashSet<>();
					
			String string_1="<http://bio2rdf.org/kegg_vocabulary:disease>";
			
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
					
						if(s.startsWith("<http://bio2rdf.org/kegg:hsa_")) {
							ps_1.add(s);
						}
						if(o.startsWith("<http://bio2rdf.org/kegg:hsa_")) {
							ps_1.add(o);
						}
						
						if(p.equals(string_1)) {
							fromGene.add(s);
						}
					}
				}
			}
			br.close();
			
			System.out.println("gene all: "+ps_1.size());
			System.out.println("fromGene: "+fromGene.size());
			System.out.println("fromDisease: "+fromDisease.size());
			
			fromDisease.retainAll(fromGene);
			System.out.println("fromGene-fromDisease: "+fromDisease.size());
			
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
