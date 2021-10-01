package data.render.node.targets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.bytedeco.javacpp.videoInputLib.videoDevice;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Target_Irefindex2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
public static void writeMapping( String outfile) throws IOException{
	HashMap<String, HashSet<String>> uniprotToDrugbank=  getUniprotToDrug();
		BufferedWriter bw_2 =new BufferedWriter(new FileWriter(new File(outfile)));
		BufferedReader br = new BufferedReader(new FileReader(new File(dataDir+"/input/done/irefindex-all.nq")));
		String line=null;
		HashSet<String> sameAs=new HashSet<String>();
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
					if(s.startsWith("<http://bio2rdf.org/uniprot:")) {
						if(uniprotToDrugbank.containsKey(s)) {
							for(String drug:uniprotToDrugbank.get(s)) {
								sameAs.add(s+" "+"<http://www.w3.org/2002/07/owl#sameAs>"+" "+drug+" .");
							}
						}
					}
					if(o.startsWith("<http://bio2rdf.org/uniprot:")) {
						if(uniprotToDrugbank.containsKey(o)) {
							for(String drug:uniprotToDrugbank.get(o)) {
								sameAs.add(o+" "+"<http://www.w3.org/2002/07/owl#sameAs>"+" "+drug+" .");
							}
						}
					}
				}
			}
		}
		br.close();
		
		for(String string:sameAs) {
			bw_2.write(string+"\n");
		}
		bw_2.flush();
		bw_2.close();
	}

	public static HashMap<String, HashSet<String>> getUniprotToDrug() throws IOException {
		HashMap<String, HashSet<String>> uniprotToDrugbank=new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(dataDir+"/input/done/drugbank.nq")));
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
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")) {
						if(uniprotToDrugbank.containsKey(o)){
							uniprotToDrugbank.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							uniprotToDrugbank.put(o, set);
						}
					}
				}
			}
		}
		br.close();
		return uniprotToDrugbank;
	}
}


