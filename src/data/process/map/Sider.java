package data.process.map;
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
public class Sider {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("data/input/drugCloud/sider.nt"))) ;
		new Sider().writeDrugSideEffect("data/input/cider/sider_dump.nt",bw,false);
	}
	
	public void writeDrugSideEffect(String input, BufferedWriter bw, Boolean writeType) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		
		HashMap<String,String> sideeffects = new HashMap<String,String>();
		HashSet<String> sides = new HashSet<String>();
		HashMap<String,HashSet<String>> siderDrugBank = new HashMap<String,HashSet<String>>();
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
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/sider/resource/drugs/")&o.startsWith("<http://")
							&p.equals("<http://www4.wiwiss.fu-berlin.de/sider/resource/sider/sideEffect>")){
						bw.write(line+"\n");
					}
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/sider/resource/drugs/")&o.equals("<http://www4.wiwiss.fu-berlin.de/sider/resource/sider/side_effects>")
							&p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")){
						if(writeType){
							bw.write(line+"\n");
						}
					}
					
					if(s.startsWith("<http://www4.wiwiss.fu-berlin.de/sider/resource/drugs/")&o.startsWith("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs")
							&p.equals("<http://www.w3.org/2002/07/owl#sameAs>")){
						bw.write(s+" <http://www.w3.org/2004/02/skos/core#closeMatch> "+o+" .\n");
					}
					
				}
			}
		}
		
		bw.flush();
	}
	
	
	

}
