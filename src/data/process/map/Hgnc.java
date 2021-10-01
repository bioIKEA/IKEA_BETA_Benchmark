package data.process.map;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
public class Hgnc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
public HashMap<String,String> getHgncUniprotMapping(String hgncfile) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(hgncfile)));
		String line=null;
		HashMap<String,String> hgncUniprot=new HashMap<>();
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
					
					if(s.startsWith("<http://bio2rdf.org/hgnc:")&o.startsWith("<http://")){
						if(p.equals("<http://bio2rdf.org/hgnc_vocabulary:x-uniprot>")){
							hgncUniprot.put(s, o);
						}
					}
				}
			}
		}
	
		return hgncUniprot;
	}

public HashMap<String,HashSet<String>> getUniprotHgnc(String input) throws IOException{
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
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				
				if(p.equals("<http://bio2rdf.org/hgnc_vocabulary:x-uniprot>")
						&s.startsWith("<http://bio2rdf.org/hgnc:")
						&o.startsWith("<http://bio2rdf.org/uniprot:")
						){
					if(map.containsKey(o)){
						map.get(o).add(s);	
					}else{
						HashSet<String> set=new HashSet<>();
						set.add(s);
						map.put(o, set);
					}
				}
			}
		}
		
	}
	return map;
}
}
