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

public class Parser_hgnc {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		getproterty(dataDir+"/input/hgnc_complete_set.nq") ;
		
//		exploreAllObjects(dataDir+"/input/hgnc_complete_set.nq");
	}
	
	
	public static void getproterty(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps=new HashSet<>();
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
					ps.add(p);
					
				}
			}
		}
		br.close();
		
		for(String string:ps){
			System.out.println(string);
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
				
		ps.add("<http://bio2rdf.org/hgnc_vocabulary:x-uniprot>");
		ps.add("<http://bio2rdf.org/hgnc_vocabulary:x-omim>");
		ps.add("<http://bio2rdf.org/hgnc_vocabulary:x-ncbigene>");
		
		HashSet<String> preticates=new HashSet<>();
		
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
						preticates.add(standard_s+" "+p+" "+standard_o);
						ins.add(o);
					}
					
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(new File(input)));
		line=null;
		
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
