package data.render.repository;

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

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import javassist.expr.NewArray;

public class Linkspl {
	public static String dataDir="data_sample";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static HashMap<String, HashSet<String>> hugotoPharmgkb() throws IOException {
		
		HashMap<String, HashSet<String>> hugotoPharmgkb=new HashMap<>();
		
		BufferedReader  br = new BufferedReader(new FileReader(new File
				(dataDir+"/input/done/pharmgkb_genes.nq")));
		 String line=null;
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
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:symbol>")) {
						
						String newo="<http://bio2rdf.org/hugo:"+o.substring(o.lastIndexOf(":")+1, o.lastIndexOf(">"))+">";
						
						if(hugotoPharmgkb.containsKey(newo)){
							hugotoPharmgkb.get(newo).add(s);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							hugotoPharmgkb.put(newo, set);	
						}
					}
				}
			}
		}
		br.close();
		return hugotoPharmgkb;
	}
	
	public static void extract(String input, String output) throws IOException {
		HashMap<String, HashSet<String>> hugotoPharmgkb=hugotoPharmgkb();
		HashMap<String, String> local_mapHashMap=new HashMap<>();
		HashSet<String> ps=new HashSet<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		
		HashSet<String> drugsHashSet=new HashSet<>();
		HashSet<String> pharmacologicHashSet=new HashSet<>();
		HashSet<String> typeHashSet=new HashSet<>();
		HashSet<String> targetHashSet=new HashSet<>();
		
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
					if(p.equals(new String("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/establishedPharmacologicClass>").toLowerCase())) {
						ps.add(s+" "+"<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-pharmacologic_class>"+" "+o+" .");
						drugsHashSet.add(s);
						pharmacologicHashSet.add(o);
					}
					
					if(p.equals(new String("<http://purl.org/dc/elements/1.1/subject>").toLowerCase())) {
						ps.add(s+" "+"<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-type>"+" "+o+" .");
						drugsHashSet.add(s);
						typeHashSet.add(o);
					}
					
					if(p.equals(new String("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/pharmgxData>").toLowerCase())) {
						local_mapHashMap.put(o, s);
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
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim().toLowerCase();
					String o = quard[2].toString().trim().toLowerCase();
					
					if(p.equals(new String("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/HGNCGeneSymbol>").toLowerCase())) {
						if(local_mapHashMap.containsKey(s)) {
							if(hugotoPharmgkb.containsKey(o)) {
								for(String pharmgkb:hugotoPharmgkb.get(o)) {
									ps.add(local_mapHashMap.get(s)+" "+"<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-target>"+" "+pharmgkb+" .");
									drugsHashSet.add(local_mapHashMap.get(s));
									targetHashSet.add(pharmgkb);
									
								}
							}
						}
						
					}
					
					if(p.equals(new String("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/pharmgxXref>").toLowerCase())) {
						if(local_mapHashMap.containsKey(s)) {
							ps.add(local_mapHashMap.get(s)+" "+"<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-target>"+" "+o+" .");
							drugsHashSet.add(local_mapHashMap.get(s));
							targetHashSet.add(o);
						}
						
					}
				}
			}
		}
		br.close();
		
		
		for(String drug:drugsHashSet ) {
			ps.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drugs>" + " .");
		}
		
		for(String gene:targetHashSet ) {
			ps.add(gene + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/targets>" + " .");
		}
		
		for(String pharmacologic:pharmacologicHashSet ) {
			ps.add(pharmacologic + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/pharmacologic_classes>" + " .");
		}
		
		for(String type:typeHashSet ) {
			ps.add(type + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug_types>" + " .");
		}
		
		ps.add("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drugs> <http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-target> <http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/targets> .");
		ps.add("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drugs> <http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-pharmacologic_class> <http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/pharmacologic_classes> .");
		ps.add("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drugs> <http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-type> <http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug_types> .");
		
		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(output)));
		for(String string:ps){
			bw_1.write(string+"\n");
		}
		bw_1.flush();
		bw_1.close();
	}

}
