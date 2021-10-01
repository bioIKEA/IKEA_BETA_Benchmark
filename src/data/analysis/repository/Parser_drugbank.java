package data.analysis.repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import java_cup.internal_error;

public class Parser_drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		getproterty_advance(dataDir+"/input/drugbank.nq") ;
		checkProperty(dataDir+"/input/done/drugbank.nq");
	}
	
	public static void checkProperty(String input) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> valueHashMap=new HashMap<>();
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
				
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:category>")) {
						if(valueHashMap.containsKey(o)) {
							valueHashMap.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							valueHashMap.put(o, set);
						}
					}
				}
			}
		}
		System.out.println("size： "+valueHashMap.size());
		int all_number=0;
		HashMap<String,Integer> drugbankClass_counter=new HashMap<>();
		for(Entry<String, HashSet<String>> entry:valueHashMap.entrySet()) {
			all_number+=entry.getValue().size();
			drugbankClass_counter.put(entry.getKey(), entry.getValue().size());
		}
		System.out.println("average： "+(double)all_number/valueHashMap.size());
		
		
		ArrayList<Map.Entry<String,Integer>> list_2 = new ArrayList<>(drugbankClass_counter.entrySet());
	       Collections.sort(list_2, new Comparator<Map.Entry<String,Integer>>() {
	           public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
	               return o2.getValue().compareTo(o1.getValue());//升序，前边加负号变为降序
	           }
	       });

	      for (int i = 0; i < 10; i++) {
			System.out.println(list_2.get(i));
	      }
	      
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
