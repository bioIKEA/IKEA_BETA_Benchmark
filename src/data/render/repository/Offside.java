package data.render.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Offside {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
public static void extract(String input, String output) throws IOException {
		
		HashSet<String> ps=new HashSet<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		
		HashSet<String> drugsHashSet=new HashSet<>();
		HashSet<String> geneHashSet=new HashSet<>();
		HashSet<String> offsideHashSet=new HashSet<>();
		
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
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:event>")) {
						ps.add(s+" "+"<http://bio2rdf.org/pharmgkb_vocabulary:offside-target>"+" "+o+" .");
						geneHashSet.add(o);
						offsideHashSet.add(s);
					}
					if(p.equals("<http://bio2rdf.org/pharmgkb_vocabulary:chemical>")) {
						ps.add(s+" "+"<http://bio2rdf.org/pharmgkb_vocabulary:offside-drug>"+" "+o+" .");
						drugsHashSet.add(o);
						offsideHashSet.add(s);
					}
				}
			}
		}
		br.close();
		
		for(String drug:drugsHashSet ) {
			ps.add(drug + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/pharmgkb_vocabulary:drugs>" + " .");
		}
		
		for(String gene:geneHashSet ) {
			ps.add(gene + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/pharmgkb_vocabulary:targets>" + " .");
		}
		
		for(String offside:offsideHashSet ) {
			ps.add(offside + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/pharmgkb_vocabulary:offsides>" + " .");
		}
		
		ps.add("<http://bio2rdf.org/pharmgkb_vocabulary:offsides> <http://bio2rdf.org/pharmgkb_vocabulary:offside-target> <http://bio2rdf.org/pharmgkb_vocabulary:targets> .");
		ps.add("<http://bio2rdf.org/pharmgkb_vocabulary:offsides> <http://bio2rdf.org/pharmgkb_vocabulary:offside-drug> <http://bio2rdf.org/pharmgkb_vocabulary:drugs> .");
		
		
		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(output)));
		for(String string:ps){
			bw_1.write(string+"\n");
		}
		bw_1.flush();
		bw_1.close();
	}
}
