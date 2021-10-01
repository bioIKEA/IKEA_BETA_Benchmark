package data.process.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class UniprotParser {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		new UniprotParser().getHomoProtein("data/input/uniport/uniprot-human-instances.nt", "data/input/drugCloud/uniprot-human-proteins.nt");
	}
	
	
	public void getHomoProtein(String input, String output) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		BufferedWriter bw= new BufferedWriter(new FileWriter(new File(output)));
		String line=null;
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
					
					if(s.startsWith("<http://")&o.equals("<http://purl.uniprot.org/core/Protein>")&p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")){
					bw.write(line+"\n");
					}
					
					if(s.startsWith("<http://")&o.startsWith("<http://")&p.equals("<http://purl.uniprot.org/core/replaces>")){
					bw.write(line+"\n");
					}
				}
			}
			
		}
		bw.flush();
		bw.close();
		
	}

}
