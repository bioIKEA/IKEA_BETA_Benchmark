package data.render.node.features;

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

import com.github.andrewoma.dexx.collection.internal.adapter.SetAdapater;

import javassist.expr.NewArray;

public class Generate_feature {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	}
	
	
	public static void pull_chemical(String outfile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/input/done/drugbank.nq")));
		String line=null;
		HashMap<String,HashSet<String>> calculated_properties=new HashMap<>();
		HashSet<String> smile_subject=new HashSet<>();
		HashMap<String,String>  values=new HashMap<>();
		
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:calculated-properties>")){
						if(calculated_properties.containsKey(s)) {
							calculated_properties.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							calculated_properties.put(s, set);
						}
					}
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")&&
							o.equals("<http://bio2rdf.org/drugbank_vocabulary:SMILES>")) {
						smile_subject.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:value>")) {
						String value=o.substring(o.indexOf("\"")+1,o.lastIndexOf("\""));
						values.put(s, value);
					}
			}
		}
		
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outfile)));
		
		for(Entry<String, HashSet<String>> entry:calculated_properties.entrySet()) {
			String drugString=entry.getKey().toLowerCase();
			for(String string:entry.getValue()) {
				if(smile_subject.contains(string)) {
					String value=values.get(string);
					bw.write(drugString+"\t"+value+"\n");
				}
			}
		}
		bw.flush();
		bw.close();
		br.close();
	}
	
	
	public static void pull_gene(String outfile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/input/done/drugbank_v3.nq")));
		String line=null;
		HashMap<String,String> sequence_subject=new HashMap<>();
		
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:gene-sequence>")) {
						String value=o.substring(o.indexOf(" bp")+3,o.lastIndexOf("\""));
						value=value.replaceAll("\n", "");
						sequence_subject.put(s, value.trim());
					}
			}
		}
		
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outfile)));
		
		for(Entry<String,String> entry:sequence_subject.entrySet()) {
			String drugString=entry.getKey().toLowerCase();
			String value=entry.getValue();
			bw.write(drugString+"\t"+value+"\n");
		}
		bw.flush();
		bw.close();
		br.close();
	}

}
