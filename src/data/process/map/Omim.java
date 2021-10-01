package data.process.map;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Omim {
	public HashMap<String,HashSet<String>> getUniprotOmim(String input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashMap<String,HashSet<String>> map= new HashMap<>();
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
					
					if(p.equals("<http://bio2rdf.org/omim_vocabulary:x-uniprot>")
							&s.startsWith("<http://bio2rdf.org/omim:")
							&o.startsWith("<http://bio2rdf.org/uniprot:")
							){
						if(map.containsKey(o)){
							map.get(o).add(s);	
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(s);
							map.put(o,set);
						}
					}
				}
			}
			
		}
		return map;
	}
}
