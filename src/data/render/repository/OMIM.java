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

public class OMIM {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	
	public static void extract(String input, String output) throws IOException {
		
		HashSet<String> ps=new HashSet<>();
		
		HashMap<String,String> toDiseaSet=new HashMap<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		
		HashSet<String> diseaseSet=new HashSet<>();
		HashSet<String> symbolSet=new HashSet<>();
		HashSet<String> featureSet=new HashSet<>();
		String line=null;
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					s=s.replaceAll(" ", "");
					String p = quard[1].toString().trim().toLowerCase();
					
					String o = quard[2].toString().trim().toLowerCase();
					o=o.replaceAll(" ", "");
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:phenotype-map>")) {
						toDiseaSet.put(o, s);
						diseaseSet.add(s);
					}
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:clinical-synopsis>")) {
						toDiseaSet.put(o, s);
						diseaseSet.add(s);
					}
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:gene-symbol>")) {
						ps.add(s+" "+"<http://bio2rdf.org/omim_vocabulary:disease-symbol>"+" "+o+" .");
						diseaseSet.add(s);
						symbolSet.add(o);
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
					s=s.replaceAll(" ", "");
					String p = quard[1].toString().trim().toLowerCase();
					String o = quard[2].toString().trim().toLowerCase();
					o=o.replaceAll(" ", "");
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:genesymbols>")) {
						if(toDiseaSet.containsKey(s)) {
							ps.add(toDiseaSet.get(s)+" "+"<http://bio2rdf.org/omim_vocabulary:disease-map_symbol>"+" "+o+" .");
							symbolSet.add(o);
							diseaseSet.add(s);
						}
					}
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:feature>")) {
						if(toDiseaSet.containsKey(s)) {
							ps.add(toDiseaSet.get(s)+" "+"<http://bio2rdf.org/omim_vocabulary:disease-feature>"+" "+o+" .");
							featureSet.add(o);
							diseaseSet.add(s);
						}
					}
				}
			}
		}
		br.close();
		
		for(String disease:diseaseSet ) {
			ps.add(disease + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/omim_vocabulary:diseases>" + " .");
		}
		for(String symbol:symbolSet ) {
			ps.add(symbol + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/omim_vocabulary:gene_symbols>" + " .");
		}
		for(String feature:featureSet ) {
			ps.add(feature + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/omim_vocabulary:features>" + " .");
		}
		ps.add("<http://bio2rdf.org/omim_vocabulary:diseases> <http://bio2rdf.org/omim_vocabulary:disease-symbol> <http://bio2rdf.org/omim_vocabulary:gene_symbols> .");
		ps.add("<http://bio2rdf.org/omim_vocabulary:diseases> <http://bio2rdf.org/omim_vocabulary:disease-map_symbol> <http://bio2rdf.org/omim_vocabulary:gene_symbols> .");
		ps.add("<http://bio2rdf.org/omim_vocabulary:diseases> <http://bio2rdf.org/omim_vocabulary:disease-feature> <http://bio2rdf.org/omim_vocabulary:features> .");
			
		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(output)));
		for(String string:ps){
			bw_1.write(string+"\n");
		}
		bw_1.flush();
		bw_1.close();
	}
	
}
