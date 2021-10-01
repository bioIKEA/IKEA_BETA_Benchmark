package data.process.map;

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

import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class DBpedia {
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String,HashSet<String>> map=getDiseaseHgnc("D:/data/drug-taget-network/Databases/data/input/diseasome_dump.nt");
		writeDBpediaMapping(map, "D:/data/drug-taget-network/Databases/data/output/disease_diseasome_omim(dbpedia).nq");
	}
	
	public static HashMap<String,HashSet<String>> getDiseaseHgnc(String diseasomeFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(diseasomeFile)));
		String line=null;
		
		HashMap<String,HashSet<String>> map=new HashMap<>();
		
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
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")
							&o.startsWith("<http://www.dbpedia.org/resource/")
							&p.equals("<http://www.w3.org/2002/07/owl#sameAs>")
							){
						String newo="<http://dbpedia.org/resource/"+o.substring(o.lastIndexOf("/")+1, o.length());
						if(map.containsKey(newo)){
							map.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							map.put(newo, set);
						}
					}
					
				}
			}
			
		}
		return map;
	}
	
	
	public static void writeDBpediaMapping(HashMap<String,HashSet<String>> map1, String outfile) throws IOException{
		
		
		StringBuffer sb=new StringBuffer();
		for(String dbpedia:map1.keySet()){
			sb.append(dbpedia+",");
		}
		
		String queryStr = "PREFIX dbo: <http://dbpedia.org/ontology/> "
				+ "select * where "
				+ "{?subject dbo:omim ?object . "
				+ "FILTER (?subject IN ("
				+ sb.toString().substring(0,sb.toString().length()-1)
				+ "))"
				+ "}";
		
        Query query = QueryFactory.create(queryStr);

        
        HashMap<String,HashSet<String>> map2=new HashMap<>();
        
        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            while(rs.hasNext()){
            	QuerySolution qs=rs.next();
            	String key="<"+qs.get("subject")+">";
            	String value=qs.get("object").toString().substring(0, qs.get("object").toString().indexOf("^"));
            	value="<http://bio2rdf.org/omim:"+value+">";
            	if(map2.containsKey(key)){
            		map2.get(key).add(value);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(value);
					map2.put(key, set);
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outfile)));
        HashSet<String> lines=new HashSet<>();
        int i=0;
        int j=0;
        for(String string:map1.keySet()){
        	if(!map2.containsKey(string)){
        		i++;
        	}else{
        		j++;
        		for(String string_1:map1.get(string)){
        			for(String string_2:map2.get(string)){
        				lines.add(string_1+" <http://www.w3.org/2002/07/owl#sameAs> "+string_2+" .");
        			}
        		}
        	}
        }
        System.out.println("dbpedia omim: "+j+" percetnage: "+(j)/(i+j));
        for(String string:lines){
        	bw.write(string+"\n");	
        }
        bw.flush();
        bw.close();
	}
	
	
	public static HashMap<String,HashSet<String>> getDBpediaMapping(HashMap<String,HashSet<String>> map1) throws IOException{
		
		HashMap<String,HashSet<String>> map2=new HashMap<>();
		StringBuffer sb=new StringBuffer();
		for(String dbpedia:map1.keySet()){
			sb.append(dbpedia+",");
		}
		
		/**
		 *  bug fixed 09/02/2021
		 */
		if (sb.toString().length()>0) {
			String queryStr = "PREFIX dbo: <http://dbpedia.org/ontology/> "
					+ "select * where "
					+ "{?subject dbo:omim ?object . "
					+ "FILTER (?subject IN ("
					+ sb.toString().substring(0,sb.toString().length()-1)
					+ "))"
					+ "}";
			
	        Query query = QueryFactory.create(queryStr);
	        
	        // Remote execution.
	        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
	            // Set the DBpedia specific timeout.
	            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

	            // Execute.
	            ResultSet rs = qexec.execSelect();
	            while(rs.hasNext()){
	            	QuerySolution qs=rs.next();
	            	String key="<"+qs.get("subject")+">";
	            	String value=qs.get("object").toString().substring(0, qs.get("object").toString().indexOf("^"));
	            	value="<http://bio2rdf.org/omim:"+value+">";
	            	if(map2.containsKey(key)){
	            		map2.get(key).add(value);
					}else{
						HashSet<String> set=new HashSet<>();
						set.add(value);
						map2.put(key, set);
					}
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		
        return map2;
	}


}
