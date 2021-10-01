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
import java.util.Map.Entry;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;




public class KeggDiseaseMapping {
	
	HashMap<String,HashSet<String>> keggToMeshOrICD10;
	HashMap<String,HashSet<String>> meshOrICD10ToDbpedia;
	HashMap<String,HashSet<String>> dbpediaToDiseasome;
	HashMap<String,HashSet<String>> mappings;
	public static String DiseasomeNameSpace="<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/";
	public static String ICD10="icd10";
	public static String MESH="mesh";
	
	public static void main(String[] args) throws IOException{
		
		String keggfile="data/input/kgg/kegg-disease.nq";
		String dbpediafile="data/input/dbpedia/mappingbased-properties_en.nt";
		String dbpediadiseasomefile="data/input/dbpedia/diseasome_links.nt";
		
		String intermediateType=KeggDiseaseMapping.MESH;
		
		KeggDiseaseMapping kd= new KeggDiseaseMapping(keggfile, dbpediafile, dbpediadiseasomefile, intermediateType);
		kd.writeKeggDiseaseMapping(null);
		kd.printMapping("data/input/drugCloud/keggToDiseasome_mesh_mapping.nt");
//		
//		mappingCompare("data/input/drugCloud/keggToDiseasome_mesh_mapping.nt","data/input/drugCloud/keggToDiseasome_icd10_mapping.nt");
	}
	
	
	public KeggDiseaseMapping(String keggfile, String dbpediafile, String dbpediadiseasomefile, String intermediateType) throws IOException{
		keggToMeshOrICD10=new HashMap<>();
		meshOrICD10ToDbpedia=new HashMap<>();
		dbpediaToDiseasome=new HashMap<>();
		mappings=new HashMap<>();
		System.out.println(" loading keggfile ");
		getMapping(keggfile, intermediateType, keggToMeshOrICD10) ;
		System.out.println(" loading dbpediafile ");
		getMapping(dbpediafile, intermediateType,  meshOrICD10ToDbpedia) ;
		System.out.println(" loading dbpediadiseasomefile ");
		getMapping(dbpediadiseasomefile, "null", dbpediaToDiseasome);
		System.err.println("data 1 size: "+keggToMeshOrICD10.size());
		System.err.println("data 2 size: "+meshOrICD10ToDbpedia.size());
		System.err.println("data 3 size: "+ dbpediaToDiseasome.size());
	}
	
	
	public void getMapping(String mappingfile, String intermediateType, HashMap<String,HashSet<String>> atob) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(mappingfile)));
		String line=null;
		
		while((line=br.readLine())!=null){
			if(line.startsWith("<http://")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					if(intermediateType.equals("null")){
						
						if(p.equals("<http://www.w3.org/2002/07/owl#sameAs>")){
							if(s.startsWith("<http://dbpedia.org/resource/")&o.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")){
								
								if(atob.containsKey(s)){
									atob.get(s).add(o);
								}else{
									HashSet<String> set=new HashSet<>();
									set.add(o);
									atob.put(s, set);
								}
								
							}
						}
					}
					
					if(intermediateType.equals(KeggDiseaseMapping.ICD10)){
						if(p.equals("<http://bio2rdf.org/kegg_vocabulary:x-icd10>")){
							if(s.startsWith("<http://bio2rdf.org/kegg:H")&o.startsWith("<http://bio2rdf.org/icd10")){
								if(atob.containsKey(s)){
									atob.get(s).add(o);
								}else{
									HashSet<String> set=new HashSet<>();
									set.add(o);
									atob.put(s, set);
								}
								
							}
						}	
					}
					
					if(intermediateType.equals(KeggDiseaseMapping.MESH)){
						if(p.equals("<http://bio2rdf.org/kegg_vocabulary:x-mesh>")){
							if(s.startsWith("<http://bio2rdf.org/kegg:H")&o.startsWith("<http://bio2rdf.org/mesh")){
								if(atob.containsKey(s)){
									atob.get(s).add(o);
								}else{
									HashSet<String> set=new HashSet<>();
									set.add(o);
									atob.put(s, set);
								}
								
							}
						}	
					}
					
					if(intermediateType.equals(KeggDiseaseMapping.ICD10)){
						if(p.equals("<http://dbpedia.org/ontology/icd10>")){
							if(s.startsWith("<http://dbpedia.org/resource/")&o.contains("\"")){
								String newo="<http://bio2rdf.org/icd10:"+o.substring(o.indexOf("\"")+1, o.lastIndexOf("\""))+">";
								if(atob.containsKey(newo)){
									atob.get(newo).add(s);
								}else{
									HashSet<String> set=new HashSet<>();
									set.add(s);
									atob.put(newo, set);
								}
								
							}
						}	
					}
					
					if(intermediateType.equals(KeggDiseaseMapping.MESH)){
						if(p.equals("<http://dbpedia.org/ontology/meshId>")){
							if(s.startsWith("<http://dbpedia.org/resource/")&o.contains("\"")){
								String newo="<http://bio2rdf.org/mesh:"+o.substring(o.indexOf("\"")+1, o.lastIndexOf("\""))+">";
								if(atob.containsKey(newo)){
									atob.get(newo).add(s);
								}else{
									HashSet<String> set=new HashSet<>();
									set.add(s);
									atob.put(newo, set);
								}
							}
						}	
					}
				}
			}
		}
		
	}
	
	
	
	public HashMap<String,HashSet<String>> writeKeggDiseaseMapping(BufferedWriter bw) throws IOException{
		
		keggToMeshOrICD10=new HashMap<>();
		meshOrICD10ToDbpedia=new HashMap<>();
		dbpediaToDiseasome=new HashMap<>();
		
		for(Entry<String,HashSet<String>> entry:keggToMeshOrICD10.entrySet()){
			for(String meshoricd10:entry.getValue()){
				if(meshOrICD10ToDbpedia.containsKey(meshoricd10)){
					for(String dbpedia:meshOrICD10ToDbpedia.get(meshoricd10)){
						if(dbpediaToDiseasome.containsKey(dbpedia)){
							for(String disease:dbpediaToDiseasome.get(dbpedia)){
								bw.write(entry.getKey()+" <http://www.w3.org/2004/02/skos/core#closeMatch> "+disease+" .\n");
							}
						}
					}
				}
			}
		}
		bw.flush();
		return mappings;
	}
	
	public void printMapping(String OneToOneoutput) throws IOException{
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(OneToOneoutput)));
		HashSet<String> target=new HashSet<>();
		int i=0;
		for(Entry<String,HashSet<String>> entry:mappings.entrySet()){
			
				if(entry.getValue().size()==1){
					for(String string:entry.getValue()){
						if(string.startsWith(KeggDiseaseMapping.DiseasomeNameSpace)){
						i++;
						bw1.write(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string+" .\n");
						target.add(string);
						}
					}
				}else{
					for(String string:entry.getValue()){
						if(string.startsWith(KeggDiseaseMapping.DiseasomeNameSpace)){
							target.add(string);
							bw1.write(entry.getKey()+" <http://www.w3.org/2004/02/skos/core#closeMatch> "+string+" .\n");	
						}
						
					}
				}
			}
		System.out.println("another pure one to one : "+i);
		System.out.println("total target : "+target.size());
		bw1.flush();
		bw1.close();
	}
	
		
	public HashSet<String> copySet(HashSet<String> set){
		HashSet<String> copy= new HashSet<>();
		for(String string:set){
			copy.add(string);
		}
		return copy;
	}
	public static void mappingCompare(String file1,String file2) throws IOException{
		HashMap<String,String> map1=getSameAs( file1);
		HashMap<String,String> map2=getSameAs( file2);
		HashMap<String,HashSet<String>> map3=getCloseMatch( file1);
		HashMap<String,HashSet<String>> map4=getCloseMatch( file2);
		int i=0;
		int j=0;
		for(Entry<String,String> entry:map1.entrySet()){
			if(map2.containsKey(entry.getKey())){
				if(entry.getValue().equals(map2.get(entry.getKey()))){
				i++;	
				};
			}else{
				if(!map4.containsKey(entry.getKey())){
					j++;
				}else{
					boolean add=true;
					for(String string:map4.get(entry.getKey())){
						if(string.equals(entry.getValue())){
							add=false;
						}
					}
					if(add){
//						j++;
					}
				}
			}
		}
		
		for(Entry<String,String> entry:map2.entrySet()){
			if(!map1.containsKey(entry.getKey())){
				if(!map3.containsKey(entry.getKey())){
					j++;
				}else{
					boolean add=true;
					for(String string:map3.get(entry.getKey())){
						if(string.equals(entry.getValue())){
							add=false;
						}
					}
					if(add){
//						j++;
					}
				}
			}
		}
		System.out.println("file 1: "+map1.size());
		System.out.println("file 2: "+map2.size());
		System.out.println("merge : "+i);
		System.out.println("merge : "+j);
	}
	
	public static HashMap<String,String> getSameAs(String file) throws IOException{
		BufferedReader br= new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<String,String> map= new HashMap<>();
		while((line=br.readLine())!=null){
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				
				if(p.equals("<http://www.w3.org/2002/07/owl#sameAs>")){
					map.put(s, o);
				}
			}
		}
		return map;
	}
	
	public static HashMap<String,HashSet<String>> getCloseMatch(String file) throws IOException{
		BufferedReader br= new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<String,HashSet<String>> map= new HashMap<>();
		while((line=br.readLine())!=null){
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				
				if(p.equals("<http://www.w3.org/2004/02/skos/core#closeMatch>")){
					if(map.containsKey(s)){
						map.get(s).add(o);
					}else{
						HashSet<String> set= new HashSet<>();
						set.add(o);
						map.put(s, set);
					}
				}
			}
		}
		return map;
	}
	
	
	public void getDiseaseGene(String keggDisease) throws IOException{
		BufferedReader br= new BufferedReader(new FileReader(new File(keggDisease)));
		String line=null;
		
		HashMap<String,HashSet<String>> diseaseGene = new HashMap<String,HashSet<String>>();
		
		while((line=br.readLine())!=null){
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				
				if(s.startsWith("<http://bio2rdf.org/kegg:H")&o.startsWith("<http://bio2rdf.org/kegg:")&p.startsWith("<http://bio2rdf.org/kegg_vocabulary:gene>"))	
				{
					if(diseaseGene.containsKey(s)){
						diseaseGene.get(s).add(o);
					}else{
						HashSet<String> set=new HashSet<>();
						set.add(o);
						diseaseGene.put(s, set);
					}
				}
				
			}
		}
		
	}
	
	
	
	
}
