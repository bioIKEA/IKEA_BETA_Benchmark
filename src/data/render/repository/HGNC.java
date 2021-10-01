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

public class HGNC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
public static HashMap<String, HashSet<String>> hgncToUniprot(String input) throws IOException{
		
//	external: -> <http://bio2rdf.org/hgnc:5> <http://bio2rdf.org/hgnc_vocabulary:x-uniprot> <http://bio2rdf.org/uniprot:P04217>
//		external: -> <http://bio2rdf.org/hgnc:5> <http://bio2rdf.org/hgnc_vocabulary:x-ncbigene> <http://bio2rdf.org/ncbigene:1>
//		external: -> <http://bio2rdf.org/hgnc:5> <http://bio2rdf.org/hgnc_vocabulary:x-omim> <http://bio2rdf.org/omim:138670>
		
		HashMap<String, HashSet<String>> mapping=new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps=new HashSet<>();
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
					if(p.equals("<http://bio2rdf.org/hgnc_vocabulary:x-uniprot>")
							&&s.startsWith("<http://bio2rdf.org/hgnc:")
							&&o.startsWith("<http://bio2rdf.org/uniprot:")) {
						
						if(mapping.containsKey(s)) {
							mapping.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							mapping.put(s, set);
						}
					}
					
				}
			}
		}
		br.close();
		
		return mapping;
	}

}
