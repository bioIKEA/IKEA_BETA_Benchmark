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

public class Parser_taxonomy {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		getproterty(dataDir+"/input/taxonomy-nodes.nq") ;
		getproterty(dataDir+"/input/taxonomy-names.nq") ;
		
	}
	
	
	public static void checkSameValue(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps_1=new HashSet<>();
		HashSet<String> ps_2=new HashSet<>();
		HashSet<String> ps_3=new HashSet<>();
		HashSet<String> ps_4=new HashSet<>();
				
		String string_1="<http://bio2rdf.org/kegg_vocabulary:gene>";
		String string_2="<http://bio2rdf.org/kegg_vocabulary:marker>";
		String string_3="<http://bio2rdf.org/kegg_vocabulary:ko-gene>";
		String string_4="<http://bio2rdf.org/kegg_vocabulary:ko-marker>";
		
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
				
					
					if(p.equals(string_1)) {
						ps_1.add(o);
					}
					if(p.equals(string_2)) {
						ps_2.add(o);
					}
					
					if(p.equals(string_3)) {
						ps_3.add(o);
					}
					if(p.equals(string_4)) {
						ps_4.add(o);
					}
				}
			}
		}
		br.close();
		
		System.out.println("stirng-1 "+ps_1.size());
		System.out.println("stirng-2 "+ps_2.size());
		ps_1.retainAll(ps_2);
		System.out.println("stirng-1 &2  "+ps_1.size());
		
		
		System.out.println("stirng-3 "+ps_3.size());
		System.out.println("stirng-4 "+ps_4.size());
		ps_3.retainAll(ps_4);
		System.out.println("stirng-3 &4  "+ps_3.size());
		
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
		exclude.add("<http://bio2rdf.org/sider_vocabulary:pdf-url>");
		exclude.add("<http://www.w3.org/2000/01/rdf-schema#seeAlso>");	
		
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
