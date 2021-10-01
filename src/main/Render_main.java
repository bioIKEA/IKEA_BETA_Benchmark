package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.nd4j.shade.jackson.dataformat.yaml.snakeyaml.introspector.BeanAccess;
import org.openbel.framework.common.bel.parser.BELScriptParser.set_annotation_return;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import data.process.map.DBpedia;
import data.render.node.diseases.Disease_Diseasome2Omim;
import data.render.node.diseases.Disease_Kegg2Omim;
import data.render.node.diseases.Disease_Offside2Omim;
import data.render.node.diseases.Disease_Pharmgkb2Omim;
import data.render.node.diseases.Disease_Sider2Omim;
import data.render.node.drugs.Drug_Diseasome2Drugbank;
import data.render.node.drugs.Drug_Kegg2Drugbank;
import data.render.node.drugs.Drug_Linkspl2Drugbank;
import data.render.node.drugs.Drug_Offside2Drugbank;
import data.render.node.drugs.Drug_PharmGKB2Drugbank;
import data.render.node.drugs.Drug_Sider2Drugbank;
import data.render.node.features.Generate_feature;
import data.render.node.targets.Target_Diseasome2Drugbank;
import data.render.node.targets.Target_GOA2Drugbank;
import data.render.node.targets.Target_Irefindex2Drugbank;
import data.render.node.targets.Target_Kegg2Drugbank;
import data.render.node.targets.Target_Linkspl2Drugbank;
import data.render.node.targets.Target_Omim2Drugbank;
import data.render.node.targets.Target_Pharmgkb2Drugbank;
import data.render.repository.Irefindex;
import data.render.repository.KEGG;
import data.render.repository.Linkspl;
import data.render.repository.OMIM;
import data.render.repository.PharmGKB;
import data.render.repository.Sider;
import java_cup.internal_error;
import jsat.linear.distancemetrics.PearsonDistance;

public class Render_main {
	
	public static String dataDir="data_sample";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
				
		GOA();
		Diseasome();
		Kegg( );
		PhargGKB( );
		Drugbank( );
		Omim( );
		Sider();
		Offside();
		Linkspl();
		irefindex();

	}
	
	public void setDataDir(String dir) {
		dataDir=dir;
	}
	
	public static void parse_mapping(HashSet<String> nodes, String file, String base, BufferedWriter bw_all,String taget_name, HashMap<String, HashSet<String>> nodespace_mapping) throws IOException {
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(base+"/"+new File(file).getName())));
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashSet<String> nodes_1=new HashSet<>();
		HashSet<String> nodes_2=new HashSet<>();
		HashMap<String, HashSet<String>> nodes_1_mapping=new HashMap<>();
		HashMap<String, HashSet<String>> nodes_2_mapping=new HashMap<>();
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim().toLowerCase();
					String o = quard[2].toString().trim().toLowerCase();
					
					if(nodes.contains(o)) {
						nodes_1.add(s);
						nodes_2.add(o);
						
						if(nodes_1_mapping.containsKey(s)) {
							nodes_1_mapping.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							nodes_1_mapping.put(s, set);
						}
						
						if(nodes_2_mapping.containsKey(o)) {
							nodes_2_mapping.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							nodes_2_mapping.put(o, set);
						}
						
						if(nodespace_mapping.containsKey(o)) {
							nodespace_mapping.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							nodespace_mapping.put(o, set);
						}
					}
			}
		}
		String name=new File(file).getName();
		name=name.substring(0,name.lastIndexOf("."));
		
		bw_all.write(name+": "+nodes_1.size()+"\n");
		bw_all.write(taget_name+": "+nodes_2.size()+"\n");
		for (Entry<String, HashSet<String>> entry:nodes_1_mapping.entrySet()) {
			bw.write(name+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		for (Entry<String, HashSet<String>> entry:nodes_2_mapping.entrySet()) {
			bw.write(taget_name+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		bw_all.flush();
		bw.flush();
		bw.close();
	}
	
	public static void parse_associate(String file, String base, BufferedWriter bw_all) throws IOException {
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(base+"/"+new File(file).getName())));
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<String, HashSet<String>> types=new HashMap<>();
		HashMap<String, HashSet<String>> associations=new HashMap<>();
		HashMap<String, HashSet<String>> nodes=new HashMap<>();
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim().toLowerCase();
					String o = quard[2].toString().trim().toLowerCase();
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")) {
						if(types.containsKey(o)) {
							types.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							types.put(o, set);
						}
					}else {
						if(associations.containsKey(p)) {
							associations.get(p).add(line);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(line);
							associations.put(p, set);
						}
						
						if(nodes.containsKey(o)) {
							nodes.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							nodes.put(o, set);
						}
						
						if(nodes.containsKey(s)) {
							nodes.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							nodes.put(s, set);
						}
					}
			}
		}
		for (Entry<String, HashSet<String>> entry:types.entrySet()) {
			bw_all.write(new File(file).getName()+"\t"+"types"+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		
		for (Entry<String, HashSet<String>> entry:associations.entrySet()) {
			bw_all.write(new File(file).getName()+"\t"+"associations"+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		
		for (Entry<String, HashSet<String>> entry:nodes.entrySet()) {
			bw.write("nodes"+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		bw.flush();
		bw.close();
		bw_all.flush();
	}
	
	
	
	public static void parse_associate(HashSet<String> lines, String base,BufferedWriter bw_all) throws IOException {
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(base+"/"+new File("irefindex.nq").getName())));
		HashMap<String, HashSet<String>> types=new HashMap<>();
		HashMap<String, HashSet<String>> associations=new HashMap<>();
		HashMap<String, HashSet<String>> nodes=new HashMap<>();
		for(String  line:lines) {
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim().toLowerCase();
					String o = quard[2].toString().trim().toLowerCase();
					
					if(p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")) {
						if(types.containsKey(o)) {
							types.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							types.put(o, set);
						}
					}else {
						if(associations.containsKey(p)) {
							associations.get(p).add(line);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(line);
							associations.put(p, set);
						}
						
						if(nodes.containsKey(o)) {
							nodes.get(o).add(s);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(s);
							nodes.put(o, set);
						}
						
						if(nodes.containsKey(s)) {
							nodes.get(s).add(o);
						}else {
							HashSet<String> set=new HashSet<>();
							set.add(o);
							nodes.put(s, set);
						}
					}
			}
		}
		for (Entry<String, HashSet<String>> entry:types.entrySet()) {
			bw_all.write("irefindex.nq"+"\t"+"types"+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		
		for (Entry<String, HashSet<String>> entry:associations.entrySet()) {
			bw_all.write("irefindex.nq"+"\t"+"associations"+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		
		for (Entry<String, HashSet<String>> entry:nodes.entrySet()) {
			bw.write("nodes"+"\t"+entry.getKey()+"\t"+entry.getValue().size()+"\n");
		}
		bw.flush();
		bw.close();
	}
	
	public static void show_All_properties(HashSet<String> files) throws IOException {
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(dataDir+"/.tmp")));
		for(String file:files) {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line=null;
			HashSet<String> ps=new HashSet<>();
			while((line=br.readLine())!=null){
					InputStream inputStream = new ByteArrayInputStream(line.getBytes());
					NxParser nxp = new NxParser();
					nxp.parse(inputStream);
					while (nxp.hasNext()) {
						Node[] quard = nxp.next();
						String s = quard[0].toString().trim().toLowerCase();
						String p = quard[1].toString().trim().toLowerCase();
						String o = quard[2].toString().trim().toLowerCase();
						ps.add(p);
				}
			}
			for(String p:ps) {
				System.out.println(new File(file).getName() +"->"+p);	
				bw.write(new File(file).getName() +"->"+p+"\n");
			}
			bw.flush();
		}
		bw.flush();
		bw.close();
	}
	public static void calculate_feature() throws IOException {
		Generate_feature.pull_chemical("dataDir"+"/output/datasets/orignial/smile.txt");
		Generate_feature.pull_gene(dataDir+"/output/datasets/orignial/sequence.txt");
	}
	public static void split(String file, int number) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		ArrayList<String> lineStrings=new ArrayList<>();
		String line=null;
		while((line=br.readLine())!=null) {
			lineStrings.add(line);
		}
		br.close();
		int number_line=(int) (lineStrings.size()/number);
		
		for (int i = 0; i < number; i++) {
			BufferedWriter bWriter =new BufferedWriter(new FileWriter(new File(file+"_"+i+".nt")));
			for (int j = i*number; j < i*number+number_line && j<lineStrings.size(); j++) {
				bWriter.write(lineStrings.get(j)+"\n");
			}
			bWriter.flush();
			bWriter.close();
		}
		
	}

	public static void GOA() throws IOException {
		data.render.repository.GOA.extract(
				dataDir+"/input/done/goa_human.nq",
				dataDir+"/output/association_goa.nq");
		Target_GOA2Drugbank
				.writeMapping(dataDir+"/output/target_mapping_goa.nq");
	}

	public static void Diseasome() throws IOException {
		data.render.repository.Diseasome.extract(
				dataDir+"/input/done/diseasome_dump.nt",
				dataDir+"/output/association_diseasome.nq");

		Disease_Diseasome2Omim
				.writeMapping(dataDir+"/output/disease_diseasome_omim.nq");

		Drug_Diseasome2Drugbank
				.writeMapping(dataDir+"/output/drug_diseasome_drugbank.nq");

		Target_Diseasome2Drugbank.writeMapping(
				dataDir+"/output/target_diseasome_drugbank.nq");

	}

	public static void Kegg() throws IOException {
		KEGG kegg = new KEGG();
		kegg.extract(dataDir+"/output/association_kegg.nq");
		Disease_Kegg2Omim
				.writeMapping(dataDir+"/output/disease_kegg_omim.nq");
		Drug_Kegg2Drugbank
				.writeMapping(dataDir+"/output/drug_kegg_drugbank.nq");
		Target_Kegg2Drugbank
				.writeMapping(dataDir+"/output/target_kegg_drugbank.nq");
	}

	public static void PhargGKB() throws IOException {
		PharmGKB.extract(dataDir+"/input/done/pharmgkb_relationships.nq",
				dataDir+"/input/done/pharmgkb_drugs.nq",
				dataDir+"/input/done/pharmgkb_diseases.nq",
				dataDir+"/input/done/pharmgkb_genes.nq",
				dataDir+"/output/association_pharmgkb.nq");
		Disease_Pharmgkb2Omim
				.writeMapping(dataDir+"/output/disease_pharmgkb_omim.nq");
		Drug_PharmGKB2Drugbank
				.writeMapping(dataDir+"/output/drug_pharmgkb_drugbank.nq");
		Target_Pharmgkb2Drugbank
				.writeMapping(dataDir+"/output/target_pharmgkb_drugbank.nq");
	}

	public static void Drugbank() throws IOException {
		data.render.repository.Drugbank.extract(
				dataDir+"/input/done/drugbank.nq",
				dataDir+"/output/association_drugbank.nq");
	}

	public static void Omim() throws IOException {

		OMIM.extract(dataDir+"/input/done/omim.nq",
				dataDir+"/output/association_omim.nq");
		Target_Omim2Drugbank
				.writeMapping(dataDir+"/output/target_omim_drugbank.nq");
	}

	public static void Sider() throws IOException {
		Sider.extract(dataDir+"/input/done/sider_dump.nt",
				dataDir+"/output/association_sider.nq");
		Drug_Sider2Drugbank
				.writeMapping(dataDir+"/output/drug_sider_drugbank.nq");
		Disease_Sider2Omim
				.writeMapping(dataDir+"/output/disease_sider_omim.nq");
	}

	public static void Offside() throws IOException {
		data.render.repository.Offside.extract(
				dataDir+"/input/done/offsides.nq",
				dataDir+"/output/association_offside.nq");
		Drug_Offside2Drugbank
				.writeMapping(dataDir+"/output/drug_offside_drugbank.nq");
		Disease_Offside2Omim
				.writeMapping(dataDir+"/output/disease_offside_omim.nq");
	}

	public static void Linkspl() throws IOException {
		Linkspl.extract(dataDir+"/input/done/linkspl.nt",
				dataDir+"/output/association_linkspl.nq");
		Drug_Linkspl2Drugbank
				.writeMapping(dataDir+"/output/drug_linkspl_drugbank.nq");
		Target_Linkspl2Drugbank
				.writeMapping(dataDir+"/output/target_linkspl_drugbank.nq");
	}

	public static void irefindex() throws IOException {

		Irefindex.extract(dataDir+"/input/done/irefindex-all.nq",
				dataDir+"/output/association_irefindex.nq");
		Target_Irefindex2Drugbank.writeMapping(
				dataDir+"/output/target_irefindex_drugbank.nq");
	}

	public static void integration() throws IOException {

		HashSet<String> dataHashSet = new HashSet<>();
		for (File file : new File(dataDir+"/output/datasets").listFiles()) {
			readData(file.getAbsolutePath(), dataHashSet);
		}

		HashSet<String> properties = new HashSet<>();
		HashSet<String> types = new HashSet<>();
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(new File(dataDir+"/output/data_all.nt")));
		for (String string : dataHashSet) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim().toLowerCase();
				String p = quard[1].toString().trim().toLowerCase();
				String o = quard[2].toString().trim().toLowerCase();
				properties.add(p);
				if(!p.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")) {
					bw.write(string + "\n");
				}else {
					types.add(o);
				}
			}
		}
		bw.flush();
		bw.close();
		
		for(String string:types) {
		System.out.println(string);
	}
		
	}

	
	public static void checkData(String file) throws IOException {
		System.out.println(file);
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		ArrayList<String> printsHashMap=new ArrayList<>();
		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			try {
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim().toLowerCase();
					String o = quard[2].toString().trim().toLowerCase();
					printsHashMap.add(line);
				}
			} catch (Exception e) {
				System.out.println(line);
				System.exit(0);
			}

		}
		br.close();
		System.out.println(printsHashMap.get(57433));
		System.out.println(printsHashMap.get(57434));
		System.out.println(printsHashMap.get(57435));
		
	}
	
	
	public static void readData(String file, HashSet<String> data) throws IOException {
		System.out.println(file);
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			try {
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim().toLowerCase();
					String o = quard[2].toString().trim().toLowerCase();
					data.add(s + " " + p + " " + o + " .");
				}
			} catch (Exception e) {
				System.out.println(line);
				System.exit(0);
			}

		}
		br.close();
	}
	
	
}
