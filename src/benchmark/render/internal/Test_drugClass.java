package benchmark.render.internal;

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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Test_drugClass {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		drugbanktoLinkpl();
	}
	
	
	public static void drugbanktoLinkpl() throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/association_drugbank.nq")));
		String line=null;
		HashSet<String> drugSet=new HashSet<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					if(s.startsWith("<http://bio2rdf.org/drugbank:db")) {
						drugSet.add(s);	
					}
				} 
			}	
		}
		br.close();
		
		HashMap<String, HashSet<String>> linkplToDrugbank=new HashMap<>();
		br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/drug_linkspl_drugbank.nq")));
		line=null;
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://www.w3.org/2002/07/owl#sameAs>")) {
					if(o.startsWith("<http://bio2rdf.org/drugbank:db")) {
						if(linkplToDrugbank.containsKey(s)) {
							linkplToDrugbank.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							linkplToDrugbank.put(s, set);
						}
					}
				} 
			}	
		}
		System.out.println("linkplToDrugbank: "+linkplToDrugbank.size());
		HashMap<String, HashSet<String>> drugbankClass_1=new HashMap<>();
		HashMap<String, HashSet<String>> drugbankClass_2=new HashMap<>();
		br=new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/association_linkspl.nq")));
		line=null;
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if(p.equals("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-pharmacologic_class>")) {
					if(linkplToDrugbank.containsKey(s)) {
						for(String drug:linkplToDrugbank.get(s)) {
							if(drugbankClass_1.containsKey(o)) {
								drugbankClass_1.get(o).add(drug);
							}else {
								HashSet<String> set=new HashSet<>();
								set.add(drug);
								drugbankClass_1.put(o, set);
							}
						}
					}
				}
				
				if(p.equals("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-type>")) {
					if(linkplToDrugbank.containsKey(s)) {
						for(String drug:linkplToDrugbank.get(s)) {
							if(drugbankClass_2.containsKey(o)) {
								drugbankClass_2.get(o).add(drug);
							}else {
								HashSet<String> set=new HashSet<>();
								set.add(drug);
								drugbankClass_2.put(o, set);
							}
						}
					}
				} 
			}	
		}
		
		System.out.println("drugbank size: "+drugSet.size());
		System.out.println("drugbankClass_1 size: "+drugbankClass_1.size());
		System.out.println("drugbankClass_2 size: "+drugbankClass_2.size());
		
		int all_number_1=0;
		
		HashMap<String, Integer> drugbankClass_1_counter=new HashMap<>();
		HashMap<String, Integer> drugbankClass_2_counter=new HashMap<>();
		
		for(Entry<String, HashSet<String>> entry:drugbankClass_1.entrySet()) {
			all_number_1+=entry.getValue().size();
			drugbankClass_1_counter.put(entry.getKey(), entry.getValue().size());
		}
		System.out.println("average drugbankClass_1： "+(double)all_number_1/drugbankClass_1.size());
		
		
		int all_number_2=0;
		for(Entry<String, HashSet<String>> entry:drugbankClass_2.entrySet()) {
			all_number_2+=entry.getValue().size();
			drugbankClass_2_counter.put(entry.getKey(), entry.getValue().size());
		}
		System.out.println("average drugbankClass_2： "+(double)all_number_2/drugbankClass_2.size());
		
	    ArrayList<Map.Entry<String,Integer>> list_1 = new ArrayList<>(drugbankClass_1_counter.entrySet());
        Collections.sort(list_1, new Comparator<Map.Entry<String,Integer>>() {
            public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());//升序，前边加负号变为降序
            }
        });

       for (int i = 0; i < 10; i++) {
		System.out.println(list_1.get(i));
       }
		
       ArrayList<Map.Entry<String,Integer>> list_2 = new ArrayList<>(drugbankClass_2_counter.entrySet());
       Collections.sort(list_2, new Comparator<Map.Entry<String,Integer>>() {
           public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
               return o2.getValue().compareTo(o1.getValue());//升序，前边加负号变为降序
           }
       });

      for (int i = 0; i < 10; i++) {
		System.out.println(list_2.get(i));
      }
		
		
	}
}
