package data.render.node.diseases;

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

import data.process.map.DBpedia;
import jsat.regression.StochasticGradientBoosting;



public class Disease_Diseasome2Omim {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		/**
		 *  Use UMLS and dbpedia to get diseasome to omim
		 */
		HashMap<String,HashSet<String>> umls_2=Disease_Sider2Omim.getOmim(dataDir+"/input/done/omim.nq"); //umls,Omim
		HashMap<String,HashSet<String>> umls_1=Disease_Sider2Omim.getSider(dataDir+"/input/done/sider_dump.nt");//umls,sider
		HashMap<String,HashSet<String>> siderToOmim=Disease_Sider2Omim.getSiderToOmim(umls_1, umls_2); // sider, omim
		HashMap<String,HashSet<String>> DiseasetoSider=Disease_Diseasome2Omim.getDiseasome
				(dataDir+"/input/done/diseasome_dump.nt"); // disease, sider
		
		HashMap<String,HashSet<String>> dbpediaTodisease=DBpedia.getDiseaseHgnc(dataDir+"/input/done/diseasome_dump.nt");
		HashMap<String,HashSet<String>> dbpediaToOmim=DBpedia.getDBpediaMapping(dbpediaTodisease);
		
		
		writeMapping(DiseasetoSider, siderToOmim,
				dbpediaTodisease,dbpediaToOmim,"D:/data/drug-taget-network/Databases/data/output/disease_diseasome_omim.nq");
		
	}
	
	public static void writeMapping(String output) throws IOException {
		/**
		 *  Use UMLS and dbpedia to get diseasome to omim
		 */
		HashMap<String,HashSet<String>> umls_2=Disease_Sider2Omim.getOmim(dataDir+"/input/done/omim.nq"); //umls,Omim
		HashMap<String,HashSet<String>> umls_1=Disease_Sider2Omim.getSider(dataDir+"/input/done/sider_dump.nt");//umls,sider
		HashMap<String,HashSet<String>> siderToOmim=Disease_Sider2Omim.getSiderToOmim(umls_1, umls_2); // sider, omim
		HashMap<String,HashSet<String>> DiseasetoSider=Disease_Diseasome2Omim.getDiseasome
				(dataDir+"/input/done/diseasome_dump.nt"); // disease, sider
		
		HashMap<String,HashSet<String>> dbpediaTodisease=DBpedia.getDiseaseHgnc(dataDir+"/input/done/diseasome_dump.nt");
		HashMap<String,HashSet<String>> dbpediaToOmim=DBpedia.getDBpediaMapping(dbpediaTodisease);
		
		
		writeMapping(DiseasetoSider, siderToOmim,
				dbpediaTodisease,dbpediaToOmim,output);
	}
	
	public static HashMap<String,HashSet<String>> getDiseasome(String input) throws IOException{
		HashMap<String,HashSet<String>> dbpediaToSider=getSider(dataDir+"/input/done/sider_dump.nt");
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> DiseasetoSider=new HashMap<>();
		HashSet<String> disease=new HashSet<>();
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
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
							&o.equals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases>")){
						disease.add(s);
					}
					
					if(p.equals("<http://www.w3.org/2002/07/owl#sameas>")
							&o.startsWith("<http://www4.wiwiss.fu-berlin.de/sider/resource/side_effects/")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")){
						if(DiseasetoSider.containsKey(s)){
							DiseasetoSider.get(s).add(o);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(o);
							DiseasetoSider.put(s, set);
						}
					}
					if(p.equals("<http://www.w3.org/2002/07/owl#sameas>")
							&o.startsWith("<http://www.dbpedia.org/resource/")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/")){
						if(dbpediaToSider.containsKey(o)){
							HashSet<String> siders=dbpediaToSider.get(o);
							for(String sider:siders){
								if(DiseasetoSider.containsKey(s)){
									DiseasetoSider.get(s).add(sider);
								}else{
									HashSet<String> set=new HashSet<>();
									set.add(sider);
									DiseasetoSider.put(s, set);
								}		
							}
						}
					}
				}
			}
		}
		System.out.println("diseasome to sider: "+DiseasetoSider.size());
		System.out.println("diseasome disease: "+disease.size());
		br.close();
		return DiseasetoSider;
	}
	
	public static HashMap<String,HashSet<String>> getSider(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> toSider=new HashMap<>();
		HashSet<String> disease=new HashSet<>();
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
					
					
					if(p.equals("<http://www.w3.org/2002/07/owl#sameas>")
							&o.startsWith("<http://www.dbpedia.org/resource/")
							&s.startsWith("<http://www4.wiwiss.fu-berlin.de/sider/resource/side_effects/")){
						if(toSider.containsKey(o)){
							toSider.get(o).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							toSider.put(o, set);
						}
					}
				}
			}
		}
		br.close();
		return toSider;
	}
	
	public static void writeMapping(HashMap<String,HashSet<String>> tosider,HashMap<String,HashSet<String>> siderToOmim,String outfile) throws IOException{
		int i=0;
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(Entry<String,HashSet<String>> entry:tosider.entrySet()){
					for(String string_1:entry.getValue()){
						if(siderToOmim.containsKey(string_1)){
							for(String string_2:siderToOmim.get(string_1)){
								bw.write(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string_2+" .\n");		
								i++;
							}
						}
					}
		}
		System.out.println("diseasome2omim mapping: "+i);
		bw.flush();
		bw.close();
	}
	
	
	public static void writeMapping(HashMap<String,HashSet<String>> tosider,HashMap<String,HashSet<String>> siderToOmim,
			HashMap<String,HashSet<String>> dbpediatoDiseasome,HashMap<String,HashSet<String>> dbpediaToOmim,String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		HashSet<String> lines=new HashSet<>();
		int k=0;
		HashSet<String> diseasome=new HashSet<>();
		HashSet<String> omim=new HashSet<>();
		for(Entry<String,HashSet<String>> entry:tosider.entrySet()){
					for(String string_1:entry.getValue()){
						if(siderToOmim.containsKey(string_1)){
							for(String string_2:siderToOmim.get(string_1)){
								lines.add(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string_2+" .");
								k++;
								diseasome.add(entry.getKey());
								omim.add(string_2);
							}
						}
					}
		}
		
		int i=0;
        int j=0;
        for(String string:dbpediatoDiseasome.keySet()){
        	if(!dbpediaToOmim.containsKey(string)){
        		i++;
        	}else{
        		j++;
        		for(String string_1:dbpediatoDiseasome.get(string)){
        			for(String string_2:dbpediaToOmim.get(string)){
        				lines.add(string_1+" <http://www.w3.org/2002/07/owl#sameAs> "+string_2+" .");
        				diseasome.add(string_1);
						omim.add(string_2);
        			}
        		}
        	}
        }
        System.out.println("dbpedia omim: "+j+" percetnage: "+(double)j/(i+j));
        System.out.println("diseasome2omim mapping: "+k);
        for(String string:lines){
        	bw.write(string+"\n");
        }
		System.out.println("@@@@ Diseasome to Omim mapping: "+lines.size());
		System.out.println("@@@@ Diseasome mapped: "+diseasome.size());
		System.out.println("@@@@ Omim mapped: "+omim.size());
		
		bw.flush();
		bw.close();
	}
	
	
}
