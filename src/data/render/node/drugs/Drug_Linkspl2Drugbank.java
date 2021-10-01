package data.render.node.drugs;

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

import javassist.expr.NewArray;

public class Drug_Linkspl2Drugbank {
	public static String dataDir="data_sample";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static void writeMapping(String outfile ) throws IOException {
		HashMap<String,HashSet<String>> todrugbank=getDrugbank(dataDir+"/input/done/linkspl.nt");
		
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		
		for(Entry<String,HashSet<String>> entry:todrugbank.entrySet()){
			for(String string:entry.getValue()){
				bw.write(entry.getKey()+" <http://www.w3.org/2002/07/owl#sameAs> "+string+" .\n");
			}
		}
		bw.flush();
		bw.close();
	}
	
	public static HashMap<String,HashSet<String>> getDrugbank(String drugbankfile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(drugbankfile)));
		String line=null;
		HashMap<String,HashSet<String>> todrugBank=new HashMap<>();
		HashSet<String> drugs=new HashSet<>();
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
					
					if(p.equals(new String("<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/subjectXref>").toLowerCase())
							&o.startsWith("<http://bio2rdf.org/drugbank:db")){
						if(todrugBank.containsKey(s)){
							todrugBank.get(s).add(o);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(o);
							todrugBank.put(s, set);
						}
					}
					
				}
			}
		}
		System.out.println("DrugBank drugbank: "+todrugBank.size());
		System.out.println("DrugBank drugs: "+drugs.size());
		br.close();
		return todrugBank;
	}
	

}
