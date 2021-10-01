/**
 * 
 */
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

import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

/**
 * @author ns
 *
 */
public class GOA {
	public static String dataDir="data_sample";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		extract(dataDir+"/input/done/goa_human.nq",
				dataDir+"/output/association_goa.nq");
		
	}
	
	
	public static void extract(String input, String outfile_1) throws IOException{
		
		HashSet<String> proteinHashSet=new HashSet<>();
		HashSet<String> componentashSet=new HashSet<>();
		HashSet<String> functionHashSet=new HashSet<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line=null;
		HashSet<String> ps=new HashSet<>();
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
					if(p.equals("<http://bio2rdf.org/goa_vocabulary:function>")
							&&s.startsWith("<http://bio2rdf.org/uniprot:")
							&&o.startsWith("<http://bio2rdf.org/go:")) {
						ps.add(s+" "+p+" "+o+" .");
						proteinHashSet.add(s);
						componentashSet.add(o);
					}
					if(p.equals("<http://bio2rdf.org/goa_vocabulary:component>")
							&&s.startsWith("<http://bio2rdf.org/uniprot:")
							&&o.startsWith("<http://bio2rdf.org/go:")) {
						ps.add(s+" "+p+" "+o+" .");
						proteinHashSet.add(s);
						functionHashSet.add(o);
					}
					
				}
			}
		}
		br.close();
		
		for(String protein:proteinHashSet ) {
			ps.add(protein + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/goa_vocabulary:genes>" + " .");
		}
		for(String component:componentashSet ) {
			ps.add(component + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/goa_vocabulary:components>" + " .");
		}
		for(String function:functionHashSet ) {
			ps.add(function + " " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>" + " " + "<http://bio2rdf.org/goa_vocabulary:functions>" + " .");
		}
		ps.add("<http://bio2rdf.org/goa_vocabulary:genes> <http://bio2rdf.org/goa_vocabulary:component> <http://bio2rdf.org/goa_vocabulary:components> .");
		ps.add("<http://bio2rdf.org/goa_vocabulary:genes> <http://bio2rdf.org/goa_vocabulary:function> <http://bio2rdf.org/goa_vocabulary:functions> .");

		BufferedWriter bw_1 =new BufferedWriter(new FileWriter(new File(outfile_1)));
		for(String string:ps){
			bw_1.write(string+"\n");
		}
		bw_1.flush();
		bw_1.close();
		
	}
}
