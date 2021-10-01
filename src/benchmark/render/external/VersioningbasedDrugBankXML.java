package benchmark.render.external;

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
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.ext.com.google.common.base.CharMatcher;
import org.semanticweb.yars.nx.parser.NxParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class VersioningbasedDrugBankXML {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub
	
		HashMap<String,String> xml_relations=extract_newDrugBankRelation
				(dataDir+"/input/full database.xml");
		Set<String> retained_relationSet=Retain(dataDir+"/output/datasets/orignial/association_drugbank.nq", 
				xml_relations);
		
		System.out.println("xml_relations: "+xml_relations.size());
		System.out.println("retained_relationSet: "+retained_relationSet);
		
//		PythonDataPreparement.generate_entire_netowrk("/home/biocaddie/workspace/network/data/input/network");
//		
//
//		filter("/home/biocaddie/workspace/network/data/newlinks_2018_restricted.tsv", 
//				"/home/biocaddie/workspace/network/data/newlinks_2018_restricted_2010.tsv") ;
//		
//		PythonDataPreparement.generate_entire_netowrk_withIdx("/home/biocaddie/workspace/network/data/input/network");
		
	}
	
	public static void filter(String file, String outfile) throws IOException{
		HashSet<String> set=new HashSet<>();
		
		set.add("2007");
		set.add("2008");
		set.add("2009");
		set.add("2010");
		set.add("2011");
		set.add("2012");
		set.add("2013");
		set.add("2014");
		set.add("2015");
		set.add("2016");
		set.add("2017");
		set.add("2018");
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outfile)));
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null){
			String[] elements=line.split("\t");
			
			int fit=0;
			for(String date:elements[1].split(";")){
				for(String string:set){
					if(date.contains(string)){
						fit++;
						break;
					}
				}
			}
			
			if(fit==elements[1].split(";").length){
				bw.write(line+"\n");
			}
		}
		bw.flush();
		bw.close();
		br.close();
	}
	public static HashMap<String,String> extract_newDrugBankRelation(String drugbank_data_file) throws ParserConfigurationException, SAXException, IOException{
			  	int counter=0;
			  	HashMap<String,String> map=new HashMap<>();
				File fXmlFile = new File(drugbank_data_file);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);

				//optional, but recommended
				//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();

				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

				NodeList nList = doc.getElementsByTagName("drug");

				System.out.println("drug: "+nList.getLength());

				for (int temp = 0; temp < nList.getLength(); temp++) {
					
					String drug_id="";
					Node drug = nList.item(temp);
					NodeList list=drug.getChildNodes();
					
					for (int i = 0; i < list.getLength(); i++) {
						Node node=list.item(i);
						if(node.getNodeName().equals("drugbank-id")){
								if(node.hasAttributes()){
									Node id=node.getAttributes().item(0);
									if(id.getNodeName().equals("primary")&&id.getTextContent().equals("true")){
//										System.out.println("drug id: "+node.getTextContent());
										drug_id=node.getTextContent();
									}
								}
						}
						if(node.getNodeName().equals("targets")){
							NodeList targets = node.getChildNodes();
							for (int j = 0; j < targets.getLength(); j++) {
								Node target=targets.item(j);
								if(target.getNodeName().equals("target")){
									NodeList targetList=target.getChildNodes();
									
									String target_id = null;
									HashSet<String> set=new HashSet<>();
									
									for (int k = 0; k < targetList.getLength(); k++) {
										
										if(targetList.item(k).getNodeName().equals("id")){
											target_id=targetList.item(k).getTextContent();
//											bw.write("<http://bio2rdf.org/drugbank:"+drug_id+"> <http://bio2rdf.org/MultiPartiteNetwork_vocabulary:Drug-Target> <http://bio2rdf.org/drugbank:"+target_id+"> .\n");
											counter++;
										}
										
										if(targetList.item(k).getNodeName().equals("references")){
											
											NodeList reference_list=targetList.item(k).getChildNodes();
											
											for (int l = 0; l < reference_list.getLength(); l++) {
												
												if(reference_list.item(l).getNodeName().equals("articles")){
													
													NodeList articles_list=reference_list.item(l).getChildNodes();
													
													for (int m = 0; m < articles_list.getLength(); m++) {
													
														if(articles_list.item(m).getNodeName().equals("article")){
															
															NodeList article_list=articles_list.item(m).getChildNodes();
															
															for (int n = 0; n < article_list.getLength(); n++) {
																
																Node article=article_list.item(n);
																
																if(article.getNodeName().equals("citation")){
																	set.add(article.getTextContent());
																}
																
															}
															
														}
														
													}
													
												}
												
											}
											
										}
										
									}
									
//								for(String string:set){
//									
//									String new_string=string.substring(string.indexOf(".")+1,string.length());
//									
//									System.out.println(drug_id+" -> "+target_id+" ====> "+new_string);
//									
//									map.put("<http://bio2rdf.org/drugbank:"+drug_id+"> <http://bio2rdf.org/MultiPartiteNetwork_vocabulary:Drug-Target> <http://bio2rdf.org/drugbank:"+target_id+"> .",
//											new_string);
//								}
								
								
									if(set.size()>0){
								
										StringBuffer sb= new StringBuffer();	
										
										for(String string:set){
											String new_string=string.substring(string.indexOf(".")+1,string.length());
											sb.append(new_string).append(";");
											
										}
										if(drug_id.toLowerCase().contains("db")&&
												target_id.toLowerCase().contains("be")) {
										
											map.put("<http://bio2rdf.org/drugbank:"+drug_id.toLowerCase()+"> <http://bio2rdf.org/drugbank_vocabulary:drug-target> <http://bio2rdf.org/drugbank:"+target_id.toLowerCase()+"> .",
													sb.toString());
										}
									}
								}
							}
						}
					}
				}
				System.out.println(" links in total: "+counter);
		 
		 return map;
	}
	
	
	public static Set<String> Retain(String old_file, HashMap<String,String> new_map) throws IOException{
		
		Set<String> old_relation=get_drug_target(old_file);
		Set<String> drugs=get_all_drugs(old_file) ;
		Set<String> targets=get_all_targets(old_file) ;
		Set<String> news=new HashSet();
		
		for(String string:new_map.keySet()){
			news.add(string);
		}
		
		System.out.println(old_relation.size());
		System.out.println(news.size());
		System.out.println("old_relation: "+old_relation.size());
		news.removeAll(old_relation);
		Set<String> return_set=new HashSet();
		for(String string:news) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				org.semanticweb.yars.nx.Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim().toLowerCase();
				if(drugs.contains(s)&&targets.contains(o)){
					return_set.add(string);
				}
			}
		}
		
//		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(output)));
//		for (String string:news) {
//			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
//			NxParser nxp = new NxParser();
//			nxp.parse(inputStream);
//			while (nxp.hasNext()) {
//				org.semanticweb.yars.nx.Node[] quard = nxp.next();
//				String s = quard[0].toString().trim();
//				String p = quard[1].toString().trim();
//				String o = quard[2].toString().trim();
//				if(drugs.contains(s)&&targets.contains(o)){
//					bw.write(string+"\t"+new_map.get(string)+"\n");
//				}
//			}
//			
//		}
//		bw.flush();
//		bw.close();
		System.out.println(return_set.size());
		return return_set;
	}
	 
	
public static HashMap<String,HashSet<String>> Retain(HashSet<String> old_relation, HashMap<String,String> new_map) throws IOException{
		
		Set<String> drugs=get_all_drugs(old_relation) ;
		Set<String> targets=get_all_targets(old_relation) ;
		Set<String> news=new HashSet();
		
		for(String string:new_map.keySet()){
			news.add(string);
		}
		
		System.out.println(old_relation.size());
		System.out.println(news.size());
		System.out.println("old_relation: "+old_relation.size());
		news.removeAll(old_relation);
		HashMap<String,HashSet<String>> map=new HashMap<String,HashSet<String>>();
		for(String string:news) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				org.semanticweb.yars.nx.Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim().toLowerCase();
				if(drugs.contains(s)&&targets.contains(o)){
					
					if(map.containsKey(o)) {
						map.get(o).add(string);
					}else {
						HashSet<String> set=new HashSet<String>();
						set.add(string);
						map.put(o, set);
					}
				}
			}
		}
		
		System.out.println(map.size());
		return map;
	}

	public static HashSet<String> get_drug_target(String file) throws IOException{
		HashSet<String> set=new HashSet<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					org.semanticweb.yars.nx.Node[] quard = nxp.next();
					
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
						
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")
							&s.startsWith("<http://bio2rdf.org/drugbank:db")
							&o.startsWith("<http://bio2rdf.org/drugbank:be")){
						set.add(line);
					}
				}
			}
		}
		br.close();
		return set;
	}
	
	
	
	public static HashSet<String> get_all_drugs(String file) throws IOException{
		HashSet<String> set=new HashSet<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					org.semanticweb.yars.nx.Node[] quard = nxp.next();
					
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					if (s.startsWith("<http://bio2rdf.org/drugbank:db")){
						set.add(s);
					}
				}
			}
		}
		br.close();
		return set;
	}
	
	
	public static HashSet<String> get_all_targets(String file) throws IOException{
		HashSet<String> set=new HashSet<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					org.semanticweb.yars.nx.Node[] quard = nxp.next();
					
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					if (o.startsWith("<http://bio2rdf.org/drugbank:be")){
						set.add(o);
					}
					
				}
			}
		}
		br.close();
		return set;
	}
	
	
	
	public static HashSet<String> get_all_drugs(HashSet<String> triples) throws IOException{
		HashSet<String> set=new HashSet<>();
		
		for(String line:triples) {
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					org.semanticweb.yars.nx.Node[] quard = nxp.next();
					
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					if (s.startsWith("<http://bio2rdf.org/drugbank:db")){
						set.add(s);
					}
				}
			}
		}
		return set;
	}
	
	
	public static HashSet<String> get_all_targets(HashSet<String> triples) throws IOException{
		HashSet<String> set=new HashSet<>();
		
		for(String line:triples) {
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					org.semanticweb.yars.nx.Node[] quard = nxp.next();
					
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();
					
					if (o.startsWith("<http://bio2rdf.org/drugbank:be")){
						set.add(o);
					}
					
				}
			}
		}
		return set;
	}
}